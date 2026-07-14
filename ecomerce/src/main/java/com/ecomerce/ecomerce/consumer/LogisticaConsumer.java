package com.ecomerce.ecomerce.consumer;

import com.ecomerce.ecomerce.dto.PedidoDTO;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

@Component
public class LogisticaConsumer {

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(
                    value = "fila.logistica",
                    durable = "true",
                    arguments = {
                            // Define a Exchange para onde os erros vão
                            @Argument(name = "x-dead-letter-exchange", value = "dlx.exchange"),
                            // Define o nome da fila de erro
                            @Argument(name = "x-dead-letter-routing-key", value = "fila.logistica.dlq")
                    }
            ),
            exchange = @Exchange(value = "pedidos.fanout", type = ExchangeTypes.FANOUT)
    ))
    public void separarEstoque(PedidoDTO pedido){
        System.out.println("[LOGISTICA] iniciando separação do Pedido: "+ pedido.id() + " para " + pedido.cliente() );

        if (pedido.cliente().equalsIgnoreCase("Hacker")) {
            System.out.println("[LOGÍSTICA] ERRO FATAL! Tentativa de fraude detectada no pedido " + pedido.id());
            throw new RuntimeException("Erro ao processar pedido na logística!");
        }

        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("[LOGÍSTICA] Pedido " + pedido.id() + " separado com sucesso!");
    }
}
