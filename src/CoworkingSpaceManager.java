import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CoworkingSpaceManager {
    private final Scanner scanner;
    private final List<CoworkingSpace> spaces = new ArrayList<>();
    private final List<Reservation> reservations = new ArrayList<>();
    private int reservationIdCounter = 1, coworkingSpaceIdCounter = 1;
    public CoworkingSpaceManager(Scanner scanner){
        this.scanner = scanner;
    }

    public void createSpace() {
        scanner.nextLine();
        System.out.println("Enter space type:");
        String type = scanner.nextLine().trim();

        while (type.isEmpty()) {
            System.out.println("Space type cannot be empty.");
            type = scanner.nextLine().trim();
        }

        System.out.println("Enter price:");
        float price = scanner.nextFloat();

        while (price <= 0) {
            System.out.println("Invalid price. Price must be a positive number.");
            price = scanner.nextFloat();
        }

        System.out.println("Enter availability status (true/false):");
        boolean status = scanner.nextBoolean();

        spaces.add(new CoworkingSpace(coworkingSpaceIdCounter++, type, price, status));
        System.out.println("Coworking space added.");
    }
    public void removeSpace() {
        if (isSpacesEmpty()){
            System.out.println("There are no existing coworking spaces!");
            return;
        }

        System.out.println("Enter space ID to remove:");
        int id = scanner.nextInt();
        while (!spaceExists(id)) {
            System.out.println("Space with this ID does not exist.");
            id = scanner.nextInt();
        }

        int finalId = id;
        spaces.removeIf(space -> space.getId() == finalId);
        System.out.println("Coworking space removed.");
    }

    public List<CoworkingSpace> getSpaces() {
        return spaces;
    }

    public List<CoworkingSpace> getAvailableSpaces() {
        List<CoworkingSpace> availableSpaces = new ArrayList<>();
        for (CoworkingSpace space : spaces) {
            if (space.isStatus()) {
                availableSpaces.add(space);
            }
        }
        return availableSpaces;
    }
    public void printAvailableSpaces() {
        System.out.println("Available Spaces:");
        if (getAvailableSpaces().size() != 0) {
            for (CoworkingSpace space : getAvailableSpaces()) {
                System.out.println(space);
            }
        }else{
            System.out.println("There are no available spaces!");
        }
    }
    public void printAllReservations(){
        if (isReservationsEmpty()){
            System.out.println("There are no existing reservations!");
            return;
        }
        System.out.println("All Reservations:");
        for (Reservation res : getReservations()) {
            System.out.println(res);
        }
    }
    public List<Reservation> getReservations() {
        return reservations;
    }

    public boolean spaceExists(int id) {
        for (CoworkingSpace space : spaces) {
            if (space.getId() == id) {
                return true;
            }
        }
        return false;
    }
    public boolean isSpacesEmpty(){
        return spaces.size() == 0;
    }
    public boolean isReservationsEmpty(){
        return reservations.size() == 0;
    }
    public void getUserReservations(String userName){
        boolean hasReservations = false;
        for (Reservation res : getReservations()) {
            if (res.getUserName().equals(userName)) {
                System.out.println(res);
                hasReservations = true;
            }
        }
        if (!hasReservations) {
            System.out.println("You have no reservations.");
        }
    }
    public boolean isSpaceAvailable(int id) {
        for (CoworkingSpace space : spaces) {
            if (space.getId() == id && space.isStatus()) {
                return true;
            }
        }
        return false;
    }

    public void makeReservation(String userName) {
        if (isSpacesEmpty()) {
            System.out.println("There are no existing coworking spaces! You can't make a reservation without available spaces!");
            return;
        }
        System.out.println("Enter space ID:");
        int spaceId = scanner.nextInt();
        scanner.nextLine();
        if (!spaceExists(spaceId) || !isSpaceAvailable(spaceId)) {
            System.out.println("Space with this ID does not exist or is not available.");
            return;
        }

        LocalDate date = null;
        while (date == null) {
            System.out.println("Enter date (YYYY-MM-DD):");
            String dateString = scanner.nextLine();
            try {
                date = LocalDate.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please enter the date in YYYY-MM-DD format.");
            }
        }
        LocalTime startTime = null;
        while (startTime == null) {
            System.out.println("Enter start time (HH:MM):");
            String startTimeString = scanner.nextLine();
            try {
                startTime = LocalTime.parse(startTimeString, DateTimeFormatter.ofPattern("HH:mm"));
            } catch (DateTimeParseException e) {
                System.out.println("Invalid time format. Please enter the time in HH:MM format.");
            }
        }
        LocalTime endTime = null;
        while (endTime == null) {
            System.out.println("Enter end time (HH:MM):");
            String endTimeString = scanner.nextLine();
            try {
                endTime = LocalTime.parse(endTimeString, DateTimeFormatter.ofPattern("HH:mm"));
            } catch (DateTimeParseException e) {
                System.out.println("Invalid time format. Please enter the time in HH:MM format.");
            }
        }
        if (!isTimeSlotAvailable(spaceId, date.toString(), startTime.toString(), endTime.toString())) {
            System.out.println("Space is not available at this time slot.");
            return;
        }
        reservations.add(new Reservation(reservationIdCounter++, userName, spaceId, date.toString(), startTime.toString(), endTime.toString()));
        System.out.println("Reservation made.");
    }

    public void cancelReservation() {
        if(isReservationsEmpty()){
            System.out.println("There are no existing reservations! You can't cancel a reservation!");
            return;
        }
        System.out.println("Enter reservation ID to cancel:");
        int reservationId = scanner.nextInt();
        scanner.nextLine();
        if (!reservationExists(reservationId)) {
            System.out.println("Reservation with this ID does not exist.");
            return;
        }

        reservations.removeIf(reservation -> reservation.getId() == reservationId);
        System.out.println("Reservation canceled.");
    }

    public boolean reservationExists(int id) {
        for (Reservation res : reservations) {
            if (res.getId() == id) {
                return true;
            }
        }
        return false;
    }

    public boolean isTimeSlotAvailable(int spaceId, String date, String startTime, String endTime) {
        for (Reservation res : reservations) {
            if (res.getSpaceId() == spaceId && res.getDate().equals(date)) { // Проверка на пересечение временных интервалов
                 if ((startTime.compareTo(res.getStartTime()) >= 0 && startTime.compareTo(res.getEndTime()) < 0) || (endTime.compareTo(res.getStartTime()) > 0 && endTime.compareTo(res.getEndTime()) <= 0) || (startTime.compareTo(res.getStartTime()) <= 0 && endTime.compareTo(res.getEndTime()) >= 0)) {
                     return false;
                 }
            }
        }
        return true;
    }
}
