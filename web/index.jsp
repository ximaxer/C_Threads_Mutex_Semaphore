<%--
  Created by IntelliJ IDEA.
  User: ximax
  Date: 08/05/2021
  Time: 22:52
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="s" uri= "/struts-tags" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <title>Login page</title>
  </head>
  <body>
    <s:form action="login" method="post">
        <s:textfield name="uname" value="admin"/>
        <s:text name="Username"/><br>
        <s:textfield name="pass" value="admin"/>
        <s:text name="Password"/><br>
      <s:submit value="login" align="center"/>
    </s:form>
  </body>
</html>