package com.champs21.schoolapp.classtune;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.champs21.schoolapp.R;
import com.champs21.schoolapp.model.Batch;
import com.champs21.schoolapp.model.TeacherInfo;
import com.champs21.schoolapp.model.Wrapper;
import com.champs21.schoolapp.networking.AppRestClient;
import com.champs21.schoolapp.utils.AppConstant;
import com.champs21.schoolapp.utils.GsonParser;
import com.champs21.schoolapp.utils.URLHelper;
import com.champs21.schoolapp.viewhelpers.UIHelper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by BLACK HAT on 12-Nov-15.
 */
public class CreateTeacherActivity extends Activity implements DatePicker.OnDateChangedListener{

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

    private int year;
    private int month;
    private int day;

    private LinearLayout layoutUserIdHolder;
    private TextView txtUserId;

    private EditText txtEmployeeNumber;
    private DatePicker pickerDob;
    private Spinner spinnerGender;
    private EditText txtContact;
    private EditText txtJobTitle;
    private Spinner spinnerCategory;
    private Spinner spinnerDepartment;
    private Spinner spinnerGrade;
    private TextView txtPosition;
    private DatePicker pickerJoiningDate;
    private Spinner spinnerBatch;
    private LinearLayout layoutPositionHolder;

    private Button btnCreate;

    private String dateJoining = "";

    private List<Batch> listBatch;
    private String batchId = "";
    private List<String> listGender;

    private List<TeacherInfo> listTeacherInfoGrade;
    private List<TeacherInfo> listTeacherInfoDepartment;
    private List<TeacherInfo> listTeacherInfoCategory;

    private String selectedGrade = "";
    private String selectedDepartment = "";
    private String selectedCategory = "";
    private String selectedPosition = "";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_teacher);

        uiHelper = new UIHelper(CreateTeacherActivity.this);

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


        listBatch = new ArrayList<Batch>();

        listTeacherInfoGrade = new ArrayList<TeacherInfo>();
        listTeacherInfoDepartment = new ArrayList<TeacherInfo>();
        listTeacherInfoCategory = new ArrayList<TeacherInfo>();

        initView();
        initAction();
        initApiGetBatch();
        initApiGetTeacherInfo();

    }

    private void initView()
    {
        layoutUserIdHolder = (LinearLayout)this.findViewById(R.id.layoutUserIdHolder);
        txtUserId = (TextView)this.findViewById(R.id.txtUserId);

        txtEmployeeNumber = (EditText)this.findViewById(R.id.txtEmployeeNumber);
        pickerDob = (DatePicker)this.findViewById(R.id.pickerDob);
        spinnerGender = (Spinner)this.findViewById(R.id.spinnerGender);
        txtContact = (EditText)this.findViewById(R.id.txtContact);
        txtJobTitle = (EditText)this.findViewById(R.id.txtJobTitle);
        spinnerCategory = (Spinner)this.findViewById(R.id.spinnerCategory);
        spinnerDepartment = (Spinner)this.findViewById(R.id.spinnerDepartment);
        spinnerGrade = (Spinner)this.findViewById(R.id.spinnerGrade);
        txtPosition = (TextView)this.findViewById(R.id.txtPosition);
        pickerJoiningDate = (DatePicker)this.findViewById(R.id.pickerJoiningDate);
        spinnerBatch = (Spinner)this.findViewById(R.id.spinnerBatch);

        btnCreate = (Button)this.findViewById(R.id.btnCreate);

        layoutPositionHolder = (LinearLayout)this.findViewById(R.id.layoutPositionHolder);


    }

    private void initAction()
    {
        setCurrentDateOnView();

        txtEmployeeNumber.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (s.length() != 0) {
                    layoutUserIdHolder.setVisibility(View.VISIBLE);
                    txtUserId.setText("Your user id is: " + schoolId + "-" + txtEmployeeNumber.getText().toString());
                } else {
                    layoutUserIdHolder.setVisibility(View.GONE);
                }

            }
        });

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


        spinnerCategory.setPrompt("Select a category");
        spinnerDepartment.setPrompt("Select department");
        spinnerGrade.setPrompt("Select a grade");

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(checkValidForm() == true)
                {
                    initApiCallCreateTeacher();
                }
            }
        });


    }



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

        dateJoining = sb.toString();
        pickerJoiningDate.init(year, month, day, this);

    }

    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

        switch (view.getId())
        {
            case R.id.pickerDob:
                StringBuilder sb = new StringBuilder()
                        .append(year).append("-")
                        .append(monthOfYear + 1).append("-")
                        .append(dayOfMonth).append(" ");

                Log.e("CHANGED_DATE_DOB", "is: " + sb.toString());
                dob = sb.toString();

                break;

            case R.id.pickerJoiningDate:
                StringBuilder sb1 = new StringBuilder()
                        .append(year).append("-")
                        .append(monthOfYear + 1).append("-")
                        .append(dayOfMonth).append(" ");

                Log.e("CHANGED_DATE_JOINING", "is: " + sb1.toString());
                dateJoining = sb1.toString();

                break;

            default:
                break;

        }


    }


    private void initBatchSpinner()
    {
        final SpinnerBatchAdapter adapter = new SpinnerBatchAdapter(this, android.R.layout.simple_spinner_item, listBatch);
        adapter.notifyDataSetChanged();
        spinnerBatch.setAdapter(adapter);

        spinnerBatch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Batch batch = adapter.getItem(position);
                batchId = batch.getId();
                Log.e("Spinner batch click", "id is: " + batchId+" name is: "+batch.getName());

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    private void initApiGetBatch()
    {
        RequestParams params = new RequestParams();

        params.put("school_code", schoolCode);

        AppRestClient.post(URLHelper.URL_PAID_BATCH, params, batchHandler);
    }

    AsyncHttpResponseHandler batchHandler = new AsyncHttpResponseHandler() {

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

            listBatch.clear();


            Wrapper modelContainer = GsonParser.getInstance()
                    .parseServerResponse(responseString);

            if (modelContainer.getStatus().getCode() == 200) {

                Log.e("CODE 200", "code 200");
                JsonArray arrayBatch = modelContainer.getData().get("batches").getAsJsonArray();
                for (int i = 0; i < parseBatch(arrayBatch.toString()).size(); i++)
                {
                    listBatch.add(parseBatch(arrayBatch.toString()).get(i));
                }

                initBatchSpinner();


            }

            else {

            }



        };
    };

    private ArrayList<Batch> parseBatch(String object) {
        ArrayList<Batch> data = new ArrayList<Batch>();
        data = new Gson().fromJson(object, new TypeToken<ArrayList<Batch>>() {}.getType());
        return data;
    }


    private void initTeacherInfoSpiners()
    {

        final SpinnerTeacherInfoAdapter adapterGrade = new SpinnerTeacherInfoAdapter(this, android.R.layout.simple_spinner_item, listTeacherInfoGrade);
        adapterGrade.notifyDataSetChanged();
        spinnerGrade.setAdapter(adapterGrade);

        spinnerGrade.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                TeacherInfo data = adapterGrade.getItem(position);
                selectedGrade = data.getId();
                Log.e("Spinner info grad click", "id is: " + data.getId()+" name is: "+data.getName());

                layoutPositionHolder.setVisibility(View.VISIBLE);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        final SpinnerTeacherInfoAdapter adapterDepartment = new SpinnerTeacherInfoAdapter(this, android.R.layout.simple_spinner_item, listTeacherInfoDepartment);
        adapterDepartment.notifyDataSetChanged();
        spinnerDepartment.setAdapter(adapterDepartment);

        spinnerDepartment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                TeacherInfo data = adapterDepartment.getItem(position);
                selectedDepartment = data.getId();
                Log.e("Spinner info dep click", "id is: " + data.getId()+" name is: "+data.getName());

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        final SpinnerTeacherInfoAdapter adapterCategory = new SpinnerTeacherInfoAdapter(this, android.R.layout.simple_spinner_item, listTeacherInfoCategory);
        adapterCategory.notifyDataSetChanged();
        spinnerCategory.setAdapter(adapterCategory);

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                TeacherInfo data = adapterCategory.getItem(position);
                selectedCategory = data.getId();
                Log.e("Spinner info cat click", "id is: " + data.getId()+" name is: "+data.getName());

                initApiCallPosition(selectedCategory);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    private void initApiGetTeacherInfo()
    {
        RequestParams params = new RequestParams();

        params.put("school_code", schoolCode);

        AppRestClient.post(URLHelper.URL_TEACHER_INFO, params, teacherInfoHandler);
    }

    AsyncHttpResponseHandler teacherInfoHandler = new AsyncHttpResponseHandler() {


        //UIHelper uiHelper2 = new UIHelper(CreateTeacherActivity.this);
        @Override
        public void onFailure(Throwable arg0, String arg1) {
            /*uiHelper2.showMessage(arg1);
            if (uiHelper2.isDialogActive()) {
                uiHelper2.dismissLoadingDialog();
            }*/
        };

        @Override
        public void onStart() {

            //uiHelper2.showLoadingDialog("Please wait...");


        };

        @Override
        public void onSuccess(int arg0, String responseString) {

            Log.e("SCCCCC", "response: " + responseString);

            //uiHelper2.dismissLoadingDialog();

            listTeacherInfoGrade.clear();
            listTeacherInfoDepartment.clear();
            listTeacherInfoCategory.clear();


            Wrapper modelContainer = GsonParser.getInstance()
                    .parseServerResponse(responseString);

            if (modelContainer.getStatus().getCode() == 200) {

                Log.e("CODE 200", "code 200");
                JsonArray arrayGrades = modelContainer.getData().get("grades").getAsJsonArray();
                JsonArray arrayDepartment = modelContainer.getData().get("departments").getAsJsonArray();
                JsonArray arrayCategories = modelContainer.getData().get("categories").getAsJsonArray();


                for (int i = 0; i < parseTeacherInfo(arrayGrades.toString()).size(); i++)
                {
                    listTeacherInfoGrade.add(parseTeacherInfo(arrayGrades.toString()).get(i));
                }

                for (int i = 0; i < parseTeacherInfo(arrayDepartment.toString()).size(); i++)
                {
                    listTeacherInfoDepartment.add(parseTeacherInfo(arrayDepartment.toString()).get(i));
                }

                for (int i = 0; i < parseTeacherInfo(arrayCategories.toString()).size(); i++)
                {
                    listTeacherInfoCategory.add(parseTeacherInfo(arrayCategories.toString()).get(i));
                }


                initTeacherInfoSpiners();

                //initApiCallPosition(selectedCategory);


            }

            else {

            }



        };
    };

    private ArrayList<TeacherInfo> parseTeacherInfo(String object) {
        ArrayList<TeacherInfo> data = new ArrayList<TeacherInfo>();
        data = new Gson().fromJson(object, new TypeToken<ArrayList<TeacherInfo>>() {}.getType());
        return data;
    }

    private void initApiCallPosition(String selectedCategory)
    {
        RequestParams params = new RequestParams();

        params.put("school_code", schoolCode);
        params.put("category_id", selectedCategory);

        AppRestClient.post(URLHelper.URL_TEACHER_POSITION, params, teacherPositionHandler);
    }

    AsyncHttpResponseHandler teacherPositionHandler = new AsyncHttpResponseHandler() {


        //UIHelper uiHelper2 = new UIHelper(CreateTeacherActivity.this);
        @Override
        public void onFailure(Throwable arg0, String arg1) {
            /*uiHelper2.showMessage(arg1);
            if (uiHelper2.isDialogActive()) {
                uiHelper2.dismissLoadingDialog();
            }*/
        };

        @Override
        public void onStart() {

            //uiHelper2.showLoadingDialog("Please wait...");


        };

        @Override
        public void onSuccess(int arg0, String responseString) {

            Log.e("SCCCCC", "response: " + responseString);

            //uiHelper2.dismissLoadingDialog();


            Wrapper modelContainer = GsonParser.getInstance()
                    .parseServerResponse(responseString);

            if (modelContainer.getStatus().getCode() == 200) {

                Log.e("CODE 200", "code 200");

                JsonArray arrayPos = modelContainer.getData().get("position").getAsJsonArray();

                String posName = arrayPos.get(0).getAsJsonObject().get("name").getAsString();
                selectedPosition = arrayPos.get(0).getAsJsonObject().get("id").getAsString();

                txtPosition.setText(posName);

            }

            else if (modelContainer.getStatus().getCode() == 401)
            {

                Log.e("CODE 401", "code 401");

               uiHelper.showErrorDialog("The school didn't create employee position for this yet!");

            }

            else if(modelContainer.getStatus().getCode() == 400)
            {
                Log.e("CODE 400", "code 400");

                uiHelper.showErrorDialog("Something went wrong please try again.");
            }

            else {

            }



        };
    };

    private boolean checkValidForm()
    {
        boolean isValid = true;

        if(txtEmployeeNumber.getText().toString().matches(""))
        {
            uiHelper.showErrorDialog("Employee no. cannot be empty!");
            isValid = false;
        }

        else if(txtContact.getText().toString().matches(""))
        {
            uiHelper.showErrorDialog("Contact no. cannot be empty!!");
            isValid = false;
        }

        else if(selectedGrade.matches(""))
        {
            uiHelper.showErrorDialog("Please select a grade!");
            isValid = false;
        }

        else if(selectedDepartment.matches(""))
        {
            uiHelper.showErrorDialog("Please select a department!");
            isValid = false;
        }

        else if(selectedCategory.matches(""))
        {
            uiHelper.showErrorDialog("Please select a category!");
            isValid = false;
        }

        else if(txtJobTitle.getText().toString().matches(""))
        {
            uiHelper.showErrorDialog("Please select a job title!");
            isValid = false;
        }

        else if(txtJobTitle.getText().toString().matches(""))
        {
            uiHelper.showErrorDialog("Please select a job title!");
            isValid = false;
        }


        else if(batchId.matches(""))
        {
            uiHelper.showErrorDialog("Please select a batch!");
            isValid = false;
        }




        return isValid;
    }


    private void initApiCallCreateTeacher()
    {
        RequestParams params = new RequestParams();

        params.put("first_name", firstName);//
        params.put("last_name", lastName);//
        params.put("email", eMail);//
        params.put("password", password);//
        params.put("school_code", schoolCode);//
        params.put("date_of_birth", dob);//
        params.put("gender", gender);//
        params.put("contact_no", txtContact.getText().toString());//

        params.put("employee_number", txtEmployeeNumber.getText().toString());
        params.put("job_title", txtJobTitle.getText().toString());
        params.put("employee_category_id", selectedCategory);
        params.put("employee_position_id", selectedPosition);
        params.put("employee_department_id", selectedDepartment);
        params.put("employee_grade_id", selectedGrade);
        params.put("joining_date", dateJoining);
        params.put("batch_id", batchId);






        AppRestClient.post(URLHelper.URL_PAID_TEACHER, params, createTeacherHandler);
    }

    AsyncHttpResponseHandler createTeacherHandler = new AsyncHttpResponseHandler() {

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


}
