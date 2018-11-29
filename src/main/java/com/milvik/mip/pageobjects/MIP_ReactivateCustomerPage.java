package com.milvik.mip.pageobjects;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.milvik.mip.pageutil.MIP_CustomerManagementPage;
import com.milvik.mip.utility.MIP_Logging;

public class MIP_ReactivateCustomerPage extends MIP_CustomerManagementPage {
	public MIP_ReactivateCustomerPage(WebDriver driver) {
		super(driver);
	}

	private static Logger logger;
	static {
		logger = MIP_Logging.logDetails("MIP_ReactivateCustomerPage");
	}

	public List<String> getProductForReactivation() {
		logger.info("Getting products for reactivation in Reactivate Customer Page");
		List<String> product = new ArrayList<String>();
		List<WebElement> ele = driver.findElements(By
				.xpath("//input[@id='offerId'][@value='1']"));
		if (ele.size() != 0) {
			product.add(MIP_CustomerManagementPage.BIMA_ACCIDENT_PROTECTION);
		}
		ele = driver.findElements(By
				.xpath("//input[@id='offerId'][@value='2']"));
		if (ele.size() != 0) {
			product.add(MIP_CustomerManagementPage.BIMA_HOSPITALIZATION);
		}
		return product;
	}

	public void selectProductForReactivation(String offer) {
		logger.info("Selecting  product for reactivation " + offer);
		if (offer.contains(MIP_CustomerManagementPage.BIMA_ACCIDENT_PROTECTION)) {
			this.waitForElementTobeClickable(By
					.xpath("//input[@id='offerId'][@value='1']"));
			this.waitForElementToVisible(
					By.xpath("//input[@id='offerId'][@value='1']")).click();
		}
		if (offer.contains(MIP_CustomerManagementPage.BIMA_HOSPITALIZATION)) {
			this.waitForElementTobeClickable(By
					.xpath("//input[@id='offerId'][@value='2']"));
			this.waitForElementToVisible(
					By.xpath("//input[@id='offerId'][@value='2']")).click();
		}
		logger.info("Selected the offer " + offer);
	}

	public MIP_ReactivateCustomerPage selectBimaAccidentProtecionBenefitLevelForReactivation(
			String benefitLevel) {
		logger.info("Selecting Bima Accident protection Benefit Level  for reactivation as"
				+ benefitLevel);
		WebElement ele = this.waitForElementToVisible(By.id("newOfferCoverId"));
		this.selectDropDownbyText(ele, benefitLevel);
		logger.info("Selected Bima Accident protection Benefit Level for reactivation as "
				+ benefitLevel);
		return this;
	}

	public MIP_ReactivateCustomerPage selectBimaHospitalizationBenefitLevelForReactivation(
			String benefitLevel) {
		logger.info("Selecting Bima Hospitalization Benefit Level  for reactivation as"
				+ benefitLevel);
		WebElement ele = this.waitForElementToVisible(By
				.id("newOfferCoverIdHP"));
		this.selectDropDownbyText(ele, benefitLevel);
		logger.info("Selected Bima Hospitalization Benefit Level for reactivation as "
				+ benefitLevel);
		return this;
	}

}
