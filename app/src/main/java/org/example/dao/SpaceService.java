package org.example.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.example.entity.CoworkingSpace;
import org.example.inputProvider.InputProvider;

public class SpaceService {
    private static final String GET_ALL_SPACES = "SELECT * FROM spaces";
    private static final String CREATE_SPACE = "INSERT INTO spaces (space_type, space_price, space_availability) VALUES (?, ?, ?)";
    private static final String HAS_RESERVATIONS = "SELECT COUNT(*) FROM reservations WHERE space_id = ?";
    private static final String REMOVE_SPACE = "DELETE FROM spaces WHERE space_id = ?";
    private final InputProvider inputProvider;
    public static final List<CoworkingSpace> spaces = new ArrayList<>();
    private static final ConnectionPool connectionPool = ConnectionPool.getInstance();
    public SpaceService(InputProvider inputProvider) {this.inputProvider = inputProvider;}
    public List<CoworkingSpace> getSpaces() {
        return spaces;
    }
    private boolean AvailableSpacesExist(){
        return spaces.stream()
                .anyMatch(CoworkingSpace::isStatus);
    }
    public void getAllSpaces() {
        spaces.clear();
        try (Connection connection = connectionPool.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(GET_ALL_SPACES)) {
            while (resultSet.next()) {
                int id = resultSet.getInt("space_id");
                String type = resultSet.getString("space_type");
                float price = resultSet.getFloat("space_price");
                boolean status = resultSet.getBoolean("space_availability");
                spaces.add(new CoworkingSpace(id, type, price, status));
            }
        } catch (SQLException e) {
            System.out.println("Error while loading spaces: " + e.getMessage());
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

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     CREATE_SPACE,
                     Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, type);
            preparedStatement.setFloat(2, price);
            preparedStatement.setBoolean(3, status);
            preparedStatement.executeUpdate();

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    spaces.add(new CoworkingSpace(id, type, price, status));
                }
            }

            System.out.println("Coworking space added.");
        } catch (SQLException e) {
            System.out.println("Error while adding space: " + e.getMessage());
        }
    }
    public static boolean spaceExists(int id) {
        return spaces.stream()
                .anyMatch(space -> Optional.ofNullable(space)
                        .map(s -> s.getId() == id)
                        .orElse(false));
    }
    public static boolean isSpacesEmpty(){
        return spaces.size() == 0;
    }
    public static boolean isSpaceAvailable(int id) {
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
    private boolean hasReservations(int spaceId) {
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     HAS_RESERVATIONS)) {
            preparedStatement.setInt(1, spaceId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                resultSet.next();
                return resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error while checking reservations: " + e.getMessage());
            return false;
        }
    }
    public void removeSpace() {
        if (isSpacesEmpty()) {
            System.out.println("There are no existing coworking spaces!");
            return;
        }
        System.out.println("Enter space ID to remove:");
        int id = inputProvider.nextInt();
        inputProvider.nextLine();
        while (!spaceExists(id)) {
            System.out.println("Space with this ID does not exist.");
            id = inputProvider.nextInt();
            inputProvider.nextLine();
        }
        if (hasReservations(id)) {
            System.out.println("Space with this ID has existing reservations. Please remove related reservations first.");
            return;
        }
        try (Connection connection = connectionPool.getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement preparedStatement = connection.prepareStatement(
                    REMOVE_SPACE)) {
                preparedStatement.setInt(1, id);
                preparedStatement.executeUpdate();
                int finalId = id;
                spaces.removeIf(space -> space.getId() == finalId);
            }
            connection.commit();
            System.out.println("Coworking space removed.");
        } catch (SQLException e) {
            System.out.println("Error while removing space: " + e.getMessage());
            try (Connection connection = connectionPool.getConnection()) {
                connection.rollback();
            } catch (SQLException rollbackException) {
                System.out.println("Error while rolling back transaction: " + rollbackException.getMessage());
            }
        }
    }
}
