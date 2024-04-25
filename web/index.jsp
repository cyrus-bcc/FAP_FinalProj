<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Login Screen</title>
        <link rel="stylesheet" type="text/css" href="styles.css">
    </head>
    <body>
        <%
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            response.setHeader("Pragma", "no-cache");
            response.setDateHeader("Expires", 0);
        %>
        <div class="container">
            <h1>Login Screen</h1>
            <form action="LoginScreenServlet" method="post">
                <label>Email:</label>
                <input type="text" name="email" id="email"><br>
                <label>Password:</label>
                <input type="password" name="password" id="password"><br>
                <label>CAPTCHA:</label>
                <img src="CaptchaServlet" alt="CAPTCHA Image"><br>
                <input type="text" name="captcha_input"><br>
                <input type="submit" value="Login">
            </form>
        </div>
    </body>
</html>
