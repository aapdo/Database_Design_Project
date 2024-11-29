package com.drawit.drawit.controller;

import com.drawit.drawit.dto.ItemDto;
import com.drawit.drawit.dto.request.RequestRegisterDto;
import com.drawit.drawit.service.ItemService;
import com.drawit.drawit.service.PurchaseService;
import com.drawit.drawit.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
    private final UserService userService;

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
        Long userId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());

        try {
            userService.changeActiveItem(userId, itemId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

        return ResponseEntity.ok("Active item updated successfully.");
    }

    @PostMapping("/purchase")
    public ResponseEntity<?> buyItem(@Valid @RequestBody Long itemId) {
        return ResponseEntity.ok(" ");
    }
}
