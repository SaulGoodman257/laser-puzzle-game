package com.puzzle.controller;

import com.puzzle.dto.LevelDto;
import com.puzzle.service.LevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/levels")
public class LevelController {
    private final LevelService levelService;
    @Autowired
    public LevelController(LevelService levelService) {
        this.levelService = levelService;
    }
    @PostMapping("/save/{username}")
    public ResponseEntity<String> saveLevel(@PathVariable String username, @RequestBody LevelDto levelDto) {
        levelService.saveLevel(username, levelDto);
        return ResponseEntity.ok("Level saved successfully");
    }
    @GetMapping("/{username}")
    public ResponseEntity<List<LevelDto>> getLevels(@PathVariable String username) {
        List<LevelDto> levels = levelService.getLevelsByUser(username);
        return ResponseEntity.ok(levels);
    }
    @GetMapping("/{username}/{levelNumber}")
    public ResponseEntity<LevelDto> getLevel(@PathVariable String username, @PathVariable int levelNumber) {
        LevelDto level = levelService.getLevelByUserAndNumber(username, levelNumber);
        if (level != null) {
            return ResponseEntity.ok(level);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

