package uw.cse.dineon.user.checkin.test;

import uw.cse.dineon.user.checkin.IntentIntegrator;
import uw.cse.dineon.user.checkin.IntentResult;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.test.AndroidTestCase;

public class IntentIntegratorTest extends AndroidTestCase {

	private IntentIntegrator test;
	private Activity act;
	
	public IntentIntegratorTest() {
		super();
	}

	protected void setUp() throws Exception {
		act = new Activity();
		test = new IntentIntegrator(act);
	}

	protected void tearDown() throws Exception {
	}

	public void testIntentIntegrator() {
		assertNotNull(test);
	}

	public void testParseActivityResult() {
		IntentResult ir = IntentIntegrator.parseActivityResult(0x0000c0de, Activity.RESULT_OK, new Intent());
		assertNotNull(ir);
	}

	public void testShareTextCharSequence() {
//		AlertDialog ad = test.shareText("tests!!!");
//		assertNotNull(ad);
	}
}
