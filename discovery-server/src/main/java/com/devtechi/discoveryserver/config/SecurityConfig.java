package com.devtechi.discoveryserver.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;

@Configuration
@EnableWebSecurity
public class SecurityConfig   {
   /* New Way (Spring Security 6 / Boot 3.x)
    You now define a SecurityFilterChain bean instead of extending WebSecurityConfigurerAdapter.
    */
}
