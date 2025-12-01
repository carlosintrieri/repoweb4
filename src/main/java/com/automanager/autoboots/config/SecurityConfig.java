package com.automanager.autoboots.config;

import com.automanager.autoboots.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuração de segurança do Spring Security
 * 
 * Esta classe configura:
 * - Endpoints públicos vs protegidos
 * - Filtro JWT para autenticação
 * - Encoder de senha (BCrypt)
 * - Políticas de sessão (Stateless)
 * - Permissões baseadas em roles
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity  // Habilita @PreAuthorize nos controllers
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    
    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Desabilita CSRF (não precisamos em API REST stateless)
                .csrf(csrf -> csrf.disable())
                
                // Configura autorização de requisições
                .authorizeHttpRequests(auth -> auth
                    // Endpoints PÚBLICOS (não precisam token)
                    .requestMatchers("/api/auth/**").permitAll()
                    .requestMatchers("/h2-console/**").permitAll()
                    .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                    
                    // Endpoints PROTEGIDOS com regras específicas
                    .requestMatchers(HttpMethod.DELETE, "/api/usuarios/**").hasRole("ADMINISTRADOR")
                    .requestMatchers(HttpMethod.GET, "/api/usuarios/**").hasAnyRole("ADMINISTRADOR", "GERENTE", "VENDEDOR", "CLIENTE")
                    
                    // Todos os outros endpoints precisam autenticação
                    .anyRequest().authenticated()
                )
                
                // Política de sessão STATELESS (não mantém sessão no servidor)
                .sessionManagement(session -> 
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                
                // Configura o provider de autenticação
                .authenticationProvider(authenticationProvider())
                
                // ⭐ ADICIONA O FILTRO JWT ANTES DO FILTRO PADRÃO DO SPRING
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        // Permite frames (necessário para H2 Console)
        http.headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }

    /**
     * Encoder de senha usando BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Provider de autenticação que usa nosso UserDetailsService
     * e o PasswordEncoder configurado
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Bean do AuthenticationManager usado no AuthController
     * para autenticar usuários no endpoint /login
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
