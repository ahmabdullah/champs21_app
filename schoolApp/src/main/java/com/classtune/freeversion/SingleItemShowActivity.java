package com.classtune.freeversion;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.classtune.schoolapp.R;
import com.classtune.schoolapp.fragments.SingleFragmentTypeFive;
import com.classtune.schoolapp.fragments.SingleFragmentTypeFour;
import com.classtune.schoolapp.fragments.SingleFragmentTypeOne;
import com.classtune.schoolapp.fragments.SingleFragmentTypeTwo;
import com.classtune.schoolapp.fragments.SingleFragmentTypeWebView;
import com.classtune.schoolapp.fragments.SingleFragmentTypeZero;
import com.classtune.schoolapp.model.AssessmentQuestion;
import com.classtune.schoolapp.model.FreeVersionPost;
import com.classtune.schoolapp.model.Wrapper;
import com.classtune.schoolapp.networking.AppRestClient;
import com.classtune.schoolapp.utils.AppConstant;
import com.classtune.schoolapp.utils.AppUtility;
import com.classtune.schoolapp.utils.GsonParser;
import com.classtune.schoolapp.utils.RequestKeyHelper;
import com.classtune.schoolapp.utils.URLHelper;
import com.classtune.schoolapp.utils.UserHelper;
import com.classtune.schoolapp.utils.UserHelper.UserAccessType;
import com.classtune.schoolapp.viewhelpers.PopupDialog;
import com.classtune.schoolapp.viewhelpers.PopupDialogSingleItemAssessmentInvok;
import com.classtune.schoolapp.viewhelpers.UIHelper;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;



public class SingleItemShowActivity extends ChildContainerActivity {

	private final static String TAG="Single Item Show Activity";
	private boolean isFromGoodRead = false;
	
	private UserHelper userHelper;
	private UIHelper uiHelper;
	private String itemId = "";
	private String itemCategoryId = "";
	
	private RelativeLayout fragmentContainer;
	
	private JsonObject objectPost;
	private JsonObject objectRoot;
	
	private int normalPostType;
	
	
	private ImageButton btnGoodRead, btnCandle, btnMySchool;
	private TextView internetErrorTxt;
	private LinearLayout layoutAssementHolder;

	private String assesmentId;
	private List<AssessmentQuestion> listAssessmentQuestion = new ArrayList<AssessmentQuestion>();
	
	
	
	
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
		setContentView(R.layout.activity_singleitemshow_fragment);
		if(AppRestClient.isObjectDeleted())
		{
			AppRestClient.init();
		}
		Log.e("HAHAHAH", "Aise");
		
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			if (bundle.containsKey(AppConstant.GOING_GOODREAD)) {
				this.isFromGoodRead = true;
			} else
				this.isFromGoodRead = false;
		}
		
		
		userHelper = new UserHelper(this);
		uiHelper = new UIHelper(this);
		initViews();

		if (bundle != null) {
			if (bundle.containsKey(AppConstant.ITEM_ID))
			{
				Log.e("HAHAHAH", bundle.getString(AppConstant.ITEM_ID));
				
				this.itemId = bundle.getString(AppConstant.ITEM_ID);
				this.itemCategoryId = bundle.getString(AppConstant.ITEM_CAT_ID);
			}
			else
			{
				this.itemId=getItemIdFromUrl();
			}
			/*
			 * this.isFromGoodRead =
			 * bundle.getBoolean(AppConstant.GOING_GOODREAD);
			 */
			Log.e("SINGLE_NEWS", "cat_id: " + this.itemCategoryId + " id: "
					+ itemId);

			if(AppUtility.isInternetConnected())
			{
				internetErrorTxt.setVisibility(View.GONE);
				initApiCall(this.itemId, this.itemCategoryId);
			}
			else
			{
				
				internetErrorTxt.setVisibility(View.VISIBLE);
			}
			
			
		}
		
		//initApiCall("143", "");
		//initApiCall("371", "");
		
		
		
		final AdView mAdView = (AdView)this.findViewById(R.id.adView);
		mAdView.setVisibility(View.GONE);
		
		if(UserHelper.isLoggedIn() == false)
		{
			mAdView.setVisibility(View.VISIBLE);
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
		else
		{
			if(userHelper.getUser().getAccessType() == UserAccessType.FREE)
			{
				mAdView.setVisibility(View.VISIBLE);
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
			else
			{
				mAdView.setVisibility(View.GONE);
			}
			
		}
	}
	
	private String getItemIdFromUrl()
	{
		Uri data = getIntent().getData();
	    Log.d(TAG, data.toString());
	    String scheme = data.getScheme(); 
	    Log.d(TAG, scheme);
	    String host = data.getHost(); 
	    Log.d(TAG, host);
	    String inurl = data.toString();
	    String[] tokens=inurl.split("-");
	    return tokens[tokens.length-1];
	  
	}
	
	
	private void initViews()
	{
		this.fragmentContainer = (RelativeLayout)this.findViewById(R.id.fragmentContainer);
		internetErrorTxt=(TextView)this.findViewById(R.id.internet_error_text);
		btnGoodRead = (ImageButton)this.findViewById(R.id.btn_goodread);
		btnCandle = (ImageButton)this.findViewById(R.id.btn_candle);
		btnMySchool = (ImageButton)this.findViewById(R.id.btn_myschool);
		
		if(UserHelper.isLoggedIn()){
			if(userHelper.getUser().getAccessType()==UserAccessType.PAID){
				btnMySchool.setBackgroundResource(R.drawable.btn_my_diary_pop);
			}
		}
		
		btnGoodRead.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (UserHelper.isLoggedIn()) {
					startActivity(new Intent(SingleItemShowActivity.this,GoodReadActivity.class));
				} else {
					/*uiHelper.showSuccessDialog(
							getString(R.string.not_logged_in_msg), "Dear User");*/
					showCustomDialog("GOOD READ",
							R.drawable.goodread_popup_icon,getResources().getString(R.string.good_read_msg)+"\n"+ getResources()
							.getString(R.string.not_logged_in_msg));
					
				}
			}
		});
		
		btnCandle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (UserHelper.isLoggedIn()) {
					startActivity(new Intent(SingleItemShowActivity.this,
							CandleActivity.class));
				} else {
					/*uiHelper.showSuccessDialog(
							getString(R.string.not_logged_in_msg), "Dear User");*/
					showCustomDialog("CANDLE",
							R.drawable.candle_popup_icon,getResources().getString(R.string.candle_msg)+"\n"+ getResources()
									.getString(R.string.not_logged_in_msg));
					
				}

			}
		});
		
		btnMySchool.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (UserHelper.isLoggedIn()) {
					switch (UserHelper.getUserAccessType()) {
					case FREE:
						if (userHelper.getUser().isJoinedToSchool()) {
							Intent schoolIntent = new Intent(SingleItemShowActivity.this,
									SingleSchoolFreeVersionActivity.class);
							schoolIntent
									.putExtra(AppConstant.SCHOOL_ID, UserHelper
											.getJoinedSchool().getSchool_id());
							startActivity(schoolIntent);

						} else {
							Intent schoolIntent = new Intent(SingleItemShowActivity.this,
									SchoolFreeVersionActivity.class);
							startActivity(schoolIntent);
						}
						break;

					case PAID:
						/*if (userHelper.getUser().getType() == UserTypeEnum.PARENTS)
							startActivityForResult(
									new Intent(SingleItemShowActivity.this,
											ChildSelectionActivity.class),CommonChildFragment.REQUEST_CODE_CHILD_SELECTION);
						else{
							//(SingleItemShowActivity.this).setActionBarTitle(userHelper.getUser().getPaidInfo().getSchool_name());
							//((HomePageFreeVersion)getActivity()).loadFragment(PaidVersionHomeFragment.newInstance(1));	
						} */
							
						//setResult(RESULT_OK,new Intent());
						parentSetResult();
						finish();
						break;

					default:
						break;
					}
				} else {
					Intent schoolIntent = new Intent(SingleItemShowActivity.this,
							SchoolFreeVersionActivity.class);
					startActivity(schoolIntent);
				}
			}
		});
		
		
		this.layoutAssementHolder = (LinearLayout)this.findViewById(R.id.layoutAssementHolder);
		
		this.layoutAssementHolder.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				/*Intent intent = new Intent(SingleItemShowActivity.this, AssesmentActivity.class);
				
				JsonObject data2 = new Gson().fromJson(objectPost.toString(), JsonObject.class);
	            String rootObj2 = new Gson().toJson(data2);
				intent.putExtra(AppConstant.POST_FRAG_OBJECT2, rootObj2);
				
				
				startActivity(intent);*/
				
				initApiCallAssessment();
				
				
			}
		});
	}
	
	
	
	
	public void showCustomDialog(String headerText, int imgResId,
			String descriptionText) {

			PopupDialog picker = PopupDialog.newInstance(0);
			picker.setData(headerText,descriptionText,imgResId, SingleItemShowActivity.this);
			picker.show(SingleItemShowActivity.this.getSupportFragmentManager(), null);
	}
	
	
	public void showCustomDialogAssessmentInvok(boolean canPlayNow, String headerText, String titleText, String noOfQuestion, String totalScore, String highestScore, String totalTime, String totalPlayed, int iconResId,
			Context context) {

			PopupDialogSingleItemAssessmentInvok picker = PopupDialogSingleItemAssessmentInvok.newInstance(0);
			picker.setData(canPlayNow, headerText, titleText, noOfQuestion, totalScore, highestScore, totalTime, totalPlayed, iconResId, context, new PopupDialogSingleItemAssessmentInvok.IStartButtonClick() {
				
				@Override
				public void onStartButtonClick() {
					// TODO Auto-generated method stub
					Intent intent = new Intent(SingleItemShowActivity.this, AssesmentActivity.class);
					
					JsonObject data2 = new Gson().fromJson(objectPost.toString(), JsonObject.class);
		            String rootObj2 = new Gson().toJson(data2);
					intent.putExtra(AppConstant.POST_FRAG_OBJECT2, rootObj2);
					
					
					startActivity(intent);
				}

				@Override
				public void onLeaderBoardButtonClick() {
					// TODO Auto-generated method stub
					Intent intent = new Intent(SingleItemShowActivity.this, AssessmentLeaderBoardActivity.class);
					intent.putExtra("assessment_id", assesmentId);
					startActivity(intent);
					
				}
			});
			picker.show(SingleItemShowActivity.this.getSupportFragmentManager(), null);
	}
	
	
	public void initApiCall(String itemId, String itemCategoryId) {

		Log.e("SINGLE_NEWS_INITAPICALL", "cat_id: " + this.itemCategoryId
				+ " id: " + itemId);

		RequestParams params = new RequestParams();
		params.put("id", itemId);
		
		if(itemCategoryId != null)
			params.put("category_id", itemCategoryId);

		
		if (UserHelper.isLoggedIn())
			params.put(RequestKeyHelper.USER_ID, UserHelper.getUserFreeId());

		AppRestClient.post(URLHelper.URL_FREE_VERSION_SINGLENEWS, params,singleNewsHandler);
		/*AppRestClient.post(URLHelper.URL_FREE_VERSION_SINGLENEWS, params,
				singleNewsHandler);*/
	}
	
	
	public void initApiCall(String itemId, String mainId, String itemCategoryId) {

		Log.e("SINGLE_NEWS_INITAPICALL", "cat_id: " + this.itemCategoryId
				+ " id: " + itemId);

		RequestParams params = new RequestParams();
		params.put("id", itemId);
		params.put("main_id", mainId);
		if(itemCategoryId != null)
			params.put("category_id", itemCategoryId);

		
		
		if (UserHelper.isLoggedIn())

			params.put(RequestKeyHelper.USER_ID, UserHelper.getUserFreeId());

		/*AppRestClient.post(URLHelper.URL_FREE_VERSION_SINGLENEWS, params,
				singleNewsHandler);*/
		AppRestClient.post(URLHelper.URL_FREE_VERSION_SINGLENEWS, params,singleNewsHandler);
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

			if (modelContainer.getStatus().getCode() == 200) {
				
				objectRoot = modelContainer.getData().getAsJsonObject();
				
				objectPost = modelContainer.getData().getAsJsonObject().get("post").getAsJsonObject();
				
				normalPostType = objectPost.get("normal_post_type").getAsInt();
				
				//String assessmentId = objectPost.get("assessment_id").getAsString();
				
				assesmentId = objectPost.get("assessment_id").getAsString();
				
				
				if(TextUtils.isEmpty(assesmentId) || assesmentId.equalsIgnoreCase("0"))
				{
					layoutAssementHolder.setVisibility(View.GONE);
				}
				else
				{
					layoutAssementHolder.setVisibility(View.VISIBLE);
				}
				
				
				String forceType = objectPost.get("force_web_view_mobie").getAsString();
				if(forceType.equalsIgnoreCase("1"))
				{
					fragment = new SingleFragmentTypeWebView();
		            fragmentManager = getSupportFragmentManager();
		            fragmentTransaction = fragmentManager.beginTransaction();
		            fragmentTransaction.replace(R.id.fragmentContainer, fragment);

		            totalObj = new Gson().fromJson(objectRoot.toString(), JsonObject.class);
		            totalObjString = new Gson().toJson(totalObj);
		            
		            data = new Gson().fromJson(objectPost.toString(), FreeVersionPost.class);
		            rootObj = new Gson().toJson(data);
		            
		            data2 = new Gson().fromJson(objectPost.toString(), JsonObject.class);
		            rootObj2 = new Gson().toJson(data2);
		            

		            bundle = new Bundle();
		            bundle.putString(AppConstant.POST_FRAG_OBJECT, rootObj);
		            bundle.putString(AppConstant.POST_FRAG_OBJECT2, rootObj2);
		            bundle.putString(AppConstant.POST_FRAG_OBJECT_TOTAL, totalObjString);
		            
		            
		            fragment.setArguments(bundle);
		            
		            
		            fragmentTransaction.commit();
				}
				else
				{
					loadFragment(normalPostType);
				}
			}

			else {

			}

		};

	};
	
	
	
	Fragment fragment;
	FragmentManager fragmentManager;
	FragmentTransaction fragmentTransaction;
	FreeVersionPost data;
	String rootObj;
	JsonObject data2;
	String rootObj2;
	Bundle bundle;
	
	JsonObject totalObj;
	String totalObjString;
	
	
	
	private void loadFragment(int normalPostType)
	{
		if(normalPostType == 0)
		{
			fragment = new SingleFragmentTypeZero();
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragmentContainer, fragment);

            totalObj = new Gson().fromJson(objectRoot.toString(), JsonObject.class);
            totalObjString = new Gson().toJson(totalObj);
            
            data = new Gson().fromJson(objectPost.toString(), FreeVersionPost.class);
            rootObj = new Gson().toJson(data);
            
            data2 = new Gson().fromJson(objectPost.toString(), JsonObject.class);
            rootObj2 = new Gson().toJson(data2);
            

            bundle = new Bundle();
            bundle.putString(AppConstant.POST_FRAG_OBJECT, rootObj);
            bundle.putString(AppConstant.POST_FRAG_OBJECT2, rootObj2);
            bundle.putString(AppConstant.POST_FRAG_OBJECT_TOTAL, totalObjString);
            
            
            fragment.setArguments(bundle);
            
            
            fragmentTransaction.commit();
		}
		else if(normalPostType == 2)
		{
			fragment = new SingleFragmentTypeOne();
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragmentContainer, fragment);
            
            totalObj = new Gson().fromJson(objectRoot.toString(), JsonObject.class);
            totalObjString = new Gson().toJson(totalObj);

            data = new Gson().fromJson(objectPost.toString(), FreeVersionPost.class);
            rootObj = new Gson().toJson(data);
            
            
            data2 = new Gson().fromJson(objectPost.toString(), JsonObject.class);
            rootObj2 = new Gson().toJson(data2);
            

            bundle = new Bundle();
            bundle.putString(AppConstant.POST_FRAG_OBJECT, rootObj);
            bundle.putString(AppConstant.POST_FRAG_OBJECT2, rootObj2);
            bundle.putString(AppConstant.POST_FRAG_OBJECT_TOTAL, totalObjString);
            
            
            fragment.setArguments(bundle);
            
            
            fragmentTransaction.commit();
		}
		
		else if(normalPostType == 4)
		{
			fragment = new SingleFragmentTypeFour();
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragmentContainer, fragment);
            
            totalObj = new Gson().fromJson(objectRoot.toString(), JsonObject.class);
            totalObjString = new Gson().toJson(totalObj);

            data = new Gson().fromJson(objectPost.toString(), FreeVersionPost.class);
            rootObj = new Gson().toJson(data);
            
            
            data2 = new Gson().fromJson(objectPost.toString(), JsonObject.class);
            rootObj2 = new Gson().toJson(data2);
            

            bundle = new Bundle();
            bundle.putString(AppConstant.POST_FRAG_OBJECT, rootObj);
            bundle.putString(AppConstant.POST_FRAG_OBJECT2, rootObj2);
            bundle.putString(AppConstant.POST_FRAG_OBJECT_TOTAL, totalObjString);
            
            
            fragment.setArguments(bundle);
            
            
            fragmentTransaction.commit();
		}
		
		
		else if(normalPostType == 3)
		{
			fragment = new SingleFragmentTypeFive();
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragmentContainer, fragment);
            
            totalObj = new Gson().fromJson(objectRoot.toString(), JsonObject.class);
            totalObjString = new Gson().toJson(totalObj);

            data = new Gson().fromJson(objectPost.toString(), FreeVersionPost.class);
            rootObj = new Gson().toJson(data);
            
            
            data2 = new Gson().fromJson(objectPost.toString(), JsonObject.class);
            rootObj2 = new Gson().toJson(data2);
            

            bundle = new Bundle();
            bundle.putString(AppConstant.POST_FRAG_OBJECT, rootObj);
            bundle.putString(AppConstant.POST_FRAG_OBJECT2, rootObj2);
            bundle.putString(AppConstant.POST_FRAG_OBJECT_TOTAL, totalObjString);
            
            
            fragment.setArguments(bundle);
            
            
            fragmentTransaction.commit();
		}
		
		
		else if(normalPostType == 7)
		{
			fragment = new SingleFragmentTypeTwo();
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragmentContainer, fragment);
            
            totalObj = new Gson().fromJson(objectRoot.toString(), JsonObject.class);
            totalObjString = new Gson().toJson(totalObj);

            data = new Gson().fromJson(objectPost.toString(), FreeVersionPost.class);
            rootObj = new Gson().toJson(data);
            
            
            data2 = new Gson().fromJson(objectPost.toString(), JsonObject.class);
            rootObj2 = new Gson().toJson(data2);
            

            bundle = new Bundle();
            bundle.putString(AppConstant.POST_FRAG_OBJECT, rootObj);
            bundle.putString(AppConstant.POST_FRAG_OBJECT2, rootObj2);
            bundle.putString(AppConstant.POST_FRAG_OBJECT_TOTAL, totalObjString);
            
            
            fragment.setArguments(bundle);
            
            
            fragmentTransaction.commit();
		}
		
		
		
		else
		{
			fragment = new SingleFragmentTypeZero();
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragmentContainer, fragment);
            
            totalObj = new Gson().fromJson(objectRoot.toString(), JsonObject.class);
            totalObjString = new Gson().toJson(totalObj);

            data = new Gson().fromJson(objectPost.toString(), FreeVersionPost.class);
            rootObj = new Gson().toJson(data);
            
            
            data2 = new Gson().fromJson(objectPost.toString(), JsonObject.class);
            rootObj2 = new Gson().toJson(data2);
            

            bundle = new Bundle();
            bundle.putString(AppConstant.POST_FRAG_OBJECT, rootObj);
            bundle.putString(AppConstant.POST_FRAG_OBJECT2, rootObj2);
            bundle.putString(AppConstant.POST_FRAG_OBJECT_TOTAL, totalObjString);
            
            
            fragment.setArguments(bundle);
            
            
            fragmentTransaction.commit();
		}
		
		
		/*switch (normalPostType) {
		case 0:
			
			 fragment = new SingleFragmentTypeZero();
             fragmentManager = getSupportFragmentManager();
             fragmentTransaction = fragmentManager.beginTransaction();
             fragmentTransaction.replace(R.id.fragmentContainer, fragment);

             data = new Gson().fromJson(objectPost.toString(), FreeVersionPost.class);
             rootObj = new Gson().toJson(data);
             
             
             data2 = new Gson().fromJson(objectPost.toString(), JsonObject.class);
             rootObj2 = new Gson().toJson(data2);
             

             bundle = new Bundle();
             bundle.putString(AppConstant.POST_FRAG_OBJECT, rootObj);
             bundle.putString(AppConstant.POST_FRAG_OBJECT2, rootObj2);
             
             
             fragment.setArguments(bundle);
             
             
             fragmentTransaction.commit();
			
			
			
			break;
			
			
		case 1:
			
			 fragment = new SingleFragmentTypeOne();
             fragmentManager = getSupportFragmentManager();
             fragmentTransaction = fragmentManager.beginTransaction();
             fragmentTransaction.replace(R.id.fragmentContainer, fragment);

             data = new Gson().fromJson(objectPost.toString(), FreeVersionPost.class);
             rootObj = new Gson().toJson(data);
             
             
             data2 = new Gson().fromJson(objectPost.toString(), JsonObject.class);
             rootObj2 = new Gson().toJson(data2);
             

             bundle = new Bundle();
             bundle.putString(AppConstant.POST_FRAG_OBJECT, rootObj);
             bundle.putString(AppConstant.POST_FRAG_OBJECT2, rootObj2);
             
             
             fragment.setArguments(bundle);
             
             
             fragmentTransaction.commit();
			
			
			
			
			break;
			
			
		case 2:
			
			
			break;

		default:
			
			break;
		}*/
	}
	
	private void initApiCallAssessment() {


		RequestParams params = new RequestParams();
		params.put("assesment_id", assesmentId);
		
		if(UserHelper.isLoggedIn())
		{
			params.put(RequestKeyHelper.USER_ID, UserHelper.getUserFreeId());
		}

		/*AppRestClient.post(URLHelper.URL_GET_ASSESSMENT, params,
				assessmentHandler);*/
		AppRestClient.post(URLHelper.URL_GET_ASSESSMENT, params,assessmentHandler);
	}
	
	
	private AsyncHttpResponseHandler assessmentHandler = new AsyncHttpResponseHandler() {

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
			//Log.e("ASSESSMENT", "data: "+responseString);

			uiHelper.dismissLoadingDialog();

			Wrapper modelContainer = GsonParser.getInstance()
					.parseServerResponse(responseString);

			if (modelContainer.getStatus().getCode() == 200) {
				
				JsonObject assessment = modelContainer.getData().get("assesment").getAsJsonObject();
				
				String title = assessment.get("title").getAsString();
				String topic = assessment.get("topic").getAsString();
				String time = assessment.get("created_date").getAsString();
				String playCount = assessment.get("played").getAsString();
				String highestScore = modelContainer.getData().get("higistmark").getAsString();
				
				
				JsonArray arrayquestion = assessment.get("question").getAsJsonArray();
				
				List<AssessmentQuestion> data = parseQuestion(arrayquestion.toString());
				listAssessmentQuestion = data;
				
				int totalScore = 0;
				int totalTime = 0;
				
				for(int i=0;i<listAssessmentQuestion.size();i++)
				{
					
					int sc = Integer.parseInt(listAssessmentQuestion.get(i).getMark());
					totalScore = totalScore+sc;
					
					int tm = Integer.parseInt(listAssessmentQuestion.get(i).getTime());
					totalTime = totalTime+tm;
					
				}
				
				
				boolean canplayNow = modelContainer.getData().get("can_play").getAsBoolean();
				String lastPlayed = modelContainer.getData().get("last_played").getAsString();

				if(canplayNow == true)
				{
					showCustomDialogAssessmentInvok(canplayNow, "QUIZ", title, String.valueOf(arrayquestion.size()), String.valueOf(totalScore), highestScore,
							String.valueOf(totalTime), playCount, R.drawable.assessment_icon_popup, SingleItemShowActivity.this);
				}
				else
				{
					showCustomDialogAssessmentInvok(canplayNow, "QUIZ", "Thank you for your interest, you can try this particular quiz tomorrow. Try our other quizes.\n\nLast Played: "+lastPlayed, "", "", "",
							"", "", R.drawable.assessment_icon_popup, SingleItemShowActivity.this);
				}
				
				
				
			}

			else {

			}

		};

	};
	
	
	public void shareAll()
	{
		if (AppUtility.isInternetConnected()) 
		{
			(SingleItemShowActivity.this).sharePostUniversal(data);
		}
	}
	
	
	public List<AssessmentQuestion> parseQuestion(String object) {

		List<AssessmentQuestion> tags = new ArrayList<AssessmentQuestion>();
		Type listType = new TypeToken<List<AssessmentQuestion>>() {
		}.getType();
		tags = (List<AssessmentQuestion>) new Gson().fromJson(object, listType);
		return tags;
	}
	
	
	@Override
	public void onClick(View view) {
		
		switch (view.getId()) {
		case R.id.back_btn_home:
			
			Intent intent = new Intent(this, HomePageFreeVersion.class);
			//intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); 
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT); 
			startActivity(intent);
			finish(); 
			
			break;

		default:
			break;
		}
		// TODO Auto-generated method stub
		//super.onClick(view);
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this, HomePageFreeVersion.class);
		//intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); 
		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT); 
		startActivity(intent);
		finish(); 
		
		
		super.onBackPressed();
	}
	
	

}
