package com.drawit.drawit.repository;

import com.drawit.drawit.entity.Friendship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    List<Friendship> findAllByFriendIdAndStatus(Long friendId, Friendship.AcceptStatus status);

}
