package com.example;

import com.example.entity.Space;
import com.example.entity.Reservation;
import com.example.repository.ReservationRepository;
import com.example.repository.SpaceRepository;
import com.example.repository.UserRepository;
import com.example.service.ReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ReservationServiceTest {

    @InjectMocks
    private ReservationService reservationService;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private SpaceRepository spaceRepository;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        reservationService = ReservationService.getInstance();
        reservationService.setRepositories(reservationRepository, spaceRepository, userRepository);
    }

    @Test
    public void givenValidInput_whenMakeReservation_thenNoExceptionThrown() {
        when(userRepository.existsById(anyInt())).thenReturn(true);
        when(spaceRepository.findById(anyInt())).thenReturn(Optional.of(new Space()));

        LocalDate date = LocalDate.of(2023, 4, 6);
        LocalTime startTime = LocalTime.of(10, 0);
        LocalTime endTime = LocalTime.of(12, 0);

        Reservation reservation = new Reservation();
        reservation.setUserId(1);
        reservation.setSpaceId(1);
        reservation.setDate(date);
        reservation.setStartTime(startTime);
        reservation.setEndTime(endTime);

        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);

        assertDoesNotThrow(() -> reservationService.makeReservation(1, 1, date, startTime, endTime));
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }

    @Test
    public void givenEndTimeBeforeStartTime_whenMakeReservation_thenThrowIllegalArgumentException() {
        LocalDate date = LocalDate.of(2023, 4, 6);
        LocalTime startTime = LocalTime.of(10, 0);
        LocalTime endTime = LocalTime.of(9, 0);

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                reservationService.makeReservation(1, 1, date, startTime, endTime)
        );

        String expectedMessage = "End time must be after start time.";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void givenSpaceNotExists_whenMakeReservation_thenThrowIllegalArgumentException() {
        when(userRepository.existsById(anyInt())).thenReturn(true);
        when(spaceRepository.findById(anyInt())).thenReturn(Optional.empty());

        LocalDate date = LocalDate.of(2023, 4, 6);
        LocalTime startTime = LocalTime.of(10, 0);
        LocalTime endTime = LocalTime.of(12, 0);

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                reservationService.makeReservation(1, 1, date, startTime, endTime)
        );

        String expectedMessage = "Space with ID 1 does not exist.";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }
}
