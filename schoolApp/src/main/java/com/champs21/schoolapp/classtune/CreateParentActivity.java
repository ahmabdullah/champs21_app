package com.champs21.schoolapp.classtune;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.champs21.schoolapp.R;
import com.champs21.schoolapp.model.Wrapper;
import com.champs21.schoolapp.networking.AppRestClient;
import com.champs21.schoolapp.utils.AppConstant;
import com.champs21.schoolapp.utils.GsonParser;
import com.champs21.schoolapp.utils.URLHelper;
import com.champs21.schoolapp.viewhelpers.UIHelper;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by BLACK HAT on 10-Nov-15.
 */
public class CreateParentActivity extends Activity implements DatePicker.OnDateChangedListener, IDialogSelectChildrenDoneListener{


    private UIHelper uiHelper;

    private int ordinal = -1;
    private String schoolId = "";

    private String gender = "1";
    private String dob = "";

    private String firstName = "";
    private String lastName = "";
    private String eMail = "";
    private String password = "";
    private String schoolCode = "";
    private String batchId = "";

    private EditText txtUserId;
    private DatePicker pickerDob;
    private Spinner spinnerGender;
    private EditText txtContact;



    private Button btnCreate;

    private int year;
    private int month;
    private int day;

    private List<String> listGender;

    private Button btnAddChild;

    private DialogSelectChildren dialog;

    private String childParam = "";

    private List<String> listChildParam;

    private LinearLayout layoutChildViewHolder;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_parent);

        Bundle extras = getIntent().getExtras();

        if(extras != null)
        {
            ordinal = extras.getInt(AppConstant.USER_TYPE_CLASSTUNE);
            schoolId = extras.getString(AppConstant.SCHOOL_ID_CLASSTUNE);

            firstName = extras.getString(AppConstant.STUDENT_FIRST_NAME_CLASSTUNE);
            lastName = extras.getString(AppConstant.STUDENT_LAST_NAME_CLASSTUNE);
            eMail = extras.getString(AppConstant.STUDENT_EMAIL_CLASSTUNE);
            password = extras.getString(AppConstant.STUDENT_PASSWORD_CLASSTUNE);
            schoolCode = extras.getString(AppConstant.STUDENT_SCHOOL_CODE_CLASSTUNE);
        }

        Log.e("ORDINAL AND SCHOOL ID", "ordinal: " + ordinal + " school id: " + schoolId);

        uiHelper = new UIHelper(CreateParentActivity.this);

        initView();
        initAction();
    }

    private void initView()
    {
        txtUserId = (EditText)this.findViewById(R.id.txtUserId);
        pickerDob = (DatePicker)this.findViewById(R.id.pickerDob);
        spinnerGender = (Spinner)this.findViewById(R.id.spinnerGender);
        txtContact = (EditText)this.findViewById(R.id.txtContact);


        btnCreate = (Button)this.findViewById(R.id.btnCreate);
        btnAddChild = (Button)this.findViewById(R.id.btnAddChild);

        dialog = new DialogSelectChildren(this, schoolCode, this);
        layoutChildViewHolder = (LinearLayout)this.findViewById(R.id.layoutChildViewHolder);
    }

    private void initAction()
    {
        listGender = new ArrayList<String>();
        listGender.add("Male");
        listGender.add("Female");

        ArrayAdapter<String> genderAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listGender);
        spinnerGender.setAdapter(genderAdapter);

        spinnerGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Log.e("Spinner item position", "is: " + position);
                if (position == 0) {
                    gender = "1";
                } else {
                    gender = "2";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        setCurrentDateOnView();


        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkValidForm() == true) {
                    initApiCall();
                }
            }
        });


        btnAddChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                dialog.show();

            }
        });

        listChildParam = new ArrayList<String>();

    }


    /*private void addViewMoreChild()
    {
        TextView tv = new TextView(this);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.65f);

        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        tv.setHighlightColor(Color.BLACK);
        tv.setLayoutParams(params);
        tv.setText("Child Id");


        EditText et = new EditText(this);
        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.35f);
        et.setLayoutParams(params2);
        et.setSingleLine(true);

        LinearLayout layout = new LinearLayout(this);
        LinearLayout.LayoutParams paramsLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        paramsLayout.setMargins((int) AppUtility.getDeviceIndependentDpFromPixel(this, 10), (int) AppUtility.getDeviceIndependentDpFromPixel(this, 10), (int) AppUtility.getDeviceIndependentDpFromPixel(this, 10),
                (int) AppUtility.getDeviceIndependentDpFromPixel(this, 10));
        layout.setLayoutParams(paramsLayout);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setGravity(Gravity.CENTER);
        layout.setWeightSum(2f);

        layout.addView(tv);
        layout.addView(et);



        layoutChildIdHolder.addView(layout);




    }*/

    public void setCurrentDateOnView() {

        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        StringBuilder sb = new StringBuilder()
                .append(year).append("-")
                .append(month + 1).append("-")
                .append(day).append(" ");

        Log.e("CURRENT_DATE", "is: " + sb.toString());
        dob = sb.toString();

        pickerDob.init(year, month, day, this);

    }

    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        StringBuilder sb = new StringBuilder()
                .append(year).append("-")
                .append(monthOfYear + 1).append("-")
                .append(dayOfMonth).append(" ");

        Log.e("CHANGED_DATE", "is: " + sb.toString());
        dob = sb.toString();
    }



    private boolean checkValidForm()
    {
        boolean isValid = true;

        if(txtUserId.getText().toString().matches(""))
        {
            uiHelper.showErrorDialog("User Id cannot be empty!");
            isValid = false;
        }

        else if(txtContact.getText().toString().matches(""))
        {
            uiHelper.showErrorDialog("Contact no. cannot be empty!!");
            isValid = false;
        }


        return isValid;
    }

    private void initApiCall()
    {
        RequestParams params = new RequestParams();

        params.put("first_name", firstName);
        params.put("last_name", lastName);
        params.put("email", eMail);
        params.put("password", password);
        params.put("school_code", schoolCode);
        params.put("user_id", txtUserId.getText().toString());

        params.put("date_of_birth", dob);

        params.put("gender", gender);
        params.put("contact_no", txtContact.getText().toString());


        Log.e("CHILD_PARAMS", "is: "+childParam);

        params.put("childrens", childParam);



        AppRestClient.post(URLHelper.URL_PAID_PARENT, params, createParentHandler);
    }

    AsyncHttpResponseHandler createParentHandler = new AsyncHttpResponseHandler() {

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

            Log.e("SCCCCC", "response: " + responseString);

            uiHelper.dismissLoadingDialog();


            Wrapper modelContainer = GsonParser.getInstance()
                    .parseServerResponse(responseString);

            if (modelContainer.getStatus().getCode() == 200) {

                Log.e("CODE 200", "code 200");
            }

            else if (modelContainer.getStatus().getCode() == 401) {

                Log.e("CODE 401", "code 401");
                uiHelper.showErrorDialog("Username already exists!");
            }

            else if (modelContainer.getStatus().getCode() == 400) {

                Log.e("CODE 400", "code 400");
                uiHelper.showErrorDialog("Something went wrong please try again.");
            }

            else if (modelContainer.getStatus().getCode() == 402) {

                Log.e("CODE 402", "code 402");
                uiHelper.showErrorDialog("Invalid student Id!");
            }


            else {

            }



        };
    };


    @Override
    public void onDoneSelection(String childrenParam) {


        listChildParam.add(childrenParam);

        for(int i=0; i<listChildParam.size();i++)
        {
            if(i>0)
                childParam = childParam+"|"+listChildParam.get(i);
            else
                childParam = listChildParam.get(i);
        }


        if(childParam.length() > 0)
        {
            btnAddChild.setText("Add More");
        }
        else
        {
            btnAddChild.setText("Add Children");
        }
    }
}
