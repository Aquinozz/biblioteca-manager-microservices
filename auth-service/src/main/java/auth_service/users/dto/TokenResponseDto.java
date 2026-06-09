package auth_service.users.dto;

public record TokenResponseDto(String token, long expiresIn) {
}
