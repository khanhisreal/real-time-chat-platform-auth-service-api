package lucas.personal.realtime_chat.auth_service_api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

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
