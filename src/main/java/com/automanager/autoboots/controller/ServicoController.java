package com.automanager.autoboots.controller;

import com.automanager.autoboots.model.Servico;
import com.automanager.autoboots.repository.ServicoRepository;
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
@RequestMapping("/api/servicos")
public class ServicoController {

    @Autowired
    private ServicoRepository servicoRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE', 'VENDEDOR')")
    public ResponseEntity<CollectionModel<Servico>> listarTodos() {
        List<Servico> servicos = servicoRepository.findAll();
        for (Servico servico : servicos) {
            servico.add(linkTo(methodOn(ServicoController.class).buscarPorId(servico.getId())).withSelfRel());
        }
        Link selfLink = linkTo(methodOn(ServicoController.class).listarTodos()).withSelfRel();
        return ResponseEntity.ok(CollectionModel.of(servicos, selfLink));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE', 'VENDEDOR')")
    public ResponseEntity<Servico> buscarPorId(@PathVariable Long id) {
        return servicoRepository.findById(id)
                .map(servico -> {
                    servico.add(linkTo(methodOn(ServicoController.class).buscarPorId(id)).withSelfRel());
                    return ResponseEntity.ok(servico);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE')")
    public ResponseEntity<Servico> criar(@Valid @RequestBody Servico servico) {
        Servico saved = servicoRepository.save(servico);
        saved.add(linkTo(methodOn(ServicoController.class).buscarPorId(saved.getId())).withSelfRel());
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE')")
    public ResponseEntity<Servico> atualizar(@PathVariable Long id, @Valid @RequestBody Servico servicoAtualizado) {
        return servicoRepository.findById(id)
                .map(servico -> {
                    servico.setNome(servicoAtualizado.getNome());
                    servico.setValor(servicoAtualizado.getValor());
                    servico.setDescricao(servicoAtualizado.getDescricao());
                    Servico saved = servicoRepository.save(servico);
                    saved.add(linkTo(methodOn(ServicoController.class).buscarPorId(id)).withSelfRel());
                    return ResponseEntity.ok(saved);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        if (servicoRepository.existsById(id)) {
            servicoRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
