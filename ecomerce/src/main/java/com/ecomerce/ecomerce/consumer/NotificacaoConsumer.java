package com.ecomerce.ecomerce.consumer;

import com.ecomerce.ecomerce.dto.PedidoDTO;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
public class NotificacaoConsumer {

    private JavaMailSender mailSender;

    public NotificacaoConsumer(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "fila.notificacao", durable = "true"),
            exchange = @Exchange(value = "pedidos.fanout", type = ExchangeTypes.FANOUT)
    ))
    public void enviarEmailDePagamento(PedidoDTO pedido) {
        System.out.println("✉️ [NOTIFICAÇÃO] Gerando cobrança PIX para: " + pedido.cliente());

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("lucashenriquebarros2016@gmail.com");
            helper.setTo(pedido.email());
            helper.setSubject("💠 Pagamento via PIX Liberado - Pedido #" + pedido.id());


            String suaChavePix = "84981753790";

            String seuLinkDoBanco = "https://nubank.com.br/pagar/sua-conta";


            String htmlTemplate = """
                    <div style='font-family: Arial, sans-serif; color: #333; max-width: 600px; margin: 0 auto; border: 1px solid #ddd; border-radius: 10px; padding: 20px;'>
                        <h2 style='color: #32bcad;'>Olá, %s!</h2>
                        <p>O seu pedido <b>#%s</b> foi reservado com sucesso no nosso sistema.</p>
                        <p>Para liberar a separação no estoque, realize o pagamento via <b>PIX</b> no valor exato de:</p>
                        <h3 style='color: #28a745;'>R$ %s</h3>
                        
                        <div style='background-color: #f8f9fa; padding: 15px; border-radius: 8px; text-align: center; margin: 20px 0;'>
                            <p style='margin: 0; font-size: 14px; color: #666;'> Chave PIX:</p>
                            <p style='margin: 5px 0 0 0; font-size: 18px; font-weight: bold; color: #000;'>%s</p>
                        </div>

                        <div style='text-align: center;'>
                            <a href='%s' style='background-color: #32bcad; color: white; padding: 12px 25px; text-decoration: none; border-radius: 5px; font-weight: bold; display: inline-block;'>
                                Pagar com Link do Banco
                            </a>
                        </div>
                        
                        <br>
                        <p style='font-size: 12px; color: #999; text-align: center;'>
                            *Este é um projeto acadêmico de Mensageria com RabbitMQ.*
                        </p>
                    </div>
                    """.formatted(
                    pedido.cliente(),
                    pedido.id(),
                    pedido.valorTotal(),
                    suaChavePix,
                    seuLinkDoBanco
            );

            helper.setText(htmlTemplate, true);
            mailSender.send(message);

            System.out.println("[NOTIFICAÇÃO] E-mail com PIX enviado com sucesso para " + pedido.email() + "!");

        } catch (MessagingException e) {
            System.out.println(" [NOTIFICAÇÃO] Erro ao montar o e-mail: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}