package com.certimetergroup.myapp.service;

import com.certimetergroup.myapp.enumeration.UserRoleEnum;
import com.certimetergroup.myapp.requestcontext.RequestContext;
import com.certimetergroup.myapp.resourcemodel.User;
import org.springframework.stereotype.Service;

import java.util.EnumSet;

@Service
public class AuthorizationService {
    public boolean isAuthorized(RequestContext request, EnumSet<UserRoleEnum> authorizedRoles) {
        User user = request.getUser();

        return authorizedRoles.contains(user.getRole());
    }
}

