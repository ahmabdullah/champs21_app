package com.champs21.freeversion;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.champs21.schoolapp.GcmIntentService;
import com.champs21.schoolapp.R;
import com.champs21.schoolapp.model.ModelContainer;
import com.champs21.schoolapp.model.Notice;
import com.champs21.schoolapp.model.Wrapper;
import com.champs21.schoolapp.networking.AppRestClient;
import com.champs21.schoolapp.utils.AppConstant;
import com.champs21.schoolapp.utils.AppUtility;
import com.champs21.schoolapp.utils.GsonParser;
import com.champs21.schoolapp.utils.ReminderHelper;
import com.champs21.schoolapp.utils.RequestKeyHelper;
import com.champs21.schoolapp.utils.URLHelper;
import com.champs21.schoolapp.utils.UserHelper;
import com.champs21.schoolapp.viewhelpers.CustomButton;
import com.champs21.schoolapp.viewhelpers.UIHelper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class SingleNoticeActivity extends ChildContainerActivity {
	
	
	private UIHelper uiHelper;
	private UserHelper userHelper;
	private Gson gson;
	private String id;
	private Notice data;
	
	private TextView txtNoticeTitle;
	private TextView txtDate;
	//private ExpandableTextView txtContent;
	private WebView txtContent;
	private CustomButton btnNoticeAcknowledge;
	private CustomButton btnNoticeReminder;

    private RelativeLayout layoutMessage;
    private LinearLayout layoutDataContainer;
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
        homeBtn.setVisibility(View.VISIBLE);
        logo.setVisibility(View.GONE);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_single_notice);
		
		gson = new Gson();
		
		uiHelper = new UIHelper(this);
		userHelper = new UserHelper(this);
		
		if(getIntent().getExtras() != null)
			this.id = getIntent().getExtras().getString(AppConstant.ID_SINGLE_NOTICE);
		
		initView();
		initApiCall();


        if(getIntent().getExtras()!=null)
        {
            if(getIntent().getExtras().containsKey("total_unread_extras"))
            {
                String rid = getIntent().getExtras().getBundle("total_unread_extras").getString("rid");
                String rtype = getIntent().getExtras().getBundle("total_unread_extras").getString("rtype");


                Log.e("SINGLE_NOTICE", "rtype: "+rtype+" "+"rid: "+rid);

                GcmIntentService.initApiCall(rid, rtype);
            }
        }
		
	}
	
	private void initView()
	{
		txtNoticeTitle = (TextView)this.findViewById(R.id.txtNoticeTitle);
		txtDate = (TextView)this.findViewById(R.id.txtDate);
		//txtContent = (ExpandableTextView)this.findViewById(R.id.txtContent);
		txtContent = (WebView)this.findViewById(R.id.txtContent);
		btnNoticeAcknowledge = (CustomButton)this.findViewById(R.id.btnNoticeAcknowledge);
		btnNoticeReminder = (CustomButton)this.findViewById(R.id.btnNoticeReminder);

        layoutMessage = (RelativeLayout)this.findViewById(R.id.layoutMessage);
        layoutDataContainer = (LinearLayout)this.findViewById(R.id.layoutDataContainer);
	}
	
	
	private void initAction()
	{
		/*if(ReminderHelper.getInstance().reminder_map == null)
			ReminderHelper.getInstance().constructReminderFromSharedPreference();*/
		
		Log.e("TITLE", "is: " + data.getNoticeTitle());
		
		
		txtNoticeTitle.setText(data.getNoticeTitle());
		txtDate.setText(data.getPublishedAt());
		//txtContent.setText(" "+ Html.fromHtml(data.getNoticeContent(), null, new MyTagHandler()));
		txtContent.loadData(data.getNoticeContent(), "text/html; charset=utf-8", "UTF-8");
		
		btnNoticeAcknowledge.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				requestAcknowledge(v.getTag().toString(), (CustomButton) v);
			}
		});
		
		
		
		btnNoticeReminder.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				CustomButton reminderBtn = (CustomButton) v;
				Notice rmNotice = data;
				reminderBtn.setImage(R.drawable.btn_reminder_tap);
				reminderBtn.setTitleColor(SingleNoticeActivity.this
						.getResources().getColor(R.color.maroon));
				reminderBtn.setEnabled(false);
				String content = ""+Html.fromHtml(rmNotice.getNoticeContent());
				/*ReminderHelper.getInstance().setReminder(rmNotice.getPublishedAt(),
						rmNotice.getNoticeTitle(), content,
						rmNotice.getPublishedAt(), SingleNoticeActivity.this);*/


                AppUtility.listenerDatePickerCancel = new AppUtility.IDatePickerCancel() {
                    @Override
                    public void onCancelCalled() {

                        Log.e("CCCCC", "cancel called");
                        btnNoticeReminder.setImage(R.drawable.btn_reminder_normal);
                        btnNoticeReminder.setTitleColor(SingleNoticeActivity.this.getResources().getColor(R.color.gray_1));
                        btnNoticeReminder.setEnabled(true);
                    }
                };


                AppUtility.showDateTimePicker(rmNotice.getPublishedAt(), rmNotice.getNoticeTitle(), content, SingleNoticeActivity.this);


			}
		});
		
		
		btnNoticeAcknowledge.setTag(data.getNoticeId());
		
		
		if (data.getAllAck().size() != 0) {
			if (data.getAllAck().get(0).getAcknowledge_status()
					.equals("1")) {

				setButtonState(btnNoticeAcknowledge, R.drawable.done_tap, false, "Acknowledged");
				
			} else if (data.getAllAck().get(0).getAcknowledge_status()
					.equals("0")) {
				setButtonState(btnNoticeAcknowledge, R.drawable.done_normal, true, "Acknowledge");
			}
		} else {
			setButtonState(btnNoticeAcknowledge, R.drawable.done_normal, false, "Acknowledge");
			btnNoticeAcknowledge.setTitleColor(this.getResources().getColor(R.color.gray_1));
		}
		
		if (ReminderHelper.getInstance().reminder_map.containsKey(data.getPublishedAt())){
			setButtonState(btnNoticeReminder, R.drawable.btn_reminder_tap, false, "Reminder");
			
		}else {
			setButtonState(btnNoticeReminder, R.drawable.btn_reminder_normal, true, "Reminder");
		}
		
		
	}
	
	@SuppressLint("ResourceAsColor")
	private void setButtonState(CustomButton btn, int imgResId, boolean enable , String btnText) {
		
		btn.setImage(imgResId);
		btn.setTitleText(btnText);
		btn.setEnabled(enable);
		if(enable) {
			setBtnTitleColor(btn, R.color.gray_1); 
		} else {
			setBtnTitleColor(btn, R.color.maroon); 
		}
	}
	private void setBtnTitleColor(CustomButton btn, int colorId) {
		btn.setTitleColor(this.getResources().getColor(colorId));
	}
	
	private CustomButton clickedAckBtn;
	private CustomButton clickedReminderBtn;
	private void requestAcknowledge(String tag, CustomButton btn) {
		// TODO Auto-generated method stub
		this.clickedAckBtn = btn;

		RequestParams params = new RequestParams();

		

		params.put(RequestKeyHelper.USER_SECRET, UserHelper.getUserSecret());
		params.put(RequestKeyHelper.NOTICE_ID, btn.getTag().toString());

		AppRestClient.post(URLHelper.URL_NOTICE_ACKNOWLEDGE, params,
				ackBtnHandler);
	}

	AsyncHttpResponseHandler ackBtnHandler = new AsyncHttpResponseHandler() {
		public void onFailure(Throwable arg0, String arg1) {
			Log.e("button", "failed");
			uiHelper.showMessage(arg1);
			uiHelper.dismissLoadingDialog();
		};

		public void onStart() {
			Log.e("button", "onstart");
			uiHelper.showLoadingDialog("Please wait...");
		};

		public void onSuccess(int arg0, String response) {
			Log.e("response", response);
			Log.e("button", "success");
			uiHelper.dismissLoadingDialog();

			ModelContainer modelContainer = GsonParser.getInstance().parseGson(
					response);

			// arrangeHomeworkData(modelContainer);

			// adapter.notifyDataSetChanged();

			// Log.e("status code", modelContainer.getStatus().getCode() + "");
			if (modelContainer.getData().getNotice_ack()
					.getAcknowledge_status().equals("1")) {
				clickedAckBtn.setImage(R.drawable.done_tap);
				clickedAckBtn.setTitleColor(SingleNoticeActivity.this
						.getResources().getColor(R.color.maroon));
				clickedAckBtn.setTitleText("Acknowledged");
				clickedAckBtn.setEnabled(false);
			}

		};
	};
	

	private void initApiCall()
	{

		RequestParams params = new RequestParams();
		params.put(RequestKeyHelper.USER_SECRET, UserHelper.getUserSecret());
		params.put("id", this.id);
		
		
		AppRestClient.post(URLHelper.URL_SINGLE_NOTICE, params, singleNoticeHandler);
	
	}
	
	AsyncHttpResponseHandler singleNoticeHandler = new AsyncHttpResponseHandler() {

		@Override
		public void onFailure(Throwable arg0, String arg1) {
			uiHelper.showMessage(arg1);
			if (uiHelper.isDialogActive()) {
				uiHelper.dismissLoadingDialog();
			}
		};

		@Override
		public void onStart() {
			
				uiHelper.showLoadingDialog("Please wait...");
			

		};

		@Override
		public void onSuccess(int arg0, String responseString) {
			

			uiHelper.dismissLoadingDialog();


			Wrapper modelContainer = GsonParser.getInstance()
					.parseServerResponse(responseString);

			if (modelContainer.getStatus().getCode() == 200) {


                layoutDataContainer.setVisibility(View.VISIBLE);
                layoutMessage.setVisibility(View.GONE);
				
				JsonObject objNotice = modelContainer.getData().get("notice").getAsJsonObject();
				data = gson.fromJson(objNotice.toString(), Notice.class);
				
				
				
				initAction();
				
			}

            else if(modelContainer.getStatus().getCode() == 400 && modelContainer.getStatus().getCode() != 404)
            {
                layoutDataContainer.setVisibility(View.GONE);
                layoutMessage.setVisibility(View.VISIBLE);
            }
			
			else {

			}
			
			

		};
	};
}
