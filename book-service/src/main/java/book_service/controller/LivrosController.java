package book_service.controller;

import book_service.dto.LivroRequestDTO;
import book_service.dto.LivroResponseDTO;
import book_service.enums.EnumLivro;
import book_service.model.LivrosModel;
import book_service.service.LivrosService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import book_service.dto.AtualizarEstoqueRequest;

import java.util.List;

@Tag(name = "Livros", description = "Operações relacionadas aos livros")
@RestController
@RequestMapping("/livros")
public class LivrosController {

    private final LivrosService livrosService;

    public LivrosController(LivrosService livrosService) {
        this.livrosService = livrosService;
    }



    @GetMapping("/{id}")
    public ResponseEntity<LivrosModel> buscarPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                livrosService.buscarPorId(id)
        );
    }

    @GetMapping
    public ResponseEntity<List<LivrosModel>> listarLivros(
            @RequestParam(required = false) String autor,
            @RequestParam(required = false) String titulo,
            @RequestParam(required = false) EnumLivro categoria) {

        return ResponseEntity.ok(
                livrosService.filtrar(autor, titulo, categoria)
        );
    }

    @PutMapping("/{id}/estoque")
    public ResponseEntity<Void> atualizarEstoque(
            @PathVariable Long id,
            @RequestBody AtualizarEstoqueRequest request) {

        livrosService.atualizarEstoque(id, request.getQuantidade());

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Cria um novo livro")
    @PostMapping
    public ResponseEntity<LivroResponseDTO> criarLivro(
            @Valid @RequestBody LivroRequestDTO dto) {

        LivroResponseDTO livroCriado = livrosService.salvar(dto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(livroCriado);
    }

    @Operation(summary = "Deleta um livro pelo ID")
    @DeleteMapping("/{id}")
    public void deletarLivro(@PathVariable Long id){
        livrosService.deletar(id);
    }

    @Operation(summary = "Atualiza um livro existente")
    @PutMapping("/{id}")
    public LivrosModel atualizar(@PathVariable Long id, @RequestBody LivrosModel livro) {
        return livrosService.atualizar(id, livro);
    }
}