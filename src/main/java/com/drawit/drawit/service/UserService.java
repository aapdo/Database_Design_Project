package com.drawit.drawit.service;

import com.drawit.drawit.entity.CustomUserDetails;
import com.drawit.drawit.entity.User;
import com.drawit.drawit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    // 로그인 ID 중복 체크
    public boolean isLoginIdDuplicate(String loginId) {
        return userRepository.existsByLoginId(loginId);
    }

    // 닉네임 중복 체크
    public boolean isNicknameDuplicate(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    // 회원가입 시 사용자 저장
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    // 로그인 아이디로 사용자 로드
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

    public Optional<User> getUserByLoginId(String loginId) throws UsernameNotFoundException {
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return Optional.ofNullable(user);
    }

    public User getUserByNickname(String nickname) throws UsernameNotFoundException {
        User user = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return user;
    }

    public List<User> getUserList(){
        return userRepository.findAll();
    }

    @Transactional
    public User updateUserNickName(Long userId, String newNickname) throws UsernameNotFoundException {
        User user = userRepository.findByIdForUpdate(userId.toString())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + userId));
        user.setNickname(newNickname);
        userRepository.save(user);
        return user;
    }
}
