package com.han.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;

//import org.apache.commons.beanutils.BeanUtils;
//import org.springframework.jdbc.core.RowMapper;

public class MyRowMapper /*implements RowMapper*/ {
//
//	private Class cls;
//
//	public MyRowMapper(Class cls) {
//		this.cls = cls;
//	}
//
//	@Override
//	public Object mapRow(ResultSet rs, int num) throws SQLException {
//		Object t = null;
//		try {
//			// 获取对象中的所有字段
//			Field[] fields = cls.getDeclaredFields();
//			// 实例化
//			t = cls.newInstance();
//			for (Field f : fields) {
//				// 获取字段名称
//				String fieldName = f.getName();
//				// 通过字段名称获取该字段的值（实体字段名称必须与数据库字段名称一致才可以）
//				Object o = rs.getObject(fieldName);
//				if (o == null) {
//					continue;
//				}
//				// 使用BeanUtils通过字段名将value设置到实体中
//				BeanUtils.setProperty(t, fieldName, o);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return t;
//	}

}