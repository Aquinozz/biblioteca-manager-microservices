package vendas_service.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Getter
@AllArgsConstructor
public enum EnumStatusVenda {


    CANCELADA ("C", "CANCELADA"),
    REALIZADA ("R", "REALIZADA");

    private  String codigo;
    private  String descricao;

    @JsonCreator
    public static EnumStatusVenda situacaoVenda(String codigo){
        if (codigo == null) {
            throw new IllegalArgumentException("Código da venda não pode ser null");
        }

        for (EnumStatusVenda status : values()) {
            if (status.codigo.equalsIgnoreCase(codigo)) {
                return status;
            }
        }

        throw new IllegalArgumentException("Código inválido para EnumVenda: " + codigo);
        }
    }

