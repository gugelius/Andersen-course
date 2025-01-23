package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

public class CoworkingSpaceManager {
    private final InputProvider inputProvider;
//    private final Scanner scanner;
    private final List<CoworkingSpace> spaces = new ArrayList<>();
    private final List<Reservation> reservations = new ArrayList<>();
    private int reservationIdCounter = 1, coworkingSpaceIdCounter = 1;
    private static final String INPUT_FILE_NAME = "C:\\Users\\GUGELIUSSS\\Desktop\\study\\Andersen-course\\app\\src\\main\\resources\\input.txt";
    private static final String OUTPUT_FILE_NAME = "C:\\Users\\GUGELIUSSS\\Desktop\\study\\Andersen-course\\app\\src\\main\\resources\\out.txt";
    public CoworkingSpaceManager(InputProvider inputProvider){
        this.inputProvider = inputProvider;
    }

    public List<CoworkingSpace> getSpaces() {
        return spaces;
    }

    private boolean AvailableSpacesExist(){
        return spaces.stream()
                .anyMatch(CoworkingSpace::isStatus);
    }

    public void loadSpaces() {
        try (BufferedReader reader = new BufferedReader(new FileReader(INPUT_FILE_NAME))) {
            spaces.addAll(reader.lines()
                    .map(line -> {
                        String[] parts = line.split(",");
                        if (parts.length != 3) {
                            System.out.println("Incorrect line format in input file: " + line);
                            return Optional.<CoworkingSpace>empty();
                        }
                        try {
                            String type = parts[0].trim();
                            float price = Float.parseFloat(parts[1].trim());
                            boolean status = Boolean.parseBoolean(parts[2].trim());
                            return Optional.of(new CoworkingSpace(coworkingSpaceIdCounter++, type, price, status));
                        } catch (NumberFormatException e) {
                            System.out.println("Error loading space: " + e.getMessage());
                            return Optional.<CoworkingSpace>empty();
                        }
                    })
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList()));
        } catch (IOException e) {
            System.out.println("Error while loading spaces: " + e);
        }
    }

    public void createSpace() {
        inputProvider.nextLine();
        System.out.println("Enter space type:");
        String type = inputProvider.nextLine().trim();

        while (type.isEmpty()) {
            System.out.println("Space type cannot be empty.");
            type = inputProvider.nextLine().trim();
        }

        System.out.println("Enter price:");
        float price = inputProvider.nextFloat();

        while (price <= 0) {
            System.out.println("Invalid price. Price must be a positive number.");
            price = inputProvider.nextFloat();
        }

        System.out.println("Enter availability status (true/false):");
        boolean status = inputProvider.nextBoolean();

        spaces.add(new CoworkingSpace(coworkingSpaceIdCounter++, type, price, status));
        System.out.println("Coworking space added.");
    }

    private boolean spaceExists(int id) {
        return spaces.stream()
                .anyMatch(space -> Optional.ofNullable(space)
                        .map(s -> s.getId() == id)
                        .orElse(false));
    }

    private boolean isSpacesEmpty(){
        return spaces.size() == 0;
    }

    private boolean isSpaceAvailable(int id) {
        return spaces.stream()
                .anyMatch(space -> Optional.ofNullable(space)
                        .map(s -> s.getId() == id && s.isStatus())
                        .orElse(false));
    }

    public void printAvailableSpaces() {
        if(AvailableSpacesExist()){
            System.out.println("Available spaces:");
            spaces.stream()
                    .filter(CoworkingSpace::isStatus)
                    .forEach(System.out::println);
        }
        else {
            System.out.println("There are no available spaces!");
        }
    }

    public void removeSpace() {
        if (isSpacesEmpty()){
            System.out.println("There are no existing coworking spaces!");
            return;
        }

        System.out.println("Enter space ID to remove:");
        int id = inputProvider.nextInt();
        while (!spaceExists(id)) {
            System.out.println("Space with this ID does not exist.");
            id = inputProvider.nextInt();
        }

        int finalId = id;
        spaces.removeIf(space -> space.getId() == finalId);
        System.out.println("Coworking space removed.");
    }

    public List<? extends Reservation> getReservations() {
        return reservations;
    }

    public void getUserReservations(String userName) {
        List<Reservation> userReservations = reservations.stream()
                .filter(reservation -> Optional.ofNullable(reservation)
                        .map(res -> res.getUserName().equals(userName))
                        .orElse(false))
                .collect(Collectors.toList());
        if (userReservations.isEmpty()) {
            System.out.println("You have no reservations!");
        } else {
            userReservations.forEach(System.out::println);
        }
    }

    public void printAllReservations(){
        if(ReservationsAreEmpty()){
            System.out.println("There are no existing reservations!");
        }
        else {
            reservations.forEach(System.out::println);
        }
    }
    private boolean ReservationsAreEmpty(){
        return reservations.size() == 0;
    }
    private boolean ReservationsAreEmpty(String userName){
        return reservations.stream()
                .noneMatch(reservation -> Optional.ofNullable(reservation)
                        .map(r -> r.getUserName().equals(userName))
                        .orElse(false));
    }

    public void makeReservation(String userName) {
        if (isSpacesEmpty()) {
            System.out.println("There are no existing coworking spaces! You can't make a reservation without available spaces!");
            return;
        }
        System.out.println("Enter space ID:");
        int spaceId = inputProvider.nextInt();
        inputProvider.nextLine();
        if (!spaceExists(spaceId) || !isSpaceAvailable(spaceId)) {
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
        try {
            if (!isTimeSlotAvailable(spaceId, date.toString(), startTime.toString(), endTime.toString())) {
                throw new TimeSlotNotAvailableException("Space is not available at this time slot!");
            }
            reservations.add(new Reservation(reservationIdCounter++, userName, spaceId, date.toString(), startTime.toString(), endTime.toString()));
            System.out.println("Reservation made.");
        } catch (TimeSlotNotAvailableException e){
            System.out.println("Error while making a reservation " + e);
        }
    }

    public void cancelReservation(String userName) {
        if(ReservationsAreEmpty(userName)){
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

        reservations.removeIf(reservation -> reservation.getId() == reservationId);
        System.out.println("Reservation canceled.");
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
    public void finalStateWriter() {
        try (FileWriter writer = new FileWriter(OUTPUT_FILE_NAME)) {
            writer.write("All reservations:\n");
            reservations.forEach(res -> {
                try {
                    writer.write(res.toString() + "\n");
                } catch (IOException e) {
                    System.out.println("Error writing reservation: " + e);
                }
            });

            writer.write("All spaces:\n");
            spaces.forEach(space -> {
                try {
                    writer.write(space.toString() + "\n");
                } catch (IOException e) {
                    System.out.println("Error writing space: " + e);
                }
            });
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }
    }
}
