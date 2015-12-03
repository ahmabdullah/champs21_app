package com.classtune.freeversion;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.classtune.schoolapp.fragments.GoodReadPostFragment;
import com.classtune.schoolapp.model.GoodReadPostAll;
import com.classtune.freeversion.PagerSlidingTabStripGoodRead.TextTabProvider;

public class TabPagerAdapterGoodRead extends FragmentPagerAdapter implements TextTabProvider{
	
	
	GoodReadPostAll postList;
	
	
	public TabPagerAdapterGoodRead(FragmentManager fm,GoodReadPostAll postList) {
		super(fm);
		// TODO Auto-generated constructor stub
		this.postList = postList;
	}

	@Override
	public Fragment getItem(int position) {
		// TODO Auto-generated method stub
		Log.e("SIZE OF POST IN GOOD READ FOLDER", postList.getGoodreadPostList().get(position).getPost().size()+"");
		switch (position) 
		{
			
			default:
			 return GoodReadPostFragment.newInstance(position, postList.getGoodreadPostList().get(position).getPost(), postList.getGoodreadPostList().get(position).getFolderName());
			 
			
		}  
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return postList.getGoodreadPostList().size();
	}



	@Override
	public String getPageText(int position) {
		// TODO Auto-generated method stub
		return postList.getGoodreadPostList().get(position).getFolderName();
	}
	
	public String getFolderId(int position){
		return null;
	}

}

