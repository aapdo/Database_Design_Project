package com.drawit.drawit.configuration;

import com.drawit.drawit.entity.Item;
import com.drawit.drawit.repository.ItemRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

//@Component
public class ItemInitializer implements CommandLineRunner {

    private final ItemRepository itemRepository;

    public ItemInitializer(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        List<Item> items = List.of(
                Item.builder().name("닉네임 색상 변경권").description("내 닉네임을 검은색으로 바꿀 수 있어요!").target("nickname").color("black").cost(0).purchases(new ArrayList<>()).build(),
                Item.builder().name("채팅 색상 변경권").description("내 채팅을 검은색으로 바꿀 수 있어요!").target("chatting").color("black").cost(0).purchases(new ArrayList<>()).build(),
                Item.builder().name("닉네임 색상 변경권").description("내 닉네임을 빨간색으로 바꿀 수 있어요!").target("nickname").color("red").cost(100).purchases(new ArrayList<>()).build(),
                Item.builder().name("닉네임 색상 변경권").description("내 닉네임을 주황색으로 바꿀 수 있어요!").target("nickname").color("orange").cost(100).purchases(new ArrayList<>()).build(),
                Item.builder().name("닉네임 색상 변경권").description("내 닉네임을 노란색으로 바꿀 수 있어요!").target("nickname").color("yellow").cost(100).purchases(new ArrayList<>()).build(),
                Item.builder().name("닉네임 색상 변경권").description("내 닉네임을 초록색으로 바꿀 수 있어요!").target("nickname").color("green").cost(100).purchases(new ArrayList<>()).build(),
                Item.builder().name("닉네임 색상 변경권").description("내 닉네임을 파란색으로 바꿀 수 있어요!").target("nickname").color("blue").cost(100).purchases(new ArrayList<>()).build(),
                Item.builder().name("닉네임 색상 변경권").description("내 닉네임을 남색으로 바꿀 수 있어요!").target("nickname").color("indigo").cost(100).purchases(new ArrayList<>()).build(),
                Item.builder().name("닉네임 색상 변경권").description("내 닉네임을 보라색으로 바꿀 수 있어요!").target("nickname").color("violet").cost(100).purchases(new ArrayList<>()).build(),
                Item.builder().name("닉네임 색상 변경권").description("내 닉네임을 핑크색으로 바꿀 수 있어요!").target("nickname").color("pink").cost(100).purchases(new ArrayList<>()).build(),
                Item.builder().name("채팅 색상 변경권").description("내 채팅을 빨간색으로 바꿀 수 있어요!").target("chatting").color("red").cost(100).purchases(new ArrayList<>()).build(),
                Item.builder().name("채팅 색상 변경권").description("내 채팅을 주황색으로 바꿀 수 있어요!").target("chatting").color("orange").cost(100).purchases(new ArrayList<>()).build(),
                Item.builder().name("채팅 색상 변경권").description("내 채팅을 노란색으로 바꿀 수 있어요!").target("chatting").color("yellow").cost(100).purchases(new ArrayList<>()).build(),
                Item.builder().name("채팅 색상 변경권").description("내 채팅을 초록색으로 바꿀 수 있어요!").target("chatting").color("green").cost(100).purchases(new ArrayList<>()).build(),
                Item.builder().name("채팅 색상 변경권").description("내 채팅을 파란색으로 바꿀 수 있어요!").target("chatting").color("blue").cost(100).purchases(new ArrayList<>()).build(),
                Item.builder().name("채팅 색상 변경권").description("내 채팅을 남색으로 바꿀 수 있어요!").target("chatting").color("indigo").cost(100).purchases(new ArrayList<>()).build(),
                Item.builder().name("채팅 색상 변경권").description("내 채팅을 보라색으로 바꿀 수 있어요!").target("chatting").color("violet").cost(100).purchases(new ArrayList<>()).build(),
                Item.builder().name("채팅 색상 변경권").description("내 채팅을 핑크색으로 바꿀 수 있어요!").target("chatting").color("pink").cost(100).purchases(new ArrayList<>()).build()
        );

        itemRepository.saveAll(items);
    }
}
