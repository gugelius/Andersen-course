package com.example.service;

import com.example.entity.Space;
import com.example.repository.ReservationRepository;
import com.example.repository.SpaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SpaceService {

    @Autowired
    private SpaceRepository spaceRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    public List<Space> getAllSpaces() {
        return spaceRepository.findAll();
    }

    public void createSpace(String type, float price, boolean status) {
        Space space = new Space();
        space.setType(type);
        space.setPrice(price);
        space.setStatus(status);
        spaceRepository.save(space);
    }

    public void removeSpace(int spaceId) {
        Optional<Space> space = spaceRepository.findById(spaceId);
        if (space.isPresent() && reservationRepository.existsBySpaceId(spaceId)) {
            throw new IllegalStateException("This space has associated reservations. Please remove the reservations first.");
        } else if (!space.isPresent()) {
            throw new IllegalArgumentException("Space with ID " + spaceId + " does not exist.");
        } else {
            spaceRepository.deleteById(spaceId);

        }
    }
}