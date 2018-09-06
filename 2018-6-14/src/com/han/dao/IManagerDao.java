package com.han.dao;
import java.util.List;

import com.han.po.Manager;

/**
 * 
 * IManagerDao½Ó¿Ú
 * Thu Jun 14 09:08:15 CST 2018
 * @author º«ÉÙ±ó 
 * 
 */ 
public interface IManagerDao {

	public int insertManager(Manager manager);

	public int updateManager(Manager manager);

	public int deleteManager(Integer id);

	public List<Manager> selectManagers(int page,int size);

	public List<Manager> selectManagers(int page,int size,String sort);

	public List<Manager> selectManagers(int page,int size,String sort,String order);

	public int selectCounts();

	public int selectTotalPage(int size);

	public List<Manager> selectManagers(String colName,String keyWords,int page,int size,String sort,String order);

	public int selectCounts(String colName,String keyWords);

	public int selectTotalPage(String colName,String keyWords,int size);

	public Manager selectManager(Integer id);


}
