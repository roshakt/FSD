// package com.projfinal.usersmanagementsystem.config;


// import jakarta.servlet.FilterChain;
// import jakarta.servlet.ServletException;
// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.core.context.SecurityContext;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
// import org.springframework.stereotype.Component;
// import org.springframework.web.filter.OncePerRequestFilter;

// import com.projfinal.usersmanagementsystem.service.JWTUtils;
// import com.projfinal.usersmanagementsystem.service.OurUserDetailsService;

// import java.io.IOException;

// @Component
// public class JWTAuthFilter extends OncePerRequestFilter {

//     @Autowired
//     private JWTUtils jwtUtils;

//     @Autowired
//     private OurUserDetailsService ourUserDetailsService;


//     @Override
//     protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

//         final String authHeader = request.getHeader("Authorization");
//         final String jwtToken;
//         final String userEmail;

//         if (authHeader == null || authHeader.isBlank()) {
//             filterChain.doFilter(request, response);
//             return;
//         }

//         jwtToken = authHeader.substring(7);
//         userEmail = jwtUtils.extractUsername(jwtToken);

//         if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//             UserDetails userDetails = ourUserDetailsService.loadUserByUsername(userEmail);

//             if (jwtUtils.isTokenValid(jwtToken, userDetails)) {
//                 SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
//                 UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
//                         userDetails, null, userDetails.getAuthorities()
//                 );
//                 token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                 securityContext.setAuthentication(token);
//                 SecurityContextHolder.setContext(securityContext);
//             }
//         }
//         filterChain.doFilter(request, response);
//     }
// }
package com.projfinal.usersmanagementsystem.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.projfinal.usersmanagementsystem.service.JWTUtils;
import com.projfinal.usersmanagementsystem.service.OurUserDetailsService;

import java.io.IOException;

@Component
public class JWTAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private OurUserDetailsService ourUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwtToken;
        final String userEmail;

        if (authHeader == null || authHeader.isBlank() || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwtToken = authHeader.substring(7);

        // Log the extracted token for debugging purposes
        System.out.println("Extracted JWT Token: " + jwtToken);

        // Validate token structure
        if (jwtToken.isEmpty() || jwtToken.split("\\.").length != 3) {
            System.out.println("Invalid JWT Token structure");
            filterChain.doFilter(request, response);
            return;
        }

        try {
            userEmail = jwtUtils.extractUsername(jwtToken);
        } catch (Exception e) {
            System.out.println("JWT token parsing failed: " + e.getMessage());
            filterChain.doFilter(request, response);
            return;
        }

        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = ourUserDetailsService.loadUserByUsername(userEmail);

            if (jwtUtils.isTokenValid(jwtToken, userDetails)) {
                SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
                UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                securityContext.setAuthentication(token);
                SecurityContextHolder.setContext(securityContext);
            }
        }
        filterChain.doFilter(request, response);
    }
}
