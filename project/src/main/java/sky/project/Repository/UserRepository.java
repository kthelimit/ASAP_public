package sky.project.Repository;

import sky.project.Entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    // userId와 password가 일치하는 유저를 찾는 메소드
    Optional<User> findByUserIdAndPassword(String userId, String password);
    boolean existsById(String userId); // 아이디 존재 확인 메소드 추가
    Optional<User> findByUserId(String userId); // 아이디로 사용자 찾기
    @Modifying
    @Query("UPDATE User u SET u.userType = :userType WHERE u.userId = :userId")
    void updateUserByUserId(@Param("userId") String userId, @Param("userType") String userType);
    // 아이디에서 검색어와 일치하는 사용자 찾기
    Page<User> findByUserIdContainingIgnoreCase(String userId, Pageable pageable);

}