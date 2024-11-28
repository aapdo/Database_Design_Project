package com.drawit.drawit.entity;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "User")
@Builder
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String roles;

    @Column(nullable = false)
    private String loginId;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nickname;

    @Column(name = "total_points", nullable = false)
    private Integer totalPoints = 0;

    @Column(name = "current_points", nullable = false)
    private Integer currentPoints = 0;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "nickname_color", nullable = false)
    private String nicknameColor = "white";

    @Column(name = "chatting_color", nullable = false)
    private String chattingColor = "white";

    // 관계 매핑
    @OneToMany(mappedBy = "user")
    private List<Friendship> friendships;

    @OneToMany(mappedBy = "friend")
    private List<Friendship> friendsOf;

    @OneToMany(mappedBy = "host")
    private List<GameRoom> hostedGameRooms;

    @OneToMany(mappedBy = "user")
    private List<Purchase> purchases;

    @OneToMany(mappedBy = "user")
    private List<Post> posts;

    @OneToMany(mappedBy = "user")
    private List<GameParticipant> gameParticipants;

    @OneToMany(mappedBy = "user")
    private List<Comment> comments;

    @OneToMany(mappedBy = "drawer")
    private List<GameRound> drawnRounds;

    public static class UserBuilder {
        private String roles = "user";
        private Integer totalPoints = 0;
        private Integer currentPoints = 0;
        private LocalDateTime createdAt = LocalDateTime.now();
        private String nicknameColor = "white";
        private String chattingColor = "white";
    }
}
