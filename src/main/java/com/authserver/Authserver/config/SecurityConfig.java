package com.authserver.Authserver.config;

import com.authserver.Authserver.repository.OAuthUserRepository;
import com.authserver.Authserver.repository.TenantRepository;
import com.authserver.Authserver.repository.UserRoleRepository;
import com.authserver.Authserver.service.CustomOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.beans.factory.annotation.Autowired;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private OAuth2UserService<OAuth2UserRequest, OAuth2User> customOAuth2UserService;

    @SuppressWarnings("removal")
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(withDefaults())
           .authorizeHttpRequests(auth -> auth
               .requestMatchers("/login**").permitAll()
               .anyRequest().authenticated()
           )
           .oauth2Login(oauth -> oauth
               .loginPage("http://localhost:5173/login")
               .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
               .defaultSuccessUrl("http://localhost:5173/", true)
           )
           .exceptionHandling(eh -> eh
                .authenticationEntryPoint(new RestAuthenticationEntryPoint()) 
            )
           .csrf().disable()
           .formLogin(withDefaults())
           .sessionManagement(session -> session
               .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
           )
           .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("http://localhost:5173/login")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
            );
        return http.build();
    }

    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService(OAuthUserRepository userRepo, UserRoleRepository roleRepo, TenantRepository tenantRepository) {
        return new CustomOAuth2UserService(userRepo, roleRepo, tenantRepository);
    }
}
