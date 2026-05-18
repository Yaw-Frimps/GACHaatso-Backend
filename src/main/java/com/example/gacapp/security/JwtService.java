package com.example.gacapp.security;

import com.example.gacapp.config.JwtProperties;
import com.example.gacapp.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtProperties jwtProperties;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    public String extractApprovalStatus(String token) {
        return extractClaim(token, claims -> claims.get("approval", String.class));
    }

    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        final Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }


    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();

        if (userDetails instanceof User user) {
            String role = user.getRole() != null ? user.getRole().name() : "USER";
            String approval = user.getApprovalStatus() != null ? user.getApprovalStatus().name() : "NOT_REQUIRED";

            claims.put("role", role);
            claims.put("approval", approval);
            claims.put("userId", user.getId());
        }

        return generateToken(claims, userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtProperties.getExpiration()))
                .signWith(getSignInKey())
                .compact();
    }

    // ================= VALIDATION =================

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);

        boolean basicValid = username.equals(userDetails.getUsername()) && !isTokenExpired(token);

        if (userDetails instanceof User user) {
            String role = extractRole(token);
            String approval = extractApprovalStatus(token);

            String userRole = user.getRole() != null ? user.getRole().name() : "";
            String userApproval = user.getApprovalStatus() != null ? user.getApprovalStatus().name() : "NOT_REQUIRED";

            return basicValid && role.equals(userRole) && approval.equals(userApproval);
        }

        return basicValid;
    }

    // ================= INTERNAL =================

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSignInKey() {

        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecret());
        return Keys.hmacShaKeyFor(keyBytes);
    }
}