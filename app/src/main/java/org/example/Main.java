package org.example;
// TODO Tests
import org.example.classLoader.GugeliusClassLoader;
import org.example.dao.ReservationService;
import org.example.dao.SpaceService;
import org.example.inputProvider.InputProvider;
import org.example.inputProvider.ScannerInputProvider;
import java.lang.reflect.Method;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final InputProvider inputProvider = new ScannerInputProvider(scanner);
    private static final ReservationService reservationService = new ReservationService(inputProvider);
    private static final SpaceService spaceService = new SpaceService(inputProvider);
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
        try {
            String classPath = "C:\\Users\\GUGELIUSSS\\Desktop\\study\\Andersen-course\\app\\build\\classes\\java\\main";
            ClassLoader parentClassLoader = GugeliusClassLoader.class.getClassLoader();
            GugeliusClassLoader myClassLoader = new GugeliusClassLoader(classPath, parentClassLoader);
            Class<?> loadedClass = myClassLoader.loadClass("org.example.classLoader.TestMessage");
            System.out.println("Class " + loadedClass.getName() + " successfully loaded.");
            Object instance = loadedClass.newInstance();
            Method method = loadedClass.getMethod("Message");
            method.invoke(instance);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(WELCOME_MSG);
        spaceService.getAllSpaces();
        reservationService.getAllReservations();
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
                    case 1 -> spaceService.createSpace();
                    case 2 -> spaceService.removeSpace();
                    case 3 -> reservationService.printAllReservations();
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
        int user_id = 0;
        while (userName.isEmpty() || user_id == 0){
            userName = scanner.nextLine().trim();
            user_id = reservationService.getUserId(userName);
            if(userName.isEmpty() || user_id == 0){
                System.out.println(CORRECT_VALUE_MSG);
            }
        }

        while (!backToMainMenu) {
            System.out.println(USER_MENU);
            try {
                switch (scanner.nextInt()) {
                    case 1 -> spaceService.printAvailableSpaces();
                    case 2 -> reservationService.makeReservation(user_id);
                    case 3 -> reservationService.getUserReservations(user_id);
                    case 4 -> reservationService.cancelReservation(user_id);
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

