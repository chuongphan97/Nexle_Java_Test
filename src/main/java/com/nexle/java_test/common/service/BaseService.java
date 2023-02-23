package com.nexle.java_test.common.service;

import com.nexle.java_test.common.entity.User;
import com.nexle.java_test.common.repository.UserRepository;
import com.nexle.java_test.config.jwt.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class BaseService {

    @Autowired
    private static UserRepository userRepository;

    /**
     * Get current userId has logged.
     *
     * @return
     */
    public Integer getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer userId = null;
        if (principal instanceof UserDetails) {
            userId = ((UserPrincipal) principal).getUserId();
        } else if (!principal.equals("anonymousUser")) {
            userId = Integer.parseInt((String) principal);
        }
        return userId;
    }

    /**
     * Get current user has logged
     *
     * @return
     */
    public User getCurrentUserLogged() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = null;
        if (principal instanceof UserDetails) {
            UserPrincipal userPrincipal = (UserPrincipal) principal;
            user = userRepository.findById(userPrincipal.getUserId()).orElse(null);
        }
        return user;
    }
}
