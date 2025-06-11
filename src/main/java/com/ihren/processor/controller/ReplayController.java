package com.ihren.processor.controller;

import com.ihren.processor.service.ReplayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/replay")
@RequiredArgsConstructor
public class ReplayController {

    private final ReplayService replayService;

    @PostMapping("/all")
    public ResponseEntity<Integer> replayAll() {
        return ResponseEntity.ok(replayService.replayAll());
    }
}
