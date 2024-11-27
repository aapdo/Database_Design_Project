package com.drawit.drawit.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "GameRound")
public class GameRound {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "game_room_id", nullable = false)
    private GameRoom gameRoom;

    @ManyToOne
    @JoinColumn(name = "drawer_id", nullable = false)
    private User drawer;

    @Column(name = "round_number", nullable = false)
    private Integer roundNumber;

    @Column(name = "correct_word", nullable = false)
    private String correctWord;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    @OneToMany(mappedBy = "gameRound")
    private List<GameGuess> guesses;

    @OneToOne(mappedBy = "gameRound")
    private Post post;
}