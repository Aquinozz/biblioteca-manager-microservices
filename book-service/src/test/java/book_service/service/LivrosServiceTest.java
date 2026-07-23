package book_service.service;

import book_service.dto.LivroRequestDTO;
import book_service.dto.LivroResponseDTO;
import book_service.enums.EnumLivro;
import book_service.model.LivrosModel;
import book_service.repository.LivrosRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LivrosServiceTest {

    @Mock
    private LivrosRepository livrosRepository;

    @InjectMocks
    private LivrosService livrosService;

    @Test
    void buscarPorId_DeveRetornarLivro() {
        LivrosModel livro = new LivrosModel();
        livro.setId(1L);
        livro.setTitulo("1984");
        when(livrosRepository.findById(1L)).thenReturn(Optional.of(livro));

        LivrosModel resultado = livrosService.buscarPorId(1L);

        assertEquals("1984", resultado.getTitulo());
    }

    @Test
    void buscarPorId_DeveLancarErroQuandoNaoEncontrado() {
        when(livrosRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> livrosService.buscarPorId(1L));
    }

    @Test
    void salvar_DeveCriarLivroComSucesso() {
        LivroRequestDTO dto = new LivroRequestDTO();
        dto.setTitulo("1984");
        dto.setAutor("Orwell");
        dto.setAnoCriacao(1949);
        dto.setPreco(BigDecimal.valueOf(29.90));
        dto.setQuantidade(10);
        dto.setCategoria(EnumLivro.FICCAO_CIENTIFICA);

        LivrosModel livroSalvo = new LivrosModel();
        livroSalvo.setId(1L);
        livroSalvo.setTitulo("1984");
        when(livrosRepository.save(any())).thenReturn(livroSalvo);

        LivroResponseDTO resultado = livrosService.salvar(dto);

        assertNotNull(resultado);
        assertEquals("1984", resultado.getTitulo());
        verify(livrosRepository).save(any());
    }

    @Test
    void atualizarEstoque_DeveAtualizarQuantidade() {
        LivrosModel livro = new LivrosModel();
        livro.setId(1L);
        livro.setQuantidade(5);
        when(livrosRepository.findById(1L)).thenReturn(Optional.of(livro));

        livrosService.atualizarEstoque(1L, 10);

        assertEquals(10, livro.getQuantidade());
        verify(livrosRepository).save(livro);
    }

    @Test
    void atualizarEstoque_DeveLancarErroQuandoLivroNaoEncontrado() {
        when(livrosRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> livrosService.atualizarEstoque(1L, 10));
    }

    @Test
    void deletar_DeveRemoverLivro() {
        LivrosModel livro = new LivrosModel();
        livro.setId(1L);
        when(livrosRepository.findById(1L)).thenReturn(Optional.of(livro));

        livrosService.deletar(1L);

        verify(livrosRepository).delete(livro);
    }

    @Test
    void deletar_DeveLancarErroQuandoNaoEncontrado() {
        when(livrosRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> livrosService.deletar(1L));
    }

    @Test
    void filtrar_DeveFiltrarPorAutor() {
        when(livrosRepository.findByAutorContainingIgnoreCase("Orwell"))
                .thenReturn(List.of(new LivrosModel()));

        List<LivrosModel> resultado = livrosService.filtrar("Orwell", null, null);

        assertEquals(1, resultado.size());
    }

    @Test
    void filtrar_DeveFiltrarPorTitulo() {
        when(livrosRepository.findByTituloContainingIgnoreCase("1984"))
                .thenReturn(List.of(new LivrosModel()));

        List<LivrosModel> resultado = livrosService.filtrar(null, "1984", null);

        assertEquals(1, resultado.size());
    }

    @Test
    void filtrar_DeveFiltrarPorCategoria() {
        when(livrosRepository.findByCategoria(EnumLivro.FICCAO_CIENTIFICA))
                .thenReturn(List.of(new LivrosModel()));

        List<LivrosModel> resultado = livrosService.filtrar(null, null, EnumLivro.FICCAO_CIENTIFICA);

        assertEquals(1, resultado.size());
    }

    @Test
    void filtrar_DeveRetornarTodosQuandoSemFiltro() {
        when(livrosRepository.findAll()).thenReturn(List.of(new LivrosModel(), new LivrosModel()));

        List<LivrosModel> resultado = livrosService.filtrar(null, null, null);

        assertEquals(2, resultado.size());
    }
}
