package com.certimeter.myapp.requestcontext;

import com.certimeter.myapp.enumeration.UserRoleEnum;
import com.certimeter.myapp.resourcemodel.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

@Getter
@Setter
@ToString
@Component("requestScopedBean")
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class RequestContext {
    User user;
    String accessToken;
    UserRoleEnum role;
}
