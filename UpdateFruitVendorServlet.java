import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet("/UpdateFruitVendorServlet")
public class UpdateFruitVendorServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();

        String code = request.getParameter("code");
        String vendor = request.getParameter("vendor");

        String url = "jdbc:mysql://localhost:3306/warehouse_system";
        String dbUser = "root";
        String dbPassword = "manish"; // Replace with your actual DB password

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(url, dbUser, dbPassword);

            String sql = "UPDATE fruits SET vendor = ? WHERE code = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, vendor);
            pstmt.setString(2, code);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                out.println("Vendor updated successfully.");
            } else {
                out.println("Failed to update vendor.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            out.println("Error updating vendor: " + e.getMessage());
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}