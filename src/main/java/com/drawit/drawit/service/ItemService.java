package com.drawit.drawit.service;

import com.drawit.drawit.entity.Item;
import com.drawit.drawit.repository.ItemRepository;
import com.drawit.drawit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public List<Item> getItemList() {
        return itemRepository.findAll();
    }
}
