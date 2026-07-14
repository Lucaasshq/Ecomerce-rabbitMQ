package com.ecomerce.ecomerce.consumer;

import com.ecomerce.ecomerce.dto.PedidoDTO;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class LogisticaDLQConsumer {

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "fila.logistica.dlq", durable = "true"),
            exchange = @Exchange(value = "dlx.exchange", type = ExchangeTypes.DIRECT),
            key = "fila.logistica.dlq"
    ))
    public void processarMensagemMorta(PedidoDTO pedido) {
        System.out.println("[ALERTA ] O pedido " + pedido.id() + " falhou repetidas vezes e foi enviado para o arquivo morto.");
        System.out.println("[ALERTA ] Dados para auditoria: Cliente -> " + pedido.cliente() + " | Valor -> R$" + pedido.valorTotal());
    }
}
