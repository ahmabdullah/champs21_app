package com.champs21.schoolapp.classtune;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.champs21.freeversion.CompleteProfileActivityContainer;
import com.champs21.freeversion.HomePageFreeVersion;
import com.champs21.schoolapp.R;
import com.champs21.schoolapp.adapters.CropOptionAdapter;
import com.champs21.schoolapp.fragments.AlbumStorageDirFactory;
import com.champs21.schoolapp.fragments.BaseAlbumDirFactory;
import com.champs21.schoolapp.fragments.FroyoAlbumDirFactory;
import com.champs21.schoolapp.fragments.UserTypeSelectionDialog;
import com.champs21.schoolapp.model.Batch;
import com.champs21.schoolapp.model.CropOption;
import com.champs21.schoolapp.model.UserAuthListener;
import com.champs21.schoolapp.model.Wrapper;
import com.champs21.schoolapp.networking.AppRestClient;
import com.champs21.schoolapp.utils.AppConstant;
import com.champs21.schoolapp.utils.AppUtility;
import com.champs21.schoolapp.utils.GsonParser;
import com.champs21.schoolapp.utils.RequestKeyHelper;
import com.champs21.schoolapp.utils.SPKeyHelper;
import com.champs21.schoolapp.utils.URLHelper;
import com.champs21.schoolapp.utils.UserHelper;
import com.champs21.schoolapp.viewhelpers.PopupDialogChangePassword;
import com.champs21.schoolapp.viewhelpers.UIHelper;
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
 * Created by BLACK HAT on 09-Nov-15.
 */
public class CreateStudentActivity extends FragmentActivity implements UserAuthListener {


    private int ordinal = -1;
    private String schoolId = "";

    private Spinner spinnerBatch;
    //private DatePicker pickerDob;
    private Spinner spinnerGender;
    private EditText txtContact;
    private EditText txtAdmission;
    private Button btnCreate;

    private UIHelper uiHelper;

    private List<String> listGender;

    private String gender = "";
    private String dob = "";

    private int year;
    private int month;
    private int day;

    private String firstName = "";
    private String lastName = "";
    private String eMail = "";
    private String password = "";
    private String schoolCode = "";
    private String batchId = "";

    private List<Batch> listBatch;

    private LinearLayout layoutUserIdHolder;
    private TextView txtUserId;

    //upload photo
    //private Button btnUploadPhoto;

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
    private UserHelper userHelper;

    private ActionBar actionBar;
    private ImageButton btnNext;

    private TextView txtDob;
    private RelativeLayout layoutDatePicker;

    private TextView txtUploadPhoto;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_student2);

        Bundle extras = getIntent().getExtras();
        userHelper = new UserHelper(this, CreateStudentActivity.this);
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

        Log.e("ORDINAL AND SCHOOL ID", "ordinal: "+ordinal+" school id: "+schoolId);

        uiHelper = new UIHelper(CreateStudentActivity.this);
        listBatch = new ArrayList<Batch>();

        initView();
        setUpActionBar();
        initAction();
        initApiGetBatch();


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

                //batchId = listBatch.get(0).getId();
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
                Log.e("Spinner batch click", "id is: " + batchId + " name is: " + batch.getName());

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initView()
    {
        spinnerBatch = (Spinner)this.findViewById(R.id.spinnerBatch);
        //pickerDob = (DatePicker)this.findViewById(R.id.pickerDob);
        spinnerGender = (Spinner)this.findViewById(R.id.spinnerGender);
        txtContact = (EditText)this.findViewById(R.id.txtContact);
        txtAdmission = (EditText)this.findViewById(R.id.txtAdmission);
        btnCreate = (Button)this.findViewById(R.id.btnCreate);

        layoutUserIdHolder = (LinearLayout)this.findViewById(R.id.layoutUserIdHolder);
        txtUserId = (TextView)this.findViewById(R.id.txtUserId);

        layoutUploadPhoto = (RelativeLayout)this.findViewById(R.id.layoutUploadPhoto);

        imageNameContainer = (LinearLayout)this.findViewById(R.id.image_attached_layout);
        tvImageName = (TextView)this.findViewById(R.id.tv_image_name);
        btn_cross_image = (ImageView)this.findViewById(R.id.btn_cross_image);

        txtDob = (TextView)this.findViewById(R.id.txtDob);
        layoutDatePicker = (RelativeLayout)this.findViewById(R.id.layoutDatePicker);

        txtUploadPhoto = (TextView)this.findViewById(R.id.txtUploadPhoto);
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

        //setCurrentDateOnView();

        /*btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkValidForm() == true) {
                    initApicall();
                }
            }
        });*/

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkValidForm() == true) {
                    initApicall();
                }

            }
        });


        txtAdmission.addTextChangedListener(new TextWatcher() {

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
                    txtUserId.setText(schoolId + "-" + txtAdmission.getText().toString());
                } else {
                    layoutUserIdHolder.setVisibility(View.GONE);
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

        layoutDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(CreateStudentActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

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

            CreateStudentActivity.this.year = myCalendar.get(Calendar.YEAR);
            CreateStudentActivity.this.month = myCalendar.get(Calendar.MONTH);
            CreateStudentActivity.this.day = myCalendar.get(Calendar.DAY_OF_MONTH);

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

    /*public void setCurrentDateOnView() {

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

        //pickerDob.init(year, month, day, this);

    }*/

    /*@Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        StringBuilder sb = new StringBuilder()
                .append(year).append("-")
                .append(monthOfYear + 1).append("-")
                .append(dayOfMonth).append(" ");

        Log.e("CHANGED_DATE", "is: " + sb.toString());
        dob = sb.toString();
    }*/


    private boolean checkValidForm()
    {
        boolean isValid = true;

        if(txtAdmission.getText().toString().matches(""))
        {
            uiHelper.showErrorDialog(AppConstant.CLASSTUNE_MESSAGE_ADMISSION_NUMBER);
            isValid = false;
        }
        else if(batchId.matches(""))
        {
            uiHelper.showErrorDialog(AppConstant.CLASSTUNE_MESSAGE_BATCH_SELECT);
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
        return isValid;
    }

    private void initApicall()
    {
        RequestParams params = new RequestParams();

        params.put("first_name", firstName);
        params.put("last_name", lastName);
        params.put("email", eMail);
        params.put("password", password);
        params.put("school_code", schoolCode);
        params.put("batch_id", batchId);

        params.put("date_of_birth", dob);

        params.put("gender", gender);
        params.put("contact_no", txtContact.getText().toString());
        params.put("admission", txtAdmission.getText().toString());

        if(!selectedImagePath.equalsIgnoreCase(""))
        {
            File myImage = new File(selectedImagePath);
            try {
                params.put(RequestKeyHelper.PROFILE_IMAGE, myImage);

                Log.e("IMG_FILE", "is: "+myImage);
            } catch(FileNotFoundException e) {}
        }


        userHelper.doClassTuneLogin(URLHelper.URL_PAID_STUDENT, params);
        //AppRestClient.post(URLHelper.URL_PAID_STUDENT, params, createStudentHandler);
    }

    AsyncHttpResponseHandler createStudentHandler = new AsyncHttpResponseHandler() {

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

            Log.d("TOTAL RESPONSE", "response: " + responseString);

            uiHelper.dismissLoadingDialog();


            Wrapper modelContainer = GsonParser.getInstance()
                    .parseServerResponse(responseString);

            if (modelContainer.getStatus().getCode() == 200) {

                Log.e("CODE 200", "code 200");
            }

            else if (modelContainer.getStatus().getCode() == 401) {

                Log.e("CODE 401", "code 401");
                uiHelper.showErrorDialog(AppConstant.CLASSTUNE_MESSAGE_ADMISSION_NUMBER_EXISTS);
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
                CreateStudentActivity.this);



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
            Toast.makeText(CreateStudentActivity.this, "Camera not found!", Toast.LENGTH_SHORT)
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
                            CreateStudentActivity.this.getContentResolver().delete(uri,
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
                        Intent intent = new Intent(CreateStudentActivity.this,
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
                        }else doPaidNavigation();
                        break;

                    default:
                        break;
                }

            } else {
                finish();
                Intent intent = new Intent(CreateStudentActivity.this,
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

    public void doPaidNavigation(){
        switch (userHelper.getUser().getType()) {

            case PARENTS:
                if (userHelper.getUser().getChildren() == null) {
                    Log.e("Userhelper", "null");
                }
                if (userHelper.getUser().getChildren().size() > 0) {
                    Intent paidIntent = new Intent(CreateStudentActivity.this,
                            HomePageFreeVersion.class);
                    paidIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(paidIntent);
                    finish();
                }
                break;
            default:

                /*if(userHelper.getUserAccessType()== UserHelper.UserAccessType.PAID ){//&& UserHelper.isFirstLogin()
                    PopupDialogChangePassword picker = new PopupDialogChangePassword();
                    picker.show(getSupportFragmentManager(), null);
                }*/
                Intent paidIntent = new Intent(CreateStudentActivity.this,
                        HomePageFreeVersion.class);
                paidIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(paidIntent);
                finish();
                break;
        }

    }
}
