package com.example.quizly.security;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.security.enterprise.AuthenticationStatus;
import jakarta.security.enterprise.authentication.mechanism.http.HttpAuthenticationMechanism;
import jakarta.security.enterprise.authentication.mechanism.http.HttpMessageContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.HashSet;
import java.util.Set;

@ApplicationScoped
public class FirebaseAuthenticationMechanism implements HttpAuthenticationMechanism {

    @Override
    public AuthenticationStatus validateRequest(HttpServletRequest request, HttpServletResponse response, HttpMessageContext httpMessageContext) {
        
        // 1. Grab the token from the HTTP Headers
        String header = request.getHeader("Authorization");

        // 2. If there is no token, let them proceed as an Anonymous user.
        // This allows your @PermitAll endpoints (like Students joining a quiz) to still work!
        if (header == null || !header.startsWith("Bearer ")) {
            return httpMessageContext.doNothing();
        }

        // 3. Extract just the token string
        String token = header.substring("Bearer ".length());

        try {
            // 4. Ask Firebase to verify the signature and expiration date
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);

            // 5. Get the user's email
            String email = decodedToken.getEmail();

            // 6. Assign Roles (You could look these up in your ProfDAO if you want!)
            Set<String> roles = new HashSet<>();
            roles.add("PROFESSOR"); 

            // 7. Tell WildFly: "This is a valid user, let them in!"
            return httpMessageContext.notifyContainerAboutLogin(email, roles);

        } catch (Exception e) {
            // The token is expired, fake, or invalid. Reject the request!
            return httpMessageContext.responseUnauthorized();
        }
    }
}