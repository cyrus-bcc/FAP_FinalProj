/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfDocument;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Choco
 */
public class ReportGenerator extends HttpServlet {

    private static final Font BOLD_ITALIC = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLDITALIC);

    private void generateReports(HttpServletResponse response, String userType, String ownerEmail) throws DocumentException, SQLException, IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=Reports.pdf");

        Document document;
        PdfWriter writer;

        if (userType.equalsIgnoreCase("Admin")) {
            document = new Document(PageSize.LETTER);
            writer = PdfWriter.getInstance(document, response.getOutputStream());
        } else {
            // Use a custom page size for the GUEST report
            Rectangle customPageSize = new Rectangle(700, 250); // Width, Height
            document = new Document(customPageSize);
            writer = PdfWriter.getInstance(document, response.getOutputStream());
        }

        document.open();
        document.addTitle("Reports");

        // Add report type
        Paragraph reportType = new Paragraph(userType.equalsIgnoreCase("Admin") ? "Admin Report" : "Guest Report", BOLD_ITALIC);
        reportType.setAlignment(Element.ALIGN_CENTER);
        document.add(reportType);

        // Add page numbers
        PdfContentByte cb = writer.getDirectContent();
        Phrase footer;

        // Add owner information
        Phrase owner = new Phrase("Owner: " + ownerEmail, FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10));
        ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, owner, document.left(), document.bottom() - 18, 0);

        
        MyFooter event = new MyFooter();

        event.onEndPage(writer, document);

        Connection con = DriverManager.getConnection("jdbc:derby://localhost:1527/UserDB", "app", "app");
        PreparedStatement ps;

        if (userType.equalsIgnoreCase("Admin")) {
            // Add Admin Report
            ps = con.prepareStatement("SELECT Username, Role FROM USER_INFO");
            ResultSet rs = ps.executeQuery();
            PdfPTable table = new PdfPTable(3);

            int ctr = 1;
            table.addCell("ID");
            table.addCell("Username");
            table.addCell("Role");

            while (rs.next()) {
                PdfPCell userCell = new PdfPCell(new Phrase(rs.getString("USERNAME")));
                PdfPCell roleCell = new PdfPCell(new Phrase(rs.getString("ROLE")));

                table.addCell(String.valueOf(ctr));
                table.addCell(userCell);
                table.addCell(roleCell);

                ctr++;

            }
            Paragraph spacer = new Paragraph(new Chunk(new VerticalPositionMark()));
            spacer.setSpacingAfter(10);
            
            
          
            document.add(spacer);
            document.add(table);
            event.onEndPage(writer, document);
            document.close();

        } else if (userType.equalsIgnoreCase("Guest")) {
            // Add Guest Report
            PdfPTable table = new PdfPTable(2);
            table.addCell("Username");
            table.addCell("Password");

            ps = con.prepareStatement("SELECT USERNAME, PASSWORD FROM USER_INFO WHERE USERNAME = ?");
            ps.setString(1, ownerEmail); // Assuming ownerEmail is the username of the logged-in user
            ResultSet rs = ps.executeQuery();

            footer = new Phrase("Page " + writer.getPageNumber() + " of 1", FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10));
            ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT, footer, document.right(), document.bottom() - 18, 0);

            while (rs.next()) {
                PdfPCell userCell = new PdfPCell(new Phrase(rs.getString("USERNAME")));
                PdfPCell passwordCell = new PdfPCell(new Phrase(rs.getString("PASSWORD")));

                table.addCell(userCell);
                table.addCell(passwordCell);
            }

            document.add(new Paragraph("\n\n"));
            document.add(table);
            document.close();
        }
    }
    class MyFooter extends PdfPageEventHelper {
    Font ffont = new Font(Font.FontFamily.UNDEFINED, 5, Font.ITALIC);

    public void onEndPage(PdfWriter writer, Document document) {
        PdfContentByte cb = writer.getDirectContent();
        Phrase footer = new Phrase("Page " + writer.getPageNumber() + " of 2", FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10));
        
         ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT, footer, document.right(), document.bottom() - 18, 0);
    }
}
         

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet ReportGenerator</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ReportGenerator at " + request.getContextPath() + "</h1>");
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
        HttpSession session = request.getSession();
        String userType = (String) session.getAttribute("role");
        String ownerEmail = (String) session.getAttribute("email");
        try {
            generateReports(response, userType, ownerEmail);
        } catch (DocumentException | SQLException e) {
            e.printStackTrace();
        }
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
