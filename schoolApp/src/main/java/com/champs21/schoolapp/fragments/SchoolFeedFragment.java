package com.champs21.schoolapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.champs21.freeversion.CandleActivity;
import com.champs21.freeversion.ChildContainerActivity;
import com.champs21.freeversion.GoodReadActivity;
import com.champs21.freeversion.HomePageFreeVersion;
import com.champs21.freeversion.PaidVersionHomeFragment;
import com.champs21.freeversion.SchoolScrollableDetailsActivity;
import com.champs21.freeversion.SchoolSingleItemShowActivity;
import com.champs21.schoolapp.R;
import com.champs21.schoolapp.model.FreeVersionPost;
import com.champs21.schoolapp.model.FreeVersionPost.DateFeed;
import com.champs21.schoolapp.model.UserAuthListener;
import com.champs21.schoolapp.model.Wrapper;
import com.champs21.schoolapp.networking.AppRestClient;
import com.champs21.schoolapp.utils.AppConstant;
import com.champs21.schoolapp.utils.AppUtility;
import com.champs21.schoolapp.utils.GsonParser;
import com.champs21.schoolapp.utils.RequestKeyHelper;
import com.champs21.schoolapp.utils.SchoolApp;
import com.champs21.schoolapp.utils.URLHelper;
import com.champs21.schoolapp.utils.UserHelper;
import com.champs21.schoolapp.utils.UserHelper.UserAccessType;
import com.champs21.schoolapp.utils.UserHelper.UserTypeEnum;
import com.champs21.schoolapp.viewhelpers.CustomButton;
import com.champs21.schoolapp.viewhelpers.PagerContainer;
import com.champs21.schoolapp.viewhelpers.PopupDialog;
import com.champs21.schoolapp.viewhelpers.ResizableImageView;
import com.champs21.schoolapp.viewhelpers.UIHelper;
import com.champs21.schoolapp.viewhelpers.UninterceptableViewPager;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListViewCustom;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class SchoolFeedFragment extends Fragment implements UserAuthListener {

    UIHelper uiHelper;
    UserHelper userHelper;
    private PullToRefreshListViewCustom listGoodread;
    private GoodReadAdapter adapter;
    private ArrayList<FreeVersionPost> allGooadReadPost = new ArrayList<FreeVersionPost>();
    private ProgressBar spinner;

    boolean hasNext = false;
    private int pageNumber = 1;
    private int pageSize = 10;
    private boolean isRefreshing = false;
    private boolean loading = false;
    private boolean stopLoadingData = false;
    private int[] lArray = {R.id.l1, R.id.l2, R.id.l3, R.id.l4, R.id.l5,
            R.id.l6};
    private int[] dArray = {R.id.d1, R.id.d2, R.id.d3, R.id.d4, R.id.d5,
            R.id.d6};
    private int[] mArray = {R.id.m1, R.id.m2, R.id.m3, R.id.m4, R.id.m5,
            R.id.m6};
    private LinearLayout mQuickReturnTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiHelper = new UIHelper(getActivity());
        adapter = new GoodReadAdapter(getActivity(), allGooadReadPost);
        userHelper = new UserHelper(this, getActivity());
        allGooadReadPost.clear();
        //Log.e("SIZE OF ALLGOODREADPOST:", "" + allGooadReadPost.size());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_school_feed, container,
                false);
        //some code goes here
        spinner = (ProgressBar) view.findViewById(R.id.loading);

        // adapter.notifyDataSetChanged();

        mQuickReturnTextView = (LinearLayout) view
                .findViewById(R.id.quick_return_tv);
        setupPoppyView(mQuickReturnTextView);
        listGoodread = (PullToRefreshListViewCustom) view
                .findViewById(R.id.listView_category);
        int footerHeight = getActivity().getResources().getDimensionPixelSize(
                R.dimen.footer_height);
        //some code hello there
        listGoodread.setQuickReturnListViewOnScrollListener(
                PullToRefreshListViewCustom.QuickReturnType.FOOTER, null, 0,
                mQuickReturnTextView, footerHeight);
        // listViewFitness.getRefreshableView().addHeaderView(mQuickReturnTextView);
        listGoodread.setCanSlideInIdleScrollState(true);
        listGoodread.setAdapter(adapter);
        setUpList();
        loadDataInToList();
        return view;
    }

    private void loadDataInToList() {
        if (AppUtility.isInternetConnected()) {
            processFetchPost(URLHelper.URL_FREE_VERSION_CATEGORY, "");
        } else
            uiHelper.showMessage(getActivity().getString(
                    R.string.internet_error_text));
    }

    private void setUpList() {
        initializePageing();
        listGoodread.setMode(Mode.PULL_FROM_END);
        // Set a listener to be invoked when the list should be refreshed.
        listGoodread.setOnRefreshListener(new OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(getActivity(),
                        System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
                                | DateUtils.FORMAT_SHOW_DATE
                                | DateUtils.FORMAT_ABBREV_ALL);

                // Update the LastUpdatedLabel
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

                Mode m = listGoodread.getCurrentMode();
                if (m == Mode.PULL_FROM_START) {
                    stopLoadingData = false;
                    isRefreshing = true;
                    pageNumber = 1;
                    loading = true;
                    processFetchPost(URLHelper.URL_FREE_VERSION_CATEGORY, "");
                } else if (!stopLoadingData) {
                    pageNumber++;
                    loading = true;
                    processFetchPost(URLHelper.URL_FREE_VERSION_CATEGORY, "");
                } else {
                    new NoDataTask().execute();
                }
            }
        });

        adapter = new GoodReadAdapter(getActivity(), allGooadReadPost);

        listGoodread.setAdapter(adapter);
    }

    private void processFetchPost(String url, String categoryIndex) {

        RequestParams params = new RequestParams();

        params.put(RequestKeyHelper.PAGE_NUMBER, pageNumber + "");
        params.put(RequestKeyHelper.PAGE_SIZE, pageSize + "");
        params.put(RequestKeyHelper.SCHOOL_ID, getArguments().getInt("school_id") + "");
        // getArguments().getInt("school_id") + "");
        Log.e("SCHOOL_ID_FEED", getArguments().getInt("school_id") + "");
        params.put(RequestKeyHelper.TARGET, "school");
        if (UserHelper.isLoggedIn()) {
            if (userHelper.getUser().getAccessType() == UserAccessType.PAID) {
                params.put(RequestKeyHelper.USER_SECRET,
                        UserHelper.getUserSecret());
                if (userHelper.getUser().getType() == UserTypeEnum.PARENTS) {
                    params.put("batch_id", userHelper.getUser()
                            .getSelectedChild().getBatchId());
                    params.put("student_id", userHelper.getUser()
                            .getSelectedChild().getProfileId());
                }
                AppRestClient.post(URLHelper.URL_PAID_VERSION_SCHOOL_FEED,
                        params, fitnessHandler);

            } else {
                AppRestClient.post(URLHelper.URL_FREE_VERSION_SCHOOL_FEED,
                        params, fitnessHandler);
            }

        }

        // Log.e("Params", params.toString());
        // if(getArguments().getInt("school_id")==-5)return;

    }

    AsyncHttpResponseHandler fitnessHandler = new AsyncHttpResponseHandler() {

        @Override
        public void onFailure(Throwable arg0, String arg1) {
            uiHelper.showMessage(arg1);
            if (uiHelper.isDialogActive()) {
                uiHelper.dismissLoadingDialog();
            }
        }

        ;

        @Override
        public void onStart() {
            if (pageNumber == 0 && !isRefreshing) {
                if (!uiHelper.isDialogActive())
                    uiHelper.showLoadingDialog(getString(R.string.loading_text));
                else
                    uiHelper.updateLoadingDialog(getString(R.string.loading_text));
            }
            if (pageNumber == 1) {
                spinner.setVisibility(View.VISIBLE);
            }
        }

        ;

        @Override
        public void onSuccess(int arg0, String responseString) {

            if (uiHelper.isDialogActive()) {
                uiHelper.dismissLoadingDialog();
            }
            /*
			 * if (fitnessAdapter.getPageNumber() == 1) {
			 * fitnessAdapter.getList().clear(); // setupPoppyView(); }
			 */
            Log.e("Response CATEGORY", responseString);
            // app.showLog("Response", responseString);
            Wrapper modelContainer = GsonParser.getInstance()
                    .parseServerResponse(responseString);

            if (modelContainer.getStatus().getCode() == 200) {
                hasNext = modelContainer.getData().get("has_next")
                        .getAsBoolean();

                if (pageNumber == 1)
                    adapter.clearList();
                spinner.setVisibility(View.GONE);
                if (!hasNext) {
                    // fitnessAdapter.setStopLoadingData(true);
                    stopLoadingData = true;
                }
                // fitnessAdapter.getList().addAll();
                ArrayList<FreeVersionPost> allpost = GsonParser.getInstance()
                        .parseFreeVersionPost(
                                modelContainer.getData().getAsJsonArray("post")
                                        .toString());

                // if (pageNumber == 1)
                for (int i = 0; i < allpost.size(); i++) {
                    if (allpost.get(i).getPostType().equals("20")) {
                        adapter.addSeparatorItem(allpost.get(i));
                    } else
                        adapter.addItem(allpost.get(i));
                }
                adapter.notifyDataSetChanged();

                if (pageNumber != 0 || isRefreshing) {
                    listGoodread.onRefreshComplete();
                    loading = false;
                }
            }
        }
    };

    private void initializePageing() {
        pageNumber = 1;
        isRefreshing = false;
        loading = false;
        stopLoadingData = false;
    }

    public static SchoolFeedFragment newInstance(int schoolId) {
        SchoolFeedFragment f = new SchoolFeedFragment();
        Bundle args = new Bundle();
        args.putInt("school_id", schoolId);
        f.setArguments(args);
        return f;
    }

    private class NoDataTask extends AsyncTask<Void, Void, String[]> {

        @Override
        protected String[] doInBackground(Void... params) {
            // Simulates a background job.
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {

            adapter.notifyDataSetChanged();

            // Call onRefreshComplete when the list has been refreshed.
            listGoodread.onRefreshComplete();

            super.onPostExecute(result);
        }
    }

    public void showCustomDialog(String headerText, int imgResId,
                                 String descriptionText) {

        PopupDialog picker = PopupDialog.newInstance(0);
        picker.setData(headerText, descriptionText, imgResId, getActivity());
        picker.show(getChildFragmentManager(), null);
    }

    public class GoodReadAdapter extends BaseAdapter {

        private Context context;
        private ArrayList<FreeVersionPost> list;
        private static final int TYPE_ITEM = 0;
        private static final int TYPE_SUMMERY = 1;

        private static final int TYPE_MAX_COUNT = 2;
        private TreeSet<Integer> mSeparatorSet = new TreeSet<Integer>();
        private TreeSet<Integer> mSingleSeparatorSet = new TreeSet<Integer>();
        private LayoutInflater mInflater;

        public GoodReadAdapter(Context context, ArrayList<FreeVersionPost> list) {
            // Cache the LayoutInflate to avoid asking for a new one each time.
            mInflater = LayoutInflater.from(context);
            this.list = new ArrayList<FreeVersionPost>();
        }

        public void clearList() {
            this.list.clear();
            this.mSeparatorSet.clear();
            this.mSingleSeparatorSet.clear();
        }

        public void addItem(final FreeVersionPost item) {
            list.add(item);
            // The notification is not necessary since the items are not added
            // dynamically
            // notifyDataSetChanged();
        }

        public void addSeparatorItem(final FreeVersionPost item) {
            list.add(item);
            // Save separator position
            // This is used to check whether the element is a separator or an
            // item
            mSeparatorSet.add(list.size() - 1);
            // The notification is not necessary since the separators are not
            // added dynamically
            // notifyDataSetChanged();
        }

        public GoodReadAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            if (mSeparatorSet.contains(position))
                return TYPE_SUMMERY;

            return TYPE_ITEM;

        }

        @Override
        public int getViewTypeCount() {
            return TYPE_MAX_COUNT;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final int i = position;

            ViewHolder holder = null;
            int type = getItemViewType(position);
            FreeVersionPost mpost = list.get(position);

            if (convertView == null) {
                holder = new ViewHolder();

                switch (type) {
                    case TYPE_SUMMERY:
                        convertView = mInflater.inflate(
                                R.layout.fragment_paidverision_summery, parent,
                                false);
                        if (userHelper.getUser().getType() == UserTypeEnum.PARENTS) {
                            holder.meeting = (LinearLayout) convertView
                                    .findViewById(R.id.sum_lay_meeting);
                            holder.leave = (LinearLayout) convertView
                                    .findViewById(R.id.sum_lay_leave);
                            holder.tution = (LinearLayout) convertView
                                    .findViewById(R.id.sum_lay_tution);
                            holder.leaveStatusText = (TextView) convertView
                                    .findViewById(R.id.sum_tv_leave);
                            holder.meetingStatusText = (TextView) convertView
                                    .findViewById(R.id.sum_tv_meeting);
                        }
                        holder.attendance = (LinearLayout) convertView
                                .findViewById(R.id.sum_lay_attendance);
                        holder.schoolName = (TextView) convertView
                                .findViewById(R.id.sum_tv_school_name);
                        holder.studentName = (TextView) convertView
                                .findViewById(R.id.sum_tv_child_name);
                        holder.schoolPicture = (ResizableImageView) convertView
                                .findViewById(R.id.sum_iv_banner);
                        holder.profilePicture = (ImageView) convertView
                                .findViewById(R.id.sum_iv_profile_photo);
                        holder.currentDate = (TextView) convertView
                                .findViewById(R.id.sum_tv_date);
                        holder.eventText = (TextView) convertView
                                .findViewById(R.id.sum_tv_event);
                        holder.todayTextView = (TextView) convertView
                                .findViewById(R.id.sum_tv_today);
                        if (userHelper.getUser().getType() == UserTypeEnum.TEACHER) {
                            holder.meeting = (LinearLayout) convertView.findViewById(R.id.sum_lay_meeting);
                            holder.sum_lay_add_homework = (LinearLayout) convertView.findViewById(R.id.sum_lay_add_homework);
                            holder.sum_lay_add_quiz = (LinearLayout) convertView.findViewById(R.id.sum_lay_add_quiz);
                            holder.sum_lay_rollcall = (LinearLayout) convertView.findViewById(R.id.sum_lay_roll_call);
                            holder.nextClasses = (LinearLayout) convertView.findViewById(R.id.sum_lay_next_classes);
                            holder.nextClass2 = (LinearLayout) convertView.findViewById(R.id.sum_lay_next_class_second);
                            holder.nextHomework2 = (LinearLayout) convertView.findViewById(R.id.sum_lay_homework_second);
                            holder.teacherHomewoks = (LinearLayout) convertView.findViewById(R.id.sum_lay_next_homeworks);
                            holder.routineHomeworkQuizAdd = (LinearLayout) convertView.findViewById(R.id.sum_lay_teacher_rhq);
                            holder.sum_tv_batch_course1 = (TextView) convertView.findViewById(R.id.sum_tv_batch_course1);
                            holder.sum_tv_batch_course2 = (TextView) convertView.findViewById(R.id.sum_tv_batch_course2);
                            holder.sum_tv_class_duration1 = (TextView) convertView.findViewById(R.id.sum_tv_class_duration1);
                            holder.sum_tv_class_duration2 = (TextView) convertView.findViewById(R.id.sum_tv_class_duration2);
                            holder.sum_tv_subject_name_day1 = (TextView) convertView.findViewById(R.id.sum_tv_subject_name_day1);
                            holder.sum_tv_subject_name_day2 = (TextView) convertView.findViewById(R.id.sum_tv_subject_name_day2);
                            holder.sum_tv_teacher_hw_subject_stat1 = (TextView) convertView.findViewById(R.id.sum_tv_teacher_hw_subject_stat1);
                            holder.sum_tv_teacher_hw_subject_stat2 = (TextView) convertView.findViewById(R.id.sum_tv_teacher_hw_subject_stat2);
                            holder.sum_tv_teacher_hw_class_section1 = (TextView) convertView.findViewById(R.id.sum_tv_teacher_hw_class_section1);
                            holder.sum_tv_teacher_hw_class_section2 = (TextView) convertView.findViewById(R.id.sum_tv_teacher_hw_class_section2);
                            holder.sum_tv_teacher_hw_date1 = (TextView) convertView.findViewById(R.id.sum_tv_teacher_hw_date1);
                            holder.sum_tv_teacher_hw_date2 = (TextView) convertView.findViewById(R.id.sum_tv_teacher_hw_date2);

                        } else {

                            holder.attendanceTextView = (TextView) convertView
                                    .findViewById(R.id.sum_tv_attendance_text);
                            holder.classTomorrow = (LinearLayout) convertView
                                    .findViewById(R.id.sum_lay_has_class_tomorrow);
                            holder.toggle = (LinearLayout) convertView
                                    .findViewById(R.id.sum_lay_toggle);
                            holder.homework = (LinearLayout) convertView
                                    .findViewById(R.id.sum_lay_homework);
                            holder.quiz = (LinearLayout) convertView
                                    .findViewById(R.id.sum_lay_quiz);
                            holder.examResultText = (TextView) convertView
                                    .findViewById(R.id.sum_tv_result_pub_text);
                            holder.examRoutineText = (TextView) convertView
                                    .findViewById(R.id.sum_tv_exam_routine);
                            holder.examTomorrow = (LinearLayout) convertView
                                    .findViewById(R.id.sum_lay_exam);
                            holder.routinePublish = (LinearLayout) convertView
                                    .findViewById(R.id.sum_lay_exam_routine_publish);
                            holder.reusltPublish = (LinearLayout) convertView
                                    .findViewById(R.id.sum_lay_report_card);
                        }

                        holder.dateSlot = (LinearLayout) convertView
                                .findViewById(R.id.sum_lay_date_slot);
                        // holder.reusltPublish =
                        // (LinearLayout)convertView.findViewById(R.id.sum_lay)
                        holder.eventTomorrow = (LinearLayout) convertView
                                .findViewById(R.id.sum_lay_event);

                        holder.notice = (LinearLayout) convertView
                                .findViewById(R.id.sum_lay_notice);
                        for (int m = 0; m < 6; m++) {
                            holder.linearLayoutArray[m] = (LinearLayout) convertView
                                    .findViewById(lArray[m]);
                            holder.dateTextViewArray[m] = (TextView) convertView
                                    .findViewById(dArray[m]);
                            holder.monthTextViewArray[m] = (TextView) convertView
                                    .findViewById(mArray[m]);
                        }
                        break;
                    case TYPE_ITEM:
                        convertView = mInflater.inflate(
                                R.layout.fragment_freeversion_school_adapter,
                                parent, false);
                        holder.imgViewCategoryMenuIcon = (ImageView) convertView
                                .findViewById(R.id.imgViewCategoryMenuIcon);
                        holder.txtCategoryName = (TextView) convertView
                                .findViewById(R.id.txtCategoryName);
                        holder.txtPublishedDateString = (TextView) convertView
                                .findViewById(R.id.txtPublishedDateString);
                        holder.container = (PagerContainer) convertView
                                .findViewById(R.id.pager_container_row);
                        holder.viewPager = (UninterceptableViewPager) holder.container
                                .getViewPager();
                        // holder.viewPager.setPageMargin(10);
                        holder.textTitle = (TextView) convertView
                                .findViewById(R.id.txtHeading);
                        holder.txtSummary = (TextView) convertView
                                .findViewById(R.id.txtSummary);
                        holder.txtSummary.setOnClickListener(new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                int pos = Integer.parseInt(v.getTag().toString());
                                Intent intent = new Intent(getActivity(),
                                        SchoolSingleItemShowActivity.class);
                                intent.putExtra(AppConstant.ITEM_ID, list.get(pos)
                                        .getId());
                                intent.putExtra(AppConstant.ITEM_CAT_ID,
                                        list.get(pos).getCategoryId());
                                startActivity(intent);
                            }
                        });
                        holder.pagerBlock = (LinearLayout) convertView
                                .findViewById(R.id.middle);
                        holder.seenTextView = (TextView) convertView
                                .findViewById(R.id.fv_post_tv_seen);
                        holder.wowCount = (TextView) convertView
                                .findViewById(R.id.wow_count);
                        holder.btnWow = (CustomButton) convertView
                                .findViewById(R.id.btnWow);
                        holder.btnWow.setOnClickListener(new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                int pos = Integer.parseInt(v.getTag().toString());
                                doWow(pos);
							/*
							 * CustomButton w = (CustomButton) v;
							 * w.setTitleColor
							 * (getResources().getColor(R.color.red));
							 * w.setImage(R.drawable.wow_icon_tap);
							 */
                                View mother = (View) v.getParent().getParent()
                                        .getParent().getParent();
                                TextView count = (TextView) mother
                                        .findViewById(R.id.wow_count);
                                int n = Integer.parseInt(count.getText().toString()
                                        .split(" ")[0]);
                                if (n == 0)
                                    count.setVisibility(View.VISIBLE);
                                count.setText((n + 1) + " WoW");
                            }
                        });
                        holder.btnShare = (CustomButton) convertView
                                .findViewById(R.id.btnShare);
                        holder.btnShare.setOnClickListener(new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                int pos = Integer.parseInt(v.getTag().toString());
                                if (AppUtility.isInternetConnected()) {
                                    ((ChildContainerActivity) getActivity())
                                            .sharePostUniversal(list.get(pos));
                                }
                            }
                        });

                        holder.btnReadLater = (CustomButton) convertView
                                .findViewById(R.id.btnReadLater);
                        holder.btnReadLater
                                .setOnClickListener(new OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        int pos = Integer.parseInt(v.getTag()
                                                .toString());
                                        if (UserHelper.isLoggedIn())
                                            doReadLater(pos);
                                        else
                                            showCustomDialog(
                                                    "READ LATER",
                                                    R.drawable.read_later_red_icon,
                                                    getResources()
                                                            .getString(
                                                                    R.string.read_later_msg)
                                                            + "\n"
                                                            + getResources()
                                                            .getString(
                                                                    R.string.not_logged_in_msg));
                                    }
                                });
                        break;

                    default:
                        break;
                }
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (list.size() > 0) {

                switch (type) {
                    case TYPE_SUMMERY:
                        FreeVersionPost summary = list.get(position);
                        DateFeed feed = summary.getDateFeeds().get(
                                summary.getCurrentSummeryPosition());
                        holder.schoolName.setText(summary.getSchool_name());
                        holder.studentName.setText(feed.getStudent_name());
                        holder.currentDate.setText(summary.getCurrentDate().split(" ")[0] + "\n" + summary.getCurrentDate().split(" ")[1]);
                        if (!TextUtils.isEmpty(summary.getLast_visited().getType())) {
                            holder.todayTextView.setText(summary.getLast_visited()
                                    .getFirst()
                                    + "\n"
                                    + summary.getLast_visited().getNumber()
                                    + "\n"
                                    + summary.getLast_visited().getType());
                        } else {
                            holder.todayTextView.setText(summary.getLast_visited().getNumber());
                        }


                        if (userHelper.getUser().getType() == UserTypeEnum.TEACHER) {
                            disableBlock(holder.meeting, feed.isMeetingTomorrow(), 9);
                            if (feed.getNextClasses().size() == 0) {
                                holder.nextClasses.setVisibility(View.GONE);
                            } else {
                                holder.nextClasses.setVisibility(View.VISIBLE);
                                holder.nextClasses.setOnClickListener(new OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        ((HomePageFreeVersion) getActivity())
                                                .loadPaidFragment(PaidVersionHomeFragment
                                                        .newInstance(4));
                                    }
                                });
                                holder.sum_tv_subject_name_day1.setText(feed.getNextClasses().get(0).getSubject_name() + "(" + capitalize(feed.getNextClasses().get(0).getWeekday_text().substring(0, 3)) + ")");
                                holder.sum_tv_batch_course1.setText(feed.getNextClasses().get(0).getBatch_name() + ", " + feed.getNextClasses().get(0).getCourse_name());
                                holder.sum_tv_class_duration1.setText(feed.getNextClasses().get(0).getClass_start_time() + "-" + feed.getNextClasses().get(0).getClass_end_time());
                                if (feed.getNextClasses().size() > 1) {
                                    holder.sum_tv_subject_name_day2.setText(feed.getNextClasses().get(1).getSubject_name() + "(" + capitalize(feed.getNextClasses().get(1).getWeekday_text().substring(0, 3)) + ")");
                                    holder.sum_tv_batch_course2.setText(feed.getNextClasses().get(1).getBatch_name() + ", " + feed.getNextClasses().get(1).getCourse_name());
                                    holder.sum_tv_class_duration2.setText(feed.getNextClasses().get(1).getClass_start_time() + "-" + feed.getNextClasses().get(1).getClass_end_time());
                                } else {
                                    holder.nextClass2.setVisibility(View.GONE);
                                }
                            }
                            if (feed.getNextHomeWorks().size() == 0) {
                                holder.teacherHomewoks.setVisibility(View.GONE);
                            } else {
                                holder.teacherHomewoks.setVisibility(View.VISIBLE);
                                holder.teacherHomewoks.setOnClickListener(new OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        ((HomePageFreeVersion) getActivity())
                                                .loadPaidFragment(PaidVersionHomeFragment
                                                        .newInstance(3));
                                    }
                                });
                                holder.sum_tv_teacher_hw_subject_stat1.setText(feed.getNextHomeWorks().get(0).getSubjects() + "(" + feed.getNextHomeWorks().get(0).getDone() + " Done)");
                                holder.sum_tv_teacher_hw_class_section1.setText(feed.getNextHomeWorks().get(0).getBatch() + ", " + feed.getNextHomeWorks().get(0).getCourse());
                                holder.sum_tv_teacher_hw_date1.setText(feed.getNextHomeWorks().get(0).getDuedate());
                                if (feed.getNextHomeWorks().size() > 1) {
                                    holder.sum_tv_teacher_hw_subject_stat2.setText(feed.getNextHomeWorks().get(1).getSubjects() + "(" + feed.getNextHomeWorks().get(1).getDone() + " Done)");
                                    holder.sum_tv_teacher_hw_class_section2.setText(feed.getNextHomeWorks().get(1).getBatch() + ", " + feed.getNextHomeWorks().get(1).getCourse());
                                    holder.sum_tv_teacher_hw_date2.setText(feed.getNextHomeWorks().get(1).getDuedate());
                                } else {
                                    holder.nextHomework2.setVisibility(View.GONE);
                                }
                            }
                            holder.routineHomeworkQuizAdd.setVisibility(View.VISIBLE);
                            holder.sum_lay_rollcall.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    ((HomePageFreeVersion) getActivity())
                                            .loadPaidFragment(PaidVersionHomeFragment
                                                    .newInstance(2));
                                }
                            });
                            holder.sum_lay_add_homework.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    ((HomePageFreeVersion) getActivity())
                                            .loadPaidFragment(PaidVersionHomeFragment
                                                    .newInstance(3));
                                }
                            });
                        } else {
                            holder.toggle.setVisibility(View.VISIBLE);
                            holder.attendance.setVisibility(View.VISIBLE);
                            holder.attendanceTextView.setText(feed.getAttendence());
                            disableBlock(holder.classTomorrow,
                                    feed.isHasClassTomorrow(), 8);
                            disableBlock(holder.homework, feed.getHomeWorkSubjects()
                                    .size() != 0, 2);
                            disableBlock(holder.attendance, true, 7);
                            disableBlock(holder.examTomorrow, feed.isHasExamTomorrow(),
                                    9);
                            disableBlock(holder.quiz,
                                    feed.getSummeryQuizes().size() != 0, 3);
                            disableBlock(holder.reusltPublish,
                                    !TextUtils.isEmpty(feed.getResult_publish()), 10);
                            if (!TextUtils.isEmpty(feed.getResult_publish())) {
                                holder.examResultText.setText(feed.getResult_publish()
                                        + " Result published.");
                            }
                            if (!TextUtils.isEmpty(feed.getRoutine_publish())) {
                                holder.examRoutineText.setText(feed
                                        .getRoutine_publish() + " Routine published.");
                            }
                            disableBlock(holder.routinePublish,
                                    !TextUtils.isEmpty(feed.getRoutine_publish()), 9);
                            holder.toggle.setOnClickListener(new OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    View mother = (View) v.getParent().getParent();
                                    View child = mother
                                            .findViewById(R.id.sum_lay_date_slot);
                                    if (child.getVisibility() == View.VISIBLE)
                                        child.setVisibility(View.GONE);
                                    else {
                                        child.setVisibility(View.VISIBLE);
                                        listGoodread.refreshDrawableState();
                                    }

                                }
                            });
                        }

                        if (userHelper.getUser().getType() == UserTypeEnum.PARENTS) {
                            disableBlock(holder.leave, feed.getSummeryLeaves()
                                    .size() != 0, 14);
                            disableBlock(holder.tution, feed.isFees(), 15);
                            disableBlock(holder.meeting, feed.getSummeryMeetings()
                                    .size() != 0, 11);
                            if (feed.getSummeryMeetings().size() > 0) {
                                if (feed.getSummeryMeetings().get(0).getSubject()
                                        .contains("Accepted"))
                                    holder.meetingStatusText
                                            .setText("Accepted. See details.");
                                else
                                    holder.meetingStatusText
                                            .setText("Declined. See details.");
                            }
                            if (feed.getSummeryLeaves().size() != 0) {
                                if (feed.getSummeryLeaves().get(0).getSubject()
                                        .contains("Approved"))
                                    holder.leaveStatusText
                                            .setText("Approved. See details.");
                                else
                                    holder.leaveStatusText
                                            .setText("Declined. See details.");
                            }

                        }

                        disableBlock(holder.eventTomorrow,
                                feed.isHasEventTomorrow(), 6);
                        if (feed.isHasEventTomorrow())
                            holder.eventText.setText("You have "
                                    + feed.getEvent_name() + " Tomorrow.");

                        disableBlock(holder.notice, feed.isHasNotice(), 4);

                        if (!TextUtils.isEmpty(summary.getSchool_picture()))
                            SchoolApp.getInstance().displayUniversalImage(
                                    summary.getSchool_picture(),
                                    holder.schoolPicture);
                        if (!TextUtils.isEmpty(summary.getProfile_picture()))
                            SchoolApp.getInstance().displayUniversalImage(
                                    summary.getProfile_picture(),
                                    holder.profilePicture);
                        for (int k = 0; k < 6; k++) {
                            holder.linearLayoutArray[k].setTag(k + "");
                            holder.linearLayoutArray[k]
                                    .setOnClickListener(new OnClickListener() {

                                        @Override
                                        public void onClick(View v) {
                                            list.get(0).setCurrentSummeryPosition(
                                                    Integer.parseInt(v.getTag()
                                                            .toString()));
                                            adapter.notifyDataSetChanged();
                                        }
                                    });
                            if (k == summary.getCurrentSummeryPosition()) {
                                holder.linearLayoutArray[k]
                                        .setBackgroundColor(getActivity()
                                                .getResources().getColor(
                                                        R.color.white));
                                holder.dateTextViewArray[k]
                                        .setTextColor(getActivity().getResources()
                                                .getColor(R.color.black));
                            } else {
                                holder.linearLayoutArray[k]
                                        .setBackgroundColor(getActivity()
                                                .getResources().getColor(
                                                        R.color.gray_3));
                                holder.dateTextViewArray[k]
                                        .setTextColor(getActivity().getResources()
                                                .getColor(R.color.gray_4));
                            }
                            holder.dateTextViewArray[k].setText(summary
                                    .getSummeryDates().get(k).getNumber());
                            holder.monthTextViewArray[k].setText(summary
                                    .getSummeryDates().get(k).getName()
                                    .substring(0, 3));

                        }

                        break;
                    case TYPE_ITEM:
                        DisplayImageOptions userimgoptions = new DisplayImageOptions.Builder()
                                .displayer(new RoundedBitmapDisplayer((int) 60))
                                .showImageForEmptyUri(R.drawable.user_icon)
                                .showImageOnFail(R.drawable.user_icon)
                                .cacheInMemory(true).cacheOnDisc(true)
                                .bitmapConfig(Bitmap.Config.RGB_565).build();
                        ImageLoader.getInstance().displayImage(
                                list.get(position).getAuthor_image(),
                                holder.imgViewCategoryMenuIcon, userimgoptions,
                                new ImageLoadingListener() {

                                    @Override
                                    public void onLoadingStarted(String arg0,
                                                                 View arg1) {
                                    }

                                    @Override
                                    public void onLoadingFailed(String arg0,
                                                                View arg1, FailReason arg2) {
                                    }

                                    @Override
                                    public void onLoadingComplete(String arg0,
                                                                  View arg1, Bitmap arg2) {
                                    }

                                    @Override
                                    public void onLoadingCancelled(String arg0,
                                                                   View arg1) {
                                    }
                                });
					/*
					 * SchoolApp.getInstance().displayUniversalImage(
					 * allGooadReadPost.get(position).getAuthor_image(),
					 * holder.imgViewCategoryMenuIcon);
					 */
                        // }
                        holder.txtCategoryName.setText(list.get(position)
                                .getAuthor());
                        holder.textTitle.setText(list.get(position).getTitle());
                        holder.txtPublishedDateString.setText(list.get(position)
                                .getPublishedDateString());
                        holder.txtSummary.setText(list.get(position).getSummary());
                        holder.seenTextView.setText(list.get(position)
                                .getSeenCount());

                        if (list.get(position).getWow_count().equals("0")) {
                            holder.wowCount.setVisibility(View.GONE);
                        } else
                            holder.wowCount.setVisibility(View.VISIBLE);

                        holder.wowCount.setText(list.get(position).getWow_count()
                                + " WoW");

                        // setting position tag of the buttons
                        holder.btnWow.setTag("" + position);
                        holder.btnShare.setTag("" + position);
                        holder.btnReadLater.setTag("" + position);
                        holder.wowCount.setTag("" + position);
                        holder.txtSummary.setTag("" + position);

                        // if(map.get(currentTabKey).get(position).getMobileImageUrl()
                        // != null ||
                        // map.get(currentTabKey).get(position).getMobileImageUrl().length()
                        // > 0)
                        // SchoolApp.getInstance().displayUniversalImage(map.get(currentTabKey).get(position).getMobileImageUrl(),
                        // holder.imgMobileImage);

                        Log.e("PUBLISHED_DATE", "is:"
                                + list.get(position).getPublishedDateString());

                        ArrayList<String> imgPaths = list.get(position).getImages();
                        if (imgPaths.size() == 0) {
                            holder.pagerBlock.setVisibility(View.GONE);
                        } else
                            holder.pagerBlock.setVisibility(View.VISIBLE);

                        holder.imgAdapter = new ImagePagerAdapter(imgPaths);

                        // Log.e("IMAGE PATH",listData.get(position).getImages().get(0)+"");

                        // If hardware acceleration is enabled, you should also
                        // remove
                        // clipping on the pager for its children.

                        holder.viewPager.setAdapter(holder.imgAdapter);
                        holder.viewPager.setTag("" + position);
                        // holder.imgAdapter.notifyDataSetChanged();
                        holder.viewPager.setOffscreenPageLimit(holder.imgAdapter
                                .getCount());
                        // A little space between pages
                        holder.viewPager.setPageMargin(15);

                        // If hardware acceleration is enabled, you should also
                        // remove
                        // clipping on the pager for its children.
                        holder.viewPager.setClipChildren(false);
                        //
                        holder.viewPager.setOnTouchListener(new OnTouchListener() {
                            private float pointX;
                            private float pointY;
                            private int tolerance = 50;

                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                switch (event.getAction()) {
                                    case MotionEvent.ACTION_MOVE:
                                        return false; // This is important, if you
                                    // return
                                    // TRUE the action of swipe will
                                    // not
                                    // take place.
                                    case MotionEvent.ACTION_DOWN:
                                        pointX = event.getX();
                                        pointY = event.getY();
                                        break;
                                    case MotionEvent.ACTION_UP:
                                        boolean sameX = pointX + tolerance > event
                                                .getX()
                                                && pointX - tolerance < event.getX();
                                        boolean sameY = pointY + tolerance > event
                                                .getY()
                                                && pointY - tolerance < event.getY();
                                        if (sameX && sameY) {

									/*
									 * FreeVersionPost item = (FreeVersionPost)
									 * adapter
									 * .getItem(Integer.parseInt(v.getTag()
									 * .toString()));
									 * 
									 * Intent intent = new Intent(
									 * getActivity(),
									 * SingleItemShowActivity.class);
									 * intent.putExtra(AppConstant.ITEM_ID,
									 * item.getId());
									 * intent.putExtra(AppConstant.ITEM_CAT_ID,
									 * item.getCategoryId());
									 * intent.putExtra(AppConstant
									 * .GOING_GOODREAD, "OK");
									 * startActivity(intent);
									 */
                                        }
                                }
                                return false;
                            }
                        });
                        break;
                    default:
                        break;
                }
            } else {
                Log.e("FREE_HOME_API", "array is empty!");
            }

            return convertView;
        }

        private String capitalize(final String line) {
            return Character.toUpperCase(line.charAt(0)) + line.substring(1);
        }

        private void doWow(int i) {
            RequestParams params = new RequestParams();
            FreeVersionPost p = list.get(i);
            // params.put(RequestKeyHelper.USER_ID, UserHelper.getUserFreeId());
            params.put(RequestKeyHelper.POST_ID, list.get(i).getId());
            list.get(i).setCan_wow(0);

            list.get(i).setWow_count(
                    (Integer.parseInt(p.getWow_count()) + 1) + "");
            AppRestClient.post(URLHelper.URL_FREE_VERSION_ADDWOW, params,
                    wowHandler);
        }

        private void doReadLater(int i) {
            RequestParams params = new RequestParams();

            params.put(RequestKeyHelper.USER_ID, UserHelper.getUserFreeId());
            params.put(RequestKeyHelper.POST_ID, list.get(i).getId());

            AppRestClient.post(URLHelper.URL_FREE_VERSION_READLATER, params,
                    readLaterHandler);
        }

        private int getVisibility(boolean isVisible) {
            if (isVisible)
                return View.VISIBLE;
            else
                return View.INVISIBLE;
        }

        private void disableBlock(View view, boolean state, int pos) {

            view.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    int pos = Integer.parseInt(v.getTag().toString());
                    ((HomePageFreeVersion) getActivity())
                            .loadPaidFragment(PaidVersionHomeFragment
                                    .newInstance(pos));
                }
            });
            switch (view.getId()) {
                case R.id.sum_lay_event:
                case R.id.sum_lay_has_class_tomorrow:
                case R.id.sum_lay_homework:
                case R.id.sum_lay_report_card:
                case R.id.sum_lay_quiz:
                case R.id.sum_lay_leave:
                case R.id.sum_lay_tution:
                case R.id.sum_lay_attendance:
                case R.id.sum_lay_meeting:
                    if (state) {

                        view.setVisibility(View.VISIBLE);
                        view.setTag("" + pos);

                    } else {
                        view.setVisibility(View.GONE);

                    }

                    break;
                case R.id.sum_lay_exam:
                case R.id.sum_lay_notice:
                case R.id.sum_lay_exam_routine_publish:
                    if (state) {
                        view.setVisibility(View.VISIBLE);
                        view.setTag("" + pos);

                    } else {
                        view.setVisibility(View.GONE);
                        // view.setBackgroundColor(getResources().getColor(R.color.bg_disable));
                        // view.findViewById(R.id.sum_iv_disable).setBackgroundColor(getActivity().getResources().getColor(R.color.red_disable));
                    }

                    break;

                default:
                    break;
            }

        }
    }

    AsyncHttpResponseHandler wowHandler = new AsyncHttpResponseHandler() {
        public void onStart() {
            // uiHelper.showLoadingDialog("Please wait...");
        }

        ;

        public void onFailure(Throwable arg0, String arg1) {
            uiHelper.showMessage(arg1);
            if (uiHelper.isDialogActive()) {
                uiHelper.dismissLoadingDialog();
            }
            Log.e("WOW HOYNAI", "HEHEHEHE" + arg1);
        }

        ;

        public void onSuccess(int arg0, String responseString) {
            if (uiHelper.isDialogActive()) {
                uiHelper.dismissLoadingDialog();
            }

            Wrapper wrapper = GsonParser.getInstance().parseServerResponse(
                    responseString);

            if (wrapper.getStatus().getCode() == 200) {
                // uiHelper.showMessage("Successfully added wow");
                Log.e("WOW HOISE", "HEHEHEHE");
            } else {
                Log.e("WOW HONAI ONNO CODE", " " + responseString);
            }
        }

        ;
    };

    AsyncHttpResponseHandler readLaterHandler = new AsyncHttpResponseHandler() {
        public void onStart() {
            uiHelper.showLoadingDialog("Please wait...");
        }

        ;

        public void onFailure(Throwable arg0, String arg1) {
            uiHelper.showMessage(arg1);
            if (uiHelper.isDialogActive()) {
                uiHelper.dismissLoadingDialog();
            }
        }

        ;

        public void onSuccess(int arg0, String responseString) {
            if (uiHelper.isDialogActive()) {
                uiHelper.dismissLoadingDialog();
            }

            Wrapper wrapper = GsonParser.getInstance().parseServerResponse(
                    responseString);

            if (wrapper.getStatus().getCode() == 200) {
                uiHelper.showMessage("Post has been added to GoodRead");
            }
        }

        ;
    };

    class ViewHolder {
        ImageView imgViewCategoryMenuIcon;
        TextView txtCategoryName;
        TextView txtPublishedDateString;
        PagerContainer container;
        UninterceptableViewPager viewPager;
        TextView txtSummary;
        ImagePagerAdapter imgAdapter;
        TextView seenTextView;
        TextView textTitle;
        LinearLayout pagerBlock;
        CustomButton btnWow, btnShare, btnReadLater;
        TextView wowCount;

        // SUMMERY LAYOUT
        TextView schoolName, currentDate, studentName;
        TextView todayTextView;
        TextView attendanceTextView, leaveStatusText, meetingStatusText;
        TextView examRoutineText, examResultText, eventText;
        TextView sum_tv_subject_name_day1, sum_tv_batch_course1, sum_tv_class_duration1;
        TextView sum_tv_subject_name_day2, sum_tv_batch_course2, sum_tv_class_duration2;
        TextView sum_tv_teacher_hw_subject_stat1, sum_tv_teacher_hw_class_section1, sum_tv_teacher_hw_date1;
        TextView sum_tv_teacher_hw_subject_stat2, sum_tv_teacher_hw_class_section2, sum_tv_teacher_hw_date2;
        ResizableImageView schoolPicture;
        ImageView profilePicture;
        LinearLayout classTomorrow, homework, reusltPublish, routinePublish,
                eventTomorrow, examTomorrow, notice, quiz;
        LinearLayout meeting, leave, tution, attendance, dateSlot, toggle,
                nextClasses, teacherHomewoks, nextClass2, nextHomework2, routineHomeworkQuizAdd;
        LinearLayout sum_lay_rollcall, sum_lay_add_homework, sum_lay_add_quiz;

        TextView[] dateTextViewArray = new TextView[6];
        TextView[] monthTextViewArray = new TextView[6];
        LinearLayout[] linearLayoutArray = new LinearLayout[6];

    }

    private class ImagePagerAdapter extends PagerAdapter {

        private List<String> images;
        private LayoutInflater inflater;

        ImagePagerAdapter(List<String> images) {
            this.images = images;
            inflater = getActivity().getLayoutInflater();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public Object instantiateItem(ViewGroup view, int position) {
            View imageLayout = inflater.inflate(R.layout.item_pager_image,
                    view, false);
            assert imageLayout != null;
            ImageView imageView = (ImageView) imageLayout
                    .findViewById(R.id.image);
            final ProgressBar spinner = (ProgressBar) imageLayout
                    .findViewById(R.id.loading);
            SchoolApp.getInstance().displayUniversalImage(images.get(position),
                    imageView);
            // imageLoader.displayImage(images.get(position), imageView,
            // options,
            // new SimpleImageLoadingListener() {
            // @Override
            // public void onLoadingStarted(String imageUri, View view) {
            // spinner.setVisibility(View.VISIBLE);
            // }
            //
            // @Override
            // public void onLoadingFailed(String imageUri, View view,
            // FailReason failReason) {
            // String message = null;
            // switch (failReason.getType()) {
            // case IO_ERROR:
            // message = "Input/Output error";
            // break;
            // case DECODING_ERROR:
            // message = "Image can't be decoded";
            // break;
            // case NETWORK_DENIED:
            // message = "Downloads are denied";
            // break;
            // case OUT_OF_MEMORY:
            // message = "Out Of Memory error";
            // break;
            // case UNKNOWN:
            // message = "Unknown error";
            // break;
            // }
            // Toast.makeText(getActivity(), message,
            // Toast.LENGTH_SHORT).show();
            //
            // spinner.setVisibility(View.GONE);
            // }
            //
            // @Override
            // public void onLoadingComplete(String imageUri,
            // View view, Bitmap loadedImage) {
            // spinner.setVisibility(View.GONE);
            // }
            // });

            view.addView(imageLayout, 0);
            return imageLayout;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }
    }

    @Override
    public void onAuthenticationStart() {

    }

    @Override
    public void onAuthenticationSuccessful() {

    }

    @Override
    public void onAuthenticationFailed(String msg) {

    }

    @Override
    public void onPaswordChanged() {

    }

    private void setupPoppyView(View poppyView) {
        ImageButton btnGoodread = (ImageButton) poppyView
                .findViewById(R.id.btn_goodread);
        btnGoodread.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (UserHelper.isLoggedIn())
                    startActivity(new Intent(SchoolFeedFragment.this
                            .getActivity(), GoodReadActivity.class));
                else {
                    showCustomDialog(
                            "GOOD READ",
                            R.drawable.goodread_popup_icon,
                            getResources().getString(R.string.good_read_msg)
                                    + "\n"
                                    + getResources().getString(
                                    R.string.not_logged_in_msg));
                }
                // uiHelper.showSuccessDialog(
                // getString(R.string.not_logged_in_msg), "Dear User");
            }
        });

        ImageButton btnMySchool = (ImageButton) poppyView
                .findViewById(R.id.btn_myschool);
        btnMySchool.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

					/*Intent schoolIntent = new Intent(getActivity(),
							SchoolFreeVersionActivity.class);
					startActivity(schoolIntent);*/
                Intent intent = new Intent(
                        getActivity(),
                        SchoolScrollableDetailsActivity.class);
                intent.putExtra(AppConstant.SCHOOL_ID, userHelper.getJoinedSchool().getSchool_id());
                startActivity(intent);

                // uiHelper.showMessage("Magic mart is comming soon!");
            }
        });

        ImageButton btnCandle = (ImageButton) poppyView
                .findViewById(R.id.btn_candle);
        btnCandle.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (UserHelper.isLoggedIn())
                    startActivity(new Intent(getActivity(),
                            CandleActivity.class));
                else {
                    showCustomDialog(
                            "CANDLE",
                            R.drawable.candle_popup_icon,
                            getResources().getString(R.string.candle_msg)
                                    + "\n"
                                    + getResources().getString(
                                    R.string.not_logged_in_msg));
                }
                // uiHelper.showSuccessDialog(
                // getString(R.string.not_logged_in_msg), "Dear User");
            }
        });
    }
}
