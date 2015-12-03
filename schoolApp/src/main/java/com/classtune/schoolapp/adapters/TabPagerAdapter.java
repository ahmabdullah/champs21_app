/**
 * 
 */
package com.classtune.schoolapp.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.classtune.schoolapp.fragments.HomeworkFragment;
import com.classtune.schoolapp.fragments.NoticeFragment;
import com.classtune.schoolapp.fragments.ParentAcademicCalender;
import com.classtune.schoolapp.fragments.ParentAttendenceFragment;
import com.classtune.schoolapp.fragments.ParentEventFragment;
import com.classtune.schoolapp.fragments.ParentReportCardFragment;
import com.classtune.schoolapp.fragments.RoutineFragment;
import com.classtune.schoolapp.fragments.SuperAwesomeCardFragment;
import com.classtune.schoolapp.fragments.SyllabusFragment;

import com.classtune.schoolapp.fragments.TransportFragment;


import com.classtune.schoolapp.viewhelpers.PagerSlidingTabStrip.IconTabProvider;

/**
 * @author Amit
 *
 */
public class TabPagerAdapter extends FragmentPagerAdapter implements IconTabProvider {

	private int[] ICONS;
	private int[] ICONS_TAP;// = {R.drawable.icon_homework, R.drawable.icon_notification, R.drawable.icon_reminder, R.drawable.icon_routine};
	private boolean isFirstAdapter;

	public TabPagerAdapter(FragmentManager fm, int[] arrayOfIcons, int[] arrayOfIconsTap, boolean isFirstAdapter) {
		super(fm);

		ICONS = arrayOfIcons;
		this.isFirstAdapter = isFirstAdapter;
		ICONS_TAP = arrayOfIconsTap;
	}

	public TabPagerAdapter(FragmentManager fm, boolean isFirstAdapter) {
		super(fm);
		// TODO Auto-generated constructor stub
		this.isFirstAdapter = isFirstAdapter;
	}


	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentPagerAdapter#getItem(int)
	 */
	@Override
	public Fragment getItem(int position) {
		// TODO Auto-generated method stub
		if (isFirstAdapter) {
			switch (position) {
			case 0:
				return new HomeworkFragment();
			case 1:
				return SuperAwesomeCardFragment.newInstance(position);
			case 2:
				return new ParentAcademicCalender();
			case 3:
				return new ParentReportCardFragment();
			default:
				return SuperAwesomeCardFragment.newInstance(position);
			}  
		} else {
			switch (position) {
			case 0:
				return new ParentEventFragment();
			case 1:
				return new ParentAttendenceFragment();
			case 2:
				return new TransportFragment();
			case 3:
				return new SyllabusFragment();
			case 4:
				return new NoticeFragment();
			case 5:
				return new RoutineFragment();
			default:
				return SuperAwesomeCardFragment.newInstance(position);

			}

		}


	}

	/* (non-Javadoc)
	 * @see android.support.v4.view.PagerAdapter#getCount()
	 */
	@Override
	public int getCount() {
		//		// TODO Auto-generated m
		return ICONS.length;
	}

	@Override
	public int getPageIconResId(int position) {
		// TODO Auto-generated method stub
		return ICONS[position];
	}

	@Override
	public int getPageIconResIdTap(int position) {
		// TODO Auto-generated method stub
		return ICONS_TAP[position];
	}
}
