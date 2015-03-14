package com.pcwrek.seck;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Set;


public class DBConnection {

	public static void main(String[] args) throws SQLException,
			ClassNotFoundException {

		// Connection connection = getConnetion();

		// String query =
		// "insert into RECORD(url,http_header,html_header,page_text) values('www.google.com','google http header','google html header','google page text')";

		// String query =
		// "insert into rec(url,http_header,resource_files) values('www.google.com','google http header','"+MapToJson()+"')";
		//
		// Statement st = connection.createStatement();
		//
		// st.executeUpdate(query);
		// connection.close();
		//
	}

	public static void inserRecord(Connection connection, String URL,
			Map<String, String> httpHeader, String htmlHeader, String pageText,
			Map<String, Set<String>> resorucesFiles) {

		Statement statement = null;

		try {
//			Gson gson = new Gson();		
//			String httperHeaderJson = gson.toJson(httpHeader);
//
//			String resourceFileJson = gson.toJson(resorucesFiles);

			//htmlHeader = htmlHeader.replaceAll("'", "");

//			String query = "insert into rec(url,http_header,html_header,page_text,resource_files) values( '"
//					+ URL
//					+ "','"
//					+ httperHeaderJson
//					+ "', '"
//					+ htmlHeader
//					+ "','" + pageText + "' , '" + resourceFileJson + "')";
			
			
//			String query = "insert into rec(url,http_header,page_text,resource_files) values( '"
//			+ URL
//			+ "','"
//			+ httperHeaderJson
//			+ "', '" + pageText + "' , '" + resourceFileJson + "')";
//			statement = connection.createStatement();

//			statement.executeUpdate(query);

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

	}

	public static Connection getConnetion() throws SQLException,
			ClassNotFoundException {
		Class.forName("org.postgresql.Driver");
		String url = "jdbc:postgresql://localhost:5432/seck";
		return DriverManager.getConnection(url, "seck", "seck");
	}

}
