package com.example.controller;

import com.example.entity.Space;
import com.example.entity.Reservation;
import com.example.service.SpaceService;
import com.example.service.ReservationService;
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

    @Autowired
    private SpaceService SpaceService;

    @Autowired
    private ReservationService reservationService;

    @GetMapping("/spaces")
    public String getAllSpaces(Model model) {
        List<Space> spaces = SpaceService.getAllSpaces();
        model.addAttribute("spaces", spaces);
        return "spaces";
    }

    @PostMapping("/spaces/create")
    public String createSpace(@RequestParam String type, @RequestParam float price, @RequestParam(required = false) String status, Model model) {
        List<Space> spaces = SpaceService.getAllSpaces();
        model.addAttribute("spaces", spaces);

        if (type.isEmpty() || price <= 0) {
            model.addAttribute("errorMessage", "Invalid input: type must not be empty and price must be greater than 0.");
            return "spaces";
        }

        boolean isStatus = status != null && status.equals("true");
        SpaceService.createSpace(type, price, isStatus);
        return "redirect:/admin/spaces";
    }

    @PostMapping("/spaces/delete/{spaceId}")
    public String removeSpace(@PathVariable int spaceId) {
        SpaceService.removeSpace(spaceId);
        return "redirect:/admin/spaces";
    }

    @GetMapping("/reservations")
    public String getAllReservations(Model model) {
        List<Reservation> reservations = reservationService.getAllReservations();
        model.addAttribute("reservations", reservations);
        return "reservations";
    }

    @PostMapping("/reservations/create")
    public String makeReservation(@RequestParam int userId, @RequestParam int spaceId, @RequestParam String date, @RequestParam String startTime, @RequestParam String endTime, Model model) {
        List<Reservation> reservations = reservationService.getAllReservations();
        model.addAttribute("reservations", reservations);

        if (userId <= 0 || spaceId <= 0 || date.isEmpty() || startTime.isEmpty() || endTime.isEmpty()) {
            model.addAttribute("errorMessage", "Invalid input: all fields are required and must be valid.");
            return "reservations";
        }

        try {
            LocalDate reservationDate = LocalDate.parse(date);
            LocalTime reservationStartTime = LocalTime.parse(startTime);
            LocalTime reservationEndTime = LocalTime.parse(endTime);
            reservationService.makeReservation(userId, spaceId, reservationDate, reservationStartTime, reservationEndTime);
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "reservations";
        }
        return "redirect:/admin/reservations";
    }

    @PostMapping("/reservations/delete/{reservationId}")
    public String cancelReservation(@PathVariable int reservationId) {
        reservationService.cancelReservation(reservationId);
        return "redirect:/admin/reservations";
    }
}
