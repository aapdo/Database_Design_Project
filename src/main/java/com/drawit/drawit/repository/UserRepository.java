package com.drawit.drawit.repository;

import com.drawit.drawit.entity.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<User> findByLoginId(String loginId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<User> findByNickname(String nickname);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    boolean existsByLoginId(String loginId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    boolean existsByNickname(String nickname);

    // 비관적 락, 업데이트 도중 조회하지 못함.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT u FROM User u WHERE u.id = :userId")
    Optional<User> findByIdForUpdate(@Param("userId") String userId);
    @Modifying
    @Query("UPDATE User u SET u.nickname = :nickname WHERE u.id = :userId")
    int updateNicknameById(@Param("userId") String userId, @Param("nickname") String nickname);
}
