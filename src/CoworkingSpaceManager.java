import java.util.ArrayList;
import java.util.List;

public class CoworkingSpaceManager {
    private final List<CoworkingSpace> spaces = new ArrayList<>();
    private final List<Reservation> reservations = new ArrayList<>();
    private int reservationIdCounter = 1, coworkingSpaceIdCounter = 1;

    public void createSpace(String type, float price, boolean status) {
        spaces.add(new CoworkingSpace(coworkingSpaceIdCounter++, type, price, status));
    }

    public void removeSpace(int id) {
        spaces.removeIf(space -> space.getId() == id);
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

    public void makeReservation(String userName, int spaceId, String date, String startTime, String endTime) {
        reservations.add(new Reservation(reservationIdCounter++, userName, spaceId, date, startTime, endTime));
    }

    public void cancelReservation(int reservationId) {
        reservations.removeIf(reservation -> reservation.getId() == reservationId);
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
