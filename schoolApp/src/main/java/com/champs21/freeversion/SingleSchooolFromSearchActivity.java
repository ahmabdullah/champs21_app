package com.champs21.freeversion;

import java.util.ArrayList;
import java.util.List;

import com.champs21.schoolapp.R;
import com.champs21.schoolapp.fragments.AllSchoolListFragment;
import com.champs21.schoolapp.model.SchoolDetails;
import com.champs21.schoolapp.utils.AppConstant;
import com.champs21.schoolapp.utils.SchoolApp;
import com.google.android.gms.internal.gs;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class SingleSchooolFromSearchActivity extends ChildContainerActivity {
	
	private JsonArray arraySchools;
	private List<SchoolDetails> listSchools;
	private ListView listView;
	
	private SchoolSearchAdapter adapter;

	private Gson gson = new Gson();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.single_school_fromsearch_activity);
		
		
		Bundle bundle = getIntent().getExtras();
		if(bundle != null)
		{
			String val = bundle.getString(AppConstant.LIST_SCHOOL);
			String val2 = bundle.getString(AppConstant.ARRAY_SCHOOL);
			
			listSchools = gson.fromJson(val, new TypeToken<ArrayList<SchoolDetails>>(){}.getType());
			arraySchools = gson.fromJson(val2, JsonArray.class);
			
			
			Log.e("SINGLE_DATA", "data: "+listSchools.get(0).getName());
			
			initView();
		}
		
	}
	
	
	private void initView()
	{
		this.listView = (ListView)this.findViewById(R.id.listView);
		this.adapter = new SchoolSearchAdapter();
		this.listView.setAdapter(this.adapter);
		
		
		this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, 
					long id) {
				// TODO Auto-generated method stub
				
				SchoolDetails data = (SchoolDetails)adapter.getItem(position);
				
				//Log.e("DATA_ITEM_CLICKED", "data: "+data.getName());
				
				/*JsonArray arrayPages = arraySchools.get(position).getAsJsonObject().get("school_pages").getAsJsonArray();	
				
				
				
				
				String val = new Gson().toJson(arraySchools.get(position).getAsJsonObject());
				
				String val1 = new Gson().toJson(arrayPages);
				
				Intent intent = new Intent(SingleSchooolFromSearchActivity.this, SchoolDetailsActivity.class);
				intent.putExtra(AppConstant.SCHOOL_DATA, val);
				intent.putExtra(AppConstant.SCHOOL_PAGE_DATA, val1);
				startActivity(intent);*/
				
				Intent intent = new Intent(SingleSchooolFromSearchActivity.this, SchoolScrollableDetailsActivity.class);
				intent.putExtra(AppConstant.SCHOOL_ID, data.getId());
				startActivity(intent);
				
			}

		
		
		
		});
	}
	
	
	
	public class SchoolSearchAdapter extends BaseAdapter
	{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return listSchools.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return listSchools.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder;
			
			if(convertView == null)
			{
				holder = new ViewHolder();

				convertView = LayoutInflater.from(SingleSchooolFromSearchActivity.this).inflate(R.layout.row_school_list2, parent, false);

				
				holder.imgLogo = (ImageView)convertView.findViewById(R.id.imgLogo);
				holder.txtSchoolName = (TextView)convertView.findViewById(R.id.txtSchoolName);
				holder.txtLocation = (TextView)convertView.findViewById(R.id.txtLocation);
				holder.progressBar = (ProgressBar)convertView.findViewById(R.id.progressBar);
				holder.btnJoin = (ImageButton) convertView.findViewById(R.id.btnJoin);
				
				convertView.setTag(holder);
				
			}
			else
			{
				holder = (ViewHolder)convertView.getTag();
			}
			
			
			holder.btnJoin.setVisibility(View.GONE);
		
			if(listSchools.size() > 0)
			{
				//SchoolApp.getInstance().displayUniversalImage(listSchools.get(position).getLogoUrl(), holder.imgLogo);
				SchoolApp.getInstance().displayUniversalImage(listSchools.get(position).getPicture(), holder.imgLogo, holder.progressBar);
				
				holder.txtSchoolName.setText(listSchools.get(position).getName());
				
				holder.txtLocation.setText(listSchools.get(position).getLocation()+", "+listSchools.get(position).getDivision());

				
				
			}
			
			
			
			return convertView;
		}
		
	}
	
	
	class ViewHolder
	{
		ImageView imgLogo;
		TextView txtSchoolName;
		TextView txtLocation;
		ProgressBar progressBar;
		ImageButton btnJoin;
	}

}
