package com.example.controller;

import com.example.entity.Reservation;
import com.example.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @GetMapping("/reservations")
    public String getAllReservations(Model model) {
        List<Reservation> reservations = reservationService.getAllReservations();
        model.addAttribute("reservations", reservations);
        return "reservations";
    }

    @PostMapping("/reservations/create")
    public String makeReservation(@RequestParam int userId, @RequestParam int spaceId, @RequestParam String date, @RequestParam String startTime, @RequestParam String endTime) {
        LocalDate reservationDate = LocalDate.parse(date);
        LocalTime reservationStartTime = LocalTime.parse(startTime);
        LocalTime reservationEndTime = LocalTime.parse(endTime);
        reservationService.makeReservation(userId, spaceId, reservationDate, reservationStartTime, reservationEndTime);
        return "redirect:/reservations";
    }

    @PostMapping("/reservations/delete/{reservationId}")
    public String cancelReservation(@PathVariable int reservationId) {
        reservationService.cancelReservation(reservationId);
        return "redirect:/reservations";
    }
}
