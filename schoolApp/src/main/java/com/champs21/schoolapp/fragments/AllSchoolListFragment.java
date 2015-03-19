package com.champs21.schoolapp.fragments;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.support.v4.app.Fragment;
import android.text.TextPaint;
import android.text.format.DateUtils;
import android.text.style.UnderlineSpan;
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

import com.champs21.freeversion.SchoolFreeVersionActivity;
import com.champs21.freeversion.SchoolScrollableDetailsActivity;
import com.champs21.freeversion.SingleSchoolFreeVersionActivity;
import com.champs21.schoolapp.R;
import com.champs21.schoolapp.fragments.SchoolJoinGetAdditionalInfoFragment.onInfoListener;
import com.champs21.schoolapp.fragments.SchoolJoinUserTypeSelection.UserTypeSelectedListener;
import com.champs21.schoolapp.model.SchoolDetails;
import com.champs21.schoolapp.model.SchoolDetails.SchoolPages;
import com.champs21.schoolapp.model.SchoolJoinAdditionalInfo;
import com.champs21.schoolapp.model.SchoolJoinObject;
import com.champs21.schoolapp.model.SchoolJoinStatusTag;
import com.champs21.schoolapp.model.User;
import com.champs21.schoolapp.model.UserAuthListener;
import com.champs21.schoolapp.model.Wrapper;
import com.champs21.schoolapp.networking.AppRestClient;
import com.champs21.schoolapp.utils.AppConstant;
import com.champs21.schoolapp.utils.AppUtility;
import com.champs21.schoolapp.utils.GsonParser;
import com.champs21.schoolapp.utils.RequestKeyHelper;
import com.champs21.schoolapp.utils.SchoolApp;
import com.champs21.schoolapp.utils.URLHelper;
import com.champs21.schoolapp.utils.UserHelper;
import com.champs21.schoolapp.utils.UserHelper.UserAccessType;
import com.champs21.schoolapp.utils.UserHelper.UserTypeEnum;
import com.champs21.schoolapp.viewhelpers.PopupDialog;
import com.champs21.schoolapp.viewhelpers.UIHelper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class AllSchoolListFragment extends Fragment implements
		UserAuthListener, UserTypeSelectedListener, onInfoListener {

	private UIHelper uiHelper;
	private View view;
	public String selectedSchoolId = "";

	// private LinearLayout layoutButtonHolder;

	private String txtSelectLetter = "a";

	private List<SchoolDetails> listSchools = new ArrayList<SchoolDetails>();

	private SchoolAdapter adapter;

	private PullToRefreshListView listView;

	// private JsonArray arraySchools;
	private JsonArray arraySchools2 = new JsonArray();

	// private List<TextView> listLetterBtns = new ArrayList<TextView>();

	private boolean hasNext = false;

	private int pageNumber = 1;

	private ProgressBar progressBar;
	private UserHelper userHelper;
	
	
	private boolean isRefreshing = false;
	private boolean loading = false;
	private boolean stopLoadingData = false;
	
	
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		//initApiCall(pageNumber);

		super.onCreate(savedInstanceState);

	}
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		
		initApiCall(pageNumber);
	}
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		view = inflater.inflate(R.layout.fragment_allschool_list2, container,
				false);

		uiHelper = new UIHelper(this.getActivity());
		userHelper = new UserHelper(this.getActivity());
		initViews(view);

		// initApiCall(pageNumber);

		return view;
	}

	private int preLast;

	private void initViews(View view) {
		this.progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

		if (listSchools.size() > 0)
			progressBar.setVisibility(View.GONE);

		// this.layoutButtonHolder =
		// (LinearLayout)view.findViewById(R.id.layoutButtonHolder);
		this.listView = (PullToRefreshListView)view.findViewById(R.id.listView);
		
		setUpList();

		//this.adapter = new SchoolAdapter();
		//this.listView.setAdapter(adapter);

		/*
		 * TextView tv = new TextView(getActivity()); tv.setText("A");
		 * 
		 * TextView tv1 = new TextView(getActivity()); tv1.setText("|");
		 */

		// listLetterBtns.clear();

		/*
		 * for(char i = 'A'; i<= 'Z'; i++) { TextView letter = new
		 * TextView(getActivity()); letter.setText(Character.toString(i));
		 * letter.setPadding(10, 10, 10, 10);
		 * letter.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
		 * letter.setTextColor
		 * (getResources().getColorStateList(R.color.txt_search));
		 * 
		 * 
		 * letter.setOnClickListener(onClickTxtView((View)letter,
		 * Character.toString(i))); listLetterBtns.add(letter);
		 * 
		 * TextView divider = new TextView(getActivity()); divider.setText("|");
		 * divider.setTextColor(getResources().getColor(R.color.gray_1));
		 * divider.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		 * 
		 * //this.layoutButtonHolder.addView(letter);
		 * //this.layoutButtonHolder.addView(divider);
		 * 
		 * 
		 * if(i == 'A') { letter.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
		 * 
		 * SpannableString content = new
		 * SpannableString(letter.getText().toString()); content.setSpan(new
		 * UnderlineSpan(), 0, letter.getText().toString().length(), 0);
		 * letter.setText(content); } }
		 */

		this.listView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub

						SchoolDetails data = (SchoolDetails) adapter
								.getItem(position-1);

						Log.e("DATA_ITEM_CLICKED", "data: "+position);

						/*
						 * JsonArray arrayPages =
						 * arraySchools2.get(position).getAsJsonObject
						 * ().get("school_pages").getAsJsonArray();
						 * 
						 * 
						 * 
						 * 
						 * String val = new
						 * Gson().toJson(arraySchools2.get(position
						 * ).getAsJsonObject());
						 * 
						 * String val1 = new Gson().toJson(arrayPages);
						 * 
						 * Intent intent = new
						 * Intent(AllSchoolListFragment.this.getActivity(),
						 * SchoolDetailsActivity.class);
						 * intent.putExtra(AppConstant.SCHOOL_DATA, val);
						 * intent.putExtra(AppConstant.SCHOOL_PAGE_DATA, val1);
						 * startActivity(intent);
						 */

						if (data.getJoinStatus() != 2) {
							Intent intent = new Intent(
									AllSchoolListFragment.this.getActivity(),
									SchoolScrollableDetailsActivity.class);
							intent.putExtra(AppConstant.SCHOOL_ID, data.getId());
							startActivity(intent);
						} else {
							Intent intent = new Intent(
									AllSchoolListFragment.this.getActivity(),
									SingleSchoolFreeVersionActivity.class);
							intent.putExtra(AppConstant.SCHOOL_ID, data.getId());
							startActivity(intent);

						}

					}

				});

		/*
		 * listView.setOnScrollListener(new AbsListView.OnScrollListener() {
		 * 
		 * @Override public void onScrollStateChanged(AbsListView view, int
		 * scrollState) { // TODO Auto-generated method stub
		 * 
		 * }
		 * 
		 * @Override public void onScroll(AbsListView view, int
		 * firstVisibleItem, int visibleItemCount, int totalItemCount) { // TODO
		 * Auto-generated method stub final int lastItem = firstVisibleItem +
		 * visibleItemCount; if (lastItem == totalItemCount) { if (preLast !=
		 * lastItem) { // to avoid multiple calls for // last item Log.e("Last",
		 * "Last"); preLast = lastItem;
		 * 
		 * if (hasNext) { pageNumber++; initApiCall(pageNumber);
		 * 
		 * }
		 * 
		 * } } }
		 * 
		 * });
		 */

	}
	
	private void initializePageing() {
		pageNumber = 1;
		isRefreshing = false;
		loading = false;
		stopLoadingData = false;
	}
	
	private void setUpList() {

		initializePageing();
		listView.setMode(Mode.PULL_FROM_END);
		// Set a listener to be invoked when the list should be refreshed.
		listView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				String label = DateUtils.formatDateTime(getActivity(),
						System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
								| DateUtils.FORMAT_SHOW_DATE
								| DateUtils.FORMAT_ABBREV_ALL);

				// Update the LastUpdatedLabel
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

				Mode m = listView.getCurrentMode();
				if (m == Mode.PULL_FROM_START) {
					stopLoadingData = false;
					isRefreshing = true;
					pageNumber = 1;
					loading = true;
					/*processFetchPost(URLHelper.URL_FREE_VERSION_CATEGORY,
							currentCategoryId);*/
					initApiCall(pageNumber);
				} else if (!stopLoadingData) {
					pageNumber++;
					loading = true;
					/*processFetchPost(URLHelper.URL_FREE_VERSION_CATEGORY,
							currentCategoryId);*/
					initApiCall(pageNumber);
					
				} else {
					new NoDataTask().execute();
				}
			}
		});

		
		
		this.adapter = new SchoolAdapter();
		this.adapter.clearList();
		this.listView.setAdapter(adapter);
	}
	
	
	

	private void initApiCall(int pageNumber) {

		RequestParams params = new RequestParams();

		params.put("page_size", "10");
		params.put("page_number", String.valueOf(pageNumber));

		if (UserHelper.isLoggedIn())
			params.put(RequestKeyHelper.USER_ID, UserHelper.getUserFreeId());

		AppRestClient.post(URLHelper.URL_FREE_VERSION_SCHOOL, params,
				searchNameHandler);
	}

	private AsyncHttpResponseHandler searchNameHandler = new AsyncHttpResponseHandler() {

		@Override
		public void onFailure(Throwable arg0, String arg1) {
			// uiHelper.showMessage(arg1);
			// uiHelper.dismissLoadingDialog();

		};

		@Override
		public void onStart() {
			// uiHelper.showLoadingDialog("Please wait...");

			if (pageNumber == 1) {
				progressBar.setVisibility(View.VISIBLE);
			} else {

			}
		};

		@Override
		public void onSuccess(String responseString) {
			// Log.e("FREE_HOME", "data: "+responseString);
			// listSchools.clear();
			//listSchools.clear();
			arraySchools2 = new JsonArray();

			// uiHelper.dismissLoadingDialog();
			progressBar.setVisibility(View.GONE);

			Wrapper modelContainer = GsonParser.getInstance()
					.parseServerResponse(responseString);

			hasNext = modelContainer.getData().get("has_next").getAsBoolean();
			
			
			if (pageNumber == 1)
			{
				adapter.clearList();
			}
				
			if (!hasNext) 
			{
				stopLoadingData = true;
			}

			if (modelContainer.getStatus().getCode() == 200) {

				Log.e("ALL_SCL_LIST", "data: "
						+ modelContainer.getData().getAsJsonObject());

				JsonArray arraySchoolsTemp = modelContainer.getData()
						.getAsJsonObject().get("schools").getAsJsonArray();

				// arraySchools =
				// modelContainer.getData().getAsJsonObject().get("schools").getAsJsonArray();

				// arraySchools = arraySchoolsTemp;

				for (int i = 0; i < arraySchoolsTemp.size(); i++) {
					arraySchools2.add(arraySchoolsTemp.get(i));
				}

				for (int i = 0; i < parseSchools(arraySchools2.toString())
						.size(); i++) {
					listSchools.add(parseSchools(arraySchools2.toString())
							.get(i));
				}

				// listSchools = parseSchools(arraySchools.toString());

				// listView.setAdapter(adapter);
				if (pageNumber != 0 || isRefreshing) 
				{
					listView.onRefreshComplete();
					loading = false;
				}
				
				adapter.notifyDataSetChanged();

			}

			else {

			}
		};
	};

	/*
	 * private View.OnClickListener onClickTxtView(final View view, final String
	 * text) {
	 * 
	 * 
	 * View.OnClickListener listener = new View.OnClickListener() {
	 * 
	 * @Override public void onClick(View v) { // TODO Auto-generated method
	 * stub Log.e("CLICK_TXT_VIEW", "val: "+text); txtSelectLetter = text;
	 * 
	 * initApiCall(txtSelectLetter);
	 * 
	 * ((TextView) view).setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
	 * 
	 * SpannableString content = new SpannableString(text); content.setSpan(new
	 * UnderlineSpan(), 0, text.length(), 0); ((TextView)
	 * view).setText(content);
	 * 
	 * 
	 * for(TextView tv : listLetterBtns) { if(((TextView) view) != tv) {
	 * SpannableString ss= new SpannableString(tv.getText().toString());
	 * ss.setSpan(new MyClickableSpan(text), 0, tv.length(),
	 * Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); tv.setText(ss);
	 * tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
	 * 
	 * } }
	 * 
	 * } };
	 * 
	 * 
	 * SpannableString ss= new SpannableString(text); ss.setSpan(new
	 * MyClickableSpan(text), 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
	 * ((TextView) view).setText(ss);
	 * 
	 * 
	 * 
	 * return listener;
	 * 
	 * }
	 */

	/*
	 * class MyClickableSpan extends ClickableSpan{
	 * 
	 * String clicked; public MyClickableSpan(String string) { // TODO
	 * Auto-generated constructor stub super(); clicked =string; }
	 * 
	 * public void onClick(View tv) {
	 * 
	 * 
	 * }
	 * 
	 * public void updateDrawState(TextPaint ds) {// override updateDrawState
	 * ds.setUnderlineText(false); // set to false to remove underline }
	 * 
	 * 
	 * } public class NonUnderlinedClickableSpan extends ClickableSpan {
	 * 
	 * @Override public void updateDrawState(TextPaint ds) { //
	 * ds.setColor(ds.linkColor); ds.setUnderlineText(false); // set to false to
	 * remove underline }
	 * 
	 * @Override public void onClick(View widget) { // TODO Auto-generated
	 * method stub } }
	 */

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

	public ArrayList<SchoolDetails> parseSchools(String object) {
		ArrayList<SchoolDetails> data = new ArrayList<SchoolDetails>();
		data = new Gson().fromJson(object,
				new TypeToken<ArrayList<SchoolDetails>>() {
				}.getType());
		return data;
	}

	public ArrayList<SchoolPages> parseSchoolPages(String object) {
		ArrayList<SchoolPages> data = new ArrayList<SchoolPages>();
		data = new Gson().fromJson(object,
				new TypeToken<ArrayList<SchoolPages>>() {
				}.getType());
		return data;
	}
	
	
	private class NoDataTask extends AsyncTask<Void, Void, String[]> {

		@Override
		protected String[] doInBackground(Void... params) {
			// Simulates a background job.
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
			return null;
		}

		@Override
		protected void onPostExecute(String[] result) {

			adapter.notifyDataSetChanged();

			// Call onRefreshComplete when the list has been refreshed.
			listView.onRefreshComplete();

			super.onPostExecute(result);
		}
	}
	
	
	

	public class SchoolAdapter extends BaseAdapter {

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

		public void clearList() {
			listSchools.clear();
		}
		
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder;

			if (convertView == null) {
				holder = new ViewHolder();

				convertView = LayoutInflater.from(
						AllSchoolListFragment.this.getActivity()).inflate(
						R.layout.row_school_list2, parent, false);

				holder.imgLogo = (ImageView) convertView
						.findViewById(R.id.imgLogo);
				holder.txtSchoolName = (TextView) convertView
						.findViewById(R.id.txtSchoolName);
				holder.txtLocation = (TextView) convertView
						.findViewById(R.id.txtLocation);
				holder.progressBar = (ProgressBar) convertView
						.findViewById(R.id.progressBar);

				holder.btnJoin = (ImageButton) convertView
						.findViewById(R.id.btnJoin);

				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			if (listSchools.size() > 0) {
				// SchoolApp.getInstance().displayUniversalImage(listSchools.get(position).getPicture(),
				// holder.imgLogo);
				SchoolJoinStatusTag tag = new SchoolJoinStatusTag();
				tag.setSchoolId(listSchools.get(position).getId());
				tag.setStatus(listSchools.get(position).getJoinStatus());
				holder.btnJoin.setTag(tag);

				SchoolApp.getInstance().displayUniversalImage(
						listSchools.get(position).getPicture(), holder.imgLogo,
						holder.progressBar);

				holder.txtSchoolName.setText(listSchools.get(position)
						.getName());

				holder.txtLocation.setText(listSchools.get(position)
						.getLocation()
						+ ", "
						+ listSchools.get(position).getDivision());
				// join status
				
				switch (listSchools.get(position).getJoinStatus()) {
				case 0:
					if(userHelper.getUser().isJoinedToSchool()){ holder.btnJoin.setVisibility(View.GONE);
					} else  holder.btnJoin.setVisibility(View.VISIBLE);
					holder.btnJoin.setImageResource(R.drawable.btn_join);
					break;
				case 1:
					holder.btnJoin.setImageResource(R.drawable.processing);
					holder.btnJoin.setVisibility(View.VISIBLE);
					break;

				case 2:
					holder.btnJoin.setImageResource(R.drawable.leave);
					holder.btnJoin.setVisibility(View.VISIBLE);
					break;

				case 3:
					if(userHelper.getUser().isJoinedToSchool()){ holder.btnJoin.setVisibility(View.GONE);
					} else holder.btnJoin.setVisibility(View.VISIBLE);
					holder.btnJoin.setImageResource(R.drawable.btn_join);
					break;

				default:
					break;
				}

			}
			holder.btnJoin.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Log.e("IMAGE_BUTT", "join clicked");
					if (AppUtility.isInternetConnected()) {

						if (UserHelper.isLoggedIn()) {
							SchoolJoinStatusTag tag = (SchoolJoinStatusTag) v
									.getTag();
							selectedSchoolId = tag.getSchoolId();
							if (tag.getStatus() == 0) {

								if (userHelper.getUser().getType() == UserTypeEnum.OTHER) {
									SchoolJoinUserTypeSelection typeSelectionPicker = SchoolJoinUserTypeSelection
											.newInstance();
									typeSelectionPicker
											.setData(AllSchoolListFragment.this);
									typeSelectionPicker.show(
											getChildFragmentManager(), null);
								}
								else
								{
									onUserTypeSelected(userHelper.getUser().getType());
								}
							} else if (tag.getStatus() == 2) {
								RequestParams params = new RequestParams();
								params.put("school_id", selectedSchoolId);
								params.put(RequestKeyHelper.USER_ID,
										UserHelper.getUserFreeId());
								AppRestClient
										.post(URLHelper.URL_FREE_VERSION_SCHOOL_LEAVE,
												params, leaveHandler);
							}

						} else {
							showCustomDialog(
									"MY SCHOOL",
									R.drawable.school_popup_icon,
									getResources().getString(
											R.string.school_msg)
											+ "\n"
											+ getResources().getString(
													R.string.not_logged_in_msg));

							// startActivity(new Intent(getActivity(),
							// LoginActivity.class));
						}
					} else {
						uiHelper.showMessage(getString(R.string.internet_error_text));
					}
				}
			});

			return convertView;
		}

	}

	public void showCustomDialog(String headerText, int imgResId,
			String descriptionText) {

		PopupDialog picker = PopupDialog.newInstance(0);
		picker.setData(headerText, descriptionText, imgResId, getActivity());
		picker.show(getChildFragmentManager(), null);
	}

	class ViewHolder {
		ImageView imgLogo;
		TextView txtSchoolName;
		TextView txtLocation;
		ProgressBar progressBar;
		ImageButton btnJoin;
	}

	public static class NoUnderlineSpan extends UnderlineSpan {
		public NoUnderlineSpan() {
		}

		public NoUnderlineSpan(Parcel src) {
		}

		@Override
		public void updateDrawState(TextPaint ds) {
			ds.setUnderlineText(false);
		}
	}

	@Override
	public void onUserTypeSelected(UserTypeEnum type) {
		// TODO Auto-generated method stub
		SchoolJoinGetAdditionalInfoFragment getAdditionalInfoDialog;
		switch (type) {
		case STUDENT:
			Log.e("Type", "Student");
			getAdditionalInfoDialog = SchoolJoinGetAdditionalInfoFragment
					.newInstance(UserTypeEnum.STUDENT.ordinal());
			getAdditionalInfoDialog.setData(AllSchoolListFragment.this);
			getAdditionalInfoDialog.show(getChildFragmentManager(), null);
			break;
		case TEACHER:
			Log.e("Type", "Teacher");
			getAdditionalInfoDialog = SchoolJoinGetAdditionalInfoFragment
					.newInstance(UserTypeEnum.TEACHER.ordinal());
			getAdditionalInfoDialog.setData(AllSchoolListFragment.this);
			getAdditionalInfoDialog.show(getChildFragmentManager(), null);
			break;
		case PARENTS:
			Log.e("Type", "Parents");
			getAdditionalInfoDialog = SchoolJoinGetAdditionalInfoFragment
					.newInstance(UserTypeEnum.PARENTS.ordinal());
			getAdditionalInfoDialog.setData(AllSchoolListFragment.this);
			getAdditionalInfoDialog.show(getChildFragmentManager(), null);
			break;
		case ALUMNI:
			Log.e("Type", "Alumni");
			getAdditionalInfoDialog = SchoolJoinGetAdditionalInfoFragment
					.newInstance(UserTypeEnum.ALUMNI.ordinal());
			getAdditionalInfoDialog.setData(AllSchoolListFragment.this);
			getAdditionalInfoDialog.show(getChildFragmentManager(), null);
			break;
		default:
			break;
		}
	}

	@Override
	public void onInfoEntered(SchoolJoinObject obj) {
		Log.e("Response",
				getJsonStringfromCustomObject(obj.getAdditionalInfo()));
		RequestParams params = new RequestParams();
		if (obj.getType() == UserTypeEnum.ALUMNI)
			params.put("type", "1");
		else
			params.put("type", obj.getType().ordinal() + "");
		params.put("grade", obj.getGradeIDs());
		params.put("information",
				getJsonStringfromCustomObject(obj.getAdditionalInfo()));
		params.put("school_id", selectedSchoolId);
		params.put(RequestKeyHelper.USER_ID, UserHelper.getUserFreeId());
		AppRestClient.post(URLHelper.URL_FREE_VERSION_SCHOOL_JOIN, params,
				joinHandler);
	}

	private String getJsonStringfromCustomObject(SchoolJoinAdditionalInfo info) {
		Gson gson = new Gson();
		return gson.toJson(info);
	}

	AsyncHttpResponseHandler joinHandler = new AsyncHttpResponseHandler() {

		@Override
		public void onFailure(Throwable arg0, String arg1) {
			super.onFailure(arg0, arg1);
			uiHelper.dismissLoadingDialog();
			uiHelper.showMessage(arg1);
		}

		@Override
		public void onStart() {
			super.onStart();
			uiHelper.showLoadingDialog("Joining...");
		}

		@Override
		public void onSuccess(int arg0, String responseString) {
			super.onSuccess(arg0, responseString);

			Log.e("Response****", responseString);
			uiHelper.dismissLoadingDialog();
			Wrapper wrapper = GsonParser.getInstance().parseServerResponse(
					responseString);
			if (wrapper.getStatus().getCode() == AppConstant.RESPONSE_CODE_SUCCESS) {
				User u=GsonParser.getInstance().parseUser(wrapper.getData().get("userinfo").toString());
				u.setAccessType(UserAccessType.FREE);
				u.setType();
				userHelper.storeLoggedInUser(u);
				
				listSchools.clear();
				adapter.notifyDataSetChanged();
				SchoolFreeVersionActivity.shouldRefresh = true;
				
				initApiCall(1);
				
				
				
			} else {
				uiHelper.showMessage(wrapper.getStatus().getMsg());
			}

		}

	};

	AsyncHttpResponseHandler leaveHandler = new AsyncHttpResponseHandler() {

		@Override
		public void onFailure(Throwable arg0, String arg1) {
			super.onFailure(arg0, arg1);
			uiHelper.dismissLoadingDialog();
			uiHelper.showMessage(arg1);
		}

		@Override
		public void onStart() {
			super.onStart();
			uiHelper.showLoadingDialog("Leaving...");
		}

		@Override
		public void onSuccess(int arg0, String responseString) {
			super.onSuccess(arg0, responseString);

			Log.e("Response****", responseString);
			uiHelper.dismissLoadingDialog();
			Wrapper wrapper = GsonParser.getInstance().parseServerResponse(
					responseString);
			if (wrapper.getStatus().getCode() == AppConstant.RESPONSE_CODE_SUCCESS) {

				User u=GsonParser.getInstance().parseUser(wrapper.getData().get("userinfo").toString());
				u.setAccessType(UserAccessType.FREE);
				u.setType();
				userHelper.storeLoggedInUser(u);
				
				listSchools.clear();
				adapter.notifyDataSetChanged();
				SchoolFreeVersionActivity.shouldRefresh = true;
				initApiCall(1);
			} else {
				uiHelper.showMessage(wrapper.getStatus().getMsg());
			}

		}

	};




	@Override
	public void onPaswordChanged() {
		// TODO Auto-generated method stub
		
	}

}
