package com.han.dao;
import java.util.List;

import com.han.po.Student;

/**
 * 
 * IStudentDao½Ó¿Ú
 * Thu Jun 14 09:08:15 CST 2018
 * @author º«ÉÙ±ó 
 * 
 */ 
public interface IStudentDao {

	public int insertStudent(Student student);

	public int updateStudent(Student student);

	public int deleteStudent(Integer id);

	public List<Student> selectStudents(int page,int size);

	public List<Student> selectStudents(int page,int size,String sort);

	public List<Student> selectStudents(int page,int size,String sort,String order);

	public int selectCounts();

	public int selectTotalPage(int size);

	public List<Student> selectStudents(String colName,String keyWords,int page,int size,String sort,String order);

	public int selectCounts(String colName,String keyWords);

	public int selectTotalPage(String colName,String keyWords,int size);

	public Student selectStudent(Integer id);


}
