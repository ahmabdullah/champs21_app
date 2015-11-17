package com.champs21.schoolapp.classtune;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.champs21.schoolapp.R;
import com.champs21.schoolapp.utils.AppConstant;
import com.champs21.schoolapp.utils.SchoolApp;

/**
 * Created by BLACK HAT on 08-Nov-15.
 */
public class UserSelectionActivity extends Activity implements View.OnClickListener{

    private int ordinal = -1;

    private ImageButton btnStudentSelect, btnParentSelect, btnTeacherSelect;
    private TextView txtMeHeader;
    private TextView txtMember;
    private TextView txtStudentHeader;
    private TextView txtParentHeader;
    private TextView txtTeacherHeader;
    private Button btnSignIn;
    private ImageView imgViewAbout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_userselection_layout2);

        initView();
        initAction();
    }

    private void initView()
    {
        btnStudentSelect = (ImageButton)this.findViewById(R.id.btnStudentSelect);
        btnParentSelect = (ImageButton)this.findViewById(R.id.btnParentSelect);
        btnTeacherSelect = (ImageButton)this.findViewById(R.id.btnTeacherSelect);
        txtMeHeader = (TextView)this.findViewById(R.id.txtMeHeader);
        txtMember = (TextView)this.findViewById(R.id.txtMember);
        txtStudentHeader = (TextView)this.findViewById(R.id.txtStudentHeader);
        txtParentHeader = (TextView)this.findViewById(R.id.txtParentHeader);
        txtTeacherHeader = (TextView)this.findViewById(R.id.txtTeacherHeader);
        btnSignIn = (Button)this.findViewById(R.id.btnSignIn);
        imgViewAbout = (ImageView)this.findViewById(R.id.imgViewAbout);
    }

    private void initAction()
    {
        btnStudentSelect.setOnClickListener(this);
        btnParentSelect.setOnClickListener(this);
        btnTeacherSelect.setOnClickListener(this);


        txtMeHeader.setTypeface(SchoolApp.getInstance().getClassTuneFontRes(AppConstant.CLASSTUNE_FONT_NAME));
        txtMeHeader.setText("I'm");

        txtMember.setTypeface(SchoolApp.getInstance().getClassTuneFontRes(AppConstant.CLASSTUNE_FONT_NAME));

        txtStudentHeader.setTypeface(SchoolApp.getInstance().getClassTuneFontRes(AppConstant.CLASSTUNE_FONT_NAME));
        txtParentHeader.setTypeface(SchoolApp.getInstance().getClassTuneFontRes(AppConstant.CLASSTUNE_FONT_NAME));
        txtTeacherHeader.setTypeface(SchoolApp.getInstance().getClassTuneFontRes(AppConstant.CLASSTUNE_FONT_NAME));

        btnSignIn.setTypeface(SchoolApp.getInstance().getClassTuneFontRes(AppConstant.CLASSTUNE_FONT_NAME));



    }

    @Override
    public void onClick(View v)
    {
        Intent intent = new Intent(UserSelectionActivity.this, RegistrationFirstPhaseActivity.class);


        switch (v.getId())
        {
            case R.id.btnStudentSelect:
                intent.putExtra(AppConstant.USER_TYPE_CLASSTUNE, 2);

                break;

            case R.id.btnParentSelect:
                intent.putExtra(AppConstant.USER_TYPE_CLASSTUNE, 4);

                break;

            case R.id.btnTeacherSelect:
                intent.putExtra(AppConstant.USER_TYPE_CLASSTUNE, 3);

                break;

            default:
                break;
        }

        startActivity(intent);

    }
}
