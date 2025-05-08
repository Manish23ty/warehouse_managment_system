import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();

        // Debugging: Log that the servlet is called
        out.println("Servlet called successfully");

        // Get form data
        String name = request.getParameter("name");
        String mobile = request.getParameter("mobile");
        String email = request.getParameter("email");
        String storeName = request.getParameter("storeName");
        String address = request.getParameter("address");
        String pincode = request.getParameter("pincode");
        String district = request.getParameter("district");
        String state = request.getParameter("state");
        String userId = request.getParameter("userId");
        String password = request.getParameter("password");

        // Debugging: Log individual form data
        out.println("Name: " + name);
        out.println("Mobile: " + mobile);
        out.println("Email: " + email);
        out.println("Store Name: " + storeName);
        out.println("Address: " + address);
        out.println("Pincode: " + pincode);
        out.println("District: " + district);
        out.println("State: " + state);
        out.println("User ID: " + userId);
        out.println("Password: " + password);

        // Check for null or empty values
        if (name == null || name.trim().isEmpty() ||
            mobile == null || mobile.trim().isEmpty() ||
            email == null || email.trim().isEmpty() ||
            storeName == null || storeName.trim().isEmpty() ||
            address == null || address.trim().isEmpty() ||
            pincode == null || pincode.trim().isEmpty() ||
            district == null || district.trim().isEmpty() ||
            state == null || state.trim().isEmpty() ||
            userId == null || userId.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {
            out.println("Error: All fields are required and cannot be empty.");
            return;
        }

        // Database connection details
        String url = "jdbc:mysql://localhost:3306/warehouse_system";
        String dbUser = "root"; // Replace with your MySQL username
        String dbPassword = "manish"; // Replace with your MySQL password

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            out.println("JDBC Driver loaded successfully");

            // Establish connection
            conn = DriverManager.getConnection(url, dbUser, dbPassword);
            out.println("Database connection established");

            // SQL query to insert customer data
            String sql = "INSERT INTO customers (name, mobile, email, store_name, address, pincode, district, state, user_id, password) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setString(2, mobile);
            stmt.setString(3, email);
            stmt.setString(4, storeName);
            stmt.setString(5, address);
            stmt.setString(6, pincode);
            stmt.setString(7, district);
            stmt.setString(8, state);
            stmt.setString(9, userId);
            stmt.setString(10, password);

            // Execute the query
            int rows = stmt.executeUpdate();
            out.println("Rows inserted: " + rows);
            if (rows > 0) {
                out.println("Registration successful! You can now login.");
                response.sendRedirect("register.jsp");
            } else {
                out.println("Registration failed. Please try again.");
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            out.println("Driver Error: " + e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
            out.println("SQL Error: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            out.println("General Error: " + e.getMessage());
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
                out.println("Database connection closed");
            } catch (SQLException e) {
                e.printStackTrace();
                out.println("Error closing connection: " + e.getMessage());
            }
        }
    }
}