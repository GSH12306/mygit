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

	private String authorName = "���ٱ�";// ��������
	// ���ݿ�����
	private static final String URL = "jdbc:mysql://localhost:3306/";
	private static final String DRIVER = "com.mysql.jdbc.Driver";

	/**
	 * ���ݱ�ṹ����po,dao�ӿ��Լ�ʵ�ֽӿ�
	 * 
	 * @param dbName
	 *            ���ݿ�����
	 * @param user
	 *            ���ݿ��û���
	 * @param password
	 *            ���ݿ�����
	 * @param basePackage
	 *            ��������λ��
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
		System.out.println("������ϣ�����ʱ��"+(e-s)+"ms");
	}

	/**
	 * �������ݿ��б�Ľṹ����po��
	 * 
	 * @param dbName
	 *            ���ݿ�����
	 * @param tableName
	 *            ������
	 * @param user
	 *            ���ݿ��û���
	 * @param password
	 *            ���ݿ�����
	 * @param beanPackage
	 *            ����İ��������磺com.han.po
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
	 * ����һ����
	 * 
	 * @param con
	 *            ���ݿ�����
	 * @param tableName
	 *            ����
	 * @param beanPackage
	 *            �������
	 * 
	 */
	private void createPO(Connection con, String tableName, String beanPackage) {
		File directory = new File("");
		String outputPath = directory.getAbsolutePath() + "/src/" + checkPackage(beanPackage).replace(".", "/") + "/"
				+ firstLetterUpper(tableName) + ".java";
		File outputFile = new File(outputPath);

		// �жϰ����Ƿ����
		if (!outputFile.getParentFile().exists()) {
			outputFile.getParentFile().mkdirs();
		}
		String[] colnames; // ��������
		String[] colTypes; // ������������
		int[] colSizes; // ������С����
		boolean f_util = false; // �Ƿ���Ҫ�����java.util.*
		// ��Ҫ����ʵ����ı�
		String sql = "select * from `" + tableName + "`";
		PreparedStatement pStemt = null;
		PrintWriter pw = null;
		try {
			pStemt = con.prepareStatement(sql);

			ResultSetMetaData rsmd = pStemt.getMetaData();
			int size = rsmd.getColumnCount(); // ͳ����
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
			System.out.println("�����ƴ���");
		} finally {
			if (pw != null) {
				pw.close();
			}
		}

	}

	/**
	 * ������
	 * 
	 * @param tableName
	 *            ������
	 * @param colnames
	 *            ������
	 * @param colTypes
	 *            ������
	 * @param colSizes
	 *            �д�С
	 * @param beanPackage
	 *            ����İ�·��
	 * @param f_util
	 *            �Ƿ���Ҫutil��
	 * @return ���ַ���
	 */
	private String parse(String tableName, String[] colnames, String[] colTypes, int[] colSizes, String beanPackage,
			boolean f_util) {
		StringBuffer sb = new StringBuffer();

		sb.append("package " + checkPackage(beanPackage) + ";\r\n");

		sb.append("import java.io.Serializable;");
		sb.append("\r\n");
		// �ж��Ƿ��빤�߰�
		if (f_util) {
			sb.append("import java.util.Date;\r\n");
			sb.append("\r\n");
		}
		// ע�Ͳ���
		sb.append("/**\n");
		sb.append(" * \n * " + tableName + " ʵ����\r\n");
		sb.append(" * " + new Date() + "\r\n");
		sb.append(" * " + "@author " + authorName + " \r\n");
		sb.append(" * \n */ \r\n");
		// ʵ�岿��
		sb.append("public class " + firstLetterUpper(tableName) + " implements Serializable{\r\n");
		sb.append(processAllAttrs(colnames, colTypes));// ����
		sb.append(processDefaultConstructor(tableName));
		sb.append(processArgsConstructor(tableName, colnames, colTypes));
		sb.append(processToString(tableName, colnames));
		sb.append(processAllMethod(colnames, colTypes));// get set����
		sb.append("}\r\n");
		return sb.toString();
	}

	/**
	 * ����������ǰ���з�Сд��ĸ�������
	 * 
	 * @param packageOutPath
	 *            ����
	 * @return ���˷�Сд��ĸ��İ���
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
	 * �������е�����
	 * 
	 * @param colnames
	 *            ������
	 * @param colTypes
	 *            ������
	 * @return �����ַ���
	 */
	private String processAllAttrs(String[] colnames, String[] colTypes) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < colnames.length; i++) {
			sb.append("\tprivate " + sqlType2JavaType(colTypes[i]) + " " + firstLetterLower(colnames[i]) + ";\r\n");
		}
		return sb.toString();
	}

	/**
	 * ���ɹ��췽��
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
	 * �������е�getset����
	 * 
	 * @param colnames
	 *            ������
	 * @param colTypes
	 *            ������
	 * @return getset�ַ���
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
	 * �������ַ���������ĸ�ĳɴ�д
	 * 
	 * @param str
	 *            ����ĸҪ��ɴ�д���ַ���
	 * @return ����ĸ��ɴ�д���ַ���
	 */
	private String firstLetterUpper(String str) {
		char[] ch = str.toCharArray();
		if (ch[0] >= 'a' && ch[0] <= 'z') {
			ch[0] = (char) (ch[0] - 32);
		}
		return new String(ch);
	}

	/**
	 * �������ַ���������ĸ�ĳ�Сд
	 * 
	 * @param str
	 *            ����ĸҪ���Сд���ַ���
	 * @return ����ĸ���Сд���ַ���
	 */
	private String firstLetterLower(String str) {
		char[] ch = str.toCharArray();
		if (ch[0] >= 'A' && ch[0] <= 'Z') {
			ch[0] = (char) (ch[0] + 32);
		}
		return new String(ch);
	}

	/**
	 * �������ݿ���������ͻ�ȡ��Ӧ��java��������
	 * 
	 * @param sqlType
	 *            ���ݿ���������
	 * @return ��Ӧ��java��������
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
	 * ����dao�ӿ�
	 * 
	 * @param dbName
	 *            ���ݿ�����
	 * @param user
	 *            ���ݿ��û���
	 * @param password
	 *            ���ݿ�����
	 * @param daoPackage
	 *            dao����λ��
	 * @param beanPackage
	 *            po����λ��
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
				// ��������
				String keyName = prs.getObject(4).toString();
				createDao(con, tableName, keyName, daoPackage, beanPackage);

			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("���ݿ����ô���");
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
	 *            ���ݿ�����
	 * @param user
	 *            ���ݿ��û���
	 * @param password
	 *            ���ݿ�����
	 * @param daoImplPackage
	 *            daoʵ�����λ��
	 * @param beanPackage
	 *            po����λ��
	 * @param daoPackage
	 *            dao����λ��
	 * @param utilPackage
	 *            ���߰���λ��
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
				// ��������
				String keyName = prs.getObject(4).toString();

				createDaoImpl(con, tableName, keyName, daoImplPackage, beanPackage, daoPackage, utilPackage);

			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("���ݿ����ô���");
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

		// �жϰ����Ƿ����
		if (!outputFile.getParentFile().exists()) {
			outputFile.getParentFile().mkdirs();
		}
		String[] colnames; // ��������
		String[] colTypes; // ������������
		int[] colSizes; // ������С����
		// ��Ҫ����ʵ����ı�
		String sql = "select * from `" + tableName + "`";
		PreparedStatement pStemt = null;
		PrintWriter pw = null;
		try {
			pStemt = con.prepareStatement(sql);
			ResultSetMetaData rsmd = pStemt.getMetaData();
			int size = rsmd.getColumnCount(); // ͳ����
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
			System.out.println("�����ƴ���");
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

		// �жϰ����Ƿ����
		if (!outputFile.getParentFile().exists()) {
			outputFile.getParentFile().mkdirs();
		}

		// ��Ҫ����ʵ����ı�
		String sql = "select * from `" + tableName + "`";
		PreparedStatement pStemt = null;
		PrintWriter pw = null;
		try {
			pStemt = con.prepareStatement(sql);
			ResultSetMetaData rsmd = pStemt.getMetaData();
			int size = rsmd.getColumnCount(); // ͳ����
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
			System.out.println("�����ƴ���");
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
		// ע�Ͳ���
		sb.append("/**\n");
		sb.append(" * \n * " + UpperTableName + "Daoʵ����\r\n");
		sb.append(" * " + new Date() + "\r\n");
		sb.append(" * " + "@author " + authorName + " \r\n");
		sb.append(" * \n */ \r\n");
		// ʵ�岿��
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
		// ע�Ͳ���
		sb.append("/**\n");
		sb.append(" * \n * I" + firstLetterUpper(tableName) + "Dao�ӿ�\r\n");
		sb.append(" * " + new Date() + "\r\n");
		sb.append(" * " + "@author " + authorName + " \r\n");
		sb.append(" * \n */ \r\n");
		// ʵ�岿��
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
	 * �������ݿ��б�Ľṹ����po��
	 * 
	 * @param dbName
	 *            ���ݿ�����
	 * @param user
	 *            ���ݿ��û���
	 * @param password
	 *            ���ݿ�����
	 * @param beanPackage
	 *            ����İ��������磺com.han.po
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
			System.out.println("���ݿ����ô���");
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