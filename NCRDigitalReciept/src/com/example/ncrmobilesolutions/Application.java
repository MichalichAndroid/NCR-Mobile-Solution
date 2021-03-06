package com.example.ncrmobilesolutions;

import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.content.SharedPreferences;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseTwitterUtils;
import com.parse.ParseUser;
import com.parse.PushService;
import com.paypal.android.MEP.PayPal;

public class Application extends android.app.Application {
	// Debugging switch

	// Debugging tag for the application
	public static final String APPTAG = "DigitalReciept";

	public static final String PushTokenId = "PushTokenId";
	public static final String SharedPreferencesName = "com.example.ncrdigitalreciept";
	public static String UpdateList_ACTION = "com.example.ncrmobilesolutions.UpdateList";
	public static String UpdateMainMenu_ACTION = "com.example.ncrmobilesolutions.UpdateMainMenu";
	public static final String UserNameKey = "UserNameKey";
	public static final String LastFetchDateKey = "LastFetchDateKey";
	
	

	private static Context context;

	private static boolean _paypalLibraryInit = false;;

	public Application() {
	}

	@Override
	public void onCreate() {
		super.onCreate();

		context = getApplicationContext();
		Parse.enableLocalDatastore(this);

		// Production
		Parse.initialize(this, getString(R.string.application_id),
				getString(R.string.client_id));

		// Test
		/*
		 * Parse.initialize(this, "SNGu8bRK5nxTwGTh3qrvDzFE8tRbIarDGaRsKxwr",
		 * "W3CAAQRO4Xq2TqtDtvRgZKtYaQLrGIZsPnbOKCwp"); UpdateInstalation();
		 */
		/*
		 * <key>FacebookAppID</key> <string></string>
		 * <key>FacebookDisplayName</key> <string>NCR Receipts</string>
		 */
		
		UpdateInstalation();
		ParseFacebookUtils.initialize("1002365463126175");
		ParseTwitterUtils.initialize("f6QN6eoNDXA9VOj6mRegsXGlG",
				"xZZUUcy9KaTxJbZL75rhKL7IDGmMK2ItzeNO7EtdSsuIxYX9if");
		initPayPalLibrary();

		// ParseUser.enableAutomaticUser();

		// "YOUR_PARSE_APPLICATION_ID",
		// "YOUR_PARSE_CLIENT_KEY");

	}

	public static boolean RegenerateOfBarcodeRequired = false;

	public static void SavePushToken(String token) {
		SharedPreferences prefs = context.getSharedPreferences(
				SharedPreferencesName, Context.MODE_PRIVATE);
		prefs.edit().putString(PushTokenId, token).commit();
	}

	public static String GetPushToken() {
		SharedPreferences prefs = context.getSharedPreferences(
				SharedPreferencesName, Context.MODE_PRIVATE);
		return prefs.getString(PushTokenId, "");
	}

	public static Context GetContext() {

		return context;
	}

	public static void UpdateInstalation() {
		ParseUser parseUser = ParseUser.getCurrentUser();
		ParseInstallation parseInstallation = ParseInstallation
				.getCurrentInstallation();
		if (parseUser != null) {
			ParseACL postACL = new ParseACL(parseUser);
			postACL.setPublicReadAccess(true);
			postACL.setPublicWriteAccess(true);
			parseInstallation.put("user", parseUser);
		} else {
			parseInstallation.remove("user");
		}

	//	parseInstallation.saveInBackground();
		try {
			parseInstallation.save();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void SetUserName(String string) {
		SharedPreferences prefs = context.getSharedPreferences(
				SharedPreferencesName, Context.MODE_PRIVATE);
		prefs.edit().putString(UserNameKey, string).commit();

	}

	public static String GetUserName() {

		SharedPreferences prefs = context.getSharedPreferences(
				SharedPreferencesName, Context.MODE_PRIVATE);
		return prefs.getString(UserNameKey, "");
	}

	/*
	 * public static void SaveLastFetchDate() { // TODO Auto-generated method
	 * stub SaveLastFetchDate(System.currentTimeMillis()); }
	 */

	public static void SaveLastFetchDate(long millis) {
		// TODO Auto-generated method stub
		SharedPreferences prefs = context.getSharedPreferences(
				SharedPreferencesName, Context.MODE_PRIVATE);
		prefs.edit().putLong(LastFetchDateKey, millis).commit();
	}

	public static Date GetLastFetchDate() {
		// TODO Auto-generated method stub
		SharedPreferences prefs = context.getSharedPreferences(
				SharedPreferencesName, Context.MODE_PRIVATE);
		Date date = new Date(prefs.getLong(LastFetchDateKey, 0));
		return date;
	}

	public static void OnLogout() {
		ParseObject.unpinAllInBackground();
		SetUserName("");
		SaveLastFetchDate(0);
	}

	public void initPayPalLibrary() {
		PayPal pp = PayPal.getInstance();

		if (pp == null) { // Test to see if the library is already initialized

			// This main initialization call takes your Context, AppID, and
			// target server
			pp = PayPal.initWithAppID(this, "APP-80W284485P519543T",
					PayPal.ENV_SANDBOX);

			// Required settings:

			// Set the language for the library
			String lang = Locale.getDefault().toString();
			pp.setLanguage(lang);

			// Some Optional settings:

			// Sets who pays any transaction fees. Possible values are:
			// FEEPAYER_SENDER, FEEPAYER_PRIMARYRECEIVER, FEEPAYER_EACHRECEIVER,
			// and FEEPAYER_SECONDARYONLY
		//	pp.setFeesPayer(PayPal.FEEPAYER_EACHRECEIVER);

			// true = transaction requires shipping
		//	pp.setShippingEnabled(false);

			_paypalLibraryInit  = true;
		}

	}

}
