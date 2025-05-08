import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import org.json.JSONArray;
import org.json.JSONObject;

@WebServlet("/OrderServlet")
public class OrderServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
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

            if ("list".equals(action)) {
                response.setContentType("application/json");
                String sql = "SELECT * FROM orders";
                pstmt = conn.prepareStatement(sql);
                rs = pstmt.executeQuery();

                JSONArray ordersArray = new JSONArray();
                while (rs.next()) {
                    JSONObject order = new JSONObject();
                    order.put("id", rs.getInt("id"));
                    order.put("customerId", rs.getInt("customer_id"));
                    order.put("fruitId", rs.getInt("fruit_id"));
                    order.put("quantity", rs.getInt("quantity"));
                    order.put("totalPrice", rs.getDouble("total_price"));
                    order.put("orderDate", rs.getString("order_date"));
                    ordersArray.put(order);
                }
                out.print(ordersArray.toString());
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