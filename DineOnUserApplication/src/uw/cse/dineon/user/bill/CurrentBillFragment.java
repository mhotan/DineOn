package uw.cse.dineon.user.bill;

import uw.cse.dineon.user.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

/**
 * 
 * @author mhotan
 *
 */
public class CurrentBillFragment extends Fragment implements OnSeekBarChangeListener {

	private TextView mTitle, mSubTotal, mTotalTax, mTotal, mTip;
	
	private SeekBar mTipBar;
	private int mCurTipPercent;
	private double mTotalAmount;
	
	private String mSession;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_pay_bill,
				container, false);
		
		mTitle = (TextView) view.findViewById(R.id.label_bill_title);
		mSubTotal = (TextView) view.findViewById(R.id.value_order_total);
		mTotalTax = (TextView) view.findViewById(R.id.value_order_tax);
		mTotal = (TextView) view.findViewById(R.id.value_final_total);
		mTip = (TextView) view.findViewById(R.id.value_tip);
		
		mTipBar = (SeekBar) view.findViewById(R.id.seekBar_tip_variable);
		mTipBar.setMax(100);
		mTipBar.setProgress(0);
		mCurTipPercent = 0;
		mTip.setText("" + mCurTipPercent + "%");
		mTipBar.setOnSeekBarChangeListener(this);
		
		mSession = null;
		
		// Total is currently zero
		mTotalAmount = 0.0;
		return view;
	}
	
	/**
	 * Activities that own this fragment can use this to determine.
	 * TODO Replace the Dining Session string with instance 
	 * @param session to set
	 */
	public void setDiningSession(String session) {
		if (session == null) {
			return;
		}
		
		mSession = session;
		// Use the Dining session to assign the values appropiately
		mTitle.setText("Current Bill for " + mSession);
		
		double subTotal = 100; //Session get totals
		double tax = (double) (subTotal * .1);
		
		mSubTotal.setText(String.format("%.2f", subTotal));
		mTotalTax.setText(String.format("%.2f", tax));
		
		double tipAmount = ((double)mCurTipPercent / 100.0) * (tax + subTotal);
		double total = subTotal + tax + tipAmount;
		mTip.setText(" " + mCurTipPercent + "%,  $" + String.format("%.2f", tipAmount));
		mTotal.setText(String.format("%.2f", total));
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		if (fromUser) {
			mCurTipPercent = progress;
			
			setDiningSession(mSession);
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) { }

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) { }

}
