package com.example.quizly.security;

import java.io.IOException;
import jakarta.servlet.Filter;

import com.example.quizly.controller.TeacherSession;

import jakarta.inject.Inject;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@WebFilter("/teacher/*")
public class JSFSecurityFilter implements Filter {

    @Inject
    private TeacherSession TeacherSession;


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        // Check if the user is logged in using our Session Bean
        if (TeacherSession != null && TeacherSession.isLoggedIn()) {
            // They are logged in! Let them pass through to the page.
            chain.doFilter(request, response);
        } else {
            // INTRUDER! Kick them back to the login page.
            res.sendRedirect(req.getContextPath() + "/login.xhtml");
        }
    }
}
