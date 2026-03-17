# Automated Task Reminder and Tracking Application

## 📌 Project Overview

The **Automated Task Reminder and Tracking Application** is a Spring Boot-based web application that helps users manage tasks efficiently by tracking deadlines and sending automated reminders.

The system allows users to create tasks, monitor their status, and receive scheduled notifications through email to ensure important activities are not missed.

---

## 🚀 Features

* User registration and authentication
* OTP verification for secure login
* Create, update, and delete tasks
* Task status tracking
* Automated task reminders using scheduler
* Email notification service
* Secure configuration using Spring Security

---

## 🛠️ Technologies Used

* Java
* Spring Boot
* Spring Security
* Spring Data JPA
* Hibernate
* MySQL
* Maven
* HTML / CSS / JavaScript

---

## 📂 Project Structure

src/main/java

config

* SecurityConfig.java
* WebConfig.java

controller

* Handles HTTP requests and API endpoints

dto

* OtpRequest.java
* ResendOtpRequest.java

entity

* Task.java
* User.java

repository

* Database interaction using JPA

service

* EmailService.java
* TaskService.java

scheduler

* TaskReminderScheduler.java

---

## ⚙️ Installation and Setup

### 1. Clone the repository

```id="c1"
git clone https://github.com/Nithisha78/task-reminder-app.git
```

### 2. Navigate to project folder

```id="c2"
cd task-reminder-app
```

### 3. Configure database

Update **application.properties** with your MySQL credentials.

Example:

```
spring.datasource.url=jdbc:mysql://localhost:3306/taskdb
spring.datasource.username=root
spring.datasource.password=yourpassword
```

### 4. Build the project

```
mvn clean install
```

### 5. Run the application

```
mvn spring-boot:run
```

### 6. Open in browser

```
http://localhost:8080
```

---

## 🔔 Reminder System

The application uses a **Spring Scheduler** to automatically check task deadlines and trigger reminder notifications through the email service.

---

## 🔮 Future Enhancements

* Mobile notification support
* Calendar integration


---


