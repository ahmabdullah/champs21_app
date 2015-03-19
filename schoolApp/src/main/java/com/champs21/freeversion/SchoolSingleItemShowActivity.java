package com.champs21.freeversion;

import java.util.List;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.champs21.schoolapp.R;
import com.champs21.schoolapp.model.FreeVersionPost;
import com.champs21.schoolapp.model.Wrapper;
import com.champs21.schoolapp.networking.AppRestClient;
import com.champs21.schoolapp.utils.AppConstant;
import com.champs21.schoolapp.utils.AppUtility;
import com.champs21.schoolapp.utils.GsonParser;
import com.champs21.schoolapp.utils.RequestKeyHelper;
import com.champs21.schoolapp.utils.SchoolApp;
import com.champs21.schoolapp.utils.URLHelper;
import com.champs21.schoolapp.utils.UserHelper;
import com.champs21.schoolapp.viewhelpers.CommentDialog;
import com.champs21.schoolapp.viewhelpers.PagerContainer;
import com.champs21.schoolapp.viewhelpers.PopupDialog;
import com.champs21.schoolapp.viewhelpers.UIHelper;
import com.champs21.schoolapp.viewhelpers.UninterceptableViewPager;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class SchoolSingleItemShowActivity extends ChildContainerActivity {

	private final static String TAG = "Single Item Show Activity";
	private boolean isFromGoodRead = false;

	private UIHelper uiHelper;
	private String itemId = "";
	private String itemCategoryId = "";

	private JsonObject objectPost;
	private JsonObject objectRoot;

	private ImageButton btnGoodRead, btnCandle, btnMySchool;
	private TextView txtCategoryName;
	private TextView txtTitle;
	private TextView auther_name_txt;
	private TextView since_txt;
	private ImageButton btnShareUpper;
	private ImageButton goodreadBtn;
	private TextView txtgoodRead;
	private TextView txtFullContent;
	private ProgressBar progressBar;
	private PagerContainer container;
	private UninterceptableViewPager imageViewPager;
	private LinearLayout wowCommentLayout;
	private TextView wowCount;
	private TextView commentCoutn;

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		homeBtn.setVisibility(View.VISIBLE);
		logo.setVisibility(View.GONE);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_school_singleitemshow_fragment);

		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			if (bundle.containsKey(AppConstant.GOING_GOODREAD)) {
				this.isFromGoodRead = true;
			} else
				this.isFromGoodRead = false;
		}

		initViews();
		userHelper = new UserHelper(this);
		uiHelper = new UIHelper(this);

		if (bundle != null) {

			Log.e("SINGLE_NEWS", "cat_id: " + this.itemCategoryId + " id: "
					+ itemId);

			initApiCall(getIntent().getStringExtra(AppConstant.ITEM_ID),
					getIntent().getStringExtra(AppConstant.ITEM_CAT_ID));
		}

		// initApiCall("143", "");
		// initApiCall("371", "");

		final AdView mAdView = (AdView) this.findViewById(R.id.adView);
		mAdView.setVisibility(View.GONE);
		AdRequest adRequest = new AdRequest.Builder().build();
		mAdView.loadAd(adRequest);

		mAdView.setAdListener(new AdListener() {

			@Override
			public void onAdLoaded() {
				// TODO Auto-generated method stub
				super.onAdLoaded();

				mAdView.setVisibility(View.VISIBLE);
			}
		});

	}

	private String getItemIdFromUrl() {
		Uri data = getIntent().getData();
		Log.d(TAG, data.toString());
		String scheme = data.getScheme();
		Log.d(TAG, scheme);
		String host = data.getHost();
		Log.d(TAG, host);
		String inurl = data.toString();
		String[] tokens = inurl.split("-");
		return tokens[tokens.length - 1];

	}

	private void initViews() {

		txtCategoryName = (TextView) findViewById(R.id.txtCategoryName);

		txtTitle = (TextView) findViewById(R.id.txtTitle);
		auther_name_txt = (TextView) findViewById(R.id.auther_name_txt);
		since_txt = (TextView) findViewById(R.id.since_txt);

		btnShareUpper = (ImageButton) findViewById(R.id.btnShareUpper);
		goodreadBtn = (ImageButton) findViewById(R.id.button_goodread);
		txtgoodRead = (TextView) findViewById(R.id.txtgoodRead);
		wowCount = (TextView)findViewById(R.id.wow_count);
		commentCoutn =(TextView)findViewById(R.id.comment_count);
//		commentCoutn.setText(data. "")
		wowCommentLayout = (LinearLayout) findViewById(R.id.wow_comment_layout);

		wowCommentLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				CommentDialog dialog = CommentDialog.newInstance(Integer
						.parseInt(data.getId()), "1".equals(data.getCan_comment()),data.getWow_count()+" people wow this");
				Log.e("CAN_COMMENT", data.getCan_comment());
				dialog.show(getSupportFragmentManager(), null);
			}
		});

		// for sticky

		/***
		 * 
		 * ## start local fragment variable
		 * 
		 * ****/
		// this.imgView = (ImageView)view.findViewById(R.id.imgView);
		txtFullContent = (TextView) findViewById(R.id.txtFullContent);
		progressBar = (ProgressBar) findViewById(R.id.progressBar);

		container = (PagerContainer) findViewById(R.id.pager_container);
		imageViewPager = (UninterceptableViewPager) this.container
				.findViewById(R.id.imageViewPager);
		imageViewPager.setPageMargin(4);
		imageViewPager.setClipChildren(false);
		imageViewPager.setOffscreenPageLimit(1);

		btnGoodRead = (ImageButton) this.findViewById(R.id.btn_goodread);
		btnCandle = (ImageButton) this.findViewById(R.id.btn_candle);
		btnMySchool = (ImageButton) this.findViewById(R.id.btn_myschool);

		btnGoodRead.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (UserHelper.isLoggedIn()) {
					startActivity(new Intent(SchoolSingleItemShowActivity.this,
							GoodReadActivity.class));
				} else {
					/*
					 * uiHelper.showSuccessDialog(
					 * getString(R.string.not_logged_in_msg), "Dear User");
					 */
					showCustomDialog(
							"GOOD READ",
							R.drawable.goodread_popup_icon,
							getResources().getString(R.string.good_read_msg)
									+ "\n"
									+ getResources().getString(
											R.string.not_logged_in_msg));

				}
			}
		});

		btnCandle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (UserHelper.isLoggedIn()) {
					startActivity(new Intent(SchoolSingleItemShowActivity.this,
							CandleActivity.class));
				} else {
					/*
					 * uiHelper.showSuccessDialog(
					 * getString(R.string.not_logged_in_msg), "Dear User");
					 */
					showCustomDialog(
							"CANDLE",
							R.drawable.candle_popup_icon,
							getResources().getString(R.string.candle_msg)
									+ "\n"
									+ getResources().getString(
											R.string.not_logged_in_msg));

				}

			}
		});

		btnMySchool.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent schoolIntent = new Intent(
						SchoolSingleItemShowActivity.this,
						SchoolFreeVersionActivity.class);
				startActivity(schoolIntent);
			}
		});

	}

	public void showCustomDialog(String headerText, int imgResId,
			String descriptionText) {

		PopupDialog picker = PopupDialog.newInstance(0);
		picker.setData(headerText, descriptionText, imgResId,
				SchoolSingleItemShowActivity.this);
		picker.show(
				SchoolSingleItemShowActivity.this.getSupportFragmentManager(),
				null);
	}

	public void initApiCall(String itemId, String itemCategoryId) {

		Log.e("SINGLE_NEWS_INITAPICALL", "cat_id: " + this.itemCategoryId
				+ " id: " + itemId);

		RequestParams params = new RequestParams();
		params.put("id", itemId);

		if (itemCategoryId != null)
			params.put("category_id", itemCategoryId);

		if (UserHelper.isLoggedIn())
			params.put(RequestKeyHelper.USER_ID, UserHelper.getUserFreeId());

		AppRestClient.post(URLHelper.URL_FREE_VERSION_SINGLENEWS, params,
				singleNewsHandler);
	}

	private AsyncHttpResponseHandler singleNewsHandler = new AsyncHttpResponseHandler() {

		@Override
		public void onFailure(Throwable arg0, String arg1) {
			uiHelper.showMessage(arg1);
			uiHelper.dismissLoadingDialog();
		};

		@Override
		public void onStart() {
			uiHelper.showLoadingDialog("Please wait...");
		};

		@Override
		public void onSuccess(String responseString) {
			// Log.e("FREE_HOME", "data: "+responseString);

			uiHelper.dismissLoadingDialog();

			Wrapper modelContainer = GsonParser.getInstance()
					.parseServerResponse(responseString);

			Log.e("RESPONSE_SINGLE_NEWS_SCHOOL", responseString);
			if (modelContainer.getStatus().getCode() == 200) {

				objectRoot = modelContainer.getData().getAsJsonObject();

				objectPost = modelContainer.getData().getAsJsonObject()
						.get("post").getAsJsonObject();

				String assessmentId = objectPost.get("assessment_id")
						.getAsString();

				totalObj = new Gson().fromJson(objectRoot.toString(),
						JsonObject.class);
				totalObjString = new Gson().toJson(totalObj);

				data = new Gson().fromJson(objectPost.toString(),
						FreeVersionPost.class);

				rootObj = new Gson().toJson(data);

				data2 = new Gson().fromJson(objectPost.toString(),
						JsonObject.class);
				rootObj2 = new Gson().toJson(data2);

				/*
				 * bundle = new Bundle();
				 * bundle.putString(AppConstant.POST_FRAG_OBJECT, rootObj);
				 * bundle.putString(AppConstant.POST_FRAG_OBJECT2, rootObj2);
				 * bundle.putString(AppConstant.POST_FRAG_OBJECT_TOTAL,
				 * totalObjString);
				 */

				objectPost = new Gson().fromJson(rootObj2, JsonObject.class);

				objectRoot = new Gson().fromJson(totalObjString,
						JsonObject.class);

				initViewActions();

			}

		};

	};

	FreeVersionPost data;
	String rootObj;
	JsonObject data2;
	String rootObj2;
	Bundle bundle;

	JsonObject totalObj;
	String totalObjString;
	private String goodReadFolderId;
	private boolean isAlreadyGoodRead = false;

	private void initViewActions() {
		// this.txtCategoryName.setText(data.getCategoryName());

		this.txtTitle.setText(data.getTitle());
		this.auther_name_txt.setSelected(true);
		if (!TextUtils.isEmpty(data.getAuthor())) {
			this.auther_name_txt.setVisibility(View.VISIBLE);
			this.auther_name_txt.setText("By " + data.getAuthor());
		}

		else {
			this.auther_name_txt.setVisibility(View.GONE);
		}
		
		this.commentCoutn.setText(data.getCommentCount()+" comments");
		this.wowCount.setText(data.getWow_count()+" WoW");
		this.since_txt.setText(data.getPublishedDateString() + " ago");

		this.btnShareUpper.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				//sharePost(data);
				shareAll();
			}
		});

		if (UserHelper.isLoggedIn()) {
			if (!TextUtils.isEmpty(objectRoot.get("good_read").getAsString())) {
				goodReadFolderId = objectRoot.get("good_read").getAsString();
				goodreadBtn.setBackgroundResource(R.drawable.remove_normal);
				txtgoodRead.setText("Remove\nGood Read");

				isAlreadyGoodRead = true;
			} else {
				isAlreadyGoodRead = false;
			}

		}

		this.txtFullContent.setText(data.getFullContent());

		ImageViewPagerAdapter imageAdapter = new ImageViewPagerAdapter(
				data.getImages());
		imageAdapter.notifyDataSetChanged();
		imageViewPager.setAdapter(imageAdapter);

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
			ImageView view = new ImageView(SchoolSingleItemShowActivity.this);
			view.setLayoutParams(new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT));
			view.setScaleType(ScaleType.FIT_XY);

			SchoolApp.getInstance().displayUniversalImage(
					listImage.get(position), view, progressBar);

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

	public void sharePost(FreeVersionPost post) {
		this.data = post;
		((ChildContainerActivity) this).doFaceBookLogin(true);

	}
	
	public void shareAll()
	{
		if (AppUtility.isInternetConnected()) 
		{
			(SchoolSingleItemShowActivity.this).sharePostUniversal(data);
		}
	}
}
