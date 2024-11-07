package sky.project.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sky.project.Entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUserId(String userId);
    boolean existsById(String userId);

}