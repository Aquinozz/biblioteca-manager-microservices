package auth_service.users.service;

import java.util.Set;

import auth_service.config.TokenProvider;
import auth_service.users.dto.LoginRequestDto;
import auth_service.users.dto.RegisterRequestDto;
import auth_service.users.dto.TokenResponseDto;
import auth_service.users.enums.RolesType;
import auth_service.users.models.RolesEntity;
import auth_service.users.models.Users;
import auth_service.users.repository.RolesRepository;
import auth_service.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final RolesRepository rolesRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;
    private long expiration=900000;

    public void register(RegisterRequestDto registerRequestDto) throws BadRequestException {

        log.info("Tentando registrar usuário com email: {}", registerRequestDto.getEmail());

        Users user = userRepository.findByEmail(registerRequestDto.getEmail())
                .orElse(null);

        if (user != null) {
            log.warn("Tentativa de registro com email já existente: {}", registerRequestDto.getEmail());
            throw new BadRequestException("Usuário já cadastrado");
        }

        RolesEntity role = rolesRepository.findByNome(RolesType.ROLE_LEITOR.name())
                .orElseGet(() -> {
                    log.info("Role ROLE_LEITOR não encontrada, criando nova");
                    return rolesRepository.save(
                            RolesEntity.builder()
                                    .nome(RolesType.ROLE_LEITOR.name())
                                    .build()
                    );
                });

        userRepository.save(
                Users.builder()
                        .nome(registerRequestDto.getNome())
                        .email(registerRequestDto.getEmail())
                        .senha(passwordEncoder.encode(registerRequestDto.getSenha()))
                        .roles(Set.of(role))
                        .build()
        );

        log.info("Usuário registrado com sucesso: {}", registerRequestDto.getEmail());
    }

    public TokenResponseDto login(LoginRequestDto dto) throws Exception {

        log.info("Tentativa de login para email: {}", dto.getEmail());

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getSenha())
            );

            String token = tokenProvider.gerarToken(authentication);

            log.info("Login realizado com sucesso para: {}", dto.getEmail());

            return new TokenResponseDto(token, expiration);

        } catch (BadCredentialsException e) {

            log.warn("Falha no login (credenciais inválidas) para: {}", dto.getEmail());

            throw new BadRequestException("credenciais inválidas");

        } catch (Exception e) {

            log.error("Erro inesperado no login para: {}", dto.getEmail(), e);

            throw e;
        }
    }
}