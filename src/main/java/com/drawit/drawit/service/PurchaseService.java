package com.drawit.drawit.service;

import com.drawit.drawit.dto.ItemDto;
import com.drawit.drawit.entity.Item;
import com.drawit.drawit.entity.Purchase;
import com.drawit.drawit.entity.User;
import com.drawit.drawit.repository.ItemRepository;
import com.drawit.drawit.repository.PurchaseRepository;
import com.drawit.drawit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PurchaseService {
    private final PurchaseRepository purchaseRepository;
    private final ItemService itemService;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Transactional
    public List<ItemDto> getItemsBytUserId(Long userId) {
        List<Item> itemList = purchaseRepository.findItemsByUserId(userId);
        List<ItemDto> itemDtoList = new ArrayList<>();
        for (Item item : itemList) {
            itemDtoList.add(itemService.itemToItemDto(item));
        }
        return itemDtoList;
    }

    @Transactional
    public void buyItem(Long userId, Long itemId) {
        // 1. 유저 확인

        log.info("find user");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        // 2. 아이템 확인
        log.info("find item");
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found with ID: " + itemId));

        // 3. 아이템 구매 가능한지 확인
        log.info("certification");
        if (user.getCurrentPoints() < item.getCost()) {
            throw new IllegalStateException("Not enough points to buy this item.");
        }

        // 4. 이미 구매했는지 확인
        log.info("already");
        boolean alreadyPurchased = purchaseRepository.existsByUserIdAndItemId(userId, itemId);
        if (alreadyPurchased) {
            throw new IllegalArgumentException("Item already purchased.");
        }

        // 5. `Purchase` 생성 및 저장
        log.info("make purchase");
        Purchase purchase = Purchase.builder()
                .user(user)
                .item(item)
                .purchasedAt(LocalDateTime.now())
                .used(false) // 새로 구매한 아이템은 기본적으로 사용 중이 아님
                .build();
        purchaseRepository.save(purchase);

        // 6. 유저의 포인트 차감
        user.setCurrentPoints(user.getCurrentPoints() - item.getCost());
        userRepository.save(user);
    }
}
