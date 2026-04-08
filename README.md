# 📌 EduSmart

EduSmart is a full-stack role-based education management system that connects Students, Teachers, and Admins on a single platform. It provides secure authentication, academic management, and real-time communication using a modern Android frontend and scalable backend.

---

## 🚀 Features

### 🔐 Authentication & Authorization
- JWT-based authentication  
- Role-Based Access Control (Student / Teacher / Admin)  
- Secure API communication  

### 📊 Academic Management
- Attendance tracking system  
- Timetable management  

### 💬 Real-Time Communication
- Group chat (class-based)  
- Private one-to-one chat  
- WebSocket-based messaging (STOMP)  

### 🌐 API System
- RESTful APIs  
- JSON request/response handling  

---

## 🧑‍🤝‍🧑 User Roles

### 👨‍🎓 Student
- Secure login  
- View attendance  
- View timetable  
- Participate in group & private chats  

### 👩‍🏫 Teacher
- Secure login  
- Mark and manage student attendance  
- View timetable  
- Communicate with students  

### 👨‍💼 Admin
- Manage students & teachers  
- Control system-level operations  

---

## 📱 Frontend (Android)

- Kotlin  
- Jetpack Compose  
- Hilt (Dependency Injection)  
- MVVM Architecture  
- ViewModel  
- Retrofit (API Integration)  
- WebSocket integration for real-time updates  

---

## 🖥️ Backend

- Java  
- Spring Boot  
- Spring Security  
- JWT Authentication  
- REST APIs  
- WebSocket (STOMP)  

---

## 🔑 Authentication Flow

1. User logs in with credentials  
2. Backend validates user and role  
3. JWT token is generated  
4. Token is sent in request headers  

Example: Authorization: Bearer <JWT_TOKEN>



---

## 🏗️ Architecture

### Backend
- Layered architecture (Controller → Service → Repository)  
- Stateless system design  
- Scalable and maintainable  

### Frontend
- MVVM architecture  
- Clean separation of concerns  
- Reactive UI with Jetpack Compose  

---

## 🛠️ Tech Stack

### Frontend
- Kotlin  
- Jetpack Compose  
- Hilt  
- Retrofit  
- ViewModel  

### Backend
- Java  
- Spring Boot  
- Spring Security  
- JWT  
- WebSocket (STOMP)  

### Tools
- Postman  
- Git & GitHub  

---

## 🎯 Highlights

- Full-stack system (Android + Backend)  
- Secure role-based authentication  
- Real-time chat system  
- Modern Android development with Compose + Hilt  
- Clean and scalable architecture  

---

## 📝 One-Line Summary

EduSmart is a full-stack education platform with an Android app built using Jetpack Compose and Hilt, and a Spring Boot backend providing secure role-based access, academic management, and real-time communication via REST APIs and WebSocket.
