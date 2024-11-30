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

    // 별도의 쿼리 메서드로 유사 기능 제공
    @Query("SELECT f.friend.id FROM Friendship f WHERE f.user.id = :userId AND f.status = com.drawit.drawit.entity.Friendship$AcceptStatus.ACCEPT")
    List<Long> findFriendsByUserIdAsFriend(@Param("userId") Long userId);

    @Query("SELECT f.user.id FROM Friendship f WHERE f.friend.id = :userId AND f.status = com.drawit.drawit.entity.Friendship$AcceptStatus.ACCEPT")
    List<Long> findFriendsByUserIdAsUser(@Param("userId") Long userId);
}
