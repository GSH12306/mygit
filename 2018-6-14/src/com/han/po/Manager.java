package com.han.po;
import java.io.Serializable;
/**
 * 
 * manager 实体类
 * Thu Jun 14 09:08:15 CST 2018
 * @author 韩少斌 
 * 
 */ 
public class Manager implements Serializable{
	private Integer id;
	private String name;
	private String password;
	private Integer isUse;

	public Manager(){
		super();
	}

	public Manager(Integer id,String name,String password,Integer isUse){
		super();
		this.id=id;
		this.name=name;
		this.password=password;
		this.isUse=isUse;
	}

	@Override
	public String toString(){
		return "Manager [id=" + id + ", name=" + name + ", password=" + password + ", isUse=" + isUse + "]";
	}

	public void setId(Integer id){
		this.id=id;
	}

	public Integer getId(){
		return id;
	}

	public void setName(String name){
		this.name=name;
	}

	public String getName(){
		return name;
	}

	public void setPassword(String password){
		this.password=password;
	}

	public String getPassword(){
		return password;
	}

	public void setIsUse(Integer isUse){
		this.isUse=isUse;
	}

	public Integer getIsUse(){
		return isUse;
	}
}

