<%@page import="java.util.List"%>
<%@page import="com.han.po.Student"%>
<%@page import="com.han.dao.impl.StudentDao"%>
<%@page import="com.han.dao.IStudentDao"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<link rel="stylesheet" href="css/show.css">
</head>
<body>

<div>
	<ul class="title">
		<li>编号</li>
		<li>姓名</li>
		<li>电话</li>
		<li>操作</li>
	</ul>
	
	<%
	
	String p = request.getParameter("p");
	
	int x = 1;
	try{
		x = Integer.valueOf(p);
	}catch(NumberFormatException e){}
	if(x < 1){
		x = 1;
	}

	IStudentDao isd = new StudentDao();
	int size = 3;
	int count = isd.selectCounts();
	int pageSize = count % size == 0 ? count / size : count / size + 1;

	if(x > pageSize){
		x = pageSize;
	}
	
	
	List <Student> stus = isd.selectStudents(x, size);
	for (Student student : stus) {
	%>
	<ul>
		<li><%= student.getId() %></li>
		<li><%= student.getName() %></li>
		<li><%= student.getPhone() %></li>
		<li><a href="action/doDeleteStudent.jsp?deleteId=<%= student.getId() %>">删除</a></li>
	</ul>

	
	
	<%} %>
	
	<div class="pageDiv">
	<%if(x > 1){ %>
		<a href="students.jsp?p=1">首页</a>
		<a href="students.jsp?p=<%=x-1%>">上一页</a>
	<%} %>
	
	<%
	int start = x - 3;
	if(start < 1){
		start = 1;
	}
	int end = x + 3;
	if(end > pageSize){
		end = pageSize;
	}
	for(int i = start ; i <= end; i++) {
		if(i!=x){
	%>
		<a  href="students.jsp?p=<%=i%>"><%=i%></a>
	
	<%}else{
		%>
		<a class="currentPage" href="students.jsp?p=<%=i%>"><%=i%></a>
		<%
	  }
	} %>
	
	<%if(x < pageSize){ %>	
		<a href="students.jsp?p=<%=x+1%>">下一页</a>
		<a href="students.jsp?p=<%=pageSize%>">末页</a>
	<%} %>
	</div>
	
</div>	
</body>
</html>