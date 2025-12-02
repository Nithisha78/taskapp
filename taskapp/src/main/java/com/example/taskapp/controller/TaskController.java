package com.example.taskapp.controller;

import com.example.taskapp.entity.Task;
import com.example.taskapp.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;


    @GetMapping
    public String listTasks(Model model) {
        model.addAttribute("tasks", taskService.getAllTasks());
        return "tasks";
    }

    // Open form (for add or update)
    @GetMapping("/form")
    public String openForm(@RequestParam(required = false) Long id, Model model) {
        if (id == null) {
            model.addAttribute("task", new Task()); // add new
        } else {
            model.addAttribute("task", taskService.getTaskById(id)); // edit
        }
        return "task-form";
    }


    @PostMapping("/add")
    public String addTask(Task task) {
        taskService.addTask(task);
        return "redirect:/tasks";
    }


    @PostMapping("/update")
    public String updateTask(Task task) {
        taskService.updateTask(task);
        return "redirect:/tasks";
    }


    @GetMapping("/delete/{id}")
    public String deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return "redirect:/tasks";
    }


    @GetMapping("/complete/{id}")
    public String completeTask(@PathVariable Long id) {
        taskService.completeTask(id);
        return "redirect:/tasks";
    }
}
