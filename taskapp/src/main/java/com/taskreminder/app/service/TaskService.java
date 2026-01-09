package com.taskreminder.app.service;

import com.taskreminder.app.entity.Task;
import com.taskreminder.app.entity.User;
import com.taskreminder.app.enums.TaskPriority;
import com.taskreminder.app.enums.TaskStatus;
import com.taskreminder.app.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private EmailService emailService;

    public void addTask(Task task) {
        task.setStatus(TaskStatus.PENDING);
        task.setCreatedAt(LocalDateTime.now());
        task.setCompletedAt(null);
        taskRepository.save(task);
    }

    public List<Task> getTasksByUser(User user) {
        return taskRepository.findByUser(user);
    }

    public Page<Task> getTasksByUserPaged(User user, int page, int size) {
        return taskRepository.findByUser(user, PageRequest.of(page, size));
    }

    public Task getTaskById(Long id) {
        return taskRepository.findById(id).orElse(null);
    }

    public void updateTask(Task task) {
        Task existingTask = taskRepository.findById(task.getId()).orElse(null);

        if (existingTask != null) {
            task.setCreatedAt(existingTask.getCreatedAt());
            task.setCompletedAt(existingTask.getCompletedAt());
            task.setUser(existingTask.getUser());
            taskRepository.save(task);
        }
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    public List<Task> searchByTitle(String keyword, User user) {
        return taskRepository.findByUser(user)
                .stream()
                .filter(task -> task.getTitle().toLowerCase()
                        .contains(keyword.toLowerCase()))
                .toList();
    }

    public List<Task> filterByStatus(TaskStatus status, User user) {
        return taskRepository.findByUser(user)
                .stream()
                .filter(task -> task.getStatus() == status)
                .toList();
    }

    public List<Task> filterByPriority(TaskPriority priority, User user) {
        return taskRepository.findByUser(user)
                .stream()
                .filter(task -> task.getPriority() == priority)
                .toList();
    }

    public void completeTask(Long id) {
        Task task = taskRepository.findById(id).orElse(null);

        if (task != null && task.getStatus() != TaskStatus.COMPLETED) {
            task.setStatus(TaskStatus.COMPLETED);
            task.setCompletedAt(LocalDateTime.now());
            taskRepository.save(task);
        }
    }

    public List<Task> getDueTodayTasks(User user) {
        LocalDate today = LocalDate.now();
        return taskRepository.findByUserAndDueDate(user, today);
    }

    public List<Task> getUpcomingTasks(User user) {
        LocalDate today = LocalDate.now();
        return taskRepository.findByUserAndDueDateAfter(user, today);
    }

    public List<Task> getOverdueTasks(User user) {
        LocalDate today = LocalDate.now();
        return taskRepository.findByUserAndDueDateBeforeAndStatus(
                user, today, TaskStatus.PENDING);
    }
}
