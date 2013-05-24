package uw.cse.dineon.user.bill;

import java.text.NumberFormat;

import uw.cse.dineon.library.DiningSession;
import uw.cse.dineon.user.DineOnUserApplication;
import uw.cse.dineon.user.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

/**
 * 
 * @author mhotan
 *
 */
public class CurrentBillFragment extends Fragment implements 
OnSeekBarChangeListener,
OnClickListener {

	private TextView mTitle, mSubTotal, mTotalTax, mTotal, mTip;
	
	private SeekBar mTipBar;
	private int mCurTipPercent;
	private double mSubtotal;
	private double mTotalAmount;
	private double mTax;
	private Button mPayButton;
	
	private DiningSession mSession;
	
	private NumberFormat mFormatter = NumberFormat.getCurrencyInstance();;
	
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
		
		mPayButton = (Button) view.findViewById(R.id.button_pay_with_magic);
		mPayButton.setOnClickListener(this);
		
		mTipBar = (SeekBar) view.findViewById(R.id.seekBar_tip_variable);
		mTipBar.setMax(100);
		mTipBar.setProgress(0);
		mCurTipPercent = 0;
		mTip.setText("" + mCurTipPercent + "%");
		mTipBar.setOnSeekBarChangeListener(this);
		
		mSession = DineOnUserApplication.getCurrentDiningSession();
		
		return view;
	}
	
	/**
	 * Update the UI fragment to reflect current bill.
	 * @param subtotal of current order
	 * @param tax for current order
	 */
	public void setBill(String subtotal, String tax) {
		
		if (this.mSession == null) {

			DineOnUserApplication.getCurrentDiningSession();
		}

		mTitle.setText("Current Bill for " + mSession.getRestaurantInfo().getName());
		
		mSubTotal.setText(subtotal);
		mTotalTax.setText(tax);
		
		this.mSubtotal = Double.parseDouble(subtotal.substring(1));
		this.mTax = Double.parseDouble(tax.substring(1));
		double tipAmount = ((double)mCurTipPercent / 100.0) * (this.mTax + this.mSubtotal);
		double totalPrice = this.mSubtotal + this.mTax + tipAmount;
		mTip.setText(" " + mCurTipPercent + "%,  $" + String.format("%.2f", tipAmount));
		mTotal.setText(this.mFormatter.format(totalPrice));
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		if (fromUser) {
			mCurTipPercent = progress;
			
			double tipAmount = ((double)mCurTipPercent / 100.0) * (this.mTax + this.mSubtotal);
			mTip.setText(" " + mCurTipPercent + "%,  $" + String.format("%.2f", tipAmount));
			double totalPrice = this.mSubtotal + this.mTax + tipAmount;
			mTotal.setText(this.mFormatter.format(totalPrice));
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) { }

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) { }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Toast.makeText(getActivity(), "Your order has been paid for.", 
				Toast.LENGTH_SHORT).show();
		getActivity().onBackPressed();
	}

}
