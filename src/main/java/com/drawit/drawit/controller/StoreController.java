package com.drawit.drawit.controller;

import com.drawit.drawit.dto.request.RequestRegisterDto;
import com.drawit.drawit.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StoreController {

    private final ItemService itemService;

    @GetMapping("/items")
    public ResponseEntity<?> getItems() {
        return ResponseEntity.ok(itemService.getItemList());
    }

    @GetMapping("/items/myItems")
    public ResponseEntity<?> getMyItems() {
        return ResponseEntity.ok(" ");
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
