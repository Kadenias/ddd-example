package de.adesso.schulungen.testapplication.web;

import de.adesso.schulungen.testapplication.domain.Role;
import de.adesso.schulungen.testapplication.exception.TokenRefreshException;
import de.adesso.schulungen.testapplication.infrasturcture.*;
import de.adesso.schulungen.testapplication.security.jwt.JwtUtils;
import de.adesso.schulungen.testapplication.security.services.RefreshTokenService;
import de.adesso.schulungen.testapplication.security.services.UserDetailsImpl;
import de.adesso.schulungen.testapplication.web.model.*;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

  @Autowired
  AuthenticationManager authenticationManager;

  @Autowired
  UserRepository userRepository;

  @Autowired
  RoleRepository roleRepository;

  @Autowired
  PasswordEncoder encoder;

  @Autowired
  JwtUtils jwtUtils;

  @Autowired
  RefreshTokenService refreshTokenService;

  @PostMapping("/signin")
  @Operation(summary = "Signs in a user. Returns the Bearer token used for request that need to be authenticated.")
  public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody final LoginRequest loginRequest) {

    final Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);

    final UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
    final String jwt = jwtUtils.generateJwtToken(userDetails);
    
    final List<String> roles = userDetails.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .toList();

    final RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

    return ResponseEntity.ok(new JwtResponse(
            jwt,
            refreshToken.getToken(),
            userDetails.getId(),
            userDetails.getUsername(),
            userDetails.getEmail(),
            roles));
  }

  @PostMapping("/signup")
  @Operation(summary = "Creates an new user account. Username and Email must be unique. Possbile roles: admin, mod, user")
  public ResponseEntity<?> registerUser(@Valid @RequestBody final SignupRequest signUpRequest) {
    if (userRepository.existsByUsername(signUpRequest.getUsername())) {
      return ResponseEntity
          .badRequest()
          .body(new MessageResponse("Error: Username is already taken!"));
    }

    if (userRepository.existsByEmail(signUpRequest.getEmail())) {
      return ResponseEntity
          .badRequest()
          .body(new MessageResponse("Error: Email is already in use!"));
    }

    // Create new user's account
    final UserEntity user = new UserEntity(signUpRequest.getUsername(),
               signUpRequest.getEmail(),
               encoder.encode(signUpRequest.getPassword()));

    final Set<String> strRoles = signUpRequest.getRoles();
    final Set<RoleEntity> roles = new HashSet<>();

    if (strRoles == null) {
      final RoleEntity userRole = roleRepository.findByName(Role.ROLE_USER)
          .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
      roles.add(userRole);
    } else {
      strRoles.forEach(role -> {
        switch (role.toLowerCase()) {
        case "admin":
          final RoleEntity adminRole = roleRepository.findByName(Role.ROLE_ADMIN)
              .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
          roles.add(adminRole);
          break;
        case "mod":
          final RoleEntity modRole = roleRepository.findByName(Role.ROLE_MODERATOR)
              .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
          roles.add(modRole);
          break;
        default:
          final RoleEntity userRole = roleRepository.findByName(Role.ROLE_USER)
              .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
          roles.add(userRole);
        }
      });
    }

    user.setRoles(roles);
    userRepository.save(user);

    return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
  }

  @PostMapping("/refreshtoken")
  public ResponseEntity<?> refreshtoken(@Valid @RequestBody final TokenRefreshRequest request) {
    final String requestRefreshToken = request.getRefreshToken();

    return refreshTokenService.findByToken(requestRefreshToken)
            .map(refreshTokenService::verifyExpiration)
            .map(RefreshToken::getUser)
            .map(user -> {
              final String token = jwtUtils.generateTokenFromUsername(user.getUsername());
              return ResponseEntity.ok(new TokenRefreshResponse(token, requestRefreshToken));
            })
            .orElseThrow(() -> new TokenRefreshException(requestRefreshToken,
                    "Refresh token is not in database!"));
  }
}
