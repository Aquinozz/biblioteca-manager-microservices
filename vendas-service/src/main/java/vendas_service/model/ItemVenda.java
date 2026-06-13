package vendas_service.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

// getters e setters
@Getter
@Setter

@Entity
@Table(name = "item_venda")
public class ItemVenda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;



    private Long livroId;   // ID do livro no microserviço de livros

    //esse json ignore deixa o retorno mais limpo
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "venda_id")
    private VendasModel venda;

    private Integer quantidade;
    private BigDecimal precoUnitario;




}
