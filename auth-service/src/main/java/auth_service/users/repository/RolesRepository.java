package auth_service.users.repository;

import auth_service.users.models.RolesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RolesRepository extends JpaRepository<RolesEntity, Long> {

    Optional<RolesEntity> findByNome(String nome);

    boolean existsByNome(String nome);
}