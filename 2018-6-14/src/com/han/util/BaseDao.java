package com.han.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseDao<T> {



	
	public List<List<Map<String, Object>>> execute(String sql, Object... args) {
		List<List<Map<String, Object>>> list = new ArrayList<>();
		Connection conn = getconnection();
		CallableStatement cs = null;
		ResultSet rs = null;
		try {
			cs = conn.prepareCall(sql);
			if (args != null) {
				for (int i = 0; i < args.length; i++) {
					cs.setObject(i + 1, args[i]);
				}
			}
//			执行存储过程
			cs.execute();
			do{
//				存放一个结果集的List
				List<Map<String, Object>> records = new ArrayList<>();
//				获取结果集
				rs = cs.getResultSet();
//				获取结果集的结构对象
				ResultSetMetaData data = rs.getMetaData();
//				获取结果集列的数量
				int size = data.getColumnCount();
				while(rs.next()){
//					存放一条记录的Map
					Map<String, Object> map = new HashMap<>();
//					遍历结果集中所有的列
					for (int i = 0; i < size; i++) {
//						获取结果集中列名
						String name = data.getColumnName(i+1);
//						向map中存放一列的值,key是列名,value是对应的值
						map.put(name, rs.getObject(name));
					}
//					加入到存放一个结果集数据的list中
					records.add(map);
				}
//				将一个结果集的数据存放到总的list中
				list.add(records);
			}while(cs.getMoreResults());
			
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			close(conn, cs, rs);
		}

		return list;
	}

	// "{call proc(?,?,?,?)}"
	public List<T> execute(String sql, Class cls, Object... args) {
		return executeQuery(sql, cls, args);
	}

	// public void execute(String sql ,Object... args){
	// Connection conn = getconnection();
	// CallableStatement cs = null;
	// try {
	// cs = conn.prepareCall(sql);
	// if(args != null){
	// for (int i = 0; i < args.length; i++) {
	// cs.setObject(i+1, args[i]);
	// }
	// }
	// cs.executeQuery();
	// } catch (SQLException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }finally {
	// close(conn, cs);
	// }
	//
	// }

	public Connection getconnection() {

		Connection conn = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();

		}

		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/student", "root", "admin123456");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return conn;
	}

	public void close(Connection conn) {
		try {
			if (conn != null) {
				conn.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void close(PreparedStatement ps) {
		try {
			if (ps != null) {
				ps.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void close(ResultSet rs) {
		try {
			if (rs != null) {
				rs.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void close(Connection conn, PreparedStatement ps) {
		close(ps);
		close(conn);
	}

	public void close(Connection conn, PreparedStatement ps, ResultSet rs) {
		close(rs);
		close(conn, ps);
	}

	public int executeUpdate(String sql) {
		return executeUpdate(sql, null);
	}

	public int executeUpdate(String sql, String... args) {
		int c = 0;
		Connection conn = null;
		PreparedStatement ps = null;

		try {
			conn = getconnection();
			ps = conn.prepareStatement(sql);
			if (args != null) {
				for (int i = 0; i < args.length; i++) {
					ps.setString(i + 1, args[i]);
				}
			}
			c = ps.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			close(conn, ps);
		}
		return c;
	}

	public List<Map<String, Object>> executeQueryMap(String sql) throws Exception {
		return executeQueryMap(sql, null);
	}

	public List<Map<String, Object>> executeQueryMap(String sql, Object... args) throws Exception {
		List<Map<String, Object>> obs = new ArrayList<>();
		// 创建连接
		Connection conn = getconnection();
		// 语句执行对象
		PreparedStatement ps = null;
		// 结果集
		ResultSet rs = null;

		try {
			ps = conn.prepareStatement(sql);
			if (args != null) {
				for (int i = 0; i < args.length; i++) {
					ps.setObject(i + 1, args[i]);
				}
			}

			ResultSetMetaData metaData = ps.getMetaData();
			int size = metaData.getColumnCount();
			System.out.println(size);
			String[] columNames = new String[size];
			for (int i = 0; i < size; i++) {
				String colName = metaData.getColumnName(i + 1);
				for (int j = 0; j < i; j++) {
					if (columNames[j].equalsIgnoreCase(colName)) {
						throw new Exception("结果集中有相同名称的列");
					}
				}

				columNames[i] = colName;
			}

			rs = ps.executeQuery();
			while (rs.next()) {
				Map<String, Object> map = new HashMap<>();
				for (String columName : columNames) {
					map.put(columName, rs.getObject(columName));
				}
				obs.add(map);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			close(conn, ps, rs);
		}

		return obs;
	}

	public List<T> executeQuery(String sql, Class cls) {
		return executeQuery(sql, cls, null);
	}

	public List<T> executeQuery(String sql, Class cls, Object... args) {
		List<T> objects = null;
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = getconnection();
			ps = conn.prepareStatement(sql);
			if (args != null) {
				for (int i = 0; i < args.length; i++) {
					ps.setObject(i + 1, args[i]);
				}
			}
			rs = ps.executeQuery();
			while (rs.next()) {
				if (objects == null) {
					objects = new ArrayList<>();
				}
				T obj = (T) cls.newInstance();
				Field[] fields = cls.getDeclaredFields();
				for (Field field : fields) {
					String methodName = "set" + UpperFirstLetter(field.getName());
					Class argClass = field.getType();
					Method method = cls.getMethod(methodName, argClass);
					method.invoke(obj, rs.getObject(field.getName()));
				}
				objects.add(obj);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			close(conn, ps, rs);
		}
		return objects;
	}

	public List<T> executeQuery(String sql, String classStr) {
		try {
			return executeQuery(sql, Class.forName(classStr));
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public List<T> executeQuery(String sql, String classStr, String... args) {
		try {
			return executeQuery(sql, Class.forName(classStr), args);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public int countRecord(String sql) {
		return countRecord(sql, null);
	}

	public int countRecord(String sql, Object... args) {
		int c = 0;
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		con = getconnection();
		try {
			ps = con.prepareStatement(sql);
			if (args != null) {
				for (int i = 0; i < args.length; i++) {
					ps.setObject(i + 1, args[i]);
				}
			}
			rs = ps.executeQuery();
			if (rs.next()) {
				c = rs.getInt(1);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			close(con, ps, rs);
		}
		return c;
	}

	private String UpperFirstLetter(String srcStr) {

		StringBuilder destStr = new StringBuilder();

		destStr.append(srcStr.substring(0, 1).toUpperCase());
		destStr.append(srcStr.substring(1));

		return destStr.toString();
	}
}
