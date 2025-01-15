package com.certimeter.myapp.controller;

import com.certimeter.myapp.dto.UserDTO;
import com.certimeter.myapp.dto.UserResPagination;
import com.certimeter.myapp.enumeration.ResponseEnum;
import com.certimeter.myapp.enumeration.UserRoleEnum;
import com.certimeter.myapp.exception.FailureException;
import com.certimeter.myapp.requestcontext.RequestContext;
import com.certimeter.myapp.resourcemodel.User;
import com.certimeter.myapp.service.AuthorizationService;
import com.certimeter.myapp.service.UserMapper;
import com.certimeter.myapp.service.UserService;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/")
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;
    private final AuthorizationService authorizationService;
    private final RequestContext requestContext;

    public UserController(UserService userService, UserMapper userMapper, AuthorizationService authorizationService, RequestContext requestContext) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.authorizationService = authorizationService;
        this.requestContext = requestContext;
    }

    @GetMapping("/users")
    public ResponseEntity<UserResPagination> getUsers(
            @RequestParam Optional<String> username,
            @RequestParam Optional<String> usernameMatchMode,
            @RequestParam Optional<String> firstname,
            @RequestParam Optional<String> firstnameMatchMode,
            @RequestParam Optional<String> surname,
            @RequestParam Optional<String> surnameMatchMode,
            @RequestParam Optional<String> phoneNumber,
            @RequestParam Optional<String> phoneNumberMatchMode,
            @RequestParam Optional<String> email,
            @RequestParam Optional<String> emailMatchMode,
            @RequestParam Optional<String> role,
            @RequestParam Optional<String> roleMatchMode,
            @RequestParam Optional<String> birthdate,
            @RequestParam Optional<String> birthdateMatchMode,
            @RequestParam(value = "page", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize) {
        EnumSet<UserRoleEnum> authorizedRoles = EnumSet.of(UserRoleEnum.SUPER_ADMIN, UserRoleEnum.SYSTEM_ADMIN);
        if (!authorizationService.isAuthorized(requestContext, authorizedRoles)) {
            throw new FailureException(ResponseEnum.FORBIDDEN);
        }
        return new ResponseEntity<>(userService.getUsers(username, usernameMatchMode, firstname, firstnameMatchMode, surname, surnameMatchMode, phoneNumber, phoneNumberMatchMode, email, emailMatchMode, role, roleMatchMode, birthdate, birthdateMatchMode, pageNo, pageSize), HttpStatus.OK);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserDTO> getUserById(@Valid @PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user == null) throw new FailureException(ResponseEnum.RESOURCE_NOT_FOUND);
        return ResponseEntity.ok(userMapper.userToUserDto(user));
    }

    //TODO probably I don't need this endpoint, delete it if it's not used
    @PostMapping("/users/usernames")
    public Map<Long, String> getUsernamesGivenIds(@RequestBody List<Long> ids) {
        EnumSet<UserRoleEnum> authorizedRoles = EnumSet.of(UserRoleEnum.SUPER_ADMIN);
        if (!authorizationService.isAuthorized(requestContext, authorizedRoles)) {
            throw new FailureException(ResponseEnum.FORBIDDEN);
        }
        return userService.getUsernamesGivenIds(ids);
    }

    @PostMapping("/users")
    public ResponseEntity<String> addUser(@Valid @RequestBody User user) {
        EnumSet<UserRoleEnum> authorizedRoles = EnumSet.of(UserRoleEnum.SUPER_ADMIN);
        if (!authorizationService.isAuthorized(requestContext, authorizedRoles)) {
            throw new FailureException(ResponseEnum.FORBIDDEN);
        }
        boolean userAdded = userService.addUser(user);
        if (!userAdded) throw new FailureException(ResponseEnum.USERNAME_ALREADY_EXISTS);
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseEnum.CREATED.getDescription());
    }

    @PostMapping("/users/batch")
    public ResponseEntity<String> addUsers(@Valid @RequestBody List<User> users) {
        EnumSet<UserRoleEnum> authorizedRoles = EnumSet.of(UserRoleEnum.SUPER_ADMIN);
        if (!authorizationService.isAuthorized(requestContext, authorizedRoles)) {
            throw new FailureException(ResponseEnum.FORBIDDEN);
        }
        boolean usersAdded = userService.addUsers(users);
        if (!usersAdded) throw new FailureException(ResponseEnum.USERNAME_ALREADY_EXISTS);
        return ResponseEntity.status(HttpStatus.CREATED).body("Users added successfully.");
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<String> replaceUser(@Valid @RequestBody User user, @PathVariable Long id) {
        EnumSet<UserRoleEnum> authorizedRoles = EnumSet.of(UserRoleEnum.SUPER_ADMIN);
        if (!authorizationService.isAuthorized(requestContext, authorizedRoles)) {
            throw new FailureException(ResponseEnum.FORBIDDEN);
        }

        boolean userReplaced = userService.replaceUser(user, id);
        if (!userReplaced) throw new FailureException(ResponseEnum.RESOURCE_NOT_FOUND);
        return ResponseEntity.ok("User replaced successfully.");
    }

    @PatchMapping("/users/{id}")
    public ResponseEntity<String> updateUser(@Valid @PathVariable Long id, @RequestBody Map<String, Object> updates) {
        EnumSet<UserRoleEnum> authorizedRoles = EnumSet.of(UserRoleEnum.SUPER_ADMIN);
        if (!authorizationService.isAuthorized(requestContext, authorizedRoles)) {
            throw new FailureException(ResponseEnum.FORBIDDEN);
        }
        boolean userUpdated = userService.updateUser(id, updates);
        if (!userUpdated) throw new FailureException(ResponseEnum.RESOURCE_NOT_FOUND);
        return ResponseEntity.ok("User updated successfully.");
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> removeUser(@Valid @PathVariable Long id) {
        EnumSet<UserRoleEnum> authorizedRoles = EnumSet.of(UserRoleEnum.SUPER_ADMIN);
        if (!authorizationService.isAuthorized(requestContext, authorizedRoles)) {
            throw new FailureException(ResponseEnum.FORBIDDEN);
        }

        boolean userRemoved = userService.removeUser(id);
        if (!userRemoved) throw new FailureException(ResponseEnum.RESOURCE_NOT_FOUND);
        return ResponseEntity.ok("User removed successfully.");
    }
}