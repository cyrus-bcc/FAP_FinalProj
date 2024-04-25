/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.*;
import java.io.*;
import java.io.IOException;
import java.io.PrintWriter;
import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author purp
 */
public class CaptchaServlet extends HttpServlet {

    // Java pprogram to automatically generate CAPTCHA and
    // verify user
    // Returns true if given two strings are same
    static boolean checkCaptcha(String captcha, String user_captcha) {
        return captcha.equals(user_captcha);
    }

    // Generates a CAPTCHA of given length
    static String generateCaptcha(int n) {
        //to generate random integers in the range [0-61]
        Random rand = new Random(62);

        // Characters to be included
        String chrs = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

        // Generate n characters from above set and
        // add these characters to captcha.
        String captcha = "";
        while (n-- > 0) {
            int index = (int) (Math.random() * 62);
            captcha += chrs.charAt(index);
        }

        return captcha;
    }

    // Driver code
    public void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        // Generate a random CAPTCHA
        String captchaLengthStr = getServletContext().getInitParameter("captchaLength");
        int captchaLength = Integer.parseInt(captchaLengthStr);
        String captcha = generateCaptcha(captchaLength);
        System.out.println(captcha);

        // Ask user to enter a CAPTCHA
        System.out.println("Enter above CAPTCHA: ");
        String usr_captcha = reader.readLine();

        // Notify user about matching status
        if (checkCaptcha(captcha, usr_captcha)) {
            System.out.println("CAPTCHA Matched");
        } else {
            System.out.println("CAPTCHA Not Matched");
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
            out.println("<title>Servlet CaptchaServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet CaptchaServlet at " + request.getContextPath() + "</h1>");
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
        // Generate CAPTCHA

        String captchaLengthStr = getServletContext().getInitParameter("captchaLength");
        int captchaLength = Integer.parseInt(captchaLengthStr);
        String captcha = generateCaptcha(captchaLength);

        // Store CAPTCHA in session
        HttpSession session = request.getSession();
        session.setAttribute("captcha", captcha);
        System.out.println("Generated CAPTCHA: " + captcha); // Log statement for debugging

        // Generate CAPTCHA image
        int width = 400;
        int height = 50;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        // Set background color
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);

        // Draw text
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        g2d.drawString(captcha, 10, 30);

        // Dispose resources
        g2d.dispose();

        // Write image to response
        response.setContentType("imagse/png");
        ImageIO.write(image, "png", response.getOutputStream());
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
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
