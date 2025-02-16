package com.example.repository;

import com.example.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
    List<Reservation> findByUserId(int userId);
    List<Reservation> findBySpaceIdAndDate(int spaceId, LocalDate date);
    boolean existsBySpaceId(int spaceId);
}
