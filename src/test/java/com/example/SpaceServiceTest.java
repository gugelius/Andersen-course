package com.example;

import com.example.entity.Space;
import com.example.repository.ReservationRepository;
import com.example.repository.SpaceRepository;
import com.example.service.SpaceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class SpaceServiceTest {

    @Mock
    private SpaceRepository spaceRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private SpaceService spaceService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void removeSpace_ShouldThrowIllegalStateException_WhenSpaceHasAssociatedReservations() {
        int spaceId = 1;
        when(spaceRepository.findById(spaceId)).thenReturn(Optional.of(new Space()));
        when(reservationRepository.existsBySpaceId(spaceId)).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> spaceService.removeSpace(spaceId));
    }

    @Test
    void removeSpace_ShouldThrowIllegalArgumentException_WhenSpaceDoesNotExist() {
        int spaceId = 1;
        when(spaceRepository.findById(spaceId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> spaceService.removeSpace(spaceId));
    }

    @Test
    void removeSpace_ShouldDeleteSpace_WhenNoAssociatedReservations() {
        int spaceId = 1;
        Space space = new Space();
        when(spaceRepository.findById(spaceId)).thenReturn(Optional.of(space));
        when(reservationRepository.existsBySpaceId(spaceId)).thenReturn(false);

        spaceService.removeSpace(spaceId);

        verify(spaceRepository, times(1)).deleteById(spaceId);
    }
}
