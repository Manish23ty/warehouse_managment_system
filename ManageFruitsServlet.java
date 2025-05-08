import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import org.json.JSONArray;
import org.json.JSONObject;

@WebServlet("/ManageFruitsServlet")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, // 2MB
                 maxFileSize = 1024 * 1024 * 10,      // 10MB
                 maxRequestSize = 1024 * 1024 * 50)   // 50MB
public class ManageFruitsServlet extends HttpServlet {
    private static final String UPLOAD_DIR = "images";

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

            if ("getVendors".equals(action)) {
                response.setContentType("application/json");
                String sql = "SELECT id, name FROM staff WHERE role LIKE 'vendor%'";
                pstmt = conn.prepareStatement(sql);
                rs = pstmt.executeQuery();

                JSONArray vendorsArray = new JSONArray();
                while (rs.next()) {
                    JSONObject vendor = new JSONObject();
                    vendor.put("id", rs.getInt("id"));
                    vendor.put("name", rs.getString("name"));
                    vendorsArray.put(vendor);
                }
                out.print(vendorsArray.toString());
            } else if ("list".equals(action)) {
                String staffId = request.getParameter("staffId");
                response.setContentType("application/json");
                String sql = "SELECT f.*, s.name AS staff_name FROM fruits f LEFT JOIN staff s ON f.staff_id = s.id";
                if (staffId != null && !staffId.isEmpty()) {
                    sql += " WHERE f.staff_id = ?";
                }
                pstmt = conn.prepareStatement(sql);
                if (staffId != null && !staffId.isEmpty()) {
                    pstmt.setInt(1, Integer.parseInt(staffId));
                }
                rs = pstmt.executeQuery();

                JSONArray fruitsArray = new JSONArray();
                while (rs.next()) {
                    JSONObject fruit = new JSONObject();
                    fruit.put("id", rs.getInt("id"));
                    fruit.put("code", rs.getString("code"));
                    fruit.put("name", rs.getString("name"));
                    fruit.put("type", rs.getString("type"));
                    fruit.put("quantity", rs.getInt("quantity"));
                    fruit.put("pricePerCrate", rs.getDouble("price_per_crate"));
                    fruit.put("imageUrl", rs.getString("image_url"));
                    fruit.put("staffId", rs.getInt("staff_id"));
                    fruit.put("staffName", rs.getString("staff_name"));
                    fruitsArray.put(fruit);
                }
                out.print(fruitsArray.toString());
            } else if ("add".equals(action)) {
                String name = request.getParameter("name");
                String type = request.getParameter("type");
                String pricePerCrate = request.getParameter("pricePerCrate");
                String staffId = request.getParameter("staffId");
                Part filePart = request.getPart("imageFile");

                String prefix = name.length() >= 3 ? name.substring(0, 3).toLowerCase() : name.toLowerCase();
                String sqlCount = "SELECT COUNT(*) FROM fruits WHERE code LIKE ?";
                pstmt = conn.prepareStatement(sqlCount);
                pstmt.setString(1, prefix + "-%");
                rs = pstmt.executeQuery();
                int count = 0;
                if (rs.next()) {
                    count = rs.getInt(1);
                }
                String newCode = prefix + "-" + (count + 101);

                String imageUrl = null;
                if (filePart != null && filePart.getSize() > 0) {
                    String fileName = newCode + ".jpg";
                    String uploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIR;
                    File uploadDir = new File(uploadPath);
                    if (!uploadDir.exists()) uploadDir.mkdir();
                    String filePath = uploadPath + File.separator + fileName;
                    filePart.write(filePath);
                    imageUrl = "/" + UPLOAD_DIR + "/" + fileName;
                } else {
                    throw new ServletException("Image file is required.");
                }

                String sql = "INSERT INTO fruits (code, name, type, price_per_crate, image_url, staff_id) VALUES (?, ?, ?, ?, ?, ?)";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, newCode);
                pstmt.setString(2, name);
                pstmt.setString(3, type);
                pstmt.setDouble(4, Double.parseDouble(pricePerCrate));
                pstmt.setString(5, imageUrl);
                pstmt.setInt(6, Integer.parseInt(staffId));
                pstmt.executeUpdate();

                out.print("Fruit added successfully!");
            } else if ("update".equals(action)) {
                String id = request.getParameter("id");
                String name = request.getParameter("name");
                String type = request.getParameter("type");
                String pricePerCrate = request.getParameter("pricePerCrate");
                String staffId = request.getParameter("staffId");
                Part filePart = request.getPart("imageFile");

                String imageUrl = request.getParameter("existingImageUrl");
                if (filePart != null && filePart.getSize() > 0) {
                    String codeSql = "SELECT code FROM fruits WHERE id = ?";
                    pstmt = conn.prepareStatement(codeSql);
                    pstmt.setInt(1, Integer.parseInt(id));
                    rs = pstmt.executeQuery();
                    String code = null;
                    if (rs.next()) {
                        code = rs.getString("code");
                    }

                    String fileName = code + ".jpg";
                    String uploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIR;
                    File uploadDir = new File(uploadPath);
                    if (!uploadDir.exists()) uploadDir.mkdir();
                    String filePath = uploadPath + File.separator + fileName;
                    filePart.write(filePath);
                    imageUrl = "/" + UPLOAD_DIR + "/" + fileName;
                }

                String sql = "UPDATE fruits SET name = ?, type = ?, price_per_crate = ?, image_url = ?, staff_id = ? WHERE id = ?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, name);
                pstmt.setString(2, type);
                pstmt.setDouble(3, Double.parseDouble(pricePerCrate));
                pstmt.setString(4, imageUrl);
                pstmt.setInt(5, Integer.parseInt(staffId));
                pstmt.setInt(6, Integer.parseInt(id));
                int rows = pstmt.executeUpdate();

                if (rows > 0) {
                    out.print("Fruit updated successfully!");
                } else {
                    out.print("Fruit not found!");
                }
            } else if ("updateQuantity".equals(action)) {
                String id = request.getParameter("id");
                String quantity = request.getParameter("quantity");
                String staffId = request.getParameter("staffId");

                String checkSql = "SELECT staff_id FROM fruits WHERE id = ?";
                pstmt = conn.prepareStatement(checkSql);
                pstmt.setInt(1, Integer.parseInt(id));
                rs = pstmt.executeQuery();
                if (rs.next() && rs.getInt("staff_id") == Integer.parseInt(staffId)) {
                    String sql = "UPDATE fruits SET quantity = ? WHERE id = ?";
                    pstmt = conn.prepareStatement(sql);
                    pstmt.setInt(1, Integer.parseInt(quantity));
                    pstmt.setInt(2, Integer.parseInt(id));
                    int rows = pstmt.executeUpdate();

                    if (rows > 0) {
                        out.print("Quantity updated successfully!");
                    } else {
                        out.print("Fruit not found!");
                    }
                } else {
                    out.print("You are not authorized to update this fruit!");
                }
            } else if ("delete".equals(action)) {
                String id = request.getParameter("id");

                String sql = "DELETE FROM fruits WHERE id = ?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, Integer.parseInt(id));
                int rows = pstmt.executeUpdate();

                if (rows > 0) {
                    out.print("Fruit deleted successfully!");
                } else {
                    out.print("Fruit not found!");
                }
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