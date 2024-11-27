package com.drawit.drawit.repository;

import com.drawit.drawit.entity.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    // 사용자나 아이템으로 구매 내역을 조회하는 메서드를 선언할 수 있습니다.
}
