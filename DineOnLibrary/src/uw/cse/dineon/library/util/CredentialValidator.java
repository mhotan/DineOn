package uw.cse.dineon.library.util;

import java.util.regex.Pattern;

/**
 * Static tool to help validate login credential syntax.
 * @author mhotan
 */
public final class CredentialValidator {

	private static final String RESOLVED = "Valid!";

	private static final int MIN_LENGTH = 1; // TODO change later

	private static final Resolution RESOLVED_INSTANCE = new Resolution();
	private static final Resolution NULL_PW = new Resolution(false, "Can't have a null password");
	private static final Resolution EMPTY_PW = new Resolution(false, 
			"Can't have a password with no characters");
	private static final Resolution SHORT_PW = new Resolution(false, 
			"Password must have more then " + MIN_LENGTH + " characters");
	private static final Resolution SPACEIN_PW = new Resolution(false, 
			"Password can't contain spaces");
	//TODO etc..

	private static final Resolution NULL_UN = new Resolution(false, "Can't have a null username");
	private static final Resolution EMPTY_UN = new Resolution(false, 
			"Can't have a username with no characters");
	private static final Resolution SHORT_UN = new Resolution(false,
			"Username is to short");
	//TODO etc..

	private static final Resolution NULL_EMAIL = new Resolution(false, "Can't have a null email");
	private static final Resolution INVALID_EMAIL = new Resolution(false, "Invalid email"); 

	
	private static final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
			"[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" 
					+ "\\@"
					+ "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" 
					+  "("
					+ "\\."
					+ "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}"
					+ ")+" );

	/**
	 * Validates a complete set of attributes.
	 * @param username
	 * @param email
	 * @param pw
	 * @param pwRepeat
	 * @return
	 */
	public static Resolution validateAll(String username, String email, String pw,
			String pwRepeat) {
		// Handle the validation
		Resolution emailRes = CredentialValidator.isValidEmail(email);
		Resolution pwRes = CredentialValidator.isValidPassword(pw);
		Resolution pwRepRes = CredentialValidator.isValidPassword(pwRepeat);
		Resolution userName = CredentialValidator.isValidEmail(email);

		StringBuffer buf = new StringBuffer();
		if (!userName.isValid()) {
			buf.append(userName.getMessage() + "\n");
		}
		if (!emailRes.isValid()) {
			buf.append(emailRes.getMessage() + "\n");
		}
		if (!pwRes.isValid()) {
			buf.append(pwRes.getMessage() + "\n");
		}
		if (!pwRepRes.isValid()) {
			buf.append(pwRepRes.getMessage() + "\n");
		}
		
		if (buf.length() == 0) { // Success!
			return RESOLVED_INSTANCE;
		} 
		return new Resolution(false, buf.toString());
	}

	/**
	 * Can't create instance.
	 */
	private CredentialValidator() { }

	/**
	 * Syntactically validates email.
	 * @param email email to validate
	 * @return Resolution to return.
	 */
	public static Resolution isValidEmail(String email) {
		if (email == null) {
			return NULL_EMAIL;
		}
		if (EMAIL_ADDRESS_PATTERN.matcher(email).matches()) {
			return RESOLVED_INSTANCE;
		}
		return INVALID_EMAIL;
	}

	/**
	 * Syntactic username validator.
	 * @param username usrename to validate
	 * @return Resolution of this username
	 */
	public static Resolution isValidUserName(String username) {
		if (username == null) {
			return NULL_UN;
		} 
		if (username.isEmpty()) {
			return EMPTY_UN;
		}
		if (username.length() < MIN_LENGTH) {
			return SHORT_UN;
		}

		return RESOLVED_INSTANCE;
	}

	/**
	 * Syntactically validates passwords.
	 * 
	 * @param password Password to validate as a "Good" password
	 * @return A resolution instance stating the result of validifying the password 
	 */
	public static Resolution isValidPassword(String password){
		if (password == null) {
			return NULL_PW;
		} 
		if (password.isEmpty()) {
			return EMPTY_PW;
		}
		if (password.length() < MIN_LENGTH) {
			return SHORT_PW;
		}
		if (password.contains(" ")) {
			return SPACEIN_PW;
		}

		return RESOLVED_INSTANCE;
	}

	/**
	 * TODO Might use later pending how we create messages
	 * MH To lazy right now ;-)
	 * Handles the distribution and creation of Resolution instances.
	 * Allows memory conservation and virtualization of Resolution construction
	 * @author mhotan
	 */
	private static class ResolutionFactory {
		//		
		//		/**
		//		 * 
		//		 * @param pw
		//		 * @return
		//		 */
		//		static Resolution getNullOrEmptyResolution(String pw){
		//			return NULL_PW;
		//		}
	}

	/**
	 * General resolution class the has the ability to communicate
	 * to the client whether the given request produced a valid result.
	 * 
	 * To see associated messages use getMessage().
	 * To see if valid use isValid().
	 * 
	 * @author mhotan
	 */
	public static class Resolution {

		private final boolean isResolved;
		private final String message;

		/**
		 * Returns resolved password result.
		 * Only Password validator can generate a resolution
		 */
		Resolution() {
			this(true, RESOLVED);
		}

		/**
		 * 
		 * @param valid true if valid password, false other wise
		 * @param message 
		 */
		Resolution(boolean valid, String message) {
			this.isResolved = valid;
			this.message = message;
		}

		/**
		 * Returns whether the password is valid.
		 * @return true if valid
		 */
		public boolean isValid(){
			return isResolved;
		}

		/**
		 * Returns the message associated with this resolution.
		 * @return message as string literal
		 */
		public String getMessage(){
			return message;
		}
	}

}
