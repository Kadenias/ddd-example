package de.adesso.schulungen.testapplication.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtils {
  private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

  @Value("${de.adesso.lasttest.jwtSecret}")
  private String jwtSecret;

  @Value("${de.adesso.lasttest.jwtExpirationMs}")
  private int jwtExpirationMs;

  public String generateTokenFromUsername(final String username) {
    return Jwts.builder()
            .subject(username)
            .issuedAt(new Date())
            .expiration(new Date(new Date().getTime() + jwtExpirationMs))
            .signWith(key())
            .compact();
  }

  public String generateJwtToken(final UserDetails userPrincipal) {
    return generateTokenFromUsername(userPrincipal.getUsername());
  }
  
  private SecretKey key() {
    return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
  }

  public String getUserNameFromJwtToken(final String token) {
    return Jwts.parser()
            .verifyWith(key())
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .getSubject();
  }

  public boolean validateJwtToken(final String authToken) {
    try {
      Jwts.parser().verifyWith(key()).build().parseSignedClaims(authToken);
      return true;
    } catch (final MalformedJwtException e) {
      logger.error("Invalid JWT token: {}", e.getMessage());
    } catch (final ExpiredJwtException e) {
      logger.error("JWT token is expired: {}", e.getMessage());
    } catch (final UnsupportedJwtException e) {
      logger.error("JWT token is unsupported: {}", e.getMessage());
    } catch (final IllegalArgumentException e) {
      logger.error("JWT claims string is empty: {}", e.getMessage());
    }

    return false;
  }
}
