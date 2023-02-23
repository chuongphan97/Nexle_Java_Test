package com.nexle.java_test.config.jwt;

import com.nexle.java_test.exception.AuthenticationException;
import com.nexle.java_test.utils.constants.Constants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

public class JwtAuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtProvider tokenProvider;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        UserDetails userDetails = new UserPrincipal();
        try {
            String jwt = getJwtToken(request);
            if (jwt != null && this.tokenProvider.validateJwtToken(jwt)) {
                String userId = String.valueOf(this.tokenProvider.getClaimInfo(jwt, "userId"));
                userDetails = this.userDetailsService.loadUserByUsername(userId);

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            if (StringUtils.isNotEmpty(e.getMessage()) && e.getMessage().contains(Constants.JWT_ERROR_REGEX)) {
                this.buildExceptionOutput(request, response, e.getMessage(), e.getMessage(),
                        HttpStatus.UNAUTHORIZED.value(), HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
            this.buildExceptionOutput(request, response, AuthenticationException.ERROR_JWT_INVALID_TOKEN, AuthenticationException.ERROR_JWT_INVALID_TOKEN,
                    HttpStatus.UNAUTHORIZED.value(), HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        response.setBufferSize(64 * 1024);

        filterChain.doFilter(request, response);
    }

    private String getJwtToken(HttpServletRequest request) {
        String authHeader = request.getHeader(Constants.AUTHORIZATION_HEADER);
        if (authHeader != null && authHeader.startsWith(Constants.JWT_TOKEN_TYPE)) {
            return authHeader.replace(Constants.JWT_TOKEN_TYPE, Constants.EMPTY);
        }
        return null;
    }

    /**
     * Build json exception output
     *
     * @param request        {@link HttpServletRequest}
     * @param response       {@link HttpServletResponse}
     * @param errorMsg       errorMsg
     * @param msg            msg
     * @param status         status
     * @param responseStatus responseStatus
     * @throws IOException
     */
    private void buildExceptionOutput(HttpServletRequest request, HttpServletResponse response,
                                      String errorMsg, String msg, Integer status, Integer responseStatus) throws IOException {
        response.setContentType("application/json");
        response.setStatus(responseStatus);
        String sb = "{ " +
                "\"path\": \"" +
                request.getRequestURL() +
                "\"" +
                "," +
                "\"error\":" +
                "\"" +
                errorMsg +
                "\"" +
                "," +
                "\"message\":" +
                "\"" +
                msg +
                "\"" +
                "," +
                "\"timestamp\":" +
                new Date().getTime() +
                "," +
                "\"status\":" +
                status +
                "} ";
        response.getWriter().write(sb);
    }
}