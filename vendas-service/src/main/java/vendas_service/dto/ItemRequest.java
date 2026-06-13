package vendas_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class ItemRequest {

    private Long livroId;
    private Integer quantidade;
}