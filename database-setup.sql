-- ArthSethu Database Setup Script
-- Run this script in your PostgreSQL database to create the required database

-- Create database (run this as postgres superuser)
-- CREATE DATABASE arthsethu;

-- Connect to arthsethu database and run the following:

-- The application will automatically create tables using JPA/Hibernate
-- with spring.jpa.hibernate.ddl-auto=update

-- Optional: Create indexes for better performance
-- These will be created automatically by JPA annotations, but listed here for reference

-- Users table indexes
-- CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
-- CREATE INDEX IF NOT EXISTS idx_users_tier ON users(tier);

-- Business profiles table indexes  
-- CREATE INDEX IF NOT EXISTS idx_business_profiles_user_id ON business_profiles(user_id);
-- CREATE INDEX IF NOT EXISTS idx_business_profiles_city ON business_profiles(city);
-- CREATE INDEX IF NOT EXISTS idx_business_profiles_business_type ON business_profiles(business_type);

-- Daily metrics table indexes
-- CREATE INDEX IF NOT EXISTS idx_daily_metrics_user_id ON daily_metrics(user_id);
-- CREATE INDEX IF NOT EXISTS idx_daily_metrics_date ON daily_metrics(metric_date);
-- CREATE INDEX IF NOT EXISTS idx_daily_metrics_user_date ON daily_metrics(user_id, metric_date);

-- Feasibility reports table indexes
-- CREATE INDEX IF NOT EXISTS idx_feasibility_reports_user_id ON feasibility_reports(user_id);
-- CREATE INDEX IF NOT EXISTS idx_feasibility_reports_generated_at ON feasibility_reports(generated_at);

-- Subscriptions table indexes
-- CREATE INDEX IF NOT EXISTS idx_subscriptions_user_id ON subscriptions(user_id);
-- CREATE INDEX IF NOT EXISTS idx_subscriptions_status ON subscriptions(status);
-- CREATE INDEX IF NOT EXISTS idx_subscriptions_tier ON subscriptions(tier);

-- Grant permissions (adjust as needed for your setup)
-- GRANT ALL PRIVILEGES ON DATABASE arthsethu TO postgres;
-- GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO postgres;
-- GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO postgres;

-- Verify connection
SELECT 'Database setup complete!' as status;