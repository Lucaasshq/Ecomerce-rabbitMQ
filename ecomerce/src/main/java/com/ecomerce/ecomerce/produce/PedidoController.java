package com.ecomerce.ecomerce.produce;

import com.ecomerce.ecomerce.config.RabbitConfig;
import com.ecomerce.ecomerce.dto.PedidoDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private RabbitTemplate rabbitTemplate;

    public PedidoController(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @PostMapping()
    public ResponseEntity<String> criarPedido(@RequestBody PedidoDTO pedido) {
        System.out.println("[API] Recebendo pedido: " + pedido.cliente());

        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_NAME, "", pedido);

        return  ResponseEntity.accepted().body("Pedido "+ pedido.id() +" em processamento!");
    }
}
