package com.drawit.drawit.service;

import com.drawit.drawit.dto.ItemDto;
import com.drawit.drawit.entity.Item;
import com.drawit.drawit.repository.ItemRepository;
import com.drawit.drawit.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    public List<ItemDto> getItemList() {
        List<Item> itemList = itemRepository.findAll();
        List<ItemDto> itemDtoList = new ArrayList<>();
        for (Item item: itemList) {
            itemDtoList.add(itemToItemDto(item));
        }
        return itemDtoList;
    }

    public ItemDto itemToItemDto(Item item) {
        return objectMapper.convertValue(item, ItemDto.class);
    }
}
