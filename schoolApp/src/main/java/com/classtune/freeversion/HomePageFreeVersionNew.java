package com.classtune.freeversion;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.classtune.schoolapp.R;
import com.classtune.schoolapp.viewhelpers.PagerSlidingTabStrip;

public class HomePageFreeVersionNew extends Fragment{

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		setUp();
		tabsFirst.setViewPager(pager, tabsFirst);
	}

	public static final String TAG = HomePageFreeVersionNew.class.getSimpleName();

	private View view;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		view = inflater.inflate(R.layout.fragment_homepage_freeversion,
				container, false);
		tabsFirst=(PagerSlidingTabStrip)view.findViewById(R.id.tab);
		pager=(ViewPager)view.findViewById(R.id.pager);
		return view;
	}


	private PagerSlidingTabStrip tabsFirst;
	private TabPagerAdapterFreeVersion adapter;
	private ViewPager pager;
	
    private static int positionPager = 0;
    
    
    public static HomePageFreeVersionNew newInstance() {
		return new HomePageFreeVersionNew();
	}
	
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		
	}
	
	

	private void setUp() {
		
		int[] firstAdapterIconArray = { R.drawable.icon_home,
				R.drawable.icon_resource, R.drawable.icon_newsarticle,
				R.drawable.icon_entertainment, R.drawable.sports_normal,
				R.drawable.icon_games, R.drawable.icon_eca,
				R.drawable.icon_fitness, R.drawable.icon_food,
				R.drawable.travel_normal,R.drawable.icon_personality,
				R.drawable.literature_normal,R.drawable.video_normal};
		
		int[] firstAdapterIconTapArray = { R.drawable.home_tap,
				R.drawable.resource_tap, R.drawable.newsarticle_tap,
				R.drawable.entertainment_tap, R.drawable.sports_tap,
				R.drawable.game_tap, R.drawable.eca_tap,
				R.drawable.fitness_tap, R.drawable.food_tap,
				R.drawable.travel_tap,R.drawable.personality_tap,
				R.drawable.literature_tap, R.drawable.video_tap};
		adapter = new TabPagerAdapterFreeVersion(getChildFragmentManager(),
				firstAdapterIconArray,firstAdapterIconTapArray);
		pager.setOffscreenPageLimit(2);
		pager.setAdapter(adapter);
		
		
		pager.setCurrentItem(positionPager);
	}
}

	
	
	



