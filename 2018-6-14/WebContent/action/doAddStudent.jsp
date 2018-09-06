<%@page import="com.han.po.Student"%>
<%@page import="com.han.dao.impl.StudentDao"%>
<%@page import="com.han.dao.IStudentDao"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
	<%
		//设置字符集
		request.setCharacterEncoding("UTF-8");
	
		String name = request.getParameter("name");
		String phone = request.getParameter("phone");
		String address = request.getParameter("address");
		String stas = request.getParameter("stas");
		
		IStudentDao isd = new StudentDao();
		Student student = new Student(name,phone,address,Integer.valueOf(stas));
		int x = isd.insertStudent(student);
		if(x == 1){
			//重定向
			//response.sendRedirect("../addStudentSuccess.html");
			
			//请求转发
			request.getRequestDispatcher("../addStudentSuccess.jsp").forward(request, response);
			
		}else{
			response.sendRedirect("../addStudent.jsp");
		}
		
		%>