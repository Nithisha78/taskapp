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


    public void addTask(Task task) {
        task.setStatus(TaskStatus.PENDING);
        task.setCreatedAt(LocalDateTime.now());
        task.setCompletedAt(null);
        taskRepository.save(task);
    }


    public List<Task> getAllTasks() {
        return taskRepository.findAll(Sort.by("id").descending());
    }

    public Page<Task> getTasksPaginated(int page, int size) {
        return taskRepository.findAll(
                PageRequest.of(page, size, Sort.by("id").descending())
        );
    }


    public Task getTaskById(Long id) {
        return taskRepository.findById(id).orElse(null);
    }


    public void updateTask(Task task) {

        Task existingTask = taskRepository.findById(task.getId()).orElse(null);

        if (existingTask != null) {
            task.setCreatedAt(existingTask.getCreatedAt());
            task.setCompletedAt(existingTask.getCompletedAt());
            taskRepository.save(task);
        }
    }


    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }


    public List<Task> searchByTitle(String keyword) {
        return taskRepository.findByTitleContainingIgnoreCase(keyword);
    }


    public List<Task> filterByStatus(TaskStatus status) {
        return taskRepository.findByStatus(status);
    }

    public List<Task> filterByPriority(TaskPriority priority) {
        return taskRepository.findByPriority(priority);
    }


    public void completeTask(Long id) {
        Task task = taskRepository.findById(id).orElse(null);
        if (task != null && task.getStatus() != TaskStatus.COMPLETED) {
            task.setStatus(TaskStatus.COMPLETED);
            task.setCompletedAt(LocalDateTime.now()); // ✅ SET COMPLETED TIME
            taskRepository.save(task);
        }
    }
}
