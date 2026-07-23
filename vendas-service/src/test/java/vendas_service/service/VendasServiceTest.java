package vendas_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vendas_service.client.LivroClient;
import vendas_service.dto.ItemRequest;
import vendas_service.dto.LivroDTO;
import vendas_service.dto.VendaRequest;
import vendas_service.enums.EnumPagamentoVenda;
import vendas_service.enums.EnumStatusVenda;
import vendas_service.model.ItemVenda;
import vendas_service.model.VendasModel;
import vendas_service.repository.VendasRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VendasServiceTest {

    @Mock
    private VendasRepository vendasRepository;

    @Mock
    private LivroClient livroClient;

    @Mock
    private VendaEventPublisher vendaEventPublisher;

    @InjectMocks
    private VendasService vendasService;

    @Test
    void vender_DeveRealizarVendaComSucesso() {
        LivroDTO livro = new LivroDTO(1L, "1984", BigDecimal.valueOf(29.90), 10);
        when(livroClient.buscarPorId(1L)).thenReturn(livro);

        VendaRequest request = new VendaRequest();
        request.setFormaPagamento(EnumPagamentoVenda.PIX);
        request.setItens(List.of(new ItemRequest() {{ setLivroId(1L); setQuantidade(2); }}));

        VendasModel vendaSalva = new VendasModel();
        vendaSalva.setId(1L);
        vendaSalva.setValorTotal(BigDecimal.valueOf(59.80));
        vendaSalva.setStatus(EnumStatusVenda.REALIZADA);
        when(vendasRepository.save(any())).thenReturn(vendaSalva);

        VendasModel resultado = vendasService.vender(request);

        assertNotNull(resultado);
        assertEquals(EnumStatusVenda.REALIZADA, resultado.getStatus());
        verify(livroClient).buscarPorId(1L);
        verify(vendaEventPublisher).publicarVendaRealizada(any(), eq(1L), eq(2));
        verify(vendasRepository).save(any());
    }

    @Test
    void vender_DeveLancarErroQuandoLivroNaoEncontrado() {
        when(livroClient.buscarPorId(1L)).thenReturn(null);

        VendaRequest request = new VendaRequest();
        request.setFormaPagamento(EnumPagamentoVenda.PIX);
        request.setItens(List.of(new ItemRequest() {{ setLivroId(1L); setQuantidade(1); }}));

        assertThrows(RuntimeException.class, () -> vendasService.vender(request));
        verify(vendasRepository, never()).save(any());
    }

    @Test
    void vender_DeveLancarErroQuandoEstoqueInsuficiente() {
        LivroDTO livro = new LivroDTO(1L, "1984", BigDecimal.valueOf(29.90), 1);
        when(livroClient.buscarPorId(1L)).thenReturn(livro);

        VendaRequest request = new VendaRequest();
        request.setFormaPagamento(EnumPagamentoVenda.PIX);
        request.setItens(List.of(new ItemRequest() {{ setLivroId(1L); setQuantidade(5); }}));

        assertThrows(RuntimeException.class, () -> vendasService.vender(request));
        verify(vendasRepository, never()).save(any());
    }

    @Test
    void vender_DeveLancarErroQuandoParcelamentoInvalido() {
        LivroDTO livro = new LivroDTO(1L, "1984", BigDecimal.valueOf(29.90), 10);
        when(livroClient.buscarPorId(1L)).thenReturn(livro);

        VendaRequest request = new VendaRequest();
        request.setFormaPagamento(EnumPagamentoVenda.PIX);
        request.setNumeroParcelas(3);
        request.setItens(List.of(new ItemRequest() {{ setLivroId(1L); setQuantidade(1); }}));

        assertThrows(RuntimeException.class, () -> vendasService.vender(request));
    }

    @Test
    void vender_DeveCalcularParcelasCorretamente() {
        LivroDTO livro = new LivroDTO(1L, "1984", BigDecimal.valueOf(30.00), 10);
        when(livroClient.buscarPorId(1L)).thenReturn(livro);

        VendaRequest request = new VendaRequest();
        request.setFormaPagamento(EnumPagamentoVenda.CREDITO);
        request.setNumeroParcelas(3);
        request.setItens(List.of(new ItemRequest() {{ setLivroId(1L); setQuantidade(1); }}));

        VendasModel vendaSalva = new VendasModel();
        vendaSalva.setId(1L);
        when(vendasRepository.save(any())).thenReturn(vendaSalva);

        vendasService.vender(request);

        ArgumentCaptor<VendasModel> captor = ArgumentCaptor.forClass(VendasModel.class);
        verify(vendasRepository).save(captor.capture());
        VendasModel capturada = captor.getValue();

        assertEquals(3, capturada.getNumeroParcelas());
        assertEquals(BigDecimal.valueOf(10.00).setScale(2), capturada.getValorParcelas());
    }

    @Test
    void cancelarVenda_DeveCancelarComSucesso() {
        ItemVenda item = new ItemVenda();
        item.setLivroId(1L);
        item.setQuantidade(2);

        VendasModel venda = new VendasModel();
        venda.setId(1L);
        venda.setStatus(EnumStatusVenda.REALIZADA);
        venda.setItens(List.of(item));

        when(vendasRepository.findById(1L)).thenReturn(Optional.of(venda));

        vendasService.cancelarVenda(1L);

        assertEquals(EnumStatusVenda.CANCELADA, venda.getStatus());
        verify(vendaEventPublisher).publicarVendaCancelada(1L, 1L, 2);
    }

    @Test
    void cancelarVenda_DeveLancarErroQuandoJaCancelada() {
        VendasModel venda = new VendasModel();
        venda.setStatus(EnumStatusVenda.CANCELADA);

        when(vendasRepository.findById(1L)).thenReturn(Optional.of(venda));

        assertThrows(RuntimeException.class, () -> vendasService.cancelarVenda(1L));
        verify(vendaEventPublisher, never()).publicarVendaCancelada(any(), any(), any());
    }

    @Test
    void cancelarVenda_DeveLancarErroQuandoNaoEncontrada() {
        when(vendasRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> vendasService.cancelarVenda(1L));
    }

    @Test
    void buscarPorId_DeveRetornarVenda() {
        VendasModel venda = new VendasModel();
        venda.setId(1L);
        when(vendasRepository.findById(1L)).thenReturn(Optional.of(venda));

        VendasModel resultado = vendasService.buscarPorId(1L);

        assertEquals(1L, resultado.getId());
    }

    @Test
    void buscarPorId_DeveLancarErroQuandoNaoEncontrada() {
        when(vendasRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> vendasService.buscarPorId(1L));
    }
}
