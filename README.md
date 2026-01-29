📌 PROJECT: EduSmart – Backend System

EduSmart is a role-based education management backend that connects 👨‍🎓 Students, 👩‍🏫 Teachers, and 👨‍💼 Admins on a single platform. It provides secure authentication, academic management features, and real-time communication using REST APIs and WebSocket technology.

🧑‍🤝‍🧑 USER ROLES
• 👨‍🎓 Student  
  – Secure login  
  – View attendance  
  – View timetable  
  – Participate in group & private chat  

• 👩‍🏫 Teacher  
  – Secure login  
  – Mark student attendance  
  – View timetable  
  – Communicate with students  

• 👨‍💼 Admin  
  – Manage students & teachers  
  – Control system-level operations  

✨ KEY FEATURES
• 🔐 JWT-based authentication & authorization  
• 🧭 Role-based access control (Student / Teacher / Admin)  
• 📊 Attendance management system  
• 🗓️ Timetable viewing for students & teachers  
• 💬 Real-time chatting system  
  – 📢 Group chat (class-based)  
  – 🔒 Private one-to-one chat  
• 🌐 RESTful APIs with JSON request/response  
• ⚡ WebSocket communication using STOMP  

🔑 AUTHENTICATION FLOW
• User logs in with credentials  
• Backend validates user & role  
• JWT token is generated  
• Token is sent in request headers  

Example:
Authorization: Bearer <JWT_TOKEN>

🛠️ TECH STACK
• ☕ Java  
• 🚀 Spring Boot  
• 🛡️ Spring Security  
• 🔑 JWT Authentication  
• 🌐 REST APIs  
• ⚡ WebSocket (STOMP)  
• 🧪 Postman (API testing & documentation)  
• 🗂️ Git & GitHub  

🏗️ ARCHITECTURE HIGHLIGHTS
• Layered architecture (Controller → Service → Repository)  
• Stateless backend design  
• Scalable and maintainable code structure  

🎯 PROJECT HIGHLIGHTS
• Real-world education platform backend  
• Secure role-based system  
• REST + WebSocket integration  
• Clean API documentation & testing  

📝 ONE-LINE SUMMARY
EduSmart is a Spring Boot–based backend system that connects students, teachers, and admins, providing secure authentication, attendance and timetable management, and real-time chat using REST APIs and WebSocket (STOMP).
