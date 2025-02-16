package org.example.dao;

import org.example.entity.Reservation;
import org.example.inputProvider.InputProvider;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class ReservationService {
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("org.example");
    private EntityManager em = emf.createEntityManager();
    private final InputProvider inputProvider;

    public ReservationService(InputProvider inputProvider) {
        this.inputProvider = inputProvider;
    }

    public List<Reservation> getAllReservations() {
        TypedQuery<Reservation> query = em.createQuery("SELECT r FROM Reservation r", Reservation.class);
        return query.getResultList();
    }

    public void printAllReservations() {
        List<Reservation> reservations = getAllReservations();
        if (reservations.isEmpty()) {
            System.out.println("No reservations found.");
        } else {
            reservations.forEach(System.out::println);
        }
    }

    public List<Reservation> getUserReservations(int userId) {
        TypedQuery<Reservation> query = em.createQuery("SELECT r FROM Reservation r WHERE r.userId = :userId", Reservation.class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }

    public void printUserReservations(int userId) {
        List<Reservation> reservations = getUserReservations(userId);
        if (reservations.isEmpty()) {
            System.out.println("No reservations found for user.");
        } else {
            reservations.forEach(System.out::println);
        }
    }

    public void makeReservation(int userId) {
        System.out.println("Enter space ID:");
        int spaceId = inputProvider.nextInt();
        inputProvider.nextLine();

        System.out.println("Enter date (YYYY-MM-DD):");
        LocalDate date = LocalDate.parse(inputProvider.nextLine());

        System.out.println("Enter start time (HH:MM):");
        LocalTime startTime = LocalTime.parse(inputProvider.nextLine());

        System.out.println("Enter end time (HH:MM):");
        LocalTime endTime = LocalTime.parse(inputProvider.nextLine());

        // Проверка на доступность
        TypedQuery<Reservation> query = em.createQuery("SELECT r FROM Reservation r WHERE r.spaceId = :spaceId AND r.date = :date AND ((r.startTime <= :endTime AND r.endTime >= :startTime) OR (r.startTime <= :startTime AND r.endTime >= :endTime))", Reservation.class);
        query.setParameter("spaceId", spaceId);
        query.setParameter("date", date);
        query.setParameter("startTime", startTime);
        query.setParameter("endTime", endTime);
        List<Reservation> existingReservations = query.getResultList();

        if (!existingReservations.isEmpty()) {
            System.out.println("The space is already reserved for the selected time.");
        } else {
            em.getTransaction().begin();
            Reservation reservation = new Reservation(userId, spaceId, date, startTime, endTime);
            em.persist(reservation);
            em.getTransaction().commit();
            System.out.println("Reservation made.");
        }
    }

    public void cancelReservation(int userId) {
        List<Reservation> userReservations = getUserReservations(userId);
        if (userReservations.isEmpty()) {
            System.out.println("No reservations found for user to cancel.");
            return;
        }

        System.out.println("Enter reservation ID to cancel:");
        int reservationId = inputProvider.nextInt();
        inputProvider.nextLine();

        em.getTransaction().begin();
        TypedQuery<Reservation> query = em.createQuery("SELECT r FROM Reservation r WHERE r.id = :reservationId AND r.userId = :userId", Reservation.class);
        query.setParameter("reservationId", reservationId);
        query.setParameter("userId", userId);
        List<Reservation> result = query.getResultList();
        if (!result.isEmpty()) {
            em.remove(result.get(0));
            System.out.println("Reservation canceled.");
        } else {
            System.out.println("Reservation with this ID does not exist for the specified user.");
        }
        em.getTransaction().commit();
    }

    public int getUserId(String userName) {
        TypedQuery<Integer> query = em.createQuery("SELECT u.id FROM User u WHERE u.name = :userName", Integer.class);
        query.setParameter("userName", userName);
        List<Integer> result = query.getResultList();
        return result.isEmpty() ? 0 : result.get(0);
    }
}
