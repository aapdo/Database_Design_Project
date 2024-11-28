package com.drawit.drawit.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Getter
@Setter
public class CustomUserDetails implements UserDetails {
    private Long userId;
    private String nickname;
    private String loginId;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(Long userId, String nickname, String loginId, String password, Collection<? extends GrantedAuthority> authorities) {
        this.userId = userId;
        this.nickname = nickname;
        this.loginId = loginId;
        this.password = password;
        this.authorities = authorities;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return userId.toString();
    }

    @Override
    public String toString() {
        return "userId=" + userId + ", nickname='" + nickname  + ", loginId='" + loginId;
    }
}
