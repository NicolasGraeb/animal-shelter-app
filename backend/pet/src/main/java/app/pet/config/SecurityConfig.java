package app.pet.config;

import app.pet.security.JwtAuthenticationFilter;
import app.pet.services.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig
    ) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public UserDetailsService userDetailsService(UserService userService) {
        return username -> {
            var user = userService.getUserByUsername(username);
            return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPasswordHash())
                .roles(user.getRole().name())
                .build();
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
          .cors(Customizer.withDefaults())
          .csrf(csrf -> csrf.disable())
          .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
          .authorizeHttpRequests(auth -> auth
              // pozwól wszystkiemu OPTIONS
              .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

              // publiczne
              .requestMatchers(HttpMethod.POST,   "/api/auth/**").permitAll()
              .requestMatchers("/api/refresh-token").permitAll()
              .requestMatchers("/swagger-ui.html","/swagger-ui/**","/v3/api-docs/**").permitAll()

              // zabezpieczone
              .requestMatchers(HttpMethod.POST,   "/api/animals").authenticated()
              .requestMatchers(HttpMethod.POST,   "/api/adoptions").authenticated()
              // <— zamiast ** używamy {id} lub *
              .requestMatchers(HttpMethod.PATCH,  "/api/adoptions/{id}/status").authenticated()

              .requestMatchers(HttpMethod.GET,    "/api/favorites", "/api/favorites/**").authenticated()
              .requestMatchers(HttpMethod.POST,   "/api/favorites").authenticated()
              .requestMatchers(HttpMethod.DELETE, "/api/favorites/**").authenticated()

              .anyRequest().authenticated()
          )
          .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(
        @Value("${allowed.origins}") String originsRaw
    ) {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowedOriginPatterns(List.of(originsRaw.split(",")));
        cfg.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        cfg.setAllowedHeaders(List.of("*"));
        cfg.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource src = new UrlBasedCorsConfigurationSource();
        src.registerCorsConfiguration("/**", cfg);
        return src;
    }
}
