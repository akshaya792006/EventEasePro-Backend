package com.eventease.eventeasepro.service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eventease.eventeasepro.entity.Event;
import com.eventease.eventeasepro.entity.QrToken;
import com.eventease.eventeasepro.repository.EventRepository;
import com.eventease.eventeasepro.repository.QrTokenRepository;

@Service
public class QrTokenService {

    private static final long QR_VALIDITY_SECONDS = 300;

    private final SecureRandom secureRandom = new SecureRandom();

    private final QrTokenRepository qrTokenRepository;
    private final EventRepository eventRepository;

    public QrTokenService(
            QrTokenRepository qrTokenRepository,
            EventRepository eventRepository) {

        this.qrTokenRepository = qrTokenRepository;
        this.eventRepository = eventRepository;
    }

    // Generate a secure random token
    public String generateToken() {

        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);
    }

    // QR token expires after 5 minutes
    public Instant getExpiryTime() {

        return Instant.now().plusSeconds(QR_VALIDITY_SECONDS);
    }

    // Check whether the token has expired
    public boolean isExpired(Instant expiryTime) {

        return expiryTime == null
                || !Instant.now().isBefore(expiryTime);
    }

    public long getValiditySeconds() {

        return QR_VALIDITY_SECONDS;
    }

    // Generate and save a token for a specific event
    @Transactional
    public QrToken generateTokenForEvent(Long eventId) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Event not found: " + eventId));

        // Delete the previous token for this event
        qrTokenRepository.deleteByEventId(eventId);
        qrTokenRepository.flush();

        // Create a new token
        QrToken qrToken = new QrToken(
                event.getId(),
                generateToken(),
                getExpiryTime()
        );

        // Save the new token
        return qrTokenRepository.save(qrToken);
    }

    // Validate token, event ID and expiry time
    @Transactional(readOnly = true)
    public boolean validateToken(Long eventId, String token) {

        System.out.println("DEBUG: Request Event ID = " + eventId);

        System.out.println("DEBUG: Received Token Length = "
                + (token == null ? 0 : token.length()));

        if (token == null || token.isBlank()) {

            System.out.println("DEBUG: Token is empty");
            return false;
        }

        // Search for the token in the database
        Optional<QrToken> result =
                qrTokenRepository.findByToken(token.trim());

        if (result.isEmpty()) {

            System.out.println(
                    "DEBUG: Token not found in database");

            Optional<QrToken> latest =
                    qrTokenRepository
                            .findFirstByEventIdOrderByIdDesc(eventId);

            if (latest.isPresent()) {

                QrToken latestToken = latest.get();

                System.out.println(
                        "DEBUG: Database Token Length = "
                                + latestToken.getToken().length());

                System.out.println(
                        "DEBUG: Database Event ID = "
                                + latestToken.getEventId());
            }

            return false;
        }

        QrToken qr = result.get();

        System.out.println(
                "DEBUG: Database Event ID = " + qr.getEventId());

        System.out.println(
                "DEBUG: Expiry = " + qr.getExpiresAt());

        boolean expired = isExpired(qr.getExpiresAt());

        System.out.println("DEBUG: Expired = " + expired);

        boolean sameEvent = qr.getEventId().equals(eventId);

        System.out.println("DEBUG: Same Event = " + sameEvent);

        return sameEvent && !expired;
    }
}