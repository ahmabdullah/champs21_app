package com.classtune.freeversion;



import com.classtune.schoolapp.fragments.CommonChildFragment;

import com.classtune.schoolapp.fragments.SuperAwesomeCardFragment;
import com.classtune.schoolapp.fragments.VideoFragment;
import com.classtune.schoolapp.viewhelpers.PagerSlidingTabStrip.IconTabProvider;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabPagerAdapterFreeVersion extends FragmentPagerAdapter implements IconTabProvider{
	
	
	private int[] ICONS;
	private int[] ICONS_TAP;
	
	public TabPagerAdapterFreeVersion(FragmentManager fm, int[] arrayOfIcons, int[] arrayOfIconsTap) {
		super(fm);
		// TODO Auto-generated constructor stub
		this.ICONS = arrayOfIcons;
		this.ICONS_TAP = arrayOfIconsTap;
	}

	@Override
	public Fragment getItem(int position) {
		// TODO Auto-generated method stub
		switch (position) 
		{
			case 0:
//				return new FreeVersionHomeFragment();
				return CommonChildFragment.newInstance(-1,"");
			case 1:
				return CommonChildFragment.newInstance(6,"");
			case 2:
				return CommonChildFragment.newInstance(38,"");
			case 3:
				return CommonChildFragment.newInstance(3,"");
			case 4:
				return CommonChildFragment.newInstance(47,"");
			case 5:
				return CommonChildFragment.newInstance(1,"");
			case 6:
				return CommonChildFragment.newInstance(5,"");
			case 7:
				//return FitnessFragment.newInstance(11);
				return CommonChildFragment.newInstance(2,"");
			case 8:
				return CommonChildFragment.newInstance(4,"");
			case 9:
				return CommonChildFragment.newInstance(36,"");
			case 10:
				return CommonChildFragment.newInstance(37,"");
			case 11:
				return CommonChildFragment.newInstance(48,"");
			case 12:
				return  VideoFragment.newInstance(49);
			/*case 13:
				return new SchoolAllFragment();*/
			default:
				return SuperAwesomeCardFragment.newInstance(position);
		}  
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
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
