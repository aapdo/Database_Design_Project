package com.drawit.drawit.repository;

import com.drawit.drawit.entity.Item;
import com.drawit.drawit.entity.Purchase;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p.item FROM Purchase p WHERE p.user.id = :userId")
    List<Item> findItemsByUserId(@Param("userId") Long userId);

    // 유저의 특정 타겟에 해당하는 모든 Purchase 조회
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Purchase p JOIN p.item i WHERE p.user.id = :userId")
    List<Purchase> findPurchaseByUserId(@Param("userId") Long userId);

    // 유저와 특정 아이템에 해당하는 Purchase 조회
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Purchase p WHERE p.user.id = :userId AND p.item.id = :itemId")
    Optional<Purchase> findByUserIdAndItemId(@Param("userId") Long userId, @Param("itemId") Long itemId);
}
