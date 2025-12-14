package com.example.taskapp.controller;

import com.example.taskapp.entity.Task;
import com.example.taskapp.service.TaskService;
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

    @GetMapping("/form")
    public String addTaskForm(Model model) {
        model.addAttribute("task", new Task());
        return "add-task";
    }

    @PostMapping("/add")
    public String addTask(Task task, RedirectAttributes ra) {
        taskService.addTask(task);
        ra.addFlashAttribute("success", "Task added successfully");
        return "redirect:/tasks";
    }

    @GetMapping("/edit/{id}")
    public String editTask(@PathVariable Long id, Model model) {
        model.addAttribute("task", taskService.getTaskById(id));
        return "edit-task";
    }

    @PostMapping("/update")
    public String updateTask(Task task) {
        taskService.updateTask(task);
        return "redirect:/tasks";
    }

    @GetMapping("/delete/{id}")
    public String deleteTask(@PathVariable Long id, RedirectAttributes ra) {
        taskService.deleteTask(id);
        ra.addFlashAttribute("success", "Task deleted successfully");
        return "redirect:/tasks";
    }


    @GetMapping("/complete/{id}")
    public String completeTask(@PathVariable Long id, RedirectAttributes ra) {
        taskService.completeTask(id);
        ra.addFlashAttribute("success", "Task marked as completed");
        return "redirect:/tasks";
    }


    @GetMapping("/search")
    public String search(@RequestParam String keyword, Model model) {
        model.addAttribute("tasks", taskService.searchByTitle(keyword));
        return "tasks";
    }


    @GetMapping("/filter/status")
    public String filterByStatus(@RequestParam String status, Model model) {
        model.addAttribute("tasks", taskService.filterByStatus(status));
        return "tasks";
    }


    @GetMapping("/filter/priority")
    public String filterByPriority(@RequestParam String priority, Model model) {
        model.addAttribute("tasks", taskService.filterByPriority(priority));
        return "tasks";
    }

}
