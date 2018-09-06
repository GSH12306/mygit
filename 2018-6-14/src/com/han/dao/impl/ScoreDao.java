package com.han.dao.impl;
import java.util.List;

import com.han.dao.IScoreDao;
import com.han.util.BaseDao;
import com.han.po.Score;

/**
 * 
 * ScoreDao实现类
 * Thu Jun 14 09:08:15 CST 2018
 * @author 韩少斌 
 * 
 */ 
public class ScoreDao extends BaseDao<Score>  implements IScoreDao{

	@Override
	public int insertScore(Score score){

		String sql = "INSERT INTO `score` VALUES (default,?,?,?)";
		return executeUpdate(sql, score.getStuid().toString(),score.getCouid().toString(),score.getScore().toString());
	}

	@Override
	public int updateScore(Score score){

		String sql = "UPDATE `score` SET `stuid`=?,`couid`=?,`score`=? where `scoid`=?";
		return executeUpdate(sql, score.getStuid().toString(),score.getCouid().toString(),score.getScore().toString(),score.getScoid().toString());
	}


	@Override
	public int deleteScore(Integer scoid){

		String sql = "DELETE FROM `score` WHERE `scoid` = ?";
		return executeUpdate(sql, Integer.toString(scoid));
	}


	@Override
	public List<Score> selectScores(int page, int size) {
		return selectScores(page, size, null, null);
	}

	@Override
	public List<Score> selectScores(int page, int size, String sort) {
		return selectScores(page, size, sort, null);
	}

	@Override
	public List<Score> selectScores(int page,int size,String sort,String order){

		String sql = "SELECT * FROM `score` ";
		if(sort != null && order != null){
			sql += "order by `"+sort+"` "+order+" LIMIT ?,?";
		}else if(sort != null && order == null){
			sql += "order by `"+sort+"` LIMIT ?,?";
		}else if(sort == null && order == null){
			sql += "LIMIT ?,?";
		}
		return executeQuery(sql, Score.class,(page-1)*size, size);
	}

	@Override
	public int selectCounts(){

		String sql = "SELECT COUNT(1) FROM `score`";
		return countRecord(sql);
	}

	@Override
	public int selectTotalPage(int size){

		int count = selectCounts();
		return count % size == 0 ? count / size : count / size + 1;
	}

	@Override
	public List<Score> selectScores(String colName,String keyWords,int page,int size,String sort,String order){
		String sql = "SELECT * FROM `score`  where `"+colName+"` like ?  order by `"+sort+"` "+ order +" LIMIT ?,? ";
		return executeQuery(sql, Score.class,"%"+keyWords+"%",(page-1)*size, size);
	}

	@Override
	public int selectCounts(String colName,String keyWords){
		String sql = "SELECT COUNT(1) FROM `score` where `"+colName+"` like ?";
		return countRecord(sql,"%"+keyWords+"%");
	}

	@Override
	public int selectTotalPage(String colName,String keyWords,int size){

		int count = selectCounts(colName,keyWords);
		return count % size == 0 ? count / size : count / size + 1;
	}

	@Override
	public Score selectScore(Integer scoid){

		String sql = "SELECT * FROM `score` WHERE `scoid` = ?";
		List<Score> scores = executeQuery(sql, Score.class, scoid+"");
		return scores == null || scores.isEmpty() ? null : scores.get(0);
	}

}
