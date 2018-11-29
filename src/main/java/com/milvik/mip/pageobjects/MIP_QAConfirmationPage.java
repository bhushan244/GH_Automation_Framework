package com.milvik.mip.pageobjects;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.milvik.mip.pageutil.MIP_CustomerManagementPage;
import com.milvik.mip.utility.MIP_Logging;

public class MIP_QAConfirmationPage extends MIP_CustomerManagementPage {
	private static Logger logger;
	static {
		logger = MIP_Logging.logDetails("MIP_QAConfirmationPage");
	}

	public MIP_QAConfirmationPage(WebDriver driver) {
		super(driver);
	}

	public Map<String, String> getCustomerInformation() {
		logger.info("Getting customer Information from QA Confirmation Page");
		Map<String, String> cust_details = new HashMap<String, String>();
		cust_details
				.put("cust_fname",
						this.waitForElementToVisible(
								By.xpath("//input[@id='custName'][@readonly='readonly']"))
								.getAttribute("value"));
		cust_details.put(
				"cust_sname",
				this.waitForElementToVisible(
						By.xpath("//input[@id='sname'][@readonly='readonly']"))
						.getAttribute("value"));
		cust_details.put(
				"cust_age",
				this.waitForElementToVisible(
						By.xpath("//input[@id='age'][@readonly='readonly']"))
						.getAttribute("value"));
		cust_details.put(
				"cust_dob",
				this.waitForElementToVisible(
						By.xpath("//input[@id='dob'][@readonly='readonly']"))
						.getAttribute("value"));
		cust_details.put(
				"cust_nic",
				this.waitForElementToVisible(
						By.xpath("//input[@id='cnic'][@readonly='readonly']"))
						.getAttribute("value"));
		this.waitForElementToVisible(By.id("backBtn")).isDisplayed();
		this.waitForElementToVisible(By.id("confirmBtn")).isDisplayed();
		return cust_details;

	}

	public Map<String, String> getPABenInformation() {
		logger.info("Getting PA Beneficiary  Information from QA Confirmation Page");
		Map<String, String> cust_details = new HashMap<String, String>();
		cust_details
				.put("pa_relationship",
						this.waitForElementToVisible(
								By.xpath("//input[@id='insRelation'][@readonly='readonly']"))
								.getAttribute("value"));
		cust_details
				.put("pa_ben_fname",
						this.waitForElementToVisible(
								By.xpath("//input[@id='insRelName'][@readonly='readonly']"))
								.getAttribute("value"));
		cust_details
				.put("pa_ben_sname",
						this.waitForElementToVisible(
								By.xpath("//input[@id='insRelSurName'][@readonly='readonly']"))
								.getAttribute("value"));
		cust_details
				.put("pa_ben_age",
						this.waitForElementToVisible(
								By.xpath("//input[@id='insRelAge'][@readonly='readonly']"))
								.getAttribute("value"));
		cust_details
				.put("pa_coverlevel",
						this.waitForElementToVisible(
								By.xpath("//input[@id='coverLevel'][@readonly='readonly']"))
								.getAttribute("value"));
		cust_details
				.put("pa_reg_by",
						this.waitForElementToVisible(
								By.xpath("//input[@id='createdBy'][@readonly='readonly']"))
								.getAttribute("value"));
		cust_details.put(
				"pa_agent_id",
				this.waitForElementToVisible(
						By.xpath("//input[@id='regBy'][@readonly='readonly']"))
						.getAttribute("value"));
		cust_details
				.put("pa_reg_date",
						this.waitForElementToVisible(
								By.xpath("//input[@id='regDate'][@readonly='readonly']"))
								.getAttribute("value"));
		cust_details
				.put("pa_conf_status",
						this.waitForElementToVisible(
								By.xpath("//div[@id='paOfferInfo']//div[contains(text(),'Confimation Status')]//following-sibling::div/input"))
								.getAttribute("value"));
		return cust_details;

		/*
		 * cust_details .put("coaching_program", this.waitForElementToVisible(
		 * By
		 * .xpath("//input[@id='healthTipSMSFrequency'][@readonly='readonly']"))
		 * .getAttribute("value")); cust_details .put("date_program",
		 * this.waitForElementToVisible(
		 * By.xpath("//input[@id='healthTipDate'][@readonly='readonly']"))
		 * .getAttribute("value")); cust_details .put("coaching_program_status",
		 * this.waitForElementToVisible(
		 * By.xpath("//input[@id='healthTipStatus'][@readonly='readonly']"))
		 * .getAttribute("value"));
		 */
	}

	public Map<String, String> getHPBenInformation() {
		logger.info("Getting HP Beneficiary  Information from QA Confirmation Page");
		Map<String, String> cust_details = new HashMap<String, String>();
		cust_details
				.put("hp_relationship",
						this.waitForElementToVisible(
								By.xpath("//input[@id='insRelationHP'][@readonly='readonly']"))
								.getAttribute("value"));
		cust_details
				.put("hp_ben_fname",
						this.waitForElementToVisible(
								By.xpath("//input[@id='insRelNameHP'][@readonly='readonly']"))
								.getAttribute("value"));
		cust_details
				.put("hp_ben_sname",
						this.waitForElementToVisible(
								By.xpath("//input[@id='insRelSurnameHP'][@readonly='readonly']"))
								.getAttribute("value"));
		cust_details
				.put("hp_ben_age",
						this.waitForElementToVisible(
								By.xpath("//input[@id='insRelAgeHP'][@readonly='readonly']"))
								.getAttribute("value"));
		cust_details
				.put("hp_coverlevel",
						this.waitForElementToVisible(
								By.xpath("//input[@id='coverLevelHP'][@readonly='readonly']"))
								.getAttribute("value"));
		cust_details
				.put("hp_reg_by",
						this.waitForElementToVisible(
								By.xpath("//input[@id='createdByHP'][@readonly='readonly']"))
								.getAttribute("value"));
		cust_details
				.put("hp_agent_id",
						this.waitForElementToVisible(
								By.xpath("//input[@id='regByHP'][@readonly='readonly']"))
								.getAttribute("value"));
		cust_details
				.put("hp_reg_date",
						this.waitForElementToVisible(
								By.xpath("//input[@id='regDateHP'][@readonly='readonly']"))
								.getAttribute("value"));
		cust_details
				.put("hp_conf_status",
						this.waitForElementToVisible(
								By.xpath("//div[@id='hpOfferInfo']//div[contains(text(),'Confimation Status')]//following-sibling::div/input"))
								.getAttribute("value"));
		return cust_details;

	}

	public String getWaridConfirmationSuccessMessage() {
		WebDriverWait w = new WebDriverWait(driver, 50);
		return w.until(
				ExpectedConditions.visibilityOfElementLocated(By
						.id("errormessage_div"))).getText();
	}

	public MIP_QAConfirmationPage clickOnConfirmButton() {
		logger.info("Clicking on Confirm Button");
		this.waitForElementTobeClickable(By.id("confirmBtn")).click();
		return this;
	}

}
