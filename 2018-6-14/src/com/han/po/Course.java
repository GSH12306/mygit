package com.han.po;
import java.io.Serializable;
/**
 * 
 * course 实体类
 * Thu Jun 14 09:08:15 CST 2018
 * @author 韩少斌 
 * 
 */ 
public class Course implements Serializable{
	private Integer couid;
	private String name;
	private String reMark;

	public Course(){
		super();
	}

	public Course(Integer couid,String name,String reMark){
		super();
		this.couid=couid;
		this.name=name;
		this.reMark=reMark;
	}

	@Override
	public String toString(){
		return "Course [couid=" + couid + ", name=" + name + ", reMark=" + reMark + "]";
	}

	public void setCouid(Integer couid){
		this.couid=couid;
	}

	public Integer getCouid(){
		return couid;
	}

	public void setName(String name){
		this.name=name;
	}

	public String getName(){
		return name;
	}

	public void setReMark(String reMark){
		this.reMark=reMark;
	}

	public String getReMark(){
		return reMark;
	}
}

