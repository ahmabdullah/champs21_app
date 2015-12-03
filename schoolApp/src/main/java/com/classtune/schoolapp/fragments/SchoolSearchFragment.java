package com.classtune.schoolapp.fragments;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.classtune.freeversion.SingleSchooolFromSearchActivity;
import com.classtune.schoolapp.R;
import com.classtune.schoolapp.model.SchoolDetails;
import com.classtune.schoolapp.model.UserAuthListener;
import com.classtune.schoolapp.model.Wrapper;
import com.classtune.schoolapp.networking.AppRestClient;
import com.classtune.schoolapp.utils.AppConstant;
import com.classtune.schoolapp.utils.GsonParser;
import com.classtune.schoolapp.utils.URLHelper;
import com.classtune.schoolapp.viewhelpers.UIHelper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class SchoolSearchFragment extends Fragment implements
		UserAuthListener {

	private UIHelper uiHelper;
	private View view;
	//private PoppyViewHelper mPoppyViewHelper;

	private Spinner spinnerMedium;
	private Spinner spinnerCountry;
	private Spinner spinnerCity;
	private Spinner spinnerLocation;

	private ImageButton btnSchoolSearch;
	private List<SchoolDetails> listSchools;
	private JsonArray arraySchools;
	
	private EditText txtSchoolName;
	
	
	private String name = "";
	private String division = "";
	private String medium = "";
	private String location = "";
	private String country = "";
	
	private ImageButton btnMedium;
	private ImageButton btnCountry;
	private ImageButton btnDivision;
	private ImageButton btnLocation;
	
	private TextView txtMedium;
	private TextView txtCountry;
	private TextView txtDivision;
	private TextView txtLocation;
	
	
	
	private String selectItemName = "";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		/*
		 * AppRestClient.post(URLHelper.URL_FREE_VERSION_HOME, null,
		 * freeHomeHandler);
		 */

		uiHelper = new UIHelper(this.getActivity());

		view = inflater.inflate(R.layout.fragment_school_search2, container,
				false);

		// setupPoppyView();

		initViewObjects(view);

		return view;
	}

	/*private void setupPoppyView() {
		// TODO Auto-generated method stub
		mPoppyViewHelper = new PoppyViewHelper(getActivity());
		View poppyView = mPoppyViewHelper.createPoppyViewOnListView(view,
				R.id.listViewHomeData, R.layout.poppyview_free,
				new AbsListView.OnScrollListener() {
					@Override
					public void onScrollStateChanged(AbsListView view,
							int scrollState) {
						Log.d("ListViewActivity", "onScrollStateChanged");
					}

					@Override
					public void onScroll(AbsListView view,
							int firstVisibleItem, int visibleItemCount,
							int totalItemCount) {
						Log.d("ListViewActivity", "onScroll");
					}
				});

		Button btnGoodread = (Button) poppyView.findViewById(R.id.btn_goodread);
		btnGoodread.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(SchoolSearchFragment.this
						.getActivity(), GoodReadActivity.class));
			}
		});

		Button btnCmart = (Button) poppyView.findViewById(R.id.btn_cmart);
		btnCmart.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}
		});

		Button btnCandle = (Button) poppyView.findViewById(R.id.btn_candle);
		btnCandle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}
		});
	}
*/
	private void initViewObjects(View view) {
		
		/*this.txtSchoolName = (EditText)view.findViewById(R.id.txtSchoolName);
		
		this.spinnerMedium = (Spinner) view.findViewById(R.id.spinnerMedium);
		this.spinnerCountry = (Spinner) view.findViewById(R.id.spinnerCountry);
		this.spinnerCity = (Spinner) view.findViewById(R.id.spinnerCity);
		this.spinnerLocation = (Spinner) view.findViewById(R.id.spinnerLocation);
		
		
		List<String> sortedLocation = new ArrayList<String>();
		for(int i=0;i<getResources().getStringArray(R.array.location_list).length;i++)
		{
			sortedLocation.add(getResources().getStringArray(R.array.location_list)[i]);
		}
		Collections.sort(sortedLocation);

		this.spinnerMedium.setAdapter(new ArrayAdapter<String>(this.getActivity(), R.layout.layout_text_list, getResources().getStringArray(R.array.medium_list)));
		this.spinnerCountry.setAdapter(new ArrayAdapter<String>(this.getActivity(), R.layout.layout_text_list, getResources().getStringArray(R.array.country_list)));
		this.spinnerCity.setAdapter(new ArrayAdapter<String>(this.getActivity(), R.layout.layout_text_list, getResources().getStringArray(R.array.city_list)));
		this.spinnerLocation.setAdapter(new ArrayAdapter<String>(this.getActivity(), R.layout.layout_text_list, getResources().getStringArray(R.array.location_list)));
		
		
		
		this.spinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {
				// TODO Auto-generated method stub
				
				Log.e("ITEM_CLICKED", parent.getItemAtPosition(pos).toString());
			
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
		});
		
		
		this.btnSchoolSearch = (ImageButton)view.findViewById(R.id.btnSchoolSearch);
		
		this.btnSchoolSearch.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				name = txtSchoolName.getText().toString();
				division = spinnerCity.getSelectedItem().toString();
				medium = spinnerMedium.getSelectedItem().toString();
				location = spinnerLocation.getSelectedItem().toString();
				
				Log.e("DATA", "data: "+name+" d "+division+" m "+medium+" l "+location);
				
				if(location.equalsIgnoreCase("-----"))
					initApiCall(name, division, medium, "");
				else
					initApiCall(name, division, medium, location);
				
				initApiCall(name, division, medium, location);
				
			}
		});*/
		
		
		this.txtSchoolName = (EditText)view.findViewById(R.id.txtSchoolName);
		
		
		this.txtMedium = (TextView)view.findViewById(R.id.txtMedium); 
		this.txtCountry = (TextView)view.findViewById(R.id.txtCountry);
		this.txtDivision = (TextView)view.findViewById(R.id.txtDivision);
		this.txtLocation = (TextView)view.findViewById(R.id.txtLocation);
		
		
		this.btnMedium = (ImageButton)view.findViewById(R.id.btnMedium);
		this.btnMedium.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//createPopUpBelowButton(R.menu.popup_menu_medium);
				PopupMenu popup = new PopupMenu(SchoolSearchFragment.this.getActivity(), btnCountry);
				popup.getMenuInflater().inflate(R.menu.popup_menu_medium, popup.getMenu());  
				 
				popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {  
		             public boolean onMenuItemClick(MenuItem item) {  
		              //Toast.makeText(SchoolSearchFragment.this.getActivity(),"You Clicked : " + item.getTitle(),Toast.LENGTH_SHORT).show();  
		              
		              txtMedium.setText(item.getTitle().toString());
		              
		              return true;  
		             }  
		            });  

		        popup.show();


			}
		});
		
		this.btnCountry = (ImageButton)view.findViewById(R.id.btnCountry);
		this.btnCountry.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//createPopUpBelowButton(R.menu.popup_menu_country);
				PopupMenu popup = new PopupMenu(SchoolSearchFragment.this.getActivity(), btnCountry);
				popup.getMenuInflater().inflate(R.menu.popup_menu_country, popup.getMenu());  
				 
				popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {  
		             public boolean onMenuItemClick(MenuItem item) {  
		              //Toast.makeText(SchoolSearchFragment.this.getActivity(),"You Clicked : " + item.getTitle(),Toast.LENGTH_SHORT).show();  
		              
		              txtCountry.setText(item.getTitle().toString());
		              
		              return true;  
		             }  
		            });  

		        popup.show();
				
				
				

			}
		});
		
		
		
		this.btnDivision = (ImageButton)view.findViewById(R.id.btnDivision);
		this.btnDivision.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//createPopUpBelowButton(R.menu.popup_menu_division);
				PopupMenu popup = new PopupMenu(SchoolSearchFragment.this.getActivity(), btnCountry);
				popup.getMenuInflater().inflate(R.menu.popup_menu_division, popup.getMenu());  
				 
				popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {  
		             public boolean onMenuItemClick(MenuItem item) {  
		              //Toast.makeText(SchoolSearchFragment.this.getActivity(),"You Clicked : " + item.getTitle(),Toast.LENGTH_SHORT).show();  
		              
		              txtDivision.setText(item.getTitle().toString());
		              
		              return true;  
		             }  
		            });  

		        popup.show();
				
				
				
			}
		});
		
		this.btnLocation = (ImageButton)view.findViewById(R.id.btnLocation);
		this.btnLocation.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//createPopUpBelowButton(R.menu.popup_menu_location);
				PopupMenu popup = new PopupMenu(SchoolSearchFragment.this.getActivity(), btnCountry);
				popup.getMenuInflater().inflate(R.menu.popup_menu_location, popup.getMenu());  
				 
				popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {  
		             public boolean onMenuItemClick(MenuItem item) {  
		              //Toast.makeText(SchoolSearchFragment.this.getActivity(),"You Clicked : " + item.getTitle(),Toast.LENGTH_SHORT).show();  
		              
		              txtLocation.setText(item.getTitle().toString());
		              
		              return true;  
		             }  
		            });  

		        popup.show();
				

			}
		});
		
		
		this.btnSchoolSearch = (ImageButton)view.findViewById(R.id.btnSchoolSearch);
		
		this.btnSchoolSearch.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				name = txtSchoolName.getText().toString();
				division = txtDivision.getText().toString();
				medium = txtMedium.getText().toString();
				location = txtLocation.getText().toString();
				
				
				
				initApiCall(name, division, medium, location);
				
			}
		});
		
		
	}
	
	
	
	private String createPopUpBelowButton(int menuResId)
	{
		
		PopupMenu popup = new PopupMenu(SchoolSearchFragment.this.getActivity(), btnCountry);
		popup.getMenuInflater().inflate(menuResId, popup.getMenu());  
		 
		popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {  
             public boolean onMenuItemClick(MenuItem item) {  
              //Toast.makeText(SchoolSearchFragment.this.getActivity(),"You Clicked : " + item.getTitle(),Toast.LENGTH_SHORT).show();  
              
              selectItemName = item.getTitle().toString();
              
              return true;  
             }  
            });  

        popup.show();

        return selectItemName;
	}
	
	
	
	
	
	private void initApiCall(String name, String division, String medium, String location)
	{
		
		RequestParams params = new RequestParams();
		
		params.put("name", name);
		params.put("division", division);
		params.put("medium", medium);
		params.put("location", location);
		
		/*if(division.length() > 0 )
			params.put("name", name);
		
		if(division.length() > 0 )
			params.put("division", division);
		
		if(medium.length() > 0 )
			params.put("medium", medium);
		
		if(location.length() > 0 )
			params.put("location", location);*/
		
		AppRestClient.post(URLHelper.URL_FREE_VERSION_SCHOOL_SEARCH, params, 
				searchNameHandler);
	}
	
	private AsyncHttpResponseHandler searchNameHandler = new AsyncHttpResponseHandler(){

		@Override
		public void onFailure(Throwable arg0, String arg1) 
		{
			uiHelper.showMessage(arg1);
			uiHelper.dismissLoadingDialog();
		};

		@Override
		public void onStart() 
		{
			uiHelper.showLoadingDialog("Please wait...");
		};

		@Override
		public void onSuccess(String responseString) 
		{
			//Log.e("FREE_HOME", "data: "+responseString);

			uiHelper.dismissLoadingDialog();

			Wrapper modelContainer = GsonParser.getInstance().parseServerResponse(responseString);

			if (modelContainer.getStatus().getCode() == 200)
			{
				
				Log.e("ALL_SCL_LIST", "data: "+modelContainer.getData().getAsJsonObject());
				
				
				arraySchools = modelContainer.getData().getAsJsonObject().get("schools").getAsJsonArray();
				
				
				listSchools = parseSchools(arraySchools.toString());
				
				if(listSchools.size() > 0)
				{
					Gson gson = new Gson();
					Intent intent = new Intent(SchoolSearchFragment.this.getActivity(), SingleSchooolFromSearchActivity.class);
					String val = gson.toJson(listSchools);
					String val2 = gson.toJson(arraySchools);
					intent.putExtra(AppConstant.LIST_SCHOOL, val);
					intent.putExtra(AppConstant.ARRAY_SCHOOL, val2);
					startActivity(intent);
					
				}
				else
				{
					Toast.makeText(SchoolSearchFragment.this.getActivity(), "No data found!", Toast.LENGTH_SHORT).show();
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

	@Override
	public void onAuthenticationStart() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAuthenticationSuccessful() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAuthenticationFailed(String msg) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPaswordChanged() {
		// TODO Auto-generated method stub
		
	}

	/*public class MyAdapter extends ArrayAdapter<String> {

		private String[] objects;

		public MyAdapter(Context context, int textViewResourceId,
				String[] objects) {
			super(context, textViewResourceId, objects);
			this.objects = objects;
		}

		@Override
		public View getDropDownView(int position, View convertView,
				ViewGroup parent) {

			LayoutInflater inflater = SchoolSearchFragment.this.getActivity()
					.getLayoutInflater();

			View row = inflater.inflate(R.layout.aaa2, parent, false);

			return row;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return getCustomView(position, convertView, parent);
		}

		public View getCustomView(int position, View convertView,
				ViewGroup parent) {

			LayoutInflater inflater = SchoolSearchFragment.this.getActivity()
					.getLayoutInflater();

			View row = inflater.inflate(R.layout.aaa, parent, false);

			TextView label = (TextView) row.findViewById(R.id.textView1);
			label.setText(objects[position]);

			Button btn = (Button) row.findViewById(R.id.btn);

			return row;
		}

	}*/

}
