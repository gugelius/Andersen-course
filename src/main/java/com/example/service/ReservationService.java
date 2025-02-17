package com.example.service;

import com.example.entity.Space;
import com.example.entity.Reservation;
import com.example.repository.SpaceRepository;
import com.example.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private SpaceRepository SpaceRepository;

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public List<Reservation> getUserReservations(int userId) {
        return reservationRepository.findByUserId(userId);
    }

    public void makeReservation(int userId, int spaceId, LocalDate date, LocalTime startTime, LocalTime endTime) {
        if (endTime.isBefore(startTime) || endTime.equals(startTime)) {
            throw new IllegalArgumentException("End time must be after start time.");
        }

        Optional<Space> spaceOptional = SpaceRepository.findById(spaceId);
        if (!spaceOptional.isPresent()) {
            throw new IllegalArgumentException("Space with ID " + spaceId + " does not exist.");
        }

        List<Reservation> existingReservations = reservationRepository.findBySpaceIdAndDate(spaceId, date);
        for (Reservation existingReservation : existingReservations) {
            if ((startTime.isAfter(existingReservation.getStartTime()) && startTime.isBefore(existingReservation.getEndTime())) ||
                    (endTime.isAfter(existingReservation.getStartTime()) && endTime.isBefore(existingReservation.getEndTime())) ||
                    (startTime.equals(existingReservation.getStartTime()) || endTime.equals(existingReservation.getEndTime()))) {
                throw new IllegalArgumentException("Space is already reserved during the specified time.");
            }
        }

        Reservation reservation = new Reservation();
        reservation.setUserId(userId);
        reservation.setSpaceId(spaceId);
        reservation.setDate(date);
        reservation.setStartTime(startTime);
        reservation.setEndTime(endTime);
        reservationRepository.save(reservation);
    }

    public void cancelReservation(int reservationId) {
        reservationRepository.deleteById(reservationId);
    }
}
