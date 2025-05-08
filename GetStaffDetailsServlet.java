import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import org.json.JSONObject;

@WebServlet("/GetStaffDetailsServlet")
public class GetStaffDetailsServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession(false);
        JSONObject staffDetails = new JSONObject();

        if (session != null && session.getAttribute("staffRole") != null) {
            staffDetails.put("role", session.getAttribute("staffRole"));
            staffDetails.put("name", session.getAttribute("staffName"));
        } else {
            staffDetails.put("role", "unknown");
        }

        out.println(staffDetails.toString());
    }
}