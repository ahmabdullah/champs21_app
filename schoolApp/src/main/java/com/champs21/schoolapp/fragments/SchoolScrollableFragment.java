package com.champs21.schoolapp.fragments;

import java.util.ArrayList;
import java.util.List;

import uk.co.deanwild.flowtextview.FlowTextView;

import com.champs21.freeversion.SchoolAllActivities;
import com.champs21.freeversion.SchoolFreeVersionActivity;
import com.champs21.freeversion.SchoolPopulationActivity;
import com.champs21.freeversion.SchoolScrollableDetailsActivity;
import com.champs21.freeversion.SchoolScrollableDetailsActivity.ImageViewPagerAdapter;
import com.champs21.schoolapp.R;
import com.champs21.schoolapp.model.SchoolActivities;
import com.champs21.schoolapp.model.Wrapper;
import com.champs21.schoolapp.model.SchoolDetails.SchoolPages;
import com.champs21.schoolapp.networking.AppRestClient;
import com.champs21.schoolapp.utils.AppConstant;
import com.champs21.schoolapp.utils.AppUtility;
import com.champs21.schoolapp.utils.GsonParser;
import com.champs21.schoolapp.utils.RequestKeyHelper;
import com.champs21.schoolapp.utils.SchoolApp;
import com.champs21.schoolapp.utils.URLHelper;
import com.champs21.schoolapp.utils.UserHelper;
import com.champs21.schoolapp.viewhelpers.CustomTextView;
import com.champs21.schoolapp.viewhelpers.PagerContainer;
import com.champs21.schoolapp.viewhelpers.UIHelper;
import com.champs21.schoolapp.viewhelpers.UninterceptableViewPager;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;

public class SchoolScrollableFragment extends Fragment {

	private View view;

	private LinearLayout laycoutContentHolder;
	private ProgressBar progressBar;

	private JsonObject objSchool;
	private List<String> listImage = new ArrayList<String>();

	public static SchoolScrollableFragment newInstance(int schoolId) {
		SchoolScrollableFragment f = new SchoolScrollableFragment();
		// Supply index input as an argument.
		Bundle args = new Bundle();
		args.putInt("index", schoolId);
		f.setArguments(args);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		String schoolId = String.valueOf(getArguments().getInt("index", 0));

		initApiCall(schoolId);

		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		view = inflater.inflate(R.layout.school_scrollable_details_activity,
				container, false);
		((Button) view.findViewById(R.id.btn_all_school))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent schoolIntent = new Intent(getActivity(),
								SchoolFreeVersionActivity.class);
						startActivity(schoolIntent);
					}
				});
		initView(view);

		return view;
	}

	private void initView(View view) {
		this.laycoutContentHolder = (LinearLayout) view
				.findViewById(R.id.laycoutContentHolder);
		this.progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
	}

	private void initAction(ArrayList<SchoolPages> data, JsonObject JsonObject) {
		Log.e("GAL", "is: " + data.size());

		for (int i = 0; i < data.size(); i++) {
			/*
			 * View child =
			 * getLayoutInflater().inflate(R.layout.row_school_scrollable_details
			 * , null); this.laycoutContentHolder.addView(child);
			 */
			populateSchoolSegments(data, i, JsonObject);
			Log.e("GAL", "is: " + data.get(i).getGallery());
		}

		this.progressBar.setVisibility(View.GONE);
	}

	private void populateSchoolSegments(ArrayList<SchoolPages> data,
			int position, JsonObject JsonObject) {
		View view = getActivity().getLayoutInflater().inflate(
				R.layout.row_school_scrollable_details, null);

		LinearLayout layoutPagerHolder = (LinearLayout) view
				.findViewById(R.id.layoutPagerHolder);

		PagerContainer container = (PagerContainer) view
				.findViewById(R.id.pager_container);
		UninterceptableViewPager imageViewPager = (UninterceptableViewPager) container
				.findViewById(R.id.imageViewPager);
		imageViewPager.setPageMargin(4);
		imageViewPager.setClipChildren(false);
		imageViewPager.setOffscreenPageLimit(1);

		LinearLayout linearLayoutCoverHolder = (LinearLayout) view
				.findViewById(R.id.linearLayout2);
		ImageView imgCover = (ImageView) view.findViewById(R.id.imgCover);
		ProgressBar progressImgBar = (ProgressBar) view
				.findViewById(R.id.progressImgBar);
		CustomTextView txtSchoolName = (CustomTextView) view
				.findViewById(R.id.txtSchoolName);
		CustomTextView txtLocation = (CustomTextView) view
				.findViewById(R.id.txtLocation);

		TextView txtTitle = (TextView) view.findViewById(R.id.txtTitle);

		LinearLayout layoutWebView = (LinearLayout) view
				.findViewById(R.id.layoutWebView);
		WebView webViewContent = (WebView) view
				.findViewById(R.id.webViewContent);
		TextView txtPageTitleOther = (TextView) view
				.findViewById(R.id.txtPageTitleOther);
		CustomTextView txtMenuTitle = (CustomTextView) view
				.findViewById(R.id.txtMenuTitle);
		FlowTextView webView = (FlowTextView) view.findViewById(R.id.webView);

		LinearLayout layoutContent = (LinearLayout) view
				.findViewById(R.id.layoutContent);
		ImageView imageAbout = (ImageView) view.findViewById(R.id.imageView);
		ProgressBar imgProgressBarAbout = (ProgressBar) view
				.findViewById(R.id.progressBar);

		if (position == 0) {
			linearLayoutCoverHolder.setVisibility(View.VISIBLE);

			SchoolApp.getInstance().displayUniversalImage(
					objSchool.get("cover").getAsString(), imgCover,
					progressImgBar);
			txtSchoolName.setText(objSchool.get("name").getAsString());
			txtLocation.setText(objSchool.get("location").getAsString() + ", "
					+ objSchool.get("division").getAsString());
		} else {
			linearLayoutCoverHolder.setVisibility(View.GONE);
		}

		if (!data.get(position).getMenuId().equalsIgnoreCase("1")) // this is
																	// other
																	// pages
		{
			layoutWebView.setVisibility(View.VISIBLE);
			layoutContent.setVisibility(View.GONE);

			txtPageTitleOther.setText(data.get(position).getTitle());
			showWebViewContent(data.get(position).getWeb_view(), webViewContent);

		} else // this is about page
		{
			layoutWebView.setVisibility(View.GONE);
			layoutContent.setVisibility(View.VISIBLE);

			txtMenuTitle.setText(data.get(position).getTitle());

			loadFlowTextData(data.get(position).getContent(), webView);

			if (!TextUtils.isEmpty(data.get(position).getImage()))
				SchoolApp.getInstance().displayUniversalImage(
						data.get(position).getImage(), imageAbout,
						imgProgressBarAbout);
		}

		// pager area
		listImage.clear();

		if (data.get(position).getGallery().size() > 0) {
			listImage.addAll(data.get(position).getGallery());
			if (listImage.size() > 0) {
				layoutPagerHolder.setVisibility(View.VISIBLE);
			} else {
				layoutPagerHolder.setVisibility(View.GONE);
			}

			ImageViewPagerAdapter imageAdapter = new ImageViewPagerAdapter(
					listImage);
			imageAdapter.notifyDataSetChanged();
			imageViewPager.setAdapter(imageAdapter);
		}

		txtTitle.setText(data.get(position).getName());

		this.laycoutContentHolder.addView(view);

	}
	
	private void populateActivitySegments(Wrapper modelContainer)
	{
		View view = getActivity().getLayoutInflater().inflate(R.layout.row_school_scrollable_activity, null);
		
		ImageView imgView = (ImageView)view.findViewById(R.id.imgView);
		TextView txtTitle = (TextView)view.findViewById(R.id.txtTitle);
		TextView txtSummery = (TextView)view.findViewById(R.id.txtSummery);
		TextView txtSeeAll = (TextView)view.findViewById(R.id.txtSeeAll);
		LinearLayout layoutActivityInnerHolder = (LinearLayout)view.findViewById(R.id.layoutActivityInnerHolder);
		
		if( modelContainer.getData().getAsJsonObject().get("activity").getAsJsonArray().size() > 0)
		{
			String imgUrl= "";
			if(modelContainer.getData().getAsJsonObject().get("activity").getAsJsonArray().get(0).getAsJsonObject().get("gallery").getAsJsonArray().size() > 0)
			{
				imgView.setVisibility(View.VISIBLE);
				imgUrl = modelContainer.getData().getAsJsonObject().get("activity").getAsJsonArray().get(0).getAsJsonObject().get("gallery").getAsJsonArray().get(0).getAsString();
			}
			else
			{
				imgView.setVisibility(View.GONE);
			}
			
			String strTitle = modelContainer.getData().getAsJsonObject().get("activity").getAsJsonArray().get(0).getAsJsonObject().get("title").getAsString();
			String strSummary = modelContainer.getData().getAsJsonObject().get("activity").getAsJsonArray().get(0).getAsJsonObject().get("summary").getAsString();
			
			
			ProgressBar progressBarImgView = (ProgressBar)view.findViewById(R.id.progressBarImgView);
			//SchoolApp.getInstance().displayUniversalImage(imgUrl, imgView);
			SchoolApp.getInstance().displayUniversalImage(imgUrl, imgView, progressBarImgView);
			txtTitle.setText(strTitle);
			txtSummery.setText(strSummary);
			
			
			txtSeeAll.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(getActivity(), SchoolAllActivities.class);
					intent.putExtra(AppConstant.SCHOOL_ID, objSchool.get("id").getAsString());
					startActivity(intent);
				}
			});
			
			
			JsonArray arrayActivity = modelContainer.getData().getAsJsonObject().get("activity").getAsJsonArray();
			
			if(arrayActivity.size() > 0)
			{
				JsonArray arrayGalleryAct = arrayActivity.get(0).getAsJsonObject().get("gallery").getAsJsonArray();
				List<String> listGallery = new ArrayList<String>();
				
				if(arrayGalleryAct.size() > 0)
				{
					for(int j=0;j<arrayGalleryAct.size();j++)
					{
						listGallery.add(arrayGalleryAct.get(j).getAsString());
					}
				}
				
				final SchoolActivities activity;
				if(arrayActivity.get(0).getAsJsonObject().get("gallery").getAsJsonArray().size() > 0 )
				{
					 activity = new SchoolActivities(arrayActivity.get(0).getAsJsonObject().get("title").getAsString(), arrayActivity.get(0).getAsJsonObject().get("content").getAsString(),
							arrayActivity.get(0).getAsJsonObject().get("summary").getAsString(), 
							arrayActivity.get(0).getAsJsonObject().get("gallery").getAsJsonArray().get(0).getAsString(), listGallery);
					
				}
				else
				{
					activity = new SchoolActivities(arrayActivity.get(0).getAsJsonObject().get("title").getAsString(), arrayActivity.get(0).getAsJsonObject().get("content").getAsString(),
							arrayActivity.get(0).getAsJsonObject().get("summary").getAsString());
				}
				
				layoutActivityInnerHolder.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(getActivity(), SchoolPopulationActivity.class);				
						String val = new Gson().toJson(activity);
						intent.putExtra(AppConstant.ACTIVITY_SINGLE, val);
						startActivity(intent);
					}
				});
			}
			
			
			this.laycoutContentHolder.addView(view);
		}
		
	}
	
	

	@SuppressLint({ "SetJavaScriptEnabled", "NewApi" })
	private void showWebViewContent(String text, WebView webView) {

		webView.removeAllViews();
		final String mimeType = "text/html";
		final String encoding = "UTF-8";

		// webView.loadDataWithBaseURL("", text, mimeType, encoding, null);
		webView.loadData(text, mimeType, encoding);
		/*
		 * webView.getSettings().setLayoutAlgorithm(
		 * LayoutAlgorithm.SINGLE_COLUMN);
		 */

		WebSettings webViewSettings = webView.getSettings();
		webViewSettings.setJavaScriptEnabled(true);

		// webViewSettings.setPluginState(WebSettings.PluginState.ON);

		// webView.setWebChromeClient(new WebChromeClient());

		// webViewSettings.setUseWideViewPort(true);
		// webViewSettings.setLoadWithOverviewMode(true);
		webViewSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);

		// webViewSettings.setDefaultFontSize(18);
		// webViewSettings.setTextZoom(90);

		// (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 18,
		// getResources().getDisplayMetrics())
		// webViewSettings.setMinimumFontSize(18);
		// webViewSettings.setMinimumLogicalFontSize(18);

		// webView.getSettings().setLoadWithOverviewMode(true);
		// webView.getSettings().setUseWideViewPort(true);

		/*
		 * webViewSettings.setUseWideViewPort(true);
		 * webViewSettings.setLoadWithOverviewMode(true);
		 */
		webView.requestLayout();

	}

	private void loadFlowTextData(String data, FlowTextView webView) {

		Spanned html = Html.fromHtml(data);
		webView.setText(html);
		webView.setTextSize(AppUtility.getDeviceIndependentDpFromPixel(
				getActivity(), 16));
		TextPaint mTextPaint;

	}

	private void initApiCall(String schoolId) {
		RequestParams params = new RequestParams();
		params.put("school_id", schoolId);

		if (UserHelper.isLoggedIn())
			params.put(RequestKeyHelper.USER_ID, UserHelper.getUserFreeId());

		AppRestClient.post(URLHelper.URL_FREE_VERSION_SCHOOL_INFO, params,
				schoolDetailHandler);
	}

	private AsyncHttpResponseHandler schoolDetailHandler = new AsyncHttpResponseHandler() {

		@Override
		public void onFailure(Throwable arg0, String arg1) {
			// uiHelper.showMessage(arg1);
			// uiHelper.dismissLoadingDialog();
		};

		@Override
		public void onStart() {
			// uiHelper.showLoadingDialog("Please wait...");
		};

		@Override
		public void onSuccess(String responseString) {
			// Log.e("FREE_HOME", "data: "+responseString);

			Wrapper modelContainer = GsonParser.getInstance()
					.parseServerResponse(responseString);

			if (modelContainer.getStatus().getCode() == 200) {

				JsonArray arraySchool = modelContainer.getData().get("schools")
						.getAsJsonObject().get("school_pages").getAsJsonArray();

				ArrayList<SchoolPages> data = parseSchoolDetails(arraySchool
						.toString());

				Log.e("SCHOOL_ID",
						"is: "
								+ modelContainer.getData().get("schools")
										.getAsJsonObject().get("id")
										.getAsString());

				objSchool = modelContainer.getData().get("schools")
						.getAsJsonObject();

				initAction(data, objSchool);
				
				populateActivitySegments(modelContainer);
			}

			else {

			}
		};
	};

	public ArrayList<SchoolPages> parseSchoolDetails(String object) {
		ArrayList<SchoolPages> data = new ArrayList<SchoolPages>();
		data = new Gson().fromJson(object,
				new TypeToken<ArrayList<SchoolPages>>() {
				}.getType());
		return data;
	}

	public class ImageViewPagerAdapter extends PagerAdapter {

		private List<String> listImage;

		public ImageViewPagerAdapter(List<String> listImage) {
			this.listImage = listImage;

		}

		public int getCount() {
			return listImage.size();
		}

		public Object instantiateItem(View collection, int position) {
			ImageView view = new ImageView(getActivity());
			view.setLayoutParams(new LinearLayout.LayoutParams(150, 150));
			view.setScaleType(ScaleType.FIT_XY);
			view.setBackgroundResource(android.R.color.transparent);

			SchoolApp.getInstance().displayUniversalImage(
					listImage.get(position), view);

			Log.e("LIST_GAL", "is: " + listImage.get(position));

			((ViewPager) collection).addView(view, 0);
			return view;
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView((View) arg2);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == ((View) arg1);

		}

		@Override
		public Parcelable saveState() {
			return null;
		}
	}

}
