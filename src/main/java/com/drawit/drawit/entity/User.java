package com.drawit.drawit.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "User")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String loginId;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nickname;

    @Column(name = "total_points", nullable = false)
    private Integer totalPoints;

    @Column(name = "current_points", nullable = false)
    private Integer currentPoints;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "nickname_color", nullable = false)
    private String nicknameColor;

    @Column(name = "chatting_color", nullable = false)
    private String chattingColor;

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
}
