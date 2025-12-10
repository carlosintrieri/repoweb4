package com.automanager.autoboots.repository;

import com.automanager.autoboots.model.Venda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VendaRepository extends JpaRepository<Venda, Long> {
    List<Venda> findByFuncionarioId(Long funcionarioId);
    List<Venda> findByClienteId(Long clienteId);
}
