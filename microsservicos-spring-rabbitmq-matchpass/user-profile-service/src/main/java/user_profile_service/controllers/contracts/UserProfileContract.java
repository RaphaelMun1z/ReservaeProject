package user_profile_service.controllers.contracts;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import user_profile_service.dtos.res.UserProfileResponseDTO;

@Tag(name = "User Profile Endpoint", description = "Sincronização e consulta de dados do perfil do usuário logado")
@RequestMapping("/user-profile-service/api/profiles")
public interface UserProfileContract {
    @Operation(summary = "Sincronizar (se novo) e buscar os dados do usuário atualmente autenticado")
    @GetMapping("/me")
    ResponseEntity<UserProfileResponseDTO> getMyProfile(
        @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt
    );
}