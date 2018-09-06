package com.han.po;
import java.io.Serializable;
/**
 * 
 * score 实体类
 * Thu Jun 14 09:08:15 CST 2018
 * @author 韩少斌 
 * 
 */ 
public class Score implements Serializable{
	private Integer scoid;
	private Integer stuid;
	private Integer couid;
	private Double score;

	public Score(){
		super();
	}

	public Score(Integer scoid,Integer stuid,Integer couid,Double score){
		super();
		this.scoid=scoid;
		this.stuid=stuid;
		this.couid=couid;
		this.score=score;
	}

	@Override
	public String toString(){
		return "Score [scoid=" + scoid + ", stuid=" + stuid + ", couid=" + couid + ", score=" + score + "]";
	}

	public void setScoid(Integer scoid){
		this.scoid=scoid;
	}

	public Integer getScoid(){
		return scoid;
	}

	public void setStuid(Integer stuid){
		this.stuid=stuid;
	}

	public Integer getStuid(){
		return stuid;
	}

	public void setCouid(Integer couid){
		this.couid=couid;
	}

	public Integer getCouid(){
		return couid;
	}

	public void setScore(Double score){
		this.score=score;
	}

	public Double getScore(){
		return score;
	}
}

