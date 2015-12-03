package com.classtune.freeversion;

import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.classtune.schoolapp.R;
import com.classtune.schoolapp.utils.UserHelper;
import com.classtune.schoolapp.viewhelpers.CustomButton;
import com.classtune.schoolapp.viewhelpers.UIHelper;

public class SchoolFreeVersionActivity extends ChildContainerActivity{
	
	public static boolean shouldRefresh=false;
	private PagerSlidingTabStripGoodRead tabsFirst;
	private TabPagerAdapterSchoolFreeVersion adapter;
	private ViewPager pager;
	
    private static int positionPager = 0;
	 
	UIHelper uiHelper;
	//SchoolApp app;
	UserHelper userHelper;
	//HashMap<String, ArrayList<FreeVersionPost>> map;
	private String currentTabKey = "";
	private CustomButton current;
	private static String[] featureList={"SCHOOLS", "FIND SCHOOL", "CREATE SCHOOL PAGE"};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_school_freeversion);
		tabsFirst=(PagerSlidingTabStripGoodRead)findViewById(R.id.tab);
		pager=(ViewPager)findViewById(R.id.pager);
		uiHelper = new UIHelper(this);
		userHelper = new UserHelper(this);
		setUp();
	}

	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	private void setUp() {
		adapter = new TabPagerAdapterSchoolFreeVersion(getSupportFragmentManager(),featureList);
		pager.setOffscreenPageLimit(1);
		pager.setAdapter(adapter);
		pager.setCurrentItem(positionPager);
		tabsFirst.setPagerWeightToOne();
		tabsFirst.setViewPager(pager, tabsFirst);
	}
	/*
	@SuppressWarnings("unchecked")
	public void setTabSelected(View v) {
		CustomButton btn = (CustomButton) v;

		current.setButtonSelected(false,
				getResources().getColor(R.color.black),
				R.drawable.term_icon_normal);

		btn.setButtonSelected(true, getResources().getColor(R.color.black),
				R.drawable.term_icon_tap);
		current = btn;
	}*/
	
}
