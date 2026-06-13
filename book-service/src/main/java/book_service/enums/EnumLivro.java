package book_service.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EnumLivro {

    ACAO("AÇÃO"),
    AVENTURA("AVENTURA"),
    ROMANCE("ROMANCE"),
    FANTASIA("FANTASIA"),
    FICCAO_CIENTIFICA("FICÇÃO CIENTÍFICA"),
    SUSPENSE("SUSPENSE"),
    TERROR("TERROR"),
    DRAMA("DRAMA"),
    BIOGRAFIA("BIOGRAFIA"),
    AUTOAJUDA("AUTOAJUDA"),
    HISTORIA("HISTÓRIA"),
    INFANTIL("INFANTIL");

    private String categoria;
}
