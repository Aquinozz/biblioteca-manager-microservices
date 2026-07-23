package vendas_service.service;


import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import vendas_service.dto.VendaEvent;



@Service
@RequiredArgsConstructor
public class VendaEventPublisher {

    private final KafkaTemplate<String, VendaEvent> kafkaTemplate;

    public void publicarVendaRealizada(Long vendaId, Long livroId, Integer quantidade) {
        kafkaTemplate.send("venda-estoque",
                new VendaEvent("REALIZADA", vendaId, livroId, quantidade));
    }

    public void publicarVendaCancelada(Long vendaId, Long livroId, Integer quantidade) {
        kafkaTemplate.send("venda-estoque",
                new VendaEvent("CANCELADA", vendaId, livroId, quantidade));
    }
}