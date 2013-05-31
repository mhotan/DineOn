package uw.cse.dineon.library.test;

import uw.cse.dineon.library.util.CreditCardValidator;
import uw.cse.dineon.library.util.DineOnConstants;
import android.test.AndroidTestCase;

/**
 * Tests the CreditCardValidator class to test credit card
 * validation for the Restaurant App Create Account Activity
 * 
 * Black-Box testing
 * @author glee23
 */
public class CreditCardValidatorTest extends AndroidTestCase {

	private String validThreeDigitSecurityCodeCreditCard = "4111111111111111";
	private String validFourDigitSecurityCodeCreditCard = "378282246310005";
	private String validThirteenDigitCreditCard = "4222222222222";
	private String validFourteenDigitCreditCard = "30569309025904";
	private String validThreeDigitSecurityCode = "411";
	private String validFourDigitSecurityCode = "4111";
	private String earlierMonth = "04";
	private String laterMonth = "12";
	private String thisYear = "2013";
	private String earlierYear = "2012";
	private String laterYear = "2017";
	private String validZip = "98105";
	
	@Override
	protected void setUp() {
		DineOnConstants.DEBUG = false;
	}
	
	/**
	 * Asserts that a valid 13 digit credit card is recognized as valid.
	 */
	public void testPassThirteenDigitValidThreeDigitSecurityCodeCreditCardNumber() {
		assertTrue(CreditCardValidator.isValidCreditCard(
				validThirteenDigitCreditCard, validThreeDigitSecurityCode, 
				laterMonth, laterYear, validZip));
	}
	
	/**
	 * Asserts that a valid 14 digit credit card is recognized as valid.
	 */
	public void testPassFourteenDigitValidThreeDigitSecurityCodeCreditCardNumber() {
		assertTrue(CreditCardValidator.isValidCreditCard(
				validFourteenDigitCreditCard, validThreeDigitSecurityCode, 
				laterMonth, laterYear, validZip));
	}

	/**
	 * Asserts that a valid 15 digit credit card is recognized as valid.
	 */
	public void testPassFifteenDigitValidFourDigitSecurityCodeCreditCardNumber() {
		assertTrue(CreditCardValidator.isValidCreditCard(
				validFourDigitSecurityCodeCreditCard, validFourDigitSecurityCode, 
				laterMonth, laterYear, validZip));
	}
	
	/**
	 * Asserts that a valid 16 digit credit card is recognized as valid.
	 */
	public void testPassSixteenDigitValidThreeDigitSecurityCodeCreditCardNumber() {
		assertTrue(CreditCardValidator.isValidCreditCard(
				validThreeDigitSecurityCodeCreditCard, validThreeDigitSecurityCode, 
				laterMonth, laterYear, validZip));
	}

	/**
	 * Asserts that all null values isn't recognized as valid.
	 */
	public void testFailNullAll() {
		assertFalse(CreditCardValidator.isValidCreditCard(null, null, 
				null, null, null));
	}

	/**
	 * Asserts that a null credit card number isn't recognized as valid.
	 */
	public void testFailNullCreditCardNumber() {
		assertFalse(CreditCardValidator.isValidCreditCard(
				null, validThreeDigitSecurityCode, laterMonth, laterYear, validZip));
	}

	/**
	 * Asserts that a null security code isn't recognized as valid.
	 */
	public void testFailNullSecurityCode() {
		assertFalse(CreditCardValidator.isValidCreditCard(
				validThreeDigitSecurityCodeCreditCard, null, 
				laterMonth, laterYear, validZip));
	}

	/**
	 * Asserts that a null month isn't recognized as valid.
	 */
	public void testFailNullMonth() {
		assertFalse(CreditCardValidator.isValidCreditCard(
				validThreeDigitSecurityCodeCreditCard, validThreeDigitSecurityCode,
				null, laterYear, validZip));
	}

	/**
	 * Asserts that a null year isn't recognized as valid.
	 */
	public void testFailNullYear() {
		assertFalse(CreditCardValidator.isValidCreditCard(
				validThreeDigitSecurityCodeCreditCard, validThreeDigitSecurityCode, 
				laterMonth, null, validZip));
	}

	/**
	 * Asserts that a null zip code isn't recognized as valid.
	 */
	public void testFailNullZip() {
		assertFalse(CreditCardValidator.isValidCreditCard(
				validThreeDigitSecurityCodeCreditCard, validThreeDigitSecurityCode, 
				laterMonth, laterYear, null));
	}

	/**
	 * Asserts that all empty values isn't recognized as valid.
	 */
	public void testFailAllEmpty() {
		assertFalse(CreditCardValidator.isValidCreditCard("", "", "", "", ""));
	}

	/**
	 * Asserts that an empty credit card number isn't recognized as valid.
	 */
	public void testFailEmptyCreditCardNumber() {
		assertFalse(CreditCardValidator.isValidCreditCard(
				"", validThreeDigitSecurityCode, laterMonth, laterYear, validZip));
	}

	/**
	 * Asserts that an empty security code isn't recognized as valid.
	 */
	public void testFailEmptySecurityCode() {
		assertFalse(CreditCardValidator.isValidCreditCard(
				validThreeDigitSecurityCodeCreditCard, "", laterMonth, laterYear, validZip));
	}

	/**
	 * Asserts that an empty month isn't recognized as valid.
	 */
	public void testFailEmptyMonth() {
		assertFalse(CreditCardValidator.isValidCreditCard(
				validThreeDigitSecurityCodeCreditCard, validThreeDigitSecurityCode, 
				"", laterYear, validZip));
	}

	/**
	 * Asserts that an empty year isn't recognized as valid.
	 */
	public void testFailEmptyYear() {
		assertFalse(CreditCardValidator.isValidCreditCard(
				validThreeDigitSecurityCodeCreditCard, validThreeDigitSecurityCode, 
				laterMonth, "", validZip));
	}

	/**
	 * Asserts that an empty zip code isn't recognized as valid.
	 */
	public void testFailEmptyZip() {
		assertFalse(CreditCardValidator.isValidCreditCard(
				validThreeDigitSecurityCodeCreditCard, validThreeDigitSecurityCode, 
				laterMonth, laterYear, ""));
	}
	
	/**
	 * Asserts that an too short zip code isn't recognized as valid.
	 */
	public void testFailInvalidShortZip() {
		assertFalse(CreditCardValidator.isValidCreditCard(
				validThreeDigitSecurityCodeCreditCard, validThreeDigitSecurityCode, 
				laterMonth, laterYear, "9999"));
	}
	
	/**
	 * Asserts that a too long zip code isn't recognized as valid.
	 */
	public void testFailInvalidLongZip() {
		assertFalse(CreditCardValidator.isValidCreditCard(
				validThreeDigitSecurityCodeCreditCard, validThreeDigitSecurityCode, 
				laterMonth, laterYear, "999999"));
	}
	
	/**
	 * Asserts that an valid credit card number with a 
	 * mismatched security code isn't recognized as valid.
	 */
	public void testFailValidThreeDigitSecurityCodeCreditCardNumberFourDigitSecurityCode() {
		assertFalse(CreditCardValidator.isValidCreditCard(
				validThreeDigitSecurityCodeCreditCard, validFourDigitSecurityCode, 
				laterMonth, laterYear, validZip));
	}
	
	/**
	 * Asserts that an valid credit card number with a 
	 * mismatched security code isn't recognized as valid.
	 */
	public void testFailValidFourDigitSecurityCodeCreditCardNumberThreeDigitSecurityCode() {
		assertFalse(CreditCardValidator.isValidCreditCard(
				validFourDigitSecurityCodeCreditCard, validThreeDigitSecurityCode, 
				laterMonth, laterYear, validZip));
	}
	
	/**
	 * Asserts that an expired credit card number isn't recognized as valid.
	 */
	public void testFailOldYear() {
		assertFalse(CreditCardValidator.isValidCreditCard(
				validThreeDigitSecurityCodeCreditCard, validThreeDigitSecurityCode, 
				laterMonth, earlierYear, validZip));
	}
	
	/**
	 * Asserts that an expired credit card number isn't recognized as valid.
	 */
	public void testFailSameYearOldMonth() {
		assertFalse(CreditCardValidator.isValidCreditCard(
				validThreeDigitSecurityCodeCreditCard, validThreeDigitSecurityCode, 
				earlierMonth, thisYear, validZip));
	}
	
	/**
	 * Asserts that a 13 digit non credit card number isn't recognized as valid.
	 */
	public void testFailThirteenDigitNotCreditCardNumber() {
		assertFalse(CreditCardValidator.isValidCreditCard(
				"1234567890123", validThreeDigitSecurityCode, laterMonth, 
				laterYear, validZip));
	}
	
	/**
	 * Asserts that a 14 digit non credit card number isn't recognized as valid.
	 */
	public void testFailFourteenDigitNotCreditCardNumber() {
		assertFalse(CreditCardValidator.isValidCreditCard(
				"12345678901234", validThreeDigitSecurityCode, laterMonth, 
				laterYear, validZip));
	}
	
	/**
	 * Asserts that a 15 digit non credit card number isn't recognized as valid.
	 */
	public void testFailFifteenDigitNotCreditCardNumber() {
		assertFalse(CreditCardValidator.isValidCreditCard(
				"123456789012345", validFourDigitSecurityCode, laterMonth, 
				laterYear, validZip));
	}
	
	/**
	 * Asserts that a 16 digit non credit card number isn't recognized as valid.
	 */
	public void testFailSixteenDigitNotCreditCardNumber() {
		assertFalse(CreditCardValidator.isValidCreditCard(
				"1234567890123456", validThreeDigitSecurityCode, laterMonth, 
				laterYear, validZip));
	}
}
