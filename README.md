
# Gara App Backend

[![Java - 21](https://img.shields.io/badge/Java-21-2ea44f?logo=coffeescript)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.0-6DB33F?logo=spring-boot)](https://spring.io/projects/spring-boot)
[![Spring Security](https://img.shields.io/badge/Spring%20Security-6.x-6DB33F?logo=springsecurity)](https://spring.io/projects/spring-security)
[![JWT](https://img.shields.io/badge/JWT-Auth-000000?logo=jsonwebtokens)](https://jwt.io/)
[![Maven](https://img.shields.io/badge/Maven-3.5.0-C71A36?logo=apachemaven)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

> 🚀 A modern, secure backend application built with Spring Boot, featuring JWT authentication and real-time capabilities through Server-Sent Events.

## ✨ Features

- 🔐 **JWT Authentication** - Secure token-based authentication
- 🛡️ **Spring Security** - Comprehensive security framework
- ⚡ **Server-Sent Events** - Real-time data streaming
- ☕ **Java 21** - Latest LTS Java features
- 🏗️ **Spring Boot** - Production-ready framework
- 📦 **Maven** - Dependency management and build automation

## 🛠️ Tech Stack

| Technology | Version | Purpose |
|------------|--------|---------|
| Java | 21     | Programming Language |
| Spring Boot | 3.5.0  | Application Framework |
| Spring Security | 6.x    | Security Framework |
| JWT | -      | Authentication Tokens |
| Maven | 3.5.0  | Build Tool |

## 🚀 Quick Start

### Clone the repository

```bash
  git clone https://github.com/j-imsa/gara-app-backend.git
```

### Navigate to project directory

```bash
  cd gara-app-backend
```

### Run the application

```bash
  mvn spring-boot:run
```


## 📡 API Endpoints

### Authentication
- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration
- `POST /api/auth/refresh` - Refresh JWT token

### Real-time Events
- `GET /api/events/stream` - SSE endpoint for live updates

## 🔧 Configuration

Create `application.yml` with your settings:

```yaml
server: 
  port: 8888

jwt: 
  secret: 
    your-secret-key: 
      expiration: 86400000

spring: 
  datasource: 
    url: jdbc:h2:mem:garadb
```


## 🤝 Contributing

1. Fork the project
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

⭐ **Star this repository if you find it helpful!**