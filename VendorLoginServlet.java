import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet("/VendorLoginServlet")
public class VendorLoginServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String staffId = request.getParameter("staffId");
        String userId = request.getParameter("userId");
        String password = request.getParameter("password");
        String secretPin = request.getParameter("secretPin");

        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();

        String url = "jdbc:mysql://localhost:3306/warehouse_system";
        String dbUser = "root";
        String dbPassword = "manish";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(url, dbUser, dbPassword);

            String sql = "SELECT * FROM staff WHERE id = ? AND user_id = ? AND password = ? AND secret_pin = ? AND role LIKE 'vendor%'";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, Integer.parseInt(staffId));
            pstmt.setString(2, userId);
            pstmt.setString(3, password);
            pstmt.setString(4, secretPin);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                HttpSession session = request.getSession();
                session.setAttribute("staffId", staffId);
                out.print(""); // Empty response for successful login
            } else {
                out.print("Invalid credentials or not a vendor.");
            }
        } catch (Exception e) {
            out.print("Error: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                out.print("Error closing resources: " + e.getMessage());
            }
        }
    }
}