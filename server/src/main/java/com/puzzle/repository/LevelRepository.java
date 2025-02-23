package com.puzzle.repository;

import com.puzzle.model.Level;
import com.puzzle.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LevelRepository extends JpaRepository<Level, Long> {
    List<Level> findByUserAndLevelNumber(User user, int levelNumber);
    List<Level> findByUser(User user);
}
