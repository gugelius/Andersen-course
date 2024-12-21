import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final CoworkingSpaceManager manager = new CoworkingSpaceManager();
    private static boolean exit = false;
    private  static final String CORRECT_VALUE_MSG = "Enter correct value, please!";

    public static void main(String[] args) {

        System.out.println("Welcome to Coworking Space Reservation by GUGELIUSSS");
        mainMenu();
    }

    public static void mainMenu() {
        while (!exit) {
            System.out.println("Options:");
            System.out.println("1. Admin login");
            System.out.println("2. User login");
            System.out.println("3. Exit");
            try {
                switch (scanner.nextInt()) {
                    case 1 -> adminMenu();
                    case 2 -> userMenu();
                    case 3 -> {
                        exit = true;
                        System.out.println("Bye!");
                    }
                    default -> System.out.println("Enter correct value");
                }
            } catch (InputMismatchException e){
                System.out.println(CORRECT_VALUE_MSG);
                System.out.println(e);
                scanner.nextLine();
            }
        }
    }

    public static void adminMenu() {
        boolean backToMainMenu = false;
        while (!backToMainMenu) {
            System.out.println("1. Add a new coworking space");
            System.out.println("2. Remove a coworking space");
            System.out.println("3. View all reservations");
            System.out.println("4. Back to main menu");

            try {
                switch (scanner.nextInt()) {
                    case 1 -> {
                        scanner.nextLine();
                        System.out.println("Enter space type:");
                        String type = scanner.nextLine().trim();
                        if (type.isEmpty()) {
                            System.out.println("Space type cannot be empty.");
                            break;
                        }

                        System.out.println("Enter price:");
                        float price = scanner.nextFloat();

                        if (price <= 0) {
                            System.out.println("Invalid price. Price must be a positive number.");
                            break;
                        }

                        System.out.println("Enter availability status (true/false):");
                        boolean status = scanner.nextBoolean();

                        manager.createSpace(type, price, status);
                        System.out.println("Coworking space added.");
                    }
                    case 2 -> {
                        if (manager.isSpacesEmpty()){
                            System.out.println("There are no existing coworking spaces!");
                            break;
                        }
                        System.out.println("Enter space ID to remove:");
                        int id = scanner.nextInt();

                        if (!manager.spaceExists(id)) {
                            System.out.println("Space with this ID does not exist.");
                            break;
                        }

                        manager.removeSpace(id);
                        System.out.println("Coworking space removed.");
                    }
                    case 3 -> {
                        if (manager.isReservationsEmpty()){
                            System.out.println("There are no existing reservations!");
                            break;
                        }
                        System.out.println("All Reservations:");
                        for (Reservation res : manager.getReservations()) {
                            System.out.println(res);
                        }
                    }
                    case 4 -> backToMainMenu = true;
                    default -> System.out.println("Enter correct value");
                }
            } catch (InputMismatchException e){
                System.out.println(CORRECT_VALUE_MSG);
                System.out.println(e);
                scanner.nextLine();
            }
        }
    }
    public static void userMenu() {
        scanner.nextLine();
        boolean backToMainMenu = false;
        System.out.println("Enter your name:");
        String userName = "";
        while (userName.isEmpty()){
            userName = scanner.nextLine().trim();
            if(userName.isEmpty()){
                System.out.println(CORRECT_VALUE_MSG);
            }
        }

        while (!backToMainMenu) {
            System.out.println("User Menu:");
            System.out.println("1. Browse available spaces");
            System.out.println("2. Make a reservation");
            System.out.println("3. View my reservations");
            System.out.println("4. Cancel a reservation");
            System.out.println("5. Back to main menu");
            try {
                switch (scanner.nextInt()) {
                    case 1 -> manager.printAvailableSpaces();
                    case 2 -> {
                        if (manager.isSpacesEmpty()) {
                            System.out.println("There are no existing coworking spaces! You can't make a reservation without available spaces!");
                            break;
                        }
                        System.out.println("Enter space ID:");
                        int spaceId = scanner.nextInt();
                        scanner.nextLine();
                        if (!manager.spaceExists(spaceId) || !manager.isSpaceAvailable(spaceId)) {
                            System.out.println("Space with this ID does not exist or is not available.");
                            break;
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
                        if (!manager.isTimeSlotAvailable(spaceId, date.toString(), startTime.toString(), endTime.toString())) {
                            System.out.println("Space is not available at this time slot.");
                            break;
                        }
                        manager.makeReservation(userName, spaceId, date.toString(), startTime.toString(), endTime.toString());
                        System.out.println("Reservation made.");
                    }
                    case 3 -> manager.getUserReservations(userName);
                    case 4 -> {
                        if(manager.isReservationsEmpty()){
                            System.out.println("There are no existing reservations! You can't cancel a reservation!");
                            break;
                        }
                        System.out.println("Enter reservation ID to cancel:");
                        int reservationId = scanner.nextInt();
                        scanner.nextLine();
                        if (!manager.reservationExists(reservationId)) {
                            System.out.println("Reservation with this ID does not exist.");
                            break;
                        }

                        manager.cancelReservation(reservationId);
                        System.out.println("Reservation canceled.");
                    }
                    case 5 -> backToMainMenu = true;
                    default -> System.out.println("Enter correct value");
                }
            } catch (InputMismatchException e){
                System.out.println(CORRECT_VALUE_MSG);
                System.out.println(e);
            }
        }
    }
}

