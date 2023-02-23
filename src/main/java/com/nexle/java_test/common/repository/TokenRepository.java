package com.nexle.java_test.common.repository;

import com.nexle.java_test.common.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Integer> {
    void deleteAllByUserId(Integer userId);

    Optional<Token> findByRefreshToken(String refreshToken);
}
