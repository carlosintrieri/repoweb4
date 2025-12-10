package com.automanager.autoboots.controller;

import com.automanager.autoboots.dto.ErrorResponse;
import com.automanager.autoboots.model.Veiculo;
import com.automanager.autoboots.model.Usuario;
import com.automanager.autoboots.repository.VeiculoRepository;
import com.automanager.autoboots.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

/**
 * ✅ CORRIGIDO:
 * - Cliente só pode criar veículo para si mesmo (PROBLEMA 11 CORRIGIDO)
 * - Mensagens de erro consistentes
 */
@RestController
@RequestMapping("/api/veiculos")
public class VeiculoController {

    @Autowired
    private VeiculoRepository veiculoRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE', 'VENDEDOR', 'CLIENTE')")
    public ResponseEntity<CollectionModel<Veiculo>> listarTodos() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        String role = auth.getAuthorities().iterator().next().getAuthority();
        
        List<Veiculo> veiculos;
        
        if (role.equals("ROLE_CLIENTE")) {
            Usuario usuario = usuarioRepository.findByUsername(username).orElseThrow();
            veiculos = veiculoRepository.findByUsuarioId(usuario.getId());
        } else {
            veiculos = veiculoRepository.findAll();
        }
        
        for (Veiculo veiculo : veiculos) {
            veiculo.add(linkTo(methodOn(VeiculoController.class).buscarPorId(veiculo.getId())).withSelfRel());
        }
        
        Link selfLink = linkTo(methodOn(VeiculoController.class).listarTodos()).withSelfRel();
        return ResponseEntity.ok(CollectionModel.of(veiculos, selfLink));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE', 'VENDEDOR', 'CLIENTE')")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        String role = auth.getAuthorities().iterator().next().getAuthority();
        
        return veiculoRepository.findById(id)
                .map(veiculo -> {
                    if (role.equals("ROLE_CLIENTE")) {
                        Usuario usuario = usuarioRepository.findByUsername(username).orElseThrow();
                        if (!veiculo.getUsuario().getId().equals(usuario.getId())) {
                            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                    .body(new ErrorResponse(403, "Forbidden", 
                                            "CLIENTE só pode visualizar próprios veículos", "/api/veiculos/" + id));
                        }
                    }
                    veiculo.add(linkTo(methodOn(VeiculoController.class).buscarPorId(id)).withSelfRel());
                    return ResponseEntity.ok(veiculo);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * ✅ CORREÇÃO CRÍTICA: Cliente só pode criar veículo para si mesmo
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE', 'VENDEDOR', 'CLIENTE')")
    public ResponseEntity<?> criar(@Valid @RequestBody Veiculo veiculo) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        String role = auth.getAuthorities().iterator().next().getAuthority();
        
        // ✅ CORREÇÃO: CLIENTE só pode criar veículo para si mesmo
        if (role.equals("ROLE_CLIENTE")) {
            Usuario usuarioLogado = usuarioRepository.findByUsername(username).orElseThrow();
            
            // Verifica se está tentando criar para outro usuário
            if (veiculo.getUsuario() != null && 
                !veiculo.getUsuario().getId().equals(usuarioLogado.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ErrorResponse(403, "Forbidden", 
                                "CLIENTE só pode criar veículos para si mesmo", "/api/veiculos"));
            }
            
            // Força o veículo a ser do usuário logado
            veiculo.setUsuario(usuarioLogado);
        }
        
        Veiculo saved = veiculoRepository.save(veiculo);
        saved.add(linkTo(methodOn(VeiculoController.class).buscarPorId(saved.getId())).withSelfRel());
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE', 'VENDEDOR', 'CLIENTE')")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @Valid @RequestBody Veiculo veiculoAtualizado) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        String role = auth.getAuthorities().iterator().next().getAuthority();
        
        return veiculoRepository.findById(id)
                .map(veiculo -> {
                    if (role.equals("ROLE_CLIENTE")) {
                        Usuario usuario = usuarioRepository.findByUsername(username).orElseThrow();
                        if (!veiculo.getUsuario().getId().equals(usuario.getId())) {
                            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                    .body(new ErrorResponse(403, "Forbidden", 
                                            "CLIENTE só pode atualizar próprios veículos", "/api/veiculos/" + id));
                        }
                    }
                    
                    veiculo.setPlaca(veiculoAtualizado.getPlaca());
                    veiculo.setModelo(veiculoAtualizado.getModelo());
                    veiculo.setMarca(veiculoAtualizado.getMarca());
                    veiculo.setAno(veiculoAtualizado.getAno());
                    veiculo.setTipoVeiculo(veiculoAtualizado.getTipoVeiculo());
                    
                    Veiculo saved = veiculoRepository.save(veiculo);
                    saved.add(linkTo(methodOn(VeiculoController.class).buscarPorId(id)).withSelfRel());
                    return ResponseEntity.ok(saved);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        if (veiculoRepository.existsById(id)) {
            veiculoRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
