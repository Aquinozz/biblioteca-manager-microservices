package auth_service.users.repository;

import auth_service.users.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface UserRepository extends JpaRepository<Users, Long> {
    Optional <Users> findByEmail(String email);

    boolean existsByEmail(String email);
}
