package user_profile_service.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_user_profiles")
public class UserProfile {
    @Id
    private String id;

    private String fullName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true)
    private String document;

    public UserProfile() {
    }

    public UserProfile(String id, String fullName, String email, String document) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.document = document;
    }

    public String getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getDocument() {
        return document;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserProfile userProfile)) return false;
        return id != null && id.equals(userProfile.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
