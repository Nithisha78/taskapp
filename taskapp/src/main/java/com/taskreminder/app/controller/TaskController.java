package com.taskreminder.app.controller;

import com.taskreminder.app.entity.Task;
import com.taskreminder.app.enums.TaskPriority;
import com.taskreminder.app.enums.TaskStatus;
import com.taskreminder.app.service.TaskService;
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

    // ---------------- LIST TASKS WITH PAGINATION ----------------
    @GetMapping
    public String listTasks(
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        Page<Task> taskPage = taskService.getTasksPaginated(page, 10);

        model.addAttribute("tasks", taskPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", taskPage.getTotalPages());

        return "tasks";
    }

    // ---------------- ADD TASK FORM ----------------
    @GetMapping("/form")
    public String addTaskForm(Model model) {
        model.addAttribute("task", new Task());
        model.addAttribute("statuses", TaskStatus.values());
        model.addAttribute("priorities", TaskPriority.values());
        return "add-task";
    }

    // ---------------- ADD TASK ----------------
    @PostMapping("/add")
    public String addTask(Task task, RedirectAttributes ra) {
        taskService.addTask(task);
        ra.addFlashAttribute("success", "Task added successfully");
        return "redirect:/tasks";
    }

    // ---------------- EDIT TASK ----------------
    @GetMapping("/edit/{id}")
    public String editTask(@PathVariable Long id, Model model) {
        model.addAttribute("task", taskService.getTaskById(id));
        model.addAttribute("statuses", TaskStatus.values());
        model.addAttribute("priorities", TaskPriority.values());
        return "edit-task";
    }

    // ---------------- UPDATE TASK ----------------
    @PostMapping("/update")
    public String updateTask(Task task, RedirectAttributes ra) {
        taskService.updateTask(task);
        ra.addFlashAttribute("success", "Task updated successfully");
        return "redirect:/tasks";
    }

    // ---------------- DELETE TASK ----------------
    @GetMapping("/delete/{id}")
    public String deleteTask(@PathVariable Long id, RedirectAttributes ra) {
        taskService.deleteTask(id);
        ra.addFlashAttribute("success", "Task deleted successfully");
        return "redirect:/tasks";
    }

    // ---------------- COMPLETE TASK ----------------
    @GetMapping("/complete/{id}")
    public String completeTask(@PathVariable Long id, RedirectAttributes ra) {
        taskService.completeTask(id);
        ra.addFlashAttribute("success", "Task marked as completed");
        return "redirect:/tasks";
    }

    // ---------------- SEARCH BY TITLE ----------------
    @GetMapping("/search")
    public String search(@RequestParam String keyword, Model model) {
        model.addAttribute("tasks", taskService.searchByTitle(keyword));

        // ✅ IMPORTANT: prevent pagination errors
        model.addAttribute("currentPage", 0);
        model.addAttribute("totalPages", 1);

        return "tasks";
    }

    // ---------------- FILTER BY STATUS ----------------
    @GetMapping("/filter/status")
    public String filterByStatus(@RequestParam String status, Model model) {
        TaskStatus taskStatus = TaskStatus.valueOf(status.toUpperCase());
        model.addAttribute("tasks", taskService.filterByStatus(taskStatus));

        // ✅ IMPORTANT
        model.addAttribute("currentPage", 0);
        model.addAttribute("totalPages", 1);

        return "tasks";
    }

    // ---------------- FILTER BY PRIORITY ----------------
    @GetMapping("/filter/priority")
    public String filterByPriority(@RequestParam String priority, Model model) {
        TaskPriority taskPriority = TaskPriority.valueOf(priority.toUpperCase());
        model.addAttribute("tasks", taskService.filterByPriority(taskPriority));

        // ✅ IMPORTANT
        model.addAttribute("currentPage", 0);
        model.addAttribute("totalPages", 1);

        return "tasks";
    }

    // ---------------- VIEW TASK ----------------
    @GetMapping("/view/{id}")
    public String viewTask(@PathVariable Long id, Model model) {
        model.addAttribute("task", taskService.getTaskById(id));
        return "view-task";
    }
}
