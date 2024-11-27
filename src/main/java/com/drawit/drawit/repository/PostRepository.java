package com.drawit.drawit.repository;

import com.drawit.drawit.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    // 사용자나 게임 라운드로 게시물을 조회하는 메서드를 선언할 수 있습니다.
}
