package dev.retreever.example.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.retreever.config.RetreeverPublicPaths;
import dev.retreever.example.dto.envelope.ApiErrorResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;
import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(MockIdentityService identityService) {
        return username -> {
            MockIdentityService.MockAccount account = identityService.loadAccountByEmail(username);
            return User.withUsername(account.email())
                    .password(account.passwordHash())
                    .disabled(!account.enabled())
                    .accountLocked(account.locked())
                    .authorities(account.authorities().toArray(String[]::new))
                    .build();
        };
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint(ObjectMapper objectMapper) {
        return (request, response, authException) -> writeJsonError(
                response,
                HttpStatus.UNAUTHORIZED,
                "Unauthorized",
                authException.getMessage(),
                objectMapper
        );
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler(ObjectMapper objectMapper) {
        return (request, response, accessDeniedException) -> writeJsonError(
                response,
                HttpStatus.FORBIDDEN,
                "Forbidden",
                accessDeniedException.getMessage(),
                objectMapper
        );
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(Environment environment) {
        List<String> allowedOrigins = bindAllowedOrigins(environment);

        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(allowedOrigins);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(!allowedOrigins.isEmpty());

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public FilterRegistrationBean<DevCorsFilter> devCorsFilterRegistration(Environment environment) {
        FilterRegistrationBean<DevCorsFilter> registration =
                new FilterRegistrationBean<>(new DevCorsFilter(bindAllowedOrigins(environment)));
        registration.setName("devCorsFilter");
        registration.addUrlPatterns("/*");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registration;
    }

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            MockBearerAuthenticationFilter authenticationFilter,
            AuthenticationEntryPoint authenticationEntryPoint,
            AccessDeniedHandler accessDeniedHandler,
            CorsConfigurationSource corsConfigurationSource
    ) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(RetreeverPublicPaths.get()).permitAll()
                        .requestMatchers("/retreever/**", "/error").permitAll()
                        .requestMatchers("/api/v1/public/**").permitAll()
                        .requestMatchers("/api/v1/scenarios/public/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    private static List<String> bindAllowedOrigins(Environment environment) {
        Binder binder = Binder.get(environment);
        var configuredOrigins = binder.bind("retreever.dev.allow-cross-origin", Bindable.listOf(String.class));
        List<String> rawOrigins = configuredOrigins.isBound()
                ? configuredOrigins.get()
                : binder.bind("retreever.allow-cross-origin", Bindable.listOf(String.class)).orElse(List.of());

        return rawOrigins.stream()
                .filter(StringUtils::hasText)
                .map(String::trim)
                .distinct()
                .toList();
    }

    private static void writeJsonError(
            HttpServletResponse response,
            HttpStatus status,
            String message,
            String cause,
            ObjectMapper objectMapper
    ) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), ApiErrorResponse.build(message, cause));
    }
}
