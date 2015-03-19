package com.champs21.schoolapp.adapters;



import java.util.List;

import roboguice.event.EventThread;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.champs21.schoolapp.R;
import com.champs21.schoolapp.model.ClubNews;
import com.champs21.schoolapp.model.SchoolEvent;
import com.champs21.schoolapp.model.SchoolEventWrapper;
import com.champs21.schoolapp.model.User;
import com.champs21.schoolapp.model.Wrapper;
import com.champs21.schoolapp.model.ClubNews.clubAckTypeEnum;
import com.champs21.schoolapp.model.SchoolEvent.ackTypeEnum;
import com.champs21.schoolapp.networking.AppRestClient;
import com.champs21.schoolapp.utils.AppConstant;
import com.champs21.schoolapp.utils.AppUtility;
import com.champs21.schoolapp.utils.GsonParser;
import com.champs21.schoolapp.utils.RequestKeyHelper;
import com.champs21.schoolapp.utils.SchoolApp;
import com.champs21.schoolapp.utils.URLHelper;
import com.champs21.schoolapp.utils.UserHelper;
import com.champs21.schoolapp.viewhelpers.CustomButton;
import com.champs21.schoolapp.viewhelpers.CustomButtonTest;
import com.champs21.schoolapp.viewhelpers.ExpandableTextView;
import com.champs21.schoolapp.viewhelpers.UIHelper;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class ClubNewsListAdapter extends ArrayAdapter<ClubNews> {

	private final Context context;
	private List<ClubNews> items;
	private LayoutInflater vi;
	private UserHelper userHelper;
	private UIHelper uiHelper;
	private ClubNews selectedClub;
	
	static class ViewHolder {
	    TextView activitiesTextView,feesTextView,scheduleTextView,categoryTextView;
	    CustomButtonTest ackBtn;
	  }
	
	public ClubNewsListAdapter(Context context, List<ClubNews> objects,UIHelper uiHelper) {
		super(context,  0, objects);
		this.context=context;
		this.items=objects;
		vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		userHelper=new UserHelper(context);
		this.uiHelper=uiHelper;
		
		
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		View rowView = convertView;
	    if (rowView == null) {
	      /*LayoutInflater inflater = context.getLayoutInflater();*/
	      rowView = vi.inflate(R.layout.row_club_news, null);
	      ViewHolder viewHolder = new ViewHolder();
	      viewHolder.activitiesTextView = (TextView) rowView.findViewById(R.id.activities_value_text);
	      viewHolder.feesTextView = (TextView) rowView.findViewById(R.id.fees_value_text);
	      viewHolder.scheduleTextView = (TextView) rowView.findViewById(R.id.schedule_value_text);
	      viewHolder.categoryTextView = (TextView) rowView.findViewById(R.id.club_name_text);
	      viewHolder.ackBtn=(CustomButtonTest)rowView.findViewById(R.id.btn_apply_to_join);
	      
	      rowView.setTag(viewHolder);
	    }

	    final ViewHolder holder = (ViewHolder) rowView.getTag();
	    final ClubNews temp=items.get(position);
	    holder.activitiesTextView.setText(temp.getClubActivity());
	    String fees;
	    if(temp.getClubFees()==0)
	    {
	    	fees="Free";
	    }
	    else
	    {
	    	fees=temp.getClubFees()+" BDT.";
	    }
	    switch (temp.getClubAck()) {
		case NOT_APPLIED:
			holder.ackBtn.setButtonSelected(true);
			holder.ackBtn.setTitleText(context.getString(R.string.btn_text_apply_to_join));
			break;
		case APPLIED:
			holder.ackBtn.setButtonSelected(true);
			holder.ackBtn.setTitleText(context.getString(R.string.btn_text_applied));
			break;
		case JOINED:
			holder.ackBtn.setButtonSelected(true);
			holder.ackBtn.setTitleText(context.getString(R.string.btn_text_joined));
			break;

		default:
			break;
		}
	    holder.feesTextView.setText(fees);
	    holder.scheduleTextView.setText(temp.getClubSchedule());
	    holder.categoryTextView.setText(temp.getClubCategoryName());
	    holder.ackBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(temp.getClubAck()==clubAckTypeEnum.NOT_APPLIED)
				{
					selectedClub=items.get(position);
					sendAckToServer(selectedClub.getClubId());
				}
				
			}
		});
	    
	    return rowView;
	  }

	protected void sendAckToServer(String clubId) {
		RequestParams params=new RequestParams();
		User user=userHelper.getUser();
		params.put(RequestKeyHelper.USER_SECRET, user.getPaidInfo().getSecret());
		params.put(RequestKeyHelper.CLUB_ID, clubId);
		params.put(RequestKeyHelper.SCHOOL, user.getPaidInfo().getSchoolId());
		params.put(RequestKeyHelper.STUDENT_ID, user.getPaidInfo().getProfileId());
		AppRestClient.post(URLHelper.URL_POST_ACK_CLUB, params, postAckHandler);
	}
	
	AsyncHttpResponseHandler postAckHandler=new AsyncHttpResponseHandler()
	{
		@Override
		public void onFailure(Throwable arg0, String arg1) {
			super.onFailure(arg0, arg1);
			
		}

		@Override
		public void onStart() {
			super.onStart();
			if(!uiHelper.isDialogActive())
				uiHelper.showLoadingDialog(context.getResources().getString(R.string.loading_text));
			else
				uiHelper.updateLoadingDialog(context.getResources().getString(R.string.loading_text));
			
		}

		@Override
		public void onSuccess(int arg0, String responseString) {
			super.onSuccess(arg0, responseString);
			uiHelper.dismissLoadingDialog();
			Wrapper wrapper=GsonParser.getInstance().parseServerResponse(responseString);
			if(wrapper.getStatus().getCode()==200)
			{
				selectedClub.setClubAcknowledge(wrapper.getData().get("club_ack").getAsInt());
				notifyDataSetChanged();
			}
			else
			{
				
			}
			Log.e("Clubs", responseString);
			
			
		}
		
	};
	
	
	
	
	
	
	
	
}

	


