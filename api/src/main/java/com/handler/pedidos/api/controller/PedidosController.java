package com.handler.pedidos.api.controller;


import com.handler.pedidos.api.model.Pedidos;
import com.handler.pedidos.api.service.PedidosService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class PedidosController {

    @Autowired
    PedidosService pedidosService;



    @GetMapping("/consultar-pedidos")
    public List<Pedidos> consultarPedido(@RequestParam(required = false) String numeroControle,
                                             @RequestParam(required = false)
                                             @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataCadastro) {
        return pedidosService.consultaTodosPedidos(numeroControle, dataCadastro);
    }
    @PostMapping(consumes = {"application/json", "application/xml"})
    public ResponseEntity<List<Pedidos>> criarPedidos (@Valid @RequestBody List<Pedidos>
                                                  pedidos) throws Exception {
        List<Pedidos> pedidosSalvos = pedidosService.salvarPedidos(pedidos);

        return new ResponseEntity<>(pedidosSalvos, HttpStatus.CREATED);
    }

}
