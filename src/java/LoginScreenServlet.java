import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LoginScreenServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private Connection con;
    private String uEmail;
    private String uPassword;
    private String uRole;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

    }

    public void LoginScreenServlet() {
        try {
            // Load Driver
            String driver = getServletConfig().getInitParameter("dbDriver");
            Class.forName(driver);

            // Establish Connection
            String url = getServletConfig().getInitParameter("dbURL");
            String username = getServletConfig().getInitParameter("dbUname");
            String password = getServletConfig().getInitParameter("dbPassword");
            this.con = DriverManager.getConnection(url, username, password);
            System.out.println("Connected to: " + url);

        } catch (SQLException | ClassNotFoundException sqle) {
            sqle.printStackTrace();
        }

    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet LoginScreenServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet LoginScreenServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Debug statements
        System.out.println("Attribute set: " + request.getAttribute("databaseContents"));
        System.out.println("Forwarding to success.jsp");

        RequestDispatcher dispatcher = request.getRequestDispatcher("/success.jsp");
        dispatcher.forward(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        LoginScreenServlet();
        HttpSession session = request.getSession();

        String captcha = (String) session.getAttribute("captcha");
        String captchaInput = request.getParameter("captcha_input");

        if (captcha == null || !captcha.equals(captchaInput)) {
            request.setAttribute("error", "Invalid CAPTCHA");
            request.getRequestDispatcher("/index.jsp").forward(request, response);
            return;
        }
        String enteredEmail = request.getParameter("email");
        String enteredPassword = request.getParameter("password");

        if (enteredEmail.isEmpty() && enteredPassword.isEmpty()) {
            request.getRequestDispatcher("/noLoginCredentialsError.jsp").forward(request, response);
        }

        if (enteredEmail.isEmpty() || enteredPassword.isEmpty()) {
            if (enteredEmail.isEmpty()) {
                request.getRequestDispatcher("/noUsernameError.jsp").forward(request, response);
            } else {
                request.getRequestDispatcher("/noPasswordError.jsp").forward(request, response);
            }
        } else {
            boolean loginSuccessful = false;
            try {
                loginSuccessful = validateLogin(enteredEmail, enteredPassword);
            } catch (SQLException ex) {
                Logger.getLogger(LoginScreenServlet.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (loginSuccessful) {
                try {
                    String userRole = getUserRole(enteredEmail);
                    request.getSession().setAttribute("role", userRole);

                    session = request.getSession();
                    session.setAttribute("email", enteredEmail);
                    session.setAttribute("password", enteredPassword);

                    String encryptedPassword = Security.encrypt(enteredPassword);

                    PreparedStatement ps = con.prepareStatement("SELECT * FROM USER_INFO WHERE username = ? AND encrypted = ?");
                    ps.setString(1, enteredEmail);
                    ps.setString(2, encryptedPassword);

                    ResultSet rs = ps.executeQuery();

                    if (rs.next()) {
                        session.setAttribute("encryptedPassword", encryptedPassword);

                        Security.updateEncryptPass(con, enteredEmail, enteredPassword);

                        updateEncryptPass(con, enteredEmail, enteredPassword);
                        request.getRequestDispatcher("/success.jsp").forward(request, response);
                    } else {
                        request.getRequestDispatcher("/incorrectPasswordError.jsp").forward(request, response);
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(LoginScreenServlet.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                if (isUserInDB(enteredEmail)) {
                    request.getRequestDispatcher("/incorrectPasswordError.jsp").forward(request, response);
                } else {
                    request.getRequestDispatcher("/userNotFoundError.jsp").forward(request, response);
                }
            }
        }
    }

    private boolean isUserInDB(String email) {
        try {
            String query = "SELECT * FROM USER_INFO WHERE Username=?";
            try (PreparedStatement preparedStatement = con.prepareStatement(query)) {
                preparedStatement.setString(1, email);
                ResultSet rs = preparedStatement.executeQuery();
                return rs.next();
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            return false;
        }
    }

    public boolean validateLogin(String email, String password) throws SQLException {
        String query = "SELECT * FROM USER_INFO WHERE Username=? AND Password=?";
        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setString(1, email);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        }
    }

    public static void updateEncryptPass(Connection con, String username, String newPassword) {
        try {
            // Define the SQL query to update the encrypted password for a given username
            String query = "UPDATE APP.USER_INFO SET ENCRYPTED = ? WHERE username = ?";
            // Create a PreparedStatement object using the provided Connection and the SQL query
            PreparedStatement preparedStatement = con.prepareStatement(query);

            String newPass = Security.encrypt(newPassword);
            // Set the encrypted password and the username in the PreparedStatement
            preparedStatement.setString(1, newPass);
            preparedStatement.setString(2, username);

            // Execute the update query
            preparedStatement.executeUpdate();

            // Close the PreparedStatement to release resources
            preparedStatement.close();
        } catch (SQLException ex) {
            // Handle any SQL exceptions by printing the stack trace
            ex.printStackTrace();
        }
    }

    public String getUserRole(String email) {
        try {
            String query = "SELECT Role FROM USER_INFO WHERE Username=?";
            try (PreparedStatement preparedStatement = con.prepareStatement(query)) {
                preparedStatement.setString(1, email);
                ResultSet rs = preparedStatement.executeQuery();
                if (rs.next()) {
                    return rs.getString("Role"); // Returns the UserRole if a record is found
                }
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
        return null; // Returns null if no record is found
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
