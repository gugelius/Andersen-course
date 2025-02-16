package com.example.controller;

import com.example.entity.Space;
import com.example.entity.Reservation;
import com.example.service.SpaceService;
import com.example.service.ReservationService;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private SpaceService SpaceService;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private UserService userService;

    @GetMapping("/menu")
    public String userMenu(@RequestParam int userId, Model model) {
        model.addAttribute("userId", userId);
        return "user_menu";
    }

    @GetMapping("/spaces")
    public String viewUserSpaces(@RequestParam int userId, Model model) {
        List<Space> availableSpaces = SpaceService.getAllSpaces().stream()
                .filter(Space::isStatus)
                .collect(Collectors.toList());
        model.addAttribute("spaces", availableSpaces);
        model.addAttribute("userId", userId);
        return "user_spaces";
    }

    @GetMapping("/reservations")
    public String manageUserReservations(@RequestParam int userId, Model model) {
        List<Reservation> userReservations = reservationService.getUserReservations(userId);
        model.addAttribute("reservations", userReservations);
        model.addAttribute("userId", userId);
        return "user_reservations";
    }

    @PostMapping("/reservations/create")
    public String createUserReservation(@RequestParam int userId, @RequestParam int spaceId, @RequestParam String date, @RequestParam String startTime, @RequestParam String endTime, Model model) {
        List<Reservation> userReservations = reservationService.getUserReservations(userId);
        model.addAttribute("reservations", userReservations);
        model.addAttribute("userId", userId);

        try {
            LocalDate reservationDate = LocalDate.parse(date);
            LocalTime reservationStartTime = LocalTime.parse(startTime);
            LocalTime reservationEndTime = LocalTime.parse(endTime);
            reservationService.makeReservation(userId, spaceId, reservationDate, reservationStartTime, reservationEndTime);
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "user_reservations";
        }
        return "redirect:/user/reservations?userId=" + userId;
    }

    @PostMapping("/reservations/delete/{reservationId}")
    public String cancelUserReservation(@RequestParam int userId, @PathVariable int reservationId) {
        reservationService.cancelReservation(reservationId);
        return "redirect:/user/reservations?userId=" + userId;
    }
}
