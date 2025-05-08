import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import org.json.JSONObject;

@WebServlet("/GetCustomerDetailsServlet")
public class GetCustomerDetailsServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession(false);
        JSONObject customerDetails = new JSONObject();

        if (session != null && session.getAttribute("customerName") != null) {
            customerDetails.put("name", session.getAttribute("customerName"));
        } else {
            customerDetails.put("name", "Customer");
        }

        out.println(customerDetails.toString());
    }
}