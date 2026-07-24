package book_service.service;

import book_service.dto.VendaEvent;
import book_service.model.LivrosModel;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class VendaRealizadaConsumer {

    private final LivrosService livrosService;
    private final KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @PostConstruct
    public void init() {
        new Thread(() -> {
            try {
                Thread.sleep(15000);
                log.info("Iniciando Kafka listener...");
                kafkaListenerEndpointRegistry.getListenerContainers()
                        .forEach(c -> c.start());
            } catch (Exception e) {
                log.warn("Erro ao iniciar Kafka listener: {}", e.getMessage());
            }
        }).start();
    }

    @KafkaListener(id = "venda-estoque-listener", topics = "venda-estoque", autoStartup = "false")
    public void consumir(VendaEvent evento) {
        LivrosModel livro = livrosService.buscarPorId(evento.livroId());

        int novoEstoque;
        if ("REALIZADA".equals(evento.tipo())) {
            novoEstoque = livro.getQuantidade() - evento.quantidade();
        } else {
            novoEstoque = livro.getQuantidade() + evento.quantidade();
        }

        livrosService.atualizarEstoque(evento.livroId(), novoEstoque);
    }
}
