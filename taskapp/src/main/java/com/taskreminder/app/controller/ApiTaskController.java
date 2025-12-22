package com.taskreminder.app.controller;

import com.taskreminder.app.entity.Task;
import com.taskreminder.app.enums.TaskPriority;
import com.taskreminder.app.enums.TaskStatus;
import com.taskreminder.app.service.TaskService;
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

    // ---------------- GET ALL TASKS ----------------
    @GetMapping
    public List<Task> getAllTasks() {
        return taskService.getAllTasks();
    }

    // ---------------- GET TASK BY ID ----------------
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        Task task = taskService.getTaskById(id);
        if (task == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(task);
    }

    // ---------------- CREATE TASK ----------------
    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        taskService.addTask(task); // status defaults to PENDING
        return ResponseEntity
                .created(URI.create("/api/tasks/" + task.getId()))
                .body(task);
    }

    // ---------------- UPDATE TASK ----------------
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(
            @PathVariable Long id,
            @RequestBody Task task) {

        Task existing = taskService.getTaskById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }

        task.setId(id);
        taskService.updateTask(task);
        return ResponseEntity.ok(task);
    }

    // ---------------- DELETE TASK ----------------
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        Task task = taskService.getTaskById(id);
        if (task == null) {
            return ResponseEntity.notFound().build();
        }

        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    // ---------------- SEARCH BY TITLE ----------------
    @GetMapping("/search")
    public List<Task> searchByTitle(@RequestParam String keyword) {
        return taskService.searchByTitle(keyword);
    }

    // ---------------- FILTER BY STATUS ----------------
    @GetMapping("/filter/status")
    public List<Task> filterByStatus(@RequestParam String status) {
        TaskStatus taskStatus = TaskStatus.valueOf(status.toUpperCase());
        return taskService.filterByStatus(taskStatus);
    }

    // ---------------- FILTER BY PRIORITY ----------------
    @GetMapping("/filter/priority")
    public List<Task> filterByPriority(@RequestParam String priority) {
        TaskPriority taskPriority = TaskPriority.valueOf(priority.toUpperCase());
        return taskService.filterByPriority(taskPriority);
    }
    @GetMapping("/view/{id}")
    public ResponseEntity<Task> viewTask(@PathVariable Long id) {
        Task task = taskService.getTaskById(id);
        if (task == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(task);
    }

}
