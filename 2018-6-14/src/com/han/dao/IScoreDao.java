package com.han.dao;
import java.util.List;

import com.han.po.Score;

/**
 * 
 * IScoreDao½Ó¿Ú
 * Thu Jun 14 09:08:15 CST 2018
 * @author º«ÉÙ±ó 
 * 
 */ 
public interface IScoreDao {

	public int insertScore(Score score);

	public int updateScore(Score score);

	public int deleteScore(Integer scoid);

	public List<Score> selectScores(int page,int size);

	public List<Score> selectScores(int page,int size,String sort);

	public List<Score> selectScores(int page,int size,String sort,String order);

	public int selectCounts();

	public int selectTotalPage(int size);

	public List<Score> selectScores(String colName,String keyWords,int page,int size,String sort,String order);

	public int selectCounts(String colName,String keyWords);

	public int selectTotalPage(String colName,String keyWords,int size);

	public Score selectScore(Integer scoid);


}
