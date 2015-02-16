package com.example.ncrmobilesolutions;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.Request;
import com.facebook.Request.GraphUserCallback;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseTwitterUtils;
import com.parse.ParseUser;
import com.parse.ui.ParseLoginBuilder;
import com.paypal.android.MEP.PayPal;
import com.paypal.android.MEP.PayPalActivity;
import com.paypal.android.MEP.PayPalPreapproval;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MainDrawerActivity extends Activity {

	private static final int LoginActivityId = 5;
	public static final int PayPalPreapprove = 6;

	// private String[] mLeftTitles = new String[] { "left1", "left2", "left3"
	// };
	private String[] mRightTitles = new String[] { "right1", "right2", "right3" };
	private DrawerLayout mDrawerLayout;
	private ListView mLeftDrawerList;

	// private ListView mRightDrawerList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

		setContentView(R.layout.activity_main_drawer);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.custom_title_bar);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.activity_main_drawer_drawer_layout);
		mLeftDrawerList = (ListView) findViewById(R.id.activity_main_drawer_left_drawer_items);
		// mRightDrawerList = (ListView)
		// findViewById(R.id.activity_main_drawer_right_drawer);

		// Set the adapter for the list view

		String[] leftTitles = new String[] {
				getString(R.string.shopping_history_entry),
				getString(R.string.shopping_list_entry),
				getString(R.string.payment_entry) };
		mLeftDrawerList.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, leftTitles));

		// Set the list's click listener
		mLeftDrawerList
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						onLeftItemClicked(position);

					}

				});

		/*
		 * mRightDrawerList.setAdapter(new ArrayAdapter<String>(this,
		 * android.R.layout.simple_list_item_1, mRightTitles));
		 * 
		 * mRightDrawerList .setOnItemClickListener(new
		 * AdapterView.OnItemClickListener() {
		 * 
		 * @Override public void onItemClick(AdapterView<?> parent, View view,
		 * int position, long id) { onRightItemClicked(position);
		 * 
		 * }
		 * 
		 * });
		 */

		findViewById(R.id.activity_main_drawer_left_drawer_image)
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						GoToLogin();
					}
				});
		findViewById(R.id.activity_main_drawer_left_drawer_text)
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						GoToLogin();
					}
				});

		InitDefaultFragment();
		SetLoginState();

	}

	public void InitDefaultFragment() {
		OnShoppingHistoryClicked();

	}

	private void onLeftItemClicked(int position) {

		mLeftDrawerList.setItemChecked(position, true);
		mDrawerLayout.closeDrawers();
		mLeftDrawerList = (ListView) findViewById(R.id.activity_main_drawer_left_drawer_items);

		String clickedItem = mLeftDrawerList.getAdapter().getItem(position)
				.toString();

		if (clickedItem.equals(getString(R.string.shopping_list_entry))) {
			OnShoppingListClicked();
		} else if (clickedItem
				.equals(getString(R.string.shopping_history_entry))) {
			OnShoppingHistoryClicked();
		}

		else if (clickedItem.equals(getString(R.string.payment_entry))) {
			OnPaymentClicked();
		}

	}

	private void OnPaymentClicked() {
		SetTitleText(getString(R.string.payment_entry));

		
		  Fragment fragment = new PayPalFragment(); android.app.FragmentManager
		  fragmentManager = getFragmentManager();
		  fragmentManager.beginTransaction()
		 .replace(R.id.activity_main_drawer_content_frame, fragment)
		  .commit();
		 

		

	}

	private void OnShoppingHistoryClicked() {
		SetTitleText(getString(R.string.shopping_history_entry));
		Fragment fragment = new ShoppingHistoryFragment();
		android.app.FragmentManager fragmentManager = getFragmentManager();
		fragmentManager.beginTransaction()
				.replace(R.id.activity_main_drawer_content_frame, fragment)
				.commit();

	}

	private void OnShoppingListClicked() {
		SetTitleText(getString(R.string.shopping_list_entry));

	}

	private void SetTitleText(String text) {
		((TextView) findViewById(R.id.custom_title_text)).setText(text);

	}

	private void onRightItemClicked(int position) {
		// mRightDrawerList.setItemChecked(position, true);
		// mDrawerLayout.closeDrawer(mRightDrawerList);
		SetTitleText(mRightTitles[position]);
	}

	public void onLeftClick(View view) {
		if (!mDrawerLayout.isDrawerOpen(Gravity.START))
			mDrawerLayout.openDrawer(Gravity.START);
		else {
			mDrawerLayout.closeDrawers();
		}
	}

	public void onRightClick(View view) {
		SyncCodeDialog syncCodeDialog = new SyncCodeDialog(this, true,
				new DialogInterface.OnCancelListener() {

					@Override
					public void onCancel(DialogInterface dialog) {
						// TODO Auto-generated method stub

					}
				});

		syncCodeDialog.show();
		// mDrawerLayout.openDrawer(Gravity.END);
	}

	private void GoToLogin() {
		ParseUser currentUser = ParseUser.getCurrentUser();
		if (currentUser == null) {
			ParseLoginBuilder builder = new ParseLoginBuilder(
					MainDrawerActivity.this);
			startActivityForResult(builder.build(), LoginActivityId);
		}

		else {
			ParseUser.logOut();
			currentUser = ParseUser.getCurrentUser(); // this will now be null

			ParseInstallation installation = ParseInstallation
					.getCurrentInstallation();
			installation.remove("user");
			installation.saveInBackground();

			Application.OnLogout();
			InitDefaultFragment();
			SetLoginState();
		}
		mDrawerLayout.closeDrawers();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Check which request we're responding to
		if (requestCode == LoginActivityId) {

			ParseUser parseUser = ParseUser.getCurrentUser();
			ParseInstallation parseInstallation = ParseInstallation
					.getCurrentInstallation();
			Application.UpdateInstalation();

			AssociateUser(parseInstallation);

			// Make sure the request was successful
			if (resultCode == RESULT_OK) {

			}
		}

		if (requestCode == PayPalPreapprove) {
			
			 super.onActivityResult(requestCode, resultCode, data);
			 InitDefaultFragment();
		}
	}

	private void AssociateUser(ParseInstallation parseInstallation) {
		// TODO Auto-generated method stub

		findViewById(R.id.activity_main_drawer_loading).setVisibility(
				View.VISIBLE);
		findViewById(R.id.activity_main_drawer_frame_layout).setVisibility(
				View.GONE);
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("installationId", parseInstallation.getInstallationId());
		ParseCloud.callFunctionInBackground("associateInstallationWithUser",
				params, new FunctionCallback<Object>() {

					@Override
					public void done(Object arg0, ParseException arg1) {
						try {

							// Log.i("MainActivity", arg0.toString());
							if (arg1 != null) {
								arg1.printStackTrace();

							}

						} catch (Exception e) {

							e.printStackTrace();
						}

						findViewById(R.id.activity_main_drawer_loading)
								.setVisibility(View.GONE);
						findViewById(R.id.activity_main_drawer_frame_layout)
								.setVisibility(View.VISIBLE);

					}

				});

		SetLoginState();
	}

	protected void SetLoginState() {
		final TextView textView = (TextView) findViewById(R.id.activity_main_drawer_left_drawer_text);
		final ImageView photoImageView = (ImageView) findViewById(R.id.activity_main_drawer_left_drawer_image);
		ParseUser currentUser = ParseUser.getCurrentUser();
		if (currentUser == null) {
			textView.setText(R.string.default_name);
			photoImageView.setImageResource(R.drawable.default_user_image);

		} else {

			if (ParseFacebookUtils.isLinked(currentUser)) {
				SetFaceBookDetails(textView, photoImageView);
			} else if (ParseTwitterUtils.isLinked(currentUser)) {

				SetTwitterDetails(textView, photoImageView);

			} else {
				textView.setText(currentUser.getUsername());

			}

		}
	}

	private void SetTwitterDetails(final TextView textView,
			final ImageView photoImageView) {
		textView.setText(ParseTwitterUtils.getTwitter().getScreenName());

		AsyncTask<Void, Void, Bitmap> t = new AsyncTask<Void, Void, Bitmap>() {
			protected Bitmap doInBackground(Void... p) {
				Bitmap bm = null;
				try {
					HttpClient client = new DefaultHttpClient();
					HttpGet verifyGet = new HttpGet(
							"https://api.twitter.com/1.1/users/show.json?screen_name="
									+ ParseTwitterUtils.getTwitter()
											.getScreenName());
					ParseTwitterUtils.getTwitter().signRequest(verifyGet);
					HttpResponse response = client.execute(verifyGet);

					InputStream is = response.getEntity().getContent();

					BufferedReader r = new BufferedReader(
							new InputStreamReader(is));
					StringBuilder total = new StringBuilder();
					String line;
					while ((line = r.readLine()) != null) {
						total.append(line);
					}
					String url = "";
					String bigurl = "";
					JSONObject responseJson;
					try {
						responseJson = new JSONObject(total.toString());
						url = responseJson.getString("profile_image_url");
						int pos = url.toLowerCase().lastIndexOf("_normal.");

						bigurl = url.substring(0, pos);
						bigurl = bigurl + "."
								+ url.substring(pos + "_normal.".length());

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					is.close();

					URL aURL = new URL(bigurl);
					URLConnection conn = aURL.openConnection();
					conn.setUseCaches(true);
					conn.connect();
					is = conn.getInputStream();
					BufferedInputStream bis = new BufferedInputStream(is);
					bm = BitmapFactory.decodeStream(bis);
					bis.close();
					is.close();

				} catch (Exception e) {

					e.printStackTrace();
				}
				return bm;
			}

			protected void onPostExecute(Bitmap bm) {

				if (bm != null) {
					photoImageView.setImageBitmap(bm);
				}

			}
		};
		t.execute();
	}

	private void SetFaceBookDetails(final TextView textView,
			final ImageView photoImageView) {
		String username = Application.GetUserName();
		if (username == null || username.equals("")) {
			textView.setText(R.string.default_facebook_name);
		} else {
			textView.setText(username);
		}
		Request.newMeRequest(ParseFacebookUtils.getSession(),
				new GraphUserCallback() {

					@Override
					public void onCompleted(
							final com.facebook.model.GraphUser user,
							com.facebook.Response response) {

						if (user != null) {
							String userName = user.getName();
							textView.setText(userName);
							Application.SetUserName(userName);
						}

						if (user != null) {
							AsyncTask<Void, Void, Bitmap> t = new AsyncTask<Void, Void, Bitmap>() {
								protected Bitmap doInBackground(Void... p) {
									Bitmap bm = null;
									try {
										URL aURL = new URL(
												"https://graph.facebook.com/"
														+ user.getId()
														+ "/picture?type=large");
										URLConnection conn = aURL
												.openConnection();
										conn.setUseCaches(true);
										conn.connect();
										InputStream is = conn.getInputStream();
										BufferedInputStream bis = new BufferedInputStream(
												is);
										bm = BitmapFactory.decodeStream(bis);
										bis.close();
										is.close();
									} catch (IOException e) {
										e.printStackTrace();
									}
									return bm;
								}

								protected void onPostExecute(Bitmap bm) {

									if (bm != null) {
										photoImageView.setImageBitmap(bm);
									}

								}
							};
							t.execute();

						} else if (response.getError() != null) {
							// handle error
						}

					}
				}).executeAsync();
	}



}
