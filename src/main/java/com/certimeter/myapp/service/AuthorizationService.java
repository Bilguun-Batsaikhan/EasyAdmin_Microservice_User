package com.certimeter.myapp.service;

import com.certimeter.myapp.enumeration.UserRoleEnum;
import com.certimeter.myapp.requestcontext.RequestContext;
import org.springframework.stereotype.Service;

import java.util.EnumSet;

@Service
public class AuthorizationService {
    public boolean isAuthorized(RequestContext request, EnumSet<UserRoleEnum> authorizedRoles) {
        return authorizedRoles.contains(request.getRole());
    }
}

