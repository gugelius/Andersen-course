package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


import static org.junit.jupiter.api.Assertions.*;

class CoworkingSpaceManagerTest {

    private CoworkingSpaceManager manager;
    private InputProvider mockInputProvider;
    private static final int LOADED_SPACES_SIZE = 5;
    private static final String userName = "Gugelius";

    @BeforeEach
    void setUp() {
        mockInputProvider = Mockito.mock(InputProvider.class);
        manager = new CoworkingSpaceManager(mockInputProvider);
    }

    @Test
    void givenManager_whenLoadSpaces_thenSpacesLoadedCorrectly(){
        manager.loadSpaces();
        assertEquals(manager.getSpaces().size(), LOADED_SPACES_SIZE);
    }

    @Test
    void givenValidInput_whenCreateSpace_thenSpaceIsAdded() {
        Mockito.when(mockInputProvider.nextLine())
                .thenReturn("")
                .thenReturn("Private Office");

        Mockito.when(mockInputProvider.nextFloat())
                .thenReturn(-100f)
                .thenReturn(500f);

        Mockito.when(mockInputProvider.nextBoolean())
                .thenReturn(true);

        manager.createSpace();

        assertFalse(manager.getSpaces().isEmpty());
        assertEquals("Private Office", manager.getSpaces().get(0).getType());
        assertEquals(500f, manager.getSpaces().get(0).getPrice());
        assertTrue(manager.getSpaces().get(0).isStatus());
    }

    @Test
    void givenExistingSpace_whenRemoveSpace_thenSpaceIsRemoved(){
        manager.loadSpaces();
        Mockito.when(mockInputProvider.nextInt())
                .thenReturn(1);
        manager.removeSpace();
        assertNotEquals(LOADED_SPACES_SIZE, manager.getSpaces().size());
        assertTrue(LOADED_SPACES_SIZE == manager.getSpaces().size() + 1);
    }

    @Test
    void givenValidInput_whenMakeReservation_thenReservationIsAdded() {
        manager.loadSpaces();

        Mockito.when(mockInputProvider.nextInt())
                .thenReturn(1);

        Mockito.when(mockInputProvider.nextLine())
                .thenReturn("")
                .thenReturn("2025-01-17")
                .thenReturn("09:00")
                .thenReturn("17:00");

        manager.makeReservation(userName);

        assertFalse(manager.getReservations().isEmpty());
        assertEquals(1, manager.getReservations().size());
        assertEquals(userName, manager.getReservations().get(0).getUserName());
    }

    @Test
    void givenExistingReservation_whenCancelReservation_thenReservationIsRemoved(){
        manager.loadSpaces();

        Mockito.when(mockInputProvider.nextInt())
                .thenReturn(1);

        Mockito.when(mockInputProvider.nextLine())
                .thenReturn("")
                .thenReturn("2025-01-17")
                .thenReturn("09:00")
                .thenReturn("17:00");
        manager.makeReservation(userName);

        Mockito.when(mockInputProvider.nextInt())
                        .thenReturn(1);
        manager.cancelReservation(userName);

        assertTrue(manager.getReservations().isEmpty());
    }
}
