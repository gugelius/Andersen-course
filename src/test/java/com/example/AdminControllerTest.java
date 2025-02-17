package com.example;

import com.example.controller.AdminController;
import com.example.service.SpaceService;
import com.example.service.ReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.validation.support.BindingAwareModelMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class AdminControllerTest {

    @InjectMocks
    private AdminController adminController;

    @Mock
    private SpaceService spaceService;

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
        doNothing().when(reservationService).makeReservation(anyInt(), anyInt(), any(), any(), any());

        String result = adminController.makeReservation(1, 1, "2023-04-06", "10:00", "12:00", model);

        assertEquals("redirect:/admin/reservations", result);
        verify(reservationService, times(1)).makeReservation(anyInt(), anyInt(), any(), any(), any());
    }

    @Test
    public void givenInvalidInput_whenMakeReservation_thenReturnReservationsPage() {
        Model model = new BindingAwareModelMap();

        String result = adminController.makeReservation(1, 1, "", "10:00", "12:00", model);

        assertEquals("admin/reservations", result);
        verify(reservationService, never()).makeReservation(anyInt(), anyInt(), any(), any(), any());
        assertEquals("Invalid input: all fields are required and must be valid.", model.getAttribute("errorMessage"));
    }

    @Test
    public void givenValidInput_whenCreateSpace_thenRedirectToSpaces() {
        doNothing().when(spaceService).createSpace(anyString(), anyFloat(), anyBoolean());

        String result = adminController.createSpace("Office", 500, "on", model);

        assertEquals("redirect:/admin/spaces", result);
        verify(spaceService, times(1)).createSpace(anyString(), anyFloat(), anyBoolean());
    }

    @Test
    public void givenInvalidInput_whenCreateSpace_thenReturnSpacesPage() {
        Model model = new BindingAwareModelMap();

        String result = adminController.createSpace("", 0, null, model);

        assertEquals("admin/spaces", result);
        verify(spaceService, never()).createSpace(anyString(), anyFloat(), anyBoolean());
        assertEquals("Invalid input: type must not be empty and price must be greater than 0.", model.getAttribute("errorMessage"));
    }
}
