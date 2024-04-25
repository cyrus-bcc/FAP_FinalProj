<%-- 
    Document   : userNotFoundError
    Created on : 02 23, 24, 1:06:27 AM
    Author     : Choco
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>User Not Found Error</title>
        <link rel="stylesheet" type="text/css" href="styles.css">
    </head>
    <header>
        <% out.println(getServletContext().getInitParameter("header")); %>
    </header>

    <body>
        <div class="container">
            <h1>User Not Found Error</h1>
            <form action="index.jsp">
                <input type="submit" value="Return to Login">
            </form>
        </div>
    </body>
    <footer>   
        <% out.println(getServletContext().getInitParameter("footer"));%> 
    </footer>
</html>
