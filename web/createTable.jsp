<%--
  Created by IntelliJ IDEA.
  User: ximax
  Date: 12/05/2021
  Time: 21:03
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="s" uri= "/struts-tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <title>Criar mesa de voto</title>
</head>
<body>
    <s:form action="createTable" method="post">
        <s:textfield name="departamento"/>
        <s:text name="Departamento em que a mesa se encontra" /><br>
        <s:textfield name="ip"/>
        <s:text name="IP (224.0.0.0 - 239.255.255.255.255)" /><br>
        <s:textfield name="port"/>
        <s:text name="porta" /><br>
        <s:submit value="Criar mesa" align="center"/>
    </s:form>
</body>
</html>
