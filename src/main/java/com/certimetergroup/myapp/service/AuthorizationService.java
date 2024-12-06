package com.certimetergroup.myapp.service;

import com.certimetergroup.myapp.enumeration.UserRoleEnum;
import com.certimetergroup.myapp.requestcontext.RequestContext;
import org.springframework.stereotype.Service;

import java.util.EnumSet;

@Service
public class AuthorizationService {
    public boolean isAuthorized(RequestContext request, EnumSet<UserRoleEnum> authorizedRoles) {
        return authorizedRoles.contains(request.getRole());
    }
}

