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

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p.item FROM Purchase p WHERE p.user.id = :userId")
    List<Item> findItemsByUserId(@Param("userId") Long userId);
}
