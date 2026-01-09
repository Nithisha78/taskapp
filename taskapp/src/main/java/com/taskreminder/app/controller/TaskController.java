package com.taskreminder.app.controller;

import com.taskreminder.app.entity.Task;
import com.taskreminder.app.entity.User;
import com.taskreminder.app.enums.TaskPriority;
import com.taskreminder.app.enums.TaskStatus;
import com.taskreminder.app.service.TaskService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    private User getLoggedUser(HttpSession session) {
        return (User) session.getAttribute("loggedUser");
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

        model.addAttribute("pendingCount",
                taskService.filterByStatus(TaskStatus.PENDING, user).size());

        model.addAttribute("inProgressCount",
                taskService.filterByStatus(TaskStatus.IN_PROGRESS, user).size());

        model.addAttribute("doneCount",
                taskService.filterByStatus(TaskStatus.COMPLETED, user).size());

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

        if (getLoggedUser(session) == null) return "redirect:/login";

        model.addAttribute("task", taskService.getTaskById(id));
        model.addAttribute("statuses", TaskStatus.values());
        model.addAttribute("priorities", TaskPriority.values());
        return "edit-task";
    }

    @PostMapping("/update")
    public String updateTask(Task task, RedirectAttributes ra, HttpSession session) {

        if (getLoggedUser(session) == null) return "redirect:/login";

        taskService.updateTask(task);
        ra.addFlashAttribute("success", "Task updated successfully");
        return "redirect:/tasks";
    }

    @GetMapping("/delete/{id}")
    public String deleteTask(@PathVariable Long id, RedirectAttributes ra, HttpSession session) {

        if (getLoggedUser(session) == null) return "redirect:/login";

        taskService.deleteTask(id);
        ra.addFlashAttribute("success", "Task deleted successfully");
        return "redirect:/tasks";
    }

    @GetMapping("/complete/{id}")
    public String completeTask(@PathVariable Long id, RedirectAttributes ra, HttpSession session) {

        if (getLoggedUser(session) == null) return "redirect:/login";

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

        if (getLoggedUser(session) == null) return "redirect:/login";

        model.addAttribute("task", taskService.getTaskById(id));
        return "view-task";
    }
}
