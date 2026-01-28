package com.taskreminder.app.controller;

import com.taskreminder.app.entity.User;
import com.taskreminder.app.repository.UserRepository;
import com.taskreminder.app.service.EmailService;
import com.taskreminder.app.dto.OtpRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;


    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            RedirectAttributes ra) {

        User user = userRepository.findByEmail(email);


        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            ra.addFlashAttribute("error", "Invalid email or password");
            return "redirect:/login";
        }

        if (user.getVerified() == null || !user.getVerified()) {
            ra.addFlashAttribute("error", "Please verify your email first");
            return "redirect:/login";
        }

        session.setAttribute("loggedUser", user);
        session.setAttribute("username", user.getUsername());
        session.setAttribute("firstLogin", user.getFirstLogin());

        if (user.getFirstLogin()) {
            user.setFirstLogin(false);
            userRepository.save(user);
        }

        ra.addFlashAttribute("success", "Logged in successfully");
        return "redirect:/tasks";
    }


    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String register(User user, RedirectAttributes ra) {

        if (userRepository.findByEmail(user.getEmail()) != null) {
            ra.addFlashAttribute("error", "Email already exists");
            return "redirect:/register";
        }


        user.setPassword(passwordEncoder.encode(user.getPassword()));

        String otp = String.valueOf((int) (Math.random() * 900000) + 100000);

        user.setOtp(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(5));
        user.setVerified(false);
        user.setFirstLogin(true);

        userRepository.save(user);

        emailService.sendEmail(
                user.getEmail(),
                "Email Verification OTP",
                "Your OTP is: " + otp + "\nValid for 5 minutes."
        );

        ra.addFlashAttribute("email", user.getEmail());
        return "redirect:/verify";
    }



    @GetMapping("/verify")
    public String verifyPage() {
        return "verify";
    }

    @PostMapping("/verify-otp")
    public String verifyOtp(OtpRequest request, RedirectAttributes ra) {

        User user = userRepository.findByEmail(request.getEmail());

        if (user != null &&
                user.getOtp() != null &&
                user.getOtp().equals(request.getOtp()) &&
                user.getOtpExpiry().isAfter(LocalDateTime.now())) {

            user.setVerified(true);
            user.setOtp(null);
            user.setOtpExpiry(null);
            userRepository.save(user);

            ra.addFlashAttribute("success", "Email verified successfully");
            return "redirect:/login";
        }

        ra.addFlashAttribute("error", "Invalid or expired OTP");
        return "redirect:/verify";
    }



    @PostMapping("/resend-otp")
    public String resendOtp(@RequestParam String email, RedirectAttributes ra) {

        User user = userRepository.findByEmail(email);

        if (user == null) {
            ra.addFlashAttribute("error", "Email not found");
            return "redirect:/verify";
        }

        String newOtp = String.valueOf((int) (Math.random() * 900000) + 100000);

        user.setOtp(newOtp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(5));
        userRepository.save(user);

        emailService.sendEmail(
                user.getEmail(),
                "Resent OTP",
                "Your new OTP is: " + newOtp + "\nValid for 5 minutes."
        );

        ra.addFlashAttribute("email", email);
        return "redirect:/verify";
    }



    @PostMapping("/update-username")
    public String updateUsername(@RequestParam String username, HttpSession session) {

        User user = (User) session.getAttribute("loggedUser");
        if (user != null) {
            user.setUsername(username);
            userRepository.save(user);
            session.setAttribute("username", username);
        }
        return "redirect:/tasks";
    }



    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes ra) {
        session.invalidate();
        ra.addFlashAttribute("success", "Logged out successfully");
        return "redirect:/";
    }
}
