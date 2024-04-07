package com.handler.pedidos.api.service;


import com.handler.pedidos.api.exception.CustomException;
import com.handler.pedidos.api.exception.NumeroControleJaCadastradoException;
import com.handler.pedidos.api.model.Pedidos;
import com.handler.pedidos.api.repository.PedidoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class PedidosService {

    @Autowired
    PedidoRepository pedidoRepository;
    @Transactional
    public List<Pedidos> salvarPedidos(List<Pedidos> pedidos) throws Exception {
        for (Pedidos pedido : pedidos) {
            validaCodigoCliente(pedido);
            validaNumeroControleCadastrado(pedido);
            validaDataAtual(pedido);
            pedido.setQuantidade(validaQuantidade(pedido));
            BigDecimal valorTotal = calculaValorDesconto(pedido.getValor(), pedido.getQuantidade());
            pedido.setValor(valorTotal);
        }
        return pedidoRepository.saveAll(pedidos);
    }


    private void validaCodigoCliente(Pedidos pedidos) throws Exception {
        if(pedidos.getCodigoCliente() < 1 || pedidos.getCodigoCliente() > 10) {
            throw new NumeroControleJaCadastradoException("Código de cliente inválido.");
        }
    }
    private void validaNumeroControleCadastrado(Pedidos pedido) throws Exception {
        Optional<Pedidos> existente = pedidoRepository.findByNumeroControle(pedido.getNumeroControle());
        if (existente.isPresent()) {
           throw new NumeroControleJaCadastradoException("Número de controle já cadastrado.");
        }
    }

    private void validaDataAtual(Pedidos dataPedidos) {
        if(dataPedidos.getDataCadastro() == null) {
            dataPedidos.setDataCadastro(LocalDateTime.now());
        }
    }

    private int validaQuantidade(Pedidos quantidade) {

        if (quantidade.getQuantidade() == null) {
            quantidade.setQuantidade(1);
        }

        return quantidade.getQuantidade();
    }


    public BigDecimal calculaValorDesconto(BigDecimal valor, int quantidade) {
        BigDecimal desconto = BigDecimal.ZERO;
        if (quantidade > 5 && quantidade < 10) {
            desconto = BigDecimal.valueOf(0.05); // 5% de desconto
        } else if (quantidade >= 10) {
            desconto = BigDecimal.valueOf(0.10); // 10% de desconto
        }
        BigDecimal fatorDesconto = BigDecimal.ONE.subtract(desconto);
        return valor.multiply(BigDecimal.valueOf(quantidade)).multiply(fatorDesconto);
    }

    public List<Pedidos> consultaTodosPedidos(String numeroControle, LocalDate dataCadastro) {

        if(numeroControle != null) {
            Optional<Pedidos> pedidosOptional = pedidoRepository.findByNumeroControle(numeroControle);
            return pedidosOptional.map(Collections::singletonList).orElse(Collections.emptyList());
        } else if(dataCadastro != null) {
            LocalDateTime startDay = dataCadastro.atStartOfDay();
            LocalDateTime endDay = dataCadastro.atTime(23,59,59);

            return pedidoRepository.findByDataCadastroBetween(startDay, endDay);
        }

        else {
            return pedidoRepository.findAll();
        }

    }
}
