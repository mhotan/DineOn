package uw.cse.dineon.library.test;

import uw.cse.dineon.library.UserInfo;
import android.test.AndroidTestCase;

import com.parse.Parse;
import com.parse.ParseUser;



/**
 * 
 * @author espeo196
 *
 */
public class UserInfoTest extends AndroidTestCase {

	public static final int TIMEOUT = 1000; 
	ParseUser testUser;
	UserInfo testUInfo;
	
	public UserInfoTest() {
		super();
	}
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		Parse.initialize(this.getContext(), "RUWTM02tSuenJPcHGyZ0foyemuL6fjyiIwlMO0Ul", "wvhUoFw5IudTuKIjpfqQoj8dADTT1vJcJHVFKWtK");
		
		testUser = new ParseUser();
		testUser.setUsername("utester");
		testUser.setPassword("pass");
		testUser.signUp();
		testUser.save();
		
		testUInfo = new UserInfo(testUser);
	}

	public void testDefault() {
		assertEquals("Undetermined", testUInfo.getPhone());
		assertTrue(null == testUInfo.getEmail());
	}

	public void testGetName() {
		assertEquals("utester", testUser.getUsername());
		assertEquals("utester", testUInfo.getName());
	}
	
	public void testSetAndGetBasicPhoneNumber() {
		testUInfo.setPhone("911");
		assertEquals("911", testUInfo.getPhone());
	}
	
	public void testSetAndGetBasicEmail() {
		testUInfo.setEmail("tester@cs.washington.edu");
		assertEquals("tester@cs.washington.edu", testUInfo.getEmail());
	}
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		testUser.delete();
	}

}
