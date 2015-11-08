/* ***************************************************************** */
/*                                                                   */
/* Licensed Materials - Property of IBM                              */
/*                                                                   */
/* 5724-E76, 5724-L21, 5724-i67, 5724-L64, 5655-M44                  */
/*                                                                   */
/* Copyright IBM Corp. 2008, 2014  All Rights Reserved.           */
/*                                                                   */
/* US Government Users Restricted Rights - Use, duplication or       */
/* disclosure restricted by GSA ADP Schedule Contract with           */
/* IBM Corp.                                                         */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.sample.callcenter.manager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.ibm.sample.callcenter.model.CallBean;
import com.ibm.sample.callcenter.model.CustomerBean;
import com.ibm.sample.callcenter.model.PersistentChatBean;
import com.ibm.sample.callcenter.model.TechnicalAreaBean;

public class CallRecordManagerBean {
	
	private static DataSource ds = null;
	
	public CallRecordManagerBean() {
		try {
			InitialContext ic = new InitialContext();
			ds = (DataSource)ic.lookup("java:comp/env/jdbc/CallCenterDB");
		} catch(NamingException ne)
		{
			ne.printStackTrace();
		}
	}	
	
	/**
	 * Retrieves all call records from database and instantiates
	 * one CallBean object per row.
	 */
	public List loadAllCalls(String operatorName, int status) {
		Connection conn = null;	    
		List allCalls = new ArrayList<CallBean>();
		try {
			
			
			conn = ds.getConnection();
			Statement stmt = conn.createStatement();
			String query = null;
			if(null!=operatorName)
			{
				query = "SELECT SACALL.SA_CALL_ID, SACALL.CALL_DESCRIPTION, SACALL.OPERATOR_NAME, CUST.CUSTOMER_ID, CUST.CUSTOMER_NAME, " +
						"TAREA.TECHNICAL_ID, TAREA.TECHNICAL_AREA, SACALL.STATUS, SACALL.DATE, SACALL.CUSTOMER_CHAT_URL, SACALL.CUSTOMER_CHAT_ID, " +
						"SACALL.TECH_AREA_CHAT_URL, SACALL.TECH_AREA_CHAT_ID FROM SA_CALL SACALL, TECHNICAL_AREA TAREA, " +
						"CUSTOMER CUST WHERE SACALL.TECHNICAL_ID=TAREA.TECHNICAL_ID AND SACALL.CUSTOMER_ID=CUST.CUSTOMER_ID AND SACALL.STATUS="+status +
						" AND SACALL.OPERATOR_NAME = '"+operatorName+"'";				
			}
			else {
				query = "SELECT SACALL.SA_CALL_ID, SACALL.CALL_DESCRIPTION, SACALL.OPERATOR_NAME, CUST.CUSTOMER_ID, CUST.CUSTOMER_NAME, " +
						"TAREA.TECHNICAL_ID, TAREA.TECHNICAL_AREA, SACALL.STATUS, SACALL.DATE, SACALL.CUSTOMER_CHAT_URL, SACALL.CUSTOMER_CHAT_ID, " +
						"SACALL.TECH_AREA_CHAT_URL, SACALL.TECH_AREA_CHAT_ID FROM SA_CALL SACALL, TECHNICAL_AREA TAREA, " +
						"CUSTOMER CUST WHERE SACALL.TECHNICAL_ID=TAREA.TECHNICAL_ID AND SACALL.CUSTOMER_ID=CUST.CUSTOMER_ID AND SACALL.STATUS="+status;
			}
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next()) {
				CallBean bean = new CallBean();

				bean.setCallId(rs.getInt(1));
				bean.setCallDescription(rs.getString(2));
				bean.setOperatorName(rs.getString(3));
				bean.setCustomerId(rs.getInt(4));
				bean.setCustomerName(rs.getString(5));
				bean.setTechnicalId(rs.getString(6));
				bean.setTechinalArea(rs.getString(7));
				bean.setStatus(""+rs.getInt(8));
				bean.setDate(formatDate(rs.getString(9)));
				bean.setCustChatRoomLink(rs.getString(10));    
				bean.setCustChatRoomId(rs.getString(11));
				bean.setTechAreaChatRoomLink(rs.getString(12));
				bean.setTechAreaChatRoomId(rs.getString(13));

				allCalls.add(bean);	        	
			}
			rs.close();
			stmt.close();
			conn.close();	        	
		} catch (SQLException sqlExcept) {
			sqlExcept.printStackTrace();
		}
		return allCalls;
	}

	/**
	 * Checks if the customer already exists, if it doesn't then creates a new customer
	 * @param name
	 * @return
	 */
	public int insertCustomer(String name) {
		int numRowsInserted = 0;
		Connection conn = null;
		PreparedStatement prepStmt = null;
		try {
			conn = ds.getConnection();
			
			Statement stmt = conn.createStatement();
			String selectQuery = "SELECT CUSTOMER_NAME FROM CUSTOMER WHERE CUSTOMER_NAME = '"+ name+"'";
			ResultSet rs = stmt.executeQuery(selectQuery);
			if(rs.next())
				return 1;
			rs.close();
			stmt.close();
						
			String query = "INSERT INTO CUSTOMER (CUSTOMER_NAME) VALUES (?)";
			prepStmt = conn.prepareStatement(query);
			prepStmt.setString(1, name);
			numRowsInserted = prepStmt.executeUpdate();
			prepStmt.close();
			conn.close();
		} catch (SQLException sqlExcept) {
			try {
				prepStmt.close();
				conn.close();
			} catch (SQLException sqlE) {

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return numRowsInserted;
	}

	/**
	 * Checks if the technical area already exists, if it doesn't then creates a new technical area
	 * @param areaName
	 * @return
	 */
	public int insertTechnicalArea(String areaName) {
		int numRowsInserted = 0;
		Connection conn = null;
		PreparedStatement prepStmt = null;
		try {
			conn = ds.getConnection();
			
			Statement stmt = conn.createStatement();
			String selectQuery = "SELECT TECHNICAL_AREA FROM TECHNICAL_AREA WHERE TECHNICAL_AREA = '"+ areaName+"'";
			ResultSet rs = stmt.executeQuery(selectQuery);			
			if(rs.next())
				return 1;
			rs.close();
			stmt.close();
			
			String query = "INSERT INTO TECHNICAL_AREA (TECHNICAL_AREA) VALUES (?)";
			prepStmt = conn.prepareStatement(query);
			prepStmt.setString(1, areaName);
			numRowsInserted = prepStmt.executeUpdate();
			prepStmt.close();
			conn.close();
		} catch (SQLException sqlExcept) {
			try {
				prepStmt.close();
				conn.close();
			} catch (SQLException sqlE) {

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return numRowsInserted;
	}

	/**
	 * Inserts a new call into the database
	 * @param custId
	 * @param techId
	 * @param description
	 * @param status
	 * @param date
	 * @param custUrl
	 * @param TechUrl
	 * @return
	 */
	public int insertCall(int custId, int techId, String description, int status, Timestamp date, PersistentChatBean customerChat, 
						  PersistentChatBean techAreaChat, String operatorName) {
		int numRowsInserted = 0;
		Connection conn = null;
		PreparedStatement prepStmt = null;
		try {
			conn = ds.getConnection();
			
			String query = "INSERT INTO SA_CALL (OPERATOR_NAME, CUSTOMER_ID, TECHNICAL_ID, CALL_DESCRIPTION, STATUS, DATE, CUSTOMER_CHAT_URL, CUSTOMER_CHAT_ID, TECH_AREA_CHAT_URL, TECH_AREA_CHAT_ID) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			prepStmt = conn.prepareStatement(query);
			prepStmt.setString(1, operatorName);
			prepStmt.setInt(2, custId);
			prepStmt.setInt(3, techId);
			prepStmt.setString(4, description);
			prepStmt.setInt(5, status);
			prepStmt.setTimestamp(6, date);
			prepStmt.setString(7, customerChat.getUrl());
			
			String tempChatId = customerChat.getId();
			if (tempChatId.charAt(0) == '/')
				tempChatId = tempChatId.substring(1);
			
			String tempTechAreaChatId = techAreaChat.getId();
			if (tempTechAreaChatId.charAt(0) == '/')
				tempTechAreaChatId = tempTechAreaChatId.substring(1);
			
			
			prepStmt.setString(8, tempChatId);
			prepStmt.setString(9, techAreaChat.getUrl());
			prepStmt.setString(10, tempTechAreaChatId);
			numRowsInserted = prepStmt.executeUpdate();
			prepStmt.close();
			conn.close();
		} catch (SQLException sqlExcept) {
			try {
				prepStmt.close();
				conn.close();
			} catch (SQLException sqlE) {

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return numRowsInserted;
	}
	
	/**
	 * Retrieves all customer records from database and instantiates
	 * one CustomerBean object per row.
	 */
	public List getAllCustomers() {
		Connection conn = null;	    
		List allCustomers = new ArrayList<CustomerBean>();
		try {
			conn = ds.getConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT CUST.CUSTOMER_ID, CUST.CUSTOMER_NAME FROM CUSTOMER CUST");
			while(rs.next()) {
				CustomerBean bean = new CustomerBean();

				bean.setCustomerId(rs.getInt(1));
				bean.setCustomerName(rs.getString(2));
				
				allCustomers.add(bean);	        	
			}
			rs.close();
			stmt.close();
			conn.close();	        	
		} catch (SQLException sqlExcept) {
			sqlExcept.printStackTrace();
		}
		return allCustomers;
	}
	
	/**
	 * Retrieves all customer records from database and instantiates
	 * one CustomerBean object per row.
	 */
	public List getAllTechnicalAreas() {
		Connection conn = null;	    
		List allTechnicalAreas = new ArrayList<TechnicalAreaBean>();
		try {
			conn = ds.getConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT T_AREA.TECHNICAL_ID, T_AREA.TECHNICAL_AREA FROM TECHNICAL_AREA T_AREA");
			while(rs.next()) {
				TechnicalAreaBean bean = new TechnicalAreaBean();

				bean.setTechnicalId(rs.getInt(1));
				bean.setTechinalArea(rs.getString(2));
				
				allTechnicalAreas.add(bean);	        	
			}
			rs.close();
			stmt.close();
			conn.close();	        	
		} catch (SQLException sqlExcept) {
			sqlExcept.printStackTrace();
		}
		return allTechnicalAreas;
	}
	
	/**
	 * Update the status of an existing call identified by its call number. However the call is never deleted from the database.
	 * @param callNumber
	 */
	public int updateCallStatus(int callNumber, int status) {
		Connection conn = null;	   
		PreparedStatement prepStmt = null;
		int numRowsUpdated = 0;
		try {
			conn = ds.getConnection();
			Statement stmt = conn.createStatement();
			String query = "UPDATE SA_CALL SET STATUS = ? WHERE SA_CALL_ID = ?";
			prepStmt = conn.prepareStatement(query);
			prepStmt.setInt(1, status);
			prepStmt.setInt(2, callNumber);
			numRowsUpdated = prepStmt.executeUpdate();
			prepStmt.close();
			stmt.close();
			conn.close();	        	
		} catch (SQLException sqlExcept) {
			sqlExcept.printStackTrace();
		}
		
		return numRowsUpdated;
	}
	
	/**
	 * Returns a Customer name based on its available id
	 * @param custId
	 * @return
	 */
	public String getCustomerName(int custId){
		Connection conn = null;	    
		String custName = null;
		try {
			conn = ds.getConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT CUST.CUSTOMER_NAME FROM CUSTOMER CUST WHERE CUST.CUSTOMER_ID="+custId);
			while(rs.next()) {
				custName = rs.getString(1);	        	
			}
			rs.close();
			stmt.close();
			conn.close();	        	
		} catch (SQLException sqlExcept) {
			sqlExcept.printStackTrace();
		}
		return custName;		
	}
	
	/**
	 * Returns a Technical Area name based on its available id
	 * @param techAreaId
	 * @return
	 */
	public String getTechnicalAreaName(int techAreaId) {
		Connection conn = null;	    
		String techArea = null;
		try {
			conn = ds.getConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT T_AREA.TECHNICAL_AREA FROM TECHNICAL_AREA T_AREA WHERE T_AREA.TECHNICAL_ID="+techAreaId);
			while(rs.next()) {
				techArea = rs.getString(1);
			}
			rs.close();
			stmt.close();
			conn.close();	        	
		} catch (SQLException sqlExcept) {
			sqlExcept.printStackTrace();
		}
		return techArea;
	}
	
	/**
	 * Deletes a Customer based on its Customer id
	 * @param custId
	 * @return
	 */
	public int deleteCustomer(int custId) {
		Connection conn = null;	   
		PreparedStatement prepStmt = null;
		int numRowsUpdated = 0;
		try {
			conn = ds.getConnection();
			Statement stmt = conn.createStatement();
			String query = "DELETE FROM CUSTOMER WHERE CUSTOMER_ID=?";
			prepStmt = conn.prepareStatement(query);
			prepStmt.setInt(1, custId);
			numRowsUpdated = prepStmt.executeUpdate();
			prepStmt.close();
			stmt.close();
			conn.close();	        	
		} catch (SQLException sqlExcept) {
			sqlExcept.printStackTrace();
		}
		
		return numRowsUpdated;
	}	
	
	/**
	 * Deletes a Technical Area based on its techAreaId
	 * @param techAreaId
	 * @return
	 */
	public int deleteTechnicalArea(int techAreaId) {
		Connection conn = null;	   
		PreparedStatement prepStmt = null;
		int numRowsUpdated = 0;
		try {
			conn = ds.getConnection();
			Statement stmt = conn.createStatement();
			String query = "DELETE FROM TECHNICAL_AREA WHERE TECHNICAL_ID=?";
			prepStmt = conn.prepareStatement(query);
			prepStmt.setInt(1, techAreaId);
			numRowsUpdated = prepStmt.executeUpdate();
			prepStmt.close();
			stmt.close();
			conn.close();	        	
		} catch (SQLException sqlExcept) {
			sqlExcept.printStackTrace();
		}
		
		return numRowsUpdated;
	}	
	
	/**
	 * The date string passed to this method is a string representation of a TimeStamp object.
	 * @param date
	 * @return
	 */
	private String formatDate(String date) {
		Long time = Timestamp.valueOf(date).getTime();
		Date newDate = new Date(time);
		DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
    	return df.format(newDate); 	
	}
}
