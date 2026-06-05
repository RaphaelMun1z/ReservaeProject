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

        UserProfile profile = repository.findById(keycloakId)
            .orElseGet(() -> createNewProfileFromJwt(keycloakId, jwt));

        return new UserProfileResponseDTO(
            profile.getId(),
            profile.getFullName(),
            profile.getEmail(),
            profile.getDocument()
        );
    }

    private UserProfile createNewProfileFromJwt(String id, Jwt jwt) {
        String email = jwt.getClaimAsString("email");
        String name = jwt.getClaimAsString("name");
        String document = jwt.getClaimAsString("cpf");
        UserProfile newProfile = new UserProfile(id, name, email, document);
        return repository.save(newProfile);
    }
}
