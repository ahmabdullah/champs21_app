package com.classtune.schoolapp.classtune;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.classtune.freeversion.CompleteProfileActivityContainer;
import com.classtune.freeversion.HomePageFreeVersion;
import com.classtune.schoolapp.R;
import com.classtune.schoolapp.adapters.CropOptionAdapter;
import com.classtune.schoolapp.fragments.AlbumStorageDirFactory;
import com.classtune.schoolapp.fragments.BaseAlbumDirFactory;
import com.classtune.schoolapp.fragments.FroyoAlbumDirFactory;
import com.classtune.schoolapp.fragments.UserTypeSelectionDialog;
import com.classtune.schoolapp.model.Batch;
import com.classtune.schoolapp.model.CropOption;
import com.classtune.schoolapp.model.TeacherInfo;
import com.classtune.schoolapp.model.UserAuthListener;
import com.classtune.schoolapp.model.Wrapper;
import com.classtune.schoolapp.networking.AppRestClient;
import com.classtune.schoolapp.utils.AppConstant;
import com.classtune.schoolapp.utils.AppUtility;
import com.classtune.schoolapp.utils.GsonParser;
import com.classtune.schoolapp.utils.RequestKeyHelper;
import com.classtune.schoolapp.utils.SPKeyHelper;
import com.classtune.schoolapp.utils.URLHelper;
import com.classtune.schoolapp.utils.UserHelper;
import com.classtune.schoolapp.viewhelpers.PopupDialogChangePassword;
import com.classtune.schoolapp.viewhelpers.UIHelper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by BLACK HAT on 12-Nov-15.
 */
public class CreateTeacherActivity extends FragmentActivity implements UserAuthListener{

    private UIHelper uiHelper;

    private int ordinal = -1;
    private String schoolId = "";

    private String gender = "";
    private String dob = "";

    private String firstName = "";
    private String lastName = "";
    private String eMail = "";
    private String password = "";
    private String schoolCode = "";

    private int year;
    private int month;
    private int day;

    private UserHelper userHelper;

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

    private DatePicker pickerJoiningDate;
    private Spinner spinnerBatch;


    private String dateJoining = "";

    private List<Batch> listBatch;
    private String batchId = "";
    private List<String> listGender;

    private List<TeacherInfo> listTeacherInfoGrade;
    private List<TeacherInfo> listTeacherInfoDepartment;
    private List<TeacherInfo> listTeacherInfoCategory;
    private List<TeacherInfo> listTeacherInfoPosition;

    private String selectedGrade = "";
    private String selectedDepartment = "";
    private String selectedCategory = "";
    private String selectedPosition = "";

    private TextView txtDob;
    private Spinner spinnerPosition;
    private TextView txtJoiningDate;
    private RelativeLayout layoutDatePicker;
    private RelativeLayout layoutJoiningDatePicker;

    private ActionBar actionBar;
    private ImageButton btnNext;

    private ProgressDialog pd;

    private RelativeLayout layoutGreenPanelPosition;
    private View viewGreenPanelPosition;
    RelativeLayout layoutPositionHolder;

    private TextView txtMessage;



    //upload photo
    private RelativeLayout layoutUploadPhoto;

    private final int REQUEST_CODE_CAMERA = 110;
    private final int REQUEST_CODE_GELLERY = 111;
    private final int REQUEST_CODE_CROP = 112;
    private Uri uri = null;
    private static File schoolDirectory = null;
    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;
    private String mCurrentPhotoPath;
    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";

    private LinearLayout imageNameContainer;
    private TextView tvImageName;
    private String selectedImagePath = "";
    private ImageView btn_cross_image;

    private TextView txtUploadPhoto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_teacher2);

        uiHelper = new UIHelper(CreateTeacherActivity.this);

        userHelper = new UserHelper(this, CreateTeacherActivity.this);

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
        listTeacherInfoPosition = new ArrayList<TeacherInfo>();

        initView();
        setUpActionBar();
        initAction();
        initApiGetBatch();
        initApiGetTeacherInfo();

        schoolDirectory = new File(this.getFilesDir().getPath()
                + "/classtune");
        schoolDirectory.mkdirs(); // create folders where write files

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
        } else {
            mAlbumStorageDirFactory = new BaseAlbumDirFactory();
        }

        updateImagenamePanel(false);

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

        pickerJoiningDate = (DatePicker)this.findViewById(R.id.pickerJoiningDate);
        spinnerBatch = (Spinner)this.findViewById(R.id.spinnerBatch);


        txtDob = (TextView)this.findViewById(R.id.txtDob);
        spinnerPosition = (Spinner)this.findViewById(R.id.spinnerPosition);
        txtJoiningDate = (TextView)this.findViewById(R.id.txtJoiningDate);
        layoutDatePicker = (RelativeLayout)this.findViewById(R.id.layoutDatePicker);
        layoutJoiningDatePicker = (RelativeLayout)this.findViewById(R.id.layoutJoiningDatePicker);

        layoutUploadPhoto = (RelativeLayout)this.findViewById(R.id.layoutUploadPhoto);
        imageNameContainer = (LinearLayout)this.findViewById(R.id.image_attached_layout);
        tvImageName = (TextView)this.findViewById(R.id.tv_image_name);
        btn_cross_image = (ImageView)this.findViewById(R.id.btn_cross_image);
        txtUploadPhoto = (TextView)this.findViewById(R.id.txtUploadPhoto);


        layoutGreenPanelPosition = (RelativeLayout)this.findViewById(R.id.layoutGreenPanelPosition);
        viewGreenPanelPosition = this.findViewById(R.id.viewGreenPanelPosition);
        layoutPositionHolder = (RelativeLayout)findViewById(R.id.layoutPositionHolder);

        txtMessage = (TextView)this.findViewById(R.id.txtMessage);

    }

    private void setUpActionBar() {
        actionBar = getActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.classtune_green_color)));
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);

        View cView = getLayoutInflater().inflate(R.layout.actionbar_view_classtune, null);

        btnNext = (ImageButton) cView.findViewById(R.id.btnNext);

        actionBar.setCustomView(cView);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

    }

    private void initAction()
    {

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
                    txtUserId.setText(schoolId + "-" + txtEmployeeNumber.getText().toString());
                } else {
                    layoutUserIdHolder.setVisibility(View.GONE);
                }

            }
        });

        listGender = new ArrayList<String>();
        listGender.add("Male");
        listGender.add("Female");

        SpinnerGenderAdapter genderAdapter = new SpinnerGenderAdapter(this, android.R.layout.simple_spinner_item, listGender);
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


        //spinnerCategory.setPrompt("Select a category");
        //spinnerDepartment.setPrompt("Select department");
        //spinnerGrade.setPrompt("Select a grade");




        layoutDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(CreateTeacherActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();

            }
        });

        layoutJoiningDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(CreateTeacherActivity.this, date2, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(checkValidForm() == true)
                {
                    initApiCallCreateTeacher();
                }

            }
        });

        layoutUploadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showPicChooserDialog();
            }
        });

        btn_cross_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectedImagePath = "";
                updateImagenamePanel(false);
            }
        });


        showPositionMessageText(true);

    }


    private void showPositionMessageText(boolean show)
    {
        if(show == true)
        {
            txtMessage.setVisibility(View.VISIBLE);
            spinnerPosition.setVisibility(View.GONE);
        }
        else
        {
            txtMessage.setVisibility(View.GONE);
            spinnerPosition.setVisibility(View.VISIBLE);
        }

    }

    Calendar myCalendar = Calendar.getInstance();

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            CreateTeacherActivity.this.year = myCalendar.get(Calendar.YEAR);
            CreateTeacherActivity.this.month = myCalendar.get(Calendar.MONTH);
            CreateTeacherActivity.this.day = myCalendar.get(Calendar.DAY_OF_MONTH);

            StringBuilder sb = new StringBuilder()
                    .append(year).append("-")
                    .append(month + 1).append("-")
                    .append(day).append(" ");

            Log.e("CURRENT_DATE", "is: " + sb.toString());
            dob = sb.toString();
            txtDob.setText(dob);

            //pickerDob.init(CreateStudentActivity.this.year, CreateStudentActivity.this.month, CreateStudentActivity.this.day, this);
        }

    };


    DatePickerDialog.OnDateSetListener date2 = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            CreateTeacherActivity.this.year = myCalendar.get(Calendar.YEAR);
            CreateTeacherActivity.this.month = myCalendar.get(Calendar.MONTH);
            CreateTeacherActivity.this.day = myCalendar.get(Calendar.DAY_OF_MONTH);

            StringBuilder sb = new StringBuilder()
                    .append(year).append("-")
                    .append(month + 1).append("-")
                    .append(day).append(" ");

            Log.e("CURRENT_DATE", "is: " + sb.toString());
            dateJoining = sb.toString();
            txtJoiningDate.setText(dateJoining);

            //pickerDob.init(CreateStudentActivity.this.year, CreateStudentActivity.this.month, CreateStudentActivity.this.day, this);
        }

    };



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

            else if(modelContainer.getStatus().getCode() == 401)
            {
                uiHelper.showErrorDialog(AppConstant.CLASSTUNE_MESSAGE_CLASS_YET);
            }

            else if(modelContainer.getStatus().getCode() == 400)
            {
                uiHelper.showErrorDialog(AppConstant.CLASSTUNE_MESSAGE_SOMETHING_WENT_WRONG);
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

    SpinnerTeacherInfoAdapter adapterPosition = null;
    private void initPositionSpinner()
    {
        if(adapterPosition == null)
            adapterPosition = new SpinnerTeacherInfoAdapter(this, android.R.layout.simple_spinner_item, listTeacherInfoPosition);

        adapterPosition.notifyDataSetChanged();
        spinnerPosition.setAdapter(adapterPosition);

        spinnerPosition.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                TeacherInfo data = adapterPosition.getItem(position);
                selectedPosition = data.getId();
                Log.e("Spinner info pos click", "id is: " + data.getId() + " name is: " + data.getName());

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        Log.e("listPosition size", "is: " + listTeacherInfoPosition.size());


        if(listTeacherInfoPosition.size() > 0)
        {
            showPositionMessageText(false);
        }
        else
        {
            showPositionMessageText(true);
        }
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

            else if(modelContainer.getStatus().getCode() == 401)
            {
                uiHelper.showErrorDialog(AppConstant.CLASSTUNE_MESSAGE_EMPLOYEE_NECESSARY_INFO);
            }

            else if(modelContainer.getStatus().getCode() == 400)
            {
                uiHelper.showErrorDialog(AppConstant.CLASSTUNE_MESSAGE_SOMETHING_WENT_WRONG);
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
            if(pd.isShowing())
                pd.dismiss();
        };

        @Override
        public void onStart() {

            //uiHelper2.showLoadingDialog("Please wait...");

            pd = ProgressDialog.show(CreateTeacherActivity.this, "", "Please wait...", true, false);

        };

        @Override
        public void onSuccess(int arg0, String responseString) {

            pd.dismiss();

            Log.e("SCCCCC", "response: " + responseString);

            //uiHelper2.dismissLoadingDialog();
            listTeacherInfoPosition.clear();
            if(adapterPosition != null)
                adapterPosition.notifyDataSetChanged();


            Wrapper modelContainer = GsonParser.getInstance()
                    .parseServerResponse(responseString);

            if (modelContainer.getStatus().getCode() == 200) {

                Log.e("CODE 200", "code 200");

                JsonArray arrayPos = modelContainer.getData().get("position").getAsJsonArray();

                for (int i = 0; i < parseTeacherInfo(arrayPos.toString()).size(); i++)
                {
                    listTeacherInfoPosition.add(parseTeacherInfo(arrayPos.toString()).get(i));
                }

                initPositionSpinner();



            }

            else if (modelContainer.getStatus().getCode() == 401)
            {

                Log.e("CODE 401", "code 401");

                uiHelper.showErrorDialog(AppConstant.CLASSTUNE_MESSAGE_EMPLOYEE_POSITION);
                selectedPosition = "";

                showPositionMessageText(true);
            }

            else if(modelContainer.getStatus().getCode() == 400)
            {
                Log.e("CODE 400", "code 400");

                uiHelper.showErrorDialog(AppConstant.CLASSTUNE_MESSAGE_SOMETHING_WENT_WRONG);
                selectedPosition = "";

                showPositionMessageText(true);
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
            uiHelper.showErrorDialog(AppConstant.CLASSTUNE_MESSAGE_EMPLOYEE_NUMBER);
            isValid = false;
        }

        else if(dob.matches(""))
        {
            uiHelper.showErrorDialog(AppConstant.CLASSTUNE_MESSAGE_DOB_SELECT);
            isValid = false;
        }
        else if(gender.matches(""))
        {
            uiHelper.showErrorDialog(AppConstant.CLASSTUNE_MESSAGE_GENDER_SELECT);
            isValid = false;
        }

        else if(txtContact.getText().toString().matches(""))
        {
            uiHelper.showErrorDialog(AppConstant.CLASSTUNE_MESSAGE_CONTACT_NUMBER);
            isValid = false;
        }

        else if(txtJobTitle.getText().toString().matches(""))
        {
            uiHelper.showErrorDialog(AppConstant.CLASSTUNE_MESSAGE_JOB_TITLE);
            isValid = false;
        }
        else if(selectedCategory.matches(""))
        {
            uiHelper.showErrorDialog(AppConstant.CLASSTUNE_MESSAGE_EMPLOYEE_CATEGORY);
            isValid = false;
        }
        else if(selectedDepartment.matches(""))
        {
            uiHelper.showErrorDialog(AppConstant.CLASSTUNE_MESSAGE_EMPLOYEE_DEPARTMENT);
            isValid = false;
        }

        else if(selectedPosition.matches("")) {
            uiHelper.showErrorDialog(AppConstant.CLASSTUNE_MESSAGE_EMPLOYEE_POSITION_TYPE);
            isValid = false;
        }

        else if(dateJoining.matches(""))
        {
            uiHelper.showErrorDialog(AppConstant.CLASSTUNE_MESSAGE_EMPLOYEE_JOINING_DATE);
            isValid = false;
        }

        else if(batchId.matches(""))
        {
            uiHelper.showErrorDialog(AppConstant.CLASSTUNE_MESSAGE_BATCH_SELECT);
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


        if(!selectedImagePath.equalsIgnoreCase(""))
        {
            File myImage = new File(selectedImagePath);
            try {
                params.put(RequestKeyHelper.PROFILE_IMAGE, myImage);

                Log.e("IMG_FILE", "is: "+myImage);
            } catch(FileNotFoundException e) {}
        }


        userHelper.doClassTuneLogin(URLHelper.URL_PAID_TEACHER, params);
        //AppRestClient.post(URLHelper.URL_PAID_TEACHER, params, createTeacherHandler);
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
            uiHelper.showErrorDialog(AppConstant.CLASSTUNE_MESSAGE_EMPLOYEE_EXISTS);
        }

        else if (modelContainer.getStatus().getCode() == 400) {

            Log.e("CODE 400", "code 400");
            uiHelper.showErrorDialog(AppConstant.CLASSTUNE_MESSAGE_SOMETHING_WENT_WRONG);
        }


        else {

        }



    };
};


    private void showPicChooserDialog() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                CreateTeacherActivity.this);



        alertDialogBuilder
                .setMessage(AppConstant.CLASSTUNE_MESSAGE_SELECT_SOURCE)
                .setCancelable(false)
                .setPositiveButton("Gallery",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(final DialogInterface dialog,
                                                int which) {
                                // TODO Auto-generated method stub
                                dialog.dismiss();

								/*Intent intent = new Intent();
								intent.setType("image/*");
								intent.setAction(Intent.ACTION_GET_CONTENT);
								startActivityForResult(Intent.createChooser(intent, "Select Picture"),
										1);*/

                                dispatchOpenGelleryApp();

                            }
                        })
                .setNegativeButton("Camera",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(final DialogInterface dialog,
                                                int which) {
                                // TODO Auto-generated method stub
                                dialog.dismiss();

                                dispatchTakePictureIntent();

								/*Intent takePicture = new Intent(
										MediaStore.ACTION_IMAGE_CAPTURE);
								startActivityForResult(takePicture, 0);*/
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(true);

        alertDialog.show();

    }

    public void dispatchOpenGelleryApp() {

        if (Build.VERSION.SDK_INT <19){
            Intent intent = new Intent();
            intent.setType("image/jpeg");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"),
                    REQUEST_CODE_GELLERY);
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("*/*");
            intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
                    uri);
            startActivityForResult(Intent.createChooser(intent,"Complete action using"), REQUEST_CODE_GELLERY);

        }

    }

    public void dispatchTakePictureIntent() {

        PackageManager pm = getPackageManager();
        if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA) == false) {
            Toast.makeText(CreateTeacherActivity.this, "Camera Nnot found!", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File f = null;

        try {
            f = setUpPhotoFile();
            mCurrentPhotoPath = f.getAbsolutePath();
            takePictureIntent
                    .putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));

        } catch (IOException e) {
            e.printStackTrace();
            f = null;
            mCurrentPhotoPath = null;
        }

        startActivityForResult(takePictureIntent, REQUEST_CODE_CAMERA);
    }

    private File setUpPhotoFile() throws IOException {

        File f = createImageFile();
        mCurrentPhotoPath = f.getAbsolutePath();
        // Uri.fromFile(f);
        return f;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
        File albumF = getAlbumDir();
        File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX,
                albumF);
        return imageF;
    }

    private File getAlbumDir() {
        File storageDir = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {

            storageDir = mAlbumStorageDirFactory
                    .getAlbumStorageDir(getAlbumName());

            if (storageDir != null) {
                if (!storageDir.mkdirs()) {
                    if (!storageDir.exists()) {
                        Log.d("CameraSample", "failed to create directory");
                        return null;
                    }
                }
            }

        } else {
            Log.v(getString(R.string.app_name),
                    "External storage is not mounted READ/WRITE.");
        }

        return storageDir;
    }

    public String getAlbumName() {
        return getString(R.string.album_name_classtune);
    }



    private void updateImagenamePanel(boolean isVisible) {
        if (isVisible) {
            imageNameContainer.setVisibility(View.VISIBLE);
            tvImageName.setText(getFileNameFromPath(selectedImagePath));

            txtUploadPhoto.setVisibility(View.GONE);

        } else {
            imageNameContainer.setVisibility(View.GONE);

            txtUploadPhoto.setVisibility(View.VISIBLE);
        }
    }
    private String getFileNameFromPath(String path) {
        String[] tokens = path.split("/");
        return tokens[tokens.length - 1];
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_CAMERA:
                if (resultCode == this.RESULT_OK) {
                    // Log.e("addAdvertiseController.mCurrentPhotoPath",""+addAdvertiseController.mCurrentPhotoPath);
                    dispatchCropIntent(Uri.fromFile(new File(mCurrentPhotoPath)));
                }
                if (resultCode == this.RESULT_CANCELED) {
                    return;
                }
                break;
            case REQUEST_CODE_GELLERY:
                if (resultCode == this.RESULT_OK) {
                    // addAdvertiseController.mCurrentPhotoPath=getFilePath(data.getData());
                    // Log.e("addAdvertiseController.mCurrentPhotoPath2",""+addAdvertiseController.mCurrentPhotoPath);
                    dispatchCropIntent(data.getData());
                }
                if (resultCode == this.RESULT_CANCELED) {
                    return;
                }
                break;

            case REQUEST_CODE_CROP:
                if (resultCode == this.RESULT_OK) {
                    // Log.e("addAdvertiseController.mCurrentPhotoPath3",""+addAdvertiseController.mCurrentPhotoPath);
                    File file = new File(mCurrentPhotoPath);
                    Log.e("Normal file size:", "Image size before compress:"
                            + (file.length() / 1024) + "");
				/*
				 * if((file.length()/1024)<300) { AlertDialog_big_image_size();
				 * } else {
				 */
                    handleBigCameraPhoto(false,
                            Uri.fromFile(new File(mCurrentPhotoPath)));
                    // }
                }

                if (resultCode == this.RESULT_CANCELED) {
                    return;
                }
                break;


        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void dispatchCropIntent(Uri uriParam) {

        uri = uriParam;
        final ArrayList<CropOption> cropOptions = new ArrayList<CropOption>();

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");

        List<ResolveInfo> list = this.getPackageManager()
                .queryIntentActivities(intent, 0);

        int size = list.size();
        if (size == 0) {
            // Toast.makeText(this, "Can not find image crop app",
            // Toast.LENGTH_SHORT).show();
            handleBigCameraPhoto(true, uriParam);
            return;
        } else {
            intent.setData(uri);
            int height = AppUtility.getImageViewerImageHeight(this);
            intent.putExtra("outputX", 600);
            intent.putExtra("outputY", 600);
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("scale", true);
            intent.putExtra("return-data", false);
            intent.putExtra("noFaceDetection", true);

            File f = null;
            try {
                f = setUpPhotoFile();
                mCurrentPhotoPath = f.getAbsolutePath();

            } catch (IOException e) {
                e.printStackTrace();
                f = null;
                mCurrentPhotoPath = null;
            }
            intent.putExtra("output", Uri.fromFile(f));

            if (size == 1) {
                Intent i = new Intent(intent);
                ResolveInfo res = list.get(0);

                i.setComponent(new ComponentName(res.activityInfo.packageName,
                        res.activityInfo.name));

                startActivityForResult(i, REQUEST_CODE_CROP);
            } else {
                for (ResolveInfo res : list) {
                    final CropOption co = new CropOption();

                    co.title = this.getPackageManager()
                            .getApplicationLabel(
                                    res.activityInfo.applicationInfo);
                    co.icon = this.getPackageManager()
                            .getApplicationIcon(
                                    res.activityInfo.applicationInfo);
                    co.appIntent = new Intent(intent);

                    co.appIntent
                            .setComponent(new ComponentName(
                                    res.activityInfo.packageName,
                                    res.activityInfo.name));

                    cropOptions.add(co);
                }

                CropOptionAdapter adapter = new CropOptionAdapter(this
                        .getApplicationContext(), cropOptions);

                AlertDialog.Builder builder = new AlertDialog.Builder(
                        this);
                builder.setTitle("Choose Crop App");
                builder.setAdapter(adapter,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                startActivityForResult(
                                        cropOptions.get(item).appIntent,
                                        REQUEST_CODE_CROP);
                            }
                        });

                builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {

                        if (uri != null) {
                            CreateTeacherActivity.this.getContentResolver().delete(uri,
                                    null, null);
                            uri = null;
                        }
                    }
                });

                AlertDialog alert = builder.create();

                alert.show();
            }
        }
    }

    private void handleBigCameraPhoto(boolean resizeFlag, Uri uriParam) {

        if (uriParam != null) {
            setPic(resizeFlag, uriParam);
            selectedImagePath = mCurrentPhotoPath;
            updateImagenamePanel(true);
            // galleryAddPic();
            mCurrentPhotoPath = null;
        }

    }

    private void setPic(boolean resizeFlag, Uri uriParam) {

		/* There isn't enough memory to open up more than a couple camera photos */
		/* So pre-scale the target bitmap into which the file is decoded */

		/* Get the size of the image */
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        ContentResolver res = this.getContentResolver();
        Bitmap bitmap = null;
        InputStream is = null;
        try {
            is = res.openInputStream(uriParam);
            if (resizeFlag == true) {
                bitmap = BitmapFactory.decodeStream(is, null, bmOptions);

            } else {
                bitmap = BitmapFactory.decodeStream(is);
            }
            is.close();
        } catch (FileNotFoundException e1) {

            e1.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }

		/* Figure out which way needs to be reduced less */
		/* Get the size of the ImageView */
		/*
		 * int targetW = mImageView.getWidth(); int targetH =
		 * mImageView.getHeight();
		 */
        if (resizeFlag) {
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;
            int value = AppUtility.getImageViewerImageHeight(this);
            int targetW = value;
            int targetH = value;

            int scaleFactor = 1;
            if ((targetW > 0) || (targetH > 0)) {
                scaleFactor = Math.min(photoW / targetW, photoH / targetH);
            }

			/* Set bitmap options to scale the image decode target */
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;

			/* Decode the JPEG file into a Bitmap */
            try {
                is = res.openInputStream(uriParam);
                bitmap = BitmapFactory.decodeStream(is, null, bmOptions);
                is.close();
            } catch (FileNotFoundException e) {

                e.printStackTrace();
            } catch (IOException e) {

                e.printStackTrace();
            }
        }
        // force orientation to portrait
        if (bitmap.getWidth() > bitmap.getHeight()) {
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                    bitmap.getHeight(), matrix, true);
        }
        // Log.e("Aise", "Aise");

        // File file = new File(addAdvertiseController.mCurrentPhotoPath);
        // Log.e("Normal file size:", file.length() + "");
        // Toast.makeText(AddAdvertiseActivity.this, file.length()+"",
        // Toast.LENGTH_SHORT).show();

        FileOutputStream fOut = null;
        try {
            long timestamp = System.currentTimeMillis();
            File ezpsaImageFile = new File(schoolDirectory,
                    getString(R.string.album_name_classtune) + timestamp + ".png");

            fOut = new FileOutputStream(ezpsaImageFile);

            int quality = 40;
            int increament = 10;
            int maxFileSize = 100 * 1024;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fOut);

            while (ezpsaImageFile.length() > maxFileSize) {
                quality += increament;
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fOut);
            }

            // Log.e("Compressed file size:", file.length() + "");

            // Toast.makeText(AddAdvertiseActivity.this, file.length()+"",
            // Toast.LENGTH_SHORT).show();
            fOut.flush();
            fOut.close();
            // b.recycle();
            bitmap.recycle();
            mCurrentPhotoPath = ezpsaImageFile.getPath();

        } catch (Exception e) { // TODO
            e.printStackTrace();
        }

    }

    @Override
    public void onAuthenticationStart() {
        uiHelper.showLoadingDialog(getString(R.string.loading_text));
    }

    @Override
    public void onAuthenticationSuccessful() {
        if (uiHelper.isDialogActive()) {
            uiHelper.dismissLoadingDialog();

        }
        if (UserHelper.isRegistered()) {
            if (UserHelper.isLoggedIn()) {




                switch (UserHelper.getUserAccessType()) {
                    case FREE:
                        Intent intent = new Intent(CreateTeacherActivity.this,
                                HomePageFreeVersion.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                        break;
                    case PAID:
                        if ( UserHelper.isFirstLogin() ){
                            PopupDialogChangePassword picker = new PopupDialogChangePassword();
                            picker.show(getSupportFragmentManager(), null);
                        }else AppUtility.doPaidNavigation(userHelper, CreateTeacherActivity.this);
                        break;

                    default:
                        break;
                }

            } else {
                finish();
                Intent intent = new Intent(CreateTeacherActivity.this,
                        CompleteProfileActivityContainer.class);
                intent.putExtra(SPKeyHelper.USER_TYPE, userHelper.getUser()
                        .getType().ordinal());
                intent.putExtra("FIRST_TIME", true);
                startActivity(intent);

            }
        } else {
            Log.e("TypeSelection!", "GOOOOOOOOOOOOOOOO");
            UserTypeSelectionDialog dialogFrag = UserTypeSelectionDialog
                    .newInstance();
            dialogFrag.show(getSupportFragmentManager().beginTransaction(),
                    "dialog");

        }
    }

    @Override
    public void onAuthenticationFailed(String msg) {

    }

    @Override
    public void onPaswordChanged() {

    }


}
