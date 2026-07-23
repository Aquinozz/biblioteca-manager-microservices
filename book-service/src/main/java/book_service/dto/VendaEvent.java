package book_service.dto;

public record VendaEvent(
        String tipo,
        Long vendaId,
        Long livroId,
        Integer quantidade
) {}
