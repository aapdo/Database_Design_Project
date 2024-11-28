package com.drawit.drawit.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "GameParticipant")
public class GameParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_room_id", nullable = false)
    private GameRoom gameRoom;

    @Column(name = "points_earned", nullable = false)
    private Integer pointsEarned;

    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt;

    @OneToMany(mappedBy = "participant", fetch = FetchType.LAZY)
    private List<GameGuess> guesses;
}
