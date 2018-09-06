package com.han.dao.impl;
import java.util.List;

import com.han.dao.IStudentDao;
import com.han.util.BaseDao;
import com.han.po.Student;

/**
 * 
 * StudentDao实现类
 * Thu Jun 14 09:08:15 CST 2018
 * @author 韩少斌 
 * 
 */ 
public class StudentDao extends BaseDao<Student>  implements IStudentDao{

	@Override
	public int insertStudent(Student student){

		String sql = "INSERT INTO `student` VALUES (default,?,?,?,?)";
		return executeUpdate(sql, student.getName(),student.getPhone(),student.getAddress(),student.getStatusid().toString());
	}

	@Override
	public int updateStudent(Student student){

		String sql = "UPDATE `student` SET `name`=?,`phone`=?,`address`=?,`statusid`=? where `id`=?";
		return executeUpdate(sql, student.getName(),student.getPhone(),student.getAddress(),student.getStatusid().toString(),student.getId().toString());
	}


	@Override
	public int deleteStudent(Integer id){

		String sql = "DELETE FROM `student` WHERE `id` = ?";
		return executeUpdate(sql, Integer.toString(id));
	}


	@Override
	public List<Student> selectStudents(int page, int size) {
		return selectStudents(page, size, null, null);
	}

	@Override
	public List<Student> selectStudents(int page, int size, String sort) {
		return selectStudents(page, size, sort, null);
	}

	@Override
	public List<Student> selectStudents(int page,int size,String sort,String order){

		String sql = "SELECT * FROM `student` ";
		if(sort != null && order != null){
			sql += " order by `"+sort+"` "+order+" LIMIT ?,?";
		}else if(sort != null && order == null){
			sql += " order by `"+sort+"` LIMIT ?,?";
		}else if(sort == null && order == null){
			sql += " LIMIT ?,?";
		}
		return executeQuery(sql, Student.class,(page-1)*size, size);
	}

	@Override
	public int selectCounts(){

		String sql = "SELECT COUNT(1) FROM `student`";
		return countRecord(sql);
	}

	@Override
	public int selectTotalPage(int size){

		int count = selectCounts();
		return count % size == 0 ? count / size : count / size + 1;
	}

	@Override
	public List<Student> selectStudents(String colName,String keyWords,int page,int size,String sort,String order){
		String sql = "SELECT * FROM `student`  where `"+colName+"` like ?  order by `"+sort+"` "+ order +" LIMIT ?,? ";
		return executeQuery(sql, Student.class,"%"+keyWords+"%",(page-1)*size, size);
	}

	@Override
	public int selectCounts(String colName,String keyWords){
		String sql = "SELECT COUNT(1) FROM `student` where `"+colName+"` like ?";
		return countRecord(sql,"%"+keyWords+"%");
	}

	@Override
	public int selectTotalPage(String colName,String keyWords,int size){

		int count = selectCounts(colName,keyWords);
		return count % size == 0 ? count / size : count / size + 1;
	}

	@Override
	public Student selectStudent(Integer id){

		String sql = "SELECT * FROM `student` WHERE `id` = ?";
		List<Student> students = executeQuery(sql, Student.class, id+"");
		return students == null || students.isEmpty() ? null : students.get(0);
	}

}
