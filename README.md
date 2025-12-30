# 🚀 ArthSethu - AI-Powered Business Intelligence Platform

**ArthSethu** is an innovative AI-powered business intelligence and financial advisory platform designed to help entrepreneurs make data-driven decisions for their ventures.

## ✨ Features

### 🎯 Smart Business Onboarding
- Dynamic questionnaire that adapts to your business type
- Multi-step form with progress tracking
- Intelligent validation and user experience

### 🤖 AI CFO Assistant
- AI-powered financial advisor with intelligent responses
- Real-time chat interface with business insights
- Strategic recommendations based on your business data

### 📊 Business Dashboard
- Real-time metrics and KPI tracking
- Health score monitoring (87/100 performance tracking)
- Interactive charts and data visualization
- Performance benchmarking against industry standards

### 📋 Feasibility Reports
- Professional PDF report generation
- Comprehensive financial analysis
- Market insights and projections
- Break-even analysis and ROI calculations

### 🏢 Admin Panel
- User management and analytics
- System health monitoring
- Revenue tracking and reporting

## 🎮 Live Demo

### Quick Start - Standalone Demo
Open `DEMO_FIXED.html` in your browser for an immediate, fully functional demo that works without any server setup.

### Full Application Demo
For the complete Spring Boot application experience:

1. **Prerequisites**: Java 17+, Maven (or use included Maven wrapper)
2. **Run**: Execute `run-demo.bat` or use `./mvnw spring-boot:run`
3. **Access**: Navigate to `http://localhost:8080/arthsethu/demo`

## 🛠️ Technology Stack

### Backend
- **Spring Boot 3.x** - Main application framework
- **Spring AI** - AI integration with Ollama
- **PostgreSQL** - Primary database
- **Spring Security** - Authentication and authorization
- **Spring Data JPA** - Data persistence layer

### Frontend
- **Thymeleaf** - Server-side templating
- **HTML5/CSS3** - Modern web standards
- **JavaScript (ES6+)** - Interactive functionality
- **Responsive Design** - Mobile-first approach

### AI & Integration
- **Ollama** - Local AI model integration (Llama 3.2)
- **LangChain** - AI workflow management
- **Government APIs** - Real-time data integration
- **Email Integration** - SMTP notifications

## 🚀 Getting Started

### Option 1: Standalone Demo (Recommended for Quick Testing)
```bash
# Simply open in browser
open DEMO_FIXED.html
```

### Option 2: Full Application
```bash
# Clone the repository
git clone https://github.com/yourusername/arthsethu-platform.git
cd arthsethu-platform

# Run with Maven wrapper (Windows)
./mvnw.cmd spring-boot:run

# Run with Maven wrapper (Linux/Mac)
./mvnw spring-boot:run

# Or use the batch file (Windows)
run-demo.bat
```

### Database Setup
```sql
-- Create PostgreSQL database
CREATE DATABASE arthsethu;

-- The application will auto-create tables on first run
-- Default credentials: postgres/mpvsSQL216
```

## 📱 Demo URLs

When running the full application:

- **Home**: `http://localhost:8080/arthsethu/demo`
- **Onboarding**: `http://localhost:8080/arthsethu/demo/onboarding`
- **AI CFO**: `http://localhost:8080/arthsethu/demo/ai-cfo`
- **Dashboard**: `http://localhost:8080/arthsethu/demo/dashboard`
- **Reports**: `http://localhost:8080/arthsethu/demo/reports`
- **Admin**: `http://localhost:8080/arthsethu/demo/admin`

## 🎯 Key Highlights

### Business Intelligence
- **87/100 Health Score** tracking with real-time updates
- **34% Profit Margin** analysis (89% above industry average)
- **₹6,200 Daily Revenue** monitoring per 25-seat capacity
- **5.2% Wastage Rate** optimization recommendations

### AI-Powered Insights
- Intelligent cost optimization suggestions
- Revenue growth opportunity identification
- Market benchmarking and competitive analysis
- Strategic expansion recommendations

### Financial Analysis
- Break-even point calculations (8-month projection)
- ROI analysis and projections
- Cash flow management insights
- Investment requirement assessments

## 🏗️ Project Structure

```
arthsethu-platform/
├── src/main/java/com/arthsethu/
│   ├── controller/          # REST controllers and web endpoints
│   ├── service/            # Business logic layer
│   ├── model/              # Data models and entities
│   ├── repository/         # Data access layer
│   ├── config/             # Configuration classes
│   └── security/           # Security configurations
├── src/main/resources/
│   ├── templates/          # Thymeleaf templates
│   ├── static/             # CSS, JS, images
│   └── application.properties
├── DEMO_FIXED.html         # Standalone demo (no server required)
├── WORKING_DEMO.html       # Alternative standalone demo
└── run-demo.bat           # Windows batch file for easy startup
```

## 🔧 Configuration

### Environment Variables
```properties
# Database
DB_NAME=arthsethu
DB_USERNAME=postgres
DB_PASSWORD=mpvsSQL216

# AI Configuration
OLLAMA_BASE_URL=http://localhost:11434
OLLAMA_MODEL=llama3.2

# API Keys
CONSUMER_AFFAIRS_API_URL=https://data.gov.in/api/datastore/resource.json
LABOR_MINISTRY_API_URL=https://labour.gov.in/api
```

## 🎨 Design System

### Dark Mode FinTech Theme
- **Deep Midnight**: `#0f172a` - Primary background
- **Neon Blue**: `#3b82f6` - Primary accent
- **Gold Amber**: `#fbbf24` - Secondary accent
- **Glass Morphism**: Translucent UI elements with backdrop blur
- **Responsive Design**: Mobile-first approach with breakpoints

## 📊 Performance Metrics

### Demo Business (Mumbai Cafe)
- **Health Score**: 87/100 (Top 15% performer)
- **Daily Revenue**: ₹6,200 (₹248 per seat)
- **Profit Margin**: 34% (89% above industry average)
- **Seating Capacity**: 25 seats
- **Monthly Rent**: ₹45,000
- **Wastage Rate**: 5.2% (optimization target: 3.1%)

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- **Spring Boot Team** for the excellent framework
- **Ollama** for local AI model integration
- **Government of India** for open data APIs
- **Modern web standards** for responsive design capabilities

## 📞 Contact

- **Project**: ArthSethu Platform
- **Demo**: Open `DEMO_FIXED.html` for immediate testing
- **Issues**: Please report bugs via GitHub Issues

---

**🎯 Ready to revolutionize business intelligence with AI? Start with our standalone demo!**