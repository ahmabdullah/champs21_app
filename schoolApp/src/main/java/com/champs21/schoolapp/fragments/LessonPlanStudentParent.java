package com.champs21.schoolapp.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.champs21.schoolapp.R;
import com.champs21.schoolapp.model.LessonPlanStudentParentSubject;
import com.champs21.schoolapp.utils.UserHelper;
import com.champs21.schoolapp.viewhelpers.UIHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by BLACK HAT on 08-Apr-15.
 */
public class LessonPlanStudentParent extends Fragment{

    private View view;
    private UIHelper uiHelper;
    private UserHelper userHelper;


    private ListView listViewLessonPlanStudentParent;

    private List<LessonPlanStudentParentSubject> listSubject;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        listSubject = new ArrayList<LessonPlanStudentParentSubject>();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_lessonplan_student_parent, container, false);

        initview(view);

        return view;
    }


    private void initview(View view)
    {
        listViewLessonPlanStudentParent = (ListView)view.findViewById(R.id.listViewLessonPlanStudentParent);
    }


    private class LessonPlanSubjectAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return listSubject.size();
        }

        @Override
        public Object getItem(int position) {
            return listSubject.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder;

            if (convertView == null) {
                holder = new ViewHolder();

                convertView = LayoutInflater.from(LessonPlanStudentParent.this.getActivity()).inflate(R.layout.row_lessonplan_studentparent_subject, parent, false);

                holder.imgViewSubjectIcon = (ImageView)convertView.findViewById(R.id.imgViewSubjectIcon);
                holder.txtSubjectName = (TextView)convertView.findViewById(R.id.txtSubjectName);
                holder.txtPublishDate = (TextView)convertView.findViewById(R.id.txtPublishDate);
                holder.btnView = (ImageButton)convertView.findViewById(R.id.btnView);


                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }

            //holder.imgViewSubjectIcon.setImageResource(AppUtility.getImageResourceId(listSubject.get(position).getName(), getActivity()));




            return null;
        }


        class ViewHolder {

            ImageView imgViewSubjectIcon;
            TextView txtSubjectName;
            TextView txtPublishDate;
            ImageButton btnView;


        }


    }
}
