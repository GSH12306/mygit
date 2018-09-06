package com.han.test;

import java.util.List;

import com.han.dao.IStudentDao;
import com.han.dao.impl.StudentDao;
import com.han.po.Student;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		IStudentDao isd = new StudentDao();
		
		List<Student> stus = isd.selectStudents(1, 5);
		for (Student student : stus) {
			System.out.println(student);
		}

		
		Student s = isd.selectStudent(1008);
		System.out.println(s);
		ok:
		for (int f = 0; f < args.length; f++) {
			for (int i = 0; i < args.length; i++) {
				break ok;
			}
		}
	}

}
