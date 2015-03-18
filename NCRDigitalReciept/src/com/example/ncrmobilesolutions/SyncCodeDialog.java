package com.example.ncrmobilesolutions;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.datastructions.ParseResponseObject;
import com.google.zxing.BarcodeFormat;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;

public class SyncCodeDialog extends Dialog implements android.view.View.OnClickListener {
	private static final double ImageProportion = 0.9;
	private static final double DialogProportion = 0.8;
	private Activity parent;

	public SyncCodeDialog(Activity context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		parent = context;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sync_code_dialog);
		SetDialogBehaviour();

		Generate();

	}

	private void SetDialogBehaviour() {
		RelativeLayout relativeLayout = (RelativeLayout) findViewById(
				R.id.sync_code_dialog);
		FrameLayout.LayoutParams layoutParams = (android.widget.FrameLayout.LayoutParams) relativeLayout.getLayoutParams();
		Point size = GetDisplaySize();
		layoutParams.width=(int) (size.x *DialogProportion) ;
		layoutParams.height=(int) (size.y * DialogProportion);
		relativeLayout.setLayoutParams(layoutParams);
		relativeLayout.setOnClickListener(this);
	}

	public void Generate() {
		Application.RegenerateOfBarcodeRequired = false;

		findViewById(R.id.sync_code_dialog_loading).setVisibility(View.VISIBLE);
		findViewById(R.id.sync_code_dialog_qr_code).setVisibility(View.GONE);

		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {

				GetQRCode();

			}
		}, 50);

	}

	private void GetQRCode() {
		parent.runOnUiThread(new Runnable() {
			public void run() {
				HashMap<String, Object> params = new HashMap<String, Object>();
				params.put("installationId", ParseInstallation
						.getCurrentInstallation().getInstallationId());

				ParseCloud.callFunctionInBackground("createAssociation", params,
						new FunctionCallback<HashMap<String, Object>>() {

							@Override
							public void done(HashMap<String, Object> arg0,
									ParseException arg1) {
								try {

									if (arg0 == null) {
										arg0 = new HashMap<String, Object>();
									}
									Log.i("MainActivity", arg0.toString());
									if (arg1 != null) {
										arg1.printStackTrace();

									}
									ParseResponseObject parseResponce = new ParseResponseObject(arg0);
									int pincode = (Integer) parseResponce.GetData();
									int size = GetBitmapSize();
									QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(
											pincode + "", null,
											Contents.Type.TEXT,
											BarcodeFormat.QR_CODE.toString(),
											size);
									Bitmap bitmap;

									bitmap = qrCodeEncoder.encodeAsBitmap();

									ImageView myImage = (ImageView) findViewById(R.id.sync_code_dialog_qr_code_image);

									myImage.setImageBitmap(bitmap);
									TextView textView = (TextView) findViewById(R.id.sync_code_dialog_qr_code_managment_code_value);

									textView.setText(String.format("%05d",
											pincode));

								} catch (Exception e) {

									e.printStackTrace();
								}

								findViewById(R.id.sync_code_dialog_loading)
										.setVisibility(View.GONE);
								findViewById(R.id.sync_code_dialog_qr_code)
										.setVisibility(View.VISIBLE);

							}

						});
			}
		});
	}

	@SuppressLint("NewApi")
	private int GetBitmapSize() {

		Point size = GetDisplaySize();
		return (int) (Math.min(size.x, size.y) * DialogProportion * ImageProportion);

	}

	@SuppressLint("NewApi") private Point GetDisplaySize() {
		Display display = parent.getWindowManager().getDefaultDisplay();
		Point size = new Point();
		
		if (Build.VERSION.SDK_INT >= 13) {
			display.getSize(size);

		} else {
			size.x = display.getWidth(); // deprecated
			size.y =  display.getHeight();
		}
		return size;
	}

	@Override
	public void onClick(View v) {
		dismiss();
		
	}
}
