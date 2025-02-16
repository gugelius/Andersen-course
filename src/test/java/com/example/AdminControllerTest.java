package com.example;

import com.example.controller.AdminController;
import com.example.service.ReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AdminControllerTest {

    @InjectMocks
    private AdminController adminController;

    @Mock
    private ReservationService reservationService;

    @Mock
    private Model model;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void givenValidInput_whenMakeReservation_thenRedirectToReservations() {
        String result = adminController.makeReservation(1, 1, "2023-04-06", "10:00", "12:00", model);
        assertEquals("redirect:/admin/reservations", result);
    }

    @Test
    public void givenInvalidInput_whenMakeReservation_thenReturnReservationsPage() {
        String result = adminController.makeReservation(1, 1, "", "10:00", "12:00", model);
        assertEquals("reservations", result);
    }
}
