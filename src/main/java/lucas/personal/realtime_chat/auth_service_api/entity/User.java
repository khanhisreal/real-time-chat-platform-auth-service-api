package lucas.personal.realtime_chat.auth_service_api.entity;

import jakarta.validation.constraints.Size;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    Long userId;

    @Column(name = "username", unique = true, nullable = false, length = 50)
    @Size(max = 50)
    String username;

    @Column(name = "email", unique = true, nullable = false, length = 100)
    @Size(max = 100)
    String email;

    @Column(name = "password_hash", unique = true, nullable = false)
    String password;

}
