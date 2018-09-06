package com.han.dao.impl;
import java.util.List;

import com.han.dao.ICourseDao;
import com.han.util.BaseDao;
import com.han.po.Course;

/**
 * 
 * CourseDao实现类
 * Thu Jun 14 09:08:15 CST 2018
 * @author 韩少斌 
 * 
 */ 
public class CourseDao extends BaseDao<Course>  implements ICourseDao{

	@Override
	public int insertCourse(Course course){

		String sql = "INSERT INTO `course` VALUES (default,?,?)";
		return executeUpdate(sql, course.getName(),course.getReMark());
	}

	@Override
	public int updateCourse(Course course){

		String sql = "UPDATE `course` SET `name`=?,`reMark`=? where `couid`=?";
		return executeUpdate(sql, course.getName(),course.getReMark(),course.getCouid().toString());
	}


	@Override
	public int deleteCourse(Integer couid){

		String sql = "DELETE FROM `course` WHERE `couid` = ?";
		return executeUpdate(sql, Integer.toString(couid));
	}


	@Override
	public List<Course> selectCourses(int page, int size) {
		return selectCourses(page, size, null, null);
	}

	@Override
	public List<Course> selectCourses(int page, int size, String sort) {
		return selectCourses(page, size, sort, null);
	}

	@Override
	public List<Course> selectCourses(int page,int size,String sort,String order){

		String sql = "SELECT * FROM `course` ";
		if(sort != null && order != null){
			sql += "order by `"+sort+"` "+order+" LIMIT ?,?";
		}else if(sort != null && order == null){
			sql += "order by `"+sort+"` LIMIT ?,?";
		}else if(sort == null && order == null){
			sql += "LIMIT ?,?";
		}
		return executeQuery(sql, Course.class,(page-1)*size, size);
	}

	@Override
	public int selectCounts(){

		String sql = "SELECT COUNT(1) FROM `course`";
		return countRecord(sql);
	}

	@Override
	public int selectTotalPage(int size){

		int count = selectCounts();
		return count % size == 0 ? count / size : count / size + 1;
	}

	@Override
	public List<Course> selectCourses(String colName,String keyWords,int page,int size,String sort,String order){
		String sql = "SELECT * FROM `course`  where `"+colName+"` like ?  order by `"+sort+"` "+ order +" LIMIT ?,? ";
		return executeQuery(sql, Course.class,"%"+keyWords+"%",(page-1)*size, size);
	}

	@Override
	public int selectCounts(String colName,String keyWords){
		String sql = "SELECT COUNT(1) FROM `course` where `"+colName+"` like ?";
		return countRecord(sql,"%"+keyWords+"%");
	}

	@Override
	public int selectTotalPage(String colName,String keyWords,int size){

		int count = selectCounts(colName,keyWords);
		return count % size == 0 ? count / size : count / size + 1;
	}

	@Override
	public Course selectCourse(Integer couid){

		String sql = "SELECT * FROM `course` WHERE `couid` = ?";
		List<Course> courses = executeQuery(sql, Course.class, couid+"");
		return courses == null || courses.isEmpty() ? null : courses.get(0);
	}

}
