package book_service.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TokenProviderTest {

    private TokenProvider tokenProvider;

    @BeforeEach
    void setUp() {
        tokenProvider = new TokenProvider();
        ReflectionTestUtils.setField(tokenProvider, "key", "MinhaChaveSuperSecretaComPeloMenos32Caracteres");
    }

    @Test
    void isTokenValid_DeveRetornarTrueParaTokenValido() {
        String token = gerarTokenValido();
        assertTrue(tokenProvider.isTokenValid(token));
    }

    @Test
    void isTokenValid_DeveRetornarFalseParaTokenInvalido() {
        assertFalse(tokenProvider.isTokenValid("token.invalido"));
    }

    @Test
    void getUsername_DeveExtrairUsername() {
        String token = gerarTokenValido();
        assertEquals("admin@email.com", tokenProvider.getUsername(token));
    }

    @Test
    void getRoles_DeveExtrairRoles() {
        String token = gerarTokenValido();
        List<String> roles = tokenProvider.getRoles(token);
        assertTrue(roles.contains("ROLE_ADMIN"));
    }

    private String gerarTokenValido() {
        String secret = "MinhaChaveSuperSecretaComPeloMenos32Caracteres";
        return io.jsonwebtoken.Jwts.builder()
                .subject("admin@email.com")
                .claim("roles", List.of("ROLE_ADMIN"))
                .issuedAt(new java.util.Date())
                .expiration(new java.util.Date(System.currentTimeMillis() + 3600000))
                .signWith(javax.crypto.SecretKey.class.cast(
                        io.jsonwebtoken.security.Keys.hmacShaKeyFor(secret.getBytes())))
                .compact();
    }
}
