package vendas_service.dto;

import vendas_service.enums.EnumPagamentoVenda;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter

@Schema(description = "Dados para realizar uma venda")
public class VendaRequest {


    @Schema(example = "PIX")
    private EnumPagamentoVenda formaPagamento;

    private List<ItemRequest> itens;

    private Integer numeroParcelas;

}