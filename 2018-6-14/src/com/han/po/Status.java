package com.han.po;
import java.io.Serializable;
/**
 * 
 * status 实体类
 * Thu Jun 14 09:08:15 CST 2018
 * @author 韩少斌 
 * 
 */ 
public class Status implements Serializable{
	private Integer id;
	private String status;

	public Status(){
		super();
	}

	public Status(Integer id,String status){
		super();
		this.id=id;
		this.status=status;
	}

	@Override
	public String toString(){
		return "Status [id=" + id + ", status=" + status + "]";
	}

	public void setId(Integer id){
		this.id=id;
	}

	public Integer getId(){
		return id;
	}

	public void setStatus(String status){
		this.status=status;
	}

	public String getStatus(){
		return status;
	}
}

