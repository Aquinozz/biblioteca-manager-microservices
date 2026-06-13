package book_service.service;

import book_service.dto.LivroRequestDTO;
import book_service.dto.LivroResponseDTO;
import book_service.enums.EnumLivro;
import book_service.model.LivrosModel;
import book_service.repository.LivrosRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class LivrosService {

    // Realiza uma conexão "Automatica" com o DB
    @Autowired
    private LivrosRepository livrosRepository;

    public LivrosModel buscarPorId(Long id) {
        log.info("Buscando livro por ID: {}", id);

        return livrosRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Livro não encontrado - ID: {}", id);
                    return new RuntimeException("Livro não encontrado");
                });
    }

    @Transactional
    public void atualizarEstoque(Long id, Integer quantidade) {

        LivrosModel livro = livrosRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Livro não encontrado"));

        livro.setQuantidade(quantidade);

        livrosRepository.save(livro);
    }

    public LivroResponseDTO salvar(LivroRequestDTO dto)  {


        LivrosModel livro = new LivrosModel();

        log.info("Salvando livro - titulo: {}, autor: {}", livro.getTitulo(), livro.getAutor());

        livro.setTitulo(dto.getTitulo());
        livro.setAutor(dto.getAutor());
        livro.setDescricao(dto.getDescricao());
        livro.setAnoCriacao(dto.getAnoCriacao());
        livro.setPreco(dto.getPreco());
        livro.setQuantidade(dto.getQuantidade());
        livro.setCategoria(dto.getCategoria());

        log.info("Livro criado com sucesso");
        LivrosModel livroSalvo = livrosRepository.save(livro);

        return new LivroResponseDTO(
                livroSalvo.getId(),
                livroSalvo.getTitulo(),
                livroSalvo.getAutor(),
                livroSalvo.getDescricao(),
                livroSalvo.getAnoCriacao(),
                livroSalvo.getPreco(),
                livroSalvo.getQuantidade(),
                livroSalvo.getCategoria()
        );
    }

    public void deletar(Long id) {
        log.info("Tentando deletar livro - ID: {}", id);

        LivrosModel livro = livrosRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Livro não encontrado para deletar - ID: {}", id);
                    return new RuntimeException("Livro não encontrado");
                });

        log.info("Livro deletado com sucesso - ID: {}", id);
        livrosRepository.delete(livro);
    }

    public List<LivrosModel> filtrar (String autor, String titulo, EnumLivro categoria){

        log.info("Filtrando livros - autor: {}, titulo: {}, categoria: {}",
                autor, titulo, categoria);

        if (autor != null) {
            return livrosRepository.findByAutorContainingIgnoreCase(autor);
        }

        if (titulo != null){
            return livrosRepository.findByTituloContainingIgnoreCase(titulo);
        }

        if (categoria != null){
            return livrosRepository.findByCategoria(categoria);
        }

        return livrosRepository.findAll();
    }

    public LivrosModel atualizar(Long id, LivrosModel dadosAtualizados){

        log.info("Atualizando livro - ID: {}", id);

        LivrosModel livro = livrosRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Livro não encontrado para atualizar - ID: {}", id);
                    return new RuntimeException("Livro não encontrado");
                });

        if (dadosAtualizados.getTitulo() != null) {
            livro.setTitulo(dadosAtualizados.getTitulo());
        }
        if (dadosAtualizados.getAutor() != null) {
            livro.setAutor(dadosAtualizados.getAutor());
        }
        if (dadosAtualizados.getDescricao() != null) {
            livro.setDescricao(dadosAtualizados.getDescricao());
        }
        if (dadosAtualizados.getAnoCriacao() != null) {
            livro.setAnoCriacao(dadosAtualizados.getAnoCriacao());
        }
        if (dadosAtualizados.getPreco() != null) {
            livro.setPreco(dadosAtualizados.getPreco());
        }
        if (dadosAtualizados.getQuantidade() != null) {
            livro.setQuantidade(dadosAtualizados.getQuantidade());
        }

        log.info("Livro atualizado com sucesso - ID: {}", id);
        return livrosRepository.save(livro);
    }
}