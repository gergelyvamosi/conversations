package org.nextweb.converstions.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
public class AuthController {

  @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, Authentication authentication) {
        // Invalidate the session.  This will remove the JSESSIONID.
        HttpSession session = request.getSession(false); // Don't create a new session if one doesn't exist.
        if (session != null) {
            session.invalidate();
        }

        // Clear the Spring Security context.
        SecurityContextHolder.clearContext();

        // Optionally, you can also send a response to clear the JSESSIONID cookie explicitly (though session.invalidate() should usually handle this).
        // However, this is more complex and not always necessary, as the container should handle this.
        // If you encounter issues with the cookie not being cleared, you might need to add code here to manipulate the HttpServletResponse.
        // But for most cases, the below is sufficient.

        return ResponseEntity.ok("Logged out successfully.");
    }
    
}
