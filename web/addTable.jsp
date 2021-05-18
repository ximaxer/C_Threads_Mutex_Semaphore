<%--
  Created by IntelliJ IDEA.
  User: ximax
  Date: 12/05/2021
  Time: 21:05
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="s" uri= "/struts-tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="ISO-8859-1" language="java" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <title>Associar mesa de voto</title>
</head>
<body>

<s:text name="Eleicoes no sistema:"/><br>
<jsp:useBean id="PrimesBean" scope="request" class="webServer.model.PrimesBean"/>
<c:forEach items="${PrimesBean.activeElectionsAndCorrespondingTables}" var="value" >
    <c:out value="${value}"/><br>
</c:forEach>
<s:text name="Mesas no sistema:" /><br>

<c:forEach items="${PrimesBean.allTables}" var="value" >
    <c:out value="${value}" /><br>
</c:forEach>
    <s:form action="addTableToElection" method="post">
        <s:textfield name="election"/>
        <s:text name="Qual a eleicao ao qual quer associar a mesa" /><br>
        <s:textfield name="departament"/>
        <s:text name="Qual o departamento ao qual quer associar a mesa" /><br>
        <s:submit value="adicionar mesa" align="center"/>
    </s:form>
</body>
</html>
