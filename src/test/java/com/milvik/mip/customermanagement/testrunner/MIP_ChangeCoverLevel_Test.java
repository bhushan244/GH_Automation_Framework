package com.milvik.mip.customermanagement.testrunner;

import static org.testng.Assert.assertTrue;

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
import com.milvik.mip.dataprovider.MIP_ChangeCoverLevel_TestData;
import com.milvik.mip.dataprovider.MIP_DeRegisterCustomer_TestData;
import com.milvik.mip.dataprovider.MIP_RegisterCustomer_TestData;
import com.milvik.mip.dbqueries.MIP_ChangeCoverLevel_Queries;
import com.milvik.mip.dbqueries.MIP_DeRegisterCustomer_Queries;
import com.milvik.mip.dbqueries.MIP_RegisterCustomer_Queries;
import com.milvik.mip.pageobjects.MIP_ChangeCoverLevelPage;
import com.milvik.mip.pageobjects.MIP_DeRegisterCustomerPage;
import com.milvik.mip.pageobjects.MIP_HomePage;
import com.milvik.mip.pageobjects.MIP_LoginPage;
import com.milvik.mip.pageobjects.MIP_QAConfirmationPage;
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

public class MIP_ChangeCoverLevel_Test {
	ExtentReports report;
	ExtentTest logger;
	static Logger log;
	MIP_LoginPage loginpage;
	MIP_HomePage homepage;
	MIP_ChangeCoverLevelPage changecoverlevel = null;
	MIP_DeRegisterCustomerPage deregcust = null;
	MIP_QAConfirmationPage qaconfpage = null;
	String testcaseName;
	MIP_RegisterCustomerPage regpage = null;
	String username;
	String platform_ = "";

	@BeforeTest
	@Parameters({ "browser", "flag", "platform" })
	public void test_setup(@Optional("firefox") String browser,
			@Optional("0") String flag, @Optional("windows") String platform) {
		platform_ = platform;
		log = MIP_Logging.logDetails("MIP_ChangeCoverLevel_Test");
		report = new ExtentReports(
				"Test_Reports/MIP_ChangeCoverLevel_Test.html");
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
			homepage.clickOnMenu(MIP_Menu_Constants.CHANGE_COVER_LEVEL);
		} else {
			homepage = new MIP_HomePage(MIP_Test_Configuration.driver);
			WebDriverWait w = new WebDriverWait(MIP_Test_Configuration.driver,
					600);
			w.until(ExpectedConditions.visibilityOfElementLocated((By
					.linkText(MIP_Menu_Constants.CUSTOMER))));
			homepage.clickOnMenu(MIP_Menu_Constants.CUSTOMER);
			homepage.clickOnMenu(MIP_Menu_Constants.CHANGE_COVER_LEVEL);

		}
	}

	@Test(priority = 0, enabled = true, testName = "validate_changeCoverLevel", dataProvider = "validate_changeCoverLevel", dataProviderClass = MIP_ChangeCoverLevel_TestData.class)
	public void validate_changeCoverLevel(String msisdn) throws Throwable {
		String testcase = "validate_changeCoverLevel";
		testcaseName = testcase + "_" + msisdn;
		try {
			logger = report.startTest("validate_changeCoverLevel_" + testcase
					+ "_" + msisdn);
			changecoverlevel = PageFactory.initElements(
					MIP_Test_Configuration.driver,
					MIP_ChangeCoverLevelPage.class);
			changecoverlevel.waitForElementToVisible(By
					.xpath("//h3[contains(text(),'Change Cover Level')]"));
			changecoverlevel.waitForElementToVisible(By
					.xpath("//div[contains(text(),'All fields marked')]"));
			Thread.sleep(1000);
			changecoverlevel.enterMSISDN(msisdn);
			changecoverlevel.clickOnSeachIcon();
			changecoverlevel.waitForElementToVisible(By.id("saveBtn"));
			Map<String, String> details = changecoverlevel
					.validateChangeCoverLevelPage();
			Assert.assertTrue(details.get("msisdn").equals(msisdn.trim()));
			List<String> db_offers = MIP_RegisterCustomer_Queries
					.getRegisteredProduct(msisdn);
			if (db_offers
					.toString()
					.contains(
							MIP_CustomerManagementPage.BIMA_ACCIDENT_PROTECTION_LIKE_DB)) {
				Assert.assertTrue(db_offers.toString().contains(
						details.get("bima_Pa")));
				Map<String, String> offer_level = MIP_RegisterCustomer_Queries
						.getOfferCover(
								msisdn,
								MIP_CustomerManagementPage.BIMA_ACCIDENT_PROTECTION);
				String dashboard_offer = details.get("pa_offer_level")
						.replaceAll("[a-zA-Z\\s]", "");
				Assert.assertTrue(offer_level.get("accident_cover").contains(
						dashboard_offer));
			}
			if (db_offers.toString().contains(
					MIP_CustomerManagementPage.BIMA_HOSPITALIZATION)) {
				Assert.assertTrue(db_offers.toString().contains(
						details.get("bima_hp")));
				Map<String, String> offer_level = MIP_RegisterCustomer_Queries
						.getOfferCover(msisdn,
								MIP_CustomerManagementPage.BIMA_HOSPITALIZATION);
				String dashboard_offer = details.get("hp_offer_level")
						.replaceAll("a-zA-Z\\s", "");
				Assert.assertTrue(dashboard_offer.contains(offer_level
						.get("hospitalization_cover")));
			}
			homepage.clickOnMenu(MIP_Menu_Constants.CHANGE_COVER_LEVEL);
		} catch (Throwable t) {
			log.info("Change Cover Level---" + testcase + " Test Failed");
			log.info("Error occured in the test case", t);
			throw t;
		}
	}

	@Test(priority = 1, enabled = true, testName = "changeCoverLevel", dataProvider = "changeCoverLevel", dataProviderClass = MIP_ChangeCoverLevel_TestData.class)
	public void changeCoverLevel(String testcase, String mno, String msisdn,
			String prepostStatus, String prd_to_register,
			String accident_level, String hospital_level, String cust_fname,
			String cust_sname, String age, String cust_cni, String dob,
			String gender, String accident_relationship,
			String accident_ben_fname, String accident_ben_sname,
			String accident_ben_age, String accident_ben_msisdn,
			String accident_ben_gender, String hospitalization_relationship,
			String hospitalization_ben_fname, String hospitalization_ben_sname,
			String hospitalization_ben_age, String hospitalization_ben_msisdn,
			String hospitalization_ben_gender, String success_message,
			String conf_status, String prd_level_change, String new_pa_level,
			String new_hp_level, String change_level_succ_msg) throws Throwable {
		testcaseName = testcase + "--" + msisdn;
		try {
			username = MIP_ReadPropertyFile.getPropertyValue("username");
			logger = report.startTest("Change_Cover_Level--" + testcase + "--"
					+ msisdn);
			homepage.clickOnMenu(MIP_Menu_Constants.REGISTER_CUSTOMER);
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
			logger.log(LogStatus.INFO,
					"Validated Customer Registration Successfully.");

			// /Customer Confirmation:
			if (conf_status.equals("1")) {
				logger.log(LogStatus.INFO, "Confirming the Customer");
				homepage.clickOnMenu(MIP_Menu_Constants.QA_CONFIRMAION);
				qaconfpage = PageFactory.initElements(
						MIP_Test_Configuration.driver,
						MIP_QAConfirmationPage.class);
				qaconfpage.waitForElementToVisible(By
						.xpath("//h3[contains(text(),'QA Confirmation')]"));
				qaconfpage.waitForElementToVisible(By.id("msisdn"));
				qaconfpage.enterMSISDN(msisdn);
				qaconfpage.clickOnSeachIcon();
				Map<String, String> qaconf_details = qaconfpage
						.getCustomerInformation();

				log.info("Expected and found cust_fname:" + "DB cust_fname="
						+ db_cust_details.get("cust_fname") + " and "
						+ "Dashboard Customer fname="
						+ qaconf_details.get("cust_fname").trim());
				Assert.assertEquals(db_cust_details.get("cust_fname").trim(),
						qaconf_details.get("cust_fname").trim());
				log.info("Expected and found cust_sname:" + "DB cust_sname="
						+ db_cust_details.get("cust_sname") + " and "
						+ "Dashboard cust_sname="
						+ qaconf_details.get("cust_sname").trim());
				Assert.assertEquals(db_cust_details.get("cust_sname").trim(),
						qaconf_details.get("cust_sname").trim());

				log.info("Expected and found cust_age:" + "DB cust_age="
						+ db_cust_details.get("cust_age") + " and "
						+ "Dashboard cust_age="
						+ qaconf_details.get("cust_age").trim());
				Assert.assertEquals(db_cust_details.get("cust_age").trim(),
						qaconf_details.get("cust_age").trim());

				if (!dob.equals("")) {
					log.info("Expected and found cust_dob:" + "DB cust_age="
							+ db_cust_details.get("cust_dob") + " and "
							+ "Dashboard cust_age="
							+ qaconf_details.get("cust_dob").trim());
					Assert.assertEquals(db_cust_details.get("cust_dob").trim(),
							qaconf_details.get("cust_dob").trim());
				}
				if (!cust_cni.equals("")) {
					log.info("Expected and found NIC:" + "DB NIC number="
							+ db_cust_details.get("cust_cnic") + " and "
							+ "Dashboard cust_NIC="
							+ qaconf_details.get("cust_nic"));
					Assert.assertEquals(
							db_cust_details.get("cust_cnic").trim(),
							qaconf_details.get("cust_nic").trim());

				}

				if (prd_to_register
						.contains(MIP_CustomerManagementPage.BIMA_ACCIDENT_PROTECTION)) {
					Map<String, String> pa_qaconf_details = qaconfpage
							.getPABenInformation();
					log.info("Expected and found ben_fname:" + "DB ben_fname="
							+ db_cust_details.get("accident_ben_fname")
							+ " and " + "Dashboard ben_fname="
							+ pa_qaconf_details.get("pa_ben_fname").trim());
					Assert.assertEquals(
							db_cust_details.get("accident_ben_fname").trim(),
							pa_qaconf_details.get("pa_ben_fname").trim());
					log.info("Expected and found ben_sname:" + "DB ben_sname="
							+ db_cust_details.get("accident_ben_sname")
							+ " and " + "Dashboard ben_sname="
							+ pa_qaconf_details.get("pa_ben_sname").trim());
					Assert.assertEquals(
							db_cust_details.get("accident_ben_sname").trim(),
							pa_qaconf_details.get("pa_ben_sname").trim());
					log.info("Expected and found ben_age:" + "DB ben_age="
							+ db_cust_details.get("accident_ben_age") + " and "
							+ "Dashboard ben_age="
							+ pa_qaconf_details.get("pa_ben_age").trim());
					Assert.assertEquals(db_cust_details.get("accident_ben_age")
							.trim(), pa_qaconf_details.get("pa_ben_age").trim());
					log.info("Expected and found cust_relationship:"
							+ "DB cust_relationship="
							+ db_cust_details.get("accident_cust_relationship")
							+ " and " + "Dashboard cust_relationship="
							+ pa_qaconf_details.get("pa_relationship").trim());
					Assert.assertEquals(
							db_cust_details.get("accident_cust_relationship")
									.trim(),
							pa_qaconf_details.get("pa_relationship").trim());
					log.info("Expected and found Accident benefit_level:"
							+ "Input benefit_level=" + accident_level + " and "
							+ "Dashboard benefit_level="
							+ pa_qaconf_details.get("pa_coverlevel").trim());
					Assert.assertTrue(pa_qaconf_details
							.get("pa_coverlevel")
							.trim()
							.contains(
									accident_level.replaceAll("[a-zA-Z\\s,]",
											"").trim()));
					Map<String, String> user_info = MIP_DeRegisterCustomer_Queries
							.getCreatedUserInfo(
									msisdn,
									MIP_CustomerManagementPage.BIMA_ACCIDENT_PROTECTION);
					log.info("Expected and Found Reg Date details:"
							+ "DB reg_date=" + user_info.get("created_date")
							+ " reg_date ="
							+ pa_qaconf_details.get("pa_reg_date"));
					String reg_date = MIP_DateFunctionality
							.converDateToDBDateFormat(pa_qaconf_details.get(
									"pa_reg_date").split("\\s")[0]);
					Assert.assertTrue(user_info.get("created_date").trim()
							.contains(reg_date.trim()));
					log.info("Expected and Found reg details:" + "DB reg_by="
							+ pa_qaconf_details.get("pa_agent_id")
							+ " Reg_by =" + user_info.get("created_by"));

					Assert.assertTrue(pa_qaconf_details.get("pa_agent_id")
							.trim()
							.equalsIgnoreCase(user_info.get("created_by")));

					String confirmation_status = MIP_DeRegisterCustomer_Queries
							.getConfirmationStatus(
									msisdn,
									MIP_CustomerManagementPage.BIMA_ACCIDENT_PROTECTION);
					log.info("Expected and Found confirmation_status:"
							+ "DB confirmation_status=" + confirmation_status
							+ " Dashboard confirmation_status ="
							+ pa_qaconf_details.get("pa_conf_status"));
					Assert.assertTrue(confirmation_status.trim()
							.equalsIgnoreCase(
									pa_qaconf_details.get("pa_conf_status")));
				}
				if (prd_to_register
						.contains(MIP_CustomerManagementPage.BIMA_HOSPITALIZATION)) {
					Map<String, String> hp_qaconf_details = qaconfpage
							.getHPBenInformation();
					log.info("Expected and found ben_fname:" + "DB ben_fname="
							+ db_cust_details.get("hospitalization_ben_fname")
							+ " and " + "Dashboard ben_fname="
							+ hp_qaconf_details.get("hp_ben_fname").trim());
					Assert.assertEquals(
							db_cust_details.get("hospitalization_ben_fname")
									.trim(),
							hp_qaconf_details.get("hp_ben_fname").trim());
					log.info("Expected and found ben_sname:" + "DB ben_sname="
							+ db_cust_details.get("hospitalization_ben_sname")
							+ " and " + "Dashboard ben_sname="
							+ hp_qaconf_details.get("hp_ben_sname").trim());
					Assert.assertEquals(
							db_cust_details.get("hospitalization_ben_sname")
									.trim(),
							hp_qaconf_details.get("hp_ben_sname").trim());
					log.info("Expected and found ben_age:" + "DB ben_age="
							+ db_cust_details.get("hospitalization_ben_age")
							+ " and " + "Dashboard ben_age="
							+ hp_qaconf_details.get("hp_ben_age").trim());
					Assert.assertEquals(
							db_cust_details.get("hospitalization_ben_age")
									.trim(), hp_qaconf_details
									.get("hp_ben_age").trim());
					log.info("Expected and found cust_relationship:"
							+ "DB cust_relationship="
							+ db_cust_details
									.get("hospitalization_cust_relationship")
							+ " and " + "Dashboard cust_relationship="
							+ hp_qaconf_details.get("hp_relationship").trim());
					Assert.assertEquals(
							db_cust_details.get(
									"hospitalization_cust_relationship").trim(),
							hp_qaconf_details.get("hp_relationship").trim());
					log.info("Expected and found Hospital benefit_level:"
							+ "Input benefit_level=" + hospital_level + " and "
							+ "Dashboard benefit_level="
							+ hp_qaconf_details.get("hp_coverlevel").trim());
					Assert.assertTrue(hp_qaconf_details
							.get("hp_coverlevel")
							.trim()
							.contains(
									hospital_level.replaceAll("[a-zA-Z\\s,]",
											"").trim()));
					Map<String, String> user_info = MIP_DeRegisterCustomer_Queries
							.getCreatedUserInfo(
									msisdn,
									MIP_CustomerManagementPage.BIMA_HOSPITALIZATION);
					log.info("Expected and Found Reg Date details:"
							+ "DB reg_date=" + user_info.get("created_date")
							+ " reg_date ="
							+ hp_qaconf_details.get("hp_reg_date"));
					String reg_date = MIP_DateFunctionality
							.converDateToDBDateFormat(hp_qaconf_details.get(
									"hp_reg_date").split("\\s")[0]);
					Assert.assertTrue(user_info.get("created_date").trim()
							.contains(reg_date.trim()));
					log.info("Expected and Found dereg details:" + "DB reg_by="
							+ hp_qaconf_details.get("hp_agent_id")
							+ " Reg_by =" + user_info.get("created_by"));

					Assert.assertTrue(hp_qaconf_details.get("hp_agent_id")
							.trim()
							.equalsIgnoreCase(user_info.get("created_by")));

					String confirmation_status = MIP_DeRegisterCustomer_Queries
							.getConfirmationStatus(
									msisdn,
									MIP_CustomerManagementPage.BIMA_HOSPITALIZATION);
					log.info("Expected and Found confirmation_status:"
							+ "DB confirmation_status=" + confirmation_status
							+ " Dashboard confirmation_status ="
							+ hp_qaconf_details.get("hp_conf_status"));
					Assert.assertTrue(confirmation_status.trim()
							.equalsIgnoreCase(
									hp_qaconf_details.get("hp_conf_status")));
				}

				qaconfpage.clickOnConfirmButton();
				qaconfpage.confirmPopUp("yes");
				if (mno.equalsIgnoreCase(MIP_CustomerManagementPage.BIMA_WARID)) {
					MIP_DeRegisterCustomer_Queries.confirmTheCustomer(msisdn,
							username, prepost_status);
					Assert.assertTrue(qaconfpage
							.getWaridConfirmationSuccessMessage()
							.equalsIgnoreCase(
									MIP_DeRegisterCustomer_TestData.BIMA_WARID_CONFIRMATION_SUCC_MSG));

				} else {
					Assert.assertTrue(qaconfpage
							.getSuccessMessage()
							.equalsIgnoreCase(
									MIP_DeRegisterCustomer_TestData.BIMA_JAZZ_CONFIRMATION_SUCC_MSG));
				}

				if (prd_to_register
						.contains(MIP_CustomerManagementPage.BIMA_ACCIDENT_PROTECTION)) {

					Assert.assertTrue(MIP_DeRegisterCustomer_Queries
							.getConfirmationBy(
									msisdn,
									MIP_CustomerManagementPage.BIMA_ACCIDENT_PROTECTION)
							.trim().equalsIgnoreCase(username.trim()));
					String confirmation_status = MIP_DeRegisterCustomer_Queries
							.getConfirmationStatus(
									msisdn,
									MIP_CustomerManagementPage.BIMA_ACCIDENT_PROTECTION);
					log.info("Expected and Found confirmation_status:"
							+ "DB confirmation_status="
							+ confirmation_status
							+ " Input confirmation_status ="
							+ MIP_DeRegisterCustomer_TestData.CONFIRMED_CUSTOMER);
					Assert.assertTrue(confirmation_status
							.trim()
							.equalsIgnoreCase(
									MIP_DeRegisterCustomer_TestData.CONFIRMED_CUSTOMER));
				}
				if (prd_to_register
						.contains(MIP_CustomerManagementPage.BIMA_HOSPITALIZATION)) {

					Assert.assertTrue(MIP_DeRegisterCustomer_Queries
							.getConfirmationBy(
									msisdn,
									MIP_CustomerManagementPage.BIMA_HOSPITALIZATION)
							.trim().equalsIgnoreCase(username.trim()));
					String confirmation_status = MIP_DeRegisterCustomer_Queries
							.getConfirmationStatus(
									msisdn,
									MIP_CustomerManagementPage.BIMA_HOSPITALIZATION);
					log.info("Expected and Found confirmation_status:"
							+ "DB confirmation_status="
							+ confirmation_status
							+ " Input confirmation_status ="
							+ MIP_DeRegisterCustomer_TestData.CONFIRMED_CUSTOMER);
					Assert.assertTrue(confirmation_status
							.trim()
							.equalsIgnoreCase(
									MIP_DeRegisterCustomer_TestData.CONFIRMED_CUSTOMER));
				}

			}
			// /////CHANGE COVER LEVEL
			logger.log(LogStatus.INFO, "Changing the Customer Cover Level");
			homepage.clickOnMenu(MIP_Menu_Constants.CHANGE_COVER_LEVEL);
			changecoverlevel = PageFactory.initElements(
					MIP_Test_Configuration.driver,
					MIP_ChangeCoverLevelPage.class);
			changecoverlevel.waitForElementToVisible(By
					.xpath("//h3[contains(text(),'Change Cover Level')]"));
			changecoverlevel.waitForElementToVisible(By
					.xpath("//div[contains(text(),'All fields marked')]"));
			changecoverlevel.enterMSISDN(msisdn);
			changecoverlevel.clickOnSeachIcon();

			changecoverlevel.waitForElementToVisible(By.id("saveBtn"));
			int db_offer_level_before_change_level = -1;
			if (prd_level_change
					.contains(MIP_CustomerManagementPage.BIMA_ACCIDENT_PROTECTION)) {
				db_offer_level_before_change_level = MIP_ChangeCoverLevel_Queries
						.getOfferDetails(
								msisdn,
								MIP_CustomerManagementPage.BIMA_ACCIDENT_PROTECTION);
				changecoverlevel.updateAccidentCoverLevel(new_pa_level)
						.clickOnSaveChanges();
				Assert.assertTrue(changecoverlevel.getSuccessMessage()
						.equalsIgnoreCase(change_level_succ_msg.trim()));
				changecoverlevel.waitForElementToVisible(By.linkText("here"))
						.click();
				changecoverlevel.enterMSISDN(msisdn);
				changecoverlevel.clickOnSeachIcon();
				String current_cover_level = changecoverlevel
						.getAccidentCoverLevelInfo();
				if (conf_status.equals("1")) {
					Assert.assertEquals(
							db_offer_level_before_change_level,
							MIP_ChangeCoverLevel_Queries
									.getOfferDetails(
											msisdn,
											MIP_CustomerManagementPage.BIMA_ACCIDENT_PROTECTION));
					Assert.assertTrue(MIP_ChangeCoverLevel_Queries
							.checkRecordInUpdateChangeCoverLevel(
									msisdn,
									MIP_CustomerManagementPage.BIMA_ACCIDENT_PROTECTION));
					Map<String, String> offer_cover = MIP_ChangeCoverLevel_Queries
							.getupdateCoverLevelForConfirmedCustomer(
									msisdn,
									MIP_CustomerManagementPage.BIMA_ACCIDENT_PROTECTION);
					log.info("Old Offer Level:" + " In Offer_Subscription="
							+ accident_level + " In Update Cover Level="
							+ offer_cover.get("old_offer"));
					Assert.assertTrue(offer_cover.get("old_offer").contains(
							accident_level.replaceAll("[a-zA-Z,\\s]", "")));
					log.info("New Offer Level:" + " In Offer_Subscription="
							+ new_pa_level + " In Update Cover Level="
							+ offer_cover.get("new_offer"));
					Assert.assertTrue(offer_cover.get("new_offer").contains(
							new_pa_level.replaceAll("[a-zA-Z,\\s]", "")));
					log.info("Current Cover Level After Cover Change in Change Cover Level Page:"
							+ " In Dashboard="
							+ current_cover_level
							+ " Input Update Cover Level=" + accident_level);

					Assert.assertTrue(accident_level
							.replaceAll("[a-zA-Z,]", "").contains(
									current_cover_level.replaceAll("\\s", "")));

				} else {
					Assert.assertNotEquals(
							db_offer_level_before_change_level,
							MIP_ChangeCoverLevel_Queries
									.getOfferDetails(
											msisdn,
											MIP_CustomerManagementPage.BIMA_ACCIDENT_PROTECTION));
					Assert.assertFalse(MIP_ChangeCoverLevel_Queries
							.checkRecordInUpdateChangeCoverLevel(
									msisdn,
									MIP_CustomerManagementPage.BIMA_ACCIDENT_PROTECTION));
					Map<String, String> updated_offer_level = MIP_RegisterCustomer_Queries
							.getOfferCover(
									msisdn,
									MIP_CustomerManagementPage.BIMA_ACCIDENT_PROTECTION);
					log.info("Updated Offer Level:" + " In Offer_Subscription="
							+ updated_offer_level.get("accident_cover")
							+ " Input Update Cover Level=" + new_pa_level);

					Assert.assertTrue(new_pa_level.contains(updated_offer_level
							.get("accident_cover")));
					log.info("Current Cover Level After Cover Change in Change Cover Level Page:"
							+ " In Dashboard="
							+ current_cover_level
							+ " Input Update Cover Level=" + new_pa_level);

					Assert.assertTrue(new_pa_level.replaceAll("[a-zA-Z,\\s]",
							"").contains(
							current_cover_level.replaceAll("\\s", "")));
				}

				/*
				 * String cover_level_sms = MIP_ChangeCoverLevel_Queries
				 * .getChangeCoverLevelSMS(msisdn, prd_to_change_level); String
				 * exp_sms =
				 * MIP_ChangeCoverLevel_TestData.ACCIDENT_CHANGE_COVER_LEVEL_SMS
				 * .replaceAll("XXXXX",
				 * accident_level.replaceAll("[a-zA-z,\\s]", "")); exp_sms =
				 * exp_sms.replaceAll("YYYYY",
				 * new_Accident_offer_level.replaceAll("[\\sa-zA-Z]", ""));
				 * log.info("Expected and Found SMS:" + "Expected=" + exp_sms +
				 * " Found SMS=" + cover_level_sms);
				 * Assert.assertTrue(cover_level_sms.replaceAll("\\s", "")
				 * .equalsIgnoreCase(exp_sms.replaceAll("\\s", "")));
				 */
			}
			if (prd_level_change
					.contains(MIP_CustomerManagementPage.BIMA_HOSPITALIZATION)) {
				db_offer_level_before_change_level = MIP_ChangeCoverLevel_Queries
						.getOfferDetails(msisdn,
								MIP_CustomerManagementPage.BIMA_HOSPITALIZATION);
				changecoverlevel.updateHealthCoverLevel(new_hp_level)
						.clickOnSaveChanges();
				Assert.assertTrue(changecoverlevel.getSuccessMessage()
						.equalsIgnoreCase(change_level_succ_msg.trim()));
				changecoverlevel.waitForElementToVisible(By.linkText("here"))
						.click();
				changecoverlevel.enterMSISDN(msisdn);
				changecoverlevel.clickOnSeachIcon();
				String current_cover_level = changecoverlevel
						.getHospitalizationCoverLevelInfo();
				if (conf_status.equals("1")) {
					Assert.assertEquals(
							db_offer_level_before_change_level,
							MIP_ChangeCoverLevel_Queries
									.getOfferDetails(
											msisdn,
											MIP_CustomerManagementPage.BIMA_HOSPITALIZATION));
					Assert.assertTrue(MIP_ChangeCoverLevel_Queries
							.checkRecordInUpdateChangeCoverLevel(
									msisdn,
									MIP_CustomerManagementPage.BIMA_HOSPITALIZATION));
					Map<String, String> offer_cover = MIP_ChangeCoverLevel_Queries
							.getupdateCoverLevelForConfirmedCustomer(
									msisdn,
									MIP_CustomerManagementPage.BIMA_HOSPITALIZATION);
					log.info("Old Offer Level:" + " In Offer_Subscription="
							+ hospital_level + " In Update Cover Level="
							+ offer_cover.get("old_offer"));
					Assert.assertTrue(offer_cover.get("old_offer").contains(
							hospital_level.replaceAll("[a-zA-Z,\\s]", "")));
					log.info("New Offer Level:" + " In Offer_Subscription="
							+ new_hp_level + " In Update Cover Level="
							+ offer_cover.get("new_offer"));
					Assert.assertTrue(offer_cover.get("new_offer").contains(
							new_hp_level.replaceAll("[a-zA-Z,\\s]", "")));
					log.info("Current Cover Level After Cover Change in Change Cover Level Page:"
							+ " In Dashboard="
							+ current_cover_level
							+ " Input Update Cover Level=" + hospital_level);

					Assert.assertTrue(hospital_level
							.replaceAll("[a-zA-Z,]", "").contains(
									current_cover_level.replaceAll("\\s", "")));

				} else {
					Assert.assertNotEquals(
							db_offer_level_before_change_level,
							MIP_ChangeCoverLevel_Queries
									.getOfferDetails(
											msisdn,
											MIP_CustomerManagementPage.BIMA_HOSPITALIZATION));
					Assert.assertFalse(MIP_ChangeCoverLevel_Queries
							.checkRecordInUpdateChangeCoverLevel(
									msisdn,
									MIP_CustomerManagementPage.BIMA_HOSPITALIZATION));
					Map<String, String> updated_offer_level = MIP_RegisterCustomer_Queries
							.getOfferCover(
									msisdn,
									MIP_CustomerManagementPage.BIMA_HOSPITALIZATION);
					log.info("Updated Offer Level:" + " In Offer_Subscription="
							+ updated_offer_level.get("hospitalization_cover")
							+ " Input Update Cover Level=" + new_hp_level);

					Assert.assertTrue(new_hp_level.contains(updated_offer_level
							.get("hospitalization_cover")));
					log.info("Current Cover Level After Cover Change in Change Cover Level Page:"
							+ " In Dashboard="
							+ current_cover_level
							+ " Input Update Cover Level=" + new_hp_level);

					Assert.assertTrue(new_hp_level.replaceAll("[a-zA-Z,\\s]",
							"").contains(
							current_cover_level.replaceAll("\\s", "")));
				}

				/*
				 * String cover_level_sms = MIP_ChangeCoverLevel_Queries
				 * .getChangeCoverLevelSMS(msisdn, prd_to_change_level); String
				 * exp_sms =
				 * MIP_ChangeCoverLevel_TestData.ACCIDENT_CHANGE_COVER_LEVEL_SMS
				 * .replaceAll("XXXXX",
				 * accident_level.replaceAll("[a-zA-z,\\s]", "")); exp_sms =
				 * exp_sms.replaceAll("YYYYY",
				 * new_Accident_offer_level.replaceAll("[\\sa-zA-Z]", ""));
				 * log.info("Expected and Found SMS:" + "Expected=" + exp_sms +
				 * " Found SMS=" + cover_level_sms);
				 * Assert.assertTrue(cover_level_sms.replaceAll("\\s", "")
				 * .equalsIgnoreCase(exp_sms.replaceAll("\\s", "")));
				 */
			}

		} catch (Throwable t) {
			log.info("Change Cover Level---" + testcaseName + " Test Failed");
			log.info("Error occured in the test case", t);
			throw t;
		}
	}

	@Test(priority = 1, enabled = true, testName = "changeCoverLevelExistingCustomer", dataProvider = "changeCoverLevelExistingCustomer", dataProviderClass = MIP_ChangeCoverLevel_TestData.class)
	public void changeCoverLevel_ExistingCustomer(String testcase,
			String msisdn, String prd_level_change, String new_pa_level,
			String new_hp_level, String change_level_succ_msg) throws Throwable {
		testcaseName = testcase + "--" + msisdn;
		try {
			username = MIP_ReadPropertyFile.getPropertyValue("username");
			logger = report.startTest("Change_Cover_Level--" + testcase + "--"
					+ msisdn);
			logger.log(LogStatus.INFO, "Changing the Customer Cover Level");
			homepage.clickOnMenu(MIP_Menu_Constants.CHANGE_COVER_LEVEL);
			changecoverlevel = PageFactory.initElements(
					MIP_Test_Configuration.driver,
					MIP_ChangeCoverLevelPage.class);
			changecoverlevel.waitForElementToVisible(By
					.xpath("//h3[contains(text(),'Change Cover Level')]"));
			changecoverlevel.waitForElementToVisible(By
					.xpath("//div[contains(text(),'All fields marked')]"));
			changecoverlevel.enterMSISDN(msisdn);
			changecoverlevel.clickOnSeachIcon();

			changecoverlevel.waitForElementToVisible(By.id("saveBtn"));
			int db_offer_level_before_change_level = -1;
			if (prd_level_change
					.contains(MIP_CustomerManagementPage.BIMA_ACCIDENT_PROTECTION)) {
				db_offer_level_before_change_level = MIP_ChangeCoverLevel_Queries
						.getOfferDetails(
								msisdn,
								MIP_CustomerManagementPage.BIMA_ACCIDENT_PROTECTION);
				String current_cover_level = changecoverlevel
						.getAccidentCoverLevelInfo();
				changecoverlevel.updateAccidentCoverLevel(new_pa_level)
						.clickOnSaveChanges();
				Assert.assertTrue(changecoverlevel.getSuccessMessage()
						.equalsIgnoreCase(change_level_succ_msg.trim()));
				changecoverlevel.waitForElementToVisible(By.linkText("here"))
						.click();
				changecoverlevel.enterMSISDN(msisdn);
				changecoverlevel.clickOnSeachIcon();
				String conf_status = MIP_DeRegisterCustomer_Queries
						.getConfirmationStatus(
								msisdn,
								MIP_CustomerManagementPage.BIMA_ACCIDENT_PROTECTION);
				if (conf_status
						.equalsIgnoreCase(MIP_CustomerManagementPage.BIMA_CONFIRMATION_STATUS)) {
					Assert.assertEquals(
							db_offer_level_before_change_level,
							MIP_ChangeCoverLevel_Queries
									.getOfferDetails(
											msisdn,
											MIP_CustomerManagementPage.BIMA_ACCIDENT_PROTECTION));
					Assert.assertTrue(MIP_ChangeCoverLevel_Queries
							.checkRecordInUpdateChangeCoverLevel(
									msisdn,
									MIP_CustomerManagementPage.BIMA_ACCIDENT_PROTECTION));
					Map<String, String> offer_cover = MIP_ChangeCoverLevel_Queries
							.getupdateCoverLevelForConfirmedCustomer(
									msisdn,
									MIP_CustomerManagementPage.BIMA_ACCIDENT_PROTECTION);

					log.info("New Offer Level:" + " In Offer_Subscription="
							+ new_pa_level + " In Update Cover Level="
							+ offer_cover.get("new_offer"));
					Assert.assertTrue(offer_cover.get("new_offer").contains(
							new_pa_level.replaceAll("[a-zA-Z,\\s]", "")));
					String current_cover_level_after_levelchange = changecoverlevel
							.getAccidentCoverLevelInfo();
					log.info("Current Cover Level After Cover Change in Change Cover Level Page:"
							+ " In Dashboard before update cover level="
							+ current_cover_level
							+ " Cover Level after update cover level="
							+ current_cover_level_after_levelchange);

					Assert.assertTrue(current_cover_level_after_levelchange
							.equalsIgnoreCase(current_cover_level));

				} else {
					Assert.assertNotEquals(
							db_offer_level_before_change_level,
							MIP_ChangeCoverLevel_Queries
									.getOfferDetails(
											msisdn,
											MIP_CustomerManagementPage.BIMA_ACCIDENT_PROTECTION));
					Assert.assertFalse(MIP_ChangeCoverLevel_Queries
							.checkRecordInUpdateChangeCoverLevel(
									msisdn,
									MIP_CustomerManagementPage.BIMA_ACCIDENT_PROTECTION));
					Map<String, String> updated_offer_level = MIP_RegisterCustomer_Queries
							.getOfferCover(
									msisdn,
									MIP_CustomerManagementPage.BIMA_ACCIDENT_PROTECTION);
					log.info("Updated Offer Level:" + " In Offer_Subscription="
							+ updated_offer_level.get("accident_cover")
							+ " Input Update Cover Level=" + new_pa_level);

					Assert.assertTrue(new_pa_level.contains(updated_offer_level
							.get("accident_cover")));
					log.info("Current Cover Level After Cover Change in Change Cover Level Page:"
							+ " In Dashboard="
							+ current_cover_level
							+ " Input Update Cover Level=" + new_pa_level);

					Assert.assertTrue(new_pa_level.replaceAll("[a-zA-Z,\\s]",
							"").contains(
							current_cover_level.replaceAll("\\s", "")));
				}

				/*
				 * String cover_level_sms = MIP_ChangeCoverLevel_Queries
				 * .getChangeCoverLevelSMS(msisdn, prd_to_change_level); String
				 * exp_sms =
				 * MIP_ChangeCoverLevel_TestData.ACCIDENT_CHANGE_COVER_LEVEL_SMS
				 * .replaceAll("XXXXX",
				 * accident_level.replaceAll("[a-zA-z,\\s]", "")); exp_sms =
				 * exp_sms.replaceAll("YYYYY",
				 * new_Accident_offer_level.replaceAll("[\\sa-zA-Z]", ""));
				 * log.info("Expected and Found SMS:" + "Expected=" + exp_sms +
				 * " Found SMS=" + cover_level_sms);
				 * Assert.assertTrue(cover_level_sms.replaceAll("\\s", "")
				 * .equalsIgnoreCase(exp_sms.replaceAll("\\s", "")));
				 */
			}
			if (prd_level_change
					.contains(MIP_CustomerManagementPage.BIMA_HOSPITALIZATION)) {
				db_offer_level_before_change_level = MIP_ChangeCoverLevel_Queries
						.getOfferDetails(msisdn,
								MIP_CustomerManagementPage.BIMA_HOSPITALIZATION);
				changecoverlevel.updateHealthCoverLevel(new_hp_level)
						.clickOnSaveChanges();
				Assert.assertTrue(changecoverlevel.getSuccessMessage()
						.equalsIgnoreCase(change_level_succ_msg.trim()));
				changecoverlevel.waitForElementToVisible(By.linkText("here"))
						.click();
				changecoverlevel.enterMSISDN(msisdn);
				changecoverlevel.clickOnSeachIcon();
				String current_cover_level = changecoverlevel
						.getHospitalizationCoverLevelInfo();
				String conf_status = MIP_DeRegisterCustomer_Queries
						.getConfirmationStatus(
								msisdn,
								MIP_CustomerManagementPage.BIMA_ACCIDENT_PROTECTION);
				if (conf_status
						.equalsIgnoreCase(MIP_CustomerManagementPage.BIMA_CONFIRMATION_STATUS)) {
					Assert.assertEquals(
							db_offer_level_before_change_level,
							MIP_ChangeCoverLevel_Queries
									.getOfferDetails(
											msisdn,
											MIP_CustomerManagementPage.BIMA_HOSPITALIZATION));
					Assert.assertTrue(MIP_ChangeCoverLevel_Queries
							.checkRecordInUpdateChangeCoverLevel(
									msisdn,
									MIP_CustomerManagementPage.BIMA_HOSPITALIZATION));
					Map<String, String> offer_cover = MIP_ChangeCoverLevel_Queries
							.getupdateCoverLevelForConfirmedCustomer(
									msisdn,
									MIP_CustomerManagementPage.BIMA_HOSPITALIZATION);

					log.info("New Offer Level:" + " In Offer_Subscription="
							+ new_hp_level + " In Update Cover Level="
							+ offer_cover.get("new_offer"));
					Assert.assertTrue(offer_cover.get("new_offer").contains(
							new_hp_level.replaceAll("[a-zA-Z,\\s]", "")));
					String current_cover_level_after_levelchange = changecoverlevel
							.getHospitalizationCoverLevelInfo();
					log.info("Current Cover Level After Cover Change in Change Cover Level Page:"
							+ " In Dashboard="
							+ current_cover_level
							+ " Input Update Cover Level="
							+ current_cover_level_after_levelchange);

					Assert.assertTrue(current_cover_level_after_levelchange
							.equalsIgnoreCase(current_cover_level));

				} else {
					Assert.assertNotEquals(
							db_offer_level_before_change_level,
							MIP_ChangeCoverLevel_Queries
									.getOfferDetails(
											msisdn,
											MIP_CustomerManagementPage.BIMA_HOSPITALIZATION));
					Assert.assertFalse(MIP_ChangeCoverLevel_Queries
							.checkRecordInUpdateChangeCoverLevel(
									msisdn,
									MIP_CustomerManagementPage.BIMA_HOSPITALIZATION));
					Map<String, String> updated_offer_level = MIP_RegisterCustomer_Queries
							.getOfferCover(
									msisdn,
									MIP_CustomerManagementPage.BIMA_HOSPITALIZATION);
					log.info("Updated Offer Level:" + " In Offer_Subscription="
							+ updated_offer_level.get("hospitalization_cover")
							+ " Input Update Cover Level=" + new_hp_level);

					Assert.assertTrue(new_hp_level.contains(updated_offer_level
							.get("hospitalization_cover")));
					log.info("Current Cover Level After Cover Change in Change Cover Level Page:"
							+ " In Dashboard="
							+ current_cover_level
							+ " Input Update Cover Level=" + new_hp_level);

					Assert.assertTrue(new_hp_level.replaceAll("[a-zA-Z,\\s]",
							"").contains(
							current_cover_level.replaceAll("\\s", "")));
				}

				/*
				 * String cover_level_sms = MIP_ChangeCoverLevel_Queries
				 * .getChangeCoverLevelSMS(msisdn, prd_to_change_level); String
				 * exp_sms =
				 * MIP_ChangeCoverLevel_TestData.ACCIDENT_CHANGE_COVER_LEVEL_SMS
				 * .replaceAll("XXXXX",
				 * accident_level.replaceAll("[a-zA-z,\\s]", "")); exp_sms =
				 * exp_sms.replaceAll("YYYYY",
				 * new_Accident_offer_level.replaceAll("[\\sa-zA-Z]", ""));
				 * log.info("Expected and Found SMS:" + "Expected=" + exp_sms +
				 * " Found SMS=" + cover_level_sms);
				 * Assert.assertTrue(cover_level_sms.replaceAll("\\s", "")
				 * .equalsIgnoreCase(exp_sms.replaceAll("\\s", "")));
				 */
			}
		} catch (Exception e) {

		}
	}

	@AfterMethod(alwaysRun = true)
	public void after_test(ITestResult res) {

		if (res.getStatus() == ITestResult.FAILURE) {
			String path = MIP_ScreenShots.takeScreenShot(
					MIP_Test_Configuration.driver, res.getName() + "_"
							+ testcaseName);
			logger.log(LogStatus.FAIL, "----Test Failed");
			logger.log(LogStatus.ERROR, res.getThrowable());
			logger.log(LogStatus.FAIL, logger.addScreenCapture(path));
			homepage.clickOnMenu(MIP_Menu_Constants.CHANGE_COVER_LEVEL);
		} else {
			logger.log(LogStatus.PASS, testcaseName + "----Test passed");
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
