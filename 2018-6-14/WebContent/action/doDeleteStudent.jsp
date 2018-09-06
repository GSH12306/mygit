<%@page import="com.han.dao.impl.StudentDao"%>
<%@page import="com.han.dao.IStudentDao"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

    <%
    	//ssss.jsp?key=zhi&key2=zhi2
    	String deleteId = request.getParameter("deleteId");
    
    	IStudentDao ids = new StudentDao();
    	int deleteCount = ids.deleteStudent(Integer.valueOf(deleteId));
    	
    	
    %>
    <%= deleteCount %>