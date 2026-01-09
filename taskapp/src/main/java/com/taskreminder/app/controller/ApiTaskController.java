package com.taskreminder.app.controller;

import com.taskreminder.app.entity.Task;
import com.taskreminder.app.entity.User;
import com.taskreminder.app.enums.TaskPriority;
import com.taskreminder.app.enums.TaskStatus;
import com.taskreminder.app.service.TaskService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class ApiTaskController {

    @Autowired
    private TaskService taskService;

    @GetMapping
    public List<Task> getAllTasks(HttpSession session) {

        User user = (User) session.getAttribute("loggedUser");
        if (user == null) {
            return List.of();
        }

        return taskService.getTasksByUser(user);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id, HttpSession session) {

        User user = (User) session.getAttribute("loggedUser");
        if (user == null) {
            return ResponseEntity.status(401).build();
        }

        Task task = taskService.getTaskById(id);
        if (task == null || !task.getUser().getId().equals(user.getId())) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(task);
    }

    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task, HttpSession session) {

        User user = (User) session.getAttribute("loggedUser");
        if (user == null) {
            return ResponseEntity.status(401).build();
        }

        task.setUser(user);
        taskService.addTask(task);

        return ResponseEntity
                .created(URI.create("/api/tasks/" + task.getId()))
                .body(task);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(
            @PathVariable Long id,
            @RequestBody Task task,
            HttpSession session) {

        User user = (User) session.getAttribute("loggedUser");
        if (user == null) {
            return ResponseEntity.status(401).build();
        }

        Task existing = taskService.getTaskById(id);
        if (existing == null || !existing.getUser().getId().equals(user.getId())) {
            return ResponseEntity.notFound().build();
        }

        task.setId(id);
        task.setUser(user);
        taskService.updateTask(task);

        return ResponseEntity.ok(task);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id, HttpSession session) {

        User user = (User) session.getAttribute("loggedUser");
        if (user == null) {
            return ResponseEntity.status(401).build();
        }

        Task task = taskService.getTaskById(id);
        if (task == null || !task.getUser().getId().equals(user.getId())) {
            return ResponseEntity.notFound().build();
        }

        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public List<Task> searchByTitle(@RequestParam String keyword, HttpSession session) {

        User user = (User) session.getAttribute("loggedUser");
        if (user == null) {
            return List.of();
        }

        return taskService.searchByTitle(keyword, user);
    }

    @GetMapping("/filter/status")
    public List<Task> filterByStatus(@RequestParam String status, HttpSession session) {

        User user = (User) session.getAttribute("loggedUser");
        if (user == null) {
            return List.of();
        }

        TaskStatus taskStatus = TaskStatus.valueOf(status.toUpperCase());
        return taskService.filterByStatus(taskStatus, user);
    }

    @GetMapping("/filter/priority")
    public List<Task> filterByPriority(@RequestParam String priority, HttpSession session) {

        User user = (User) session.getAttribute("loggedUser");
        if (user == null) {
            return List.of();
        }

        TaskPriority taskPriority = TaskPriority.valueOf(priority.toUpperCase());
        return taskService.filterByPriority(taskPriority, user);
    }
}
