<%-- 
    Document   : noPasswordError
    Created on : 02 23, 24, 1:05:41 AM
    Author     : Choco
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>No Password Error</title>
        <link rel="stylesheet" type="text/css" href="styles.css">
    </head>
    <header>
        <% out.println(getServletContext().getInitParameter("header")); %>
    </header>

    <body>
        <div class="container">
            <h1>No Password Error</h1>
            <form action="index.jsp">
                <input type="submit" value="Return to Login">
            </form>
        </div>
    </body>
    <footer>   
        <% out.println(getServletContext().getInitParameter("footer"));%> 

    </footer>
</html>
