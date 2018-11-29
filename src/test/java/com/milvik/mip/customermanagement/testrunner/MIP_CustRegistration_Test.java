package com.milvik.mip.customermanagement.testrunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.milvik.mip.constants.MIP_Menu_Constants;
import com.milvik.mip.dataprovider.MIP_RegisterCustomer_TestData;
//import com.milvik.mip.dataprovider.MIP_ResetPassword_TestData;
import com.milvik.mip.dbqueries.MIP_RegisterCustomer_Queries;
import com.milvik.mip.pageobjects.MIP_HomePage;
import com.milvik.mip.pageobjects.MIP_LoginPage;
import com.milvik.mip.pageobjects.MIP_RegisterCustomerPage;
import com.milvik.mip.pageutil.MIP_CustomerManagementPage;
import com.milvik.mip.testconfig.MIP_Test_Configuration;
import com.milvik.mip.utility.MIP_BrowserFactory;
import com.milvik.mip.utility.MIP_DataBaseConnection;
import com.milvik.mip.utility.MIP_DateFunctionality;
import com.milvik.mip.utility.MIP_LaunchApplication;
import com.milvik.mip.utility.MIP_Logging;
import com.milvik.mip.utility.MIP_ReadPropertyFile;
import com.milvik.mip.utility.MIP_ScreenShots;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

public class MIP_CustRegistration_Test {
	ExtentReports report;
	ExtentTest logger;
	static Logger log;
	MIP_LoginPage loginpage;
	MIP_HomePage homepage;
	MIP_RegisterCustomerPage regpage = null;
	String username = "";
	String testcaseName = "";
	String platform_ = "";

	@BeforeTest
	@Parameters({ "browser", "flag", "platform" })
	public void test_setup(@Optional("firefox") String browser,
			@Optional("0") String flag, @Optional("windows") String platform) {
		log = MIP_Logging.logDetails("MIP_CustRegistration_Test");
		report = new ExtentReports("Test_Reports/RegisterCustomer_Test.html");
		platform_ = platform;
		if (flag.equals("0")) {
			try {
				Runtime.getRuntime().exec("taskkill /F /IM geckodriver.exe");
			} catch (Exception e) {
				log.info("No exe found");
			}
			MIP_Test_Configuration.driver = MIP_BrowserFactory.openBrowser(
					MIP_Test_Configuration.driver, browser, platform);
			MIP_ReadPropertyFile.loadProperty("config");
			MIP_DataBaseConnection.connectToDatabase(platform);
			MIP_LaunchApplication.openApplication(
					MIP_Test_Configuration.driver, platform);
			loginpage = PageFactory.initElements(MIP_Test_Configuration.driver,
					MIP_LoginPage.class);
			username = MIP_ReadPropertyFile.getPropertyValue("username");
			homepage = loginpage.login(username,
					MIP_ReadPropertyFile.getPropertyValue("password"));
			WebDriverWait w = new WebDriverWait(MIP_Test_Configuration.driver,
					600);
			w.until(ExpectedConditions.visibilityOfElementLocated((By
					.linkText(MIP_Menu_Constants.CUSTOMER))));
			homepage.clickOnMenu(MIP_Menu_Constants.CUSTOMER);
			homepage.clickOnMenu(MIP_Menu_Constants.REGISTER_CUSTOMER);
		} else {
			homepage = new MIP_HomePage(MIP_Test_Configuration.driver);
			WebDriverWait w = new WebDriverWait(MIP_Test_Configuration.driver,
					600);
			w.until(ExpectedConditions.visibilityOfElementLocated((By
					.linkText(MIP_Menu_Constants.CUSTOMER))));
			homepage.clickOnMenu(MIP_Menu_Constants.CUSTOMER);
			homepage.clickOnMenu(MIP_Menu_Constants.REGISTER_CUSTOMER);

		}
	}

	@Test(priority = 1, enabled = true, testName = "RegisterCustomer", dataProvider = "RegisterCustomer", dataProviderClass = MIP_RegisterCustomer_TestData.class)
	public void registrationTest(String testcase, String mno, String msisdn,
			String prepostStatus, String prd_to_register,
			String accident_level, String hospital_level, String cust_fname,
			String cust_sname, String age, String cust_cni, String dob,
			String gender, String accident_relationship,
			String accident_ben_fname, String accident_ben_sname,
			String accident_ben_age, String accident_ben_msisdn,
			String accident_ben_gender, String hospitalization_relationship,
			String hospitalization_ben_fname, String hospitalization_ben_sname,
			String hospitalization_ben_age, String hospitalization_ben_msisdn,
			String hospitalization_ben_gender, String success_message)
			throws Throwable {
		testcaseName = testcase + "--" + msisdn;
		try {
			username = MIP_ReadPropertyFile.getPropertyValue("username");
			logger = report.startTest(testcase + "--" + msisdn);
			regpage = PageFactory.initElements(MIP_Test_Configuration.driver,
					MIP_RegisterCustomerPage.class);
			regpage.waitForElementToVisible(By
					.xpath("//h3[contains(text(),'Register Customer')]"));
			regpage.waitForElementToVisible(By
					.xpath("//div[contains(text(),'All fields marked')]"));
			if (mno.equalsIgnoreCase(MIP_CustomerManagementPage.BIMA_WARID))
				MIP_RegisterCustomer_Queries.connectToWaridDBAndInserRecord(
						platform_, cust_fname, msisdn, prepostStatus, cust_cni);
			Thread.sleep(1000);
			regpage.enterMSISDN(msisdn);
			regpage.clickOnSeachIcon();
			regpage.waitForElementToVisible(By.id("saveBtn"));
			if (mno.equalsIgnoreCase(MIP_CustomerManagementPage.BIMA_WARID)
					&& prepostStatus
							.equalsIgnoreCase(MIP_CustomerManagementPage.BIMA_POSTPAID)) {
				boolean status = regpage.checkHPForPostPaid();
				if (status == true) {
					log.info("Hospital product for PostPaid Warid is not showing");
				} else {
					log.info("Hospital product for PostPaid Warid is  showing");
					Assert.assertTrue(status);
				}

			}
			regpage.enterCustomerInfo(mno, cust_fname, cust_sname, age,
					cust_cni, dob, gender);
			logger.log(LogStatus.INFO, "Entered Customer Information");
			Thread.sleep(1000);
			regpage.selectAvailableInformation(prd_to_register);
			logger.log(LogStatus.INFO, "Selected Available product--"
					+ prd_to_register);
			if (prd_to_register
					.contains(MIP_CustomerManagementPage.BIMA_ACCIDENT_PROTECTION)) {
				regpage.selectBimaAccidentProtecionBenefitLevel(accident_level);
				logger.log(LogStatus.INFO, "Selected "
						+ MIP_CustomerManagementPage.BIMA_ACCIDENT_PROTECTION
						+ " Cover Level " + accident_level);
				regpage.enterBeneficiaryInfoForAccidentProtection(
						accident_relationship, accident_ben_fname,
						accident_ben_sname, accident_ben_age,
						accident_ben_msisdn, accident_ben_gender);
				logger.log(LogStatus.INFO,
						"Entered Accident ProtectionBeneficiary  Information");
			}
			if (prd_to_register
					.contains(MIP_CustomerManagementPage.BIMA_HOSPITALIZATION)) {
				regpage.selectBimaHospitalizationBenefitLevel(hospital_level);
				logger.log(LogStatus.INFO, "Selected "
						+ MIP_CustomerManagementPage.BIMA_HOSPITALIZATION
						+ " Cover Level " + hospital_level);
				regpage.enterBeneficiaryInfoForHospitalization(
						hospitalization_relationship,
						hospitalization_ben_fname, hospitalization_ben_sname,
						hospitalization_ben_age, hospitalization_ben_msisdn,
						hospitalization_ben_gender);
				logger.log(LogStatus.INFO,
						"Entered Hospitalization Beneficiary  Information");
			}
			regpage.clickOnSave().confirmPopUp("yes");
			logger.log(LogStatus.INFO, "Clicked On Register Button");
			Assert.assertTrue(regpage.getSuccessMsg().replaceAll("\\s", "")
					.equalsIgnoreCase(success_message.replaceAll("\\s", "")));
			logger.log(LogStatus.INFO, "Customer Registered Successfully");
			logger.log(LogStatus.INFO,
					"Validating the Registration Aganist DataBase");
			Map<String, String> db_cust_details = MIP_RegisterCustomer_Queries
					.getCustomerDetails(msisdn, prd_to_register);
			log.info("Expected and found cust fname:" + "DB Cust Fname="
					+ db_cust_details.get("cust_fname") + " and "
					+ "Input Cust Fname=" + cust_fname);
			Assert.assertEquals(db_cust_details.get("cust_fname").trim(),
					cust_fname.trim());
			log.info("Expected and found cust_sname:" + "DB cust_sname="
					+ db_cust_details.get("cust_sname") + " and "
					+ "Input cust_sname=" + cust_sname);
			Assert.assertEquals(db_cust_details.get("cust_sname").trim(),
					cust_sname.trim());
			if (!age.equals("")) {
				log.info("Expected and found  cust_age:" + "DB cust_age="
						+ db_cust_details.get("cust_age") + " and "
						+ "Input cust_age=" + age);
				Assert.assertEquals(db_cust_details.get("cust_age").trim(),
						age.trim());
			}
			if (!dob.equals("")) {
				log.info("Expected and found  cust_dob:" + "DB cust_dob="
						+ db_cust_details.get("cust_dob") + " and "
						+ "Input cust_dob=" + dob);
				Assert.assertEquals(db_cust_details.get("cust_dob").trim(),
						dob.trim());

			}
			log.info("Expected and found cust cust_gender:" + "DB cust_gender="
					+ db_cust_details.get("cust_gender") + " and "
					+ "Input cust_gender=" + gender);
			Assert.assertEquals(db_cust_details.get("cust_gender").trim(),
					gender.trim());
			if (prd_to_register
					.contains(MIP_CustomerManagementPage.BIMA_ACCIDENT_PROTECTION)) {
				log.info("Expected and found ben_fname:" + "DB ben_fname="
						+ db_cust_details.get("accident_ben_fname") + " and "
						+ "ben_fname=" + accident_ben_fname);
				Assert.assertEquals(db_cust_details.get("accident_ben_fname")
						.trim(), accident_ben_fname.trim());
				log.info("Expected and found ben_sname:" + "DB ben_sname="
						+ db_cust_details.get("accident_ben_sname") + " and "
						+ "Input ben_sname=" + accident_ben_sname);
				Assert.assertEquals(db_cust_details.get("accident_ben_sname")
						.trim(), accident_ben_sname.trim());
				log.info("Expected and found ben_age:" + "DB ben_age="
						+ db_cust_details.get("accident_ben_age") + " and "
						+ "Input ben_age=" + accident_ben_age);
				Assert.assertEquals(db_cust_details.get("accident_ben_age")
						.trim(), accident_ben_age.trim());
				log.info("Expected and found cust_relationship:"
						+ "DB cust_relationship="
						+ db_cust_details.get("accident_cust_relationship")
						+ " and " + "Input cust_relationship="
						+ accident_relationship);
				Assert.assertEquals(
						db_cust_details.get("accident_cust_relationship")
								.trim(), accident_relationship.trim());
				log.info("Expected and found ben_gender:" + "DB ben_gender ="
						+ db_cust_details.get("accident_ben_gender") + " and "
						+ "Input ben_gender=" + accident_ben_gender);
				Assert.assertEquals(db_cust_details.get("accident_ben_gender")
						.trim(), accident_ben_gender.trim());

				log.info("Expected and found ben_msisdn:" + "DB ben_msisdn ="
						+ db_cust_details.get("accident_ben_msisdn") + " and "
						+ "Input ben_msisdn=" + accident_ben_msisdn);
				Assert.assertEquals(db_cust_details.get("accident_ben_msisdn")
						.trim(), accident_ben_msisdn.trim());

			}
			if (prd_to_register
					.contains(MIP_CustomerManagementPage.BIMA_HOSPITALIZATION)) {
				log.info("Expected and found ben_fname:" + "DB ben_fname="
						+ db_cust_details.get("hospitalization_ben_fname")
						+ " and " + "ben_fname=" + hospitalization_ben_fname);
				Assert.assertEquals(
						db_cust_details.get("hospitalization_ben_fname").trim(),
						hospitalization_ben_fname.trim());
				log.info("Expected and found ben_sname:" + "DB ben_sname="
						+ db_cust_details.get("hospitalization_ben_sname")
						+ " and " + "Input ben_sname="
						+ hospitalization_ben_sname);
				Assert.assertEquals(
						db_cust_details.get("hospitalization_ben_sname").trim(),
						hospitalization_ben_sname.trim());
				log.info("Expected and found ben_age:" + "DB ben_age="
						+ db_cust_details.get("hospitalization_ben_age")
						+ " and " + "Input ben_age=" + hospitalization_ben_age);
				Assert.assertEquals(
						db_cust_details.get("hospitalization_ben_age").trim(),
						hospitalization_ben_age.trim());
				log.info("Expected and found cust_relationship:"
						+ "DB cust_relationship="
						+ db_cust_details
								.get("hospitalization_cust_relationship")
						+ " and " + "Input cust_relationship="
						+ hospitalization_relationship);
				Assert.assertEquals(
						db_cust_details
								.get("hospitalization_cust_relationship")
								.trim(), hospitalization_relationship.trim());
				log.info("Expected and found ben_gender:" + "DB ben_gender ="
						+ db_cust_details.get("hospitalization_ben_gender")
						+ " and " + "Input ben_gender="
						+ hospitalization_ben_gender);
				Assert.assertEquals(
						db_cust_details.get("hospitalization_ben_gender")
								.trim(), hospitalization_ben_gender.trim());

				log.info("Expected and found ben_msisdn:" + "DB ben_msisdn ="
						+ db_cust_details.get("hospitalization_ben_msisdn")
						+ " and " + "Input ben_msisdn="
						+ hospitalization_ben_msisdn);
				Assert.assertEquals(
						db_cust_details.get("hospitalization_ben_msisdn")
								.trim(), hospitalization_ben_msisdn.trim());

			}

			if (!cust_cni.equals("")) {
				log.info("Expected and found NIC:" + "DB NIC number="
						+ db_cust_details.get("cust_cnic") + " and "
						+ "Input cust_NIC=" + cust_cni);
				Assert.assertEquals(db_cust_details.get("cust_cnic").trim(),
						cust_cni.trim());
				if (mno.equalsIgnoreCase(MIP_CustomerManagementPage.BIMA_WARID)) {
					log.info("Expected and found Warid NIC:"
							+ "DB Warid NIC number="
							+ db_cust_details.get("w_cnic") + " and "
							+ "Input Warid_NIC=" + cust_cni);
					Assert.assertEquals(db_cust_details.get("w_cnic").trim(),
							cust_cni.trim());
				}

			}
			log.info("Expected and found Created By:" + "DB Created By="
					+ db_cust_details.get("user") + " and "
					+ "Input Created By=" + username);
			Assert.assertEquals(db_cust_details.get("user").trim(), username);
			String date = MIP_DateFunctionality.getCurrentDate("YYYY-MM-dd");
			log.info("Expected and found Registered Date:"
					+ "DB Modified Date="
					+ db_cust_details.get("modified_date") + " and "
					+ "Registered Date=" + date);
			Assert.assertTrue(db_cust_details.get("modified_date").contains(
					date));
			String prepost_status = "";
			if (prepostStatus
					.equalsIgnoreCase(MIP_CustomerManagementPage.BIMA_PREPAID)) {
				prepost_status = MIP_CustomerManagementPage.BIMA_PREPAID_STATUS;
			} else if (prepostStatus
					.equalsIgnoreCase(MIP_CustomerManagementPage.BIMA_POSTPAID)) {
				prepost_status = MIP_CustomerManagementPage.BIMA_POSTPAID_STATUS;
			}
			log.info("Expected and found Prepost Status:"
					+ "DB Prepost Status =" + db_cust_details.get("is_prepaid")
					+ " and " + "Input Prepost Status=" + prepost_status);
			Assert.assertEquals(db_cust_details.get("is_prepaid"),
					prepost_status);
			String is_warid_status = "";
			if (mno.equalsIgnoreCase(MIP_CustomerManagementPage.BIMA_WARID)) {
				is_warid_status = MIP_CustomerManagementPage.BIMA_IS_WARID;
			} else if (mno
					.equalsIgnoreCase(MIP_CustomerManagementPage.BIMA_JAZZ)) {
				is_warid_status = MIP_CustomerManagementPage.BIMA_IS_JAZZ;
			}
			log.info("Expected and found is_Warid Status:" + "DB is_Warid ="
					+ db_cust_details.get("is_warid") + " and "
					+ "Input is_warid status=" + is_warid_status);
			Assert.assertEquals(db_cust_details.get("is_warid"),
					is_warid_status);
			regpage.waitForElementToVisible(By.id("search-icon"));
			Thread.sleep(1000);
			regpage.enterMSISDN(msisdn);
			regpage.clickOnSeachIcon();
			List<String> db_reg_prd = MIP_RegisterCustomer_Queries
					.getRegisteredProduct(msisdn);
			List<String> dashboard_reg_prd = regpage.getRegisteredOffer();
			if (mno.equalsIgnoreCase(MIP_CustomerManagementPage.BIMA_WARID)
					&& prepostStatus
							.equalsIgnoreCase(MIP_CustomerManagementPage.BIMA_POSTPAID)) {
				boolean status = regpage.checkHPForPostPaid();
				if (status == true) {
					log.info("Hospital product for PostPaid Warid is not showing");
				} else {
					log.info("Hospital product for PostPaid Warid is  showing");
					Assert.assertTrue(status);
				}

			}
			Assert.assertEquals(db_reg_prd, dashboard_reg_prd);
			Map<String, String> dashboard_cust_details = regpage
					.getCustomerInfo();
			log.info("Expected and found cust_fname:" + "DB cust_fname="
					+ db_cust_details.get("cust_fname") + " and "
					+ "Dashboard Customer fname="
					+ dashboard_cust_details.get("cust_fname").trim());
			Assert.assertEquals(db_cust_details.get("cust_fname").trim(),
					dashboard_cust_details.get("cust_fname").trim());
			log.info("Expected and found cust_sname:" + "DB cust_sname="
					+ db_cust_details.get("cust_sname") + " and "
					+ "Dashboard cust_sname="
					+ dashboard_cust_details.get("cust_sname").trim());
			Assert.assertEquals(db_cust_details.get("cust_sname").trim(),
					dashboard_cust_details.get("cust_sname").trim());
			if (!age.equals("")) {
				log.info("Expected and found cust_age:" + "DB cust_age="
						+ db_cust_details.get("cust_age") + " and "
						+ "Dashboard cust_age="
						+ dashboard_cust_details.get("cust_age").trim());
				Assert.assertEquals(db_cust_details.get("cust_age").trim(),
						dashboard_cust_details.get("cust_age").trim());
			}
			if (!dob.equals("")) {
				log.info("Expected and found cust_dob:" + "DB cust_age="
						+ db_cust_details.get("cust_dob") + " and "
						+ "Dashboard cust_age="
						+ dashboard_cust_details.get("cust_dob").trim());
				Assert.assertEquals(db_cust_details.get("cust_dob").trim(),
						dashboard_cust_details.get("cust_dob").trim());
			}
			log.info("Expected and found cust_gender:" + "DB cust_gender="
					+ db_cust_details.get("cust_gender") + " and "
					+ "Dashboard cust_gender="
					+ dashboard_cust_details.get("cust_gender").trim());
			Assert.assertEquals(db_cust_details.get("cust_gender").trim(),
					dashboard_cust_details.get("cust_gender").trim());
			if (!cust_cni.equals("")) {
				log.info("Expected and found NIC:" + "DB NIC number="
						+ db_cust_details.get("cust_cnic") + " and "
						+ "Dashboard cust_NIC="
						+ dashboard_cust_details.get("cust_nic"));
				Assert.assertEquals(db_cust_details.get("cust_cnic").trim(),
						dashboard_cust_details.get("cust_nic").trim());

			}
			if (prd_to_register
					.contains(MIP_CustomerManagementPage.BIMA_ACCIDENT_PROTECTION)) {
				Map<String, String> dashboard_ben_details = regpage
						.getAccidentBeneficiaryInfo();
				log.info("Expected and found ben_fname:" + "DB ben_fname="
						+ db_cust_details.get("accident_ben_fname") + " and "
						+ "Dashboard ben_fname="
						+ dashboard_ben_details.get("ben_fname").trim());
				Assert.assertEquals(db_cust_details.get("accident_ben_fname")
						.trim(), dashboard_ben_details.get("ben_fname").trim());
				log.info("Expected and found ben_sname:" + "DB ben_sname="
						+ db_cust_details.get("accident_ben_sname") + " and "
						+ "Dashboard ben_sname="
						+ dashboard_ben_details.get("ben_sname").trim());
				Assert.assertEquals(db_cust_details.get("accident_ben_sname")
						.trim(), dashboard_ben_details.get("ben_sname").trim());
				log.info("Expected and found ben_age:" + "DB ben_age="
						+ db_cust_details.get("accident_ben_age") + " and "
						+ "Dashboard ben_age="
						+ dashboard_ben_details.get("ben_age").trim());
				Assert.assertEquals(db_cust_details.get("accident_ben_age")
						.trim(), dashboard_ben_details.get("ben_age").trim());
				log.info("Expected and found cust_relationship:"
						+ "DB cust_relationship="
						+ db_cust_details.get("accident_cust_relationship")
						+ " and " + "Dashboard cust_relationship="
						+ dashboard_ben_details.get("ben_relationship").trim());
				Assert.assertEquals(
						db_cust_details.get("accident_cust_relationship")
								.trim(),
						dashboard_ben_details.get("ben_relationship").trim());
				log.info("Expected and found ben_msisdn:" + "DB ben_msisdn="
						+ db_cust_details.get("accident_ben_msisdn") + " and "
						+ "Dashboard ben_msisdn="
						+ dashboard_ben_details.get("ben_msisdn").trim());
				Assert.assertEquals(db_cust_details.get("accident_ben_msisdn")
						.trim(), dashboard_ben_details.get("ben_msisdn").trim());
				log.info("Expected and found ben_gender:" + "DB ben_gender="
						+ db_cust_details.get("accident_ben_gender") + " and "
						+ "Dashboard ben_gender="
						+ dashboard_ben_details.get("ben_gender").trim());
				Assert.assertEquals(db_cust_details.get("accident_ben_gender")
						.trim(), dashboard_ben_details.get("ben_gender").trim());

			}
			if (prd_to_register
					.contains(MIP_CustomerManagementPage.BIMA_HOSPITALIZATION)) {
				Map<String, String> dashboard_ben_details = regpage
						.getHospitalizationBeneficiaryInfo();
				log.info("Expected and found ben_fname:" + "DB ben_fname="
						+ db_cust_details.get("hospitalization_ben_fname")
						+ " and " + "Dashboard ben_fname="
						+ dashboard_ben_details.get("ben_fname").trim());
				Assert.assertEquals(
						db_cust_details.get("hospitalization_ben_fname").trim(),
						dashboard_ben_details.get("ben_fname").trim());
				log.info("Expected and found ben_sname:" + "DB ben_sname="
						+ db_cust_details.get("hospitalization_ben_sname")
						+ " and " + "Dashboard ben_sname="
						+ dashboard_ben_details.get("ben_sname").trim());
				Assert.assertEquals(
						db_cust_details.get("hospitalization_ben_sname").trim(),
						dashboard_ben_details.get("ben_sname").trim());
				log.info("Expected and found ben_age:" + "DB ben_age="
						+ db_cust_details.get("hospitalization_ben_age")
						+ " and " + "Dashboard ben_age="
						+ dashboard_ben_details.get("ben_age").trim());
				Assert.assertEquals(
						db_cust_details.get("hospitalization_ben_age").trim(),
						dashboard_ben_details.get("ben_age").trim());
				log.info("Expected and found cust_relationship:"
						+ "DB cust_relationship="
						+ db_cust_details
								.get("hospitalization_cust_relationship")
						+ " and " + "Dashboard cust_relationship="
						+ dashboard_ben_details.get("ben_relationship").trim());
				Assert.assertEquals(
						db_cust_details
								.get("hospitalization_cust_relationship")
								.trim(),
						dashboard_ben_details.get("ben_relationship").trim());
				log.info("Expected and found ben_msisdn:" + "DB ben_msisdn="
						+ db_cust_details.get("hospitalization_ben_msisdn")
						+ " and " + "Dashboard ben_msisdn="
						+ dashboard_ben_details.get("ben_msisdn").trim());
				Assert.assertEquals(
						db_cust_details.get("hospitalization_ben_msisdn")
								.trim(), dashboard_ben_details
								.get("ben_msisdn").trim());
				log.info("Expected and found ben_gender:" + "DB ben_gender="
						+ db_cust_details.get("hospitalization_ben_gender")
						+ " and " + "Dashboard ben_gender="
						+ dashboard_ben_details.get("ben_gender").trim());
				Assert.assertEquals(
						db_cust_details.get("hospitalization_ben_gender")
								.trim(), dashboard_ben_details
								.get("ben_gender").trim());

			}

			Map<String, String> dashboard_benefit_level = regpage
					.getBenefitLevel();
			Map<String, String> db_benefit_level = MIP_RegisterCustomer_Queries
					.getOfferCover(msisdn, prd_to_register);

			Map<String, String> coaching_pgm_info = regpage
					.getCoachingProgramInfo();
			Map<String, String> db_cp_info = MIP_RegisterCustomer_Queries
					.getCoachingProgramInfo(platform_, msisdn);
			String db_cp = MIP_RegisterCustomer_Queries
					.getCoachingProgram(Integer.parseInt((db_cp_info
							.get("coaching_id"))));
			log.info("Expected and found Coaching program:"
					+ "DB Coaching program=" + db_cp + " and "
					+ "Dashboard Coaching program="
					+ coaching_pgm_info.get("coaching_program").trim());
			Assert.assertTrue(coaching_pgm_info.get("coaching_program")
					.equalsIgnoreCase(db_cp));
			log.info("Expected and found Coaching program created date:"
					+ "DB Coaching program created date="
					+ db_cp_info.get("created_date") + " and "
					+ "Dashboard created date="
					+ coaching_pgm_info.get("date_program_Section").trim());
			String[] dashboard_date = coaching_pgm_info.get(
					"date_program_Section").split("\\-");
			String cp_dashboard_date = dashboard_date[2] + "-"
					+ dashboard_date[1] + "-" + dashboard_date[0];
			Assert.assertTrue(db_cp_info.get("created_date").contains(
					cp_dashboard_date));

			if (db_cp_info.get("is_active").equals("0")) {
				log.info("Expected and found Coaching program status:"
						+ "DB Coaching program status="
						+ MIP_RegisterCustomer_TestData.DEFAULT_COACHING_PROGRAM_STATUS
						+ " and "
						+ "Dashboard coaching program status="
						+ coaching_pgm_info.get("coaching_program_status")
								.trim());
				Assert.assertTrue(coaching_pgm_info
						.get("coaching_program_status")
						.equalsIgnoreCase(
								MIP_RegisterCustomer_TestData.DEFAULT_COACHING_PROGRAM_STATUS));
			} else {
				log.info("Coaching Programm Status is not Inactive after Registration");
				Assert.assertTrue(false);
			}
			log.info("Expected and found Coaching program created BY:"
					+ "DB Coaching program created by="
					+ MIP_RegisterCustomer_Queries.getUser(Integer
							.parseInt(db_cp_info.get("created_by"))) + " and "
					+ "Input user=" + username.trim());
			Assert.assertEquals(username, MIP_RegisterCustomer_Queries
					.getUser(Integer.parseInt(db_cp_info.get("created_by"))));
			log.info("Expected and found Coaching program is_eligible:"
					+ "DB Coaching program is_eligible="
					+ db_cp_info.get("is_eligible")
					+ " and expected is_eligible"
					+ MIP_RegisterCustomer_TestData.CP_IS_ELIGIBLE);
			Assert.assertTrue(db_cp_info.get("is_eligible").equals(
					MIP_RegisterCustomer_TestData.CP_IS_ELIGIBLE));
			log.info("Expected and found Coaching program selection date:"
					+ "DB Coaching program selection date="
					+ db_cp_info.get("program_selection_date")
					+ " and expected program selection date"
					+ MIP_DateFunctionality.getCurrentDate("YYYY-MM-dd"));
			Assert.assertTrue(db_cp_info.get("program_selection_date")
					.contains(
							MIP_DateFunctionality.getCurrentDate("YYYY-MM-dd")));
			if (mno.equalsIgnoreCase(MIP_CustomerManagementPage.BIMA_WARID)) {
				is_warid_status = MIP_CustomerManagementPage.BIMA_IS_WARID;
			} else if (mno
					.equalsIgnoreCase(MIP_CustomerManagementPage.BIMA_JAZZ)) {
				is_warid_status = MIP_CustomerManagementPage.BIMA_IS_JAZZ;
			}
			log.info("Expected and found is_Warid Status in CP Table:"
					+ "DB is_Warid =" + db_cp_info.get("is_warid") + " and "
					+ "Input is_warid status=" + is_warid_status);
			Assert.assertEquals(db_cp_info.get("is_warid"), is_warid_status);
			log.info("Expected and found Coaching program modified date:"
					+ "DB Coaching program modified date="
					+ db_cp_info.get("modified_date")
					+ " and expected CP program modified date" + "");
			Assert.assertEquals(db_cp_info.get("modified_date"), "");
			log.info("Expected and found Coaching program modified by:"
					+ "DB Coaching program modified by="
					+ db_cp_info.get("modified_by")
					+ " and expected CP program modified by" + "0");
			Assert.assertEquals(db_cp_info.get("modified_by"), "0");
			Assert.assertEquals(db_cp_info.get("service_eligibility_date"), "");
			log.info("Expected and found Coaching program event Type:"
					+ "DB Coaching program  event Type="
					+ db_cp_info.get("event_type")
					+ " and expected CP event type"
					+ MIP_RegisterCustomer_TestData.CP_EVENT_TYPE);
			Assert.assertEquals(db_cp_info.get("event_type"),
					MIP_RegisterCustomer_TestData.CP_EVENT_TYPE);
			log.info("Expected and found Coaching program event date:"
					+ "DB Coaching program  event date="
					+ db_cp_info.get("event_date")
					+ " and expected CP event date"
					+ MIP_DateFunctionality.getCurrentDate("YYYY-MM-dd"));
			Assert.assertTrue(db_cp_info.get("event_date").contains(
					MIP_DateFunctionality.getCurrentDate("YYYY-MM-dd")));

			if (prd_to_register
					.contains(MIP_CustomerManagementPage.BIMA_ACCIDENT_PROTECTION)) {

				log.info("Expected and found Accident benefit_level:"
						+ "DB benefit_level="
						+ db_benefit_level.get("accident_cover")
						+ " and "
						+ "Dashboard benefit_level="
						+ dashboard_benefit_level.get(
								"bima_accident_benefit_level").trim());
				Assert.assertTrue(dashboard_benefit_level
						.get("bima_accident_benefit_level")
						.trim()
						.contains(db_benefit_level.get("accident_cover").trim()));

			}

			if (prd_to_register
					.contains(MIP_CustomerManagementPage.BIMA_HOSPITALIZATION)) {

				log.info("Expected and found Hospitalization benefit_level:"
						+ "DB benefit_level="
						+ db_benefit_level.get("hospitalization_cover")
						+ " and "
						+ "Dashboard benefit_level="
						+ dashboard_benefit_level.get(
								"bima_hospital_benefit_level").trim());
				Assert.assertTrue(dashboard_benefit_level
						.get("bima_hospital_benefit_level")
						.trim()
						.contains(
								db_benefit_level.get("hospitalization_cover")
										.trim()));
			}
			Map<String, String> sms = MIP_RegisterCustomer_Queries
					.getRegistrationSMS(platform_, msisdn, prd_to_register);
			if (prd_to_register
					.contains(MIP_CustomerManagementPage.BIMA_ACCIDENT_PROTECTION)) {
				logger.log(LogStatus.INFO, "Validating SMS generated for "
						+ MIP_CustomerManagementPage.BIMA_ACCIDENT_PROTECTION);
				Assert.assertTrue(sms
						.get("ap_sms")
						.equalsIgnoreCase(
								MIP_RegisterCustomer_TestData.BIMA_ACCIDENT_REGISTRATION_SMS));
				Assert.assertTrue(sms.get("ap_sms_type").equalsIgnoreCase(
						MIP_RegisterCustomer_TestData.REGISTRATION_SMS_TYPE));
			}
			if (prd_to_register
					.contains(MIP_CustomerManagementPage.BIMA_HOSPITALIZATION)) {
				logger.log(LogStatus.INFO, "Validating SMS generated for "
						+ MIP_CustomerManagementPage.BIMA_HOSPITALIZATION);
				Assert.assertTrue(sms
						.get("hp_sms")
						.equalsIgnoreCase(
								MIP_RegisterCustomer_TestData.BIMA_HEALTH_REGISTRATION_SMS));
				Assert.assertTrue(sms.get("hp_sms_type").equalsIgnoreCase(
						MIP_RegisterCustomer_TestData.REGISTRATION_SMS_TYPE));
			}
			logger.log(LogStatus.INFO,
					"Validated Customer Registration Successfully.");
			homepage.clickOnMenu(MIP_Menu_Constants.REGISTER_CUSTOMER);
		} catch (Throwable t) {
			log.info("RegisterCustomer---" + testcase + " Test Failed");
			log.info("Error occured in the test case", t);
			throw t;
		}
	}

	@Test(priority = 1, enabled = true, testName = "RegisterCustomerAssignOffer", dataProvider = "assignOffer", dataProviderClass = MIP_RegisterCustomer_TestData.class)
	public void register_customer_AssignOfferTest(String testcase,
			String msisdn, String prd_to_register, String offer_level,
			String relationship, String ben_fname, String ben_sname,
			String ben_age, String ben_msisdn, String ben_gender,
			String success_message) throws Throwable {
		testcaseName = testcase + "--" + msisdn;
		try {
			Map<String, String> dashboard_ap_details_before_asssign_offer = new HashMap<String, String>();
			Map<String, String> dashboard_hp_details_before_asssign_offer = new HashMap<String, String>();
			username = MIP_ReadPropertyFile.getPropertyValue("username");
			logger = report.startTest(testcase + "--" + msisdn);
			regpage = PageFactory.initElements(MIP_Test_Configuration.driver,
					MIP_RegisterCustomerPage.class);
			regpage.waitForElementToVisible(By
					.xpath("//h3[contains(text(),'Register Customer')]"));
			regpage.waitForElementToVisible(By
					.xpath("//div[contains(text(),'All fields marked')]"));

			Thread.sleep(1000);
			regpage.enterMSISDN(msisdn);
			regpage.clickOnSeachIcon();
			regpage.waitForElementToVisible(By.id("saveBtn"));
			Thread.sleep(1000);
			regpage.selectAvailableInformation(prd_to_register);
			logger.log(LogStatus.INFO, "Selected Available product--"
					+ prd_to_register);
			Map<String, String> dashboard_cust_details_before_asssign_offer = regpage
					.getCustomerInfo();
			if (prd_to_register
					.contains(MIP_CustomerManagementPage.BIMA_ACCIDENT_PROTECTION)) {
				dashboard_hp_details_before_asssign_offer = regpage
						.getHospitalizationBeneficiaryInfo();
				regpage.selectBimaAccidentProtecionBenefitLevel(offer_level);
				logger.log(LogStatus.INFO, "Selected "
						+ MIP_CustomerManagementPage.BIMA_ACCIDENT_PROTECTION
						+ " Cover Level " + offer_level);
				regpage.enterBeneficiaryInfoForAccidentProtection(relationship,
						ben_fname, ben_sname, ben_age, ben_msisdn, ben_gender);
				logger.log(LogStatus.INFO,
						"Entered Accident ProtectionBeneficiary  Information");
			}
			if (prd_to_register
					.contains(MIP_CustomerManagementPage.BIMA_HOSPITALIZATION)) {
				dashboard_ap_details_before_asssign_offer = regpage
						.getAccidentBeneficiaryInfo();
				regpage.selectBimaHospitalizationBenefitLevel(offer_level);
				logger.log(LogStatus.INFO, "Selected "
						+ MIP_CustomerManagementPage.BIMA_HOSPITALIZATION
						+ " Cover Level " + offer_level);
				regpage.enterBeneficiaryInfoForHospitalization(relationship,
						ben_fname, ben_sname, ben_age, ben_msisdn, ben_gender);
				logger.log(LogStatus.INFO,
						"Entered Hospitalization Beneficiary  Information");
			}
			//int cp_flag = regpage.getoachingProgramStatus();
			regpage.clickOnSave().confirmPopUp("yes");
			logger.log(LogStatus.INFO, "Clicked On Register Button");

			Assert.assertTrue(regpage.getSuccessMsg().replaceAll("\\s", "")
					.equalsIgnoreCase(success_message.replaceAll("\\s", "")));

			logger.log(LogStatus.INFO, "Customer Registered Successfully");
			logger.log(LogStatus.INFO,
					"Validating the Registration Aganist DataBase");
			Thread.sleep(3000);
			Map<String, String> db_cust_details = MIP_RegisterCustomer_Queries
					.getCustomerDetails(msisdn, prd_to_register);

			if (prd_to_register
					.contains(MIP_CustomerManagementPage.BIMA_ACCIDENT_PROTECTION)) {
				log.info("Expected and found ben_fname:" + "DB ben_fname="
						+ db_cust_details.get("accident_ben_fname") + " and "
						+ "ben_fname=" + ben_fname);
				Assert.assertEquals(db_cust_details.get("accident_ben_fname")
						.trim(), ben_fname.trim());
				log.info("Expected and found ben_sname:" + "DB ben_sname="
						+ db_cust_details.get("accident_ben_sname") + " and "
						+ "Input ben_sname=" + ben_sname);
				Assert.assertEquals(db_cust_details.get("accident_ben_sname")
						.trim(), ben_sname.trim());
				log.info("Expected and found ben_age:" + "DB ben_age="
						+ db_cust_details.get("accident_ben_age") + " and "
						+ "Input ben_age=" + ben_age);
				Assert.assertEquals(db_cust_details.get("accident_ben_age")
						.trim(), ben_age.trim());
				log.info("Expected and found cust_relationship:"
						+ "DB cust_relationship="
						+ db_cust_details.get("accident_cust_relationship")
						+ " and " + "Input cust_relationship=" + relationship);
				Assert.assertEquals(
						db_cust_details.get("accident_cust_relationship")
								.trim(), relationship.trim());
				log.info("Expected and found ben_gender:" + "DB ben_gender ="
						+ db_cust_details.get("accident_ben_gender") + " and "
						+ "Input ben_gender=" + ben_gender);
				Assert.assertEquals(db_cust_details.get("accident_ben_gender")
						.trim(), ben_gender.trim());

				log.info("Expected and found ben_msisdn:" + "DB ben_msisdn ="
						+ db_cust_details.get("accident_ben_msisdn") + " and "
						+ "Input ben_msisdn=" + ben_msisdn);
				Assert.assertEquals(db_cust_details.get("accident_ben_msisdn")
						.trim(), ben_msisdn.trim());

			}
			if (prd_to_register
					.contains(MIP_CustomerManagementPage.BIMA_HOSPITALIZATION)) {
				log.info("Expected and found ben_fname:" + "DB ben_fname="
						+ db_cust_details.get("hospitalization_ben_fname")
						+ " and " + "ben_fname=" + ben_fname);
				Assert.assertEquals(
						db_cust_details.get("hospitalization_ben_fname").trim(),
						ben_fname.trim());
				log.info("Expected and found ben_sname:" + "DB ben_sname="
						+ db_cust_details.get("hospitalization_ben_sname")
						+ " and " + "Input ben_sname=" + ben_sname);
				Assert.assertEquals(
						db_cust_details.get("hospitalization_ben_sname").trim(),
						ben_sname.trim());
				log.info("Expected and found ben_age:" + "DB ben_age="
						+ db_cust_details.get("hospitalization_ben_age")
						+ " and " + "Input ben_age=" + ben_age);
				Assert.assertEquals(
						db_cust_details.get("hospitalization_ben_age").trim(),
						ben_age.trim());
				log.info("Expected and found cust_relationship:"
						+ "DB cust_relationship="
						+ db_cust_details
								.get("hospitalization_cust_relationship")
						+ " and " + "Input cust_relationship=" + relationship);
				Assert.assertEquals(
						db_cust_details
								.get("hospitalization_cust_relationship")
								.trim(), relationship.trim());
				log.info("Expected and found ben_gender:" + "DB ben_gender ="
						+ db_cust_details.get("hospitalization_ben_gender")
						+ " and " + "Input ben_gender=" + ben_gender);
				Assert.assertEquals(
						db_cust_details.get("hospitalization_ben_gender")
								.trim(), ben_gender.trim());

				log.info("Expected and found ben_msisdn:" + "DB ben_msisdn ="
						+ db_cust_details.get("hospitalization_ben_msisdn")
						+ " and " + "Input ben_msisdn=" + ben_msisdn);
				Assert.assertEquals(
						db_cust_details.get("hospitalization_ben_msisdn")
								.trim(), ben_msisdn.trim());

			}

			log.info("Expected and found Created By:" + "DB Created By="
					+ db_cust_details.get("user") + " and "
					+ "Input Created By=" + username);
			Assert.assertEquals(db_cust_details.get("user").trim(), username);
			String date = MIP_DateFunctionality.getCurrentDate("YYYY-MM-dd");
			log.info("Expected and found Registered Date:"
					+ "DB Modified Date="
					+ db_cust_details.get("modified_date") + " and "
					+ "Registered Date=" + date);
			Assert.assertTrue(db_cust_details.get("modified_date").contains(
					date));
			regpage.waitForElementToVisible(By.id("search-icon"));
			Thread.sleep(1000);
			regpage.enterMSISDN(msisdn);
			regpage.clickOnSeachIcon();
			Map<String, String> dashboard_cust_details_after_asssign_offer = regpage
					.getCustomerInfo();
			if (!dashboard_cust_details_after_asssign_offer.entrySet()
					.containsAll(
							dashboard_cust_details_before_asssign_offer
									.entrySet())) {
				logger.log(LogStatus.INFO,
						"Dashboard Customer details are not matching before and after Assign offer");
				Assert.assertTrue(false);
			}
			Map<String, String> dashboard_ap_details_after_asssign_offer = regpage
					.getAccidentBeneficiaryInfo();
			Map<String, String> dashboard_hp_details_after_asssign_offer = regpage
					.getHospitalizationBeneficiaryInfo();

			List<String> db_reg_prd = MIP_RegisterCustomer_Queries
					.getRegisteredProduct(msisdn);
			List<String> dashboard_reg_prd = regpage.getRegisteredOffer();
			Assert.assertEquals(db_reg_prd, dashboard_reg_prd);
			Map<String, String> dashboard_cust_details = regpage
					.getCustomerInfo();
			db_cust_details = MIP_RegisterCustomer_Queries.getCustomerDetails(
					msisdn, MIP_CustomerManagementPage.BIMA_ACCIDENT_PROTECTION
							+ " and "
							+ MIP_CustomerManagementPage.BIMA_HOSPITALIZATION);
			log.info("Expected and found cust_fname:" + "DB cust_fname="
					+ db_cust_details.get("cust_fname") + " and "
					+ "Dashboard Customer fname="
					+ dashboard_cust_details.get("cust_fname").trim());
			Assert.assertEquals(db_cust_details.get("cust_fname").trim(),
					dashboard_cust_details.get("cust_fname").trim());
			log.info("Expected and found cust_sname:" + "DB cust_sname="
					+ db_cust_details.get("cust_sname") + " and "
					+ "Dashboard cust_sname="
					+ dashboard_cust_details.get("cust_sname").trim());
			Assert.assertEquals(db_cust_details.get("cust_sname").trim(),
					dashboard_cust_details.get("cust_sname").trim());
			// if (!age.equals("")) {
			log.info("Expected and found cust_age:" + "DB cust_age="
					+ db_cust_details.get("cust_age") + " and "
					+ "Dashboard cust_age="
					+ dashboard_cust_details.get("cust_age").trim());
			Assert.assertEquals(db_cust_details.get("cust_age").trim(),
					dashboard_cust_details.get("cust_age").trim());
			// }
			// if (!dob.equals("")) {
			log.info("Expected and found cust_dob:" + "DB cust_age="
					+ db_cust_details.get("cust_dob") + " and "
					+ "Dashboard cust_age="
					+ dashboard_cust_details.get("cust_dob").trim());
			Assert.assertEquals(db_cust_details.get("cust_dob").trim(),
					dashboard_cust_details.get("cust_dob").trim());
			// }
			log.info("Expected and found cust_gender:" + "DB cust_gender="
					+ db_cust_details.get("cust_gender") + " and "
					+ "Dashboard cust_gender="
					+ dashboard_cust_details.get("cust_gender").trim());
			Assert.assertEquals(db_cust_details.get("cust_gender").trim(),
					dashboard_cust_details.get("cust_gender").trim());
			// if (!cust_cni.equals("")) {
			log.info("Expected and found NIC:" + "DB NIC number="
					+ db_cust_details.get("cust_cnic") + " and "
					+ "Dashboard cust_NIC="
					+ dashboard_cust_details.get("cust_nic"));
			Assert.assertEquals(db_cust_details.get("cust_cnic").trim(),
					dashboard_cust_details.get("cust_nic").trim());

			// }
			if (prd_to_register
					.contains(MIP_CustomerManagementPage.BIMA_ACCIDENT_PROTECTION)) {

				if (!dashboard_hp_details_after_asssign_offer.entrySet()
						.containsAll(
								dashboard_hp_details_before_asssign_offer
										.entrySet())) {
					logger.log(
							LogStatus.INFO,
							"Dashboard Hospitalization Beneficiary details are not matching before and after Assign offer");
					Assert.assertTrue(false);
				}
				Map<String, String> dashboard_ben_details = regpage
						.getAccidentBeneficiaryInfo();
				log.info("Expected and found ben_fname:" + "DB ben_fname="
						+ db_cust_details.get("accident_ben_fname") + " and "
						+ "Dashboard ben_fname="
						+ dashboard_ben_details.get("ben_fname").trim());
				Assert.assertEquals(db_cust_details.get("accident_ben_fname")
						.trim(), dashboard_ben_details.get("ben_fname").trim());
				log.info("Expected and found ben_sname:" + "DB ben_sname="
						+ db_cust_details.get("accident_ben_sname") + " and "
						+ "Dashboard ben_sname="
						+ dashboard_ben_details.get("ben_sname").trim());
				Assert.assertEquals(db_cust_details.get("accident_ben_sname")
						.trim(), dashboard_ben_details.get("ben_sname").trim());
				log.info("Expected and found ben_age:" + "DB ben_age="
						+ db_cust_details.get("accident_ben_age") + " and "
						+ "Dashboard ben_age="
						+ dashboard_ben_details.get("ben_age").trim());
				Assert.assertEquals(db_cust_details.get("accident_ben_age")
						.trim(), dashboard_ben_details.get("ben_age").trim());
				log.info("Expected and found cust_relationship:"
						+ "DB cust_relationship="
						+ db_cust_details.get("accident_cust_relationship")
						+ " and " + "Dashboard cust_relationship="
						+ dashboard_ben_details.get("ben_relationship").trim());
				Assert.assertEquals(
						db_cust_details.get("accident_cust_relationship")
								.trim(),
						dashboard_ben_details.get("ben_relationship").trim());
				log.info("Expected and found ben_msisdn:" + "DB ben_msisdn="
						+ db_cust_details.get("accident_ben_msisdn") + " and "
						+ "Dashboard ben_msisdn="
						+ dashboard_ben_details.get("ben_msisdn").trim());
				Assert.assertEquals(db_cust_details.get("accident_ben_msisdn")
						.trim(), dashboard_ben_details.get("ben_msisdn").trim());
				log.info("Expected and found ben_gender:" + "DB ben_gender="
						+ db_cust_details.get("accident_ben_gender") + " and "
						+ "Dashboard ben_gender="
						+ dashboard_ben_details.get("ben_gender").trim());
				Assert.assertEquals(db_cust_details.get("accident_ben_gender")
						.trim(), dashboard_ben_details.get("ben_gender").trim());

			}
			if (prd_to_register
					.contains(MIP_CustomerManagementPage.BIMA_HOSPITALIZATION)) {
				if (!dashboard_ap_details_after_asssign_offer.entrySet()
						.containsAll(
								dashboard_ap_details_before_asssign_offer
										.entrySet())) {
					logger.log(
							LogStatus.INFO,
							"Dashboard Accident protection Beneficiary details are not matching before and after Assign offer");
					Assert.assertTrue(false);
				}
				Map<String, String> dashboard_ben_details = regpage
						.getHospitalizationBeneficiaryInfo();
				log.info("Expected and found ben_fname:" + "DB ben_fname="
						+ db_cust_details.get("hospitalization_ben_fname")
						+ " and " + "Dashboard ben_fname="
						+ dashboard_ben_details.get("ben_fname").trim());
				Assert.assertEquals(
						db_cust_details.get("hospitalization_ben_fname").trim(),
						dashboard_ben_details.get("ben_fname").trim());
				log.info("Expected and found ben_sname:" + "DB ben_sname="
						+ db_cust_details.get("hospitalization_ben_sname")
						+ " and " + "Dashboard ben_sname="
						+ dashboard_ben_details.get("ben_sname").trim());
				Assert.assertEquals(
						db_cust_details.get("hospitalization_ben_sname").trim(),
						dashboard_ben_details.get("ben_sname").trim());
				log.info("Expected and found ben_age:" + "DB ben_age="
						+ db_cust_details.get("hospitalization_ben_age")
						+ " and " + "Dashboard ben_age="
						+ dashboard_ben_details.get("ben_age").trim());
				Assert.assertEquals(
						db_cust_details.get("hospitalization_ben_age").trim(),
						dashboard_ben_details.get("ben_age").trim());
				log.info("Expected and found cust_relationship:"
						+ "DB cust_relationship="
						+ db_cust_details
								.get("hospitalization_cust_relationship")
						+ " and " + "Dashboard cust_relationship="
						+ dashboard_ben_details.get("ben_relationship").trim());
				Assert.assertEquals(
						db_cust_details
								.get("hospitalization_cust_relationship")
								.trim(),
						dashboard_ben_details.get("ben_relationship").trim());
				log.info("Expected and found ben_msisdn:" + "DB ben_msisdn="
						+ db_cust_details.get("hospitalization_ben_msisdn")
						+ " and " + "Dashboard ben_msisdn="
						+ dashboard_ben_details.get("ben_msisdn").trim());
				Assert.assertEquals(
						db_cust_details.get("hospitalization_ben_msisdn")
								.trim(), dashboard_ben_details
								.get("ben_msisdn").trim());
				log.info("Expected and found ben_gender:" + "DB ben_gender="
						+ db_cust_details.get("hospitalization_ben_gender")
						+ " and " + "Dashboard ben_gender="
						+ dashboard_ben_details.get("ben_gender").trim());
				Assert.assertEquals(
						db_cust_details.get("hospitalization_ben_gender")
								.trim(), dashboard_ben_details
								.get("ben_gender").trim());

			}

			Map<String, String> dashboard_benefit_level = regpage
					.getBenefitLevel();
			Map<String, String> db_benefit_level = MIP_RegisterCustomer_Queries
					.getOfferCover(msisdn, prd_to_register);

			Map<String, String> coaching_pgm_info = regpage
					.getCoachingProgramInfo();

			Map<String, String> db_cp_info = MIP_RegisterCustomer_Queries
					.getCoachingProgramInfo(platform_, msisdn);
			String db_cp = MIP_RegisterCustomer_Queries
					.getCoachingProgram(Integer.parseInt((db_cp_info
							.get("coaching_id"))));
			log.info("Expected and found Coaching program:"
					+ "DB Coaching program=" + db_cp + " and "
					+ "Dashboard Coaching program="
					+ coaching_pgm_info.get("coaching_program").trim());
			Assert.assertTrue(coaching_pgm_info.get("coaching_program")
					.equalsIgnoreCase(db_cp));
			log.info("Expected and found Coaching program created date:"
					+ "DB Coaching program created date="
					+ db_cp_info.get("created_date") + " and "
					+ "Dashboard created date="
					+ coaching_pgm_info.get("date_program_Section").trim());
			if (!db_cp_info.get("created_date").equals("")) {
				String[] dashboard_date = coaching_pgm_info.get(
						"date_program_Section").split("\\-");
				String cp_dashboard_date = dashboard_date[2] + "-"
						+ dashboard_date[1] + "-" + dashboard_date[0];
				Assert.assertTrue(db_cp_info.get("created_date").contains(
						cp_dashboard_date));
			}
			if (db_cp_info.get("is_active").equals("0")) {
				log.info("Expected and found Coaching program status:"
						+ "DB Coaching program status="
						+ MIP_RegisterCustomer_TestData.DEFAULT_COACHING_PROGRAM_STATUS
						+ " and "
						+ "Dashboard coaching program status="
						+ coaching_pgm_info.get("coaching_program_status")
								.trim());
				Assert.assertTrue(coaching_pgm_info
						.get("coaching_program_status")
						.equalsIgnoreCase(
								MIP_RegisterCustomer_TestData.DEFAULT_COACHING_PROGRAM_STATUS));
			} else {
				log.info("Coaching Programm Status is not Inactive after Registration");
				Assert.assertTrue(false);
			}

			if (prd_to_register
					.contains(MIP_CustomerManagementPage.BIMA_ACCIDENT_PROTECTION)) {

				log.info("Expected and found Accident benefit_level:"
						+ "DB benefit_level="
						+ db_benefit_level.get("accident_cover")
						+ " and "
						+ "Dashboard benefit_level="
						+ dashboard_benefit_level.get(
								"bima_accident_benefit_level").trim());
				Assert.assertTrue(dashboard_benefit_level
						.get("bima_accident_benefit_level")
						.trim()
						.contains(db_benefit_level.get("accident_cover").trim()));
			}

			if (prd_to_register
					.contains(MIP_CustomerManagementPage.BIMA_HOSPITALIZATION)) {

				log.info("Expected and found Hospitalization benefit_level:"
						+ "DB benefit_level="
						+ db_benefit_level.get("hospitalization_cover")
						+ " and "
						+ "Dashboard benefit_level="
						+ dashboard_benefit_level.get(
								"bima_hospital_benefit_level").trim());
				Assert.assertTrue(dashboard_benefit_level
						.get("bima_hospital_benefit_level")
						.trim()
						.contains(
								db_benefit_level.get("hospitalization_cover")
										.trim()));
			}
			Map<String, String> sms = MIP_RegisterCustomer_Queries
					.getRegistrationSMS(platform_, msisdn, prd_to_register);
			if (prd_to_register
					.contains(MIP_CustomerManagementPage.BIMA_ACCIDENT_PROTECTION)) {
				logger.log(LogStatus.INFO, "Validating SMS generated for "
						+ MIP_CustomerManagementPage.BIMA_ACCIDENT_PROTECTION);
				Assert.assertTrue(sms
						.get("ap_sms")
						.equalsIgnoreCase(
								MIP_RegisterCustomer_TestData.BIMA_ACCIDENT_REGISTRATION_SMS));
				Assert.assertTrue(sms.get("ap_sms_type").equalsIgnoreCase(
						MIP_RegisterCustomer_TestData.REGISTRATION_SMS_TYPE));
			}
			if (prd_to_register
					.contains(MIP_CustomerManagementPage.BIMA_HOSPITALIZATION)) {
				logger.log(LogStatus.INFO, "Validating SMS generated for "
						+ MIP_CustomerManagementPage.BIMA_HOSPITALIZATION);
				Assert.assertTrue(sms
						.get("hp_sms")
						.equalsIgnoreCase(
								MIP_RegisterCustomer_TestData.BIMA_HEALTH_REGISTRATION_SMS));
				Assert.assertTrue(sms.get("hp_sms_type").equalsIgnoreCase(
						MIP_RegisterCustomer_TestData.REGISTRATION_SMS_TYPE));
			}
			logger.log(LogStatus.INFO,
					"Validated Customer Registration Successfully.");
			homepage.clickOnMenu(MIP_Menu_Constants.REGISTER_CUSTOMER);
		} catch (Throwable t) {
			log.info("RegisterCustomer---" + testcase + " Test Failed");
			log.info("Error occured in the test case", t);
			throw t;
		}
	}

	@Test(priority = 1, enabled = true, testName = "changeCustomerDetails", dataProvider = "changeCustomerDetails", dataProviderClass = MIP_RegisterCustomer_TestData.class)
	public void changeCustomerDetails(String testcase, String msisdn,
			String cust_fname, String cust_sname, String age, String cust_cni,
			String dob, String gender, String pa_relationship,
			String pa_ben_fname, String pa_ben_sname, String pa_ben_age,
			String pa_ben_msisdn, String pa_ben_gender, String hp_relationship,
			String hp_ben_fname, String hp_ben_sname, String hp_ben_age,
			String hp_ben_msisdn, String hp_ben_gender, String success_message)
			throws Throwable {
		testcaseName = testcase + "--" + msisdn;
		try {
			username = MIP_ReadPropertyFile.getPropertyValue("username");
			logger = report.startTest(testcase + "--" + msisdn);
			regpage = PageFactory.initElements(MIP_Test_Configuration.driver,
					MIP_RegisterCustomerPage.class);
			regpage.waitForElementToVisible(By
					.xpath("//h3[contains(text(),'Register Customer')]"));
			regpage.waitForElementToVisible(By
					.xpath("//div[contains(text(),'All fields marked')]"));
			Thread.sleep(1000);
			regpage.enterMSISDN(msisdn);
			regpage.clickOnSeachIcon();
			regpage.waitForElementToVisible(By.id("saveBtn"));
			logger.log(LogStatus.INFO,
					"Changing the Customer and Beneficiary Information");
			List<String> dashboard_reg_prd = regpage.getRegisteredOffer();
			regpage.editCustomerInfo(cust_fname, cust_sname, age, cust_cni,
					dob, gender);
			regpage.editPABeneficiaryInfo(pa_relationship, pa_ben_fname,
					pa_ben_sname, pa_ben_age, pa_ben_msisdn, pa_ben_gender);
			regpage.editHPBeneficiaryInfo(hp_relationship, hp_ben_fname,
					hp_ben_sname, hp_ben_age, hp_ben_msisdn, hp_ben_gender)
					.clickOnSave().confirmPopUp("yes");
			Assert.assertTrue(regpage.getSuccessMsg().replaceAll("\\s", "")
					.equalsIgnoreCase(success_message.replaceAll("\\s", "")));
			logger.log(LogStatus.INFO,
					"Customer and Beneficiary Information Changed Successfully.");
			String reg_prd = "";

			if (dashboard_reg_prd.size() > 1) {
				reg_prd = dashboard_reg_prd.get(0) + " and "
						+ dashboard_reg_prd.get(1);
			} else {
				reg_prd = dashboard_reg_prd.toString();
			}
			Map<String, String> db_cust_details = MIP_RegisterCustomer_Queries
					.getCustomerDetails(msisdn, reg_prd);
			if (!cust_fname.equals("")) {
				log.info("Expected and found cust fname:" + "DB Cust Fname="
						+ db_cust_details.get("cust_fname") + " and "
						+ "Input Cust Fname=" + cust_fname);
				Assert.assertEquals(db_cust_details.get("cust_fname").trim(),
						cust_fname.trim());
			}
			if (!cust_sname.equals("")) {
				log.info("Expected and found cust_sname:" + "DB cust_sname="
						+ db_cust_details.get("cust_sname") + " and "
						+ "Input cust_sname=" + cust_sname);
				Assert.assertEquals(db_cust_details.get("cust_sname").trim(),
						cust_sname.trim());
			}
			if (!age.equals("")) {
				log.info("Expected and found  cust_age:" + "DB cust_age="
						+ db_cust_details.get("cust_age") + " and "
						+ "Input cust_age=" + age);
				Assert.assertEquals(db_cust_details.get("cust_age").trim(),
						age.trim());
			}
			if (!dob.equals("")) {
				log.info("Expected and found  cust_dob:" + "DB cust_dob="
						+ db_cust_details.get("cust_dob") + " and "
						+ "Input cust_dob=" + dob);

				Assert.assertEquals(db_cust_details.get("cust_dob").trim(),
						dob.trim());

			}
			if (!gender.equals("")) {
				log.info("Expected and found cust cust_gender:"
						+ "DB cust_gender="
						+ db_cust_details.get("cust_gender") + " and "
						+ "Input cust_gender=" + gender);
				Assert.assertEquals(db_cust_details.get("cust_gender").trim(),
						gender.trim());
			}
			if (reg_prd
					.contains(MIP_CustomerManagementPage.BIMA_ACCIDENT_PROTECTION)) {
				if (!pa_ben_fname.equals("")) {
					log.info("Expected and found ben_fname:" + "DB ben_fname="
							+ db_cust_details.get("accident_ben_fname")
							+ " and " + "ben_fname=" + pa_ben_fname);
					Assert.assertEquals(
							db_cust_details.get("accident_ben_fname").trim(),
							pa_ben_fname.trim());
				}
				if (!pa_ben_sname.equals("")) {
					log.info("Expected and found ben_sname:" + "DB ben_sname="
							+ db_cust_details.get("accident_ben_sname")
							+ " and " + "Input ben_sname=" + pa_ben_sname);
					Assert.assertEquals(
							db_cust_details.get("accident_ben_sname").trim(),
							pa_ben_sname.trim());
				}
				if (!pa_ben_age.equals("")) {
					log.info("Expected and found ben_age:" + "DB ben_age="
							+ db_cust_details.get("accident_ben_age") + " and "
							+ "Input ben_age=" + pa_ben_age);
					Assert.assertEquals(db_cust_details.get("accident_ben_age")
							.trim(), pa_ben_age.trim());
				}
				if (!pa_relationship.equals("")) {
					log.info("Expected and found cust_relationship:"
							+ "DB cust_relationship="
							+ db_cust_details.get("accident_cust_relationship")
							+ " and " + "Input cust_relationship="
							+ pa_relationship);
					Assert.assertEquals(
							db_cust_details.get("accident_cust_relationship")
									.trim(), pa_relationship.trim());
				}
				if (!pa_ben_gender.equals("")) {
					log.info("Expected and found ben_gender:"
							+ "DB ben_gender ="
							+ db_cust_details.get("accident_ben_gender")
							+ " and " + "Input ben_gender=" + pa_ben_gender);
					Assert.assertEquals(
							db_cust_details.get("accident_ben_gender").trim(),
							pa_ben_gender.trim());
				}
				if (!pa_ben_msisdn.equals("")) {
					log.info("Expected and found ben_msisdn:"
							+ "DB ben_msisdn ="
							+ db_cust_details.get("accident_ben_msisdn")
							+ " and " + "Input ben_msisdn=" + pa_ben_msisdn);
					Assert.assertEquals(
							db_cust_details.get("accident_ben_msisdn").trim(),
							pa_ben_msisdn.trim());
				}
			}
			if (reg_prd
					.contains(MIP_CustomerManagementPage.BIMA_HOSPITALIZATION)) {
				if (!hp_ben_fname.equals("")) {
					log.info("Expected and found ben_fname:" + "DB ben_fname="
							+ db_cust_details.get("hospitalization_ben_fname")
							+ " and " + "ben_fname=" + hp_ben_fname);
					Assert.assertEquals(
							db_cust_details.get("hospitalization_ben_fname")
									.trim(), hp_ben_fname.trim());
				}
				if (!hp_ben_sname.equals("")) {
					log.info("Expected and found ben_sname:" + "DB ben_sname="
							+ db_cust_details.get("hospitalization_ben_sname")
							+ " and " + "Input ben_sname=" + hp_ben_sname);
					Assert.assertEquals(
							db_cust_details.get("hospitalization_ben_sname")
									.trim(), hp_ben_sname.trim());
				}
				if (!hp_ben_age.equals("")) {
					log.info("Expected and found ben_age:" + "DB ben_age="
							+ db_cust_details.get("hospitalization_ben_age")
							+ " and " + "Input ben_age=" + hp_ben_age);
					Assert.assertEquals(
							db_cust_details.get("hospitalization_ben_age")
									.trim(), hp_ben_age.trim());
				}
				if (!hp_relationship.equals("")) {
					log.info("Expected and found cust_relationship:"
							+ "DB cust_relationship="
							+ db_cust_details
									.get("hospitalization_cust_relationship")
							+ " and " + "Input cust_relationship="
							+ hp_relationship);
					Assert.assertEquals(
							db_cust_details.get(
									"hospitalization_cust_relationship").trim(),
							hp_relationship.trim());
				}
				if (!hp_ben_gender.equals("")) {
					log.info("Expected and found ben_gender:"
							+ "DB ben_gender ="
							+ db_cust_details.get("hospitalization_ben_gender")
							+ " and " + "Input ben_gender=" + hp_ben_gender);
					Assert.assertEquals(
							db_cust_details.get("hospitalization_ben_gender")
									.trim(), hp_ben_gender.trim());
				}
				if (!hp_ben_msisdn.equals("")) {
					log.info("Expected and found ben_msisdn:"
							+ "DB ben_msisdn ="
							+ db_cust_details.get("hospitalization_ben_msisdn")
							+ " and " + "Input ben_msisdn=" + hp_ben_msisdn);
					Assert.assertEquals(
							db_cust_details.get("hospitalization_ben_msisdn")
									.trim(), hp_ben_msisdn.trim());
				}
			}
			if (!cust_cni.equals("")) {
				log.info("Expected and found NIC:" + "DB NIC number="
						+ db_cust_details.get("cust_cnic") + " and "
						+ "Input cust_NIC=" + cust_cni);
				Assert.assertEquals(db_cust_details.get("cust_cnic").trim(),
						cust_cni.trim());

			}
			log.info("Expected and found Created By:" + "DB Created By="
					+ db_cust_details.get("user") + " and "
					+ "Input Created By=" + username);
			Assert.assertEquals(db_cust_details.get("user").trim(), username);
			String date = MIP_DateFunctionality.getCurrentDate("YYYY-MM-dd");
			log.info("Expected and found Registered Date:"
					+ "DB Modified Date="
					+ db_cust_details.get("modified_date") + " and "
					+ "Registered Date=" + date);
			Assert.assertTrue(db_cust_details.get("modified_date").contains(
					date));
			Thread.sleep(1000);
			regpage.waitForElementToVisible(By.id("search-icon"));
			regpage.enterMSISDN(msisdn);
			regpage.clickOnSeachIcon();
			List<String> db_reg_prd = MIP_RegisterCustomer_Queries
					.getRegisteredProduct(msisdn);
			// List<String> dashboard_reg_prd = regpage.getRegisteredOffer();
			Assert.assertEquals(db_reg_prd, dashboard_reg_prd);
			Map<String, String> dashboard_cust_details = regpage
					.getCustomerInfo();
			log.info("Expected and found cust_fname:" + "DB cust_fname="
					+ db_cust_details.get("cust_fname") + " and "
					+ "Dashboard Customer fname="
					+ dashboard_cust_details.get("cust_fname").trim());
			Assert.assertEquals(db_cust_details.get("cust_fname").trim(),
					dashboard_cust_details.get("cust_fname").trim());
			log.info("Expected and found cust_sname:" + "DB cust_sname="
					+ db_cust_details.get("cust_sname") + " and "
					+ "Dashboard cust_sname="
					+ dashboard_cust_details.get("cust_sname").trim());
			Assert.assertEquals(db_cust_details.get("cust_sname").trim(),
					dashboard_cust_details.get("cust_sname").trim());
			if (!age.equals("")) {
				log.info("Expected and found cust_age:" + "DB cust_age="
						+ db_cust_details.get("cust_age") + " and "
						+ "Dashboard cust_age="
						+ dashboard_cust_details.get("cust_age").trim());
				Assert.assertEquals(db_cust_details.get("cust_age").trim(),
						dashboard_cust_details.get("cust_age").trim());
			}
			if (!dob.equals("")) {
				log.info("Expected and found cust_dob:" + "DB cust_age="
						+ db_cust_details.get("cust_dob") + " and "
						+ "Dashboard cust_age="
						+ dashboard_cust_details.get("cust_dob").trim());
				Assert.assertEquals(db_cust_details.get("cust_dob").trim(),
						dashboard_cust_details.get("cust_dob").trim());
			}
			log.info("Expected and found cust_gender:" + "DB cust_gender="
					+ db_cust_details.get("cust_gender") + " and "
					+ "Dashboard cust_gender="
					+ dashboard_cust_details.get("cust_gender").trim());
			Assert.assertEquals(db_cust_details.get("cust_gender").trim(),
					dashboard_cust_details.get("cust_gender").trim());
			if (!cust_cni.equals("")) {
				log.info("Expected and found NIC:" + "DB NIC number="
						+ db_cust_details.get("cust_cnic") + " and "
						+ "Dashboard cust_NIC="
						+ dashboard_cust_details.get("cust_nic"));
				Assert.assertEquals(db_cust_details.get("cust_cnic").trim(),
						dashboard_cust_details.get("cust_nic").trim());

			}
			Map<String, String> dashboard_benefit_level = regpage
					.getBenefitLevel();

			if (reg_prd
					.contains(MIP_CustomerManagementPage.BIMA_ACCIDENT_PROTECTION)) {
				Map<String, String> dashboard_ben_details = regpage
						.getAccidentBeneficiaryInfo();
				log.info("Expected and found ben_fname:" + "DB ben_fname="
						+ db_cust_details.get("accident_ben_fname") + " and "
						+ "Dashboard ben_fname="
						+ dashboard_ben_details.get("ben_fname").trim());
				Assert.assertEquals(db_cust_details.get("accident_ben_fname")
						.trim(), dashboard_ben_details.get("ben_fname").trim());
				log.info("Expected and found ben_sname:" + "DB ben_sname="
						+ db_cust_details.get("accident_ben_sname") + " and "
						+ "Dashboard ben_sname="
						+ dashboard_ben_details.get("ben_sname").trim());
				Assert.assertEquals(db_cust_details.get("accident_ben_sname")
						.trim(), dashboard_ben_details.get("ben_sname").trim());
				log.info("Expected and found ben_age:" + "DB ben_age="
						+ db_cust_details.get("accident_ben_age") + " and "
						+ "Dashboard ben_age="
						+ dashboard_ben_details.get("ben_age").trim());
				Assert.assertEquals(db_cust_details.get("accident_ben_age")
						.trim(), dashboard_ben_details.get("ben_age").trim());
				log.info("Expected and found cust_relationship:"
						+ "DB cust_relationship="
						+ db_cust_details.get("accident_cust_relationship")
						+ " and " + "Dashboard cust_relationship="
						+ dashboard_ben_details.get("ben_relationship").trim());
				Assert.assertEquals(
						db_cust_details.get("accident_cust_relationship")
								.trim(),
						dashboard_ben_details.get("ben_relationship").trim());
				log.info("Expected and found ben_msisdn:" + "DB ben_msisdn="
						+ db_cust_details.get("accident_ben_msisdn") + " and "
						+ "Dashboard ben_msisdn="
						+ dashboard_ben_details.get("ben_msisdn").trim());
				Assert.assertEquals(db_cust_details.get("accident_ben_msisdn")
						.trim(), dashboard_ben_details.get("ben_msisdn").trim());
				log.info("Expected and found ben_gender:" + "DB ben_gender="
						+ db_cust_details.get("accident_ben_gender") + " and "
						+ "Dashboard ben_gender="
						+ dashboard_ben_details.get("ben_gender").trim());
				Assert.assertEquals(db_cust_details.get("accident_ben_gender")
						.trim(), dashboard_ben_details.get("ben_gender").trim());
				Map<String, String> ben_level = MIP_RegisterCustomer_Queries
						.getOfferCover(
								msisdn,
								MIP_CustomerManagementPage.BIMA_ACCIDENT_PROTECTION);
				log.info("Expected and found life benefit_level:"
						+ "Input benefit_level="
						+ ben_level.get("accident_cover")
						+ " and "
						+ "Dashboard benefit_level="
						+ dashboard_benefit_level.get(
								"bima_accident_benefit_level").trim());
				Assert.assertTrue(dashboard_benefit_level
						.get("bima_accident_benefit_level").trim()
						.contains(ben_level.get("accident_cover").trim()));
			}
			if (reg_prd
					.contains(MIP_CustomerManagementPage.BIMA_HOSPITALIZATION)) {
				Map<String, String> dashboard_ben_details = regpage
						.getHospitalizationBeneficiaryInfo();
				log.info("Expected and found ben_fname:" + "DB ben_fname="
						+ db_cust_details.get("hospitalization_ben_fname")
						+ " and " + "Dashboard ben_fname="
						+ dashboard_ben_details.get("ben_fname").trim());
				Assert.assertEquals(
						db_cust_details.get("hospitalization_ben_fname").trim(),
						dashboard_ben_details.get("ben_fname").trim());
				log.info("Expected and found ben_sname:" + "DB ben_sname="
						+ db_cust_details.get("hospitalization_ben_sname")
						+ " and " + "Dashboard ben_sname="
						+ dashboard_ben_details.get("ben_sname").trim());
				Assert.assertEquals(
						db_cust_details.get("hospitalization_ben_sname").trim(),
						dashboard_ben_details.get("ben_sname").trim());
				log.info("Expected and found ben_age:" + "DB ben_age="
						+ db_cust_details.get("hospitalization_ben_age")
						+ " and " + "Dashboard ben_age="
						+ dashboard_ben_details.get("ben_age").trim());
				Assert.assertEquals(
						db_cust_details.get("hospitalization_ben_age").trim(),
						dashboard_ben_details.get("ben_age").trim());
				log.info("Expected and found cust_relationship:"
						+ "DB cust_relationship="
						+ db_cust_details
								.get("hospitalization_cust_relationship")
						+ " and " + "Dashboard cust_relationship="
						+ dashboard_ben_details.get("ben_relationship").trim());
				Assert.assertEquals(
						db_cust_details
								.get("hospitalization_cust_relationship")
								.trim(),
						dashboard_ben_details.get("ben_relationship").trim());
				log.info("Expected and found ben_msisdn:" + "DB ben_msisdn="
						+ db_cust_details.get("hospitalization_ben_msisdn")
						+ " and " + "Dashboard ben_msisdn="
						+ dashboard_ben_details.get("ben_msisdn").trim());
				Assert.assertEquals(
						db_cust_details.get("hospitalization_ben_msisdn")
								.trim(), dashboard_ben_details
								.get("ben_msisdn").trim());
				log.info("Expected and found ben_gender:" + "DB ben_gender="
						+ db_cust_details.get("hospitalization_ben_gender")
						+ " and " + "Dashboard ben_gender="
						+ dashboard_ben_details.get("ben_gender").trim());
				Assert.assertEquals(
						db_cust_details.get("hospitalization_ben_gender")
								.trim(), dashboard_ben_details
								.get("ben_gender").trim());
				Map<String, String> ben_level = MIP_RegisterCustomer_Queries
						.getOfferCover(msisdn,
								MIP_CustomerManagementPage.BIMA_HOSPITALIZATION);
				log.info("Expected and found life benefit_level:"
						+ "Input benefit_level="
						+ ben_level.get("hospitalization_cover")
						+ " and "
						+ "Dashboard benefit_level="
						+ dashboard_benefit_level.get(
								"bima_hospital_benefit_level").trim());
				Assert.assertTrue(dashboard_benefit_level
						.get("bima_hospital_benefit_level")
						.trim()
						.contains(ben_level.get("hospitalization_cover").trim()));
			}
			logger.log(LogStatus.INFO,
					"Customer and Beneficiary Information Changes are validated Successfully.");
			homepage.clickOnMenu(MIP_Menu_Constants.REGISTER_CUSTOMER);
		} catch (Throwable t) {
			// homepage.clickOnMenu(MIP_Menu_Constants.REGISTER_CUSTOMER);
			log.info("RegisterCustomer---" + testcase + " Test Failed");
			log.info("Error occured in the test case", t);
			throw t;
		}
	}

	@Test(priority = 3, enabled = true, testName = "RegisterCustomer_Mandatory_Field_Validation", dataProvider = "registerCustomermandatoryFieldValidation", dataProviderClass = MIP_RegisterCustomer_TestData.class)
	public void mandatoryFieldValidationInregistration(String msisdn,
			String errmsg1, String errmsg2) throws Throwable {
		testcaseName = "MIP_webPortal_TC_Mandatory_Field_Validation_Customer_Registration--"
				+ msisdn;
		try {
			logger = report
					.startTest("RegisterCustomer_Mandatory_Field_Validation--"
							+ testcaseName);
			regpage = PageFactory.initElements(MIP_Test_Configuration.driver,
					MIP_RegisterCustomerPage.class);
			regpage.waitForElementToVisible(By
					.xpath("//h3[contains(text(),'Register Customer')]"));
			regpage.waitForElementToVisible(By
					.xpath("//div[contains(text(),'All fields marked')]"));
			Thread.sleep(1000);
			regpage.enterMSISDN(msisdn);
			regpage.clickOnSeachIcon();
			regpage.waitForElementToVisible(By.id("saveBtn"));
			regpage.clickOnSave();
			logger.log(LogStatus.INFO,
					"Clicked on Register Button without entering mandatory field values");
			String err_msg = regpage.getValidationMessage();
			log.info("Expected and found validation message: Expected="
					+ errmsg1 + " Found=" + err_msg);
			Assert.assertTrue(err_msg.replaceAll("\\s", "").equalsIgnoreCase(
					errmsg1.replaceAll("\\s", "")));
			regpage.selectAvailableInformation(MIP_CustomerManagementPage.BIMA_ACCIDENT_PROTECTION);
			regpage.selectAvailableInformation(MIP_CustomerManagementPage.BIMA_HOSPITALIZATION);
			regpage.clickOnSave();
			err_msg = regpage.getValidationMessage();
			log.info("Expected and found validation message: Expected="
					+ errmsg2 + " Found=" + err_msg);
			Assert.assertTrue(err_msg.replaceAll("\\s", "").equalsIgnoreCase(
					errmsg2.replaceAll("\\s", "")));
			logger.log(LogStatus.INFO,
					"Error Message Validation is successfull");
		} catch (Throwable t) {
			log.info("RegisterCustomer_Mandatory_Field_Validation--"
					+ testcaseName + " Test Failed");
			log.info("Error occured in the test case", t);
			throw t;
		}
	}

	@Test(priority = 0, enabled = true, testName = "Field_Validation_negativeData", dataProvider = "RegisterCustomer_FieldValidation", dataProviderClass = MIP_RegisterCustomer_TestData.class)
	public void fieldValidationWithNegativeData_Postpaid(String testcase,
			String mno, String msisdn, String prepostStatus,
			String prd_to_register, String accident_level, String cust_fname,
			String cust_sname, String age, String cust_cni, String dob,
			String gender, String relationship, String ben_fname,
			String ben_sname, String ben_age, String ben_msisdn,
			String ben_gender, String err_msg) throws Throwable {
		testcaseName = testcase + "---" + msisdn;
		try {
			logger = report.startTest("fieldValidationWithNegativeData--"
					+ testcase);
			regpage = PageFactory.initElements(MIP_Test_Configuration.driver,
					MIP_RegisterCustomerPage.class);
			regpage.waitForElementToVisible(By
					.xpath("//h3[contains(text(),'Register Customer')]"));
			regpage.waitForElementToVisible(By
					.xpath("//div[contains(text(),'All fields marked')]"));
			if (mno.equalsIgnoreCase(MIP_CustomerManagementPage.BIMA_WARID))
				MIP_RegisterCustomer_Queries.connectToWaridDBAndInserRecord(
						platform_, cust_fname, msisdn, prepostStatus, cust_cni);
			Thread.sleep(1000);
			regpage.enterMSISDN(msisdn);
			regpage.clickOnSeachIcon();
			regpage.waitForElementToVisible(By.id("saveBtn"));

			regpage.enterCustomerInfo(mno, cust_fname, cust_sname, age,
					cust_cni, dob, gender);
			logger.log(LogStatus.INFO, "Entered Customer Information");
			if (!dob.equals(""))
				Thread.sleep(1000);
			regpage.selectAvailableInformation(prd_to_register);
			logger.log(LogStatus.INFO, "Selected Available product--"
					+ prd_to_register);
			if (MIP_CustomerManagementPage.BIMA_ACCIDENT_PROTECTION
					.contains(prd_to_register))
				regpage.selectBimaAccidentProtecionBenefitLevel(accident_level);

			regpage.enterBeneficiaryInfoForAccidentProtection(relationship,
					ben_fname, ben_sname, ben_age, ben_msisdn, ben_gender)
					.clickOnSave();
			logger.log(LogStatus.INFO, "Entered Beneficiary Information");
			String err = regpage.getValidationMsg().trim();
			log.info("Actual Error message:" + err_msg
					+ " Error message found:" + err);
			logger.log(LogStatus.INFO, "Validating the Error Message");
			Assert.assertTrue(err.trim().replaceAll("\\s", "")
					.equalsIgnoreCase(err_msg.trim().replaceAll("\\s", "")));
			logger.log(LogStatus.INFO, "Validated Error Message");
			homepage.clickOnMenu(MIP_Menu_Constants.REGISTER_CUSTOMER);

		} catch (Throwable t) {
			log.info("fieldValidationWithNegativeData--" + testcase
					+ " Test Failed");
			log.info("Error occured in the test case", t);
			throw t;
		}
	}

	@AfterMethod(alwaysRun = true)
	public void after_test(ITestResult res) {

		if (res.getStatus() == ITestResult.FAILURE) {
			String path = MIP_ScreenShots.takeScreenShot(
					MIP_Test_Configuration.driver, res.getName() + "_"
							+ testcaseName);
			logger.log(LogStatus.FAIL, testcaseName + "----Test Failed");
			logger.log(LogStatus.ERROR, res.getThrowable());
			logger.log(LogStatus.FAIL, logger.addScreenCapture(path));
			homepage.clickOnMenu(MIP_Menu_Constants.REGISTER_CUSTOMER);
		} else {
			logger.log(LogStatus.PASS, testcaseName + "---Test passed");
		}
	}

	@AfterTest(alwaysRun = true)
	@Parameters("flag")
	public void tear_down(@Optional("0") String flag) {
		if (flag.equals("0"))
			MIP_BrowserFactory.closeDriver(MIP_Test_Configuration.driver);
		else
			homepage.clickOnMenu(MIP_Menu_Constants.CUSTOMER);
		report.endTest(logger);
		report.flush();
	}

}