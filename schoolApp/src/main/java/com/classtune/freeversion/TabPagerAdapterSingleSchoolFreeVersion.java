package com.classtune.freeversion;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.classtune.freeversion.PagerSlidingTabStripGoodRead.TextTabProvider;
import com.classtune.schoolapp.fragments.SchoolCandleFragment;
import com.classtune.schoolapp.fragments.SchoolFeedFragment;
import com.classtune.schoolapp.fragments.SchoolScrollableFragment;
import com.classtune.schoolapp.fragments.SuperAwesomeCardFragment;

public class TabPagerAdapterSingleSchoolFreeVersion extends FragmentPagerAdapter
		implements TextTabProvider {

	String[] postList;
	
	private String schoolId ;

	public TabPagerAdapterSingleSchoolFreeVersion(FragmentManager fm,
			String[] postList, String schoolId) {
		super(fm);
		// TODO Auto-generated constructor stub
		this.postList = postList;
		this.schoolId= schoolId;
	}
	
	
	
	

	@Override
	public Fragment getItem(int position) {
		// TODO Auto-generated method stub

		switch (position) 
		{
		
		case 0:
			
			return SchoolFeedFragment.newInstance(Integer.parseInt(schoolId));
		
		case 1:
			
			return SchoolScrollableFragment.newInstance(Integer.parseInt(schoolId));
		
		case 2:
			return SchoolCandleFragment.newInstance(Integer.parseInt(schoolId));
			
		default:
			return SuperAwesomeCardFragment.newInstance(position);

		}
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return postList.length;
	}

	@Override
	public String getPageText(int position) {
		// TODO Auto-generated method stub
		return postList[position];
	}

}
