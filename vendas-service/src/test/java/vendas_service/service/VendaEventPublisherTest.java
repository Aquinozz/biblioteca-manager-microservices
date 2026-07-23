package vendas_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import vendas_service.dto.VendaEvent;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VendaEventPublisherTest {

    @Mock
    private KafkaTemplate<String, VendaEvent> kafkaTemplate;

    @InjectMocks
    private VendaEventPublisher vendaEventPublisher;

    @Test
    void publicarVendaRealizada_DeveEnviarEventoParaKafka() {
        vendaEventPublisher.publicarVendaRealizada(1L, 1L, 2);

        ArgumentCaptor<VendaEvent> captor = ArgumentCaptor.forClass(VendaEvent.class);
        verify(kafkaTemplate).send(eq("venda-estoque"), captor.capture());

        VendaEvent evento = captor.getValue();
        assertEquals("REALIZADA", evento.tipo());
        assertEquals(1L, evento.vendaId());
        assertEquals(1L, evento.livroId());
        assertEquals(2, evento.quantidade());
    }

    @Test
    void publicarVendaCancelada_DeveEnviarEventoDeCancelamento() {
        vendaEventPublisher.publicarVendaCancelada(1L, 1L, 2);

        ArgumentCaptor<VendaEvent> captor = ArgumentCaptor.forClass(VendaEvent.class);
        verify(kafkaTemplate).send(eq("venda-estoque"), captor.capture());

        VendaEvent evento = captor.getValue();
        assertEquals("CANCELADA", evento.tipo());
        assertEquals(2, evento.quantidade());
    }
}
