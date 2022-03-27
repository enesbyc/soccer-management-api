package com.soccer.management.security;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.soccer.management.consts.Role;
import com.soccer.management.util.HttpUtil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

/**
 * @author enes.boyaci
 */
@Service
public class JwtUtil {

    private String secret;
    private int jwtExpirationInMs;

    @Value("${jwt.secret}")
    public void setSecret(String secret) {
        this.secret = secret;
    }

    @Value("${jwt.expirationDateInMs}")
    public void setJwtExpirationInMs(int jwtExpirationInMs) {
        this.jwtExpirationInMs = jwtExpirationInMs;
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();

        Collection<? extends GrantedAuthority> roles = userDetails.getAuthorities();

        if (roles.contains(new SimpleGrantedAuthority(Role.ADMIN.name()))) {
            claims.put("isAdmin", true);
        }
        if (roles.contains(new SimpleGrantedAuthority(Role.USER.name()))) {
            claims.put("isUser", true);
        }
        return doGenerateToken(claims, userDetails.getUsername());
    }

    public String doGenerateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder().setClaims(claims).setSubject(subject)
                        .setIssuedAt(new Date(System.currentTimeMillis()))
                        .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationInMs))
                        .signWith(SignatureAlgorithm.HS512, secret).compact();

    }

    public boolean validateToken(String authToken) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException | MalformedJwtException | UnsupportedJwtException
                 | IllegalArgumentException ex) {
            throw new BadCredentialsException("INVALID_CREDENTIALS", ex);
        } catch (ExpiredJwtException ex) {
            throw ex;
        }
    }

    public String getUsernameFromToken(String token) {
        if (Objects.nonNull(token) && !token.equals("")) {
            Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
            return claims.getSubject();
        } else
            return null;
    }

    public String getUsernameFromToken() {
        String token = HttpUtil.getTokenFromHeader();
        if (Objects.nonNull(token) && !token.equals("")) {
            Claims claims = Jwts.parser().setSigningKey(secret)
                            .parseClaimsJws(HttpUtil.getTokenFromHeader()).getBody();
            return claims.getSubject();
        } else
            return null;
    }

    public boolean getIsAdminFromToken() {
        String token = HttpUtil.getTokenFromHeader();
        if (Objects.nonNull(token) && !token.equals("")) {
            Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();

            Boolean isAdmin = claims.get("isAdmin", Boolean.class);
            if (Objects.isNull(isAdmin) || isAdmin == false)
                return false;
            else
                return true;
        } else
            return false;
    }

    public List<SimpleGrantedAuthority> getRolesFromToken(String token) {
        Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();

        List<SimpleGrantedAuthority> roles = null;

        Boolean isAdmin = claims.get("isAdmin", Boolean.class);
        Boolean isUser = claims.get("isUser", Boolean.class);

        if (isAdmin != null && isAdmin) {
            roles = Arrays.asList(new SimpleGrantedAuthority(Role.ADMIN.name()));
        }

        if (isUser != null && isUser) {
            roles = Arrays.asList(new SimpleGrantedAuthority(Role.USER.name()));
        }
        return roles;

    }

}
