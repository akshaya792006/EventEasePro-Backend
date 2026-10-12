package com.eventease.eventeasepro.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eventease.eventeasepro.entity.QrToken;
import com.eventease.eventeasepro.service.QrCodeService;
import com.eventease.eventeasepro.service.QrTokenService;

@RestController
@RequestMapping("/api/qr")
@CrossOrigin(origins = "http://localhost:5173")
public class QrController {

    private final QrCodeService qrCodeService;
    private final QrTokenService qrTokenService;

    public QrController(
            QrCodeService qrCodeService,
            QrTokenService qrTokenService) {

        this.qrCodeService = qrCodeService;
        this.qrTokenService = qrTokenService;
    }

    // Generate QR code for a specific event
    @PostMapping("/events/{eventId}/generate")
    public ResponseEntity<Map<String, Object>> generateQr(
            @PathVariable("eventId") Long eventId) {

        QrToken qrToken =
                qrTokenService.generateTokenForEvent(eventId);

        String qrContent = qrToken.getToken();

        String qrImage =
                qrCodeService.generateQrCodeBase64(qrContent);

        return ResponseEntity.ok(Map.of(
                "eventId", qrToken.getEventId(),
                "token", qrToken.getToken(),
                "expiresAt", qrToken.getExpiresAt().toString(),
                "validitySeconds",
                qrTokenService.getValiditySeconds(),
                "qrImageBase64", qrImage
        ));
    }

    // Validate QR token for a specific event
    @GetMapping("/events/{eventId}/validate")
    public ResponseEntity<Map<String, Object>> validateQr(
            @PathVariable("eventId") Long eventId,
            @RequestParam(name = "token") String token) {

        boolean valid =
                qrTokenService.validateToken(eventId, token);

        return ResponseEntity.ok(Map.of(
                "eventId", eventId,
                "valid", valid
        ));
    }
}