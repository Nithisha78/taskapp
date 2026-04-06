package com.taskreminder.app.scheduler;

import com.taskreminder.app.entity.Task;
import com.taskreminder.app.enums.TaskStatus;
import com.taskreminder.app.repository.TaskRepository;
import com.taskreminder.app.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Component
public class TaskReminderScheduler {

    private static final Logger logger = LoggerFactory.getLogger(TaskReminderScheduler.class);

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private EmailService emailService;

    // ❌ DISABLED DAILY REMINDER (kept as is)
    // @Scheduled(cron = "0 0 9 * * ?", zone = "Asia/Kolkata")
    public void sendTaskReminders() {

        logger.info("Task reminder scheduler executed");

        LocalDate today = LocalDate.now(ZoneId.of("Asia/Kolkata"));
        LocalDate tomorrow = today.plusDays(1);

        List<Task> overdueTasks =
                taskRepository.findByDueDateBeforeAndStatus(
                        today,
                        TaskStatus.PENDING
                );

        List<Task> tomorrowTasks =
                taskRepository.findByDueDateAndStatusIn(
                        tomorrow,
                        List.of(TaskStatus.PENDING, TaskStatus.IN_PROGRESS)
                );

        sendOverdueReminders(overdueTasks);
        sendTomorrowReminders(tomorrowTasks);
    }

    private void sendOverdueReminders(List<Task> tasks) {
        for (Task task : tasks) {
            emailService.sendEmail(
                    task.getUser().getEmail(),
                    "🚨 OVERDUE TASK: " + task.getTitle(),
                    "Hi " + task.getUser().getUsername() + ",\n\n" +
                            "⚠️ Your task \"" + task.getTitle() +
                            "\" was due on " + task.getDueDate() + " and is now OVERDUE.\n\n" +
                            "Please complete it as soon as possible.\n\n" +
                            "– Task Reminder App"
            );
        }
    }

    private void sendTomorrowReminders(List<Task> tasks) {
        for (Task task : tasks) {
            emailService.sendEmail(
                    task.getUser().getEmail(),
                    "⏰ Task Due Tomorrow: " + task.getTitle(),
                    "Hi " + task.getUser().getUsername() + ",\n\n" +
                            "Your task \"" + task.getTitle() +
                            "\" is due TOMORROW (" + task.getDueDate() + ").\n\n" +
                            "Please plan to complete it on time.\n\n" +
                            "– Task Reminder App"
            );
        }
    }

    // ✅ IMPROVED CUSTOM REMINDER
    @Scheduled(fixedRate = 60000)
    public void sendCustomReminders() {

        logger.info("Custom reminder scheduler running");

        List<Task> allTasks = taskRepository.findAll();
        LocalDateTime now = LocalDateTime.now();

        for (Task task : allTasks) {

            if (task.getReminderTime() != null &&
                    !task.isReminderSent() &&
                    task.getReminderTime().isBefore(now) &&
                    task.getStatus() != TaskStatus.COMPLETED) {

                String subject = "🔔 Reminder: " + task.getTitle();

                String body =
                        "Hi " + task.getUser().getUsername() + ",\n\n" +

                                "This is a reminder for your task:\n\n" +

                                "📌 Task: " + task.getTitle() + "\n" +
                                "📝 Description: " + task.getDescription() + "\n" +
                                "📅 Due Date: " + task.getDueDate() + "\n" +
                                "⚡ Priority: " + task.getPriority() + "\n\n" +

                                "❗ Status: Incomplete\n\n" +

                                "Please make sure to complete it on time.\n\n" +

                                "– Task Reminder App";

                emailService.sendEmail(
                        task.getUser().getEmail(),
                        subject,
                        body
                );

                logger.info("Reminder sent for task: {}", task.getTitle());

                task.setReminderSent(true);
                taskRepository.save(task);
            }
        }
    }
}