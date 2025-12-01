package com.automanager.autoboots.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro de autenticação JWT
 * 
 * Este filtro intercepta todas as requisições HTTP e:
 * 1. Extrai o token JWT do header Authorization
 * 2. Valida o token
 * 3. Carrega as informações do usuário
 * 4. Configura o contexto de segurança do Spring
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        
        // DEBUG: Log da requisição
        System.out.println("🔍 [JWT FILTER] URI: " + request.getRequestURI());
        
        // Pega o header Authorization
        final String authorizationHeader = request.getHeader("Authorization");
        
        System.out.println("🔍 [JWT FILTER] Authorization Header: " + authorizationHeader);

        String username = null;
        String jwt = null;

        // Verifica se o header existe e começa com "Bearer "
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            // Remove "Bearer " e pega só o token
            jwt = authorizationHeader.substring(7);
            
            System.out.println("🔍 [JWT FILTER] Token extraído: " + jwt.substring(0, Math.min(20, jwt.length())) + "...");
            
            try {
                // Extrai o username do token
                username = jwtUtil.extractUsername(jwt);
                System.out.println("✅ [JWT FILTER] Username extraído: " + username);
            } catch (Exception e) {
                // Token inválido ou expirado
                System.err.println("❌ [JWT FILTER] Erro ao extrair username: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("⚠️ [JWT FILTER] Sem token Bearer no header");
        }

        // Se temos username E ainda não há autenticação no contexto
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            
            System.out.println("🔍 [JWT FILTER] Carregando UserDetails para: " + username);
            
            try {
                // Carrega os detalhes do usuário do banco
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                
                System.out.println("🔍 [JWT FILTER] UserDetails carregado. Authorities: " + userDetails.getAuthorities());

                // Valida o token
                if (jwtUtil.validateToken(jwt, userDetails)) {
                    
                    System.out.println("✅ [JWT FILTER] Token válido!");
                    
                    // Cria o objeto de autenticação com as authorities (roles)
                    UsernamePasswordAuthenticationToken authenticationToken = 
                        new UsernamePasswordAuthenticationToken(
                            userDetails, 
                            null, 
                            userDetails.getAuthorities()
                        );
                    
                    // Adiciona detalhes da requisição
                    authenticationToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    
                    // Define a autenticação no contexto do Spring Security
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    
                    System.out.println("✅ [JWT FILTER] Autenticação configurada no SecurityContext!");
                } else {
                    System.err.println("❌ [JWT FILTER] Token inválido!");
                }
            } catch (Exception e) {
                System.err.println("❌ [JWT FILTER] Erro ao carregar usuário: " + e.getMessage());
                e.printStackTrace();
            }
        } else if (username != null) {
            System.out.println("⚠️ [JWT FILTER] Autenticação já existe no contexto");
        }
        
        // Continua a cadeia de filtros
        System.out.println("🔍 [JWT FILTER] Continuando cadeia de filtros...\n");
        filterChain.doFilter(request, response);
    }
}
