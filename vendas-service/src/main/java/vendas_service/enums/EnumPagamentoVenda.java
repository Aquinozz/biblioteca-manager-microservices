package vendas_service.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum EnumPagamentoVenda {

    PIX ("PIX"),
    DEBITO ("DEBITO"),
    CREDITO ("CREDITO");

    private String formaPagamento;
}
