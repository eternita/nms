package org.neuro4j.storage.inmemory.qp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.neuro4j.core.Connected;
import org.neuro4j.core.Network;
import org.neuro4j.utils.N4JConfig;
import org.neuro4j.utils.StringUtils;

public class N4JSQLUtils {
	
	public static Set<String> getSQLTableHeaders(Network net)
	{
		Set<String> headers = new LinkedHashSet<String>();
		headers.add("id");
		headers.add("name");
		headers.add("n4jtype");
		headers.add("connected");
		
		for (String id : net.getIds())
		{
			Connected er = net.getById(id);
			headers.addAll(er.getPropertyKeys());
		}
		
		return headers;
	}
	
	public static Connection getConnection(Map<String, String> params) throws SQLException
	{
		String jdbcURL = N4JConfig.getProperty("n4j.quering.sql.jdbc.url");
		if (null != params.get("jdbc"))
			jdbcURL = params.get("jdbc");
		
		Connection conn = null;
	    Properties connectionProps = new Properties();
	    // check if no user in url
	    if (-1 == jdbcURL.indexOf("user="))
	    	connectionProps.put("user", N4JConfig.getProperty("n4j.quering.sql.jdbc.user"));
	    
	    // check if no password in url
	    if (-1 == jdbcURL.indexOf("password="))
	    	connectionProps.put("password", N4JConfig.getProperty("n4j.quering.sql.jdbc.password"));
	    
		conn = DriverManager.getConnection(jdbcURL, connectionProps);

	    return conn;

	}
	
	public static void createTempTable(Connection conn, String tableName, Set<String> headers) // throws SQLException
	{
	
		Statement st = null;
		try {
			st = conn.createStatement();
			StringBuffer sb = new StringBuffer();
			sb.append("CREATE TABLE "+ tableName);
			sb.append("(");
			boolean first = true;
			for (String header : headers)
			{
				if (first)
				{
					first = false;
				} else {
					sb.append(", ");					
				}
				sb.append(header).append("  varchar(1024)");
			}
				
			sb.append(")  ");
			sb.append("");
			
			st.execute(sb.toString());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (null != st)
			{
				try {
					st.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}			
			}
		}
		return;
	}

	public static void dropTempTable(Connection conn, String tableName) 
	{
	
		Statement st = null;
		try {
			st = conn.createStatement();
			StringBuffer sb = new StringBuffer();
			sb.append("DROP TABLE "+ tableName);
			
			st.execute(sb.toString());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (null != st)
			{
				try {
					st.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}			
			}
		}
		return;
	}
	
	public static Network queryTempTable(Connection conn, String tableName, String sql) throws SQLException
	{
	
		Statement st = null;
		st = conn.createStatement();
		
		ResultSet result = st.executeQuery(sql);
		
		ResultSetMetaData rsmd = result.getMetaData();
		Network net = new Network();
		
		// create header
		Connected header = new Connected("n4j_sql_table_header");
		int cnt = rsmd.getColumnCount();
		for (int i = 1; i <= cnt; i++)
		{
			String columnName = rsmd.getColumnName(i);
			header.setProperty("c." + (i-1), columnName);
		}
		net.add(header);
		
		// create rows
		while (result.next())
		{
			Connected row = new Connected("n4j_row");
			for (int i = 1; i <= cnt; i++)
			{
				String key = rsmd.getColumnName(i);
				String value = result.getString(i);
				row.setProperty(key, value);
			}
			
			net.add(row);
		}
		
		return net;
	}
	
	
	public static void addER2Table(Connection conn, Connected er, String tableName)
	{
		List<String> values = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		sb.append("INSERT INTO "+ tableName);
		sb.append("(id, name, connected");
		
		sb.append("");
		for (String key : er.getPropertyKeys())
		{
			sb.append(" ,").append(key);
			values.add(er.getProperty(key));
		}
		sb.append(")");
		
		sb.append(" VALUES(");
		sb.append("'" + er.getUuid() + "'").append(", ");
		sb.append("'" + er.getName() + "'").append(", ");			
		sb.append(", ").append("'").append(StringUtils.set2str(er.getConnectedKeys())).append("'");
		
		for (String val : values)
		{
			sb.append(" ,").append("'").append(val).append("'");
		}
		sb.append(")");

		sb.append("");

		Statement st = null;
		try {
			st = conn.createStatement();
			st.executeUpdate(sb.toString());
			
			st.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (null != st)
			{
				try {
					st.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}			
			}			
		}
		
		
	}


}
