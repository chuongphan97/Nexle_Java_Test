package com.nexle.java_test.config.jwt;

import com.nexle.java_test.utils.constants.APIConstants;
import com.nexle.java_test.utils.constants.Constants;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.ws.rs.BadRequestException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtProvider {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    public boolean validateJwtToken(String authToken) throws ServletException {
        try {
            if (isTokenExpired(authToken)) {
                throw new ServletException(APIConstants.ERROR_JWT_TOKEN_EXPIRED);
            }
            Jwts.parser().setSigningKey(this.jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException | UnsupportedJwtException | IllegalArgumentException | MalformedJwtException e) {
            throw new ServletException(APIConstants.ERROR_JWT_INVALID_TOKEN);
        } catch (ExpiredJwtException e) {
            throw new ServletException(APIConstants.ERROR_JWT_TOKEN_EXPIRED);
        }
    }

    public AccessTokenDTO createAccessToken(Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        String name = String.valueOf(principal.getUserId());
        long now = (new Date()).getTime();
        long dateToMilliseconds = 24 * 60 * 60 * 1000L;
        long hourToMilliseconds = 60 * 60 * 60 * 1000L;
        Date validity = new Date(now + hourToMilliseconds);
        Date refreshTokenExpiration = new Date(now + 30 * dateToMilliseconds);
        //Build access token
        String jwt = Jwts.builder().setSubject(name)
                .setClaims(buildClaims(principal))
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS512, this.jwtSecret).compact();

        //Build refresh token
        String refreshToken = Jwts.builder().setSubject(name)
                .setExpiration(refreshTokenExpiration)
                .signWith(SignatureAlgorithm.HS512, this.jwtSecret)
                .compact();
        AccessTokenDTO accessToken = new AccessTokenDTO();
        accessToken.setToken(jwt);
        accessToken.setTokenExpiredIn(validity);
        accessToken.setRefreshToken(refreshToken);
        accessToken.setRefreshTokenExpiredIn(refreshTokenExpiration);
        accessToken.setUserId(principal.getUserId());
        accessToken.setTokenType(Constants.JWT_TOKEN_TYPE);
        return accessToken;
    }

    /**
     * build Claims
     *
     * @param principal User Principal
     * @return
     */
    private Map<String, Object> buildClaims(UserPrincipal principal) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", principal.getUserId());
        return claims;
    }

    public Object getClaimInfo(String token, String claimKey) throws BadRequestException {
        Claims claims = Jwts.parser().
                setSigningKey(this.jwtSecret).
                parseClaimsJws(token).
                getBody();
        return claims.get(claimKey);
    }

    private boolean isTokenExpired(String token) {
        final Date expiration = Jwts.parser().setSigningKey(this.jwtSecret).parseClaimsJws(token).getBody().getExpiration();
        return expiration.before(new Date());
    }
}
