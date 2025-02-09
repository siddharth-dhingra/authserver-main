package com.authserver.Authserver.service;

import com.authserver.Authserver.model.OAuthUser;
import com.authserver.Authserver.model.UserRole;
import com.authserver.Authserver.model.RoleEnum;
import com.authserver.Authserver.repository.OAuthUserRepository;
import com.authserver.Authserver.repository.UserRoleRepository;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Component;

import java.util.HashSet;

@Component
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final OAuthUserRepository userRepo;
    private final UserRoleRepository roleRepo;

    public CustomOAuth2UserService(OAuthUserRepository userRepo, UserRoleRepository roleRepo) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String googleId = oAuth2User.getAttribute("sub");
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String picture = oAuth2User.getAttribute("picture");

        OAuthUser user = userRepo.findById(googleId).orElse(new OAuthUser());
        user.setGoogleId(googleId);
        user.setEmail(email);
        user.setName(name);
        user.setPictureUrl(picture);
        userRepo.save(user);

        UserRole userRole = roleRepo.findByGoogleId(googleId).orElse(null);
        if (userRole == null) {
            userRole = new UserRole();
            userRole.setGoogleId(googleId);
            userRole.setRole(RoleEnum.USER); 
            roleRepo.save(userRole);
        }

        return new DefaultOAuth2User(
                new HashSet<>(),
                oAuth2User.getAttributes(),
                "sub" 
        );
    }
}