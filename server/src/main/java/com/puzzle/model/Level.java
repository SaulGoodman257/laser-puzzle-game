package com.puzzle.model;

import jakarta.persistence.*;

@Entity
@Table(name = "levels")
public class Level {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "level_number", nullable = false)
    private int levelNumber;

    @Column(name = "level_data", columnDefinition = "TEXT", nullable = false)
    private String levelData;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Level() {}

    public Level(int levelNumber, String levelData, User user) {
        this.levelNumber = levelNumber;
        this.levelData = levelData;
        this.user = user;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getLevelNumber() {
        return levelNumber;
    }

    public void setLevelNumber(int levelNumber) {
        this.levelNumber = levelNumber;
    }

    public String getLevelData() {
        return levelData;
    }

    public void setLevelData(String levelData) {
        this.levelData = levelData;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
