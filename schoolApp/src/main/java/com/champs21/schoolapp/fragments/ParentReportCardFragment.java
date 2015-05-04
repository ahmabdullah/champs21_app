/**
 * 
 */
package com.champs21.schoolapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TabHost.TabContentFactory;
import android.widget.TextView;

import com.champs21.freeversion.PaidVersionHomeFragment;
import com.champs21.schoolapp.R;
import com.champs21.schoolapp.model.BaseType;
import com.champs21.schoolapp.model.Batch;
import com.champs21.schoolapp.model.Picker;
import com.champs21.schoolapp.model.Picker.PickerItemSelectedListener;
import com.champs21.schoolapp.model.PickerType;
import com.champs21.schoolapp.model.StudentAttendance;
import com.champs21.schoolapp.utils.AppConstant;
import com.champs21.schoolapp.utils.RequestKeyHelper;
import com.champs21.schoolapp.utils.URLHelper;
import com.champs21.schoolapp.utils.UserHelper;
import com.champs21.schoolapp.viewhelpers.MyFragmentTabHost;
import com.champs21.schoolapp.viewhelpers.UIHelper;
import com.loopj.android.http.RequestParams;

import java.util.HashMap;

public class ParentReportCardFragment extends UserVisibleHintFragment implements MyFragmentTabHost.OnTabChangeListener{


	
	private static final String TAG = "Parent Report Card";
	private Bundle savedInstanceState=null;
	
	private Batch selectedBatch;
	private StudentAttendance selectedStudent;
    private UserHelper userHelper;

    public void clearAllTabsData(){
		/*FragmentTransaction ft = this.getChildFragmentManager().beginTransaction();
		ft.hide(mLastTab.fragment);
		ft.detach(mLastTab.fragment);
		ft.remove(mLastTab.fragment);*/
		//mTabHostAC.getTabWidget().removeAllViews();
		mTabHostReportCard.clearAllTabs();
		mapTabInfo.clear();
		mLastTab=null;
	}
	
	
	
	
	/*@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		
		if(isVisibleToUser)
		{
			Log.e(TAG, "dekhaise");
			clearAllTabsData();
			initialiseTabHost(this.savedInstanceState);
			
		}
			
			
	}*/
	
	
	private void initEmptyTab(Bundle args) {
		
        TabInfo tabInfo = null;
        MyFragmentTabHost.TabSpec spec   =   mTabHostReportCard.newTabSpec(AppConstant.TAB_EMPTY);
        spec.setIndicator(getIndicatorView(getString(R.string.title_classtest_tab), R.drawable.tab_classtest));
        addTab(this.mTabHostReportCard, spec, ( tabInfo = new TabInfo(AppConstant.TAB_EMPTY, EmptyFragment.class, args)));
        this.mapTabInfo.put(tabInfo.tag, tabInfo);
     // Default to first tab
        this.onTabChanged(AppConstant.TAB_EMPTY);
        //
        mTabHostReportCard.setOnTabChangedListener(this);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		uiHelper = new UIHelper(getActivity());
        userHelper = new UserHelper(getActivity());
    }
	
	public void showPicker(PickerType type) {

		Picker picker = Picker.newInstance(0);
		picker.setData(type, PaidVersionHomeFragment.batches, PickerCallback , "Select Batch");
		picker.show(getChildFragmentManager(), null);
	}
	
	public void showStudentPicker(PickerType type) {

		CustomPickerWithLoadData picker = CustomPickerWithLoadData.newInstance(0);
		RequestParams params = new RequestParams();
		params.put(RequestKeyHelper.BATCH_ID,selectedBatch.getId());
		params.put(RequestKeyHelper.USER_SECRET, UserHelper.getUserSecret());
		picker.setData(PickerType.TEACHER_STUDENT,params,URLHelper.URL_GET_STUDENTS_ATTENDANCE, PickerCallback , "Select Student");
		picker.show(getChildFragmentManager(), null);
	}
	PickerItemSelectedListener PickerCallback = new PickerItemSelectedListener() {

		@Override
		public void onPickerItemSelected(BaseType item) {

			switch (item.getType()) {
			case TEACHER_BATCH:
				selectedBatch=(Batch)item;
				showStudentPicker(PickerType.TEACHER_STUDENT);
				break;
			case TEACHER_STUDENT:
				selectedStudent = (StudentAttendance)item;
				Intent i = new Intent("com.champs21.schoolapp.batch");
                i.putExtra("batch_id", selectedBatch.getId());
                i.putExtra("student_id", selectedStudent.getId());
                getActivity().sendBroadcast(i);
				break;
			default:
				break;
			}

		}
	};
	
	private MyFragmentTabHost mTabHostReportCard;
	private HashMap<String, TabInfo> mapTabInfo = new HashMap<String, ParentReportCardFragment.TabInfo>();
	private TabInfo mLastTab = null;
	
	private View rootView;
	/**
	 * 
	 */
	private class TabInfo {
		 private String tag;
         private Class<?> clss;
         private Bundle args;
         private Fragment fragment;
         TabInfo(String tag, Class<?> clazz, Bundle args) {
        	 this.tag = tag;
        	 this.clss = clazz;
        	 this.args = args;
         }
         
	}
	/**
	 * 
	 *
	 */
	class TabFactory implements TabContentFactory {

		private final Context mContext;

	    /**
	     * @param context
	     */
	    public TabFactory(Context context) {
	        mContext = context;
	    }

	    /** (non-Javadoc)
	     * @see android.widget.TabHost.TabContentFactory#createTabContent(java.lang.String)
	     */
	    public View createTabContent(String tag) {
	        View v = new View(mContext);
	        v.setMinimumWidth(0);
	        v.setMinimumHeight(0);
	        return v;
	    }

	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		rootView = inflater.inflate(R.layout.parent_report_card_layout,container, false);
		mTabHostReportCard = (MyFragmentTabHost)rootView.findViewById(R.id.tabhost_reportcard);
		//CustomButton headerParent = (CustomButton) rootView.findViewById(R.id.header_parent);
		//headerParent.setImage(R.drawable.reportcard_rombus);
		//headerParent.setTitleText("Report Card");
		this.savedInstanceState=savedInstanceState;
		mTabHostReportCard.setup(getActivity(), getChildFragmentManager(), R.id.realtabcontent_report_card);
		initEmptyTab(this.savedInstanceState);
		return rootView;
	}
	
	
	/** (non-Javadoc)
     * @see android.support.v4.app.FragmentActivity#onSaveInstanceState(android.os.Bundle)
     */
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("tab", mTabHostReportCard.getCurrentTabTag()); //save the tab selected
        super.onSaveInstanceState(outState);
    }
	/**
	 * Initialise the Tab Host
	 */
	private void initialiseTabHost(Bundle args) {
		mTabHostReportCard.setup(getActivity(), getChildFragmentManager(), R.id.realtabcontent_report_card);
        TabInfo tabInfo = null;
        MyFragmentTabHost.TabSpec spec   =   mTabHostReportCard.newTabSpec(AppConstant.TAB_CLASSTEST);
        spec.setIndicator(getIndicatorView(getString(R.string.title_classtest_tab), R.drawable.tab_classtest));
        addTab(this.mTabHostReportCard, spec, ( tabInfo = new TabInfo(AppConstant.TAB_CLASSTEST, ReportClassTestFragment.class, args)));
        
       /* spec   =   mTabHostReportCard.newTabSpec(AppConstant.TAB_PROJECT);
        spec.setIndicator(getIndicatorView(getString(R.string.tab_project), R.drawable.tab_result_project));
        addTab(this.mTabHostReportCard, spec, ( tabInfo = new TabInfo(AppConstant.TAB_PROJECT, ResultProjectFragment.class, args)));*/
        
        spec   =   mTabHostReportCard.newTabSpec(AppConstant.TAB_TERM_REPORT);
        spec.setIndicator(getIndicatorView(getString(R.string.title_term_report_tab), R.drawable.tab_term_report));
        addTab(this.mTabHostReportCard, spec, ( tabInfo = new TabInfo(AppConstant.TAB_TERM_REPORT, ResultTermTesttFragment.class, args)));

        if(userHelper.getUser().getType()!= UserHelper.UserTypeEnum.TEACHER){
            spec   =   mTabHostReportCard.newTabSpec(AppConstant.TAB_PROGRESS_GRAPH);
            spec.setIndicator(getIndicatorView(getString(R.string.title_progress_graph_tab), R.drawable.tab_progress_graph));
            addTab(this.mTabHostReportCard, spec, ( tabInfo = new TabInfo(AppConstant.TAB_PROGRESS_GRAPH, ProgressGraphFragment.class, args)));
        }


        
        // Default to first tab
        this.onTabChanged(AppConstant.TAB_CLASSTEST);
        //
        mTabHostReportCard.setOnTabChangedListener(this);
        
	}
	
	private View getIndicatorView(String text,int drawableId)
	{
		View tabIndicator = LayoutInflater.from(getActivity()).inflate(R.layout.tab_indicator, this.mTabHostReportCard.getTabWidget(), false);
		TextView title = (TextView) tabIndicator.findViewById(R.id.title);
		title.setText(text);
		ImageView icon = (ImageView) tabIndicator.findViewById(R.id.icon);
		icon.setImageResource(drawableId);
		return tabIndicator;
	}
	

	private void addTab(MyFragmentTabHost tabHost, MyFragmentTabHost.TabSpec tabSpec, TabInfo tabInfo) {
		// Attach a Tab view factory to the spec
		tabSpec.setContent(new TabFactory(getActivity()));
        String tag = tabSpec.getTag();

        // Check to see if we already have a fragment for this tab, probably
        // from a previously saved state.  If so, deactivate it, because our
        // initial state is that a tab isn't shown.
        
        tabInfo.fragment = getChildFragmentManager().findFragmentByTag(tag);
        if (tabInfo.fragment != null && !tabInfo.fragment.isDetached()) {
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            ft.detach(tabInfo.fragment);
            ft.commit();
            getChildFragmentManager().executePendingTransactions();
        }

        tabHost.addTab(tabSpec,tabInfo.clss,null);
	}
	

	/** (non-Javadoc)
	 * @see android.widget.TabHost.OnTabChangeListener#onTabChanged(java.lang.String)
	 */
	public void onTabChanged(String tag) {
		TabInfo newTab = this.mapTabInfo.get(tag);
		if (mLastTab != newTab) {
			FragmentTransaction ft = this.getChildFragmentManager().beginTransaction();
            if (mLastTab != null) {
                if (mLastTab.fragment != null) {
                	ft.detach(mLastTab.fragment);
                }
            }
            if (newTab != null) {
                if (newTab.fragment == null) {
                    newTab.fragment = Fragment.instantiate(getActivity(),
                            newTab.clss.getName(), newTab.args);
                    ft.add(R.id.realtabcontent_report_card, newTab.fragment, newTab.tag);
                } else {
                    ft.attach(newTab.fragment);
                }
            }

            mLastTab = newTab;
            ft.commit();
            this.getChildFragmentManager().executePendingTransactions();
		}
    }




	@Override
	
	protected void onVisible() {
		// TODO Auto-generated method stub
		Log.e(TAG, "dekhaise");
		clearAllTabsData();
		initialiseTabHost(ParentReportCardFragment.this.savedInstanceState);
		/*if(PaidVersionHomeFragment.isBatchLoaded){
			showPicker(PickerType.TEACHER_BATCH);
		}*/
		
	}
	
	private UIHelper uiHelper;
	


	@Override
	protected void onInvisible() {
		// TODO Auto-generated method stub
		
	}



}
