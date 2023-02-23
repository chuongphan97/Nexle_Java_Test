package com.nexle.java_test.config.security;


import com.nexle.java_test.config.jwt.JwtAuthEntryPoint;
import com.nexle.java_test.config.jwt.JwtAuthTokenFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Order(SecurityProperties.BASIC_AUTH_ORDER)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final BaseUserDetailsService userDetailService;
    private final JwtAuthEntryPoint unauthorizedHandler;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public JwtAuthTokenFilter authenticationJwtTokenFilter() {
        return new JwtAuthTokenFilter();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    public static final String[] AUTH_WHITELIST = {
            // -- swagger ui
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/webjars/**",
            "/**/swagger-ui.html",
            "/api/v2/docs"
    };

    public static final String[] AUTH_FOR_GET_METHOD = {
            "/**/swagger-ui.html"
    };

    public static final String[] AUTH_FOR_POST_METHOD = {
            "/**/sign-up",
            "/**/sign-in",
            "/**/refresh-token"
    };

    public static final String[] AUTH_FOR_PUT_METHOD = {
    };


    public static final String[] AUTH_FOR_LOGIN_METHOD = {
            "/**/admin/auth/**"
    };

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder(11);
    }


    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(this.userDetailService).passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable().authorizeRequests().antMatchers(AUTH_WHITELIST).permitAll()
                .antMatchers(HttpMethod.GET, AUTH_FOR_GET_METHOD).permitAll()
                .antMatchers(HttpMethod.POST, AUTH_FOR_POST_METHOD).permitAll()
                .antMatchers(HttpMethod.PUT, AUTH_FOR_PUT_METHOD).permitAll()
                .antMatchers(HttpMethod.POST, AUTH_FOR_LOGIN_METHOD).permitAll()
                .anyRequest().authenticated().and().exceptionHandling().authenticationEntryPoint(this.unauthorizedHandler)
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
    }
}
