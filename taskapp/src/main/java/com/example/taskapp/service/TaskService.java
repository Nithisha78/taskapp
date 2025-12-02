package com.example.taskapp.service;

import com.example.taskapp.entity.Task;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TaskService {

    private List<Task> taskList = new ArrayList<>();
    private Long idCounter = 1L;

    // Add task
    public void addTask(Task task) {
        task.setId(idCounter++);
        task.setStatus("pending");
        taskList.add(task);
    }

    // Get all tasks
    public List<Task> getAllTasks() {
        return taskList;
    }

    // Delete a task
    public void deleteTask(Long id) {
        taskList.removeIf(t -> t.getId().equals(id));
    }
    public Task getTaskById(Long id) {
        for (Task t : taskList) {
            if (t.getId().equals(id)) {
                return t;
            }
        }
        return null;
    }

    public void updateTask(Task updatedTask) {
        for (Task t : taskList) {
            if (t.getId().equals(updatedTask.getId())) {
                t.setTitle(updatedTask.getTitle());
                t.setDescription(updatedTask.getDescription());
                t.setDueDate(updatedTask.getDueDate());
                // keep same status
                break;
            }
        }
    }

    // Mark task as completed
    public void completeTask(Long id) {
        for (Task t : taskList) {
            if (t.getId().equals(id)) {
                t.setStatus("completed");
                break;
            }
        }
    }
}
