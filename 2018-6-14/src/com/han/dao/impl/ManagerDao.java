package com.han.dao.impl;
import java.util.List;

import com.han.dao.IManagerDao;
import com.han.util.BaseDao;
import com.han.po.Manager;

/**
 * 
 * ManagerDao实现类
 * Thu Jun 14 09:08:15 CST 2018
 * @author 韩少斌 
 * 
 */ 
public class ManagerDao extends BaseDao<Manager>  implements IManagerDao{

	@Override
	public int insertManager(Manager manager){

		String sql = "INSERT INTO `manager` VALUES (default,?,?,?)";
		return executeUpdate(sql, manager.getName(),manager.getPassword(),manager.getIsUse().toString());
	}

	@Override
	public int updateManager(Manager manager){

		String sql = "UPDATE `manager` SET `name`=?,`password`=?,`isUse`=? where `id`=?";
		return executeUpdate(sql, manager.getName(),manager.getPassword(),manager.getIsUse().toString(),manager.getId().toString());
	}


	@Override
	public int deleteManager(Integer id){

		String sql = "DELETE FROM `manager` WHERE `id` = ?";
		return executeUpdate(sql, Integer.toString(id));
	}


	@Override
	public List<Manager> selectManagers(int page, int size) {
		return selectManagers(page, size, null, null);
	}

	@Override
	public List<Manager> selectManagers(int page, int size, String sort) {
		return selectManagers(page, size, sort, null);
	}

	@Override
	public List<Manager> selectManagers(int page,int size,String sort,String order){

		String sql = "SELECT * FROM `manager` ";
		if(sort != null && order != null){
			sql += "order by `"+sort+"` "+order+" LIMIT ?,?";
		}else if(sort != null && order == null){
			sql += "order by `"+sort+"` LIMIT ?,?";
		}else if(sort == null && order == null){
			sql += "LIMIT ?,?";
		}
		return executeQuery(sql, Manager.class,(page-1)*size, size);
	}

	@Override
	public int selectCounts(){

		String sql = "SELECT COUNT(1) FROM `manager`";
		return countRecord(sql);
	}

	@Override
	public int selectTotalPage(int size){

		int count = selectCounts();
		return count % size == 0 ? count / size : count / size + 1;
	}

	@Override
	public List<Manager> selectManagers(String colName,String keyWords,int page,int size,String sort,String order){
		String sql = "SELECT * FROM `manager`  where `"+colName+"` like ?  order by `"+sort+"` "+ order +" LIMIT ?,? ";
		return executeQuery(sql, Manager.class,"%"+keyWords+"%",(page-1)*size, size);
	}

	@Override
	public int selectCounts(String colName,String keyWords){
		String sql = "SELECT COUNT(1) FROM `manager` where `"+colName+"` like ?";
		return countRecord(sql,"%"+keyWords+"%");
	}

	@Override
	public int selectTotalPage(String colName,String keyWords,int size){

		int count = selectCounts(colName,keyWords);
		return count % size == 0 ? count / size : count / size + 1;
	}

	@Override
	public Manager selectManager(Integer id){

		String sql = "SELECT * FROM `manager` WHERE `id` = ?";
		List<Manager> managers = executeQuery(sql, Manager.class, id+"");
		return managers == null || managers.isEmpty() ? null : managers.get(0);
	}

}
