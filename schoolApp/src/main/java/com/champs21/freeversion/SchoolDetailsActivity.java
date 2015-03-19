package com.champs21.freeversion;


import java.util.ArrayList;
import java.util.List;

import uk.co.deanwild.flowtextview.FlowTextView;
import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.champs21.schoolapp.R;
import com.champs21.schoolapp.model.SchoolActivities;
import com.champs21.schoolapp.model.SchoolDetails;
import com.champs21.schoolapp.model.SchoolDetails.SchoolPages;
import com.champs21.schoolapp.model.Wrapper;
import com.champs21.schoolapp.networking.AppRestClient;
import com.champs21.schoolapp.utils.AppConstant;
import com.champs21.schoolapp.utils.AppUtility;
import com.champs21.schoolapp.utils.GsonParser;
import com.champs21.schoolapp.utils.SchoolApp;
import com.champs21.schoolapp.utils.URLHelper;
import com.champs21.schoolapp.viewhelpers.CustomTabButton;
import com.champs21.schoolapp.viewhelpers.CustomTextView;
import com.champs21.schoolapp.viewhelpers.PagerContainer;
import com.champs21.schoolapp.viewhelpers.UninterceptableViewPager;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;


public class SchoolDetailsActivity extends ChildContainerActivity implements ObservableScrollView.Callbacks{
	
	
	private List<SchoolPages> listSchoolPages;
	
	private LinearLayout scrollLinearLayout;
	
	private ImageView imgCover;
	private JsonObject objSchool;
	
	private SchoolDetails schoolData;
	
	//private ImageView imgLogo;
	
	private CustomTextView txtSchoolName;
	private CustomTextView txtLocation;
	
	
	//private UIHelper uiHelper;
	
	private List<CustomTabButton> listBtn = new ArrayList<CustomTabButton>();
	
	private CustomTabButton currentButton;
	
	
	private FlowTextView webView;
	private WebView webView2;
	
	
	private String content = "";
	private String contentWebView = "";
	private String menutitle = "";
	
	private String contentActivity = "";
	
	//private ViewPager imageViewPager;
	
	//private ImageViewPagerAdapter imageAdapter;
	private List<String> listImage = new ArrayList<String>();
	private List<String> listImageActivity = new ArrayList<String>();
	
	
	private PagerContainer container;
	private UninterceptableViewPager imageViewPager;
	
	
	
	
	private LinearLayout layoutPagerHolder;
	//private LinearLayout layoutPagerHolder2;
	
	private TextView txtSeeAll; 
	
	private ImageView imgView;
	private TextView txtTitle;
	private TextView txtSummery;
	private LinearLayout layoutActivityHolder;
	
	private String menuId = "";
	
	
	
	private LinearLayout mStickyView;
	private View mPlaceholderView;
	private ObservableScrollView mObservableScrollView;

	private CustomTextView txtMenuTitle;
	
	private ImageView imageAbout;
	
	private LinearLayout layoutAboutHolder;
	
	private LinearLayout layoutActivityInnerHolder;
	
	private LinearLayout layoutUpperPannel;
	
	private LinearLayout layoutWebViewTwo;
	
	private HorizontalScrollView horizontalScrollView;
	
	private ProgressBar progressImgBar;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(com.champs21.schoolapp.R.layout.school_details_activity3);
		
		//uiHelper = new UIHelper(this);
		
		Intent intent = getIntent();
		String val = intent.getExtras().getString(AppConstant.SCHOOL_DATA);
		String val1 = intent.getExtras().getString(AppConstant.SCHOOL_PAGE_DATA);
		
		
		JsonArray arraySchoolPage = new Gson().fromJson(val1, JsonArray.class);
		listSchoolPages = parseSchoolPages(arraySchoolPage.toString());

		
		
		objSchool = new Gson().fromJson(val, JsonObject.class);
		
		
		
		/*schoolData = new SchoolDetails();
		schoolData.setId(objSchool.get("id").getAsString());
		schoolData.setName(objSchool.get("name").getAsString());
		schoolData.setDivision(objSchool.get("division").getAsString());
		schoolData.setLocation(objSchool.get("location").getAsString());
		schoolData.setViews(objSchool.get("views").getAsString());
		schoolData.setLogoUrl(objSchool.get("logo").getAsString());
		schoolData.setCoverUrl(objSchool.get("cover").getAsString());
		schoolData.setLikeLinkUrl(objSchool.get("like_link").getAsString());*/
		
		
		
		
		
		
		initView();
	}

	
	private void initView()
	{
		this.progressImgBar = (ProgressBar)this.findViewById(R.id.progressImgBar);
		
		this.imgCover = (ImageView)this.findViewById(R.id.imgCover);
		SchoolApp.getInstance().displayUniversalImage(objSchool.get("cover").getAsString(), this.imgCover, this.progressImgBar);
		
		//this.imgLogo = (ImageView)this.findViewById(R.id.imgLogo);
		//SchoolApp.getInstance().displayUniversalImage(objSchool.get("logo").getAsString(), this.imgLogo);
		
		this.txtSchoolName = (CustomTextView)this.findViewById(R.id.txtSchoolName);
		this.txtSchoolName.setText(objSchool.get("name").getAsString());
		
		
		this.txtLocation = (CustomTextView)this.findViewById(R.id.txtLocation);
		this.txtLocation.setText(objSchool.get("location").getAsString()+", "+objSchool.get("division").getAsString());
		
		
		this.scrollLinearLayout = (LinearLayout)this.findViewById(R.id.subCategoryTabLayout);
		
		
		
		
		for(int i=0;i<listSchoolPages.size();i++)
		{
			CustomTabButton btn = createCustomTabButtn(listSchoolPages.get(i).getName(), 
					objSchool.get("id").getAsString(), listSchoolPages.get(i).getMenuId(), listSchoolPages.get(i).getId());
			
			listBtn.add(btn);
			
			LinearLayout LL = new LinearLayout(this);
		    LL.setBackgroundColor(getResources().getColor(R.color.gray_4));
		    LL.setOrientation(LinearLayout.VERTICAL);

		    LayoutParams LLParams = new LayoutParams(2,LayoutParams.MATCH_PARENT);
		    LL.setLayoutParams(LLParams);
		    
		    if(i > 0 && i <= listSchoolPages.size()-1)
		    	this.scrollLinearLayout.addView(LL);
			
			this.scrollLinearLayout.addView(btn);
			
		}
		
	
		
		this.webView = (FlowTextView)this.findViewById(R.id.webView);
		this.webView2 = (WebView)this.findViewById(R.id.webView2);
		
		
		
		initApiCall(objSchool.get("id").getAsString(), String.valueOf(1), listSchoolPages.get(0).getId());
		
		listBtn.get(0).setBackgroundResource(R.drawable.white_selected);
		
		//setTabSelected(btn);
		
		
		this.container = (PagerContainer) this.findViewById(R.id.pager_container);
		this.imageViewPager = (UninterceptableViewPager)this.container.findViewById(R.id.imageViewPager);
		this.imageViewPager.setPageMargin(4);
		this.imageViewPager.setClipChildren(false);
		this.imageViewPager.setOffscreenPageLimit(1);
		
		
		
		this.layoutPagerHolder = (LinearLayout)this.findViewById(R.id.layoutPagerHolder);
		
		
		
		this.txtSeeAll = (TextView)this.findViewById(R.id.txtSeeAll);
		
		this.txtSeeAll.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(SchoolDetailsActivity.this, SchoolAllActivities.class);
				intent.putExtra(AppConstant.SCHOOL_ID, objSchool.get("id").getAsString());
				startActivity(intent);
			}
		});
		
		this.imgView = (ImageView)this.findViewById(R.id.imgView);
		this.txtTitle = (TextView)this.findViewById(R.id.txtTitle);
		this.txtSummery = (TextView)this.findViewById(R.id.txtSummery);
		
		this.layoutActivityHolder = (LinearLayout)this.findViewById(R.id.layoutActivityHolder);
		
		
		
		//for sticky
		mObservableScrollView = (ObservableScrollView)this.findViewById(R.id.scroll_view);
        mObservableScrollView.setCallbacks(this);
        mStickyView = (LinearLayout) this.findViewById(R.id.sticky);
		mPlaceholderView = this.findViewById(R.id.placeholder);
		
        
      /*  LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mPlaceholderView.getLayoutParams();
		params.height = mStickyView.getHeight();
		mPlaceholderView.setLayoutParams(params);*/
        
        
        
		
        mObservableScrollView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        onScrollChanged(mObservableScrollView.getScrollY());
                    }
                });
        
        
        txtMenuTitle = (CustomTextView)this.findViewById(R.id.txtMenuTitle);
        
        imageAbout = (ImageView)this.findViewById(R.id.imageAbout);
        
        layoutAboutHolder = (LinearLayout)this.findViewById(R.id.layoutAboutHolder);
        
        layoutActivityInnerHolder = (LinearLayout)this.findViewById(R.id.layoutActivityInnerHolder);
        
        
        layoutUpperPannel = (LinearLayout)this.findViewById(R.id.layoutUpperPannel);
        
        layoutWebViewTwo = (LinearLayout)this.findViewById(R.id.layoutWebViewTwo);
       
        horizontalScrollView = (HorizontalScrollView)this.findViewById(R.id.horizontalScrollView);
        
        
        
        
	}
	
	
	private void loadDataWebView(String data)
	{
		/*final String mimeType = "text/html";
		final String encoding = "UTF-8";

		webView.loadDataWithBaseURL("", data, mimeType, encoding, null);
		webView.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);*/

		if(menuId.equalsIgnoreCase("1"))
		{
			layoutAboutHolder.setVisibility(View.VISIBLE);
			
			
			Spanned html = Html.fromHtml(data);
			webView.setText(html);
			webView.setTextSize(AppUtility.getDeviceIndependentDpFromPixel(SchoolDetailsActivity.this, 16));
			TextPaint mTextPaint;
			
			
			/*Typeface type = Typeface.createFromAsset(getAssets(),"fonts/AliquamREG.ttf"); 
			
			webView.setTypeface(type);
			webView.setTextSize(AppUtility.getDeviceIndependentDpFromPixel(SchoolDetailsActivity.this, 14));*/
			
			
			//webView.setText(data);
			
			/*int base = 275;
			
			String half1 ="";
			String half2="";
			half1 = data.substring(0, 275);
			half2 = data.substring(275);
			
			boolean isWhitespace = Character.isWhitespace(data.charAt(275));
			while(!isWhitespace)
			{
				base--;
				
				isWhitespace = Character.isWhitespace(data.charAt(base));
				half1 = data.substring(0, base);
				half2 = data.substring(base+1);
				
				Log.v("counting array","base: "+base);
			}
				
			
			Log.v("counting array","array is total: "+data);
			Log.v("counting array","length is: "+data.length());
			Log.v("counting array","array is one: "+half1);
			Log.v("counting array","array is two: "+half2);
			
		
			
			webView.setText(half1);
			
			webView2.setText(half2);*/
			
			
			
		}
		else
		{
			layoutAboutHolder.setVisibility(View.GONE);
			
			//webView2.setText(data);
		}
		
		//txtMenuTitle.setText(menutitle);
		
		Log.e("MENU_ID", "is: "+menuId);
		
		
		
		
		
		
		
	}


	@SuppressLint({ "SetJavaScriptEnabled", "NewApi" })
	private void showWebViewContent(String text, WebView webView) {
		
		webView.removeAllViews();
		final String mimeType = "text/html";
		final String encoding = "UTF-8";

		//webView.loadDataWithBaseURL("", text, mimeType, encoding, null);
		webView.loadData(text, mimeType, encoding);
		/*webView.getSettings().setLayoutAlgorithm(
				LayoutAlgorithm.SINGLE_COLUMN);*/
		
		WebSettings webViewSettings = webView.getSettings();
		webViewSettings.setJavaScriptEnabled(true);

		//webViewSettings.setPluginState(WebSettings.PluginState.ON);
		
		//webView.setWebChromeClient(new WebChromeClient());


		
		//webViewSettings.setUseWideViewPort(true);
		//webViewSettings.setLoadWithOverviewMode(true);
		webViewSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		
		//webViewSettings.setDefaultFontSize(18);
		//webViewSettings.setTextZoom(90);
		
		//(int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 18, getResources().getDisplayMetrics())
		//webViewSettings.setMinimumFontSize(18);
		//webViewSettings.setMinimumLogicalFontSize(18);
		
		
		//webView.getSettings().setLoadWithOverviewMode(true);
		//webView.getSettings().setUseWideViewPort(true);

		/*webViewSettings.setUseWideViewPort(true);
		webViewSettings.setLoadWithOverviewMode(true);*/
		webView.requestLayout();
				
	}
	
	
	
	private int countWords(String s)
	{

	    int counter = 0;

	    boolean word = false;
	    int endOfLine = s.length() - 1;

	    for (int i = 0; i < s.length(); i++) {
	        // if the char is letter, word = true.
	        if (Character.isLetter(s.charAt(i)) == true && i != endOfLine) {
	            word = true;
	            // if char isnt letter and there have been letters before (word
	            // == true), counter goes up.
	        } else if (Character.isLetter(s.charAt(i)) == false && word == true) {
	            counter++;
	            word = false;
	            // last word of String, if it doesnt end with nonLetter it
	            // wouldnt count without this.
	        } else if (Character.isLetter(s.charAt(i)) && i == endOfLine) {
	            counter++;
	        }
	    }
	    return counter;
	}
	
	
	private CustomTabButton createCustomTabButtn(String text, String schoolId, String menuId, String pageId)
	{
		LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		parms.setMargins(3, 0, 0, 0);
		parms.weight = 1;
		
		CustomTabButton btn = new CustomTabButton(this);
		btn.setClickable(true);
		btn.setGravity(Gravity.CENTER);
		btn.setBackgroundResource(R.drawable.tab_general_btn);
		btn.setLayoutParams(parms);
		btn.setTitleText(text);
		btn.setTitleTextSize(16);
		btn.setOnClickListener(clickListener(schoolId, menuId, pageId));
		
		
		return btn;
	}
	
	
	private View.OnClickListener clickListener(final String schoolId, final String menuId, final String pageId)
	{
		View.OnClickListener listener = new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//setTabSelected(v);
				
				
				
				int scrollX = (v.getLeft() - (getWindowWidth() / 2)) + (v.getWidth() / 2);
			    horizontalScrollView.smoothScrollTo(scrollX, 0);
				
				setTabSelected(v);
				currentButton = (CustomTabButton)v;
				
				for(int i=0;i<listBtn.size();i++)
				{
					setTabUnSelected(listBtn.get(i));
					
					
					
				}
				
				
				setTabSelected(currentButton);
				
				
				initApiCall(schoolId, menuId, pageId);
				
			}
		};
		
		return listener;
	}
	
	@SuppressLint("NewApi")
	private int getWindowWidth()
	{
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		final int width = size.x;
		
		return width;
	}
	
	private void initApiCall(String schoolId, String menuId, String pageId)
	{
		
		RequestParams params = new RequestParams();
		params.put("school_id", schoolId);
		params.put("menu_id", menuId);
		params.put("page_id", pageId);
		
		this.menuId = menuId;
		
		AppRestClient.post(URLHelper.URL_FREE_VERSION_SCHOOL_PAGE, params, 
				schoolPageHandler);
	}
	
	
	
	
	private AsyncHttpResponseHandler schoolPageHandler = new AsyncHttpResponseHandler(){

		@Override
		public void onFailure(Throwable arg0, String arg1) 
		{
			//uiHelper.showMessage(arg1);
			//uiHelper.dismissLoadingDialog();
		};

		@Override
		public void onStart() 
		{
			//uiHelper.showLoadingDialog("Please wait...");
		};

		@Override
		public void onSuccess(String responseString) 
		{
			//Log.e("FREE_HOME", "data: "+responseString);
			listImage.clear();
			listImageActivity.clear();
			
			
			webView.setText("");
			
			
			//webView.removeAllViews();
			layoutPagerHolder.setVisibility(View.GONE);
			
			//uiHelper.dismissLoadingDialog();

			Wrapper modelContainer = GsonParser.getInstance().parseServerResponse(responseString);

			if (modelContainer.getStatus().getCode() == 200)
			{
				//for page_details
				Log.e("SCHOOL_PAGE", "data: "+modelContainer.getData().getAsJsonObject());
				content = modelContainer.getData().getAsJsonObject().get("page_details").getAsJsonObject().get("content").getAsString();
				contentWebView = modelContainer.getData().getAsJsonObject().get("page_details").getAsJsonObject().get("web-view").getAsString();
				
				menutitle = modelContainer.getData().getAsJsonObject().get("page_details").getAsJsonObject().get("title").getAsString();
				JsonArray arrayGallery = modelContainer.getData().getAsJsonObject().get("page_details").getAsJsonObject().get("gallery").getAsJsonArray();
				for(int i=0;i<arrayGallery.size();i++)
				{
					listImage.add(arrayGallery.get(i).getAsString());
				}
				ImageViewPagerAdapter imageAdapter = new ImageViewPagerAdapter(listImage);
				imageAdapter.notifyDataSetChanged();
				imageViewPager.setAdapter(imageAdapter);
				
				
				
				if(listImage.size() > 0)
					layoutPagerHolder.setVisibility(View.VISIBLE);
				else
					layoutPagerHolder.setVisibility(View.GONE);
				
				imageAdapter.notifyDataSetChanged();
				
				if(menuId.equalsIgnoreCase("1"))
				{
					layoutPagerHolder.setVisibility(View.GONE);
					//txtMenuTitle.setVisibility(View.VISIBLE);
				}
				else
				{
					layoutPagerHolder.setVisibility(View.VISIBLE);
					//txtMenuTitle.setVisibility(View.GONE);
				}
				
				ProgressBar imgProgressBarAbout = (ProgressBar)SchoolDetailsActivity.this.findViewById(R.id.imgProgressBarAbout);
				
				//SchoolApp.getInstance().displayUniversalImage(modelContainer.getData().getAsJsonObject().get("page_details").getAsJsonObject().get("image").getAsString(), imageAbout);
				SchoolApp.getInstance().displayUniversalImage(modelContainer.getData().getAsJsonObject().get("page_details").getAsJsonObject().get("image").getAsString(), imageAbout, imgProgressBarAbout);
				
				
				
				//dummy hide
				//layoutPagerHolder.setVisibility(View.GONE);
				
				
				//for activity
				
				if( modelContainer.getData().getAsJsonObject().get("activity").getAsJsonArray().size() > 0)
				{
					layoutActivityHolder.setVisibility(View.VISIBLE);
					
					contentActivity = modelContainer.getData().getAsJsonObject().get("activity").getAsJsonArray().get(0).getAsJsonObject().get("content").getAsString();
					
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
					
					
					ProgressBar progressBarImgView = (ProgressBar)SchoolDetailsActivity.this.findViewById(R.id.progressBarImgView);
					//SchoolApp.getInstance().displayUniversalImage(imgUrl, imgView);
					SchoolApp.getInstance().displayUniversalImage(imgUrl, imgView, progressBarImgView);
					txtTitle.setText(strTitle);
					txtSummery.setText(strSummary);
				}		
				
				else
				{
					layoutActivityHolder.setVisibility(View.GONE);
				}
				
				
				if(!TextUtils.isEmpty(content)){
					webView.setVisibility(View.VISIBLE);
					webView2.setVisibility(View.VISIBLE);
					
					loadDataWebView(content);
					
					if(!menuId.equalsIgnoreCase("1"))
					{
						webView2.setVisibility(View.VISIBLE);
						
						showWebViewContent(contentWebView, webView2);
					}
					else
					{
						webView2.setVisibility(View.GONE);
					}
				}
				else{
					webView.setVisibility(View.GONE);
					webView2.setVisibility(View.GONE);
				}
				
				txtMenuTitle.setText(menutitle);
				
				
				
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
							Intent intent = new Intent(SchoolDetailsActivity.this, SchoolPopulationActivity.class);				
							String val = new Gson().toJson(activity);
							intent.putExtra(AppConstant.ACTIVITY_SINGLE, val);
							startActivity(intent);
						}
					});
				}
				
				
				
				
			} 

			else 
			{

			}
		};
	};
	
	
	
	
	
	public ArrayList<SchoolDetails> parseSchools(String object) {
		ArrayList<SchoolDetails> data = new ArrayList<SchoolDetails>();
		data = new Gson().fromJson(object, new TypeToken<ArrayList<SchoolDetails>>(){
		}.getType());
		return data;
	}
	

	public ArrayList<SchoolPages> parseSchoolPages(String object) {
		ArrayList<SchoolPages> data = new ArrayList<SchoolPages>();
		data = new Gson().fromJson(object, new TypeToken<ArrayList<SchoolPages>>(){
		}.getType());
		return data;
	}
	
	
	
	@SuppressWarnings("unchecked")
	public void setTabSelected(View v) {
		CustomTabButton btn = (CustomTabButton) v;

		/*current.setButtonSelected(false,
				getResources().getColor(R.color.black));*/
		

		btn.setButtonSelected(true, getResources().getColor(R.color.black));
		

	}
	
	@SuppressWarnings("unchecked")
	public void setTabUnSelected(View v) {
		CustomTabButton btn = (CustomTabButton) v;

		

		btn.setButtonSelected(false, getResources().getColor(R.color.black));
		

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
		  ImageView view = new ImageView(SchoolDetailsActivity.this);
		  view.setLayoutParams(new LinearLayout.LayoutParams(150,
				  150));
		  view.setScaleType(ScaleType.FIT_XY);
		  view.setBackgroundResource(android.R.color.transparent);
		  
		 SchoolApp.getInstance().displayUniversalImage(listImage.get(position), view);
		  
		
		  
		  Log.e("LIST_GAL", "is: "+listImage.get(position));
		  
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





	@Override
	public void onScrollChanged(int scrollY) {
		// TODO Auto-generated method stub
		mStickyView.setTranslationY(Math.max(mPlaceholderView.getTop(), scrollY-AppUtility.getDeviceIndependentDpFromPixel(SchoolDetailsActivity.this, 10)));
	}


	@Override
	public void onDownMotionEvent() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onUpOrCancelMotionEvent() {
		// TODO Auto-generated method stub
		
	}
	
	
	
	
	
}
