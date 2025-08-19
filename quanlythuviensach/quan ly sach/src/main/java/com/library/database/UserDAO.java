package com.library.database;

import com.library.model.Student;
import com.library.model.Staff;
import com.library.model.User;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for User operations
 */
public class UserDAO {
    private static final Logger LOGGER = Logger.getLogger(UserDAO.class.getName());
    private final Database database;
    
    public UserDAO() {
        this.database = Database.getInstance();
    }
    
    /**
     * Save a new user to the database
     */
    public boolean save(User user) {
        String sql = """
            INSERT INTO users (id, name, user_type, major_department, enrollment_hire_date, is_active)
            VALUES (?, ?, ?, ?, ?, ?)
        """;
        
        try {
            String userType = user instanceof Student ? "student" : "staff";
            String majorDepartment = null;
            String enrollmentHireDate = null;
            
            if (user instanceof Student) {
                Student student = (Student) user;
                majorDepartment = student.getMajor();
                enrollmentHireDate = student.getEnrollmentDate() != null ? 
                    student.getEnrollmentDate().toString() : null;
            } else if (user instanceof Staff) {
                Staff staff = (Staff) user;
                majorDepartment = staff.getDepartment();
                enrollmentHireDate = staff.getHireDate() != null ? 
                    staff.getHireDate().toString() : null;
            }
            
            int result = database.executeUpdate(sql,
                user.getId(),
                user.getName(),
                userType,
                majorDepartment,
                enrollmentHireDate,
                user instanceof Student ? ((Student) user).isActive() : ((Staff) user).isActive()
            );
            
            LOGGER.info("User saved successfully: " + user.getName());
            return result > 0;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error saving user: " + user.getName(), e);
            return false;
        }
    }
    
    /**
     * Update an existing user
     */
    public boolean update(User user) {
        String sql = """
            UPDATE users SET name=?, major_department=?, enrollment_hire_date=?, is_active=?
            WHERE id=?
        """;
        
        try {
            String majorDepartment = null;
            String enrollmentHireDate = null;
            boolean isActive = true;
            
            if (user instanceof Student) {
                Student student = (Student) user;
                majorDepartment = student.getMajor();
                enrollmentHireDate = student.getEnrollmentDate() != null ? 
                    student.getEnrollmentDate().toString() : null;
                isActive = student.isActive();
            } else if (user instanceof Staff) {
                Staff staff = (Staff) user;
                majorDepartment = staff.getDepartment();
                enrollmentHireDate = staff.getHireDate() != null ? 
                    staff.getHireDate().toString() : null;
                isActive = staff.isActive();
            }
            
            int result = database.executeUpdate(sql,
                user.getName(),
                majorDepartment,
                enrollmentHireDate,
                isActive,
                user.getId()
            );
            
            LOGGER.info("User updated successfully: " + user.getName());
            return result > 0;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating user: " + user.getName(), e);
            return false;
        }
    }
    
    /**
     * Delete a user by ID
     */
    public boolean delete(String userId) {
        String sql = "DELETE FROM users WHERE id = ?";
        
        try {
            int result = database.executeUpdate(sql, userId);
            LOGGER.info("User deleted successfully: " + userId);
            return result > 0;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting user: " + userId, e);
            return false;
        }
    }
    
    /**
     * Find a user by ID
     */
    public User findById(String userId) {
        String sql = "SELECT * FROM users WHERE id = ?";
        
        try (ResultSet rs = database.executeQuery(sql, userId)) {
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding user by ID: " + userId, e);
        }
        
        return null;
    }
    
    /**
     * Find all users
     */
    public List<User> findAll() {
        String sql = "SELECT * FROM users ORDER BY name";
        List<User> users = new ArrayList<>();
        
        try (ResultSet rs = database.executeQuery(sql)) {
            while (rs.next()) {
                User user = mapResultSetToUser(rs);
                if (user != null) {
                    users.add(user);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding all users", e);
        }
        
        return users;
    }
    
    /**
     * Search users by name
     */
    public List<User> search(String searchTerm) {
        String sql = """
            SELECT * FROM users 
            WHERE LOWER(name) LIKE LOWER(?) OR LOWER(id) LIKE LOWER(?)
            ORDER BY name
        """;
        
        List<User> users = new ArrayList<>();
        String searchPattern = "%" + searchTerm + "%";
        
        try (ResultSet rs = database.executeQuery(sql, searchPattern, searchPattern)) {
            while (rs.next()) {
                User user = mapResultSetToUser(rs);
                if (user != null) {
                    users.add(user);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error searching users", e);
        }
        
        return users;
    }
    
    /**
     * Find users by type
     */
    public List<User> findByType(String userType) {
        String sql = "SELECT * FROM users WHERE user_type = ? ORDER BY name";
        List<User> users = new ArrayList<>();
        
        try (ResultSet rs = database.executeQuery(sql, userType)) {
            while (rs.next()) {
                User user = mapResultSetToUser(rs);
                if (user != null) {
                    users.add(user);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding users by type: " + userType, e);
        }
        
        return users;
    }
    
    /**
     * Find active users
     */
    public List<User> findActiveUsers() {
        String sql = "SELECT * FROM users WHERE is_active = TRUE ORDER BY name";
        List<User> users = new ArrayList<>();
        
        try (ResultSet rs = database.executeQuery(sql)) {
            while (rs.next()) {
                User user = mapResultSetToUser(rs);
                if (user != null) {
                    users.add(user);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding active users", e);
        }
        
        return users;
    }
    
    /**
     * Update user active status
     */
    public boolean updateActiveStatus(String userId, boolean isActive) {
        String sql = "UPDATE users SET is_active = ? WHERE id = ?";
        
        try {
            int result = database.executeUpdate(sql, isActive, userId);
            return result > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating active status for user: " + userId, e);
            return false;
        }
    }
    
    /**
     * Get user count by type
     */
    public int getUserCount(String userType) {
        String sql = "SELECT COUNT(*) FROM users WHERE user_type = ? AND is_active = TRUE";
        
        try (ResultSet rs = database.executeQuery(sql, userType)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting user count", e);
        }
        
        return 0;
    }
    
    /**
     * Check if user ID exists
     */
    public boolean existsById(String userId) {
        String sql = "SELECT 1 FROM users WHERE id = ?";
        
        try (ResultSet rs = database.executeQuery(sql, userId)) {
            return rs.next();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking user existence: " + userId, e);
            return false;
        }
    }
    
    /**
     * Map ResultSet to User object
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        String name = rs.getString("name");
        String userType = rs.getString("user_type");
        String majorDepartment = rs.getString("major_department");
        String enrollmentHireDateStr = rs.getString("enrollment_hire_date");
        boolean isActive = rs.getBoolean("is_active");
        
        LocalDate enrollmentHireDate = null;
        if (enrollmentHireDateStr != null) {
            try {
                enrollmentHireDate = LocalDate.parse(enrollmentHireDateStr);
            } catch (Exception e) {
                LOGGER.warning("Invalid date format for user: " + id);
            }
        }
        
        if ("student".equals(userType)) {
            Student student = new Student(name, id, majorDepartment, enrollmentHireDate);
            student.setActive(isActive);
            return student;
        } else if ("staff".equals(userType)) {
            Staff staff = new Staff(name, id, majorDepartment, enrollmentHireDate);
            staff.setActive(isActive);
            return staff;
        }
        
        LOGGER.warning("Unknown user type: " + userType + " for user: " + id);
        return null;
    }
}
