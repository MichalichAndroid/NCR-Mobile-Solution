package com.example.ncrmobilesolutions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class ShoppingHistoryFragment extends Fragment {

	List<ParseObject> reciepts = new ArrayList<ParseObject>();
	// private SwipeDismissListViewTouchListener mSwipeDismissTouchListener;
	private RecieptPreviewAdapter mAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.shopping_history_fragment,
				container, false);
		ListView listView = (ListView) view
				.findViewById(R.id.activity_reciepts_listview);
		View footerView = getActivity().getLayoutInflater().inflate(
				R.layout.list_empty_footer, null);
		listView.addFooterView(footerView);

		return view;
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		DownloadAllReciepts();
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

	private void DownloadAllReciepts() {

		getActivity().findViewById(R.id.progressBar1).setVisibility(
				View.VISIBLE);
		getActivity().findViewById(R.id.activity_reciepts_listview)
				.setVisibility(View.GONE);
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("installationId", ParseInstallation.getCurrentInstallation()
				.getInstallationId());
		params.put("lastFetchTime", Application.GetLastFetchDate());

		ParseUser currentUser = ParseUser.getCurrentUser();
		String functionName = "fetchReceiptsByInstallationId";
		if (currentUser != null) {
			functionName = "fetchReceiptsByUser";
		}
		ParseCloud.callFunctionInBackground(functionName, params,
				new FunctionCallback<Object>() {

					@Override
					public void done(Object response, ParseException exception) {

						if (response instanceof java.util.ArrayList) {

							OnSuccesDownload((ArrayList<ParseObject>) response);

						} else {
							ShowReceiptsFromStorage();

						}

					}

				});

	}

	private void SetFinishDownloadVisability() {
		getActivity().findViewById(R.id.progressBar1).setVisibility(View.GONE);
		getActivity().findViewById(R.id.activity_reciepts_listview)
				.setVisibility(View.VISIBLE);
	}

	private void OnSuccesDownload(final List<ParseObject> reciepts1)

	{

		ArrayList<String> uniqueRetailers = new ArrayList<String>();

		long batchStart = System.currentTimeMillis();
		Log.w("Pin of All Object", "Start "
				+ (System.currentTimeMillis() - batchStart));
		for (int i = 0; i < reciepts1.size(); i++) {
			try {
				long start = System.currentTimeMillis();
				// reciepts1.get(i).getParseObject("retailer").fetchIfNeeded();
				ParseObject retailer = reciepts1.get(i).getParseObject(
						"retailer");
				String objectId = retailer.getObjectId();
				if (uniqueRetailers.contains(objectId)) {
					Log.w("'Pin' of Not unique Object",
							"" + (System.currentTimeMillis() - start));
					continue;
				}
				uniqueRetailers.add(objectId);
				reciepts1.get(i).getParseObject("retailer").pin();
				Log.w("Pin of Object", ""
						+ (System.currentTimeMillis() - start));

			} catch (ParseException e) {

				e.printStackTrace();
			}

		}
		Log.w("Pin of All Object", "End "
				+ (System.currentTimeMillis() - batchStart));

		ParseObject.pinAllInBackground(reciepts1, new SaveCallback() {

			@Override
			public void done(ParseException parseException) {
				if (parseException == null && reciepts1.size() > 0) {
					Application.SaveLastFetchDate(reciepts1.get(0)
							.getCreatedAt().getTime());
				}

				ShowReceiptsFromStorage();
			}
		});

	}

	private void ShowReceiptsFromStorage() {
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Receipts");
		// query.setCachePolicy(CachePolicy.CACHE_ELSE_NETWORK);
		query.fromLocalDatastore();
		query.orderByDescending("createdAt");
		query.include("retailer");
		query.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> recieptsRes, ParseException arg1) {

				if (recieptsRes == null)
					recieptsRes = new ArrayList<ParseObject>();
				ShoppingHistoryFragment.this.reciepts = recieptsRes;
				DrowListOfReciepts(reciepts);
				SetFinishDownloadVisability();
			}
		});
	}

	private void DrowListOfReciepts(List<ParseObject> reciepts) {

		try {
			this.reciepts = reciepts;
			ListView listView = (ListView) getActivity().findViewById(
					R.id.activity_reciepts_listview);

			mAdapter = new RecieptPreviewAdapter(getActivity(), reciepts);

			listView.setAdapter(mAdapter);

			listView.setOnItemLongClickListener(new OnItemLongClickListener() {

				@Override
				public boolean onItemLongClick(AdapterView<?> parent,
						View view, int position, long id) {

					ShowRecieptAtPosition(position);
					return false;
				}
			});

			listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {

					ShowRecieptAtPosition(position);

				}
			});

			SetFinishDownloadVisability();
		} catch (Exception exception) {
			exception.printStackTrace();
			Restart();
		}

	}

	private void Restart() {
		Intent i = getActivity()
				.getBaseContext()
				.getPackageManager()
				.getLaunchIntentForPackage(
						getActivity().getBaseContext().getPackageName());
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(i);

	}

	private void ShowRecieptAtPosition(int position) {
		Intent intent = new Intent(getActivity(), RecieptViewActivity.class);
		intent.putExtra(
				"reciept",
				ShoppingHistoryFragment.this.reciepts.get(position).getString(
						"receipt"));
		startActivity(intent);
	}

}
