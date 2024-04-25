<%-- 
    Document   : errorSession
    Created on : 02 23, 24, 1:25:04 AM
    Author     : Choco
--%>
<%@page import="javax.naming.AuthenticationException"%>
<%@page import="java.time.*"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page isErrorPage="true" import="java.lang.Exception"%>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Session Error</title>
        <link rel="stylesheet" type="text/css" href="styles.css">
    </head>
    <header>
        <% out.println(getServletContext().getInitParameter("header")); %>
    </header>

    <body>
        <div class="container">
            <h1>You attempted to access a page without logging in.</h1>
        </div>
    </body>

    <footer>   
        <% out.println(getServletContext().getInitParameter("footer"));%> 

    </footer>
</html>
