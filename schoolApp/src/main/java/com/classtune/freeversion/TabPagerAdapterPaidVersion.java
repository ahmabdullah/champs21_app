package com.classtune.freeversion;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.classtune.schoolapp.fragments.ApplyForLeaveFragment;
import com.classtune.schoolapp.fragments.FeesTabhostFragment;
import com.classtune.schoolapp.fragments.HomeworkFragment;
import com.classtune.schoolapp.fragments.MeetingFragment;
import com.classtune.schoolapp.fragments.NoticeFragment;
import com.classtune.schoolapp.fragments.ParentAcademicCalender;
import com.classtune.schoolapp.fragments.ParentAttendenceFragment;
import com.classtune.schoolapp.fragments.ParentEventFragment;
import com.classtune.schoolapp.fragments.ParentReportCardFragment;
import com.classtune.schoolapp.fragments.RoutineFragment;
import com.classtune.schoolapp.fragments.StudentListFragment;
import com.classtune.schoolapp.fragments.SuperAwesomeCardFragment;
import com.classtune.schoolapp.fragments.SyllabusFragment;
import com.classtune.schoolapp.fragments.TeacherHomeWorkFragment;
import com.classtune.schoolapp.fragments.TeacherRoutineFragment;
import com.classtune.schoolapp.fragments.TeachersAttendanceTabhostFragment;
import com.classtune.schoolapp.fragments.TransportFragment;

import com.classtune.schoolapp.utils.UserHelper.UserTypeEnum;
import com.classtune.schoolapp.viewhelpers.PagerSlidingTabStrip.IconTabProvider;

public class TabPagerAdapterPaidVersion extends FragmentPagerAdapter implements
		IconTabProvider {

	private int[] ICONS;
	private int[] ICONS_TAP;
	private UserTypeEnum type;
	

	public TabPagerAdapterPaidVersion(FragmentManager fm, int[] arrayOfIcons,
			int[] arrayOfIconsTap,UserTypeEnum type) {
		super(fm);
		// TODO Auto-generated constructor stub
		this.ICONS = arrayOfIcons;
		this.ICONS_TAP = arrayOfIconsTap;
		this.type=type;
	}

	@Override
	public Fragment getItem(int position) {
		// TODO Auto-generated method stub
		
		switch (type) {
		case STUDENT:
			switch (position) {
			case 0:
				return new HomeworkFragment();
			case 1:
				return new RoutineFragment();
			case 2:
				return new ParentAttendenceFragment();
			case 3:
				return new ParentAcademicCalender();
			case 4:
				return new SyllabusFragment();
			case 5:
				return new ParentReportCardFragment();
			case 6:
				return new ParentEventFragment();
			case 7:
				return new NoticeFragment();
			case 8:
				return new TransportFragment();
			default:
				return SuperAwesomeCardFragment.newInstance(position);

			}
		case PARENTS:
			switch (position) {
			case 0:
				return new HomeworkFragment();
			case 1:
				return new FeesTabhostFragment();
			case 2:
				return new NoticeFragment();
			case 3:
				return new ParentAttendenceFragment();
			case 4:
				return new RoutineFragment();
			case 5:
				return new SyllabusFragment();
			case 6:
				return new ParentReportCardFragment();
			case 7:
				return SuperAwesomeCardFragment.newInstance(position);
			case 8:
				return new MeetingFragment();
			case 9:
				return SuperAwesomeCardFragment.newInstance(position);
			case 10:
				return new ParentEventFragment();
			case 11:
				return new TransportFragment();
			default:
				return SuperAwesomeCardFragment.newInstance(position);

			}
			
		
		case TEACHER:
			switch (position) {
			case 0:
				return new TeachersAttendanceTabhostFragment();
			case 1:
				return new TeacherHomeWorkFragment();
			case 2:
				return new TeacherRoutineFragment();
			case 3:
				return new NoticeFragment();
			case 4:
				return new SyllabusFragment();
			case 5:
				return new StudentListFragment();
			case 6:
				return new ApplyForLeaveFragment();
			case 7:
				return new MeetingFragment();
			case 8:
				return SuperAwesomeCardFragment.newInstance(position);
			case 9:
				return new TransportFragment();
			case 10:
				return new ParentReportCardFragment();
			case 11:
				return new ParentAcademicCalender();
			
			default:
				return SuperAwesomeCardFragment.newInstance(position);

			}
		default:
			return SuperAwesomeCardFragment.newInstance(position);
		}
		
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return ICONS.length;
	}

	@Override
	public int getPageIconResId(int position) {
		// TODO Auto-generated method stub
		return ICONS[position];
	}

	@Override
	public int getPageIconResIdTap(int position) {
		// TODO Auto-generated method stub
		return ICONS_TAP[position];
	}

}
