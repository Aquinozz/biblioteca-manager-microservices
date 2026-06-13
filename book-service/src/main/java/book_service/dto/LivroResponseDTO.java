package book_service.dto;

import book_service.enums.EnumLivro;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LivroResponseDTO {

    private Long id;
    private String titulo;
    private String autor;
    private String descricao;
    private Integer anoCriacao;
    private BigDecimal preco;
    private Integer quantidade;
    private EnumLivro categoria;
}