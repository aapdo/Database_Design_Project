package com.drawit.drawit.repository;

import com.drawit.drawit.entity.Friendship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    // 쿼리 메서드 정의
    List<Friendship> findByFriendNicknameAndStatus(String nickname, Friendship.AcceptStatus status);

    // JPQL 쿼리 사용 예제
    @Query("SELECT f FROM Friendship f WHERE f.friend.nickname = :nickname AND f.status = :status")
    List<Friendship> findFriendshipsByNicknameAndStatus(
            @Param("nickname") String nickname,
            @Param("status") Friendship.AcceptStatus status);

    @Query("SELECT f.friend.id FROM Friendship f WHERE f.user.id = :userId AND f.status = 'ACCEPT' " +
            "UNION SELECT f.user.id FROM Friendship f WHERE f.friend.id = :userId AND f.status = 'ACCEPT'")
    List<Long> findFriendsByUserId(@Param("userId") Long userId);

}
