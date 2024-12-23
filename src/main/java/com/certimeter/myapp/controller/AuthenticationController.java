package com.certimeter.myapp.controller;

import com.certimeter.myapp.dto.*;
import com.certimeter.myapp.enumeration.ResponseEnum;
import com.certimeter.myapp.enumeration.UserRoleEnum;
import com.certimeter.myapp.exception.FailureException;
import com.certimeter.myapp.resourcemodel.User;
import com.certimeter.myapp.service.JWTService;
import com.certimeter.myapp.service.UserMapper;
import com.certimeter.myapp.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    private final UserService userService;
    private final JWTService jwtService;

    public AuthenticationController(UserService userService, JWTService jwtService, UserMapper userMapper) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginReq(@RequestBody LoginRequest loginRequest) {
        User user = userService.getUserByLoginInfo(loginRequest);
        if (user == null) throw new FailureException(ResponseEnum.AUTHENTICATION_FAILED);

        String uuid = UUID.randomUUID().toString();

        String accessToken = jwtService.generateAccessToken(user, uuid);
        String refreshToken = jwtService.generateRefreshToken(user, uuid);

        return ResponseEntity.ok(new LoginResponse(accessToken, refreshToken));
    }


    //RequestParam in POST takes values form body
    @PostMapping("login/refresh")
    public ResponseEntity<RefreshResponse> refresh(@RequestHeader("Authorization") String accessTokenb, @RequestBody RefreshRequest refreshRequest) {
        String refreshToken = refreshRequest.getRefreshToken();
        String accessToken = accessTokenb.substring(7);

        // The access token is not expired, and it's still valid
        if (!jwtService.isAccessTokenExpired(accessToken)) {
            return ResponseEntity.ok(new RefreshResponse(accessToken, refreshToken));
        }

        // Validate the refresh token
        jwtService.validateRefreshToken(refreshToken);

        // Ensure the UUIDs match
        String uuidAccessToken = jwtService.getClaimFromExpiredAccessToken(accessToken, "uuid", String.class);
        String uuidRefreshToken = jwtService.getClaimFromRefreshToken(refreshToken, "uuid", String.class);
        if (!uuidAccessToken.equals(uuidRefreshToken)) {
            throw new FailureException(ResponseEnum.FORBIDDEN);
        }

        // Generate a new access token
        User user = new User();
        Long idUser = jwtService.getClaimFromRefreshToken(refreshToken, "id_user", Long.class);
        String username = jwtService.getClaimFromRefreshToken(refreshToken, "username", String.class);
        String userRoleEnum = jwtService.getClaimFromRefreshToken(refreshToken, "role", String.class);

        user.setId(idUser);
        user.setUsername(username);
        user.setRole(UserRoleEnum.valueOf(userRoleEnum));

        String newAccessToken = jwtService.generateAccessToken(user, uuidRefreshToken);

        return ResponseEntity.ok(new RefreshResponse(newAccessToken, refreshToken));
    }

}
