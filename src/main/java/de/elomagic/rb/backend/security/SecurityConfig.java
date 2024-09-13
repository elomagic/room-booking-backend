package de.elomagic.rb.backend.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final ApiKeyAuthFilter authFilter;

    @Value("${application.cors.allowed-origins}")
    private String allowedOrigins;
    @Value("${application.cors.allowed-methods}")
    private String allowedMethods;
    @Value("${application.cors.allowed-headers}")
    private String allowedHeaders;

    public SecurityConfig(@Autowired ApiKeyAuthFilter authFilter) {
        this.authFilter = authFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
                // .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(AbstractHttpConfigurer::disable)
                .securityMatcher("/api/appointment")
                .authorizeHttpRequests(registry -> registry
                                .requestMatchers("/api/appointment").authenticated()
                                .anyRequest().permitAll()
                        //.requestMatchers(AntPathRequestMatcher.antMatcher("/**")).permitAll()
                        //.anyRequest().authenticated()
                )
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(allowedOrigins.split(",")));
        configuration.setAllowedMethods(List.of(allowedMethods.split(",")));
        configuration.setAllowedHeaders(List.of(allowedHeaders.split(",")));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

}