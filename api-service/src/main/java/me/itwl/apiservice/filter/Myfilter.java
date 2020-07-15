package me.itwl.apiservice.filter;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
@Component
public class Myfilter extends OncePerRequestFilter {


    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        System.out.println("llllllllllllllllllllllllllllll");
        String user_name = httpServletRequest.getHeader("user_name");
        String authorities = httpServletRequest.getHeader("authorities");
        String client_id = httpServletRequest.getHeader("client_id");
        String scope = httpServletRequest.getHeader("scopes");

        System.out.println(user_name);
        System.out.println(authorities);
        System.out.println(client_id);
        System.out.println(scope);
        filterChain.doFilter(httpServletRequest,httpServletResponse);
    }
}
