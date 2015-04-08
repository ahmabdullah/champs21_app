package com.champs21.freeversion;

import android.os.Bundle;
import android.view.View;

import com.champs21.schoolapp.R;

/**
 * Created by BLACK HAT on 08-Apr-15.
 */
public class LessonPlanSubjectDetailsActivity extends ChildContainerActivity{


    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        homeBtn.setVisibility(View.VISIBLE);
        logo.setVisibility(View.GONE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_lessonplan_subject_details);


    }
}
