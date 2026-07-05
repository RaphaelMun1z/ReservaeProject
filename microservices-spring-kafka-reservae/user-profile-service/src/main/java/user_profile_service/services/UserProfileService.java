package user_profile_service.services;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import user_profile_service.dtos.res.UserProfileResponseDTO;
import user_profile_service.entities.UserProfile;
import user_profile_service.repositories.UserProfileRepository;

@Service
public class UserProfileService {
    private final UserProfileRepository repository;

    public UserProfileService(UserProfileRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public UserProfileResponseDTO syncAndGetProfile(Jwt jwt) {
        String keycloakId = jwt.getSubject();
        String email = extractEmail(jwt);
        String fullName = extractFullName(jwt);
        String document = jwt.getClaimAsString("cpf");

        UserProfile profile = repository.findById(keycloakId)
            .orElseGet(() -> repository.save(new UserProfile(
                keycloakId,
                fullName,
                email,
                document
            )));

        profile.updateFromJwt(
            fullName,
            email
        );

        return new UserProfileResponseDTO(
            profile.getId(),
            profile.getFullName(),
            profile.getEmail(),
            profile.getDocument()
        );
    }

    private String extractFullName(Jwt jwt) {
        String name = jwt.getClaimAsString("name");
        if (name != null && !name.isBlank()) return name;

        String given = jwt.getClaimAsString("given_name");
        String family = jwt.getClaimAsString("family_name");

        if (given != null && family != null) return given + " " + family;
        if (given != null) return given;
        if (family != null) return family;

        return "Usuário";
    }

    private String extractEmail(Jwt jwt) {
        String email = jwt.getClaimAsString("email");
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Token sem claim de email — verifique o mapper no Keycloak");
        }
        return email;
    }
}
