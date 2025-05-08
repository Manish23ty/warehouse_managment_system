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

@WebServlet("/GetCustomersServlet")
public class GetCustomersServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        String url = "jdbc:mysql://localhost:3306/warehouse_system";
        String dbUser = "root";
        String dbPassword = "manish"; // Replace with your actual DB password

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        JSONArray customers = new JSONArray();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(url, dbUser, dbPassword);
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM customers");

            while (rs.next()) {
                JSONObject customer = new JSONObject();
                customer.put("user_id", rs.getString("user_id"));
                customer.put("name", rs.getString("name"));
                customer.put("email", rs.getString("email"));
                customer.put("mobile", rs.getString("mobile"));
                customers.put(customer);
            }

            out.println(customers.toString());
        } catch (Exception e) {
            e.printStackTrace();
            out.println("[]");
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