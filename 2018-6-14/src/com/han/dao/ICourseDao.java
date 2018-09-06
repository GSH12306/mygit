package com.han.dao;
import java.util.List;

import com.han.po.Course;

/**
 * 
 * ICourseDao½Ó¿Ú
 * Thu Jun 14 09:08:15 CST 2018
 * @author º«ÉÙ±ó 
 * 
 */ 
public interface ICourseDao {

	public int insertCourse(Course course);

	public int updateCourse(Course course);

	public int deleteCourse(Integer couid);

	public List<Course> selectCourses(int page,int size);

	public List<Course> selectCourses(int page,int size,String sort);

	public List<Course> selectCourses(int page,int size,String sort,String order);

	public int selectCounts();

	public int selectTotalPage(int size);

	public List<Course> selectCourses(String colName,String keyWords,int page,int size,String sort,String order);

	public int selectCounts(String colName,String keyWords);

	public int selectTotalPage(String colName,String keyWords,int size);

	public Course selectCourse(Integer couid);


}
