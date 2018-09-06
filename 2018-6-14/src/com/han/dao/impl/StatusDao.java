package com.han.dao.impl;
import java.util.List;

import com.han.dao.IStatusDao;
import com.han.util.BaseDao;
import com.han.po.Status;

/**
 * 
 * StatusDao实现类
 * Thu Jun 14 09:08:15 CST 2018
 * @author 韩少斌 
 * 
 */ 
public class StatusDao extends BaseDao<Status>  implements IStatusDao{

	@Override
	public int insertStatus(Status status){

		String sql = "INSERT INTO `status` VALUES (default,?)";
		return executeUpdate(sql, status.getStatus());
	}

	@Override
	public int updateStatus(Status status){

		String sql = "UPDATE `status` SET `status`=? where `id`=?";
		return executeUpdate(sql, status.getStatus(),status.getId().toString());
	}


	@Override
	public int deleteStatus(Integer id){

		String sql = "DELETE FROM `status` WHERE `id` = ?";
		return executeUpdate(sql, Integer.toString(id));
	}


	@Override
	public List<Status> selectStatuss(int page, int size) {
		return selectStatuss(page, size, null, null);
	}

	@Override
	public List<Status> selectStatuss(int page, int size, String sort) {
		return selectStatuss(page, size, sort, null);
	}

	@Override
	public List<Status> selectStatuss(int page,int size,String sort,String order){

		String sql = "SELECT * FROM `status` ";
		if(sort != null && order != null){
			sql += "order by `"+sort+"` "+order+" LIMIT ?,?";
		}else if(sort != null && order == null){
			sql += "order by `"+sort+"` LIMIT ?,?";
		}else if(sort == null && order == null){
			sql += "LIMIT ?,?";
		}
		return executeQuery(sql, Status.class,(page-1)*size, size);
	}

	@Override
	public int selectCounts(){

		String sql = "SELECT COUNT(1) FROM `status`";
		return countRecord(sql);
	}

	@Override
	public int selectTotalPage(int size){

		int count = selectCounts();
		return count % size == 0 ? count / size : count / size + 1;
	}

	@Override
	public List<Status> selectStatuss(String colName,String keyWords,int page,int size,String sort,String order){
		String sql = "SELECT * FROM `status`  where `"+colName+"` like ?  order by `"+sort+"` "+ order +" LIMIT ?,? ";
		return executeQuery(sql, Status.class,"%"+keyWords+"%",(page-1)*size, size);
	}

	@Override
	public int selectCounts(String colName,String keyWords){
		String sql = "SELECT COUNT(1) FROM `status` where `"+colName+"` like ?";
		return countRecord(sql,"%"+keyWords+"%");
	}

	@Override
	public int selectTotalPage(String colName,String keyWords,int size){

		int count = selectCounts(colName,keyWords);
		return count % size == 0 ? count / size : count / size + 1;
	}

	@Override
	public Status selectStatus(Integer id){

		String sql = "SELECT * FROM `status` WHERE `id` = ?";
		List<Status> statuss = executeQuery(sql, Status.class, id+"");
		return statuss == null || statuss.isEmpty() ? null : statuss.get(0);
	}

}
