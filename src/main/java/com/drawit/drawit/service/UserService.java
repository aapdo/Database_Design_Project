package com.drawit.drawit.service;

import com.drawit.drawit.dto.UserDto;
import com.drawit.drawit.entity.CustomUserDetails;
import com.drawit.drawit.entity.Item;
import com.drawit.drawit.entity.Purchase;
import com.drawit.drawit.entity.User;
import com.drawit.drawit.repository.ItemRepository;
import com.drawit.drawit.repository.PurchaseRepository;
import com.drawit.drawit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {


    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final PurchaseRepository purchaseRepository;

    // 로그인 ID 중복 체크
    @Transactional
    public boolean isLoginIdDuplicate(String loginId) {
        return userRepository.existsByLoginId(loginId);
    }

    // 닉네임 중복 체크
    @Transactional
    public boolean isNicknameDuplicate(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    // 회원가입 시 사용자 저장
    public UserDto register(User user) {
        User savedUser = userRepository.save(user);
        List<Item> initialItems = itemRepository.findAllById(List.of(1L, 2L));
        List<Purchase> purchases = initialItems.stream()
                .map(item -> Purchase.builder()
                        .user(savedUser)
                        .item(item)
                        .purchasedAt(LocalDateTime.now())
                        .used(true)
                        .build())
                .toList();

        purchaseRepository.saveAll(purchases);
        return new UserDto(savedUser);
    }

    // 로그인 아이디로 사용자 로드
    @Transactional
    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<GrantedAuthority> authorities = Arrays.stream(user.getRoles().split(","))
                //.map(role -> new SimpleGrantedAuthority("ROLE_" + role.trim().toUpperCase()))
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.trim()))
                .collect(Collectors.toList());

        return new CustomUserDetails(user.getId(), user.getNickname(), user.getLoginId(), user.getPassword(), authorities);
    }

    @Transactional
    public UserDto getUserByLoginId(String loginId) throws UsernameNotFoundException {
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        log.info("get user by login id");
        return new UserDto(user);
    }

    @Transactional
    public UserDto getUserByNickname(String nickname) throws UsernameNotFoundException {
        User user = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new UserDto(user);
    }

    @Transactional
    public List<UserDto> getUserList(){
        List<User> users = userRepository.findAll();
        List<UserDto> userDtoList = new ArrayList<>();
        for (User user: users) {
            userDtoList.add(new UserDto(user));
        }

        return userDtoList;
    }

    @Transactional
    public UserDto updateUserNickName(Long userId, String newNickname) throws UsernameNotFoundException {
        User user = userRepository.findByIdForUpdate(userId.toString())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + userId));
        user.setNickname(newNickname);
        userRepository.save(user);
        return new UserDto(user);
    }

    @Transactional
    public void changeActiveItem(Long userId, Long itemId) {
        // 1. 유저 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        // 2. 아이템 확인
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found with ID: " + itemId));
        String target = item.getTarget();

        // 4. 기존 사용 중인 아이템을 모두 `used = false`로 업데이트
        List<Purchase> userPurchases = purchaseRepository.findPurchaseByUserId(userId);
        for (Purchase purchase : userPurchases) {
            purchase.setUsed(false);
        }

        Purchase purchaseToActivate = purchaseRepository.findByUserIdAndItemId(userId, itemId)
                .orElseThrow(() -> new IllegalArgumentException("User has not purchased this item."));
        purchaseToActivate.setUsed(true);


        // 3. 아이템을 활성화 (닉네임 색상 또는 채팅 색상 변경)
        if ("nickname".equals(target)) {
            user.setNicknameColor(item.getColor());
        } else if ("chatting".equals(target)) {
            user.setChattingColor(item.getColor());
        } else {
            throw new IllegalArgumentException("Invalid target specified: " + target);
        }

        // 5. 변경 사항 저장
        userRepository.save(user);
        purchaseRepository.saveAll(userPurchases); // 기존 아이템 저장
        purchaseRepository.save(purchaseToActivate);
    }
}
