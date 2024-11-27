package com.drawit.drawit.repository;

import com.drawit.drawit.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    // 게시물이나 사용자로 댓글을 조회하는 메서드를 선언할 수 있습니다.
}
