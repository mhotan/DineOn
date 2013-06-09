package uw.cse.dineon.user.bill;

import java.text.NumberFormat;

import uw.cse.dineon.library.DiningSession;
import uw.cse.dineon.user.R;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

/**
 * 
 * @author mhotan
 */
public class CurrentBillFragment extends Fragment implements 
OnSeekBarChangeListener,
OnClickListener {

	private TextView mTitle, mSubTotal, mTotalTax, mTotal, mTip;
	
	private SeekBar mTipBar;
	private int mCurTipPercent;
	private Button mPayButton;
	
	private DiningSession mSession;
	private NumberFormat mFormatter;
	private PayBillListener mListener;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mFormatter = NumberFormat.getCurrencyInstance();
	}
	
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
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		updateBill(mListener.getDiningSession());
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof PayBillListener) {
			this.mListener = (PayBillListener) activity;
		} else {
			throw new ClassCastException(activity.toString()
					+ " must implemenet CurrentBillFragment.PayBillListener");
		}
	}
	
	/**
	 * Updates the current bill with the argument session. 
	 * @param session Session to update bill with.
	 */
	void updateBill(DiningSession session) {
		mSession = session;
		
		String title = "No Current Dining Session";
		double subTotal = 0, tax = 0, tip = 0;
		if (mSession != null) {
			title = "Current Bill for " + mSession.getRestaurantInfo().getName();
			subTotal = mSession.getSubTotal();
			tax = mSession.getTax();
		}
		tip = ((double)mCurTipPercent / 100.0) * (subTotal + tax);
		
		mTitle.setTag(title);
		mSubTotal.setText(this.mFormatter.format(subTotal));
		mTotalTax.setText(this.mFormatter.format(tax));
		mTip.setText(" " + mCurTipPercent + "%, " + mFormatter.format(tip));
		mTotal.setText(mFormatter.format(tip + subTotal + tax));
	}
	
	/**
	 * Updates the bill using the current session.
	 */
	private void updateBill() {
		updateBill(mSession);
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		if (fromUser) {
			mCurTipPercent = progress;
			updateBill();
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) { }

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) { }

	@Override
	public void onClick(View v) {
		this.mListener.payCurrentBill();
	}
	
	/**
	 * Defines an interface for the CurrentBillActivity to implement.
	 * so that it can pay the bill using UserSatelite
	 * @author mtrathjen08
	 *
	 */
	public interface PayBillListener {
		
		/**
		 * Retrieves the session to currently present for the bill. 
		 * @return Dining session to currently present.
		 */
		DiningSession getDiningSession();
		
		/**
		 * Pay the current bill by sending payment info to restaurant.
		 */
		public void payCurrentBill();
		
		// TODO 
		// Add individual check out feature.
		// 
	}

}
