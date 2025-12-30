# ArthSethu Platform - Startup Guide

## 🚀 How to Run the Application

### Prerequisites
1. **PostgreSQL Database**: Ensure PostgreSQL is running on localhost:5432
2. **Java 17+**: Make sure Java 17 or higher is installed
3. **Maven**: Maven should be available (or use the included Maven wrapper)

### Step 1: Database Setup
1. **Create Database** (if not exists):
   ```sql
   CREATE DATABASE arthsethu;
   ```

2. **Verify Connection**: The application will connect using:
   - Database: `arthsethu`
   - Username: `postgres`
   - Password: `mpvsSQL216`

### Step 2: Run the Application

#### Option A: Using Maven Wrapper (Recommended)
```bash
./mvnw.cmd spring-boot:run
```

#### Option B: Using Maven (if installed)
```bash
mvn spring-boot:run
```

#### Option C: Using Java directly
```bash
# First compile
./mvnw.cmd clean package -DskipTests

# Then run
java -jar target/arthsethu-platform-1.0.0.jar
```

### Step 3: Access the Application

Once started, the application will be available at:
**http://localhost:8080/arthsethu**

## 👨‍💼 How to Become Admin

### Method 1: Default Admin Account (Auto-Created)
The application automatically creates a default admin account:
- **Email**: `admin@arthsethu.com`
- **Password**: `admin123`
- **Role**: ADMIN

### Method 2: Register as Admin
1. Go to: http://localhost:8080/arthsethu/register
2. Fill in your email and password
3. **Admin Code**: `ARTHSETHU_ADMIN_2024`
4. Click Register

### Method 3: Database Direct Update
If you already have a user account, you can make it admin via database:
```sql
UPDATE users SET role = 'ADMIN' WHERE email = 'your-email@example.com';
```

## 🔗 Application URLs

### Main Application
- **Home**: http://localhost:8080/arthsethu/
- **Login**: http://localhost:8080/arthsethu/login
- **Register**: http://localhost:8080/arthsethu/register

### Admin Dashboard (Requires Admin Role)
- **Admin Dashboard**: http://localhost:8080/arthsethu/admin/dashboard
- **User Management**: http://localhost:8080/arthsethu/admin/users
- **System Monitoring**: http://localhost:8080/arthsethu/admin/system
- **Revenue Analytics**: http://localhost:8080/arthsethu/admin/revenue

### User Features
- **Dashboard**: http://localhost:8080/arthsethu/dashboard
- **Onboarding**: http://localhost:8080/arthsethu/onboarding/start
- **AI CFO**: http://localhost:8080/arthsethu/ai-cfo/chat (Shikhar tier)
- **Metrics**: http://localhost:8080/arthsethu/dashboard/vistar (Vistar tier)

## 🔧 Troubleshooting

### Database Connection Issues
1. **Check PostgreSQL is running**:
   ```bash
   # Windows
   net start postgresql-x64-14
   
   # Or check services
   services.msc
   ```

2. **Verify database exists**:
   ```sql
   \l  -- List databases in psql
   ```

3. **Check credentials**: Ensure username `postgres` and password `mpvsSQL216` are correct

### Application Won't Start
1. **Check Java version**:
   ```bash
   java -version  # Should be 17+
   ```

2. **Check port 8080**: Make sure port 8080 is not in use
   ```bash
   netstat -an | findstr :8080
   ```

3. **Check logs**: Look for error messages in the console output

### Admin Access Issues
1. **Clear browser cache** and try again
2. **Check user role** in database:
   ```sql
   SELECT email, role FROM users WHERE email = 'your-email@example.com';
   ```

3. **Restart application** after making database changes

## 📊 Application Features

### For Regular Users (FREE - Aarambh Tier)
- ✅ Business onboarding questionnaire
- ✅ Feasibility report generation
- ✅ PDF download
- ✅ Basic dashboard

### For Vistar Tier (₹499/month)
- ✅ All Aarambh features
- ✅ Daily metrics tracking
- ✅ Health score calculation
- ✅ Advanced analytics

### For Shikhar Tier (₹999/month)
- ✅ All Vistar features
- ✅ AI CFO chat interface
- ✅ What-if analysis
- ✅ Strategic recommendations

### For Admins
- ✅ User management
- ✅ System monitoring
- ✅ Revenue analytics
- ✅ User ban/unban
- ✅ Password reset

## 🎯 First Steps After Login

### As Admin:
1. Login with admin credentials
2. Go to Admin Dashboard
3. Check system health
4. Review user statistics
5. Monitor revenue metrics

### As User:
1. Register/Login
2. Complete onboarding questionnaire
3. Generate feasibility report
4. Upgrade to Vistar/Shikhar for advanced features

## 🔐 Security Notes

- **Change default admin password** immediately after first login
- **Admin code** (`ARTHSETHU_ADMIN_2024`) should be kept secret
- **Database credentials** are configured in application.properties
- **CSRF is disabled** for development (enable for production)

## 📞 Support

If you encounter issues:
1. Check the console logs for error messages
2. Verify database connection
3. Ensure all prerequisites are met
4. Check the troubleshooting section above

---

**🎉 Your ArthSethu Platform is ready to go!**