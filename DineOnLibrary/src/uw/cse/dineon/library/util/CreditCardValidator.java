package uw.cse.dineon.library.util;

import java.util.Calendar;

/**
 * Validates credit card numbers.
 * 
 * @author Andy Frey -- modified by glee23 
 */
public final class CreditCardValidator {

	private static final int MAX_YEAR = 2099;
	
	/**
	 * Empty Constructor for CheckStyle.
	 * 
	 */
	private CreditCardValidator() { }
	
	/**
	 * Filters out non-digit characters.
	 * 
	 * @param s String to extract the digits from
	 * @return the digits from the string
	 */
	private static String getDigitsOnly(String s) {
		StringBuffer digitsOnly = new StringBuffer();
		char c;
		for (int i = 0; i < s.length(); i++) {
			c = s.charAt(i);
			if (Character.isDigit(c)) {
				digitsOnly.append(c);
			}
		}
		return digitsOnly.toString();
	}

	/**
	 * Performs the Luhn validation check.
	 * 
	 * @param cardNumber the credit card number
	 * @param securityCode the security code
	 * @param expMo the expiration month
	 * @param expYr the expiration year
	 * @param zip the zip code
	 * @return true if the card number is a valid credit card number
	 */
	public static boolean isValidCreditCard(String cardNumber, 
			String securityCode, String expMo, String expYr, String zip) {
		if(cardNumber == null || securityCode == null 
				|| expMo == null || expYr == null 
				|| zip == null) {
			return false;
		}

		if(DineOnConstants.DEBUG) {
			return true;
		}
		
		return isValidNumber(cardNumber) 
				&& isValidSecurityCode(cardNumber, securityCode)
				&& isValidDate(expMo, expYr) && isValidZip(zip);
	}

	/**
	 * Performs the Luhn validation check.
	 * 
	 * @param cardNumber the credit card number
	 * @return true if the card number is a valid credit card number
	 */
	private static boolean isValidNumber(String cardNumber) {
		String digitsOnly = getDigitsOnly(cardNumber);
		int sum = 0;
		int digit = 0;
		int addend = 0;
		boolean timesTwo = false;
		for (int i = digitsOnly.length() - 1; i >= 0; i--) {
			digit = Integer.parseInt(digitsOnly.substring(i, i + 1));
			if (timesTwo) {
				addend = digit * 2;
				if (addend > 9) {
					addend -= 9;
				}
			} else {
				addend = digit;
			}
			sum += addend;
			timesTwo = !timesTwo;
		}
		int modulus = sum % 10;
		return modulus == 0;
	}

	/**
	 * Returns true if the security code is valid for the specified card.
	 * 
	 * @param cardNumber the credit card number
	 * @param securityCode the security code
	 * @return true if the security code is valid for the specified card
	 */
	private static boolean isValidSecurityCode(
			String cardNumber, String securityCode) {
		if(cardNumber.length() < 13 || securityCode.length() < 2) {
			return false;
		}
		boolean isValid = false;
		String digitsOnly = getDigitsOnly(cardNumber);
		String scDigitsOnly = getDigitsOnly(securityCode);
		int firstTwoDigits = Integer.parseInt(digitsOnly.substring(0, 2));

		if(firstTwoDigits == 34 || firstTwoDigits == 37) { //American Express
			isValid = (scDigitsOnly.length() == 4);
		} else {
			isValid = (scDigitsOnly.length() == 3);
		}
		return isValid;
	}

	/**
	 * Returns true if the card hasn't expired yet.
	 * 
	 * @param expMo the expiration month
	 * @param expYr the security code
	 * @return true if the security code is valid for the specified card
	 */
	private static boolean isValidDate(String expMo, String expYr) {
		if(expMo.length() == 0 || expYr.length() < 4) {
			return false;
		}
		
		int month = Integer.parseInt(getDigitsOnly(expMo));
		int year = Integer.parseInt(getDigitsOnly(expYr));
		Calendar cal = Calendar.getInstance();
		int thisMonth = cal.get(Calendar.MONTH) + 1; //zero_based
		int thisYear = cal.get(Calendar.YEAR);

		return (year < MAX_YEAR && month < 13 && month > 0 
				&& (year > thisYear || (year == thisYear && month > thisMonth)));

	}

	/**
	 * Returns true if the zip code is a 5 digit zip code.
	 * 
	 * @param zip the zip code
	 * @return true if the zip code is a 5 digit zip code
	 */
	private static boolean isValidZip(String zip) {

		return getDigitsOnly(zip).length() == 5;

	}

}