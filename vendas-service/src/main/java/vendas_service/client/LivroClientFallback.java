package vendas_service.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import vendas_service.dto.AtualizarEstoqueRequest;
import vendas_service.dto.LivroDTO;

@Slf4j
@Component
public class LivroClientFallback implements LivroClient {

    @Override
    public LivroDTO buscarPorId(Long id) {
        log.warn("Fallback: book-service indisponivel ao buscar livro {}", id);
        return null;
    }

    @Override
    public void atualizarEstoque(Long id, AtualizarEstoqueRequest request) {
        log.warn("Fallback: book-service indisponivel ao atualizar estoque do livro {}", id);
    }
}