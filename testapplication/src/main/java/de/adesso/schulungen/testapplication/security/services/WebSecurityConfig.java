package de.adesso.schulungen.testapplication.security.services;

import de.adesso.schulungen.testapplication.security.jwt.AuthEntryPointJwt;
import de.adesso.schulungen.testapplication.security.jwt.AuthTokenFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Configuration
@EnableMethodSecurity
public class WebSecurityConfig {

  private static final String[] AUTH_WHITELIST = {
          // -- Swagger UI v2
          "/v2/api-docs",
          "/swagger-resources",
          "/swagger-resources/**",
          "/configuration/ui",
          "/configuration/security",
          "/swagger-ui.html",
          "/webjars/**",
          // -- Swagger UI v3 (OpenAPI)
          "/v3/api-docs/**",
          "/swagger-ui/**",
          "/actuator/prometheus"
  };

  @Autowired
  private UserDetailsServiceImpl userDetailsService;

  @Autowired
  private AuthEntryPointJwt unauthorizedHandler;

  @Bean
  public AuthTokenFilter authenticationJwtTokenFilter() {
    return new AuthTokenFilter();
  }
  
  @Bean
  public DaoAuthenticationProvider authenticationProvider() {
      final DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
       
      authProvider.setUserDetailsService(userDetailsService);
      authProvider.setPasswordEncoder(passwordEncoder());
   
      return authProvider;
  }
  
  @Bean
  public AuthenticationManager authenticationManager(final AuthenticationConfiguration authConfig) throws Exception {
    return authConfig.getAuthenticationManager();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public FilterRegistrationBean<OncePerRequestFilter> loggingFilter() {
    FilterRegistrationBean<OncePerRequestFilter> registrationBean = new FilterRegistrationBean<>();
    registrationBean.setFilter(new OncePerRequestFilter() {
      @Override
      protected void doFilterInternal(HttpServletRequest request,
                                      HttpServletResponse response,
                                      FilterChain filterChain) throws ServletException, IOException {
        log.info("🌐 Received request: {} {}", request.getMethod(), request.getRequestURI());
        filterChain.doFilter(request, response);
      }
    });
    registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
    return registrationBean;
  }
  
  @Bean
  public SecurityFilterChain filterChain(final HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> 
          auth
                  .requestMatchers("/api/auth/**").permitAll()
                  // TODO should not be necessary
                  .requestMatchers("/api/auth/signup").permitAll()
                  .requestMatchers("/api/test/**").permitAll()
                  .requestMatchers("/api/book/getAll").permitAll()
                  .requestMatchers("/api/book/find/**").permitAll()
                  .requestMatchers("/api/book/reserve/**").permitAll()
                  .requestMatchers("/actuator/prometheus").permitAll()
                  .requestMatchers("/graphiql/**").permitAll()
                  .requestMatchers("/graphql").permitAll()
                  .requestMatchers("/graphql/schema.json").permitAll()
                  .requestMatchers(AUTH_WHITELIST).permitAll()
                  .anyRequest().authenticated()
        );
    
    http.authenticationProvider(authenticationProvider());

    http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
    
    return http.build();
  }
}
