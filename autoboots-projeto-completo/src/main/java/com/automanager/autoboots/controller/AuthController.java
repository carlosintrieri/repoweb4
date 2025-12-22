package com.automanager.autoboots.controller;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.automanager.autoboots.dto.LoginRequest;
import com.automanager.autoboots.dto.LoginResponse;
import com.automanager.autoboots.dto.RegisterRequest;
import com.automanager.autoboots.model.Usuario;
import com.automanager.autoboots.repository.UsuarioRepository;
import com.automanager.autoboots.security.JwtUtil;

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

            return ResponseEntity.ok(new LoginResponse(jwt, usuario.getUsername(), usuario.getRole()));
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
        usuario.setDataCadastro(new Date());
        usuario.setRole("CLIENTE");

        usuarioRepository.save(usuario);

        return ResponseEntity.ok("Cliente registrado com sucesso!");
    }

    @PostMapping("/create-user")
    public ResponseEntity<?> createUser(@RequestBody RegisterRequest registerRequest) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userRole = auth.getAuthorities().iterator().next().getAuthority();

        if (usuarioRepository.existsByUsername(registerRequest.getUsername())) {
            return ResponseEntity.badRequest().body("Username já existe!");
        }

        if (registerRequest.getRole() == null || registerRequest.getRole().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Role é obrigatória!");
        }

        String roleToCreate = registerRequest.getRole().trim();

        // Validações por role
        if (userRole.equals("ROLE_VENDEDOR")) {
            if (!roleToCreate.equals("CLIENTE")) {
                return ResponseEntity.status(403)
                        .body("VENDEDOR só pode criar usuários do tipo CLIENTE");
            }
        } else if (userRole.equals("ROLE_GERENTE")) {
            if (roleToCreate.equals("ADMINISTRADOR")) {
                return ResponseEntity.status(403)
                        .body("GERENTE não pode criar usuários do tipo ADMINISTRADOR");
            }
        }

        Usuario usuario = new Usuario();
        usuario.setNome(registerRequest.getNome());
        usuario.setUsername(registerRequest.getUsername());
        usuario.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        usuario.setEmail(registerRequest.getEmail());
        usuario.setDataCadastro(new Date());
        usuario.setRole(roleToCreate);

        Usuario saved = usuarioRepository.save(usuario);

        return ResponseEntity.ok("Usuário criado com sucesso com role: " + saved.getRole());
    }
}
