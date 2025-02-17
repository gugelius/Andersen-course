package org.example;

import org.example.dao.ConnectionPool;
import org.example.dao.ReservationService;
import org.example.dao.SpaceService;
import org.example.inputProvider.InputProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

class CoworkingSpaceManagerTest {

    private SpaceService spaceService;
    private ReservationService reservationService;
    private InputProvider mockInputProvider;
    private static final String userName = "Ilya";
    private static final String GET_COUNT_OF_SPACES = "SELECT COUNT(*) FROM spaces";
    private static final String REMOVE_RESERVATION = "DELETE FROM reservations WHERE space_id = ?";

    @BeforeEach
    void setUp() {
        mockInputProvider = Mockito.mock(InputProvider.class);
        spaceService = new SpaceService(mockInputProvider);
        reservationService = new ReservationService(mockInputProvider);
        spaceService.getAllSpaces();
        reservationService.getAllReservations();
    }

    @Test
    void givenManager_whenLoadSpaces_thenSpacesLoadedCorrectly() {
        assertEquals(getNumberOfSpacesInDatabase(), spaceService.getSpaces().size());
    }

    private int getNumberOfSpacesInDatabase() {
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(GET_COUNT_OF_SPACES)) {
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
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

        spaceService.createSpace();

        assertFalse(spaceService.getSpaces().isEmpty());
        assertEquals("Private Office", spaceService.getSpaces().get(spaceService.getSpaces().size() - 1).getType());
        assertEquals(500f, spaceService.getSpaces().get(spaceService.getSpaces().size() - 1).getPrice());
        assertTrue(spaceService.getSpaces().get(spaceService.getSpaces().size() - 1).isStatus());
    }

    @Test
    void givenExistingSpace_whenRemoveSpace_thenSpaceIsRemoved() {
        int initialSize = spaceService.getSpaces().size();
        int spaceId = spaceService.getSpaces().get(0).getId();

        removeReservationsBySpaceId(spaceId);

        Mockito.when(mockInputProvider.nextInt())
                .thenReturn(spaceId);

        spaceService.removeSpace();
        assertEquals(initialSize - 1, spaceService.getSpaces().size());
    }

    private void removeReservationsBySpaceId(int spaceId) {
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     REMOVE_RESERVATION)) {
            preparedStatement.setInt(1, spaceId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error while removing reservations: " + e.getMessage());
        }
    }

    @Test
    void givenValidInput_whenMakeReservation_thenReservationIsAdded() {
        int userId = reservationService.getUserId(userName);
        int spaceId = spaceService.getSpaces().get(0).getId();

        Mockito.when(mockInputProvider.nextInt())
                .thenReturn(spaceId);

        Mockito.when(mockInputProvider.nextLine())
                .thenReturn("")
                .thenReturn("2025-01-17")
                .thenReturn("09:00")
                .thenReturn("17:00");

        reservationService.makeReservation(userId);

        assertFalse(reservationService.getReservations().isEmpty());
        assertEquals(1, reservationService.getReservations().size());
        assertEquals(userId, reservationService.getReservations().get(0).getUserId());
    }

    @Test
    void givenExistingReservation_whenCancelReservation_thenReservationIsRemoved() {
        int userId = reservationService.getUserId(userName);
        int spaceId = spaceService.getSpaces().get(0).getId();

        Mockito.when(mockInputProvider.nextInt())
                .thenReturn(spaceId);

        Mockito.when(mockInputProvider.nextLine())
                .thenReturn("")
                .thenReturn("2025-01-17")
                .thenReturn("09:00")
                .thenReturn("17:00");

        reservationService.makeReservation(userId);

        int reservationId = reservationService.getReservations().get(0).getId();

        Mockito.when(mockInputProvider.nextInt())
                .thenReturn(reservationId);

        reservationService.cancelReservation(userId);

        assertTrue(reservationService.getReservations().isEmpty());
    }
}
