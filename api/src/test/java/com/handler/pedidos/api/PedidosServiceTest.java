package com.handler.pedidos.api;

import com.handler.pedidos.api.exception.NumeroControleJaCadastradoException;
import com.handler.pedidos.api.model.Pedidos;
import com.handler.pedidos.api.repository.PedidoRepository;
import com.handler.pedidos.api.service.PedidosService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PedidosServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @InjectMocks
    private PedidosService pedidosService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    public void testSalvarPedidos() throws Exception {
        Pedidos pedido = new Pedidos();
        pedido.setCodigoCliente(5L);
        pedido.setNumeroControle("123");
        pedido.setValor(new BigDecimal("100"));
        pedido.setQuantidade(5);
        pedido.setDataCadastro(LocalDateTime.now());

        List<Pedidos> pedidos = Arrays.asList(pedido);

        when(pedidoRepository.saveAll(anyList())).thenReturn(pedidos);

        List<Pedidos> savedPedidos = pedidosService.salvarPedidos(pedidos);

        assertNotNull(savedPedidos);
        assertFalse(savedPedidos.isEmpty());
        assertEquals(1, savedPedidos.size());
        verify(pedidoRepository).saveAll(anyList());
    }

    @Test
    public void testValidaCodigoClienteComExcecao() {
        Pedidos pedido = new Pedidos();
        pedido.setCodigoCliente(0L); // Código inválido
        pedido.setNumeroControle("123");
        pedido.setValor(new BigDecimal("100"));
        pedido.setQuantidade(5);
        pedido.setDataCadastro(LocalDateTime.now());

        List<Pedidos> pedidos = Arrays.asList(pedido);

        Exception exception = assertThrows(NumeroControleJaCadastradoException.class, () -> {
            pedidosService.salvarPedidos(pedidos);
        });

        assertEquals("Código de cliente inválido.", exception.getMessage());
    }

    @Test
    public void testValidaNumeroControleCadastradoComExcecao() {
        Pedidos pedido = new Pedidos();
        pedido.setNumeroControle("123"); // Número de controle supostamente já cadastrado
        pedido.setCodigoCliente(5L);
        pedido.setValor(new BigDecimal("100"));
        pedido.setQuantidade(5);
        pedido.setDataCadastro(LocalDateTime.now());

        when(pedidoRepository.findByNumeroControle("123")).thenReturn(Optional.of(pedido));

        Exception exception = assertThrows(NumeroControleJaCadastradoException.class, () -> {
            pedidosService.salvarPedidos(Collections.singletonList(pedido));
        });

        assertEquals("Número de controle já cadastrado.", exception.getMessage());
    }

    @Test
    public void testDataCadastroDefinidaQuandoNula() throws Exception {
        Pedidos pedido = new Pedidos();
        pedido.setCodigoCliente(5L);
        pedido.setNumeroControle("124");
        pedido.setValor(new BigDecimal("200"));
        pedido.setQuantidade(2);
        // DataCadastro é nulo aqui

        when(pedidoRepository.saveAll(anyList())).thenAnswer(i -> i.getArgument(0));

        List<Pedidos> resultado = pedidosService.salvarPedidos(Collections.singletonList(pedido));
        assertNotNull(resultado.get(0).getDataCadastro());
    }

    @Test
    public void testValidaQuantidadeDefinidaQuandoNula() throws Exception {
        Pedidos pedido = new Pedidos();
        pedido.setCodigoCliente(5L);
        pedido.setNumeroControle("125");
        pedido.setValor(new BigDecimal("300"));
        // Quantidade é nulo aqui

        when(pedidoRepository.saveAll(anyList())).thenAnswer(i -> i.getArgument(0));

        List<Pedidos> resultado = pedidosService.salvarPedidos(Collections.singletonList(pedido));
        assertEquals(1, resultado.get(0).getQuantidade().intValue());
    }

    @Test
    public void testCalculaValorDesconto() {
        // Supondo que o método calculaValorDesconto seja público para facilitar o teste
        BigDecimal valor = new BigDecimal("100");
        int quantidade = 10; // Isso deve acionar o desconto de 10%

        BigDecimal valorDesconto = pedidosService.calculaValorDesconto(valor, quantidade);
        BigDecimal valorEsperado = new BigDecimal("900.00"); // 100 * 10 - 10%

        assertEquals(0, valorDesconto.compareTo(valorEsperado));
    }

    @Test
    public void testConsultaPorNumeroControle() {
        Pedidos pedido = new Pedidos();
        pedido.setNumeroControle("123");
        pedido.setCodigoCliente(5L);
        pedido.setValor(new BigDecimal("400"));
        pedido.setQuantidade(2);
        pedido.setDataCadastro(LocalDateTime.now());

        when(pedidoRepository.findByNumeroControle("123")).thenReturn(Optional.of(pedido));

        List<Pedidos> resultado = pedidosService.consultaTodosPedidos("123", null);

        assertFalse(resultado.isEmpty());
        assertEquals("123", resultado.get(0).getNumeroControle());
    }


}
