package user_profile_service.dtos.req;

public record UserRequestDTO(
    String password,
    String clientId,
    String grantType,
    String username
) {
}
