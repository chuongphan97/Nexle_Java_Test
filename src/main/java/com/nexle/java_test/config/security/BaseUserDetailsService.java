package com.nexle.java_test.config.security;

import com.nexle.java_test.common.entity.User;
import com.nexle.java_test.common.repository.UserRepository;
import com.nexle.java_test.config.jwt.UserPrincipal;
import com.nexle.java_test.utils.constants.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
public class BaseUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        User user = userRepository.findById(Integer.valueOf(userId))
                .orElseThrow(()
                        -> new UsernameNotFoundException("UserId " + userId + " not found"));

        UserDetails userDetails = null;
        try {
            userDetails = UserPrincipal.create(user, getAuthorities());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userDetails;

    }

    public static Collection<? extends GrantedAuthority> getAuthorities() {
        String[] userRoles = {Constants.DEFAULT_USER_ROLE};
        return AuthorityUtils.createAuthorityList(userRoles);
    }
}
