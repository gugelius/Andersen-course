package org.example.dao;

import org.example.entity.CoworkingSpace;
import org.example.inputProvider.InputProvider;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import java.util.List;

public class SpaceService {
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("org.example");
    private EntityManager em = emf.createEntityManager();
    private final InputProvider inputProvider;

    public SpaceService(InputProvider inputProvider) {
        this.inputProvider = inputProvider;
    }

    public List<CoworkingSpace> getAllSpaces() {
        return em.createQuery("SELECT s FROM CoworkingSpace s", CoworkingSpace.class).getResultList();
    }

    public void createSpace() {
        String type;
        do {
            System.out.println("Enter space type:");
            type = inputProvider.nextLine().trim();
            if (type.isEmpty()) {
                System.out.println("Space type cannot be empty. Please enter a valid space type.");
            }
        } while (type.isEmpty());

        float price;
        do {
            System.out.println("Enter space price:");
            price = inputProvider.nextFloat();
            inputProvider.nextLine(); // Чистим ввод
            if (price <= 0) {
                System.out.println("Space price must be positive. Please enter a valid space price.");
            }
        } while (price <= 0);

        System.out.println("Enter space availability (true/false):");
        boolean status = inputProvider.nextBoolean();
        inputProvider.nextLine(); // Чистим ввод

        em.getTransaction().begin();
        CoworkingSpace space = new CoworkingSpace(type, price, status);
        em.persist(space);
        em.getTransaction().commit();
        System.out.println("Coworking space added.");
    }

    public void removeSpace() {
        System.out.println("Enter space ID to remove:");
        int spaceId = inputProvider.nextInt();
        inputProvider.nextLine(); // Чистим ввод

        CoworkingSpace space = em.find(CoworkingSpace.class, spaceId);
        if (space != null) {
            em.getTransaction().begin();
            em.remove(space);
            em.getTransaction().commit();
            System.out.println("Coworking space removed.");
        } else {
            System.out.println("Coworking space with this ID does not exist.");
        }
    }
    public void printAvailableSpaces() {
        TypedQuery<CoworkingSpace> query = em.createQuery("SELECT s FROM CoworkingSpace s WHERE s.status = true", CoworkingSpace.class);
        List<CoworkingSpace> spaces = query.getResultList();
        if (spaces.isEmpty()) {
            System.out.println("No available coworking spaces found.");
        } else {
            spaces.forEach(System.out::println);
        }
    }
}
