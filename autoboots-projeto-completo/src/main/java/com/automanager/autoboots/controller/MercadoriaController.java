package com.automanager.autoboots.controller;

import com.automanager.autoboots.model.Mercadoria;
import com.automanager.autoboots.repository.MercadoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/mercadorias")
public class MercadoriaController {

    @Autowired
    private MercadoriaRepository mercadoriaRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE', 'VENDEDOR')")
    public ResponseEntity<CollectionModel<Mercadoria>> listarTodas() {
        List<Mercadoria> mercadorias = mercadoriaRepository.findAll();
        for (Mercadoria mercadoria : mercadorias) {
            mercadoria.add(linkTo(methodOn(MercadoriaController.class).buscarPorId(mercadoria.getId())).withSelfRel());
        }
        Link selfLink = linkTo(methodOn(MercadoriaController.class).listarTodas()).withSelfRel();
        return ResponseEntity.ok(CollectionModel.of(mercadorias, selfLink));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE', 'VENDEDOR')")
    public ResponseEntity<Mercadoria> buscarPorId(@PathVariable Long id) {
        return mercadoriaRepository.findById(id)
                .map(mercadoria -> {
                    mercadoria.add(linkTo(methodOn(MercadoriaController.class).buscarPorId(id)).withSelfRel());
                    return ResponseEntity.ok(mercadoria);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE')")
    public ResponseEntity<Mercadoria> criar(@Valid @RequestBody Mercadoria mercadoria) {
        Mercadoria saved = mercadoriaRepository.save(mercadoria);
        saved.add(linkTo(methodOn(MercadoriaController.class).buscarPorId(saved.getId())).withSelfRel());
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE')")
    public ResponseEntity<Mercadoria> atualizar(@PathVariable Long id, @Valid @RequestBody Mercadoria mercadoriaAtualizada) {
        return mercadoriaRepository.findById(id)
                .map(mercadoria -> {
                    mercadoria.setNome(mercadoriaAtualizada.getNome());
                    mercadoria.setValor(mercadoriaAtualizada.getValor());
                    mercadoria.setQuantidade(mercadoriaAtualizada.getQuantidade());
                    mercadoria.setDescricao(mercadoriaAtualizada.getDescricao());
                    Mercadoria saved = mercadoriaRepository.save(mercadoria);
                    saved.add(linkTo(methodOn(MercadoriaController.class).buscarPorId(id)).withSelfRel());
                    return ResponseEntity.ok(saved);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        if (mercadoriaRepository.existsById(id)) {
            mercadoriaRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
