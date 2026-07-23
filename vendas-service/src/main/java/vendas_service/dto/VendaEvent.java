package vendas_service.dto;

public record VendaEvent(
        String tipo,        // "REALIZADA" ou "CANCELADA"
        Long vendaId,
        Long livroId,
        Integer quantidade
) {}