package com.classtune.freeversion;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.classtune.schoolapp.R;
import com.classtune.schoolapp.model.SchoolActivities;
import com.classtune.schoolapp.utils.AppConstant;
import com.classtune.schoolapp.utils.SchoolApp;
import com.classtune.schoolapp.viewhelpers.CustomTextView;
import com.classtune.schoolapp.viewhelpers.PagerContainer;
import com.classtune.schoolapp.viewhelpers.UninterceptableViewPager;
import com.google.gson.Gson;

public class SchoolPopulationActivity extends ChildContainerActivity {
	
	private CustomTextView webView;
	private PagerContainer container;
	private UninterceptableViewPager imageViewPager;
	
	private SchoolActivities activitySingleData;
	private LinearLayout layoutPagerHolder;
	
	private List<String> listImage = new ArrayList<String>();
	
	private LinearLayout redBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.school_population_activity);
		
		Bundle bundle = getIntent().getExtras();
		if(bundle != null)
		{
			String data = bundle.getString(AppConstant.ACTIVITY_SINGLE);
			
			this.activitySingleData = new Gson().fromJson(data, SchoolActivities.class);
			initView();
		}
		
		
		
	}
	
	
	private void initView()
	{
		this.redBar = (LinearLayout)this.findViewById(R.id.redBar);
		this.container = (PagerContainer) this.findViewById(R.id.pager_container);
		this.imageViewPager = (UninterceptableViewPager)this.container.findViewById(R.id.imageViewPager);
		this.imageViewPager.setPageMargin(4);
		this.imageViewPager.setClipChildren(false);
		this.imageViewPager.setOffscreenPageLimit(1);
		
		this.webView = (CustomTextView)this.findViewById(R.id.webView);
		
		
		loadDataWebView(this.webView, this.activitySingleData.getContent());
		
		
		if(activitySingleData.getListGallery() != null)
		{
			for(int i=0;i<activitySingleData.getListGallery().size();i++)
			{
				listImage.add(activitySingleData.getListGallery().get(i));
			}
		}
		
		
		
		
		
		this.layoutPagerHolder = (LinearLayout)this.findViewById(R.id.layoutPagerHolder);
		
		if(listImage.size() > 0)
		{
			this.layoutPagerHolder.setVisibility(View.VISIBLE);
			
			this.redBar.setVisibility(View.VISIBLE);
		}
		else
		{
			this.layoutPagerHolder.setVisibility(View.GONE);
			
			this.redBar.setVisibility(View.GONE);
		}
		
		
		ImageViewPagerAdapter adapter = new ImageViewPagerAdapter(listImage);
		adapter.notifyDataSetChanged();
		this.imageViewPager.setAdapter(adapter);
		
		
	}
	
	private void loadDataWebView(CustomTextView webView, String data)
	{
		/*final String mimeType = "text/html";
		final String encoding = "UTF-8";

		webView.loadDataWithBaseURL("", data, mimeType, encoding, null);
		webView.getSettings().setLayoutAlgorithm(
				LayoutAlgorithm.SINGLE_COLUMN);*/
		webView.setText(Html.fromHtml(data));
		//webView.setText(data);
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
		  ImageView view = new ImageView(SchoolPopulationActivity.this);
		  view.setLayoutParams(new LinearLayout.LayoutParams(150,
				  150));
		  view.setScaleType(ScaleType.FIT_XY);
		 
		  
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

}
