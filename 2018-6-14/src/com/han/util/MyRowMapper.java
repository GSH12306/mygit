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
//			// ��ȡ�����е������ֶ�
//			Field[] fields = cls.getDeclaredFields();
//			// ʵ����
//			t = cls.newInstance();
//			for (Field f : fields) {
//				// ��ȡ�ֶ�����
//				String fieldName = f.getName();
//				// ͨ���ֶ����ƻ�ȡ���ֶε�ֵ��ʵ���ֶ����Ʊ��������ݿ��ֶ�����һ�²ſ��ԣ�
//				Object o = rs.getObject(fieldName);
//				if (o == null) {
//					continue;
//				}
//				// ʹ��BeanUtilsͨ���ֶ�����value���õ�ʵ����
//				BeanUtils.setProperty(t, fieldName, o);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return t;
//	}

}