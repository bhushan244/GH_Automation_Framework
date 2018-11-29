package com.milvik.mip.pageobjects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import com.milvik.mip.pageutil.MIP_CustomerManagementPage;
import com.milvik.mip.utility.MIP_Logging;

public class MIP_DeRegisterCustomerPage extends MIP_CustomerManagementPage {
	private WebDriver driver;
	private static Logger logger;
	static {
		logger = MIP_Logging.logDetails("MIP_DeRegisterCustomerPage");
	}

	public MIP_DeRegisterCustomerPage(WebDriver driver) {
		super(driver);
		this.driver = driver;
	}

	public Map<String, String> getPACustomerInformation() {
		Map<String, String> dereg_details = new HashMap<String, String>();
		logger.info("Validating PA information on De-Registration Page");
		this.waitForElementToVisible(By
				.xpath("//h3[contains(text(),'De-Register Customer')]"));
		dereg_details.put("pa_cust_name",
				this.waitForElementToVisible(By.id("customerName"))
						.getAttribute("value"));
		dereg_details.put("pa_cust_msisdn",
				this.waitForElementToVisible(By.id("msisdNumber"))
						.getAttribute("value"));
		dereg_details.put("pa_cust_offer",
				this.waitForElementToVisible(By.id("customerOfferCover"))
						.getAttribute("value"));
		dereg_details.put("pa_deducted_amnt",
				this.waitForElementToVisible(By.id("deductedOfferAmount"))
						.getAttribute("value"));
		dereg_details.put("pa_cover_earned",
				this.waitForElementToVisible(By.id("earnedCover"))
						.getAttribute("value"));
		dereg_details.put(
				"pa_conf_status",
				this.waitForElementToVisible(By.id("confirmed")).getAttribute(
						"value"));
		boolean bk_val = false;
		List<WebElement> ele = driver.findElements(By
				.xpath("//*[contains(@id,'backBtn')]"));
		for (WebElement e : ele) {
			if (e.isDisplayed()) {
				bk_val = true;

			}
		}
		if (bk_val == false) {
			logger.info("Back button is not present in De-Register Page");
			Assert.assertTrue(bk_val);
		}

		this.waitForElementToPresent(
				By.xpath("//li[@id='saveBtn']/span[contains(text(),'De-Register')] | //li[@id='saveBtnHP']/span[contains(text(),'De-Register')]"))
				.isDisplayed();

		return dereg_details;
	}

	public Map<String, String> getHPCustomerInformation() {
		Map<String, String> dereg_details = new HashMap<String, String>();
		logger.info("Validating HP Information on De-Registration Page");
		dereg_details.put("hp_cust_name",
				this.waitForElementToVisible(By.id("customerNameHP"))
						.getAttribute("value"));
		dereg_details.put("hp_cust_msisdn",
				this.waitForElementToVisible(By.id("msisdNumberHP"))
						.getAttribute("value"));
		dereg_details.put("hp_cust_offer",
				this.waitForElementToVisible(By.id("customerOfferCoverHP"))
						.getAttribute("value"));
		dereg_details.put("hp_deducted_amnt",
				this.waitForElementToVisible(By.id("deductedOfferAmountHP"))
						.getAttribute("value"));
		dereg_details.put("hp_cover_earned",
				this.waitForElementToVisible(By.id("earnedCoverHP"))
						.getAttribute("value"));
		dereg_details.put("hp_conf_status",
				this.waitForElementToVisible(By.id("confirmedHP"))
						.getAttribute("value"));
		boolean bk_val = false;
		List<WebElement> ele = driver.findElements(By
				.xpath("//*[contains(@id,'backBtn')]"));
		for (WebElement e : ele) {
			if (e.isDisplayed()) {
				bk_val = true;

			}
		}
		if (bk_val == false) {
			logger.info("Back button is not present in De-Register Page");
			Assert.assertTrue(bk_val);
		}

		this.waitForElementToPresent(
				By.xpath("//li[@id='saveBtn']/span[contains(text(),'De-Register')] | //li[@id='saveBtnHP']/span[contains(text(),'De-Register')]"))
				.isDisplayed();

		return dereg_details;
	}

	public List<String> getProductOptions() {
		List<String> product = new ArrayList<String>();
		if (driver.findElements(By.xpath("//input[@id='accidentProtectPaHp']"))
				.size() != 0) {
			product.add(MIP_CustomerManagementPage.BIMA_ACCIDENT_PROTECTION_LIKE_DB
					.trim());
		}
		if (driver.findElements(By.xpath("//input[@id='hospitalPaHp']")).size() != 0) {
			product.add(MIP_CustomerManagementPage.BIMA_HOSPITALIZATION.trim());
		}

		return product;
	}

	public boolean validateCustomerInfoTableHeading() {
		this.waitForElementToVisible(By.id("customerDetailsList"));
		String xpath = "//table[@id='customerDetailsList']/thead/tr/th[contains(text(),'";
		if (this.waitForElementToVisible(By.xpath(xpath + "Customer Name')]"))
				.isDisplayed()
				&& this.waitForElementToVisible(By.xpath(xpath + "MSISDN')]"))
						.isDisplayed()
				&& this.waitForElementToVisible(
						By.xpath(xpath + "Confirmation Status')]"))
						.isDisplayed()
				&& this.waitForElementToVisible(By.xpath(xpath + "Product ')]"))
						.isDisplayed()
				&& this.waitForElementToVisible(
						By.xpath(xpath + "Registered Product Level (Dollar)')]"))
						.isDisplayed()
				&& this.waitForElementToVisible(
						By.xpath(xpath
								+ "Deducted Amount (as on date)(Dollar) ')]"))
						.isDisplayed()
				&& this.waitForElementToVisible(
						By.xpath(xpath
								+ "bimalife Cover Earned in the current month (Dollar) ')]"))
						.isDisplayed()
				&& this.waitForElementToVisible(
						By.xpath(xpath
								+ "bimahealth  Cover Earned in the current month (Dollar) ')]"))
						.isDisplayed()) {
			return true;

		}
		return false;
	}

	public int getTableHeadingIndex(String heading) {
		int count = -1;
		this.waitForElementToVisible(By.id("customerDetailsList"));
		List<WebElement> index = driver.findElements(By
				.xpath("//table[@id='customerDetailsList']/thead/tr/th"));
		for (int i = 0; i < index.size(); i++) {
			if (index.get(i).getText().equalsIgnoreCase(heading)) {
				return i + 1;

			}
		}
		return count;
	}

	public int getProductRow(String offer) {
		int count = -1;
		this.waitForElementToVisible(By.id("customerDetailsList"));
		List<WebElement> index = driver.findElements(By
				.xpath("//table[@id='customerDetailsList']/tbody//tr//td["
						+ getTableHeadingIndex("Product") + "]"));
		for (int i = 0; i < index.size(); i++) {
			if (index.get(i).getText().equalsIgnoreCase(offer)) {
				return i + 1;

			}
		}
		return count;
	}

	public Map<String, String> getCustomerInfoBeforeDereg(String offer) {
		Map<String, String> details = new HashMap<String, String>();
		this.waitForElementToVisible(By.id("customerDetailsList"));
		int row_count = getProductRow(offer);
		details.put(
				"cust_name",
				this.waitForElementToVisible(
						By.xpath("//table[@id='customerDetailsList']/tbody//tr["
								+ row_count
								+ "]//td["
								+ getTableHeadingIndex("Customer Name") + "]"))
						.getText());

		details.put(
				"offer_level",
				this.waitForElementToVisible(
						By.xpath("//table[@id='customerDetailsList']/tbody//tr["
								+ row_count
								+ "]//td["
								+ getTableHeadingIndex("Registered Product Level (Dollar)")
								+ "]")).getText());
		details.put(
				"confirmation_Status",
				this.waitForElementToVisible(
						By.xpath("//table[@id='customerDetailsList']/tbody//tr["
								+ row_count
								+ "]//td["
								+ getTableHeadingIndex("Confirmation Status")
								+ "]")).getText());
		details.put(
				"deduted_amount",
				this.waitForElementToVisible(
						By.xpath("//table[@id='customerDetailsList']/tbody//tr["
								+ row_count
								+ "]//td["
								+ getTableHeadingIndex("Deducted Amount (as on date)(Dollar)")
								+ "]")).getText());
		details.put(
				"bimalife_cover",
				this.waitForElementToVisible(
						By.xpath("//table[@id='customerDetailsList']/tbody//tr["
								+ row_count
								+ "]//td["
								+ getTableHeadingIndex("bimalife Cover Earned in the current month (Dollar)")
								+ "]")).getText());
		details.put(
				"bimahealth_cover",
				this.waitForElementToVisible(
						By.xpath("//table[@id='customerDetailsList']/tbody//tr["
								+ row_count
								+ "]//td["
								+ getTableHeadingIndex("bimahealth Cover Earned in the current month (Dollar)")
								+ "]")).getText());
		return details;
	}

	public MIP_DeRegisterCustomerPage selectAvailableOffersToDeregister(
			String product) throws InterruptedException {
		logger.info("Selecting available offers to deregister");
		if (product
				.toUpperCase()
				.trim()
				.contains(
						MIP_CustomerManagementPage.BIMA_ACCIDENT_PROTECTION
								.toUpperCase())) {
			this.waitForElementToVisible(By.id("accidentProtectPaHp"));
			this.waitForElementTobeClickable(By.id("accidentProtectPaHp"))
					.click();
		}
		if (product
				.toUpperCase()
				.trim()
				.contains(
						MIP_CustomerManagementPage.BIMA_HOSPITALIZATION
								.toUpperCase())) {
			this.waitForElementToVisible(By.id("hospitalPaHp"));
			this.waitForElementTobeClickable(By.id("hospitalPaHp")).click();
		}
		logger.info("Selected available offers to de-register");
		return this;
	}

	public MIP_DeRegisterCustomerPage clickOnDeregisterButton() {

		List<WebElement> ele = driver.findElements(By
				.xpath("//span[contains(text(),'De-Register')]"));
		for (WebElement e : ele) {
			if (e.isDisplayed()) {
				e.click();
				break;
			}
		}
		return this;
	}

	public String getDeregSuccessMessage() {

		WebDriverWait w = new WebDriverWait(driver, 60);
		w.until(ExpectedConditions.visibilityOfElementLocated(By
				.id("message_div")));
		return this.waitForElementToVisible(By.id("message_div")).getText();

	}
}
