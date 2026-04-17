package com.example.gacapp.security;

import com.example.gacapp.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    // ================= EXTRACT =================

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

    // ================= GENERATE =================

    public String generateToken(UserDetails userDetails) {

        Map<String, Object> claims = new HashMap<>();

        if (userDetails instanceof User user) {

            // ✅ Role (safe)
            String role = user.getRole() != null
                    ? user.getRole().name()
                    : "USER"; // fallback

            // ✅ Approval (NULL-SAFE FIX)
            String approval = user.getApprovalStatus() != null
                    ? user.getApprovalStatus().name()
                    : "NOT_REQUIRED"; // fallback

            claims.put("role", role);
            claims.put("approval", approval);
            claims.put("userId", user.getId());
        }

        return generateToken(claims, userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // ================= VALIDATION =================

    public boolean isTokenValid(String token, UserDetails userDetails) {

        final String username = extractUsername(token);

        boolean basicValid =
                username.equals(userDetails.getUsername()) &&
                        !isTokenExpired(token);

        if (userDetails instanceof User user) {

            String role = extractRole(token);
            String approval = extractApprovalStatus(token);

            String userRole = user.getRole() != null
                    ? user.getRole().name()
                    : "";

            String userApproval = user.getApprovalStatus() != null
                    ? user.getApprovalStatus().name()
                    : "NOT_REQUIRED";

            boolean roleMatches = role.equals(userRole);
            boolean approvalMatches = approval.equals(userApproval);

            return basicValid && roleMatches && approvalMatches;
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
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}