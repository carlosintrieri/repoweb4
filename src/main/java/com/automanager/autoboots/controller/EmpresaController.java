package com.automanager.autoboots.controller;

import com.automanager.autoboots.model.Empresa;
import com.automanager.autoboots.repository.EmpresaRepository;
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
@RequestMapping("/api/empresas")
public class EmpresaController {

    @Autowired
    private EmpresaRepository empresaRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE')")
    public ResponseEntity<CollectionModel<Empresa>> listarTodas() {
        List<Empresa> empresas = empresaRepository.findAll();
        for (Empresa empresa : empresas) {
            empresa.add(linkTo(methodOn(EmpresaController.class).buscarPorId(empresa.getId())).withSelfRel());
        }
        Link selfLink = linkTo(methodOn(EmpresaController.class).listarTodas()).withSelfRel();
        return ResponseEntity.ok(CollectionModel.of(empresas, selfLink));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE')")
    public ResponseEntity<Empresa> buscarPorId(@PathVariable Long id) {
        return empresaRepository.findById(id)
                .map(empresa -> {
                    empresa.add(linkTo(methodOn(EmpresaController.class).buscarPorId(id)).withSelfRel());
                    return ResponseEntity.ok(empresa);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE')")
    public ResponseEntity<Empresa> criar(@Valid @RequestBody Empresa empresa) {
        Empresa saved = empresaRepository.save(empresa);
        saved.add(linkTo(methodOn(EmpresaController.class).buscarPorId(saved.getId())).withSelfRel());
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE')")
    public ResponseEntity<Empresa> atualizar(@PathVariable Long id, @Valid @RequestBody Empresa empresaAtualizada) {
        return empresaRepository.findById(id)
                .map(empresa -> {
                    empresa.setRazaoSocial(empresaAtualizada.getRazaoSocial());
                    empresa.setNomeFantasia(empresaAtualizada.getNomeFantasia());
                    empresa.setTelefones(empresaAtualizada.getTelefones());
                    empresa.setEndereco(empresaAtualizada.getEndereco());
                    Empresa saved = empresaRepository.save(empresa);
                    saved.add(linkTo(methodOn(EmpresaController.class).buscarPorId(id)).withSelfRel());
                    return ResponseEntity.ok(saved);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        if (empresaRepository.existsById(id)) {
            empresaRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
