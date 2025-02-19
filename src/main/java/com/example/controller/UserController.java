package com.example.controller;

import com.example.entity.Space;
import com.example.entity.Reservation;
import com.example.entity.User;
import com.example.service.SpaceService;
import com.example.service.ReservationService;
import com.example.service.UserService;
import com.example.repository.ReservationRepository;
import com.example.repository.SpaceRepository;
import com.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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

    private final SpaceService spaceService = SpaceService.getInstance();
    private final ReservationService reservationService = ReservationService.getInstance();

    @Autowired
    private UserService userService;

    @Autowired
    public UserController(SpaceRepository spaceRepository, ReservationRepository reservationRepository, UserRepository userRepository) {
        this.spaceService.setRepositories(spaceRepository, reservationRepository);
        this.reservationService.setRepositories(reservationRepository, spaceRepository, userRepository);
    }

    @GetMapping("/menu")
    public String userMenu(Model model) {
        model.addAttribute("userId", getCurrentUserId());
        return "user_menu";
    }

    @GetMapping("/spaces")
    public String viewUserSpaces(Model model) {
        List<Space> availableSpaces = spaceService.getAllSpaces().stream()
                .filter(Space::isStatus)
                .collect(Collectors.toList());
        model.addAttribute("spaces", availableSpaces);
        model.addAttribute("userId", getCurrentUserId());
        return "user_spaces";
    }

    @GetMapping("/reservations")
    public String manageUserReservations(Model model) {
        int userId = getCurrentUserId();
        List<Reservation> userReservations = reservationService.getUserReservations(userId);
        model.addAttribute("reservations", userReservations);
        model.addAttribute("userId", userId);
        return "user_reservations";
    }

    @PostMapping("/reservations/create")
    public String createUserReservation(@RequestParam int spaceId, @RequestParam String date, @RequestParam String startTime, @RequestParam String endTime, Model model) {
        int userId = getCurrentUserId();
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
        return "redirect:/user/reservations";
    }

    @PostMapping("/reservations/delete/{reservationId}")
    public String cancelUserReservation(@PathVariable int reservationId, Model model) {
        int userId = getCurrentUserId();
        reservationService.cancelReservation(reservationId);
        return "redirect:/user/reservations";
    }

    private int getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername()).orElseThrow();
        System.out.println("Current user ID: " + user.getId());
        System.out.println("Current user username: " + user.getUsername());
        return user.getId();
    }
}

