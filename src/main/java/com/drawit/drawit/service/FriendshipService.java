package com.drawit.drawit.service;

import com.drawit.drawit.dto.FriendDto;
import com.drawit.drawit.dto.ItemDto;
import com.drawit.drawit.dto.response.ResponseFriendshipDto;
import com.drawit.drawit.dto.response.ResponseUserDto;
import com.drawit.drawit.entity.Friendship;
import com.drawit.drawit.entity.Item;
import com.drawit.drawit.entity.User;
import com.drawit.drawit.repository.FriendshipRepository;
import com.drawit.drawit.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
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
public class FriendshipService {
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final FriendshipRepository friendshipRepository;

    /**
     * 친구 요청을 추가
     */
    @Transactional
    public FriendDto addFriend(String senderNickname, String recipientNickname) {
        log.info("senderNickname: " + senderNickname);
        log.info("receiverNickname: " + recipientNickname);
        User sender = userRepository.findByNickname(senderNickname)
                .orElseThrow(() -> new IllegalArgumentException("Sender not found with ID: " + senderNickname));
        log.info("sender: " + sender.getNickname());
        User recipient = userRepository.findByNickname(recipientNickname)
                .orElseThrow(() -> new IllegalArgumentException("Recipient not found with ID: " + recipientNickname));
        log.info("receiver: " + recipient.getNickname() );

        Friendship friendship = new Friendship();
        friendship.setStatus(Friendship.AcceptStatus.WAIT);
        friendship.setUser(sender);
        friendship.setFriend(recipient);
        friendship.setCreatedAt(LocalDateTime.now());

        friendshipRepository.save(friendship);

        return FriendDto.builder()
                .id(friendship.getId())
                .status(Friendship.AcceptStatus.WAIT)
                .receiverId(friendship.getFriend().getId())
                .senderId(friendship.getUser().getId())
                .build();
    }
    /**
     * 친구 요청에 대한 응답 처리
     */
    @Transactional
    public void respondToFriendRequest(Long requestId, Friendship.AcceptStatus status) {
        Friendship friendship = friendshipRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Friendship request not found with ID: " + requestId));

        friendship.setStatus(status);
        friendshipRepository.save(friendship);
    }

    /**
     * 대기 중인 친구 요청 조회
     */
    @Transactional(readOnly = true)
    public List<ResponseFriendshipDto> getPendingRequests(String receiverNickname) {
        List<ResponseFriendshipDto> friendDtoList = new ArrayList<>();
        for (Friendship friendship : friendshipRepository.findByFriendNicknameAndStatus(receiverNickname, Friendship.AcceptStatus.WAIT)) {
            friendDtoList.add(ResponseFriendshipDto.builder()
                            .friendshipId(friendship.getId())
                            .status(friendship.getStatus())
                            .receiverId(friendship.getFriend().getId())
                            .senderNickname(friendship.getUser().getNickname())
                            .receiverNickname(friendship.getFriend().getNickname())
                    .build());
        }
        return friendDtoList;
    }


    public ResponseUserDto userToResponseUserDto(User user) {
        return objectMapper.convertValue(user, ResponseUserDto.class);
    }

}
