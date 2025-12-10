package com.automanager.autoboots.controller;

import com.automanager.autoboots.dto.ErrorResponse;
import com.automanager.autoboots.model.Venda;
import com.automanager.autoboots.model.Usuario;
import com.automanager.autoboots.repository.VendaRepository;
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

@RestController
@RequestMapping("/api/vendas")
public class VendaController {

    @Autowired
    private VendaRepository vendaRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE', 'VENDEDOR', 'CLIENTE')")
    public ResponseEntity<CollectionModel<Venda>> listarTodas() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        String role = auth.getAuthorities().iterator().next().getAuthority();
        
        List<Venda> vendas;
        Usuario usuario = usuarioRepository.findByUsername(username).orElseThrow();
        
        if (role.equals("ROLE_ADMINISTRADOR") || role.equals("ROLE_GERENTE")) {
            vendas = vendaRepository.findAll();
        } else if (role.equals("ROLE_VENDEDOR")) {
            vendas = vendaRepository.findByFuncionarioId(usuario.getId());
        } else { // CLIENTE
            vendas = vendaRepository.findByClienteId(usuario.getId());
        }
        
        for (Venda venda : vendas) {
            venda.add(linkTo(methodOn(VendaController.class).buscarPorId(venda.getId())).withSelfRel());
        }
        
        Link selfLink = linkTo(methodOn(VendaController.class).listarTodas()).withSelfRel();
        return ResponseEntity.ok(CollectionModel.of(vendas, selfLink));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE', 'VENDEDOR', 'CLIENTE')")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        String role = auth.getAuthorities().iterator().next().getAuthority();
        
        return vendaRepository.findById(id)
                .map(venda -> {
                    Usuario usuario = usuarioRepository.findByUsername(username).orElseThrow();
                    
                    if (role.equals("ROLE_VENDEDOR")) {
                        if (!venda.getFuncionario().getId().equals(usuario.getId())) {
                            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                    .body(new ErrorResponse(403, "Forbidden", 
                                            "VENDEDOR só pode visualizar próprias vendas", "/api/vendas/" + id));
                        }
                    } else if (role.equals("ROLE_CLIENTE")) {
                        if (!venda.getCliente().getId().equals(usuario.getId())) {
                            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                    .body(new ErrorResponse(403, "Forbidden", 
                                            "CLIENTE só pode visualizar próprias compras", "/api/vendas/" + id));
                        }
                    }
                    
                    venda.add(linkTo(methodOn(VendaController.class).buscarPorId(id)).withSelfRel());
                    return ResponseEntity.ok(venda);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE', 'VENDEDOR')")
    public ResponseEntity<Venda> criar(@Valid @RequestBody Venda venda) {
        // Calcula valor total automaticamente
        venda.setValorTotal(venda.calcularValorTotal());
        
        Venda saved = vendaRepository.save(venda);
        saved.add(linkTo(methodOn(VendaController.class).buscarPorId(saved.getId())).withSelfRel());
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE')")
    public ResponseEntity<Venda> atualizar(@PathVariable Long id, @Valid @RequestBody Venda vendaAtualizada) {
        return vendaRepository.findById(id)
                .map(venda -> {
                    venda.setCliente(vendaAtualizada.getCliente());
                    venda.setFuncionario(vendaAtualizada.getFuncionario());
                    venda.setVeiculo(vendaAtualizada.getVeiculo());
                    venda.setMercadorias(vendaAtualizada.getMercadorias());
                    venda.setServicos(vendaAtualizada.getServicos());
                    
                    // Recalcula valor total
                    venda.setValorTotal(venda.calcularValorTotal());
                    
                    Venda saved = vendaRepository.save(venda);
                    saved.add(linkTo(methodOn(VendaController.class).buscarPorId(id)).withSelfRel());
                    return ResponseEntity.ok(saved);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        if (vendaRepository.existsById(id)) {
            vendaRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
