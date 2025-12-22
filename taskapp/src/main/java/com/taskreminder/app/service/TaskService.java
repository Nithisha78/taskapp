package com.taskreminder.app.service;

import com.taskreminder.app.entity.Task;
import com.taskreminder.app.enums.TaskPriority;
import com.taskreminder.app.enums.TaskStatus;
import com.taskreminder.app.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    /* ===== ADD TASK ===== */
    public void addTask(Task task) {
        task.setStatus(TaskStatus.PENDING);
        task.setCreatedAt(LocalDateTime.now()); // ✅ SET CREATED TIME
        task.setCompletedAt(null);              // ✅ ENSURE NULL INITIALLY
        taskRepository.save(task);
    }

    /* ===== GET ALL TASKS ===== */
    public List<Task> getAllTasks() {
        return taskRepository.findAll(Sort.by("id").descending());
    }

    /* ===== PAGINATION ===== */
    public Page<Task> getTasksPaginated(int page, int size) {
        return taskRepository.findAll(
                PageRequest.of(page, size, Sort.by("id").descending())
        );
    }

    /* ===== GET BY ID ===== */
    public Task getTaskById(Long id) {
        return taskRepository.findById(id).orElse(null);
    }

    /* ===== UPDATE TASK ===== */
    public void updateTask(Task task) {
        // ⚠️ IMPORTANT: DO NOT override createdAt
        Task existingTask = taskRepository.findById(task.getId()).orElse(null);

        if (existingTask != null) {
            task.setCreatedAt(existingTask.getCreatedAt());
            task.setCompletedAt(existingTask.getCompletedAt());
            taskRepository.save(task);
        }
    }

    /* ===== DELETE TASK ===== */
    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    /* ===== SEARCH ===== */
    public List<Task> searchByTitle(String keyword) {
        return taskRepository.findByTitleContainingIgnoreCase(keyword);
    }

    /* ===== FILTERS ===== */
    public List<Task> filterByStatus(TaskStatus status) {
        return taskRepository.findByStatus(status);
    }

    public List<Task> filterByPriority(TaskPriority priority) {
        return taskRepository.findByPriority(priority);
    }

    /* ===== COMPLETE TASK ===== */
    public void completeTask(Long id) {
        Task task = taskRepository.findById(id).orElse(null);
        if (task != null && task.getStatus() != TaskStatus.COMPLETED) {
            task.setStatus(TaskStatus.COMPLETED);
            task.setCompletedAt(LocalDateTime.now()); // ✅ SET COMPLETED TIME
            taskRepository.save(task);
        }
    }
}
