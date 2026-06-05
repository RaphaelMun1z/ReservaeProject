package user_profile_service.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.RestController;
import user_profile_service.controllers.contracts.UserProfileContract;
import user_profile_service.dtos.res.UserProfileResponseDTO;
import user_profile_service.services.UserProfileService;

@RestController
public class UserProfileController implements UserProfileContract {

    private final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @Override
    public ResponseEntity<UserProfileResponseDTO> getMyProfile(Jwt jwt) {
        return ResponseEntity.ok(userProfileService.syncAndGetProfile(jwt));
    }
}