<%--
  Created by IntelliJ IDEA.
  User: ximax
  Date: 15/05/2021
  Time: 21:06
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="s" uri= "/struts-tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="ISO-8859-1" language="java" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <title>Escolhe Eleicao</title>
</head>
<body>

<jsp:useBean id="PrimesBean" scope="request" class="webServer.model.PrimesBean"/>
<c:forEach items="${PrimesBean.electionLists}" var="value" >
    <c:out value="${value}"/><br>
</c:forEach>
<s:form action="vote" method="post">
    <s:textfield name="lista"/>
    <s:text name="Lista desejada" /><br>
    <s:submit value="votar" align="center"/>
</s:form>
</body>
</html>
