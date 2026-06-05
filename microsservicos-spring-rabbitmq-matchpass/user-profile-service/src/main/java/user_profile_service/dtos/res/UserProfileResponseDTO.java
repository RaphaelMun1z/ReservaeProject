package user_profile_service.dtos.res;

public record UserProfileResponseDTO(
    String id,
    String fullName,
    String email,
    String document
) {
}