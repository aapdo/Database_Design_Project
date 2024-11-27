package com.drawit.drawit.repository;

import com.drawit.drawit.entity.Friendship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    // 사용자 ID로 친구 관계를 조회하는 메서드 등을 선언할 수 있습니다.
}
