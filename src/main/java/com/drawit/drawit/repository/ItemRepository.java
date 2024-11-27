package com.drawit.drawit.repository;

import com.drawit.drawit.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    // 추가적인 사용자 정의 메서드를 선언할 수 있습니다.
}
