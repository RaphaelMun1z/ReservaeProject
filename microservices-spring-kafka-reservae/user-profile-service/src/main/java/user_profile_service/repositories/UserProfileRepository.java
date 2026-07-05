package user_profile_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import user_profile_service.entities.UserProfile;

public interface UserProfileRepository extends JpaRepository<UserProfile, String> {
}
