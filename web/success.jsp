<%-- 
    Document   : success.jsp
    Created on : 02 23, 24, 12:16:29 AM
    Author     : Choco
--%>
<%@page import="javax.naming.AuthenticationException"%>
<%@page import="java.time.*"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page isErrorPage="true" import="java.lang.Exception"%>
<%@page import="javax.servlet.http.HttpSession"%>

<%
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0);
%>
<%
    // Retrieve user's email and role from the session
    String email = (String) session.getAttribute("email");
    String role = (String) session.getAttribute("role");

    if (session.getAttribute("email") == null) {
        response.sendRedirect(request.getContextPath() + "/errorSession.jsp?error=InvalidLoginAttempt");
        return;
    }
%>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Success Page</title>
        <link rel="stylesheet" type="text/css" href="styles.css">
    </head>

    <header>
        <% out.println(getServletContext().getInitParameter("header")); %>
    </header>
    <%
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        if (session.getAttribute("email") == null)
            response.sendRedirect("errorSession.jsp");
    %>
    <body>

        <div class="container">
            <h1>Welcome, <%= session.getAttribute("email")%>!</h1>
            <h2>Your Role: <%= session.getAttribute("role")%> </h2>
            <form action="ReportGenerator" method="get">
                <input type="submit" value="Generate Report">
            </form>
            <form action="LogoutServlet" method="post">
                <input type="submit" value="Logout" >
            </form>
        </div>
    </body>

    <footer>   
        <% out.println(getServletContext().getInitParameter("footer"));%> 
    </footer>
    <iframe src="SessionInvalidatorServlet" style="display: none;"></iframe>
</html>
