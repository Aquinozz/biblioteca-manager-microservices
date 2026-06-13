package book_service.repository;

import book_service.enums.EnumLivro;
import book_service.model.LivrosModel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LivrosRepository extends JpaRepository<LivrosModel, Long> {
    List<LivrosModel> findByAutorContainingIgnoreCase(String autor);
    List<LivrosModel> findByTituloContainingIgnoreCase(String titulo);


    List<LivrosModel> findByCategoria (EnumLivro categoria);

}