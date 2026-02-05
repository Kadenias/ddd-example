package de.adesso.schulungen.testapplication.security.services;

import de.adesso.schulungen.testapplication.exception.TokenRefreshException;
import de.adesso.schulungen.testapplication.infrasturcture.RefreshToken;
import de.adesso.schulungen.testapplication.infrasturcture.RefreshTokenRepository;
import de.adesso.schulungen.testapplication.infrasturcture.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

  @Value("${de.adesso.lasttest.jwtRefreshExpirationMs}")
  private Long refreshTokenDurationMs;

  @Autowired
  private RefreshTokenRepository refreshTokenRepository;

  @Autowired
  private UserRepository userRepository;

  public Optional<RefreshToken> findByToken(final String token) {
    return refreshTokenRepository.findByToken(token);
  }

  public RefreshToken createRefreshToken(final Long userId) {
    RefreshToken refreshToken = new RefreshToken();

    refreshToken.setUser(userRepository.findById(userId).get());
    refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
    refreshToken.setToken(UUID.randomUUID().toString());

    refreshToken = refreshTokenRepository.save(refreshToken);
    return refreshToken;
  }

  public RefreshToken verifyExpiration(final RefreshToken token) {
    if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
      refreshTokenRepository.delete(token);
      throw new TokenRefreshException(token.getToken(), "Refresh token was expired. Please make a new signin request");
    }

    return token;
  }
}
