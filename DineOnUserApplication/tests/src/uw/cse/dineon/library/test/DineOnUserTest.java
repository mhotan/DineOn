package uw.cse.dineon.library.test;

import android.test.AndroidTestCase;

import com.parse.ParseUser;

import uw.cse.dineon.library.DineOnUser;

public class DineOnUserTest extends AndroidTestCase{
	
	public DineOnUserTest(){
		super();
	}
	
	@Override
	protected void setUp() throws Exception{
		super.setUp();
	}
	
	@Override
	protected void tearDown() throws Exception{
		super.tearDown();
	}
	
	public void testDineOnUser(){
		ParseUser pu = new ParseUser();
		pu.setUsername("testUser");
		pu.setPassword("12345");
		pu.setEmail("test@test.com");
		pu.setObjectId("_test");
		DineOnUser dou = new DineOnUser(pu);
		assertNotNull(dou.getUserInfo());
		assertTrue(dou.getFavs().isEmpty());
		assertTrue(dou.getReserves().isEmpty());
		assertTrue(dou.getFriendList().isEmpty());
		assertNull(dou.getDiningSession());
	}

}
