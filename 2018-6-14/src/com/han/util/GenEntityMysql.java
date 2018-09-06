package com.han.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Date;

public class GenEntityMysql {

	public static void main(String[] args) {
		GenEntityMysql gem = new GenEntityMysql();
		gem.createAll("student", "root", "admin123456", "com.han");
	}

	private String authorName = "韩少斌";// 作者名字
	// 数据库连接
	private static final String URL = "jdbc:mysql://localhost:3306/";
	private static final String DRIVER = "com.mysql.jdbc.Driver";

	/**
	 * 根据表结构创建po,dao接口以及实现接口
	 * 
	 * @param dbName
	 *            数据库名称
	 * @param user
	 *            数据库用户名
	 * @param password
	 *            数据库密码
	 * @param basePackage
	 *            基础包的位置
	 */
	public void createAll(String dbName, String user, String password, String basePackage) {
		long s = System.currentTimeMillis();
		String daoImplPackage = basePackage + ".dao.impl";
		String beanPackage = basePackage + ".po";
		String daoPackage = basePackage + ".dao";
		String utilPackage = basePackage + ".util";
		createAllPO(dbName, user, password, beanPackage);
		createAllDao(dbName, user, password, daoPackage, beanPackage);
		createAllDaoImpl(dbName, user, password, daoImplPackage, beanPackage, daoPackage, utilPackage);
		long e = System.currentTimeMillis();
		System.out.println("生成完毕，共用时："+(e-s)+"ms");
	}

	/**
	 * 根据数据库中表的结构创建po类
	 * 
	 * @param dbName
	 *            数据库名称
	 * @param tableName
	 *            表名称
	 * @param user
	 *            数据库用户名
	 * @param password
	 *            数据库密码
	 * @param beanPackage
	 *            输出的包名，例如：com.han.po
	 */
	public void createPO(String dbName, String tableName, String user, String password, String beanPackage) {
		Connection con = null;
		try {
			Class.forName(DRIVER);
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}

		try {
			con = DriverManager.getConnection(URL + dbName, user, password);
			createPO(con, tableName, beanPackage);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 生成一个类
	 * 
	 * @param con
	 *            数据库连接
	 * @param tableName
	 *            表名
	 * @param beanPackage
	 *            输出包名
	 * 
	 */
	private void createPO(Connection con, String tableName, String beanPackage) {
		File directory = new File("");
		String outputPath = directory.getAbsolutePath() + "/src/" + checkPackage(beanPackage).replace(".", "/") + "/"
				+ firstLetterUpper(tableName) + ".java";
		File outputFile = new File(outputPath);

		// 判断包名是否存在
		if (!outputFile.getParentFile().exists()) {
			outputFile.getParentFile().mkdirs();
		}
		String[] colnames; // 列名数组
		String[] colTypes; // 列名类型数组
		int[] colSizes; // 列名大小数组
		boolean f_util = false; // 是否需要导入包java.util.*
		// 查要生成实体类的表
		String sql = "select * from `" + tableName + "`";
		PreparedStatement pStemt = null;
		PrintWriter pw = null;
		try {
			pStemt = con.prepareStatement(sql);

			ResultSetMetaData rsmd = pStemt.getMetaData();
			int size = rsmd.getColumnCount(); // 统计列
			colnames = new String[size];
			colTypes = new String[size];
			colSizes = new int[size];
			for (int i = 0; i < size; i++) {
				colnames[i] = rsmd.getColumnName(i + 1);
				colTypes[i] = rsmd.getColumnTypeName(i + 1);

				if (colTypes[i].equalsIgnoreCase("timestamp") || colTypes[i].equalsIgnoreCase("datetime")
						|| colTypes[i].equalsIgnoreCase("date")) {
					f_util = true;
				}
				colSizes[i] = rsmd.getColumnDisplaySize(i + 1);
			}

			String content = parse(tableName, colnames, colTypes, colSizes, beanPackage, f_util);

			pw = new PrintWriter(new FileWriter(outputPath));
			pw.println(content);
			pw.flush();
		} catch (SQLException | IOException e) {
			System.out.println("表名称错误");
		} finally {
			if (pw != null) {
				pw.close();
			}
		}

	}

	/**
	 * 生成类
	 * 
	 * @param tableName
	 *            表名称
	 * @param colnames
	 *            列名称
	 * @param colTypes
	 *            列类型
	 * @param colSizes
	 *            列大小
	 * @param beanPackage
	 *            输出的包路径
	 * @param f_util
	 *            是否需要util包
	 * @return 类字符串
	 */
	private String parse(String tableName, String[] colnames, String[] colTypes, int[] colSizes, String beanPackage,
			boolean f_util) {
		StringBuffer sb = new StringBuffer();

		sb.append("package " + checkPackage(beanPackage) + ";\r\n");

		sb.append("import java.io.Serializable;");
		sb.append("\r\n");
		// 判断是否导入工具包
		if (f_util) {
			sb.append("import java.util.Date;\r\n");
			sb.append("\r\n");
		}
		// 注释部分
		sb.append("/**\n");
		sb.append(" * \n * " + tableName + " 实体类\r\n");
		sb.append(" * " + new Date() + "\r\n");
		sb.append(" * " + "@author " + authorName + " \r\n");
		sb.append(" * \n */ \r\n");
		// 实体部分
		sb.append("public class " + firstLetterUpper(tableName) + " implements Serializable{\r\n");
		sb.append(processAllAttrs(colnames, colTypes));// 属性
		sb.append(processDefaultConstructor(tableName));
		sb.append(processArgsConstructor(tableName, colnames, colTypes));
		sb.append(processToString(tableName, colnames));
		sb.append(processAllMethod(colnames, colTypes));// get set方法
		sb.append("}\r\n");
		return sb.toString();
	}

	/**
	 * 检查包名，若前后有非小写字母，则过滤
	 * 
	 * @param packageOutPath
	 *            包名
	 * @return 过滤非小写字母后的包名
	 */
	private String checkPackage(String packageOutPath) {

		char[] ch = packageOutPath.trim().toCharArray();
		int start = 0;
		int count = ch.length;
		int end = ch.length;

		for (int i = 0; i < ch.length; i++) {
			if (ch[i] >= 'a' && ch[i] <= 'z') {
				start = i;
				break;
			}
		}
		for (int i = ch.length - 1; i > 0; i--) {
			if (ch[i] >= 'a' && ch[i] <= 'z') {
				end = i + 1;
				break;
			}
		}
		count = end - start;
		return new String(ch, start, count);
	}

	/**
	 * 生成所有的属性
	 * 
	 * @param colnames
	 *            列名称
	 * @param colTypes
	 *            列类型
	 * @return 属性字符串
	 */
	private String processAllAttrs(String[] colnames, String[] colTypes) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < colnames.length; i++) {
			sb.append("\tprivate " + sqlType2JavaType(colTypes[i]) + " " + firstLetterLower(colnames[i]) + ";\r\n");
		}
		return sb.toString();
	}

	/**
	 * 生成构造方法
	 * 
	 * @param tableName
	 * @return
	 */
	private String processDefaultConstructor(String tableName) {
		StringBuffer sb = new StringBuffer();
		sb.append("\n\tpublic " + firstLetterUpper(tableName) + "(){\n");
		sb.append("\t\tsuper();\n");
		sb.append("\t}\n");
		return sb.toString();
	}

	private String processArgsConstructor(String tableName, String[] colnames, String[] colTypes) {
		StringBuffer sb = new StringBuffer();
		StringBuffer sbAttrs = new StringBuffer();
		sb.append("\n\tpublic " + firstLetterUpper(tableName) + "(");
		for (int i = 0; i < colTypes.length; i++) {
			String lowName = firstLetterLower(colnames[i]);
			sb.append(sqlType2JavaType(colTypes[i]));
			sb.append(" " + lowName + ",");

			sbAttrs.append("\t\tthis." + lowName + "=" + lowName + ";\n");
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append("){\n");
		sb.append("\t\tsuper();\n");

		sb.append(sbAttrs);

		sb.append("\t}\n");
		return sb.toString();
	}

	private String processToString(String tableName, String[] colnames) {
		StringBuffer sb = new StringBuffer();
		sb.append("\n\t@Override\n");
		sb.append("\tpublic String toString(){\n");
		sb.append("\t\treturn \"" + firstLetterUpper(tableName) + " [");
		for (int i = 0; i < colnames.length; i++) {
			String lowName = firstLetterLower(colnames[i]);
			sb.append(lowName + "=\" + " + lowName + " + \", ");
		}
		sb.delete(sb.length() - 2, sb.length());
		sb.append("]\";\n");
		sb.append("\t}\n");
		return sb.toString();
	}

	/**
	 * 生成所有的getset方法
	 * 
	 * @param colnames
	 *            列名称
	 * @param colTypes
	 *            列类型
	 * @return getset字符串
	 */
	private String processAllMethod(String[] colnames, String[] colTypes) {
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < colnames.length; i++) {
			String upName = firstLetterUpper(colnames[i]);
			String lowName = firstLetterLower(colnames[i]);
			sb.append("\n\tpublic void set" + upName + "(" + sqlType2JavaType(colTypes[i]) + " " + lowName + "){\r\n");
			sb.append("\t\tthis." + lowName + "=" + lowName + ";\r\n");
			sb.append("\t}\r\n");
			sb.append("\n\tpublic " + sqlType2JavaType(colTypes[i]) + " get" + upName + "(){\r\n");
			sb.append("\t\treturn " + lowName + ";\r\n");
			sb.append("\t}\r\n");
		}
		return sb.toString();
	}

	/**
	 * 将输入字符串的首字母改成大写
	 * 
	 * @param str
	 *            首字母要变成大写的字符串
	 * @return 首字母变成大写的字符串
	 */
	private String firstLetterUpper(String str) {
		char[] ch = str.toCharArray();
		if (ch[0] >= 'a' && ch[0] <= 'z') {
			ch[0] = (char) (ch[0] - 32);
		}
		return new String(ch);
	}

	/**
	 * 将输入字符串的首字母改成小写
	 * 
	 * @param str
	 *            首字母要变成小写的字符串
	 * @return 首字母变成小写的字符串
	 */
	private String firstLetterLower(String str) {
		char[] ch = str.toCharArray();
		if (ch[0] >= 'A' && ch[0] <= 'Z') {
			ch[0] = (char) (ch[0] + 32);
		}
		return new String(ch);
	}

	/**
	 * 根据数据库的数据类型获取对应的java数据类型
	 * 
	 * @param sqlType
	 *            数据库数据类型
	 * @return 对应的java数据类型
	 */
	private String sqlType2JavaType(String sqlType) {

		if (sqlType.equalsIgnoreCase("bit")) {
			return "Boolean";
		} else if (sqlType.equalsIgnoreCase("tinyint")) {
			return "Byte";
		} else if (sqlType.equalsIgnoreCase("smallint")) {
			return "Short";
		} else if (sqlType.equalsIgnoreCase("int")) {
			return "Integer";
		} else if (sqlType.equalsIgnoreCase("bigint")) {
			return "Long";
		} else if (sqlType.equalsIgnoreCase("float")) {
			return "Float";
		} else if (sqlType.equalsIgnoreCase("decimal") || sqlType.equalsIgnoreCase("numeric")
				|| sqlType.equalsIgnoreCase("real") || sqlType.equalsIgnoreCase("money")
				|| sqlType.equalsIgnoreCase("smallmoney") || sqlType.equalsIgnoreCase("double")) {
			return "Double";
		} else if (sqlType.equalsIgnoreCase("varchar") || sqlType.equalsIgnoreCase("char")
				|| sqlType.equalsIgnoreCase("nvarchar") || sqlType.equalsIgnoreCase("nchar")
				|| sqlType.equalsIgnoreCase("text") || sqlType.equalsIgnoreCase("varbinary")) {
			return "String";
		} else if (sqlType.equalsIgnoreCase("datetime") || sqlType.equalsIgnoreCase("date")
				|| sqlType.equalsIgnoreCase("timestamp")) {
			return "Date";
		} else if (sqlType.equalsIgnoreCase("image")) {
			return "Blod";
		}
		return null;
	}

	/**
	 * 创建dao接口
	 * 
	 * @param dbName
	 *            数据库名称
	 * @param user
	 *            数据库用户名
	 * @param password
	 *            数据库密码
	 * @param daoPackage
	 *            dao包的位置
	 * @param beanPackage
	 *            po包的位置
	 */
	public void createAllDao(String dbName, String user, String password, String daoPackage, String beanPackage) {
		Connection con = null;
		try {
			Class.forName(DRIVER);
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}

		try {
			con = DriverManager.getConnection(URL + dbName, user, password);
			DatabaseMetaData meta = con.getMetaData();

			ResultSet rs = meta.getTables(null, null, null, new String[] { "TABLE" });
			while (rs.next()) {
				String tableName = rs.getString(3);
				ResultSet prs = meta.getPrimaryKeys(null, null, tableName);
				prs.next();
				// 主键名称
				String keyName = prs.getObject(4).toString();
				createDao(con, tableName, keyName, daoPackage, beanPackage);

			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("数据库配置错误");
		} finally {
			try {
				if (con != null) {
					con.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 * @param dbName
	 *            数据库名称
	 * @param user
	 *            数据库用户名
	 * @param password
	 *            数据库密码
	 * @param daoImplPackage
	 *            dao实现类的位置
	 * @param beanPackage
	 *            po包的位置
	 * @param daoPackage
	 *            dao包的位置
	 * @param utilPackage
	 *            工具包的位置
	 */
	public void createAllDaoImpl(String dbName, String user, String password, String daoImplPackage, String beanPackage,
			String daoPackage, String utilPackage) {
		Connection con = null;
		try {
			Class.forName(DRIVER);
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}

		try {
			con = DriverManager.getConnection(URL + dbName, user, password);
			DatabaseMetaData meta = con.getMetaData();

			ResultSet rs = meta.getTables(null, null, null, new String[] { "TABLE" });
			while (rs.next()) {
				String tableName = rs.getString(3);
				ResultSet prs = meta.getPrimaryKeys(null, null, tableName);
				prs.next();
				// 主键名称
				String keyName = prs.getObject(4).toString();

				createDaoImpl(con, tableName, keyName, daoImplPackage, beanPackage, daoPackage, utilPackage);

			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("数据库配置错误");
		} finally {
			try {
				if (con != null) {
					con.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void createDaoImpl(Connection con, String tableName, String keyName, String daoImplPackage,
			String beanPackage, String daoPackage, String utilPackage) {
		File directory = new File("");
		String outputPath = directory.getAbsolutePath() + "/src/" + checkPackage(daoImplPackage).replace(".", "/") + "/"
				+ firstLetterUpper(tableName) + "Dao" + ".java";
		File outputFile = new File(outputPath);

		// 判断包名是否存在
		if (!outputFile.getParentFile().exists()) {
			outputFile.getParentFile().mkdirs();
		}
		String[] colnames; // 列名数组
		String[] colTypes; // 列名类型数组
		int[] colSizes; // 列名大小数组
		// 查要生成实体类的表
		String sql = "select * from `" + tableName + "`";
		PreparedStatement pStemt = null;
		PrintWriter pw = null;
		try {
			pStemt = con.prepareStatement(sql);
			ResultSetMetaData rsmd = pStemt.getMetaData();
			int size = rsmd.getColumnCount(); // 统计列
			String keyType = "";
			boolean keyIsAuto = false;
			colnames = new String[size];
			colTypes = new String[size];
			colSizes = new int[size];
			for (int i = 0; i < size; i++) {
				colnames[i] = rsmd.getColumnName(i + 1);
				colTypes[i] = rsmd.getColumnTypeName(i + 1);

				colSizes[i] = rsmd.getColumnDisplaySize(i + 1);
				if (rsmd.getColumnName(i + 1).equals(keyName)) {
					keyType = sqlType2JavaType(rsmd.getColumnTypeName(i + 1));
					keyIsAuto = rsmd.isAutoIncrement(i + 1);
				}
			}
			String content = parseDaoImpl(tableName, daoImplPackage, beanPackage, daoPackage, utilPackage, keyName,
					keyType, keyIsAuto, colnames, colTypes);

			pw = new PrintWriter(new FileWriter(outputPath));
			pw.println(content);
			pw.flush();
		} catch (IOException | SQLException e) {
			System.out.println("表名称错误");
		} finally {
			if (pw != null) {
				pw.close();
			}
		}

	}

	public void createDao(Connection con, String tableName, String keyName, String daoPackage, String beanPackage) {
		File directory = new File("");
		String outputPath = directory.getAbsolutePath() + "/src/" + checkPackage(daoPackage).replace(".", "/") + "/"
				+ "I" + firstLetterUpper(tableName) + "Dao" + ".java";
		File outputFile = new File(outputPath);

		// 判断包名是否存在
		if (!outputFile.getParentFile().exists()) {
			outputFile.getParentFile().mkdirs();
		}

		// 查要生成实体类的表
		String sql = "select * from `" + tableName + "`";
		PreparedStatement pStemt = null;
		PrintWriter pw = null;
		try {
			pStemt = con.prepareStatement(sql);
			ResultSetMetaData rsmd = pStemt.getMetaData();
			int size = rsmd.getColumnCount(); // 统计列
			String keyType = "";
			for (int i = 0; i < size; i++) {
				if (rsmd.getColumnName(i + 1).equals(keyName)) {
					keyType = sqlType2JavaType(rsmd.getColumnTypeName(i + 1));
					break;
				}
			}
			String content = parseDao(tableName, daoPackage, beanPackage, keyName, keyType);

			pw = new PrintWriter(new FileWriter(outputPath));
			pw.println(content);
			pw.flush();
		} catch (IOException | SQLException e) {
			System.out.println("表名称错误");
		} finally {
			if (pw != null) {
				pw.close();
			}
		}

	}

	public String parseDaoImpl(String tableName, String daoImplPackage, String beanPackage, String daoPackage,
			String utilPackage, String keyName, String keyType, boolean keyIsAuto, String[] colNames,
			String[] colTypes) {
		StringBuffer sb = new StringBuffer();

		String UpperTableName = firstLetterUpper(tableName);

		sb.append("package " + checkPackage(daoImplPackage) + ";\r\n");

		sb.append("import java.util.List;\r\n");
		sb.append("\r\n");

		sb.append("import " + daoPackage + ".I" + UpperTableName + "Dao;\r\n");
		sb.append("import " + utilPackage + ".BaseDao;\r\n");
		sb.append("import " + beanPackage + "." + UpperTableName + ";\r\n");
		sb.append("\r\n");
		// 注释部分
		sb.append("/**\n");
		sb.append(" * \n * " + UpperTableName + "Dao实现类\r\n");
		sb.append(" * " + new Date() + "\r\n");
		sb.append(" * " + "@author " + authorName + " \r\n");
		sb.append(" * \n */ \r\n");
		// 实体部分
		sb.append("public class " + UpperTableName + "Dao extends BaseDao<" + UpperTableName + ">  implements I"
				+ UpperTableName + "Dao{\r\n");
		sb.append(parseDaoImplMethod(tableName, beanPackage, keyName, keyType, keyIsAuto, colNames, colTypes));
		sb.append("\r\n}");
		return sb.toString();
	}

	public String parseDao(String tableName, String daoPackage, String beanPackage, String keyName, String keyType) {
		StringBuffer sb = new StringBuffer();

		sb.append("package " + checkPackage(daoPackage) + ";\r\n");

		sb.append("import java.util.List;\r\n");
		sb.append("\r\n");
		sb.append("import " + beanPackage + "." + firstLetterUpper(tableName) + ";\r\n");
		sb.append("\r\n");
		// 注释部分
		sb.append("/**\n");
		sb.append(" * \n * I" + firstLetterUpper(tableName) + "Dao接口\r\n");
		sb.append(" * " + new Date() + "\r\n");
		sb.append(" * " + "@author " + authorName + " \r\n");
		sb.append(" * \n */ \r\n");
		// 实体部分
		sb.append("public interface I" + firstLetterUpper(tableName) + "Dao {\r\n");
		sb.append(parseDaoMethod(tableName, beanPackage, keyName, keyType));
		sb.append("\r\n}");
		return sb.toString();
	}

	public String parseDaoImplMethod(String tableName, String beanPackage, String keyName, String keyType,
			boolean keyIsAuto, String[] colNames, String[] colTypes) {
		String UpperTableName = firstLetterUpper(tableName);
		StringBuilder sb = new StringBuilder();
		createInsertMethod(tableName, colNames, colTypes, UpperTableName, keyIsAuto, sb);
		createUpdateMethod(tableName, colNames, colTypes, UpperTableName, sb);
		createDeleteMethod(tableName, keyName, keyType, UpperTableName, sb);
		createSelectMethodNoSortAndOrder(UpperTableName, sb);
		createSelectMethodNoOrder(UpperTableName, sb);
		createSelectMethod(tableName, UpperTableName, sb);
		createSelectCount(tableName, sb);
		createSelectTotalPage(sb);
		createSelectKeyWordsMethod(tableName, UpperTableName, sb);
		createSelectKeyWordsCount(tableName, sb);
		createSelectKeyWordsTotalPage(sb);
		createSelectOneObjectMethod(tableName, keyName, keyType, UpperTableName, sb);
		return sb.toString();
	}

	public void createSelectOneObjectMethod(String tableName, String keyName, String keyType, String UpperTableName,
			StringBuilder sb) {
		sb.append("\r\n");
		sb.append("\t@Override\r\n");
		sb.append("\tpublic " + UpperTableName + " select" + UpperTableName + "(" + keyType + " " + keyName
				+ "){\r\n\r\n");
		sb.append("\t\tString sql = \"SELECT * FROM `" + tableName + "` WHERE `" + keyName + "` = ?\";\r\n");

		sb.append("\t\tList<" + UpperTableName + "> " + tableName + "s = executeQuery(sql, " + UpperTableName
				+ ".class, " + keyName + "+\"\");\r\n");
		sb.append("\t\treturn " + tableName + "s == null || " + tableName + "s.isEmpty() ? null : " + tableName
				+ "s.get(0);\r\n");
		sb.append("\t}\r\n");
	}

	public void createSelectMethodNoOrder(String UpperTableName, StringBuilder sb) {
		sb.append("\r\n");
		sb.append("\t@Override\r\n");
		sb.append("\tpublic List<" + UpperTableName + "> select" + UpperTableName
				+ "s(int page, int size, String sort) {\r\n");
		sb.append("\t\treturn select" + UpperTableName + "s(page, size, sort, null);\r\n");
		sb.append("\t}\r\n");
	}

	public void createSelectMethodNoSortAndOrder(String UpperTableName, StringBuilder sb) {
		sb.append("\r\n");
		sb.append("\t@Override\r\n");
		sb.append("\tpublic List<" + UpperTableName + "> select" + UpperTableName + "s(int page, int size) {\r\n");
		sb.append("\t\treturn select" + UpperTableName + "s(page, size, null, null);\r\n");
		sb.append("\t}\r\n");
	}

	public void createSelectKeyWordsTotalPage(StringBuilder sb) {
		sb.append("\r\n");
		sb.append("\t@Override\r\n");
		sb.append("\tpublic int selectTotalPage(String colName,String keyWords,int size){\r\n\r\n");
		sb.append("\t\tint count = selectCounts(colName,keyWords);\r\n");
		sb.append("\t\treturn count % size == 0 ? count / size : count / size + 1;\r\n");
		sb.append("\t}\r\n");
	}

	public void createSelectKeyWordsCount(String tableName, StringBuilder sb) {
		sb.append("\r\n");
		sb.append("\t@Override\r\n");
		sb.append("\tpublic int selectCounts(String colName,String keyWords){\r\n");
		sb.append("\t\tString sql = \"SELECT COUNT(1) FROM `" + tableName + "` where `\"+colName+\"` like ?\";\r\n");
		sb.append("\t\treturn countRecord(sql,\"%\"+keyWords+\"%\");\r\n");
		sb.append("\t}\r\n");
	}

	public void createSelectKeyWordsMethod(String tableName, String UpperTableName, StringBuilder sb) {
		sb.append("\r\n");
		sb.append("\t@Override\r\n");
		sb.append("\tpublic List<" + UpperTableName + "> select" + UpperTableName
				+ "s(String colName,String keyWords,int page,int size,String sort,String order){\r\n");
		sb.append("\t\tString sql = \"SELECT * FROM `" + tableName
				+ "`  where `\"+colName+\"` like ?  order by `\"+sort+\"` \"+ order +\" LIMIT ?,? \";\r\n");
		sb.append("\t\treturn executeQuery(sql, " + UpperTableName
				+ ".class,\"%\"+keyWords+\"%\",(page-1)*size, size);\r\n");
		sb.append("\t}\r\n");
	}

	public void createSelectTotalPage(StringBuilder sb) {
		sb.append("\r\n");
		sb.append("\t@Override\r\n");
		sb.append("\tpublic int selectTotalPage(int size){\r\n\r\n");
		sb.append("\t\tint count = selectCounts();\r\n");
		sb.append("\t\treturn count % size == 0 ? count / size : count / size + 1;\r\n");
		sb.append("\t}\r\n");
	}

	public void createSelectCount(String tableName, StringBuilder sb) {
		sb.append("\r\n");
		sb.append("\t@Override\r\n");
		sb.append("\tpublic int selectCounts(){\r\n\r\n");
		sb.append("\t\tString sql = \"SELECT COUNT(1) FROM `" + tableName + "`\";\r\n");
		sb.append("\t\treturn countRecord(sql);\r\n");
		sb.append("\t}\r\n");
	}

	public void createSelectMethod(String tableName, String UpperTableName, StringBuilder sb) {
		sb.append("\r\n");
		sb.append("\t@Override\r\n");
		sb.append("\tpublic List<" + UpperTableName + "> select" + UpperTableName
				+ "s(int page,int size,String sort,String order){\r\n\r\n");

		sb.append("\t\tString sql = \"SELECT * FROM `" + tableName + "` \";\r\n");
		sb.append("\t\tif(sort != null && order != null){\r\n");
		sb.append("\t\t\tsql += \"order by `\"+sort+\"` \"+order+\" LIMIT ?,?\";\r\n");
		sb.append("\t\t}else if(sort != null && order == null){\r\n");
		sb.append("\t\t\tsql += \"order by `\"+sort+\"` LIMIT ?,?\";\r\n");
		sb.append("\t\t}else if(sort == null && order == null){\r\n");
		sb.append("\t\t\tsql += \"LIMIT ?,?\";\r\n");
		sb.append("\t\t}\r\n");

		sb.append("\t\treturn executeQuery(sql, " + UpperTableName + ".class,(page-1)*size, size);\r\n");
		sb.append("\t}\r\n");
	}

	public void createDeleteMethod(String tableName, String keyName, String keyType, String UpperTableName,
			StringBuilder sb) {
		sb.append("\r\n");
		sb.append("\t@Override\r\n");
		sb.append("\tpublic int delete" + UpperTableName + "(" + keyType + " " + keyName + "){\r\n\r\n");
		sb.append("\t\tString sql = \"DELETE FROM `" + tableName + "` WHERE `" + keyName + "` = ?\";\r\n");

		StringBuilder argsBuil = new StringBuilder("\t\treturn executeUpdate(sql, ");
		if (keyType.equals("String")) {
			argsBuil.append(keyName);
		} else {
			argsBuil.append(keyType + ".toString(" + keyName + ")");
		}
		sb.append(argsBuil + ");\r\n");
		sb.append("\t}\r\n\r\n");
	}

	public void createUpdateMethod(String tableName, String[] colNames, String[] colTypes, String UpperTableName,
			StringBuilder sb) {
		sb.append("\r\n");
		sb.append("\t@Override\r\n");
		sb.append("\tpublic int update" + UpperTableName + "(" + UpperTableName + " " + tableName + "){\r\n\r\n");
		StringBuilder sqlBuil = new StringBuilder("UPDATE `" + tableName + "` SET ");
		for (int i = 1; i < colNames.length; i++) {
			sqlBuil.append("`" + colNames[i] + "`=?,");
		}
		sqlBuil.deleteCharAt(sqlBuil.length() - 1);
		sqlBuil.append(" where `" + colNames[0] + "`=?");
		sb.append("\t\tString sql = \"" + sqlBuil.toString() + "\";\r\n");

		StringBuilder argsBuil = new StringBuilder();
		for (int i = 1; i < colNames.length; i++) {
			if (sqlType2JavaType(colTypes[i]).equals("String")) {
				argsBuil.append(tableName + ".get" + firstLetterUpper(colNames[i]) + "(),");
			} else {
				argsBuil.append(tableName + ".get" + firstLetterUpper(colNames[i]) + "().toString(),");
			}
		}
		argsBuil.append(tableName + ".get" + firstLetterUpper(colNames[0]) + "().toString()");
		sb.append("\t\treturn executeUpdate(sql, " + argsBuil + ");\r\n");
		sb.append("\t}\r\n\r\n");
	}

	public void createInsertMethod(String tableName, String[] colNames, String[] colTypes, String UpperTableName,
			boolean keyIsAuto, StringBuilder sb) {
		sb.append("\r\n");
		sb.append("\t@Override\r\n");
		sb.append("\tpublic int insert" + UpperTableName + "(" + UpperTableName + " " + tableName + "){\r\n\r\n");
		StringBuilder sqlBuil = null;
		int beginIndex = 0;
		if (keyIsAuto) {
			sqlBuil = new StringBuilder("INSERT INTO `" + tableName + "` VALUES (default");
			beginIndex = 1;
		} else {
			sqlBuil = new StringBuilder("INSERT INTO `" + tableName + "` VALUES (?");
		}

		for (int i = beginIndex; i < colTypes.length; i++) {
			sqlBuil.append(",?");
		}
		sqlBuil.append(")");

		sb.append("\t\tString sql = \"" + sqlBuil.toString() + "\";\r\n");
		StringBuilder argsBuil = new StringBuilder();
		for (int i = beginIndex; i < colNames.length; i++) {
			if (sqlType2JavaType(colTypes[i]).equals("String")) {
				argsBuil.append(tableName + ".get" + firstLetterUpper(colNames[i]) + "(),");
			} else {
				argsBuil.append(tableName + ".get" + firstLetterUpper(colNames[i]) + "().toString(),");
			}
		}
		argsBuil.deleteCharAt(argsBuil.length() - 1);
		sb.append("\t\treturn executeUpdate(sql, " + argsBuil + ");\r\n");
		sb.append("\t}\r\n");
	}

	public String parseDaoMethod(String tableName, String beanPackage, String keyName, String keyType) {
		String UpperTableName = firstLetterUpper(tableName);
		StringBuilder sb = new StringBuilder();

		sb.append("\r\n");
		sb.append("\tpublic int insert" + UpperTableName + "(" + UpperTableName + " " + tableName + ");\r\n\r\n");
		sb.append("\tpublic int update" + UpperTableName + "(" + UpperTableName + " " + tableName + ");\r\n\r\n");

		sb.append("\tpublic int delete" + UpperTableName + "(" + keyType + " " + keyName + ");\r\n\r\n");

		sb.append("\tpublic List<" + UpperTableName + "> select" + UpperTableName + "s(int page,int size);\r\n\r\n");
		sb.append("\tpublic List<" + UpperTableName + "> select" + UpperTableName
				+ "s(int page,int size,String sort);\r\n\r\n");

		sb.append("\tpublic List<" + UpperTableName + "> select" + UpperTableName
				+ "s(int page,int size,String sort,String order);\r\n\r\n");
		sb.append("\tpublic int selectCounts();\r\n\r\n");
		sb.append("\tpublic int selectTotalPage(int size);\r\n\r\n");

		sb.append("\tpublic List<" + UpperTableName + "> select" + UpperTableName
				+ "s(String colName,String keyWords,int page,int size,String sort,String order);\r\n\r\n");
		sb.append("\tpublic int selectCounts(String colName,String keyWords);\r\n\r\n");
		sb.append("\tpublic int selectTotalPage(String colName,String keyWords,int size);\r\n\r\n");

		sb.append("\tpublic " + UpperTableName + " select" + UpperTableName + "(" + keyType + " " + keyName
				+ ");\r\n\r\n");

		return sb.toString();
	}

	/**
	 * 根据数据库中表的结构创建po类
	 * 
	 * @param dbName
	 *            数据库名称
	 * @param user
	 *            数据库用户名
	 * @param password
	 *            数据库密码
	 * @param beanPackage
	 *            输出的包名，例如：com.han.po
	 * 
	 */
	public void createAllPO(String dbName, String user, String password, String beanPackage) {
		Connection con = null;
		try {
			Class.forName(DRIVER);
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}

		try {
			con = DriverManager.getConnection(URL + dbName, user, password);
			DatabaseMetaData meta = con.getMetaData();
			ResultSet rs = meta.getTables(null, null, null, new String[] { "TABLE" });
			while (rs.next()) {
				String tableName = rs.getString(3);
				createPO(con, tableName, beanPackage);

			}
		} catch (SQLException e) {
			System.out.println("数据库配置错误");
		} finally {
			try {
				if (con != null) {
					con.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

}