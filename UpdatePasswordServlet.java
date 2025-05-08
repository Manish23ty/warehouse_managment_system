import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet("/UpdatePasswordServlet")
public class UpdatePasswordServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();

        String userId = request.getParameter("userId");
        String newPassword = request.getParameter("newPassword");

        String url = "jdbc:mysql://localhost:3306/warehouse_system";
        String dbUser = "root";
        String dbPassword = "manish";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(url, dbUser, dbPassword);

            // Check if userId exists in customers table
            String sql = "SELECT * FROM customers WHERE user_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, userId);
            rs = stmt.executeQuery();

            if (rs.next()) {
                // Update password in customers table
                sql = "UPDATE customers SET password = ? WHERE user_id = ?";
                stmt = conn.prepareStatement(sql);
                stmt.setString(1, newPassword);
                stmt.setString(2, userId);
                int rows = stmt.executeUpdate();
                if (rows > 0) {
                    out.println("Password updated successfully!");
                } else {
                    out.println("Failed to update password. User ID not found.");
                }
                return;
            }

            // Check if userId exists in vendors table
            sql = "SELECT * FROM vendors WHERE user_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, userId);
            rs = stmt.executeQuery();

            if (rs.next()) {
                // Update password in vendors table
                sql = "UPDATE vendors SET password = ? WHERE user_id = ?";
                stmt = conn.prepareStatement(sql);
                stmt.setString(1, newPassword);
                stmt.setString(2, userId);
                int rows = stmt.executeUpdate();
                if (rows > 0) {
                    out.println("Password updated successfully!");
                } else {
                    out.println("Failed to update password. User ID not found.");
                }
                return;
            }

            // Check if userId exists in staff table
            sql = "SELECT * FROM staff WHERE user_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, userId);
            rs = stmt.executeQuery();

            if (rs.next()) {
                // Update password in staff table
                sql = "UPDATE staff SET password = ? WHERE user_id = ?";
                stmt = conn.prepareStatement(sql);
                stmt.setString(1, newPassword);
                stmt.setString(2, userId);
                int rows = stmt.executeUpdate();
                if (rows > 0) {
                    out.println("Password updated successfully!");
                } else {
                    out.println("Failed to update password. User ID not found.");
                }
                return;
            }

            out.println("Failed to update password. User ID not found.");

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("Error: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}