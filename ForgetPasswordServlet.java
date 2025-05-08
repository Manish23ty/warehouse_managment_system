import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet("/ForgetPasswordServlet")
public class ForgetPasswordServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();

        String userId = request.getParameter("userId");
        String mobile = request.getParameter("mobile");

        String url = "jdbc:mysql://localhost:3306/warehouse_system";
        String dbUser = "root";
        String dbPassword = "manish";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(url, dbUser, dbPassword);

            // Check in customers table
            String sql = "SELECT * FROM customers WHERE user_id = ? AND mobile = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, userId);
            stmt.setString(2, mobile);
            rs = stmt.executeQuery();

            if (rs.next()) {
                out.println("Match found");
                return;
            }

            // Check in vendors table
            sql = "SELECT * FROM vendors WHERE user_id = ? AND mobile = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, userId);
            stmt.setString(2, mobile);
            rs = stmt.executeQuery();

            if (rs.next()) {
                out.println("Match found");
                return;
            }

            // Check in staff table
            sql = "SELECT * FROM staff WHERE user_id = ? AND mobile = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, userId);
            stmt.setString(2, mobile);
            rs = stmt.executeQuery();

            if (rs.next()) {
                out.println("Match found");
                return;
            }

            out.println("Invalid User ID or Mobile Number. Please try again.");

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