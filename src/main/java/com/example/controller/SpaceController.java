package com.example.controller;

import com.example.entity.Space;
import com.example.service.SpaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class SpaceController {

    @Autowired
    private SpaceService spaceService;

    @GetMapping("/spaces")
    public String getAllSpaces(Model model) {
        List<Space> spaces = spaceService.getAllSpaces();
        model.addAttribute("spaces", spaces);
        return "spaces";
    }

    @PostMapping("/spaces/create")
    public String createSpace(@RequestParam String type, @RequestParam float price, @RequestParam boolean status) {
        spaceService.createSpace(type, price, status);
        return "redirect:/spaces";
    }

    @PostMapping("/spaces/delete/{spaceId}")
    public String removeSpace(@PathVariable int spaceId, Model model) {
        try {
            spaceService.removeSpace(spaceId);
        } catch (IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return getAllSpaces(model);
        }
        return "redirect:/spaces";
    }
}
