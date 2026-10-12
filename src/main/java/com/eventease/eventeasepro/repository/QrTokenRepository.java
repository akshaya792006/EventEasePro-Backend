package com.eventease.eventeasepro.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eventease.eventeasepro.entity.QrToken;

@Repository
public interface QrTokenRepository extends JpaRepository<QrToken, Long> {

    Optional<QrToken> findByToken(String token);

    Optional<QrToken> findFirstByEventIdOrderByIdDesc(Long eventId);

    void deleteByEventId(Long eventId);
}