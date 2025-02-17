package com.example.controller;

import com.example.entity.Space;
import com.example.entity.Reservation;
import com.example.service.SpaceService;
import com.example.service.ReservationService;
import com.example.repository.SpaceRepository;
import com.example.repository.ReservationRepository;
import com.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private SpaceService spaceService;
    private ReservationService reservationService;

    @Autowired
    public AdminController(SpaceRepository spaceRepository, ReservationRepository reservationRepository, UserRepository userRepository) {
        this.spaceService = SpaceService.getInstance();
        this.spaceService.setRepositories(spaceRepository, reservationRepository);

        this.reservationService = ReservationService.getInstance();
        this.reservationService.setRepositories(reservationRepository, spaceRepository, userRepository);
    }

    public void setSpaceService(SpaceService spaceService) {
        this.spaceService = spaceService;
    }

    public void setReservationService(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping("/spaces")
    public String getAllSpaces(Model model) {
        List<Space> spaces = spaceService.getAllSpaces();
        model.addAttribute("spaces", spaces);
        return "admin/spaces";
    }

    @PostMapping("/spaces/create")
    public String createSpace(@RequestParam String type, @RequestParam float price, @RequestParam(required = false) String status, Model model) {
        if (type.isEmpty() || price <= 0) {
            model.addAttribute("errorMessage", "Invalid input: type must not be empty and price must be greater than 0.");
            return getAllSpaces(model);
        }

        boolean isStatus = "on".equals(status);
        spaceService.createSpace(type, price, isStatus);
        return "redirect:/admin/spaces";
    }

    @PostMapping("/spaces/delete/{spaceId}")
    public String removeSpace(@PathVariable int spaceId, Model model) {
        try {
            spaceService.removeSpace(spaceId);
        } catch (IllegalStateException e) {
            model.addAttribute("errorMessage", "Cannot delete space with associated reservations. Please remove the reservations first.");
            return getAllSpaces(model);
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return getAllSpaces(model);
        }
        return "redirect:/admin/spaces";
    }

    @GetMapping("/reservations")
    public String getAllReservations(Model model) {
        List<Reservation> reservations = reservationService.getAllReservations();
        model.addAttribute("reservations", reservations);
        return "admin/reservations";
    }

    @PostMapping("/reservations/create")
    public String makeReservation(@RequestParam int userId, @RequestParam int spaceId, @RequestParam String date, @RequestParam String startTime, @RequestParam String endTime, Model model) {
        if (userId <= 0 || spaceId <= 0 || date.isEmpty() || startTime.isEmpty() || endTime.isEmpty()) {
            model.addAttribute("errorMessage", "Invalid input: all fields are required and must be valid.");
            return getAllReservations(model);
        }

        try {
            LocalDate reservationDate = LocalDate.parse(date);
            LocalTime reservationStartTime = LocalTime.parse(startTime);
            LocalTime reservationEndTime = LocalTime.parse(endTime);
            reservationService.makeReservation(userId, spaceId, reservationDate, reservationStartTime, reservationEndTime);
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return getAllReservations(model);
        }
        return "redirect:/admin/reservations";
    }

    @PostMapping("/reservations/delete/{reservationId}")
    public String cancelReservation(@PathVariable int reservationId) {
        reservationService.cancelReservation(reservationId);
        return "redirect:/admin/reservations";
    }
}