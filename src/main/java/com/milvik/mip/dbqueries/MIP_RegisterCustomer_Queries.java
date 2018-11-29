package com.milvik.mip.dbqueries;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.testng.Assert;

import com.milvik.mip.dataprovider.MIP_RegisterCustomer_TestData;
import com.milvik.mip.pageutil.MIP_CustomerManagementPage;
import com.milvik.mip.utility.MIP_DataBaseConnection;
import com.milvik.mip.utility.MIP_Logging;
import com.milvik.mip.utility.MIP_ReadPropertyFile;

public class MIP_RegisterCustomer_Queries {
	private static ResultSet result;
	private static Logger logger;
	static {
		logger = MIP_Logging.logDetails("MIP_RegisterCustomer_Queries");
	}
	private static Map<String, String> custDetails = new HashMap<String, String>();

	public static Map<String, String> getCustomerDetails(String msisdn,
			String offer) {
		logger.info("Executing getCustomerDetails query");
		try {
			result = MIP_DataBaseConnection.st
					.executeQuery("select cd.fname,cd.sname,cd.age,DATE_FORMAT(cd.dob,'%d/%m/%Y')as dob,cd.gender,cd.cnic,cd.w_cnic,(select user_uid from user_details where user_id=cd.modified_by) as user,cd.modified_date,cd.is_prepaid,cd.is_active,cd.current_bill_group,cd.is_warid"
							+ " from customer_details cd where cd.msisdn="
							+ msisdn + ";");
			result.next();
			custDetails.put("cust_fname", result.getString("fname"));
			custDetails.put("cust_sname", result.getString("sname"));
			custDetails.put("cust_age", result.getString("age"));
			if (result.getString("gender") == null) {
				custDetails.put("cust_gender", "");
			} else
				custDetails.put("cust_gender", result.getString("gender"));
			if (result.getString("dob") == null) {
				custDetails.put("cust_dob", "");
			} else
				custDetails.put("cust_dob", result.getString("dob"));
			if (result.getString("cnic") == null) {
				custDetails.put("cust_cnic", "");
			} else
				custDetails.put("cust_cnic", result.getString("cnic"));
			if (result.getString("w_cnic") == null) {
				custDetails.put("w_cnic", "");
			} else
				custDetails.put("w_cnic", result.getString("w_cnic"));
			custDetails.put("user", result.getString("user"));
			custDetails.put("modified_date", result.getString("modified_date"));
			custDetails.put("is_active", result.getString("is_active"));

			custDetails.put("is_prepaid", result.getString("is_prepaid"));
			custDetails.put("current_bill_group",
					result.getString("current_bill_group"));
			custDetails.put("is_warid", result.getString("is_warid"));

			if (offer
					.contains(MIP_CustomerManagementPage.BIMA_ACCIDENT_PROTECTION)) {
				result = MIP_DataBaseConnection.st
						.executeQuery("select  ird.fname as ben_fname,ird.sname as ben_sname,ird.age as ben_age,ird.gender as ben_gender,ird.msisdn as ben_msisdn,ird.cust_relationship "
								+ " from insured_relative_details ird join customer_details cd on cd.cust_id=ird.cust_id"
								+ " join offer_details od on od.offer_id=ird.offer_id"
								+ " where cd.msisdn="
								+ msisdn
								+ " and od.offer_name like '%Bima Accident Protection';");
				result.next();
				custDetails.put("accident_ben_fname",
						result.getString("ben_fname"));
				custDetails.put("accident_ben_sname",
						result.getString("ben_sname"));
				custDetails
						.put("accident_ben_age", result.getString("ben_age"));
				custDetails.put("accident_cust_relationship",
						result.getString("cust_relationship"));

				if (result.getString("ben_gender") == null)
					custDetails.put("accident_ben_gender", "");
				else
					custDetails.put("accident_ben_gender",
							result.getString("ben_gender"));
				if (result.getString("ben_msisdn") == null)
					custDetails.put("accident_ben_msisdn", "");
				else
					custDetails.put("accident_ben_msisdn",
							result.getString("ben_msisdn"));
			}
			if (offer.contains(MIP_CustomerManagementPage.BIMA_HOSPITALIZATION)) {
				result = MIP_DataBaseConnection.st
						.executeQuery("select  ird.fname as ben_fname,ird.sname as ben_sname,ird.age as ben_age,ird.gender as ben_gender,ird.msisdn as ben_msisdn,ird.cust_relationship "
								+ " from insured_relative_details ird join customer_details cd on cd.cust_id=ird.cust_id"
								+ " join offer_details od on od.offer_id=ird.offer_id"
								+ " where cd.msisdn="
								+ msisdn
								+ " and od.offer_name = 'Hospitalization';");
				result.next();
				custDetails.put("hospitalization_ben_fname",
						result.getString("ben_fname"));
				custDetails.put("hospitalization_ben_sname",
						result.getString("ben_sname"));
				custDetails.put("hospitalization_ben_age",
						result.getString("ben_age"));
				custDetails.put("hospitalization_cust_relationship",
						result.getString("cust_relationship"));

				if (result.getString("ben_gender") == null)
					custDetails.put("hospitalization_ben_gender", "");
				else
					custDetails.put("hospitalization_ben_gender",
							result.getString("ben_gender"));
				if (result.getString("ben_msisdn") == null)
					custDetails.put("hospitalization_ben_msisdn", "");
				else
					custDetails.put("hospitalization_ben_msisdn",
							result.getString("ben_msisdn"));
			}

		} catch (SQLException e) {
			logger.error(
					"Error while executing the getCustomerDetails queries", e);
		}
		return custDetails;
	}

	public static List<String> getRegisteredProduct(String msisdn) {
		List<String> offer = new ArrayList<String>();
		logger.info("Executing getRegisteredProduct query");
		try {

			result = MIP_DataBaseConnection.st
					.executeQuery("select od.offer_name from offer_details od join offer_subscription os on od.offer_id=os.offer_id"
							+ " join customer_details cd on cd.cust_id=os.cust_id where cd.msisdn="
							+ msisdn + ";");
			while (result.next()) {
				offer.add(result.getString("offer_name").trim());
			}

		} catch (Exception e) {
			logger.error(
					"Error while executing the getRegisteredProduct queries", e);
		}
		return offer;
	}

	public static Map<String, String> getOfferCover(String msisdn,
			String offerName) {
		Map<String, String> offer = new HashMap<String, String>();
		logger.info("Executing getOfferCover query");
		try {
			if (offerName
					.contains(MIP_CustomerManagementPage.BIMA_ACCIDENT_PROTECTION)) {
				result = MIP_DataBaseConnection.st
						.executeQuery("select  TRIM(TRAILING '.' FROM TRIM(TRAILING  '0' from ocd.offer_cover)) as offer_cover from offer_cover_details ocd join offer_subscription os on"
								+ " os.offer_cover_id=ocd.offer_cover_id join offer_details oc on os.offer_id=oc.offer_id  "
								+ " where os.cust_id=(Select cust_id from customer_details where msisdn="
								+ msisdn
								+ ") and oc.offer_name like'%"
								+ MIP_CustomerManagementPage.BIMA_ACCIDENT_PROTECTION
								+ "';");
				while (result.next()) {
					offer.put("accident_cover", result.getString("offer_cover")
							.trim());
				}
			}
			if (offerName
					.contains(MIP_CustomerManagementPage.BIMA_HOSPITALIZATION)) {
				result = MIP_DataBaseConnection.st
						.executeQuery("select  TRIM(TRAILING '.' FROM TRIM(TRAILING  '0' from ocd.offer_cover)) as offer_cover from offer_cover_details ocd join offer_subscription os on"
								+ " os.offer_cover_id=ocd.offer_cover_id join offer_details oc on os.offer_id=oc.offer_id  "
								+ " where os.cust_id=(Select cust_id from customer_details where msisdn="
								+ msisdn
								+ ") and oc.offer_name ='"
								+ MIP_CustomerManagementPage.BIMA_HOSPITALIZATION
								+ "';");
				while (result.next()) {
					offer.put("hospitalization_cover",
							result.getString("offer_cover").trim());
				}
			}

		} catch (Exception e) {
			logger.error("Error while executing the getOfferCover queries", e);
		}
		return offer;
	}

	public static Map<String, String> getRegistrationSMS(String platform,
			String msisdn, String offer) {
		Map<String, String> sms = new HashMap<String, String>();
		String sms_template = "";
		Connection con = null;
		ResultSet result = null;
		Statement st = null;
		logger.info("Executing getRegistrationSMS query");
		sms_template = "registration_success";

		try {
			logger.info("Connecting to SMS  Database");
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
			if (platform.equalsIgnoreCase("windows")) {
				con = DriverManager.getConnection("jdbc:mysql:"
						+ MIP_ReadPropertyFile.getPropertyValue("smsdburl"),
						MIP_ReadPropertyFile.getPropertyValue("smsdbusername"),
						MIP_ReadPropertyFile.getPropertyValue("smsdbpassword"));
			} else if (platform.equalsIgnoreCase("AWS")) {
				con = DriverManager.getConnection("jdbc:mysql:"
						+ MIP_ReadPropertyFile.getPropertyValue("awsdburl"),
						MIP_ReadPropertyFile.getPropertyValue("awsdbusername"),
						MIP_ReadPropertyFile.getPropertyValue("awsdbpassword"));
			}

			if (con != null) {
				logger.info("Connecting SMS to Database is successfull");
			}
		} catch (ClassNotFoundException e) {
			logger.error("Error while connecting to DataBase", e);
		} catch (SQLException e) {
			logger.error("Error while connecting to DataBase", e);
		} catch (InstantiationException e) {
			logger.error("Error while connecting to DataBase", e);
		} catch (IllegalAccessException e) {
			logger.error("Error while connecting to DataBase", e);
		}
		try {
			st = con.createStatement();
		} catch (SQLException e) {
			logger.error("Error while creating statement", e);
		}
		try {
			if (offer
					.contains(MIP_CustomerManagementPage.BIMA_ACCIDENT_PROTECTION)) {
				sms_template = "dashboard_warid_registration_success";
				result = st
						.executeQuery("select sms_text,sms_type from coaching_program_sms_details where msisdn="
								+ msisdn
								+ " and sms_template_name like '"
								+ sms_template + "'  order by id;");

				while (result.next()) {
					sms.put("ap_sms", result.getString("sms_text"));
					sms.put("ap_sms_type", result.getString("sms_type"));
				}
			}
			if (offer.contains(MIP_CustomerManagementPage.BIMA_HOSPITALIZATION)) {
				sms_template = "hp_dashboard_warid_registration_success";
				result = st
						.executeQuery("select sms_text,sms_type from coaching_program_sms_details where msisdn="
								+ msisdn
								+ " and sms_template_name like '"
								+ sms_template + "'  order by id;");

				while (result.next()) {
					sms.put("hp_sms", result.getString("sms_text"));
					sms.put("hp_sms_type", result.getString("sms_type"));
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return sms;
	}

	public static String getOfferCoverLife(String msisdn, String offerName,
			String ben_level) {
		String offer = "";
		logger.info("Executing getOfferCoverLife query");
		try {

			result = MIP_DataBaseConnection.st
					.executeQuery("select offer_cover_charges from offer_cover_details where offer_cover_life="
							+ ben_level
							+ " and offer_id=(select offer_id from offer_details where offer_name='"
							+ offerName + "')");
			while (result.next()) {
				offer = result.getString("offer_cover_charges").trim();
			}

		} catch (Exception e) {
			logger.error("Error while executing the getOfferCoverLife queries",
					e);
		}
		return offer;
	}

	public static Map<String, String> getCoachingProgramInfo(String platform,
			String msisdn) {
		Connection con = null;
		Statement st = null;
		Map<String, String> cp_info = new HashMap<String, String>();
		try {
			logger.info("Connecting to Coaching Program Database");
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
			if (platform.equalsIgnoreCase("windows")) {
				con = DriverManager.getConnection("jdbc:mysql:"
						+ MIP_ReadPropertyFile.getPropertyValue("cpdburl"),
						MIP_ReadPropertyFile.getPropertyValue("cpdbusername"),
						MIP_ReadPropertyFile.getPropertyValue("cpdbpassword"));
			} else if (platform.equalsIgnoreCase("AWS")) {
				con = DriverManager.getConnection("jdbc:mysql:"
						+ MIP_ReadPropertyFile.getPropertyValue("awsdburl"),
						MIP_ReadPropertyFile.getPropertyValue("awsdbusername"),
						MIP_ReadPropertyFile.getPropertyValue("awsdbpassword"));
			}

			if (con != null) {
				logger.info("Connecting Coaching Program to Database is successfull");
			}
		} catch (ClassNotFoundException e) {
			logger.error("Error while connecting to DataBase", e);
		} catch (SQLException e) {
			logger.error("Error while connecting to DataBase", e);
		} catch (InstantiationException e) {
			logger.error("Error while connecting to DataBase", e);
		} catch (IllegalAccessException e) {
			logger.error("Error while connecting to DataBase", e);
		}
		try {
			st = con.createStatement();
		} catch (SQLException e) {
			logger.error("Error while creating statement", e);
		}
		try {
			ResultSet result = st
					.executeQuery("select * from coaching_program_subscription where msisdn="
							+ msisdn + " order by id desc limit 1;");
			while (result.next()) {
				cp_info.put("coaching_id", result.getString("coaching_id"));
				cp_info.put("cust_id", result.getString("cust_id"));
				cp_info.put("sn_id", result.getString("sn_id"));
				cp_info.put("is_active", result.getString("is_active"));
				cp_info.put("is_eligible", result.getString("is_eligible"));

				cp_info.put("created_by", result.getString("created_by"));
				if (result.getString("created_date") == null) {
					cp_info.put("created_date", "");
				} else {
					cp_info.put("created_date",
							result.getString("created_date"));
				}
				if (result.getString("cp_id") == null) {
					cp_info.put("cp_id", "");
				} else {
					cp_info.put("cp_id", result.getString("cp_id"));
				}
				if (result.getString("program_selection_date") == null) {
					cp_info.put("program_selection_date", "");
				} else {
					cp_info.put("program_selection_date",
							result.getString("program_selection_date"));
				}

				cp_info.put("is_warid", result.getString("is_warid"));
				if (result.getString("modified_date") == null) {
					cp_info.put("modified_date", "");
				} else {
					cp_info.put("modified_date",
							result.getString("modified_date"));
				}
				if (result.getString("modified_by") == null) {
					cp_info.put("modified_by", "");
				} else {
					cp_info.put("modified_by", result.getString("modified_by"));
				}
				if (result.getString("service_eligibility_date") == null) {
					cp_info.put("service_eligibility_date", "");
				} else {
					cp_info.put("service_eligibility_date",
							result.getString("service_eligibility_date"));
				}
			}
			ResultSet result1 = st
					.executeQuery("select * from coaching_program_subscription_history where msisdn="
							+ msisdn + " order by cpsh_id desc limit 1;");
			result1.next();
			Assert.assertEquals(cp_info.get("coaching_id"),
					result1.getString("coaching_id"));
			Assert.assertEquals(cp_info.get("cust_id"),
					result1.getString("cust_id"));
			Assert.assertEquals(cp_info.get("sn_id"),
					result1.getString("sn_id"));
			Assert.assertEquals(cp_info.get("is_active"),
					result1.getString("is_active"));
			Assert.assertEquals(cp_info.get("is_eligible"),
					result1.getString("is_eligible"));
			if (!cp_info.get("created_date").equals("")) {
				Assert.assertEquals(
						cp_info.get("created_date").split("\\s")[0], result1
								.getString("created_date").split("\\s")[0]);
			}
			if (cp_info.get("created_date").equals("")) {
				if (result1.getString("created_date") == null)
					Assert.assertEquals(cp_info.get("created_date"), "");
			}
			Assert.assertEquals(cp_info.get("created_by"),
					result1.getString("created_by"));
			if (!cp_info.get("program_selection_date").equals("")) {
				Assert.assertEquals(cp_info.get("program_selection_date")
						.split("\\s")[0],
						result1.getString("program_selection_date")
								.split("\\s")[0]);
			}
			if (cp_info.get("program_selection_date").equals("")) {
				if (result1.getString("program_selection_date") == null)
					Assert.assertEquals(cp_info.get("program_selection_date"),
							"");
			}

		/*	Assert.assertEquals(cp_info.get("is_warid"),
					result1.getString("is_warid"));*/

			if (result1.getString("modified_date") == null) {
				Assert.assertEquals(cp_info.get("modified_date"), "");
			} else {
				Assert.assertEquals(
						cp_info.get("modified_date").split("\\s")[0], result1
								.getString("modified_date").split("\\s")[0]);
			}
			if (result1.getString("modified_by") == null) {
				Assert.assertEquals(cp_info.get("modified_by"), "");
			} else {
				Assert.assertEquals(cp_info.get("modified_by"),
						result1.getString("modified_by"));
			}

			cp_info.put("event_type", result1.getString("event_type"));
			if (result1.getString("event_date") == null) {
				cp_info.put("event_date", "");
			} else
				cp_info.put("event_date", result1.getString("event_date"));

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return cp_info;
	}

	public static void connectToWaridDBAndInserRecord(String platform,
			String name, String msisdn, String prepost_paid, String cni) {
		Connection con = null;
		Statement st = null;
		int status = 30;
		String billcycle = "30";
		String id_type = "N";
		if (cni.equals("")) {
			cni = "NULL";
		}
		try {
			logger.info("Connecting to Warid Database");
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
			if (platform.equalsIgnoreCase("windows")) {
				con = DriverManager.getConnection("jdbc:mysql:"
						+ MIP_ReadPropertyFile.getPropertyValue("wariddburl"),
						MIP_ReadPropertyFile
								.getPropertyValue("wariddbusername"),
						MIP_ReadPropertyFile
								.getPropertyValue("wariddbpassword"));
			} else if (platform.equalsIgnoreCase("AWS")) {
				con = DriverManager.getConnection("jdbc:mysql:"
						+ MIP_ReadPropertyFile.getPropertyValue("awsdburl"),
						MIP_ReadPropertyFile.getPropertyValue("awsdbusername"),
						MIP_ReadPropertyFile.getPropertyValue("awsdbpassword"));
			}

			if (con != null) {
				logger.info("Connecting to Database is successfull");
			}
		} catch (ClassNotFoundException e) {
			logger.error("Error while connecting to DataBase", e);
		} catch (SQLException e) {
			logger.error("Error while connecting to DataBase", e);
		} catch (InstantiationException e) {
			logger.error("Error while connecting to DataBase", e);
		} catch (IllegalAccessException e) {
			logger.error("Error while connecting to DataBase", e);
		}
		try {
			st = con.createStatement();
		} catch (SQLException e) {
			logger.error("Error while creating statement", e);
		}
		try {
			// st.executeUpdate("ALTER table crm_user_info_vw  MODIFY VENDOR_INFO_ID INTEGER NOT NULL AUTO_INCREMENT;");
			st.executeUpdate("insert into crm_user_info_vw(NAME,subno,prepost_paid,CNIC,status,billcycle,id_type)"
					+ " values('"
					+ name
					+ "',"
					+ msisdn.substring(1)
					+ ",'"
					+ prepost_paid
					+ "',"
					+ cni
					+ ","
					+ status
					+ ","
					+ billcycle + ",'" + id_type + "');");
			if (prepost_paid
					.equalsIgnoreCase(MIP_CustomerManagementPage.BIMA_POSTPAID)) {
				st.executeUpdate("insert into vendor_info_vw (Name,SUBNO,STATUS,PREPOST_PAID,BILLGROUP) values('"
						+ name
						+ "',"
						+ msisdn.substring(1)
						+ ","
						+ status
						+ ",'" + prepost_paid + "'," + billcycle + ");");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static String getCoachingProgram(int cp_id) {
		String cp = "";
		logger.info("Executing getCoachingProgram query");
		try {
			result = MIP_DataBaseConnection.st
					.executeQuery("select cp_name from coaching_programme where cp_id="
							+ cp_id + ";");
			result.next();
			cp = result.getString("cp_name");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.error("Error while executing getCoachingProgram query");
		}
		return cp;
	}

	public static String getUser(int user_id) {
		String user = "";
		logger.info("Executing getUser query");
		try {
			result = MIP_DataBaseConnection.st
					.executeQuery("select user_uid from user_details where user_id="
							+ user_id + ";");
			result.next();
			user = result.getString("user_uid");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.error("Error while executing getUser query");
		}
		return user;

	}

	public static void main(String[] args) {
		MIP_ReadPropertyFile.loadProperty("config");
		MIP_DataBaseConnection.connectToDatabase("windows");
		System.out.println(getRegistrationSMS("windows", "03091207919",
				"Bima Accident Protection and Hospitalization"));
		// getUser(Integer.parseInt("5"));

		/*
		 * String str = getRegisteredProduct("03026566500").toString();
		 * System.out.println(str);
		 * System.out.println("Warid\\Jazz Bima Accident Protection");
		 * System.out
		 * .println(str.contains("Warid\\Jazz Bima Accident Protection"));
		 */
		/*
		 * connectToWaridDBAndInserRecord("windows", "test", "03026566501",
		 * "POST", "2132779103324"); // System.out.println(str); String str =
		 * getRegisteredProduct("03026566500").toString();
		 * System.out.println(str);
		 */
	}
}
