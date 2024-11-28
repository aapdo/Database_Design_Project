package com.drawit.drawit.service;

import com.drawit.drawit.dto.ItemDto;
import com.drawit.drawit.repository.PurchaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PurchaseService {
    private final PurchaseRepository purchaseRepository;
    private final ItemService itemService;
    public List<ItemDto> getItemsBytUserId(Long userId) {
        purchaseRepository.findItemsByUserId(userId);
    }
}
