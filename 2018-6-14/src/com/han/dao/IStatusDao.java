package com.han.dao;
import java.util.List;

import com.han.po.Status;

/**
 * 
 * IStatusDao½Ó¿Ú
 * Thu Jun 14 09:08:15 CST 2018
 * @author º«ÉÙ±ó 
 * 
 */ 
public interface IStatusDao {

	public int insertStatus(Status status);

	public int updateStatus(Status status);

	public int deleteStatus(Integer id);

	public List<Status> selectStatuss(int page,int size);

	public List<Status> selectStatuss(int page,int size,String sort);

	public List<Status> selectStatuss(int page,int size,String sort,String order);

	public int selectCounts();

	public int selectTotalPage(int size);

	public List<Status> selectStatuss(String colName,String keyWords,int page,int size,String sort,String order);

	public int selectCounts(String colName,String keyWords);

	public int selectTotalPage(String colName,String keyWords,int size);

	public Status selectStatus(Integer id);


}
