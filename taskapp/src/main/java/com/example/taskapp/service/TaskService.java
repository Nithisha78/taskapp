package com.example.taskapp.service;

import com.example.taskapp.entity.Task;
import com.example.taskapp.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;


    public void addTask(Task task) {
        task.setStatus("pending");
        taskRepository.save(task);
    }


    public List<Task> getAllTasks() {
        return taskRepository.findAll();
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
        taskRepository.save(task);
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }


    public List<Task> searchByTitle(String keyword) {
        return taskRepository.findByTitleContainingIgnoreCase(keyword);
    }


    public List<Task> filterByStatus(String status) {
        return taskRepository.findByStatus(status);
    }

    public List<Task> filterByPriority(String priority) {
        return taskRepository.findByPriority(priority);
    }



    public void completeTask(Long id) {
        Task task = taskRepository.findById(id).orElse(null);
        if (task != null) {
            task.setStatus("completed");
            taskRepository.save(task);
        }
    }
}
