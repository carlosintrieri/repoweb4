package com.automanager.autoboots.controller;

import com.automanager.autoboots.dto.LoginRequest;
import com.automanager.autoboots.dto.LoginResponse;
import com.automanager.autoboots.dto.RegisterRequest;
import com.automanager.autoboots.model.Usuario;
import com.automanager.autoboots.repository.UsuarioRepository;
import com.automanager.autoboots.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

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
    
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );
            
            final UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());
            final Usuario usuario = usuarioRepository.findByUsername(loginRequest.getUsername()).orElseThrow();
            final String jwt = jwtUtil.generateToken(userDetails);
            
            return ResponseEntity.ok(new LoginResponse(jwt, usuario.getUsername(), usuario.getRole().name()));
        } catch (Exception e) {
            return ResponseEntity.status(401).build();
        }
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        if (usuarioRepository.existsByUsername(registerRequest.getUsername())) {
            return ResponseEntity.badRequest().body("Username já existe!");
        }
        
        Usuario usuario = new Usuario();
        usuario.setNome(registerRequest.getNome());
        usuario.setUsername(registerRequest.getUsername());
        usuario.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        usuario.setEmail(registerRequest.getEmail());
        usuario.setRole(registerRequest.getRole());
        usuario.setDataCadastro(new Date());
        
        usuarioRepository.save(usuario);
        
        return ResponseEntity.ok("Usuário registrado com sucesso!");
    }
}
