package com.baozi.baozistore.controller;

import com.baozi.baozistore.model.Cliente;
import com.baozi.baozistore.model.Pedido;
import com.baozi.baozistore.model.Produto;
import com.baozi.baozistore.repository.ClienteRepository;
import com.baozi.baozistore.repository.PedidoRepository;
import com.baozi.baozistore.repository.ProdutoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;

    public PedidoController(PedidoRepository pedidoRepository,
                            ClienteRepository clienteRepository,
                            ProdutoRepository produtoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.produtoRepository = produtoRepository;
    }

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody Pedido pedido) {
        if (pedido.getCliente() == null || pedido.getCliente().getId() == null
                || pedido.getProduto() == null || pedido.getProduto().getId() == null
                || pedido.getQuantidade() == null) {
            return ResponseEntity.badRequest()
                    .body("Informe cliente.id, produto.id e quantidade.");
        }

        Cliente cliente = clienteRepository.findById(pedido.getCliente().getId()).orElse(null);
        Produto produto = produtoRepository.findById(pedido.getProduto().getId()).orElse(null);

        if (cliente == null || produto == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Cliente ou produto não encontrado.");
        }

        pedido.setId(null);
        pedido.setCliente(cliente);
        pedido.setProduto(produto);
        Pedido salvo = pedidoRepository.save(pedido);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @GetMapping
    public List<Pedido> listar() {
        return pedidoRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pedido> buscarPorId(@PathVariable Long id) {
        return pedidoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> apagar(@PathVariable Long id) {
        if (!pedidoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        pedidoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}