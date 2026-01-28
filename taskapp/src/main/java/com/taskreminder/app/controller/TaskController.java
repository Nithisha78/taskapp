package com.taskreminder.app.controller;

import com.taskreminder.app.entity.Task;
import com.taskreminder.app.entity.User;
import com.taskreminder.app.enums.TaskPriority;
import com.taskreminder.app.enums.TaskStatus;
import com.taskreminder.app.service.EmailService;
import com.taskreminder.app.service.TaskService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private EmailService emailService;

    private User getLoggedUser(HttpSession session) {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null) {
            user = (User) session.getAttribute("user");
        }
        return user;
    }

    @GetMapping
    public String listTasks(
            @RequestParam(defaultValue = "0") int page,
            Model model,
            HttpSession session) {

        User user = getLoggedUser(session);
        if (user == null) return "redirect:/login";

        int pageSize = 10;
        Page<Task> taskPage =
                taskService.getTasksByUserPaged(user, page, pageSize);

        model.addAttribute("tasks", taskPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", taskPage.getTotalPages());

        return "tasks";
    }

    @GetMapping("/form")
    public String addTaskForm(Model model, HttpSession session) {

        if (getLoggedUser(session) == null) return "redirect:/login";

        model.addAttribute("task", new Task());
        model.addAttribute("statuses", TaskStatus.values());
        model.addAttribute("priorities", TaskPriority.values());
        return "add-task";
    }

    @PostMapping("/add")
    public String addTask(Task task, RedirectAttributes ra, HttpSession session) {

        User user = getLoggedUser(session);
        if (user == null) return "redirect:/login";

        task.setUser(user);
        taskService.addTask(task);

        ra.addFlashAttribute("success", "Task added successfully");
        return "redirect:/tasks";
    }

    @GetMapping("/edit/{id}")
    public String editTask(@PathVariable Long id, Model model, HttpSession session) {

        User user = getLoggedUser(session);
        if (user == null) return "redirect:/login";

        Task task = taskService.getTaskById(id);
        if (task == null || !task.getUser().getId().equals(user.getId())) {
            return "redirect:/tasks";
        }

        model.addAttribute("task", task);
        model.addAttribute("statuses", TaskStatus.values());
        model.addAttribute("priorities", TaskPriority.values());
        return "edit-task";
    }

    @PostMapping("/update")
    public String updateTask(Task task, RedirectAttributes ra, HttpSession session) {

        User user = getLoggedUser(session);
        if (user == null) return "redirect:/login";

        Task existing = taskService.getTaskById(task.getId());
        if (existing == null || !existing.getUser().getId().equals(user.getId())) {
            return "redirect:/tasks";
        }

        task.setUser(user);
        taskService.updateTask(task);

        ra.addFlashAttribute("success", "Task updated successfully");
        return "redirect:/tasks";
    }

    @GetMapping("/delete/{id}")
    public String deleteTask(@PathVariable Long id, RedirectAttributes ra, HttpSession session) {

        User user = getLoggedUser(session);
        if (user == null) return "redirect:/login";

        Task task = taskService.getTaskById(id);
        if (task == null || !task.getUser().getId().equals(user.getId())) {
            return "redirect:/tasks";
        }

        taskService.deleteTask(id);
        ra.addFlashAttribute("success", "Task deleted successfully");
        return "redirect:/tasks";
    }

    @GetMapping("/complete/{id}")
    public String completeTask(@PathVariable Long id, RedirectAttributes ra, HttpSession session) {

        User user = getLoggedUser(session);
        if (user == null) return "redirect:/login";

        Task task = taskService.getTaskById(id);
        if (task == null || !task.getUser().getId().equals(user.getId())) {
            return "redirect:/tasks";
        }

        taskService.completeTask(id);
        ra.addFlashAttribute("success", "Task marked as completed");
        return "redirect:/tasks";
    }

    @GetMapping("/search")
    public String search(@RequestParam String keyword, Model model, HttpSession session) {

        User user = getLoggedUser(session);
        if (user == null) return "redirect:/login";

        model.addAttribute("tasks", taskService.searchByTitle(keyword, user));
        model.addAttribute("currentPage", 0);
        model.addAttribute("totalPages", 1);
        return "tasks";
    }

    @GetMapping("/filter/status")
    public String filterByStatus(@RequestParam String status, Model model, HttpSession session) {

        User user = getLoggedUser(session);
        if (user == null) return "redirect:/login";

        TaskStatus taskStatus = TaskStatus.valueOf(status.toUpperCase());
        model.addAttribute("tasks", taskService.filterByStatus(taskStatus, user));
        model.addAttribute("currentPage", 0);
        model.addAttribute("totalPages", 1);
        return "tasks";
    }

    @GetMapping("/filter/priority")
    public String filterByPriority(@RequestParam String priority, Model model, HttpSession session) {

        User user = getLoggedUser(session);
        if (user == null) return "redirect:/login";

        TaskPriority taskPriority = TaskPriority.valueOf(priority.toUpperCase());
        model.addAttribute("tasks", taskService.filterByPriority(taskPriority, user));
        model.addAttribute("currentPage", 0);
        model.addAttribute("totalPages", 1);
        return "tasks";
    }

    @GetMapping("/view/{id}")
    public String viewTask(@PathVariable Long id, Model model, HttpSession session) {

        User user = getLoggedUser(session);
        if (user == null) return "redirect:/login";

        Task task = taskService.getTaskById(id);
        if (task == null || !task.getUser().getId().equals(user.getId())) {
            return "redirect:/tasks";
        }

        model.addAttribute("task", task);
        return "view-task";
    }

    @GetMapping("/export")
    public void exportTasksToCsv(HttpServletResponse response, HttpSession session) throws IOException {

        User user = getLoggedUser(session);
        if (user == null) {
            response.sendRedirect("/login");
            return;
        }

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=tasks.csv");

        List<Task> tasks = taskService.getTasksByUser(user);
        PrintWriter writer = response.getWriter();

        writer.println("ID,Title,Description,Status,Priority,Due Date");

        for (Task task : tasks) {
            writer.println(
                    task.getId() + "," +
                            "\"" + task.getTitle() + "\"," +
                            "\"" + task.getDescription() + "\"," +
                            task.getStatus() + "," +
                            task.getPriority() + "," +
                            task.getDueDate()
            );
        }

        writer.flush();
    }

    @GetMapping("/export/email")
    public String exportCsvToEmail(HttpSession session) throws Exception {

        User user = getLoggedUser(session);
        if (user == null) return "redirect:/login";

        List<Task> tasks = taskService.getTasksByUser(user);

        File csvFile = File.createTempFile("tasks-", ".csv");
        PrintWriter writer = new PrintWriter(csvFile);

        writer.println("ID,Title,Description,Status,Priority,Due Date");

        for (Task task : tasks) {
            writer.println(
                    task.getId() + "," +
                            "\"" + task.getTitle() + "\"," +
                            "\"" + task.getDescription() + "\"," +
                            task.getStatus() + "," +
                            task.getPriority() + "," +
                            task.getDueDate()
            );
        }

        writer.close();

        emailService.sendCsvEmail(
                user.getEmail(),
                "Your Tasks CSV",
                "Please find attached your tasks CSV file.",
                csvFile
        );

        return "redirect:/tasks";
    }
}
