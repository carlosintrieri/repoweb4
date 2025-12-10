package com.automanager.autoboots.model;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;

@Entity
@Table(name = "empresas")
public class Empresa extends RepresentationModel<Empresa> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Razão social é obrigatória")
    @Column(nullable = false)
    private String razaoSocial;

    @NotBlank(message = "Nome fantasia é obrigatório")
    @Column(nullable = false)
    private String nomeFantasia;

    @ElementCollection
    @CollectionTable(name = "empresa_telefones", joinColumns = @JoinColumn(name = "empresa_id"))
    @Column(name = "telefone")
    private List<String> telefones;

    @Valid
    @Embedded
    private Endereco endereco;

    // Construtores
    public Empresa() {
    }

    public Empresa(String razaoSocial, String nomeFantasia, List<String> telefones, Endereco endereco) {
        this.razaoSocial = razaoSocial;
        this.nomeFantasia = nomeFantasia;
        this.telefones = telefones;
        this.endereco = endereco;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = razaoSocial;
    }

    public String getNomeFantasia() {
        return nomeFantasia;
    }

    public void setNomeFantasia(String nomeFantasia) {
        this.nomeFantasia = nomeFantasia;
    }

    public List<String> getTelefones() {
        return telefones;
    }

    public void setTelefones(List<String> telefones) {
        this.telefones = telefones;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }
}
