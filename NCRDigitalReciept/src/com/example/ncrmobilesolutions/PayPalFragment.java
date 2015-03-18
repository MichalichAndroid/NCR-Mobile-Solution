package com.example.ncrmobilesolutions;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.datastructions.ParseResponseObject;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.paypal.android.MEP.PayPal;
import com.paypal.android.MEP.PayPalActivity;
import com.paypal.android.MEP.PayPalPreapproval;
import com.paypal.android.MEP.PayPalResultDelegate;

public class PayPalFragment extends Fragment {

	public final String PayPalDateFormat = "yyyy-MM-dd";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.paypal_fragment, container, false);

		return view;
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getApprooval();

	}

	public void onStart() {
		super.onStart();

	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

	}

	@Override
	public void onDetach() {
		super.onDetach();

	}

	private void getApprooval() {

		if (ParseUser.getCurrentUser() == null)
			return;

		HashMap<String, Object> params = new HashMap<String, Object>();

		String functionName = "getPayPalPreapprovalKey";

		ParseCloud.callFunctionInBackground(functionName, params,
				new FunctionCallback<Object>() {

					@Override
					public void done(Object result,
							ParseException parseException) {
						if (parseException != null) {
							parseException.printStackTrace();
							return;
						}

						String key = result.toString();

						if (result.getClass().equals(HashMap.class))

						{
							ParseResponseObject parseRes = new ParseResponseObject(
									(HashMap<String, Object>) result);

							String resCode = parseRes.GetStatus();
							String message = parseRes.GetMessage();
							HashMap<String, Object> data = (HashMap<String, Object>) parseRes
									.GetData();

							if (resCode.equals("1")) {
								key = data.get("preapprovalKey").toString();
								ShowPreaproovalDialog(key);
								return;
							} else {
								ShowErrorDialog(resCode, message);
							}
						}

						ShowPreaproovalDialog(key);

					}

				});

	}

	private void ShowErrorDialog(String resCode, String message) {
		new AlertDialog.Builder(getActivity())
				.setTitle("Error " + resCode)
				.setMessage(message)
				.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								FinishActivity();
							}
						})

				.setIcon(android.R.drawable.ic_dialog_alert).show();

	}

	private void ShowPreaproovalDialog(String key) {
		PayPal pp = PayPal.getInstance();
		if (pp == null)
			return;

		PayPalPreapproval preapproval = new PayPalPreapproval();
		pp.setPreapprovalKey(key);
		preapproval.setType(PayPalPreapproval.TYPE_AGREE);
		preapproval.setPinRequired(true);
		preapproval.setMerchantName("NCR Demo");
		PayPalResult palResult = new PayPalResult();
		Intent preapproveIntent = PayPal.getInstance().preapprove(preapproval,
				getActivity());
		startActivityForResult(preapproveIntent,
				MainDrawerActivity.PayPalPreapprove);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Check which request we're responding to

		if (requestCode == MainDrawerActivity.PayPalPreapprove) {

			if (PayPalActivity.RESULT_FAILURE == resultCode) {
				String errorID = data
						.getStringExtra(PayPalActivity.EXTRA_ERROR_ID);
				String errorMessage = data
						.getStringExtra(PayPalActivity.EXTRA_ERROR_MESSAGE);

				Log.i("PayPalResponse", errorID + "  =" + errorMessage);
			}
			if (resultCode == Activity.RESULT_OK) {

				Map<String, Object> params = new HashMap<String, Object>();
				ParseCloud.callFunctionInBackground(
						"confirmPayPalPreapprovalKey", params,
						new FunctionCallback<HashMap<String, Object>>() {

							@Override
							public void done(HashMap<String, Object> arg0,
									ParseException arg1) {

								if (arg1 != null) {
									ShowErrorDialog("-1", arg1.getMessage());
									return;
								}
								ParseResponseObject parseRes = new ParseResponseObject(
										(HashMap<String, Object>) arg0);

								String resCode = parseRes.GetStatus();
								String message = parseRes.GetMessage();

								if (resCode.equals("1")) {

									FinishActivity();
									return;
								} else {
									ShowErrorDialog(resCode, message);
								}

								// FinishActivity();

							}

						});

			}
			if (resultCode == Activity.RESULT_CANCELED) {
				FinishActivity();
			}

		}
	}

	private void FinishActivity() {

		Activity activity = getActivity();
		activity.getFragmentManager().beginTransaction().remove(this).commit();
		if (activity.getClass().equals(MainDrawerActivity.class)) {
			((MainDrawerActivity) activity).InitDefaultFragment();
		}
	}

	public class PayPalResult implements PayPalResultDelegate, Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 3988329024415923118L;

		@Override
		public void onPaymentCanceled(String arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPaymentFailed(String arg0, String arg1, String arg2,
				String arg3, String arg4) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPaymentSucceeded(String arg0, String arg1) {
			// TODO Auto-generated method stub

		}

	}

}
