package com.certimeter.myapp.filter;

import com.certimeter.myapp.enumeration.ResponseEnum;
import com.certimeter.myapp.exception.FailureException;
import com.certimeter.myapp.dto.Response;
import com.certimeter.myapp.requestcontext.RequestContext;
import com.certimeter.myapp.resourcemodel.User;
import com.certimeter.myapp.service.JWTService;
import com.certimeter.myapp.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(1)
public class JWTAuthenticationFilter extends OncePerRequestFilter {
    private static Logger LOG = LoggerFactory.getLogger(JWTAuthenticationFilter.class);
    private static final String LOGIN_PATH = "/auth/login";
    private static final String PASSWORD_RESET_PATH = "/auth/password/recover";

    private final JWTService jwtService;
    private final UserService userService;
    private final RequestContext requestContext;

    public JWTAuthenticationFilter(JWTService jwtService, UserService userService, RequestContext requestContext) {
        this.jwtService = jwtService;
        this.userService = userService;
        this.requestContext = requestContext;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();

        return path.equals(LOGIN_PATH)
                || path.equals(PASSWORD_RESET_PATH)
                || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")
                || path.equals("/swagger-ui.html");
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // If no valid Authorization header, block the request
            returnCustomResponse(response, HttpStatus.UNAUTHORIZED, ResponseEnum.INVALID_ACCESS_TOKEN);
            return;
        }

        String accessToken = authHeader.substring(7); // Extract the token

        try {
            jwtService.validateAccessToken(accessToken); // Validate token
            Long idUser = jwtService.getClaimFromAccessToken(accessToken, "id_user", Long.class);
            User user = userService.getUserById(idUser);

            // Populate the RequestContext
            requestContext.setUser(user);
            requestContext.setRole(user.getRole());
            requestContext.setAccessToken(accessToken);
        } catch (FailureException e) {
            if (isTokenRefreshRequest(e, request)) {
                filterChain.doFilter(request, response);
            } else {
                returnCustomResponse(response, e.getResponseEnum().getHttpStatus(), e.getResponseEnum());
            }
            return;
        }

        // If token is valid, proceed to the next filter
        filterChain.doFilter(request, response);
    }

    private boolean isTokenRefreshRequest(FailureException e, HttpServletRequest request) {
        return e.getResponseEnum().equals(ResponseEnum.EXPIRED_ACCESS_TOKEN)
                && "/auth/login/refresh".equals(request.getRequestURI());
    }

    private void returnCustomResponse(ServletResponse response, HttpStatus httpStatus, ResponseEnum responseEnum) throws IOException {
        Response customObjectResponse = new Response(responseEnum); //Response mia
        //This line converts the Response object to a byte array, which can be written to the HTTP response.
        byte[] responseToSend = restResponseBytes(customObjectResponse, MediaType.APPLICATION_JSON);
        //These lines set the content type of the response to JSON and set the HTTP status code.
        ((HttpServletResponse) response).setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        ((HttpServletResponse) response).setStatus(httpStatus.value());
        //This line writes the byte array to the response output stream.
        response.getOutputStream().write(responseToSend);
    }

    //This method serializes the Response object into a byte array
    private byte[] restResponseBytes(Response response, MediaType mediaType) throws IOException {
        //This line selects either a JSON or XML mapper based on the media type.
        ObjectMapper mapper = mediaType == MediaType.APPLICATION_JSON ? new ObjectMapper() : new XmlMapper();
        //This line converts the Response object to a JSON or XML string.
        String serialized = mapper.writeValueAsString(response);
        //Convert the string to bytes:
        return serialized.getBytes();
    }
}
