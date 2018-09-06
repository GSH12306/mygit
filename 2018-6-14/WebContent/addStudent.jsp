<%@page import="java.util.List"%>
<%@page import="com.han.po.Status"%>

<%@page import="com.han.dao.impl.StatusDao"%>
<%@page import="com.han.dao.IStatusDao"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>新增学员</title>
</head>
<body>

	<form action="action/doAddStudent.jsp" method="post">
		<input type="text" name="name" placeholder="姓名"><br/>
		<input type="text" name="phone" placeholder="手机"><br/>
		<input type="text" name="address" placeholder="地址"><br/>
		<%
			IStatusDao statusDao = new StatusDao();
			List<Status> stas = statusDao.selectStatuss(1, statusDao.selectCounts());
			for(Status sta : stas){
		%>
			<input type="radio" value="<%=sta.getId() %>" name="stas"><%=sta.getStatus() %>
		
		<%} %>
		<br/>
		<input type="submit" value="新增学员">
	</form>
</body>
</html>