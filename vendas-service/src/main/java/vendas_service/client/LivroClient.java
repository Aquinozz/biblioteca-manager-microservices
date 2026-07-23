package vendas_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import vendas_service.dto.AtualizarEstoqueRequest;
import vendas_service.dto.LivroDTO;

@FeignClient(name = "book-service", fallback = LivroClientFallback.class)
public interface LivroClient {

    @GetMapping("/livros/{id}")
    LivroDTO buscarPorId(@PathVariable("id") Long id);

    @PutMapping("/livros/{id}/estoque")
    void atualizarEstoque(
            @PathVariable("id") Long id,
            @RequestBody AtualizarEstoqueRequest request
    );
}