<%-- 
    Document   : register
    Created on : Apr 27, 2016, 10:07:29 PM
    Author     : alvin
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Register Worker</title>
    </head>
    <body>
        Register<br><br>
        
        <form action="registerHandler" method="post">
            Username : <input type="text" name="username" required/><br>
            Password : <input type="password" name="password" required/><br>
            Name : <input type="text" name="name" required/><br>
            Address : <input type="text" name="address" required/><br>
            Tag : <input type="text" name="tag" required/><br>
            <input type="submit"/>
        </form>
    </body>
</html>
