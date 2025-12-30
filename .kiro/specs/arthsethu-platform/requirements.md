# Requirements Document

## Introduction

ArthSethu is a Financial Intelligence Platform designed to solve "Estimation Paralysis" for students and "Financial Blindness" for early-stage founders. The system acts as a "Digital Co-Founder" by providing hyper-local government data integration and AI-powered strategic guidance through a tiered subscription model.

## Glossary

- **ArthSethu_Platform**: The complete FinTech AI-SaaS system
- **Aarambh_Tier**: Free tier providing feasibility analysis and PDF reports
- **Vistar_Tier**: Professional tier (₹499/month) with operational dashboards and health scoring
- **Shikhar_Tier**: Enterprise tier (₹999/month) with AI CFO and strategic simulations
- **Dynamic_Questionnaire**: Adaptive decision-tree based intake form
- **Feasibility_Engine**: Service that fetches and processes government open data
- **Health_Score**: 0-100 metric tracking business margin stability and efficiency
- **AI_CFO**: LLM-powered financial advisor using Llama 3.2
- **Government_Data_API**: Integration with Ministry of Consumer Affairs and Labor Ministry data
- **Admin_Dashboard**: Platform management interface for system owners

## Requirements

### Requirement 1: Adaptive Onboarding System

**User Story:** As a potential entrepreneur, I want to complete a dynamic questionnaire that adapts based on my business type, so that I receive tailored feasibility analysis.

#### Acceptance Criteria

1. WHEN a user selects "Cafe" as business type, THE Dynamic_Questionnaire SHALL ask about seating capacity
2. WHEN a user selects "Cloud Kitchen" as business type, THE Dynamic_Questionnaire SHALL skip seating questions and ask about packaging costs
3. WHEN a user selects "Manufacturing" as business type, THE Dynamic_Questionnaire SHALL ask about power consumption and raw material sourcing
4. THE Dynamic_Questionnaire SHALL use decision tree logic to determine the next question based on previous answers
5. WHEN the questionnaire is completed, THE ArthSethu_Platform SHALL create a tailored user profile

### Requirement 2: Government Data Integration

**User Story:** As a user seeking accurate cost estimates, I want the system to fetch real-time government data, so that my feasibility analysis reflects current market conditions.

#### Acceptance Criteria

1. THE Feasibility_Engine SHALL integrate with Ministry of Consumer Affairs open data APIs
2. THE Feasibility_Engine SHALL integrate with Labor Ministry open data APIs
3. WHEN generating feasibility reports, THE Feasibility_Engine SHALL fetch current commercial rent data for the user's specific city
4. WHEN generating feasibility reports, THE Feasibility_Engine SHALL fetch current wage data for the user's location
5. WHEN generating feasibility reports, THE Feasibility_Engine SHALL fetch current commodity prices for relevant materials (milk, steel, fabric)

### Requirement 3: Feasibility Report Generation

**User Story:** As an entrepreneur, I want to download a professional feasibility report, so that I can present it to mentors or incubators.

#### Acceptance Criteria

1. WHEN a user completes the onboarding questionnaire, THE ArthSethu_Platform SHALL generate a comprehensive feasibility report
2. THE feasibility report SHALL include an executive summary of estimated costs
3. THE feasibility report SHALL include CAPEX vs OPEX pie charts
4. THE feasibility report SHALL include break-even analysis graphs
5. WHEN a user requests the report, THE ArthSethu_Platform SHALL provide one-click PDF download functionality

### Requirement 4: Operational Dashboard

**User Story:** As a business owner on the Vistar tier, I want to log daily metrics and track my business health, so that I can monitor my operational performance.

#### Acceptance Criteria

1. WHEN a Vistar_Tier user logs in daily, THE ArthSethu_Platform SHALL prompt for three metrics: sales, expenses, and wastage
2. THE ArthSethu_Platform SHALL calculate and display a Health_Score from 0-100 based on margin stability and efficiency
3. WHEN wastage increases, THE Health_Score SHALL decrease proportionally
4. THE ArthSethu_Platform SHALL display the Health_Score using a speedometer gauge visualization
5. THE ArthSethu_Platform SHALL show 30-day trend analysis using line graphs

### Requirement 5: Subscription Management

**User Story:** As a user wanting advanced features, I want to upgrade my subscription tier, so that I can access premium functionality.

#### Acceptance Criteria

1. WHEN a user clicks "Ask AI CFO" or "Advanced Analytics", THE ArthSethu_Platform SHALL display upgrade options
2. THE ArthSethu_Platform SHALL present Vistar tier at ₹499/month and Shikhar tier at ₹999/month
3. WHEN a user selects an upgrade, THE ArthSethu_Platform SHALL display a payment gateway modal
4. WHEN payment simulation is completed successfully, THE ArthSethu_Platform SHALL activate the selected tier
5. THE ArthSethu_Platform SHALL restrict feature access based on the user's current subscription tier

### Requirement 6: AI-Powered Financial Advisory

**User Story:** As a Shikhar tier subscriber, I want to interact with an AI CFO, so that I can receive strategic financial guidance and what-if simulations.

#### Acceptance Criteria

1. THE AI_CFO SHALL be powered by Llama 3.2 running locally via Ollama
2. THE AI_CFO SHALL have contextual awareness of the user's specific business data (rent, sales history, commodity prices)
3. WHEN a user asks a what-if question, THE AI_CFO SHALL provide quantitative analysis with specific recommendations
4. THE AI_CFO SHALL integrate user's historical data with current market conditions for accurate simulations
5. THE AI_CFO SHALL respond through a chat-based interface

### Requirement 7: Administrative Management

**User Story:** As a platform administrator, I want a comprehensive admin dashboard, so that I can manage users, monitor system health, and track revenue.

#### Acceptance Criteria

1. THE Admin_Dashboard SHALL display total active users across all tiers
2. THE Admin_Dashboard SHALL provide user management capabilities including ban and password reset functions
3. THE Admin_Dashboard SHALL monitor Government_Data_API scraping status with last update timestamps
4. THE Admin_Dashboard SHALL display revenue tracking graphs for Vistar and Shikhar subscriptions
5. WHEN the Local LLM goes offline or backend errors occur, THE Admin_Dashboard SHALL display system alerts

### Requirement 8: Data Persistence and Security

**User Story:** As a platform user, I want my financial data to be securely stored and quickly accessible, so that I can trust the platform with sensitive information.

#### Acceptance Criteria

1. THE ArthSethu_Platform SHALL use PostgreSQL for structured financial data storage
2. THE ArthSethu_Platform SHALL implement Spring Security for authentication and authorization
3. WHEN storing user financial data, THE ArthSethu_Platform SHALL encrypt sensitive information
4. THE ArthSethu_Platform SHALL maintain data integrity across all user interactions
5. THE ArthSethu_Platform SHALL provide fast data retrieval for dashboard and report generation

### Requirement 9: Web Interface and User Experience

**User Story:** As a user accessing the platform, I want a responsive and intuitive web interface, so that I can efficiently navigate and use all features.

#### Acceptance Criteria

1. THE ArthSethu_Platform SHALL provide a dark-themed, high-impact UI with "Don't just dream. Calculate." as hero text
2. THE ArthSethu_Platform SHALL use standard HTML, CSS, and Vanilla JavaScript for the frontend
3. THE ArthSethu_Platform SHALL serve templates through Thymeleaf integration
4. THE ArthSethu_Platform SHALL provide responsive design compatible with desktop and mobile devices
5. THE ArthSethu_Platform SHALL implement Chart.js for financial visualizations and health score gauges