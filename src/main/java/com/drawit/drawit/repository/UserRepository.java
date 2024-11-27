package com.drawit.drawit.repository;

import com.drawit.drawit.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByLoginId(String loginId);

    Optional<User> findByNickname(String nickname);

    boolean existsByLoginId(String loginId);

    boolean existsByNickname(String nickname);
}
