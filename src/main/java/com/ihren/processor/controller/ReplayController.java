package com.ihren.processor.controller;

import com.ihren.processor.service.ReplayService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class ReplayController {

    private final ReplayService replayService;

    @PostMapping("/replay")
    public void replay() {
        replayService.replayAll();
    }
}
