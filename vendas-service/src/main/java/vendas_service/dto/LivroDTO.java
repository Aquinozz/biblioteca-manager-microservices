package vendas_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LivroDTO {

    private Long id;

    private String titulo;

    private BigDecimal preco;

    private Integer quantidade;
}