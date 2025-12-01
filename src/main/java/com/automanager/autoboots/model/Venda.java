package com.automanager.autoboots.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.springframework.hateoas.RepresentationModel;

import java.util.Date;
import java.util.List;

/**
 * Entidade Venda
 */
@Entity
@Table(name = "vendas")
public class Venda extends RepresentationModel<Venda> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Data da venda é obrigatória")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date dataVenda;

    @Column(nullable = false)
    private Double valorTotal;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cliente_id", nullable = false)
    @JsonIgnoreProperties({"password", "dataCadastro", "email"})
    private Usuario cliente;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "funcionario_id", nullable = false)
    @JsonIgnoreProperties({"password", "dataCadastro", "email"})
    private Usuario funcionario;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "veiculo_id", nullable = false)
    @JsonIgnoreProperties({"usuario"})
    private Veiculo veiculo;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "venda_mercadorias",
        joinColumns = @JoinColumn(name = "venda_id"),
        inverseJoinColumns = @JoinColumn(name = "mercadoria_id")
    )
    private List<Mercadoria> mercadorias;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "venda_servicos",
        joinColumns = @JoinColumn(name = "venda_id"),
        inverseJoinColumns = @JoinColumn(name = "servico_id")
    )
    private List<Servico> servicos;

    // Construtores
    public Venda() {
        this.dataVenda = new Date();
        this.valorTotal = 0.0;
    }

    public Venda(Date dataVenda, Usuario cliente, Usuario funcionario, Veiculo veiculo, 
                 List<Mercadoria> mercadorias, List<Servico> servicos) {
        this.dataVenda = dataVenda;
        this.cliente = cliente;
        this.funcionario = funcionario;
        this.veiculo = veiculo;
        this.mercadorias = mercadorias;
        this.servicos = servicos;
        this.valorTotal = calcularValorTotal();
    }

    // Método para calcular valor total
    public Double calcularValorTotal() {
        double total = 0.0;
        
        if (mercadorias != null) {
            for (Mercadoria m : mercadorias) {
                if (m.getValor() != null) {
                    total += m.getValor();
                }
            }
        }
        
        if (servicos != null) {
            for (Servico s : servicos) {
                if (s.getValor() != null) {
                    total += s.getValor();
                }
            }
        }
        
        return total;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataVenda() {
        return dataVenda;
    }

    public void setDataVenda(Date dataVenda) {
        this.dataVenda = dataVenda;
    }

    public Double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(Double valorTotal) {
        this.valorTotal = valorTotal;
    }

    public Usuario getCliente() {
        return cliente;
    }

    public void setCliente(Usuario cliente) {
        this.cliente = cliente;
    }

    public Usuario getFuncionario() {
        return funcionario;
    }

    public void setFuncionario(Usuario funcionario) {
        this.funcionario = funcionario;
    }

    public Veiculo getVeiculo() {
        return veiculo;
    }

    public void setVeiculo(Veiculo veiculo) {
        this.veiculo = veiculo;
    }

    public List<Mercadoria> getMercadorias() {
        return mercadorias;
    }

    public void setMercadorias(List<Mercadoria> mercadorias) {
        this.mercadorias = mercadorias;
    }

    public List<Servico> getServicos() {
        return servicos;
    }

    public void setServicos(List<Servico> servicos) {
        this.servicos = servicos;
    }

    @Override
    public String toString() {
        return "Venda{" +
                "id=" + id +
                ", dataVenda=" + dataVenda +
                ", valorTotal=" + valorTotal +
                ", cliente=" + (cliente != null ? cliente.getId() : null) +
                ", funcionario=" + (funcionario != null ? funcionario.getId() : null) +
                ", veiculo=" + (veiculo != null ? veiculo.getId() : null) +
                '}';
    }
}
