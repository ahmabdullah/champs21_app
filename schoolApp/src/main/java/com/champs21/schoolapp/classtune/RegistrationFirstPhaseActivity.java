package com.champs21.schoolapp.classtune;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.champs21.schoolapp.R;
import com.champs21.schoolapp.model.Wrapper;
import com.champs21.schoolapp.networking.AppRestClient;
import com.champs21.schoolapp.utils.AppConstant;
import com.champs21.schoolapp.utils.GsonParser;
import com.champs21.schoolapp.utils.URLHelper;
import com.champs21.schoolapp.viewhelpers.UIHelper;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Created by BLACK HAT on 08-Nov-15.
 */
public class RegistrationFirstPhaseActivity extends Activity {


    private EditText txtFirstName;
    private EditText txtLastName;
    private EditText txtEmail;
    private EditText txtPassword;
    private EditText txtRetypePassword;
    private EditText txtSchoolCode;
    private Button btnNext;

    private  UIHelper uiHelper;

    private int ordinal = -1;

    private TextWatcher txtWatcher = null;

    private String schoolId = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_firstphase);

        Bundle extras = getIntent().getExtras();

        if(extras != null)
        {
            ordinal = extras.getInt(AppConstant.USER_TYPE_CLASSTUNE);
        }

        uiHelper = new UIHelper(RegistrationFirstPhaseActivity.this);

        initView();
        initAction();

        Log.e("ORDINAL", "is: "+ordinal);

    }


    private void initView()
    {
        txtFirstName = (EditText)this.findViewById(R.id.txtFirstName);
        txtLastName = (EditText)this.findViewById(R.id.txtLastName);
        txtEmail = (EditText)this.findViewById(R.id.txtEmail);
        txtPassword = (EditText)this.findViewById(R.id.txtPassword);
        txtRetypePassword = (EditText)this.findViewById(R.id.txtRetypePassword);
        txtSchoolCode = (EditText)this.findViewById(R.id.txtSchoolCode);
        btnNext = (Button)this.findViewById(R.id.btnNext);
    }

    private void initAction()
    {
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(checkValidForm() == true && checkValidEmail() == true)
                    initApicall();

            }
        });
    }


    private boolean checkValidEmail()
    {
        if(isValidEmail(txtEmail.getText().toString()))
        {
            return true;
        }

        else
        {
            uiHelper.showErrorDialog("Invalid e-mail address");
            return false;
        }

    }


    private boolean checkValidForm()
    {
        boolean isValid = true;

        if(txtFirstName.getText().toString().matches(""))
        {
            uiHelper.showErrorDialog("First name cannot be empty!");
            isValid = false;
        }

        else if(txtLastName.getText().toString().matches(""))
        {
            uiHelper.showErrorDialog("Last name cannot be empty!!");
            isValid = false;
        }

        else if(txtEmail.getText().toString().matches(""))
        {
            uiHelper.showErrorDialog("E-mail cannot be empty!");
            isValid = false;
        }

        else if(txtPassword.getText().toString().matches(""))
        {
            uiHelper.showErrorDialog("Password cannot be empty!");
            isValid = false;
        }

        else if(txtPassword.getText().toString().length() < 6)
        {
            uiHelper.showErrorDialog("Password should be minimum of 6 characters!");
            isValid = false;
        }

        else if(txtRetypePassword.getText().toString().matches(""))
        {
            uiHelper.showErrorDialog("Retype password carefully!");
            isValid = false;
        }

        else if(!txtPassword.getText().toString().equals(txtRetypePassword.getText().toString()))
        {
            uiHelper.showErrorDialog("Password didn't match, retype password carefully!");
            isValid = false;
        }


        else if(txtSchoolCode.getText().toString().matches(""))
        {
            uiHelper.showErrorDialog("School code cannot be empty!");
            isValid = false;
        }


        return isValid;
    }


    private final static boolean isValidEmail(CharSequence target) {
        if (target == null)
            return false;

        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    private void initApicall()
    {
        RequestParams params = new RequestParams();

        params.put("first_name", txtFirstName.getText().toString());
        params.put("last_name", txtLastName.getText().toString());
        params.put("email", txtEmail.getText().toString());
        params.put("password", txtPassword.getText().toString());
        params.put("school_code", txtSchoolCode.getText().toString());



        AppRestClient.post(URLHelper.URL_PAID_USERCHECK, params, checkUserHandler);
    }

    AsyncHttpResponseHandler checkUserHandler = new AsyncHttpResponseHandler() {

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

                String sId = modelContainer.getData().get("school_id").getAsString();
                String schoolId;



                int admissionCode = Integer.parseInt(sId);
                if(admissionCode <= 9)
                {
                    schoolId = "0"+sId;
                }
                else
                {
                    schoolId = sId;
                }


                if(ordinal == 2) //type student
                {
                    Intent intent = new Intent(RegistrationFirstPhaseActivity.this, CreateStudentActivity.class);
                    intent.putExtra(AppConstant.USER_TYPE_CLASSTUNE, ordinal);
                    intent.putExtra(AppConstant.SCHOOL_ID_CLASSTUNE, schoolId);

                    intent.putExtra(AppConstant.STUDENT_FIRST_NAME_CLASSTUNE, txtFirstName.getText().toString());
                    intent.putExtra(AppConstant.STUDENT_LAST_NAME_CLASSTUNE, txtLastName.getText().toString());
                    intent.putExtra(AppConstant.STUDENT_EMAIL_CLASSTUNE, txtEmail.getText().toString());
                    intent.putExtra(AppConstant.STUDENT_PASSWORD_CLASSTUNE, txtPassword.getText().toString());
                    intent.putExtra(AppConstant.STUDENT_SCHOOL_CODE_CLASSTUNE, txtSchoolCode.getText().toString());



                    startActivity(intent);
                }

                else if(ordinal == 4) //type parent
                {
                    Intent intent = new Intent(RegistrationFirstPhaseActivity.this, CreateParentActivity.class);
                    intent.putExtra(AppConstant.USER_TYPE_CLASSTUNE, ordinal);
                    intent.putExtra(AppConstant.SCHOOL_ID_CLASSTUNE, schoolId);

                    intent.putExtra(AppConstant.STUDENT_FIRST_NAME_CLASSTUNE, txtFirstName.getText().toString());
                    intent.putExtra(AppConstant.STUDENT_LAST_NAME_CLASSTUNE, txtLastName.getText().toString());
                    intent.putExtra(AppConstant.STUDENT_EMAIL_CLASSTUNE, txtEmail.getText().toString());
                    intent.putExtra(AppConstant.STUDENT_PASSWORD_CLASSTUNE, txtPassword.getText().toString());
                    intent.putExtra(AppConstant.STUDENT_SCHOOL_CODE_CLASSTUNE, txtSchoolCode.getText().toString());

                    startActivity(intent);
                }
                else if(ordinal == 3) //type teacher
                {
                    Intent intent = new Intent(RegistrationFirstPhaseActivity.this, CreateTeacherActivity.class);
                    intent.putExtra(AppConstant.USER_TYPE_CLASSTUNE, ordinal);
                    intent.putExtra(AppConstant.SCHOOL_ID_CLASSTUNE, schoolId);

                    intent.putExtra(AppConstant.STUDENT_FIRST_NAME_CLASSTUNE, txtFirstName.getText().toString());
                    intent.putExtra(AppConstant.STUDENT_LAST_NAME_CLASSTUNE, txtLastName.getText().toString());
                    intent.putExtra(AppConstant.STUDENT_EMAIL_CLASSTUNE, txtEmail.getText().toString());
                    intent.putExtra(AppConstant.STUDENT_PASSWORD_CLASSTUNE, txtPassword.getText().toString());
                    intent.putExtra(AppConstant.STUDENT_SCHOOL_CODE_CLASSTUNE, txtSchoolCode.getText().toString());

                    startActivity(intent);
                }



                Log.e("CODE 200", "code 200");
            }

            else if (modelContainer.getStatus().getCode() == 401) {

                Log.e("CODE 401", "code 401");
                uiHelper.showErrorDialog("School code is not valid!");
            }

            else if (modelContainer.getStatus().getCode() == 400) {

                Log.e("CODE 400", "code 400");
                uiHelper.showErrorDialog("Something went wrong please try again.");
            }


            else {

            }



        };
    };
}
