package com.enviro.assessment.junior.twisisanikhosa.controller;

import com.enviro.assessment.junior.twisisanikhosa.dto.WithdrawalRequestDto;
import com.enviro.assessment.junior.twisisanikhosa.dto.WithdrawalResponseDto;
import com.enviro.assessment.junior.twisisanikhosa.service.WithdrawalService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/withdrawals")
public class WithdrawalController {

    private final WithdrawalService withdrawalService;

    public WithdrawalController(WithdrawalService withdrawalService) {
        this.withdrawalService = withdrawalService;
    }

    @PostMapping
    public ResponseEntity<WithdrawalResponseDto> createWithdrawal(
            @Valid @RequestBody WithdrawalRequestDto requestDto) {
        WithdrawalResponseDto response = withdrawalService.createWithdrawalNotice(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<WithdrawalResponseDto>> getWithdrawals(
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(withdrawalService.getWithdrawalNotices(productId, startDate, endDate));
    }

    @GetMapping("/export/csv")
    public void exportCsv(
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            HttpServletResponse response) throws IOException {

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"enviro365_withdrawal_statement.csv\"");
        withdrawalService.exportWithdrawalsCsv(productId, startDate, endDate, response.getWriter());
    }
}