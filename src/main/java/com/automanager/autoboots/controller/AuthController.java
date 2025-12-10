package com.automanager.autoboots.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.automanager.autoboots.dto.ErrorResponse;
import com.automanager.autoboots.dto.LoginRequest;
import com.automanager.autoboots.dto.LoginResponse;
import com.automanager.autoboots.dto.RegisterRequest;
import com.automanager.autoboots.model.Usuario;
import com.automanager.autoboots.repository.UsuarioRepository;
import com.automanager.autoboots.security.JwtUtil;

import jakarta.validation.Valid;

/**
 * ✅ CORRIGIDO: Register agora é PROTEGIDO - apenas usuários autenticados podem
 * criar outros usuários
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Login - PÚBLICO
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            final UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());
            final Usuario usuario = usuarioRepository.findByUsername(loginRequest.getUsername()).orElseThrow();
            final String jwt = jwtUtil.generateToken(userDetails);

            return ResponseEntity.ok(new LoginResponse(jwt, usuario.getUsername(), usuario.getRole().name()));

        } catch (Exception e) {
            return ResponseEntity.status(401)
                    .body(new ErrorResponse(401, "Unauthorized", "Credenciais inválidas", "/api/auth/login"));
        }
    }

    /**
     * ✅ CORRIGIDO: Register agora é PROTEGIDO Requer autenticação de ADMIN,
     * GERENTE ou VENDEDOR
     */
    @PostMapping("/register")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE', 'VENDEDOR')")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        if (usuarioRepository.existsByUsername(registerRequest.getUsername())) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(400, "Bad Request", "Username já existe!", "/api/auth/register"));
        }

        if (usuarioRepository.existsByEmail(registerRequest.getEmail())) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(400, "Bad Request", "Email já existe!", "/api/auth/register"));
        }

        Usuario usuario = new Usuario();
        usuario.setNome(registerRequest.getNome());
        usuario.setUsername(registerRequest.getUsername());
        usuario.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        usuario.setEmail(registerRequest.getEmail());
        usuario.setRole(registerRequest.getRole());
        usuario.setDataCadastro(LocalDateTime.now());

        usuarioRepository.save(usuario);

        return ResponseEntity.ok().body("Usuário registrado com sucesso!");
    }
}
