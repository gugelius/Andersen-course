package org.example;

import org.example.dao.ReservationService;
import org.example.dao.SpaceService;
import org.example.inputProvider.InputProvider;
import org.example.inputProvider.ScannerInputProvider;
import org.example.classLoader.GugeliusClassLoader;

import java.lang.reflect.Method;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final InputProvider inputProvider = new ScannerInputProvider(scanner);
    private static final ReservationService reservationService = new ReservationService(inputProvider);
    private static final SpaceService spaceService = new SpaceService(inputProvider);
    private static boolean exit = false;
    private static final String WELCOME_MSG = "Welcome to Coworking Space Reservation by GUGELIUSSS";
    private static final String BYE = "Bye!";
    private static final String CORRECT_VALUE_MSG = "Enter correct value, please!";
    private static final String MAIN_MENU = """
            Main menu
            1. Admin login
            2. User login
            3. Exit""";
    private static final String ADMIN_MENU = """
            Admin menu:
            1. Add a new coworking space
            2. Remove a coworking space
            3. View all reservations
            4. Back to main menu""";
    private static final String USER_MENU = """
            User menu
            1. Browse available spaces
            2. Make a reservation
            3. View my reservations
            4. Cancel a reservation
            5. Back to main menu""";

    public static void main(String[] args) {
        System.out.println(WELCOME_MSG);
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
        mainMenu();
    }

    private static void mainMenu() {
        while (!exit) {
            System.out.println(MAIN_MENU);
            try {
                int choice = scanner.nextInt();
                scanner.nextLine();
                switch (choice) {
                    case 1 -> adminMenu();
                    case 2 -> userMenu();
                    case 3 -> {
                        exit = true;
                        System.out.println(BYE);
                    }
                    default -> System.out.println(CORRECT_VALUE_MSG);
                }
            } catch (InputMismatchException e) {
                System.out.println(CORRECT_VALUE_MSG);
                scanner.nextLine();
            }
        }
    }

    private static void adminMenu() {
        boolean backToMainMenu = false;
        while (!backToMainMenu) {
            System.out.println(ADMIN_MENU);
            try {
                int choice = scanner.nextInt();
                scanner.nextLine();
                switch (choice) {
                    case 1 -> spaceService.createSpace();
                    case 2 -> spaceService.removeSpace();
                    case 3 -> reservationService.printAllReservations();
                    case 4 -> backToMainMenu = true;
                    default -> System.out.println(CORRECT_VALUE_MSG);
                }
            } catch (InputMismatchException e) {
                System.out.println(CORRECT_VALUE_MSG);
                scanner.nextLine();
            }
        }
    }

    private static void userMenu() {
        boolean backToMainMenu = false;
        System.out.println("Enter your name:");
        String userName = scanner.nextLine().trim();
        int userId = reservationService.getUserId(userName);

        while (userId == 0) {
            System.out.println(CORRECT_VALUE_MSG);
            userName = scanner.nextLine().trim();
            userId = reservationService.getUserId(userName);
        }

        while (!backToMainMenu) {
            System.out.println(USER_MENU);
            try {
                int choice = scanner.nextInt();
                scanner.nextLine();
                switch (choice) {
                    case 1 -> spaceService.printAvailableSpaces();
                    case 2 -> reservationService.makeReservation(userId);
                    case 3 -> reservationService.printUserReservations(userId);
                    case 4 -> reservationService.cancelReservation(userId);
                    case 5 -> backToMainMenu = true;
                    default -> System.out.println(CORRECT_VALUE_MSG);
                }
            } catch (InputMismatchException e) {
                System.out.println(CORRECT_VALUE_MSG);
                scanner.nextLine();
            }
        }
    }
}
