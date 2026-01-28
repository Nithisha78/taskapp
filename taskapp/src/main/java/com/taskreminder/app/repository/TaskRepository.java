package com.taskreminder.app.repository;

import com.taskreminder.app.entity.Task;
import com.taskreminder.app.entity.User;
import com.taskreminder.app.enums.TaskPriority;
import com.taskreminder.app.enums.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {


    List<Task> findByTitleContainingIgnoreCase(String keyword);

    List<Task> findByStatus(TaskStatus status);

    List<Task> findByPriority(TaskPriority priority);



    List<Task> findByDueDate(LocalDate date);

    List<Task> findByDueDateAfter(LocalDate date);

    List<Task> findByDueDateBeforeAndStatus(LocalDate date, TaskStatus status);



    List<Task> findByUser(User user);

    List<Task> findByUserAndDueDate(User user, LocalDate date);

    List<Task> findByUserAndDueDateAfter(User user, LocalDate date);

    List<Task> findByUserAndDueDateBeforeAndStatus(
            User user,
            LocalDate date,
            TaskStatus status
    );



    Page<Task> findByUser(User user, Pageable pageable);

    List<Task> findByDueDateAndStatusIn(
            LocalDate dueDate,
            List<TaskStatus> statuses
    );
}
