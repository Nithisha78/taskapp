package com.taskreminder.app.controller;

import com.taskreminder.app.entity.User;
import com.taskreminder.app.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private UserRepository userRepository;

    private User getLoggedUser(HttpSession session) {
        return (User) session.getAttribute("loggedUser");
    }

    @GetMapping
    public String profile(Model model, HttpSession session) {
        User user = getLoggedUser(session);
        if (user == null) return "redirect:/login";

        model.addAttribute("user", user);
        return "profile";
    }

    @PostMapping("/update")
    public String updateProfile(
            @RequestParam String username,
            @RequestParam(required = false) MultipartFile profilePic,
            HttpSession session
    ) {

        User user = getLoggedUser(session);
        if (user == null) return "redirect:/login";

        try {

            user.setUsername(username);


            if (profilePic != null && !profilePic.isEmpty()) {


                String uploadDir = System.getProperty("user.dir") + "/uploads/profile-images/";
                File directory = new File(uploadDir);

                if (!directory.exists()) {
                    directory.mkdirs();
                }

                String fileName = UUID.randomUUID() + "_" + profilePic.getOriginalFilename();
                File file = new File(uploadDir + fileName);

                profilePic.transferTo(file);

                user.setProfileImage(fileName);
            }

            userRepository.save(user);


            session.setAttribute("loggedUser", user);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "redirect:/profile";
    }
}
