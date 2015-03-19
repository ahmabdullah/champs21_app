package com.champs21.freeversion;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.champs21.freeversion.PagerSlidingTabStripGoodRead.TextTabProvider;
import com.champs21.schoolapp.fragments.AllSchoolListFragment;
import com.champs21.schoolapp.fragments.CreateSchoolFragment;
import com.champs21.schoolapp.fragments.MySchoolListFragment;
import com.champs21.schoolapp.fragments.SchoolSearchFragment;
import com.champs21.schoolapp.fragments.SuperAwesomeCardFragment;

public class TabPagerAdapterSchoolFreeVersion extends FragmentPagerAdapter
		implements TextTabProvider {

	String[] postList;

	public TabPagerAdapterSchoolFreeVersion(FragmentManager fm,
			String[] postList) {
		super(fm);
		// TODO Auto-generated constructor stub
		this.postList = postList;
	}

	@Override
	public Fragment getItem(int position) {
		switch (position) {
		case 0:
			return new AllSchoolListFragment();
		case 1:
			return new SchoolSearchFragment();
		case 2:
			return new CreateSchoolFragment();
		default:
			return SuperAwesomeCardFragment.newInstance(position);

		}
	}

	@Override
	public int getCount() {
		return postList.length;
	}

	@Override
	public String getPageText(int position) {
		// TODO Auto-generated method stub
		return postList[position];
	}

}
