package org.nextweb.converstions.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserHelper {

    public static final String ROOT_USER = "root";
    public static final String ANONYMOUS_USER = "anonymousUser000001876876";

    public static String getAuthenticatedUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return ANONYMOUS_USER;
    }

}
