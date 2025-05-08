import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet("/AddStockServlet")
public class AddStockServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();

        String fruitId = request.getParameter("fruitId");
        String quantityAdded = request.getParameter("quantity");
        String vendorId = request.getParameter("vendorId");

        String url = "jdbc:mysql://localhost:3306/warehouse_system";
        String dbUser = "root";
        String dbPassword = "manish";

        Connection conn = null;
        PreparedStatement stmt1 = null;
        PreparedStatement stmt2 = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(url, dbUser, dbPassword);
            conn.setAutoCommit(false);

            // Update fruit quantity
            String sql1 = "UPDATE fruits SET quantity = quantity + ? WHERE id = ?";
            stmt1 = conn.prepareStatement(sql1);
            stmt1.setInt(1, Integer.parseInt(quantityAdded));
            stmt1.setInt(2, Integer.parseInt(fruitId));
            stmt1.executeUpdate();

            // Log stock update
            String sql2 = "INSERT INTO stock_updates (vendor_id, fruit_id, quantity_added) VALUES (?, ?, ?)";
            stmt2 = conn.prepareStatement(sql2);
            stmt2.setInt(1, Integer.parseInt(vendorId));
            stmt2.setInt(2, Integer.parseInt(fruitId));
            stmt2.setInt(3, Integer.parseInt(quantityAdded));
            stmt2.executeUpdate();

            conn.commit();
            out.print("Stock added successfully!");

        } catch (Exception e) {
            try { if (conn != null) conn.rollback(); } catch (SQLException se) { se.printStackTrace(); }
            e.printStackTrace();
            out.print("Error: " + e.getMessage());
        } finally {
            try {
                if (stmt1 != null) stmt1.close();
                if (stmt2 != null) stmt2.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}