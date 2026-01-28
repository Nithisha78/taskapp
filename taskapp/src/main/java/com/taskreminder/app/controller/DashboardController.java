package com.taskreminder.app.controller;

import com.taskreminder.app.entity.Task;
import com.taskreminder.app.entity.User;
import com.taskreminder.app.enums.TaskPriority;
import com.taskreminder.app.enums.TaskStatus;
import com.taskreminder.app.service.TaskService;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private TaskService taskService;

    private User getLoggedUser(HttpSession session) {
        return (User) session.getAttribute("loggedUser");
    }

    @GetMapping
    public String dashboard(Model model, HttpSession session) {

        User user = getLoggedUser(session);
        if (user == null) return "redirect:/login";

        List<Task> allTasks = taskService.getTasksByUser(user);

        long totalTasks = allTasks.size();

        long completedTasks = allTasks.stream()
                .filter(t -> t.getStatus() == TaskStatus.COMPLETED)
                .count();

        long pendingTasks = allTasks.stream()
                .filter(t -> t.getStatus() != TaskStatus.COMPLETED)
                .count();

        long highPriorityTasks = allTasks.stream()
                .filter(t -> t.getPriority() == TaskPriority.HIGH)
                .count();

        long dueTodayTasks = taskService.getDueTodayTasks(user).size();
        long overdueTasks = taskService.getOverdueTasks(user).size();

        List<Task> recentTasks = allTasks.stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .limit(5)
                .collect(Collectors.toList());

        model.addAttribute("totalTasks", totalTasks);
        model.addAttribute("completedTasks", completedTasks);
        model.addAttribute("pendingTasks", pendingTasks);
        model.addAttribute("highPriorityTasks", highPriorityTasks);
        model.addAttribute("dueTodayTasks", dueTodayTasks);
        model.addAttribute("overdueTasks", overdueTasks);
        model.addAttribute("recentTasks", recentTasks);

        return "dashboard";
    }
}
