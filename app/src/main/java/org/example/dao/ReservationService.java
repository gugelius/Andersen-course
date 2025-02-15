package org.example.dao;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.example.entity.CoworkingSpace;
import org.example.entity.Reservation;
import org.example.inputProvider.InputProvider;

public class ReservationService {
    private static final String GET_ALL_RESERVATIONS = "SELECT * FROM reservations";
    private static final String GET_USER_ID = "SELECT user_id FROM users WHERE user_name = ?";
    private static final String MAKE_RESERVATION = "INSERT INTO reservations (user_id, space_id, reservation_date, start_time, end_time) VALUES (?, ?, ?, ?, ?)";
    private static final String CANCEL_RESERVATION = "DELETE FROM reservations WHERE reservation_id = ? AND user_id = ?";
    private final InputProvider inputProvider;
    private final List<CoworkingSpace> spaces = new ArrayList<>();
    private final List<Reservation> reservations = new ArrayList<>();
    private static final ConnectionPool connectionPool = ConnectionPool.getInstance();

    public ReservationService(InputProvider inputProvider){
        this.inputProvider = inputProvider;
    }
    public List<? extends Reservation> getReservations() {
        return reservations;
    }
    public void getAllReservations() {
        reservations.clear();
        try (Connection connection = connectionPool.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(GET_ALL_RESERVATIONS)) {
            while (resultSet.next()) {
                int id = resultSet.getInt("reservation_id");
                int user_id = resultSet.getInt("user_id");
                int spaceId = resultSet.getInt("space_id");
                String date = resultSet.getDate("reservation_date").toString();
                String startTime = resultSet.getTime("start_time").toString();
                String endTime = resultSet.getTime("end_time").toString();
                reservations.add(new Reservation(id, user_id, spaceId, date, startTime, endTime));
            }
        } catch (SQLException e) {
            System.out.println("Error while loading reservations: " + e.getMessage());
        }
    }
    public int getUserId(String userName){
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     GET_USER_ID)) {
            preparedStatement.setString(1, userName);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("user_id");
                }
            }
        } catch (SQLException e) {
            System.out.println("No such user: " + e.getMessage());
        }
        return 0;
    }
    public void getUserReservations(int user_id) {
        List<Reservation> userReservations = reservations.stream()
                .filter(reservation -> Optional.ofNullable(reservation)
                        .map(res -> res.getUserId() == user_id)
                        .orElse(false))
                .collect(Collectors.toList());
        if (userReservations.isEmpty()) {
            System.out.println("You have no reservations!");
        } else {
            userReservations.forEach(System.out::println);
        }
    }
    public void printAllReservations(){
        if(reservationsAreEmpty()){
            System.out.println("There are no existing reservations!");
        } else {
            reservations.forEach(System.out::println);
        }
    }
    private boolean reservationsAreEmpty(){
        return reservations.size() == 0;
    }
    private boolean reservationsAreEmpty(int user_id){
        return reservations.stream()
                .noneMatch(reservation -> Optional.ofNullable(reservation)
                        .map(r -> r.getUserId() == user_id)
                        .orElse(false));
    }
    public void makeReservation(int userId) {
        if (SpaceService.isSpacesEmpty()) {
            System.out.println("There are no existing coworking spaces! You can't make a reservation without available spaces!");
            return;
        }
        System.out.println("Enter space ID:");
        int spaceId = inputProvider.nextInt();
        inputProvider.nextLine();
        if (!SpaceService.spaceExists(spaceId) || !SpaceService.isSpaceAvailable(spaceId)) {
            System.out.println("Space with this ID does not exist or is not available.");
            return;
        }

        LocalDate date = null;
        while (date == null) {
            System.out.println("Enter date (YYYY-MM-DD):");
            String dateString = inputProvider.nextLine();
            try {
                date = LocalDate.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please enter the date in YYYY-MM-DD format.");
            }
        }
        LocalTime startTime = null;
        while (startTime == null) {
            System.out.println("Enter start time (HH:MM):");
            String startTimeString = inputProvider.nextLine();
            try {
                startTime = LocalTime.parse(startTimeString, DateTimeFormatter.ofPattern("HH:mm"));
            } catch (DateTimeParseException e) {
                System.out.println("Invalid time format. Please enter the time in HH:MM format.");
            }
        }
        LocalTime endTime = null;
        while (endTime == null) {
            System.out.println("Enter end time (HH:MM):");
            String endTimeString = inputProvider.nextLine();
            try {
                endTime = LocalTime.parse(endTimeString, DateTimeFormatter.ofPattern("HH:mm"));
            } catch (DateTimeParseException e) {
                System.out.println("Invalid time format. Please enter the time in HH:MM format.");
            }
        }

        if (!isTimeSlotAvailable(spaceId, date.toString(), startTime.toString(), endTime.toString())) {
            System.out.println("Space is not available at this time slot!");
            return;
        }

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     MAKE_RESERVATION,
                     Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, spaceId);
            preparedStatement.setDate(3, Date.valueOf(date));
            preparedStatement.setTime(4, Time.valueOf(correctTimeFormat(startTime.toString())));
            preparedStatement.setTime(5, Time.valueOf(correctTimeFormat(endTime.toString())));
            preparedStatement.executeUpdate();

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    reservations.add(new Reservation(id, userId, spaceId, date.toString(), startTime.toString(), endTime.toString()));
                }
            }

            System.out.println("Reservation made.");
        } catch (SQLException e) {
            System.out.println("Error while making reservation: " + e.getMessage());
        }
    }
    private String correctTimeFormat(String time) {
        if (time.length() == 5) {
            return time + ":00";
        }
        return time;
    }
    public void cancelReservation(int userId) {
        if (reservationsAreEmpty(userId)) {
            System.out.println("There are no existing reservations! You can't cancel a reservation!");
            return;
        }
        System.out.println("Enter reservation ID to cancel:");
        int reservationId = inputProvider.nextInt();
        inputProvider.nextLine();
        if (!reservationExists(reservationId)) {
            System.out.println("Reservation with this ID does not exist.");
            return;
        }

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     CANCEL_RESERVATION)) {
            preparedStatement.setInt(1, reservationId);
            preparedStatement.setInt(2, userId);
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                reservations.removeIf(reservation -> reservation.getId() == reservationId);
                System.out.println("Reservation canceled.");
            } else {
                System.out.println("Reservation with this ID does not exist for the specified user.");
            }
        } catch (SQLException e) {
            System.out.println("Error while canceling reservation: " + e.getMessage());
        }
    }
    private boolean reservationExists(int id) {
        return reservations.stream()
                .anyMatch(reservation -> Optional.ofNullable(reservation)
                        .map(r -> r.getId() == id)
                        .orElse(false));
    }
    private boolean isTimeSlotAvailable(int spaceId, String date, String startTime, String endTime) {
        return reservations.stream()
                .filter(res -> res.getSpaceId() == spaceId && res.getDate().equals(date))
                .noneMatch(res ->
                        (startTime.compareTo(res.getStartTime()) >= 0 && startTime.compareTo(res.getEndTime()) < 0) || (endTime.compareTo(res.getStartTime()) > 0 && endTime.compareTo(res.getEndTime()) <= 0) || (startTime.compareTo(res.getStartTime()) <= 0 && endTime.compareTo(res.getEndTime()) >= 0));
    }
}
