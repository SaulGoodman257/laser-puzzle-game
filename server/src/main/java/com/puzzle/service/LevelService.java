package com.puzzle.service;

import com.puzzle.dto.LevelDto;
import com.puzzle.model.Level;
import com.puzzle.model.User;
import com.puzzle.repository.LevelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LevelService {
    private final LevelRepository levelRepository;
    private final UserService userService;
    @Autowired
    public LevelService(LevelRepository levelRepository, UserService userService) {
        this.levelRepository = levelRepository;
        this.userService = userService;
    }
    @Transactional
    public void saveLevel(String username, LevelDto levelDto) {
        User user = userService.findByUsername(username);
        List<Level> existingLevels = levelRepository.findByUserAndLevelNumber(user, levelDto.getLevelNumber());
        if (!existingLevels.isEmpty()) {
            Level existingLevel = existingLevels.get(0);
            existingLevel.setLevelData(levelDto.getLevelData());
            levelRepository.save(existingLevel);
        } else {
            Level newLevel = new Level(levelDto.getLevelNumber(), levelDto.getLevelData(), user);
            levelRepository.save(newLevel);
        }
    }
    public List<LevelDto> getLevelsByUser(String username) {
        User user = userService.findByUsername(username);
        List<Level> levels = levelRepository.findByUser(user);
        return levels.stream()
            .map(level -> new LevelDto(level.getLevelNumber(), level.getLevelData()))
            .collect(Collectors.toList());
    }
    public LevelDto getLevelByUserAndNumber(String username, int levelNumber) {
        User user = userService.findByUsername(username);
        List<Level> levels = levelRepository.findByUserAndLevelNumber(user, levelNumber);
        if (!levels.isEmpty()) {
            Level level = levels.get(0);
            return new LevelDto(level.getLevelNumber(), level.getLevelData());
        }
        else {
            return  null;
        }
    }
}

