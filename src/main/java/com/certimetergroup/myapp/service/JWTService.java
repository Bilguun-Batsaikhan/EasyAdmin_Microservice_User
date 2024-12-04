package com.certimetergroup.myapp.service;

import com.certimetergroup.myapp.enumeration.ResponseEnum;
import com.certimetergroup.myapp.exception.FailureException;
import com.certimetergroup.myapp.resourcemodel.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.MacAlgorithm;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.Date;

@Service
public class JWTService {
    private static final MacAlgorithm signatureAlgorithm = Jwts.SIG.HS512;

    // HINT:    https://www.baeldung.com/spring-classpath-file-access#2-using-value
    @Value("classpath:jwt/access_token.key")
    private Resource accessTokenKeyFile;
    @Value("classpath:jwt/refresh_token.key")
    private Resource refreshTokenKeyFile;
    @Value("${authentication.jwt.expiration.time}")
    private long accessTokenExpirationTimeMillisecs;


    //--------------------
    private SecretKey accessTokenKey;
    private SecretKey refreshTokenKey;
    //--------------------

    // init(): Initializes the secret keys by reading the content of the resource files. This method is annotated with @PostConstruct, meaning it runs after the bean’s properties have been set.
    @PostConstruct
    public void init() throws IOException {
        this.refreshTokenKey = Keys.hmacShaKeyFor(refreshTokenKeyFile.getContentAsByteArray());
        this.accessTokenKey = Keys.hmacShaKeyFor(accessTokenKeyFile.getContentAsByteArray());
    }

    // Generates a refresh token for the given user, including claims like user ID and username.
    public String generateRefreshToken(User user, String uuid) {
        //UUID.randomUUID().toString() nel controller
        return Jwts
                .builder()
                .signWith(refreshTokenKey, signatureAlgorithm)
                .issuedAt(new Date(System.currentTimeMillis()))
                .claim("id_user", user.getId())
                .claim("username", user.getUsername())
                .claim("uuid", uuid)
                .claim("role", user.getRole().name())
                .compact();
    }

    // Generates an access token for the given user, including claims like user ID, username, and role. The token has an expiration time.
    public String generateAccessToken(User user, String uuid) {
        Date now = new Date(System.currentTimeMillis());
        Date expiration = new Date(accessTokenExpirationTimeMillisecs + System.currentTimeMillis());

        return Jwts
                .builder()
                .signWith(accessTokenKey, signatureAlgorithm)
                .issuedAt(now) // This becomes "iat" field in the claim
                .expiration(expiration)
                .claim("id_user", user.getId())
                .claim("username", user.getUsername())
                .claim("uuid", uuid)
                .claim("role", user.getRole().name())
                .compact();
    }

    // Validates the given access token. If the token is expired, invalid, or malformed, it throws a FailureException with the appropriate response enum.
    public void validateAccessToken(String accessToken) {
        try {

            this.validateToken(accessTokenKey, accessToken);

        } catch (ExpiredJwtException e) {
            throw new FailureException(ResponseEnum.EXPIRED_ACCESS_TOKEN);
        } catch (SignatureException e) {
            throw new FailureException(ResponseEnum.INVALID_ACCESS_TOKEN);
        } catch (MalformedJwtException e) {
            throw new FailureException(ResponseEnum.MALFORMED_ACCESS_TOKEN);
        } catch (Exception e) {
            throw new FailureException(ResponseEnum.UNEXPECTED_ERROR);
        }
    }

    public void validateRefreshToken(String refreshToken) {
        try {
            this.validateToken(refreshTokenKey, refreshToken);

        } catch (SignatureException e) {
            throw new FailureException(ResponseEnum.INVALID_REFRESH_TOKEN);
        } catch (MalformedJwtException e) {
            throw new FailureException(ResponseEnum.MALFORMED_REFRESH_TOKEN);
        } catch (Exception e) {
            throw new FailureException(ResponseEnum.UNEXPECTED_ERROR);
        }
    }

    // Retrieves a specific claim from the access token after validating it.
    public <T> T getClaimFromAccessToken(String accessToken, String fieldName, Class<T> fieldClass) {
        this.validateAccessToken(accessToken);
        return this.getClaimFromToken(accessTokenKey, accessToken, fieldName, fieldClass);
    }

    public <T> T getClaimFromRefreshToken(String refreshToken, String fieldName, Class<T> fieldClass) {
        this.validateRefreshToken(refreshToken);
        return this.getClaimFromToken(refreshTokenKey, refreshToken, fieldName, fieldClass);
    }

    public boolean isAccessTokenExpired(String accessToken) {
        try {
            validateAccessToken(accessToken);
            return false;       // token valid
        } catch (FailureException e) {
            if (e.getResponseEnum().equals(ResponseEnum.EXPIRED_ACCESS_TOKEN))
                return true;    // token expired
            throw e;            // token malformed --> ERROR
        }
    }

    /**************************************************************************************/

    private void validateToken(SecretKey secretKey, String token) {
        Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
    }

    private <T> T getClaimFromToken(SecretKey secretKey, String token, String fieldName, Class<T> fieldClass) {
        Jws<Claims> claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token);
//        Jws<Claims> claims: This variable holds the parsed JWT claims.
//        Jwts.parser(): Creates a new JWT parser.
//        verifyWith(secretKey): Configures the parser to verify the token’s signature using the provided secret key.
//        build(): Builds the parser with the specified configurations.
//        parseSignedClaims(token): Parses the JWT token and returns the claims.
        return claims.getPayload().get(fieldName, fieldClass);
    }

    public <T> T getClaimFromExpiredAccessToken(String accessToken, String fieldName, Class<T> fieldClass) {
        try {
            this.validateToken(accessTokenKey, accessToken);
        } catch (ExpiredJwtException e) {
            Claims claims = e.getClaims();
            return claims.get(fieldName, fieldClass);
        } catch (SignatureException e) {
            throw new FailureException(ResponseEnum.INVALID_ACCESS_TOKEN);
        } catch (MalformedJwtException e) {
            throw new FailureException(ResponseEnum.MALFORMED_ACCESS_TOKEN);
        } catch (Exception e) {
            throw new FailureException(ResponseEnum.UNEXPECTED_ERROR);
        }
        return null; // or throw an exception if the token is not expired
    }
}
