package book_service.service;

import book_service.dto.VendaEvent;
import book_service.model.LivrosModel;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class VendaRealizadaConsumer {

    private final LivrosService livrosService;

    @KafkaListener(topics = "venda-estoque")
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
