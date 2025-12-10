package com.automanager.autoboots.repository;

import com.automanager.autoboots.model.Mercadoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MercadoriaRepository extends JpaRepository<Mercadoria, Long> {
}
