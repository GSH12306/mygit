package com.han.po;
import java.io.Serializable;
/**
 * 
 * student 实体类
 * Thu Jun 14 09:08:15 CST 2018
 * @author 韩少斌 
 * 
 */ 
public class Student implements Serializable{
	private Integer id;
	private String name;
	private String phone;
	private String address;
	private Integer statusid;

	public Student(){
		super();
	}

	public Student(Integer id,String name,String phone,String address,Integer statusid){
		super();
		this.id=id;
		this.name=name;
		this.phone=phone;
		this.address=address;
		this.statusid=statusid;
	}
	public Student(String name,String phone,String address,Integer statusid){
		super();
		this.name=name;
		this.phone=phone;
		this.address=address;
		this.statusid=statusid;
	}

	@Override
	public String toString(){
		return "Student [id=" + id + ", name=" + name + ", phone=" + phone + ", address=" + address + ", statusid=" + statusid + "]";
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

	public void setPhone(String phone){
		this.phone=phone;
	}

	public String getPhone(){
		return phone;
	}

	public void setAddress(String address){
		this.address=address;
	}

	public String getAddress(){
		return address;
	}

	public void setStatusid(Integer statusid){
		this.statusid=statusid;
	}

	public Integer getStatusid(){
		return statusid;
	}
}

