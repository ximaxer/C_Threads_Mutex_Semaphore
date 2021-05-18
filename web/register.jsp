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
    <title>Registar Pessoas</title>
</head>
<body>
    <s:form action="registar" method="post">
        <s:textfield name="username"/>
        <s:text name="Username" /><br>
        <s:textfield name="password"/>
        <s:text name="Password" /><br>
        <s:textfield name="instituicao"/>
        <s:text name="Instituicao" /><br>
        <s:textfield name="telefone"/>
        <s:text name="Telefone" /><br>
        <s:textfield name="morada"/>
        <s:text name="Morada" /><br>
        <s:textfield name="CC"/>
        <s:text name="Numero de CC" /><br>
        <s:textfield name="valCC"/>
        <s:text name="Validade de CC(mm/dd/aaaa;hh:mm)" /><br>
        <s:submit value="registar" align="center"/>
    </s:form>
</body>
</html>
