<%--
  Created by IntelliJ IDEA.
  User: ximax
  Date: 12/05/2021
  Time: 21:07
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="s" uri= "/struts-tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="ISO-8859-1" language="java" %>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
  <title>Editar Eleicao</title>
</head>
<body>

<jsp:useBean id="PrimesBean" scope="request" class="webServer.model.PrimesBean"/>

<s:form action="editElection" method="post">
  <c:forEach items="${PrimesBean.electionProperties}" var="val" varStatus="loop">
    <c:choose>
      <c:when test="${loop.index=='0'}">
      <s:textfield name="descricao"/>
      <s:text name="Descricao antiga: " />${val}<br>
    </c:when>
    <c:when  test="${loop.index=='1'}">
      <s:textfield name="instituicao"/>
      <s:text name="Instituicao antiga: " />${val}<br>
    </c:when>
    <c:when  test="${loop.index=='2'}">
      <s:textfield name="dataI"/>
      <s:text name="Data inicial antiga: " />${val}<br>
    </c:when>
    <c:when  test="${loop.index=='3'}">
      <s:textfield name="dataF"/>
      <s:text name="Data final antiga: " />${val}<br>
    </c:when>
    <c:otherwise></c:otherwise>
  </c:choose>
  </c:forEach>
  <s:submit value="Atualizar Eleicao." align="center"/>
</s:form>
</body>
</html>
