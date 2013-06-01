package uw.cse.dineon.library.test;

import uw.cse.dineon.library.util.CredentialValidator;
import junit.framework.TestCase;

/**
 * Tests the CredentialValidator library class
 * and makes sure that usernames, passwords, emails
 * and matching passwords are all validated correctly.
 * 
 * Black box tests
 * @author glee23
 */
public class CredentialValidatorTest extends TestCase {

	/**
	 * Asserts that a null password is rejected.
	 */
	public void testFailNullPassword() {
		assertFalse(CredentialValidator.isValidPassword(
				null).isValid());
	}

	/**
	 * Asserts that an empty password is rejected.
	 */
	public void testFailEmptyPassword() {
		assertFalse(CredentialValidator.isValidPassword(
				"").isValid());
	}

	/**
	 * Asserts that a password with a space 
	 * in the beginning is rejected.
	 */
	public void testFailPasswordBeginningSpace() {
		assertFalse(CredentialValidator.isValidPassword(
				" hasfkjlsdfkasadlfkjsdflasda").isValid());
	}
	
	/**
	 * Asserts that a password with a space 
	 * in the middle is rejected.
	 */
	public void testFailPasswordMiddleSpace() {
		assertFalse(CredentialValidator.isValidPassword(
				"hasfkjlsdfkas adlfkjsdflasda").isValid());
	}
	
	/**
	 * Asserts that a password with a space 
	 * in the end is rejected.
	 */
	public void testFailPasswordEndSpace() {
		assertFalse(CredentialValidator.isValidPassword(
				"hasfkjlsdfkasadlfkjsdflasda ").isValid());
	}
	
	/**
	 * Asserts that a valid password 
	 * with lowercase letters is accepted.
	 */
	public void testPassValidPasswordLowercaseLetters() {
		assertTrue(CredentialValidator.isValidPassword(
				"hasfkjlsdfkasadlfkjsdflasda").isValid());
	}
	
	/**
	 * Asserts that a valid password 
	 * with uppercase letters is accepted.
	 */
	public void testPassValidPasswordUppercaseLetters() {
		assertTrue(CredentialValidator.isValidPassword(
				"ABCDEFGHIJKLMNOP").isValid());
	}
	
	/**
	 * Asserts that a valid password 
	 * with mixed case letters is accepted.
	 */
	public void testPassValidPasswordMixedLetters() {
		assertTrue(CredentialValidator.isValidPassword(
				"aBcdEfghIjkl").isValid());
	}
	
	/**
	 * Asserts that a valid password 
	 * with symbols is accepted.
	 */
	public void testPassValidPasswordSymbols() {
		assertTrue(CredentialValidator.isValidPassword(
				"@#&$!**)(&%^").isValid());
	}
	
	/**
	 * Asserts that a valid password 
	 * with symbols is accepted.
	 */
	public void testPassValidPasswordNumbers() {
		assertTrue(CredentialValidator.isValidPassword(
				"09238454323492390481").isValid());
	}
	
	/**
	 * Asserts that a valid password 
	 * with numbers, letters, symbols is accepted.
	 */
	public void testPassValidPasswordNumbersLettersSymbols() {
		assertTrue(CredentialValidator.isValidPassword(
				"ab092@*384$@543ad!@$jasf").isValid());
	}

	/**
	 * Asserts that a null username
	 * is rejected. 
	 */
	public void testFailNullUsername() {
		assertFalse(CredentialValidator.isValidUserName(
				null).isValid());
	}

	/**
	 * Asserts that an empty username
	 * is rejected. 
	 */
	public void testFailEmptyUsername() {
		assertFalse(CredentialValidator.isValidUserName(
				"").isValid());
	}

	/**
	 * Asserts that a valid lowercase 
	 * username is accepted. 
	 */
	public void testPassValidLowercaseUsername() {
		assertTrue(CredentialValidator.isValidUserName(
				"johndoe").isValid());
	}
	
	/**
	 * Asserts that a valid uppercase 
	 * username is accepted. 
	 */
	public void testPassValidUppercaseUsername() {
		assertTrue(CredentialValidator.isValidUserName(
				"JOHNDOE").isValid());
	}
	
	/**
	 * Asserts that a valid mixed case 
	 * username is accepted. 
	 */
	public void testPassValidMixedcaseUsername() {
		assertTrue(CredentialValidator.isValidUserName(
				"jOhNdOe").isValid());
	}
	
	/**
	 * Asserts that a valid number
	 * username is accepted. 
	 */
	public void testPassValidNumberUsername() {
		assertTrue(CredentialValidator.isValidUserName(
				"1234567").isValid());
	}
	
	/**
	 * Asserts that a valid symbol
	 * username is accepted. 
	 */
	public void testPassValidSymbolUsername() {
		assertTrue(CredentialValidator.isValidUserName(
				"!@#$%^&*()").isValid());
	}
	
	/**
	 * Asserts that a valid symbol, number,
	 * and letter username is accepted. 
	 */
	public void testPassValidLetterNumberSymbolUsername() {
		assertTrue(CredentialValidator.isValidUserName(
				"a32d4k5!%l6^&EW").isValid());
	}

	/**
	 * Asserts that null passwords
	 * are rejected.
	 */
	public void testFailPasswordMatchNullPasswords() {
		assertFalse(CredentialValidator.passwordsMatch(
				null, null).isValid());
	}

	/**
	 * Asserts that passwords that are
	 * unequal substrings of each other
	 * are rejected.
	 */
	public void testFailPasswordMatchSubstringPasswords() {
		assertFalse(CredentialValidator.passwordsMatch(
				"abcdefg", "abcdefgh").isValid());
	}

	/**
	 * Asserts that passwords that are
	 * unequal substrings of each other
	 * are rejected.
	 */
	public void testFailPasswordMatchSubstringPasswordsReversed() {
		assertFalse(CredentialValidator.passwordsMatch(
				"abcdefgh", "abcdefg").isValid());
	}

	/**
	 * Asserts that a first blank password
	 * is rejected.
	 */
	public void testFailPasswordMatchFirstPasswordEmpty() {
		assertFalse(CredentialValidator.passwordsMatch(
				"", "abcdefgh").isValid());
	}

	/**
	 * Asserts that a second blank password
	 * is rejected.
	 */
	public void testFailPasswordMatchSecondPasswordEmpty() {
		assertFalse(CredentialValidator.passwordsMatch(
				"abcdefgh", "").isValid());
	}

	/**
	 * Asserts that a first case-insensitive password
	 * is rejected.
	 */
	public void testFailPasswordMatchFirstCapital() {
		assertFalse(CredentialValidator.passwordsMatch(
				"Abcdefgh", "abcdefgh").isValid());
	}

	/**
	 * Asserts that a second case-insensitive password
	 * is rejected.
	 */
	public void testFailPasswordMatchSecondCapital() {
		assertFalse(CredentialValidator.passwordsMatch(
				"abcdefgh", "Abcdefgh").isValid());
	}

	/**
	 * Asserts that a first beginning space in the password
	 * is rejected.
	 */
	public void testFailPasswordMatchFirstBeginningSpace() {
		assertFalse(CredentialValidator.passwordsMatch(
				" abcdefgh", "abcdefgh").isValid());
	}
	
	/**
	 * Asserts that a first middle space in the password
	 * is rejected.
	 */
	public void testFailPasswordMatchFirstMiddleSpace() {
		assertFalse(CredentialValidator.passwordsMatch(
				"abcd efgh", "abcdefgh").isValid());
	}

	/**
	 * Asserts that a first ending space in the password
	 * is rejected.
	 */
	public void testFailPasswordMatchFirstEndSpace() {
		assertFalse(CredentialValidator.passwordsMatch(
				"abcdefgh ", "abcdefgh").isValid());
	}

	/**
	 * Asserts that a second beginning space in the password
	 * is rejected.
	 */
	public void testFailPasswordMatchSecondBeginningSpace() {
		assertFalse(CredentialValidator.passwordsMatch(
				"abcdefgh", " abcdefgh").isValid());
	}
	
	/**
	 * Asserts that a second middle space in the password
	 * is rejected.
	 */
	public void testFailPasswordMatchSecondMiddleSpace() {
		assertFalse(CredentialValidator.passwordsMatch(
				"abcdefgh", "abcd efgh").isValid());
	}
	
	/**
	 * Asserts that a second ending space in the password
	 * is rejected.
	 */
	public void testFailPasswordMatchSecondEndSpace() {
		assertFalse(CredentialValidator.passwordsMatch(
				"abcdefgh", "abcdefgh ").isValid());
	}

	/**
	 * Asserts that two empty passwords are the same.
	 */
	public void testPassPasswordMatchEmptyPasswords() {
		assertTrue(CredentialValidator.passwordsMatch(
				"", "").isValid());
	}
	
	/**
	 * Asserts that two valid passwords are the same.
	 */
	public void testPassPasswordMatchSamePasswords() {
		assertTrue(CredentialValidator.passwordsMatch(
				"johndoe", "johndoe").isValid());
	}

	/**
	 * Asserts that a null email is rejected.
	 */
	public void testFailNullEmail() {
		assertFalse(CredentialValidator.isValidEmail(
				null).isValid());
	}
	
	/**
	 * Asserts that an empty email is rejected.
	 */
	public void testFailEmptyEmail() {
		assertFalse(CredentialValidator.isValidEmail(
				"").isValid());
	}
	
	/**
	 * Asserts that an email without an @ symbol is rejected.
	 */
	public void testFailNoAtSymbolEmail() {
		assertFalse(CredentialValidator.isValidEmail(
				"abcdefggmail.com").isValid());
	}
	
	/**
	 * Asserts that an email without an . symbol is rejected.
	 */
	public void testFailNoPeriodEmail() {
		assertFalse(CredentialValidator.isValidEmail(
				"abcdefg@gmailcom").isValid());
	}
	
	/**
	 * Asserts that an email with illegal symbol characters is rejected.
	 */
	public void testFailIllegalCharactersEmail() {
		assertFalse(CredentialValidator.isValidEmail(
				"!abcdefg@gmail.com").isValid());
		assertFalse(CredentialValidator.isValidEmail(
				"@abcdefg@gmail.com").isValid());
		assertFalse(CredentialValidator.isValidEmail(
				"#abcdefg@gmail.com").isValid());
		assertFalse(CredentialValidator.isValidEmail(
				"$abcdefg@gmail.com").isValid());
		assertFalse(CredentialValidator.isValidEmail(
				"|abcdefg@gmail.com").isValid());
		assertFalse(CredentialValidator.isValidEmail(
				"^abcdefg@gmail.com").isValid());
		assertFalse(CredentialValidator.isValidEmail(
				"&abcdefg@gmail.com").isValid());
		assertFalse(CredentialValidator.isValidEmail(
				"*abcdefg@gmail.com").isValid());
		assertFalse(CredentialValidator.isValidEmail(
				"(abcdefg@gmail.com").isValid());
		assertFalse(CredentialValidator.isValidEmail(
				")abcdefg@gmail.com").isValid());
		assertFalse(CredentialValidator.isValidEmail(
				";abcdefg@gmail.com").isValid());
		assertFalse(CredentialValidator.isValidEmail(
				"'abcdefg@gmail.com").isValid());
		assertFalse(CredentialValidator.isValidEmail(
				"<abcdefg@gmail.com").isValid());
		assertFalse(CredentialValidator.isValidEmail(
				">abcdefg@gmail.com").isValid());
	}
	
	/**
	 * Asserts that a valid email is accepted.
	 */
	public void testPassValidEmail() {
		assertTrue(CredentialValidator.isValidEmail(
				"a@a.com").isValid());
	}
	
	/**
	 * Asserts that a valid email 
	 * with an extra period is accepted.
	 */
	public void testPassValidPeriodEmail() {
		assertTrue(CredentialValidator.isValidEmail(
				"a.a@a.com").isValid());
	}
	
	/**
	 * Asserts that a valid capitalized email 
	 * with an extra period is accepted.
	 */
	public void testPassValidCapitalizedEmail() {
		assertTrue(CredentialValidator.isValidEmail(
				"aBcDeFg@a.com").isValid());
	}
	
	/**
	 * Asserts that a valid email 
	 * with an org ending is accepted.
	 */
	public void testPassValidEmailOrg() {
		assertTrue(CredentialValidator.isValidEmail(
				"aBcDeFg@a.org").isValid());
	}
	
	/**
	 * Asserts that a valid email 
	 * with a ca ending is accepted.
	 */
	public void testPassValidEmailCa() {
		assertTrue(CredentialValidator.isValidEmail(
				"aBcDeFg@a.ca").isValid());
	}
	
	/**
	 * Asserts that a valid email 
	 * with a net ending is accepted.
	 */
	public void testPassValidEmailNet() {
		assertTrue(CredentialValidator.isValidEmail(
				"aBcDeFg@a.net").isValid());
	}
	
	/**
	 * Asserts that a valid numbered email 
	 * is accepted.
	 */
	public void testPassValidNumberedEmailNet() {
		assertTrue(CredentialValidator.isValidEmail(
				"3aBcD572eFg4@a4.net").isValid());
	}
	
}
