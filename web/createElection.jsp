<%--
  Created by IntelliJ IDEA.
  User: ximax
  Date: 12/05/2021
  Time: 21:06
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="s" uri= "/struts-tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <title>Criar Eleicao</title>
</head>
<body>
<s:form action="createElection" method="post">
    <s:textfield name="titulo"/>
    <s:text name="Titulo" /><br>
    <s:textfield name="descricao"/>
    <s:text name="Breve descricao" /><br>
    <s:textfield name="instituicao"/>
    <s:text name="Instituicao" /><br>
    <s:textfield name="dataI"/>
    <s:text name="Data de Inicio (mm/dd/aaaa;hh:mm)" /><br>
    <s:textfield name="dataF"/>
    <s:text name="Data de Fim (mm/dd/aaaa;hh:mm)" /><br>
    <s:submit value="criar eleicao" align="center"/>
</s:form>
</body>
</html>
