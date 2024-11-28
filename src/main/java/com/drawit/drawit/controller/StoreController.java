package com.drawit.drawit.controller;

import com.drawit.drawit.dto.ItemDto;
import com.drawit.drawit.dto.request.RequestRegisterDto;
import com.drawit.drawit.service.ItemService;
import com.drawit.drawit.service.PurchaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@Slf4j
public class StoreController {

    private final ItemService itemService;
    private final PurchaseService purchaseService;

    @GetMapping("/items")
    public ResponseEntity<?> getItems() {
        return ResponseEntity.ok(itemService.getItemList());
    }

    @GetMapping("/items/myItems")
    public ResponseEntity<?> getMyItems() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<ItemDto> itemDtoList = purchaseService.getItemsBytUserId(Long.parseLong(authentication.getName()));
        return ResponseEntity.ok(itemDtoList);
    }

    @PostMapping("/items/use")
    public ResponseEntity<?> useMyItem(@Valid @RequestBody Long itemId) {
        return ResponseEntity.ok(" ");
    }

    @PostMapping("/purchase")
    public ResponseEntity<?> buyItem(@Valid @RequestBody Long itemId) {
        return ResponseEntity.ok(" ");
    }
}
