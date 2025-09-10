package com.ihren.processor.controller;

import com.ihren.processor.service.TransactionReplayService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class ReplayController {

    private final TransactionReplayService transactionReplayService;

    @PostMapping("/replay")
    public void replay() {
        transactionReplayService.replay();
    }
}
