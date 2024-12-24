package sky.project.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sky.project.DTO.UserDTO;
import sky.project.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import sky.project.Entity.UserType;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUserId(String userId);

    boolean existsById(String userId);

    Page<User> findByUserIdContaining(String keyword, Pageable pageable);

    Page<User> findByUserTypeNot(UserType userType, Pageable pageable);
}