package com.automanager.autoboots.controller;

import com.automanager.autoboots.dto.ErrorResponse;
import com.automanager.autoboots.model.Usuario;
import com.automanager.autoboots.model.Role;
import com.automanager.autoboots.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

/**
 * ✅ COMPLETAMENTE CORRIGIDO:
 * 1. Senha não criptografa duas vezes (detecta se já está hasheada)
 * 2. Admin consegue criar outro admin
 * 3. Mensagens de erro consistentes
 * 4. Validações de email único
 */
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE', 'VENDEDOR')")
    public ResponseEntity<CollectionModel<Usuario>> listarTodos() {
        List<Usuario> usuarios = usuarioRepository.findAll();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String role = auth.getAuthorities().iterator().next().getAuthority();

        if (role.equals("ROLE_GERENTE")) {
            usuarios = usuarios.stream()
                    .filter(u -> u.getRole() != Role.ADMINISTRADOR)
                    .collect(Collectors.toList());
        } else if (role.equals("ROLE_VENDEDOR")) {
            usuarios = usuarios.stream()
                    .filter(u -> u.getRole() == Role.CLIENTE)
                    .collect(Collectors.toList());
        }

        for (Usuario usuario : usuarios) {
            usuario.add(linkTo(methodOn(UsuarioController.class).buscarPorId(usuario.getId())).withSelfRel());

            if (role.equals("ROLE_ADMINISTRADOR")) {
                usuario.add(linkTo(methodOn(UsuarioController.class).excluir(usuario.getId())).withRel("delete"));
            }
        }

        Link selfLink = linkTo(methodOn(UsuarioController.class).listarTodos()).withSelfRel();

        return ResponseEntity.ok(CollectionModel.of(usuarios, selfLink));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE', 'VENDEDOR', 'CLIENTE')")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        String role = auth.getAuthorities().iterator().next().getAuthority();

        return usuarioRepository.findById(id)
                .map(usuario -> {
                    if (role.equals("ROLE_CLIENTE")) {
                        Usuario usuarioLogado = usuarioRepository.findByUsername(username)
                                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

                        if (!usuario.getId().equals(usuarioLogado.getId())) {
                            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                    .body(new ErrorResponse(403, "Forbidden", 
                                            "CLIENTE só pode visualizar próprio cadastro", "/api/usuarios/" + id));
                        }
                    } else if (role.equals("ROLE_VENDEDOR")) {
                        if (usuario.getRole() != Role.CLIENTE) {
                            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                    .body(new ErrorResponse(403, "Forbidden", 
                                            "VENDEDOR só pode visualizar usuários do tipo CLIENTE", "/api/usuarios/" + id));
                        }
                    } else if (role.equals("ROLE_GERENTE")) {
                        if (usuario.getRole() == Role.ADMINISTRADOR) {
                            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                    .body(new ErrorResponse(403, "Forbidden", 
                                            "GERENTE não pode visualizar usuários do tipo ADMINISTRADOR", "/api/usuarios/" + id));
                        }
                    }

                    usuario.add(linkTo(methodOn(UsuarioController.class).buscarPorId(id)).withSelfRel());
                    usuario.add(linkTo(methodOn(UsuarioController.class).listarTodos()).withRel("all-usuarios"));

                    if (role.equals("ROLE_ADMINISTRADOR")) {
                        usuario.add(linkTo(methodOn(UsuarioController.class).excluir(id)).withRel("delete"));
                    }

                    return ResponseEntity.ok(usuario);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * ✅ CORREÇÃO CRÍTICA: Detecta se senha já está hasheada (BCrypt)
     * BCrypt hashes começam com $2a$, $2b$ ou $2y$
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE', 'VENDEDOR')")
    public ResponseEntity<?> criar(@Valid @RequestBody Usuario usuario) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String role = auth.getAuthorities().iterator().next().getAuthority();

        // Validações de permissão
        if (role.equals("ROLE_VENDEDOR")) {
            if (usuario.getRole() != Role.CLIENTE) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ErrorResponse(403, "Forbidden", 
                                "VENDEDOR só pode criar usuários do tipo CLIENTE", "/api/usuarios"));
            }
        } else if (role.equals("ROLE_GERENTE")) {
            if (usuario.getRole() == Role.ADMINISTRADOR) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ErrorResponse(403, "Forbidden", 
                                "GERENTE não pode criar usuários do tipo ADMINISTRADOR", "/api/usuarios"));
            }
        }

        // Validação de username único
        if (usuarioRepository.findByUsername(usuario.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(400, "Bad Request", "Username já existe", "/api/usuarios"));
        }
        
        // Validação de email único
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(400, "Bad Request", "Email já existe", "/api/usuarios"));
        }
        
        // ✅ CORREÇÃO: Só criptografa se NÃO for hash BCrypt
        String senha = usuario.getPassword();
        if (senha != null && !senha.matches("^\\$2[ayb]\\$.{56}$")) {
            // Não é hash BCrypt, então criptografa
            usuario.setPassword(passwordEncoder.encode(senha));
        }
        // Se já é hash BCrypt, mantém como está
        
        if (usuario.getDataCadastro() == null) {
            usuario.setDataCadastro(LocalDateTime.now());
        }

        Usuario saved = usuarioRepository.save(usuario);

        saved.add(linkTo(methodOn(UsuarioController.class).buscarPorId(saved.getId())).withSelfRel());
        saved.add(linkTo(methodOn(UsuarioController.class).listarTodos()).withRel("all-usuarios"));

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE', 'VENDEDOR')")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @Valid @RequestBody Usuario usuarioAtualizado) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String role = auth.getAuthorities().iterator().next().getAuthority();

        return usuarioRepository.findById(id)
                .map(usuario -> {
                    if (role.equals("ROLE_VENDEDOR")) {
                        if (usuario.getRole() != Role.CLIENTE) {
                            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                    .body(new ErrorResponse(403, "Forbidden", 
                                            "VENDEDOR só pode atualizar usuários do tipo CLIENTE", "/api/usuarios/" + id));
                        }
                    } else if (role.equals("ROLE_GERENTE")) {
                        if (usuario.getRole() == Role.ADMINISTRADOR) {
                            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                    .body(new ErrorResponse(403, "Forbidden", 
                                            "GERENTE não pode atualizar usuários do tipo ADMINISTRADOR", "/api/usuarios/" + id));
                        }
                        if (usuarioAtualizado.getRole() == Role.ADMINISTRADOR) {
                            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                    .body(new ErrorResponse(403, "Forbidden", 
                                            "GERENTE não pode promover usuários para ADMINISTRADOR", "/api/usuarios/" + id));
                        }
                    }

                    usuario.setNome(usuarioAtualizado.getNome());
                    usuario.setEmail(usuarioAtualizado.getEmail());

                    if (role.equals("ROLE_ADMINISTRADOR") || role.equals("ROLE_GERENTE")) {
                        usuario.setRole(usuarioAtualizado.getRole());
                    }

                    // ✅ CORREÇÃO: Só atualiza senha se vier preenchida e criptografa se necessário
                    if (usuarioAtualizado.getPassword() != null && !usuarioAtualizado.getPassword().isEmpty()) {
                        String senha = usuarioAtualizado.getPassword();
                        if (!senha.matches("^\\$2[ayb]\\$.{56}$")) {
                            usuario.setPassword(passwordEncoder.encode(senha));
                        } else {
                            usuario.setPassword(senha);
                        }
                    }

                    Usuario saved = usuarioRepository.save(usuario);

                    saved.add(linkTo(methodOn(UsuarioController.class).buscarPorId(id)).withSelfRel());
                    saved.add(linkTo(methodOn(UsuarioController.class).listarTodos()).withRel("all-usuarios"));

                    return ResponseEntity.ok(saved);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        if (usuarioRepository.existsById(id)) {
            usuarioRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
