package com.zk.dao;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.zk.utils.JDBCUtils;

public class DBDao<T> {

	/**
	 * 保存给出的t对象到相应的数据库中
	 * 
	 * @param t 要保存到数据库的对象
	 */
	public static <T> void insert(T t) {
		// 获取对象t的class对象
		@SuppressWarnings("unchecked")
		Class<T> cla = (Class<T>) t.getClass();
		// 获取对象t的所有字段
		Field[] fields = cla.getDeclaredFields();
		// 声明列表用于存放对象t的字段变量名
		List<String> keys = new ArrayList<String>();
		// 声明列表用于存放对象t的字段的值
		List<Object> values = new ArrayList<Object>();
		// 声明Method对象用于接收字段的get方法
		Method method = null;
		// 声明Object对象用于接收字段值
		Object obj = null;
		// 如果字段数组不为空，遍历对象t的字段数组
		if (fields != null && fields.length > 0) {
			for (Field field : fields) {
				// 如果该字段不是ID字段，就保存到字段列表中
				if (!field.getName().equals("id")) {
					keys.add(field.getName());
					try {
						// 获取该字段对应的get方法
						method = cla.getDeclaredMethod(getMethodName(field.getName()));
					} catch (NoSuchMethodException e) {
						e.printStackTrace();
					} catch (SecurityException e) {
						e.printStackTrace();
					}
					try {
						// 执行该字段的get方法并接收返回值
						obj = method.invoke(t);
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
					// 将返回的结果保存到字段值列表中
					values.add(obj);
				}
			}
		}

		// 组拼sql语句
		StringBuffer sql = new StringBuffer(
				"insert into " + cla.getName().substring(cla.getName().lastIndexOf(".") + 1) + "(");
		StringBuffer sqlValues = new StringBuffer("values(");
		for (int i = 0; i < keys.size() - 1; i++) {
			sql.append(keys.get(i) + ",");
			sqlValues.append("?,");
		}
		sql.append(keys.get(keys.size() - 1) + ") ");
		sqlValues.append("?)");
		sql.append(sqlValues);
		Connection conn = null;
		PreparedStatement pstat = null;
		try {
			conn = JDBCUtils.getConnection();
			pstat = conn.prepareStatement(sql.toString());
			for (int i = 0; i < values.size(); i++) {
				pstat.setObject(i + 1, values.get(i));
			}
			pstat.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		JDBCUtils.free(null, pstat, conn);
	}

	/**
	 * 根据给定的Class对象和id查询相应的结果
	 * 
	 * @param cla 给定的Class对象
	 * @param id  给定的id
	 * @return 返回查询到的相应的类的对象
	 */
	public static <T> T select(Class<T> cla, int id) {
		// 设置SQL语句
		String sql = "select * from " + cla.getName().substring(cla.getName().lastIndexOf(".") + 1) + " where id = ?";
		// 获取当前对象所属类中的方法
		Method[] methods = cla.getDeclaredMethods();
		Connection conn = null;
		PreparedStatement pstat = null;
		ResultSet rs = null;
		T t = null;
		try {
			conn = JDBCUtils.getConnection();
			pstat = conn.prepareStatement(sql);
			pstat.setInt(1, id);
			// 获取查询结果
			rs = pstat.executeQuery();
			// 获取查询结果集中的各个列的属性
			ResultSetMetaData rsmd = pstat.getMetaData();
			// 获取查询结果集中列的个数
			int columnNum = rsmd.getColumnCount();
			// 定义字符串数组存放结果集中的列名
			String[] columnNames = new String[columnNum];
			// 获取结果集中各列的列名并存放到数组中
			for (int i = 0; i < columnNum; i++) {
				columnNames[i] = rsmd.getColumnName(i + 1);
			}
			if (rs.next()) {
				t = cla.newInstance();
				for (String columnName : columnNames) {
					// 获取结果集中各列对应的set方法名
					String cName = setMethodName(columnName);
					// 根据方法名获取方法
					for (int i = 0; i < methods.length; i++) {
						if (cName.equals(methods[i].getName())) {
							methods[i].invoke(t, rs.getObject(columnName));
							break;
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCUtils.free(rs, pstat, conn);
		}
		return t;
	}

	
	/**
	 * 根据给定的Class对象和id查询相应的结果 
	 * @param cla  给定的Class对象
	 * @param id   给定的id
	 * @return     返回查询到的相应的类的对象
	 */
	public static <T> T selectByName(Class<T> cla, String name) {
		// 设置SQL语句
		String sql = "select * from " + cla.getName().substring(cla.getName().lastIndexOf(".") + 1) + " where name = ?";
		// 获取当前对象所属类中的方法
		Method[] methods = cla.getDeclaredMethods();
		Connection conn = null;
		PreparedStatement pstat = null;
		ResultSet rs = null;
		T t = null;
		try {
			conn = JDBCUtils.getConnection();
			pstat = conn.prepareStatement(sql);
			pstat.setString(1, name);
			// 获取查询结果
			rs = pstat.executeQuery();
			// 获取查询结果集中的各个列的属性
			ResultSetMetaData rsmd = pstat.getMetaData();
			// 获取查询结果集中列的个数
			int columnNum = rsmd.getColumnCount();
			// 定义字符串数组存放结果集中的列名
			String[] columnNames = new String[columnNum];
			// 获取结果集中各列的列名并存放到数组中
			for (int i = 0; i < columnNum; i++) {
				columnNames[i] = rsmd.getColumnName(i + 1);
			}
			if (rs.next()) {
				t = cla.newInstance();
				for (String columnName : columnNames) {
					// 获取结果集中各列对应的set方法名
					String cName = setMethodName(columnName);
					// 根据方法名获取方法
					for (int i = 0; i < methods.length; i++) {
						if (cName.equals(methods[i].getName())) {
							methods[i].invoke(t, rs.getObject(columnName));
							break;
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCUtils.free(rs, pstat, conn);
		}
		return t;
	}
	
	
	/**
	 * 根据给定的对象和id更新数据
	 * 
	 * @param t
	 *            给定的对象
	 * @param id
	 *            给定的id
	 */
	public static <T> void update(T t, int id) {
		// 获取对象t的class对象
		@SuppressWarnings("unchecked")
		Class<T> cla = (Class<T>) t.getClass();
		// 获取t对象中的所有字段
		Field[] fields = cla.getDeclaredFields();
		// 声明列表用于存放t对象中的字段名（ID除外）
		List<String> keys = new ArrayList<String>();
		// 声明列表用于存放t对象中的字段值（ID除外）
		List<Object> values = new ArrayList<Object>();
		// 声明Method对象用于接收字段的get方法
		Method method = null;
		// 声明Object对象用于接收字段值
		Object obj = null;
		// 如果字段数组不为空，遍历对象t的字段数组
		if (fields != null && fields.length > 0) {
			for (Field field : fields) {
				// 如果该字段不是ID字段，就保存到字段列表中
				if (!field.getName().equals("id")) {
					keys.add(field.getName());
					try {
						// 获取该字段对应的get方法
						method = cla.getDeclaredMethod(getMethodName(field.getName()));
					} catch (NoSuchMethodException e) {
						e.printStackTrace();
					} catch (SecurityException e) {
						e.printStackTrace();
					}
					try {
						// 执行该字段的get方法并接收返回值
						obj = method.invoke(t);
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
					// 将返回的结果保存到字段值列表中
					values.add(obj);
				}
			}
		}
		// 拼接SQL语句
		String table = t.getClass().getName().substring(t.getClass().getName().lastIndexOf(".") + 1);
		StringBuffer sql = new StringBuffer("update " + table + " set ");
		for (int i = 0; i < keys.size() - 1; i++) {
			sql.append(keys.get(i) + " = ? ,");
		}
		sql.append(keys.get(keys.size() - 1) + " = ? where id = ?");

		// 连接数据库
		Connection conn = null;
		PreparedStatement pstat = null;
		try {
			conn = JDBCUtils.getConnection();
			pstat = conn.prepareStatement(sql.toString());
			// 为要执行的SQL语句配置参数
			for (int i = 0; i < values.size(); i++) {
				pstat.setObject(i + 1, values.get(i));
			}
			pstat.setInt(values.size() + 1, id);
			pstat.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JDBCUtils.free(null, pstat, conn);
		}
	}

	/**
	 * 根据给定的对象、条件和数据更新数据
	 * 
	 * @param t
	 *            给定的对象
	 * @param where
	 *            给定的条件
	 * @param value
	 *            给定的值
	 */
	public static <T> void update(T t, String where, Object[] value) {
		// 获取对象t的class对象
		@SuppressWarnings("unchecked")
		Class<T> cla = (Class<T>) t.getClass();
		// 获取t对象中的所有字段
		Field[] fields = cla.getDeclaredFields();
		// 声明列表用于存放t对象中的字段名（ID除外）
		List<String> keys = new ArrayList<String>();
		// 声明列表用于存放t对象中的字段值（ID除外）
		List<Object> values = new ArrayList<Object>();
		// 声明Method对象用于接收字段的get方法
		Method method = null;
		// 声明Object对象用于接收字段值
		Object obj = null;
		// 如果字段数组不为空，遍历对象t的字段数组
		if (fields != null && fields.length > 0) {
			for (Field field : fields) {
				// 如果该字段不是ID字段，就保存到字段列表中
				if (!field.getName().equals("id")) {
					keys.add(field.getName());
					try {
						// 获取该字段对应的get方法
						method = cla.getDeclaredMethod(getMethodName(field.getName()));
					} catch (NoSuchMethodException e) {
						e.printStackTrace();
					} catch (SecurityException e) {
						e.printStackTrace();
					}
					try {
						// 执行该字段的get方法并接收返回值
						obj = method.invoke(t);
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
					// 将返回的结果保存到字段值列表中
					values.add(obj);
				}
			}
		}
		String table = t.getClass().getName().substring(t.getClass().getName().lastIndexOf(".") + 1);
		StringBuffer sql = new StringBuffer("update " + table + " set ");
		for (int i = 0; i < keys.size() - 1; i++) {
			sql.append(keys.get(i) + " = ? ,");
		}
		sql.append(keys.get(keys.size() - 1) + " = ? ");
		if (where != null && where.length() > 0) {
			sql.append(where);
		}
		Connection conn = null;
		PreparedStatement pstat = null;
		try {
			conn = JDBCUtils.getConnection();
			pstat = conn.prepareStatement(sql.toString());
			for (int i = 0; i < values.size(); i++) {
				pstat.setObject(i + 1, values.get(i));
			}
			for (int i = 0, j = values.size(); i < value.length; i++, j++) {
				pstat.setObject(j + 1, value[i]);
			}
			pstat.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JDBCUtils.free(null, pstat, conn);
		}
	}

	/**
	 * 查询所有结果
	 * 
	 * @param cla
	 *            给定的Class对象
	 * @return 返回所有的结果
	 */
	public static <T> List<T> queryAll(Class<T> cla) {
		// 设置SQL语句
		StringBuffer sql = new StringBuffer(
				"select * from " + cla.getName().substring(cla.getName().lastIndexOf(".") + 1));
		// 获取cla对象所属类的方法
		Method[] methods = cla.getDeclaredMethods();
		// 创建列表用于保存查询的结果集
		List<T> listResult = new ArrayList<T>();
		// 声明对象t用于遍历结果集
		T t = null;
		// 连接数据库
		Connection conn = null;
		PreparedStatement pstat = null;
		ResultSet rs = null;
		try {
			conn = JDBCUtils.getConnection();
			pstat = conn.prepareStatement(sql.toString());
			rs = pstat.executeQuery();
			// 获取查询的结果集中列的属性信息
			ResultSetMetaData rsmd = pstat.getMetaData();
			// 获取结果集中的列的个数
			int columnNum = rsmd.getColumnCount();
			// 创建数组用于存放结果集中的列名
			String[] columnNames = new String[columnNum];
			for (int i = 0; i < columnNum; i++) {
				columnNames[i] = rsmd.getColumnName(i + 1);
			}
			// 遍历结果集
			while (rs.next()) {
				try {
					t = cla.newInstance();
				} catch (InstantiationException e1) {
					e1.printStackTrace();
				} catch (IllegalAccessException e1) {
					e1.printStackTrace();
				}
				for (String columnName : columnNames) {
					// 根据字段名获取相应的set方法名
					String methodName = setMethodName(columnName);
					for (int i = 0; i < methods.length; i++) {
						// 方法名在方法数组中找出相应的set方法
						if (methodName.equals(methods[i].getName())) {
							try {
								// 执行相应的set方法，为对象t设置属性值
								methods[i].invoke(t, rs.getObject(columnName));
								break;
							} catch (IllegalAccessException e) {
								e.printStackTrace();
							} catch (IllegalArgumentException e) {
								e.printStackTrace();
							} catch (InvocationTargetException e) {
								e.printStackTrace();
							}
						}
					}
				}
				// 将遍历出的对象添加到指定是列表中
				listResult.add(t);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// 关闭数据库连接
			JDBCUtils.free(rs, pstat, conn);
		}
		// 返回结果列表
		return listResult;
	}

	/**
	 * 根据给定的条件查询一条数据
	 * 
	 * @param cla
	 *            给出的类的Class对象
	 * @param where
	 *            给出的查询条件
	 * @param value
	 *            给出的查询条件中的参数值
	 * @return 返回查询到的结果
	 */
	public static <T> T find(Class<T> cla, String where, Object[] value) {
		// 组合SQL语句
		StringBuffer sql = new StringBuffer(
				"select * from " + cla.getName().substring(cla.getName().lastIndexOf(".") + 1) + " ");
		if (where != null && where.length() > 0) {
			sql.append(where);
		}
		// 获取Class对象cla对应的类中方法
		Method[] methods = cla.getDeclaredMethods();
		// 连接数据库
		Connection conn = null;
		PreparedStatement pstat = null;
		ResultSet rs = null;
		T t = null;
		try {
			conn = JDBCUtils.getConnection();
			pstat = conn.prepareStatement(sql.toString());
			// 设置SQL语句中的参数
			for (int i = 0; i < value.length; i++) {
				pstat.setObject(i + 1, value[i]);
			}
			// 获取结果集
			rs = pstat.executeQuery();
			// 获取结果集中列的属性信息
			ResultSetMetaData rsmd = pstat.getMetaData();
			// 获取结果集中的列的个数
			int columnNum = rsmd.getColumnCount();
			// 创建字符串数组用于保存结果集中的列的名称
			String[] columnNames = new String[columnNum];
			// 获取结果集中的各个列的名称并保存到数组中
			for (int i = 0; i < columnNum; i++) {
				columnNames[i] = rsmd.getColumnName(i + 1);
			}
			// 遍历结果集
			if (rs.next()) {
				try {
					t = cla.newInstance();
				} catch (InstantiationException e1) {
					e1.printStackTrace();
				} catch (IllegalAccessException e1) {
					e1.printStackTrace();
				}
				for (String columnName : columnNames) {
					// 根据字段名获取相应的set方法名
					String methodName = setMethodName(columnName);
					for (int i = 0; i < methods.length; i++) {
						// 方法名在方法数组中找出相应的set方法
						if (methodName.equals(methods[i].getName())) {
							try {
								// 执行相应的set方法，为对象t设置属性值
								methods[i].invoke(t, rs.getObject(columnName));
								break;
							} catch (IllegalAccessException e) {
								e.printStackTrace();
							} catch (IllegalArgumentException e) {
								e.printStackTrace();
							} catch (InvocationTargetException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JDBCUtils.free(rs, pstat, conn);
		}
		return t;
	}

	/**
	 * 根据给定的条件查询结果
	 * 
	 * @param cla
	 *            给定的Class对象
	 * @param where
	 *            给定的查询条件
	 * @param value
	 *            给定的查询条件中的参数值
	 * @return 返回查询到的结果集
	 */
	public static <T> List<T> query(Class<T> cla, String where, Object[] value) {
		// 组合SQL语句
		StringBuffer sql = new StringBuffer(
				"select * from " + cla.getName().substring(cla.getName().lastIndexOf(".") + 1) + " ");
		if (where != null && where.length() > 0) {
			sql.append(where);
		}
		// 获取Class对象cla对应的类中方法
		Method[] methods = cla.getDeclaredMethods();
		// 连接数据库
		Connection conn = null;
		PreparedStatement pstat = null;
		ResultSet rs = null;
		List<T> listResult = new ArrayList<T>();
		T t = null;
		try {
			conn = JDBCUtils.getConnection();
			pstat = conn.prepareStatement(sql.toString());
			// 设置SQL语句中的参数
			for (int i = 0; i < value.length; i++) {
				pstat.setObject(i + 1, value[i]);
			}
			// 获取结果集
			rs = pstat.executeQuery();
			// 获取结果集中列的属性信息
			ResultSetMetaData rsmd = pstat.getMetaData();
			// 获取结果集中的列的个数
			int columnNum = rsmd.getColumnCount();
			// 创建字符串数组用于保存结果集中的列的名称
			String[] columnNames = new String[columnNum];
			// 获取结果集中的各个列的名称并保存到数组中
			for (int i = 0; i < columnNum; i++) {
				columnNames[i] = rsmd.getColumnName(i + 1);
			}
			// 遍历结果集
			while (rs.next()) {
				try {
					t = cla.newInstance();
				} catch (InstantiationException e1) {
					e1.printStackTrace();
				} catch (IllegalAccessException e1) {
					e1.printStackTrace();
				}
				for (String columnName : columnNames) {
					// 根据字段名获取相应的set方法名
					String methodName = setMethodName(columnName);
					for (int i = 0; i < methods.length; i++) {
						// 方法名在方法数组中找出相应的set方法
						if (methodName.equals(methods[i].getName())) {
							try {
								// 执行相应的set方法，为对象t设置属性值
								methods[i].invoke(t, rs.getObject(columnName));
								break;
							} catch (IllegalAccessException e) {
								e.printStackTrace();
							} catch (IllegalArgumentException e) {
								e.printStackTrace();
							} catch (InvocationTargetException e) {
								e.printStackTrace();
							}
						}
					}
				}
				// 将遍历出的对象添加到指定是列表中
				listResult.add(t);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JDBCUtils.free(rs, pstat, conn);
		}
		return listResult;
	}

	/**
	 * 根据给出的Class对象和ID删除相应的数据
	 * 
	 * @param cla
	 *            给出的Class对象
	 * @param id
	 *            给出的ID
	 */
	public static <T> void delete(Class<T> cla, int id) {
		// 拼接SQL语句
		String tableName = cla.getName().substring(cla.getName().lastIndexOf(".") + 1);
		String sql = new String("delete from " + tableName + " where id = ?");
		// 连接数据库
		Connection conn = null;
		PreparedStatement pstat = null;

		try {
			conn = JDBCUtils.getConnection();
			pstat = conn.prepareStatement(sql);
			pstat.setInt(1, id);
			pstat.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// 关闭数据库连接
			JDBCUtils.free(null, pstat, conn);
		}
	}

	/**
	 * 根据给出的Class对象、条件和参数值删除相应的数据
	 * 
	 * @param cla
	 *            给出的Class对象
	 * @param where
	 *            给出的条件
	 * @param value
	 *            给出的条件的参数值
	 */
	public static <T> void delete(Class<T> cla, String where, Object[] value) {
		String tableName = cla.getName().substring(cla.getName().lastIndexOf(".") + 1);
		StringBuffer sql = new StringBuffer("delete from " + tableName + " ");
		if (where != null && where.length() > 0) {
			sql.append(where);
		}
		// 连接数据库
		Connection conn = null;
		PreparedStatement pstat = null;
		try {
			conn = JDBCUtils.getConnection();
			pstat = conn.prepareStatement(sql.toString());
			for (int i = 0; i < value.length; i++) {
				pstat.setObject(i + 1, value[i]);
			}
			pstat.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// 关闭数据库连接
			JDBCUtils.free(null, pstat, conn);
		}

	}

	/**
	 * 根据给出的Class对象清空相应的数据表
	 * 
	 * @param cla
	 *            给出的Class对象
	 */
	public static <T> void clear(Class<T> cla) {
		String tableName = cla.getName().substring(cla.getName().lastIndexOf(".") + 1);
		String sql = new String("delete from " + tableName);

		// 连接数据库
		Connection conn = null;
		PreparedStatement pstat = null;
		try {
			conn = JDBCUtils.getConnection();
			pstat = conn.prepareStatement(sql.toString());
			pstat.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// 关闭数据库连接
			JDBCUtils.free(null, pstat, conn);
		}
	}

	/**
	 * 根据给出的字段名获取相应的get方法
	 * 
	 * @param name
	 *            给出的字段名
	 * @return 返回相应字段的get方法
	 */
	private static String getMethodName(String name) {
		char[] ch = name.toCharArray();
		ch[0] -= 32;
		String str = new String(ch);
		return "get" + str;
	}

	/**
	 * 根据给出的字段名获取相应的set方法
	 * 
	 * @param name
	 *            给出的字段名
	 * @return 返回相应字段的set方法
	 */
	private static String setMethodName(String name) {
		char[] ch = name.toCharArray();
		ch[0] -= 32;
		String str = new String(ch);
		return "set" + str;
	}
}
