package com.handler.pedidos.api.repository;

import com.handler.pedidos.api.model.Pedidos;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PedidoRepository extends JpaRepository<Pedidos, Long> {

    Optional<Pedidos> findByNumeroControle(String numeroControle);
    List<Pedidos> findByDataCadastroBetween(LocalDateTime inicio, LocalDateTime termina);
}
