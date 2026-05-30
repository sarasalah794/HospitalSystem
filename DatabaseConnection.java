package hospital.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Manages the database connection for the Hospital Management System.
 * Uses SQLite via JDBC. Implements a Singleton pattern.
 *
 * @author Student
 * @version 1.0
 */
public class DatabaseConnection {

    private static final String DB_URL = "jdbc:sqlite:hospital.db";
    private static Connection connection = null;

    private DatabaseConnection() {}

    /**
     * Returns the single shared Connection to the database.
     * @return the active database connection
     * @throws SQLException if the connection cannot be established
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL);
            System.out.println("Database connected successfully.");
        }
        return connection;
    }

    /**
     * Closes the database connection if it is open.
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }

    /**
     * Creates all required tables if they do not already exist.
     * @throws SQLException if a table creation query fails
     */
    public static void initializeDatabase() throws SQLException {
        Connection conn = getConnection();
        Statement stmt = conn.createStatement();

        stmt.execute("CREATE TABLE IF NOT EXISTS patients ("
                + "id INTEGER PRIMARY KEY, "
                + "name TEXT NOT NULL, "
                + "age INTEGER, "
                + "phone TEXT, "
                + "email TEXT, "
                + "blood_type TEXT, "
                + "allergies TEXT, "
                + "status TEXT, "
                + "room_number TEXT)");

        stmt.execute("CREATE TABLE IF NOT EXISTS doctors ("
                + "id INTEGER PRIMARY KEY, "
                + "name TEXT NOT NULL, "
                + "age INTEGER, "
                + "phone TEXT, "
                + "email TEXT, "
                + "specialty TEXT, "
                + "license_number TEXT, "
                + "available INTEGER DEFAULT 1)");

        stmt.execute("CREATE TABLE IF NOT EXISTS staff ("
                + "id INTEGER PRIMARY KEY, "
                + "name TEXT NOT NULL, "
                + "age INTEGER, "
                + "phone TEXT, "
                + "email TEXT, "
                + "staff_role TEXT, "
                + "employee_number TEXT, "
                + "department TEXT)");

        stmt.execute("CREATE TABLE IF NOT EXISTS appointments ("
                + "appointment_id INTEGER PRIMARY KEY, "
                + "patient_id INTEGER, "
                + "doctor_id INTEGER, "
                + "date TEXT, "
                + "time TEXT, "
                + "status TEXT, "
                + "notes TEXT, "
                + "FOREIGN KEY(patient_id) REFERENCES patients(id), "
                + "FOREIGN KEY(doctor_id) REFERENCES doctors(id))");

        stmt.execute("CREATE TABLE IF NOT EXISTS medical_records ("
                + "record_id INTEGER PRIMARY KEY, "
                + "patient_id INTEGER, "
                + "doctor_id INTEGER, "
                + "date TEXT, "
                + "diagnosis TEXT, "
                + "treatment TEXT, "
                + "medications TEXT, "
                + "notes TEXT, "
                + "FOREIGN KEY(patient_id) REFERENCES patients(id), "
                + "FOREIGN KEY(doctor_id) REFERENCES doctors(id))");

        stmt.execute("CREATE TABLE IF NOT EXISTS bills ("
                + "bill_id INTEGER PRIMARY KEY, "
                + "patient_id INTEGER, "
                + "bill_date TEXT, "
                + "consultation_fee REAL, "
                + "medication_fee REAL, "
                + "lab_fee REAL, "
                + "room_fee REAL, "
                + "payment_status TEXT, "
                + "FOREIGN KEY(patient_id) REFERENCES patients(id))");

        stmt.close();
        System.out.println("All tables initialized successfully.");
    }
}
