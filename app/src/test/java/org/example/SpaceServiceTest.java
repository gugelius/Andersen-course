package org.example;

import org.example.dao.SpaceService;
import org.example.entity.CoworkingSpace;
import org.example.inputProvider.InputProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SpaceServiceTest {

    private SpaceService spaceService;
    private InputProvider mockInputProvider;
    private EntityManagerFactory emf;
    private EntityManager em;
    private EntityTransaction transaction;

    @BeforeEach
    void setUp() {
        mockInputProvider = Mockito.mock(InputProvider.class);
        emf = Mockito.mock(EntityManagerFactory.class);
        em = Mockito.mock(EntityManager.class);
        transaction = Mockito.mock(EntityTransaction.class);

        when(emf.createEntityManager()).thenReturn(em);
        when(em.getTransaction()).thenReturn(transaction);
        spaceService = new SpaceService(mockInputProvider);
    }

    @Test
    void testGetAllSpaces() {
        when(em.createQuery(anyString(), eq(CoworkingSpace.class)))
                .thenAnswer(invocation -> {
                    TypedQuery<CoworkingSpace> query = mock(TypedQuery.class);
                    when(query.getResultList()).thenReturn(Collections.singletonList(new CoworkingSpace("Office", 100, true)));
                    return query;
                });

        List<CoworkingSpace> spaces = spaceService.getAllSpaces();
        assertEquals(1, spaces.size());
        assertEquals("Office", spaces.get(0).getType());
    }

    @Test
    void testCreateSpace() {
        when(mockInputProvider.nextLine()).thenReturn("Private Office");
        when(mockInputProvider.nextFloat()).thenReturn(500f);
        when(mockInputProvider.nextBoolean()).thenReturn(true);

        spaceService.createSpace();

        verify(transaction).begin();
        verify(em).persist(any(CoworkingSpace.class));
        verify(transaction).commit();
    }

    @Test
    void testRemoveSpace() {
        CoworkingSpace space = new CoworkingSpace("Office", 100, true);
        when(em.find(CoworkingSpace.class, space.getId())).thenReturn(space);
        when(mockInputProvider.nextInt()).thenReturn(space.getId());

        spaceService.removeSpace();

        verify(transaction).begin();
        verify(em).remove(any(CoworkingSpace.class));
        verify(transaction).commit();
    }
}
