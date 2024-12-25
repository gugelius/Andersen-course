import java.util.InputMismatchException;
import java.util.Scanner;
public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final CoworkingSpaceManager manager = new CoworkingSpaceManager(scanner);
    private static boolean exit = false;
    private static final String WELCOME_MSG = "Welcome to Coworking Space Reservation by GUGELIUSSS", BYE = "Bye!", CORRECT_VALUE_MSG = "Enter correct value, please!";
    private static final String MAIN_MENU = """
            Main menu
            1. Admin login
            2. User login
            3. Exit""",
    ADMIN_MENU = """
            Admin menu:
            1. Add a new coworking space
            2. Remove a coworking space
            3. View all reservations
            4. Back to main menu""",
    USER_MENU = """
            User menu
            1. Browse available spaces
            2. Make a reservation
            3. View my reservations
            4. Cancel a reservation
            5. Back to main menu""";

    public static void main(String[] args) {

        System.out.println(WELCOME_MSG);
        manager.loadSpaces();
        mainMenu();
    }

    public static void mainMenu() {
        while (!exit) {
            System.out.println(MAIN_MENU);
            try {
                switch (scanner.nextInt()) {
                    case 1 -> adminMenu();
                    case 2 -> userMenu();
                    case 3 -> {
                        manager.finalStateWriter();
                        exit = true;
                        System.out.println(BYE);
                    }
                    default -> System.out.println(CORRECT_VALUE_MSG);
                }
            } catch (InputMismatchException e){
                System.out.println(CORRECT_VALUE_MSG + e);
                scanner.nextLine();
            }
        }
    }

    public static void adminMenu() {
        boolean backToMainMenu = false;
        while (!backToMainMenu) {
            System.out.println(ADMIN_MENU);
            try {
                switch (scanner.nextInt()) {
                    case 1 -> manager.createSpace();
                    case 2 -> manager.removeSpace();
                    case 3 -> manager.printAllReservations();
                    case 4 -> backToMainMenu = true;
                    default -> System.out.println(CORRECT_VALUE_MSG);
                }
            } catch (InputMismatchException e){
                System.out.println(CORRECT_VALUE_MSG + e);
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
            System.out.println(USER_MENU);
            try {
                switch (scanner.nextInt()) {
                    case 1 -> manager.printAvailableSpaces();
                    case 2 -> manager.makeReservation(userName);
                    case 3 -> manager.getUserReservations(userName);
                    case 4 -> manager.cancelReservation();
                    case 5 -> backToMainMenu = true;
                    default -> System.out.println(CORRECT_VALUE_MSG);
                }
            } catch (InputMismatchException e){
                System.out.println(CORRECT_VALUE_MSG + e);
                scanner.nextLine();
            }
        }
    }
}

