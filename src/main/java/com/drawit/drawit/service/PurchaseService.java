package com.drawit.drawit.service;

import com.drawit.drawit.dto.ItemDto;
import com.drawit.drawit.entity.Item;
import com.drawit.drawit.repository.PurchaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PurchaseService {
    private final PurchaseRepository purchaseRepository;
    private final ItemService itemService;

    @Transactional
    public List<ItemDto> getItemsBytUserId(Long userId) {
        List<Item> itemList = purchaseRepository.findItemsByUserId(userId);
        List<ItemDto> itemDtoList = new ArrayList<>();
        for (Item item : itemList) {
            itemDtoList.add(itemService.itemToItemDto(item));
        }
        return itemDtoList;
    }
}
