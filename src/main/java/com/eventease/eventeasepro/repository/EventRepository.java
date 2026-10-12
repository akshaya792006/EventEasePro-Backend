package com.eventease.eventeasepro.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eventease.eventeasepro.entity.Event;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByDepartmentIgnoreCase(String department);
}