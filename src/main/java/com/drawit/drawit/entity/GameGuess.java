package com.drawit.drawit.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

@Data
@NoArgsConstructor
@Entity
@Table(name = "GameGuess")
public class GameGuess {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "round_id", nullable = false)
    private GameRound gameRound;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant_id", nullable = false)
    private GameParticipant participant;

    @Column(name = "guessed_word")
    private String guessedWord;

    @Column(name = "similarity")
    private Float similarity;

    @Column(name = "points_earned")
    private Integer pointsEarned;
}
