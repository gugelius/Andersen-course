package org.example;

import org.example.dao.ReservationService;
import org.example.entity.Reservation;
import org.example.inputProvider.InputProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReservationServiceTest {

    private ReservationService reservationService;
    private InputProvider mockInputProvider;
    private EntityManagerFactory emf;
    private EntityManager em;
    private EntityTransaction transaction;

    @BeforeEach
    void setUp() {
        mockInputProvider = Mockito.mock(InputProvider.class);
        emf = Mockito.mock(EntityManagerFactory.class);
        em = Mockito.mock(EntityManager.class);
        transaction = Mockito.mock(EntityTransaction.class);

        when(emf.createEntityManager()).thenReturn(em);
        when(em.getTransaction()).thenReturn(transaction);
        reservationService = new ReservationService(mockInputProvider);
    }

    @Test
    void testMakeReservation() {
        int userId = 1;
        int spaceId = 1;

        when(mockInputProvider.nextInt()).thenReturn(spaceId);
        when(mockInputProvider.nextLine()).thenReturn("2025-01-17").thenReturn("09:00").thenReturn("17:00");

        reservationService.makeReservation(userId);

        verify(transaction).begin();
        verify(em).persist(any(Reservation.class));
        verify(transaction).commit();
    }

    @Test
    void testCancelReservation() {
        int userId = 1;
        Reservation reservation = new Reservation(userId, 13, LocalDate.of(2025, 1, 17), LocalTime.of(9, 0), LocalTime.of(17, 0));
        when(em.find(Reservation.class, reservation.getId())).thenReturn(reservation);
        when(mockInputProvider.nextInt()).thenReturn(reservation.getId());

        reservationService.cancelReservation(userId);

        verify(transaction).begin();
        verify(em).remove(any(Reservation.class));
        verify(transaction).commit();
    }

    @Test
    void testGetUserId() {
        TypedQuery<Integer> userQuery = Mockito.mock(TypedQuery.class);
        when(em.createQuery("SELECT u.id FROM User u WHERE u.name = :userName", Integer.class)).thenReturn(userQuery);
        when(userQuery.setParameter(eq("userName"), anyString())).thenReturn(userQuery);
        when(userQuery.getResultList()).thenReturn(List.of(1));

        int userId = reservationService.getUserId("Ilya");

        assertEquals(1, userId);
    }
}
