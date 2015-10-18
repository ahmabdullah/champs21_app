package com.champs21.schoolapp.fragments;

import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Gravity;
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
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.champs21.freeversion.AssesmentActivity;
import com.champs21.freeversion.CandleActivity;
import com.champs21.freeversion.CompleteProfileActivityContainer;
import com.champs21.freeversion.GoodReadActivity;
import com.champs21.freeversion.HomeContainerActivity;
import com.champs21.freeversion.HomePageFreeVersion;
import com.champs21.freeversion.PaidVersionHomeFragment;
import com.champs21.freeversion.SchoolFreeVersionActivity;
import com.champs21.freeversion.SingleItemShowActivity;
import com.champs21.freeversion.SingleSchoolFreeVersionActivity;
import com.champs21.schoolapp.ChildSelectionActivity;
import com.champs21.schoolapp.LoginActivity;
import com.champs21.schoolapp.R;
import com.champs21.schoolapp.model.AddData;
import com.champs21.schoolapp.model.FreeVersionPost;
import com.champs21.schoolapp.model.SubCategory;
import com.champs21.schoolapp.model.UserAuthListener;
import com.champs21.schoolapp.model.Wrapper;
import com.champs21.schoolapp.networking.AppRestClient;
import com.champs21.schoolapp.networking.SchoolAppPostRequest;
import com.champs21.schoolapp.utils.AppConstant;
import com.champs21.schoolapp.utils.AppUtility;
import com.champs21.schoolapp.utils.GsonParser;
import com.champs21.schoolapp.utils.RequestKeyHelper;
import com.champs21.schoolapp.utils.SPKeyHelper;
import com.champs21.schoolapp.utils.SchoolApp;
import com.champs21.schoolapp.utils.URLHelper;
import com.champs21.schoolapp.utils.UserHelper;
import com.champs21.schoolapp.utils.UserHelper.UserAccessType;
import com.champs21.schoolapp.utils.UserHelper.UserTypeEnum;
import com.champs21.schoolapp.viewhelpers.CustomButton;
import com.champs21.schoolapp.viewhelpers.CustomRhombusIcon;
import com.champs21.schoolapp.viewhelpers.CustomTabButton;
import com.champs21.schoolapp.viewhelpers.PagerContainer;
import com.champs21.schoolapp.viewhelpers.PopupDialog;
import com.champs21.schoolapp.viewhelpers.ResizableImageView;
import com.champs21.schoolapp.viewhelpers.UIHelper;
import com.champs21.schoolapp.viewhelpers.UninterceptableViewPager;
import com.champs21.spellingbee.LeaderBoardActivity;
import com.champs21.spellingbee.SpellingbeeRulesActivity;
import com.champs21.spellingbee.SpellingbeeTestActivity;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListViewCustom;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public class CommonChildFragment extends Fragment implements UserAuthListener,
        OnClickListener {

    public static final int REQUEST_CODE_CHILD_SELECTION = 123;
    public static final int FROMSINGLE = 155;
    PullToRefreshListViewCustom listViewFitness;
    EfficientAdapter fitnessAdapter;
    boolean hasNext = false;
    UserHelper userHelper;
    UIHelper uiHelper;
    private String mCategoryId;
    private DisplayImageOptions options;
    private boolean addedOnce = false;
    // private ImageView bannerView;
    private com.nostra13.universalimageloader.core.ImageLoader imageLoader;
    private String currentCategoryId = "";
    CustomTabButton current;
    // pullload
    private Context mContext;
    private int pageNumber = 1;
    private int pageSize = 10;
    private boolean isRefreshing = false;
    private boolean loading = false;
    private boolean stopLoadingData = false;
    private LinearLayout mQuickReturnTextView;
    private UserTypeEnum selectedEnum = UserTypeEnum.OTHER;
    private String currentSubcategoryId = "";
    private LinearLayout headerBlock;

    public static CommonChildFragment newInstance(int index, String sub) {
        CommonChildFragment f = new CommonChildFragment();
        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putInt("categoryIndex", index);
        args.putString("subcategory", sub);
        f.setArguments(args);
        return f;
    }

    private LinearLayout scrollLinearLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        uiHelper = new UIHelper(getActivity());
        userHelper = new UserHelper(this, getActivity());
        mContext = getActivity();
    }

    private void loadDataInToList() {
        // if (AppUtility.isInternetConnected()) {
        currentCategoryId = getArguments().getInt("categoryIndex", 0) + "";
        currentSubcategoryId = getArguments().getString("subcategory");
        processFetchPost(URLHelper.URL_FREE_VERSION_CATEGORY,
                currentCategoryId, currentSubcategoryId, false);
        if (!currentCategoryId.equals("-1")) {
            mainHome.setImageResource(R.drawable.home_normal);
            mainHome.setTag(0);
            mainCategoryIcon.setImageResource(AppUtility.getResourceImageId(
                    Integer.parseInt(currentCategoryId), true, false));
            mainCategroyText.setText(AppUtility.getCatNameById(getActivity(),
                    Integer.parseInt(currentCategoryId)));
            switchLayoutSTP.setVisibility(View.GONE);
            switchLayoutOther.setVisibility(View.VISIBLE);
            if (currentCategoryId.equals("7")) headerBlock.setVisibility(View.GONE);
            else headerBlock.setVisibility(View.VISIBLE);
        } else headerBlock.setVisibility(View.VISIBLE);
        /*
		 * } else uiHelper.showMessage(getActivity().getString(
		 * R.string.internet_error_text));
		 */
    }

    private void setUpList() {

        initializePageing();
        listViewFitness.setMode(Mode.BOTH);
        // Set a listener to be invoked when the list should be refreshed.
        listViewFitness.setOnRefreshListener(new OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(mContext,
                        System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
                                | DateUtils.FORMAT_SHOW_DATE
                                | DateUtils.FORMAT_ABBREV_ALL);

                // Update the LastUpdatedLabel
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

                Mode m = listViewFitness.getCurrentMode();
                if (m == Mode.PULL_FROM_START) {
                    stopLoadingData = false;
                    isRefreshing = true;
                    pageNumber = 1;
                    loading = true;
                    processFetchPost(URLHelper.URL_FREE_VERSION_CATEGORY,
                            currentCategoryId, "", true);
                } else if (!stopLoadingData) {
                    pageNumber++;
                    loading = true;
                    processFetchPost(URLHelper.URL_FREE_VERSION_CATEGORY,
                            currentCategoryId, "", false);
                } else {
                    new NoDataTask().execute();
                }
            }
        });

        fitnessAdapter = new EfficientAdapter(mContext,
                new ArrayList<FreeVersionPost>());
        fitnessAdapter.clearList();
        listViewFitness.setAdapter(fitnessAdapter);
    }

    private View view;
    ProgressBar spinner;
    private ImageButton mainHome;
    private LinearLayout switchLayoutSTP;
    private LinearLayout switchLayoutOther;
    private ImageButton mainStudent;
    private ImageButton mainParent;
    private ImageButton mainTeacher;
    private ImageView mainCategoryIcon;
    private TextView mainCategroyText;

    private void initializePageing() {
        pageNumber = 1;
        isRefreshing = false;
        loading = false;
        stopLoadingData = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Log.e("CRETEVIEW CALLED", "HERE$$$$$$$$$");
        // TODO Auto-generated method stub
        super.onCreateView(inflater, container, savedInstanceState);
        setUpImageUtis();

        view = inflater
                .inflate(R.layout.fragment_fv_category, container, false);
        spinner = (ProgressBar) view.findViewById(R.id.loading);
        scrollLinearLayout = (LinearLayout) view
                .findViewById(R.id.subCategoryTabLayout);
        headerBlock = (LinearLayout) view.findViewById(R.id.header_block);
        mainHome = (ImageButton) view.findViewById(R.id.btn_home_home);
        mainHome.setOnClickListener(this);
        switchLayoutSTP = (LinearLayout) view
                .findViewById(R.id.top_panel_for_home);
        switchLayoutOther = (LinearLayout) view
                .findViewById(R.id.top_panel_for_others);
        mainStudent = (ImageButton) view.findViewById(R.id.btn_student_student);
        mainStudent.setOnClickListener(this);
        mainParent = (ImageButton) view.findViewById(R.id.btn_parent_parent);
        mainParent.setOnClickListener(this);
        mainTeacher = (ImageButton) view.findViewById(R.id.btn_teacher_teacher);
        mainTeacher.setOnClickListener(this);
        mainCategoryIcon = (ImageView) view.findViewById(R.id.img_cat_icon);
        mainCategroyText = (TextView) view.findViewById(R.id.txt_category_name);
        deactiveAllButton();
        mainHome.setTag(1);
        mainHome.setImageResource(R.drawable.home_tap);
        // setupPoppyView();
        // bannerView = (ImageView) view.findViewById(R.id.iv_banner);

        mQuickReturnTextView = (LinearLayout) view
                .findViewById(R.id.quick_return_tv);
        setupPoppyView(mQuickReturnTextView);
        listViewFitness = (PullToRefreshListViewCustom) view
                .findViewById(R.id.listView_category);
        int footerHeight = mContext.getResources().getDimensionPixelSize(
                R.dimen.footer_height);
        listViewFitness.setQuickReturnListViewOnScrollListener(
                PullToRefreshListViewCustom.QuickReturnType.FOOTER, null, 0,
                mQuickReturnTextView, footerHeight);
        // listViewFitness.getRefreshableView().addHeaderView(mQuickReturnTextView);
        listViewFitness.setCanSlideInIdleScrollState(true);
        setUpList();
        loadDataInToList();
        return view;
    }

    private void deactiveAllButton() {
        mainHome.setImageResource(R.drawable.home_normal);
        mainHome.setTag(0);
        mainStudent.setImageResource(R.drawable.top_bar_student_normal);
        mainStudent.setTag(0);
        mainParent.setImageResource(R.drawable.top_bar_parents_normal);
        mainParent.setTag(0);
        mainTeacher.setImageResource(R.drawable.top_bar_teacher_normal);
        mainTeacher.setTag(0);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        currentCategoryId = getArguments().getInt("categoryIndex", 0) + "";
        addedOnce = false;
        if (allSubcategory != null) {
            if (mContext != null)
                createCategoryView();
        }
        super.onViewCreated(view, savedInstanceState);
    }

    private void createTermButtons(ArrayList<SubCategory> terms) {
        // TODO Auto-generated method stub
        for (SubCategory term : terms) {
            LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            parms.setMargins(3, 0, 0, 0);
            parms.weight = 1;
            CustomTabButton btn = new CustomTabButton(mContext);
            btn.setClickable(true);
            btn.setGravity(Gravity.CENTER);
            btn.setTag(term.getId());
            btn.setTitleText(term.getName());
            btn.setLayoutParams(parms);
            btn.setImage(R.drawable.resource_sub_icon_normal);
            btn.setOnClickListener(this);
            btn.setBackgroundResource(R.drawable.tab_general_btn);
            if (term.getId().equals(currentSubcategoryId)) {
                btn.setButtonSelected(true,
                        mContext.getResources().getColor(R.color.black),
                        R.drawable.resource_sub_icon_tap);
                current = btn;
            }
            scrollLinearLayout.addView(btn);
            // Log.e("TERMID", term.getId());
        }
    }

    private void setUpImageUtis() {
        // TODO Auto-generated method stub
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true).cacheOnDisc(true).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                mContext).threadPriority(Thread.NORM_PRIORITY - 2)
                .defaultDisplayImageOptions(defaultOptions)
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                        // .writeDebugLogs() // Remove for release app
                .build();

        imageLoader = com.nostra13.universalimageloader.core.ImageLoader
                .getInstance();
        imageLoader.init(config);

        options = new DisplayImageOptions.Builder()
                .resetViewBeforeLoading(true).cacheOnDisc(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new FadeInBitmapDisplayer(300)).build();
    }

    // private PoppyViewHelper mPoppyViewHelper;
    public void showCustomDialog(String headerText, int imgResId,
                                 String descriptionText) {

        PopupDialog picker = PopupDialog.newInstance(0);
        picker.setData(headerText, descriptionText, imgResId, mContext);
        picker.show(getChildFragmentManager(), null);
    }

    public enum QuickReturnType {
        HEADER, FOOTER, BOTH, GOOGLE_PLUS, TWITTER
    }

    private void setupPoppyView(View poppyView) {
        ImageButton btnGoodread = (ImageButton) poppyView
                .findViewById(R.id.btn_goodread);
        btnGoodread.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (UserHelper.isLoggedIn())
                    startActivity(new Intent(CommonChildFragment.this
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
        if (UserHelper.isLoggedIn()) {
            if (userHelper.getUser().getAccessType() == UserAccessType.PAID) {
                btnMySchool.setBackgroundResource(R.drawable.btn_my_diary_pop);
            }
        }
        btnMySchool.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (UserHelper.isLoggedIn()) {
                    switch (UserHelper.getUserAccessType()) {
                        case FREE:
                            if (userHelper.getUser().isJoinedToSchool()) {
                                Intent schoolIntent = new Intent(mContext,
                                        SingleSchoolFreeVersionActivity.class);
                                schoolIntent
                                        .putExtra(AppConstant.SCHOOL_ID, UserHelper
                                                .getJoinedSchool().getSchool_id());
                                startActivity(schoolIntent);

                            } else {
                                Intent schoolIntent = new Intent(mContext,
                                        SchoolFreeVersionActivity.class);
                                startActivity(schoolIntent);
                            }
                            break;

                        case PAID:
                            if (userHelper.getUser().getType() == UserTypeEnum.PARENTS)
                                startActivityForResult(new Intent(mContext,
                                                ChildSelectionActivity.class),
                                        REQUEST_CODE_CHILD_SELECTION);
                            else {
                                ((HomePageFreeVersion) getActivity())
                                        .setActionBarTitle(userHelper.getUser()
                                                .getPaidInfo().getSchool_name());
                                ((HomePageFreeVersion) getActivity())
                                        .loadPaidFragment(PaidVersionHomeFragment
                                                .newInstance(1));
                            }

                            break;

                        default:
                            break;
                    }
                } else {
                    Intent schoolIntent = new Intent(mContext,
                            SchoolFreeVersionActivity.class);
                    startActivity(schoolIntent);
                }

                // uiHelper.showMessage("Magic mart is comming soon!");
            }
        });

        ImageButton btnCandle = (ImageButton) poppyView
                .findViewById(R.id.btn_candle);
        btnCandle.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (UserHelper.isLoggedIn())
                    startActivity(new Intent(mContext,
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        switch (requestCode) {
            case REQUEST_CODE_CHILD_SELECTION:

                if (data == null) {
                    if (resultCode == getActivity().RESULT_OK) {
                        ((HomePageFreeVersion) getActivity())
                                .loadPaidFragment(PaidVersionHomeFragment.newInstance(1));
                        ((HomePageFreeVersion) getActivity())
                                .setActionBarTitle(userHelper.getUser().getPaidInfo()
                                        .getSchool_name());
                    }
                    if (resultCode == getActivity().RESULT_CANCELED) {
                        return;
                    }
                } else {
                    if (resultCode == getActivity().RESULT_OK) {
                        if (userHelper.getUser().getType() == UserTypeEnum.PARENTS)
                            startActivityForResult(new Intent(getActivity(),
                                            ChildSelectionActivity.class),
                                    REQUEST_CODE_CHILD_SELECTION);
                        else {
                            ((HomePageFreeVersion) getActivity())
                                    .setActionBarTitle(userHelper.getUser()
                                            .getPaidInfo().getSchool_name());
                            ((HomePageFreeVersion) getActivity())
                                    .loadPaidFragment(PaidVersionHomeFragment
                                            .newInstance(1));
                        }
                    }
                    if (resultCode == getActivity().RESULT_CANCELED) {
                        return;
                    }
                }

                break;
            case FROMSINGLE:

                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void processFetchPost(String url, String categoryIndex,
                                  String subcategoryIndex, boolean refreshFlag) {
        // TODO Auto-generated method stub

        // RequestParams params = new RequestParams();
        Map<String, String> paramMap = new HashMap<String, String>();
        currentCategoryId = categoryIndex;
        // app.showLog("adfsdfs", app.getUserSecret());
        // int resId = Integer.parseInt(categoryIndex);
		/*
		 * if (resId != -1) {
		 * bannerView.setImageResource(AppUtility.getResourceImageId(resId,
		 * false)); }
		 */

        if (UserHelper.isLoggedIn()) {
            // paramMap.put(RequestKeyHelper.USER_ID,
            // UserHelper.getUserFreeId());
            Log.e("USER_FREE_ID", UserHelper.getUserFreeId() + "");
        }

        paramMap.put(RequestKeyHelper.PAGE_NUMBER, pageNumber + "");

        paramMap.put(RequestKeyHelper.PAGE_SIZE, pageSize + "");

        // Log.e("Params", params.toString());

        if (categoryIndex.equals("-1")) {
            if (pageNumber != 1) {
                String str = "";
                ArrayList<FreeVersionPost> tempList = fitnessAdapter.getList();
                for (int i = 0; i < tempList.size(); i++) {
                    // if (tempList.get(i).getPostType().equals("1")) {
                    str += tempList.get(i).getId();
                    if (i != tempList.size() - 1)
                        str += ",";
                    // }
                }
                Log.e("ALREADY SHOWED: ", str);
                paramMap.put(RequestKeyHelper.ALREADY_SHOWED, str);
            }

            switchLayoutSTP.setVisibility(View.VISIBLE);
            switchLayoutOther.setVisibility(View.GONE);
            if (selectedEnum != UserTypeEnum.OTHER)
                paramMap.put("user_type", selectedEnum.ordinal() + "");
            Log.e("SELECTED ENUM", selectedEnum.ordinal() + "");
			/*
			 * AppRestClient.post(URLHelper.URL_FREE_VERSION_HOME, params,
			 * fitnessHandler);
			 */
            getPostFromVolley(
                    AppRestClient
                            .getAbsoluteUrl(URLHelper.URL_FREE_VERSION_HOME),
                    paramMap, refreshFlag);
        } else {
            paramMap.put(RequestKeyHelper.CATEGORY_ID, categoryIndex);
            paramMap.put(RequestKeyHelper.SUBCATEGORY_ID, subcategoryIndex);
            // AppRestClient.post(url, params, fitnessHandler);
            getPostFromVolley(AppRestClient.getAbsoluteUrl(url), paramMap,
                    refreshFlag);
        }

    }

    private void getPostFromVolley(String url, Map<String, String> params,
                                   boolean refreshFlag) {
        // TODO Auto-generated method stub
        if (pageNumber == 1 && isRefreshing == false)
            spinner.setVisibility(View.VISIBLE);
        if (refreshFlag) {
            String temp = url;
            for (Map.Entry<String, String> entry : params.entrySet())
                temp += entry.getKey() + "=" + entry.getValue();
            SchoolApp.getInstance().getRequestQueue().getCache()
                    .invalidate(temp, false);
        }
		/*
		 * String temp = url; for (Map.Entry<String, String> entry :
		 * params.entrySet()) temp += entry.getKey() + "=" + entry.getValue();
		 * Cache cache = SchoolApp.getInstance().getRequestQueue().getCache();
		 * Entry entry = cache.get(temp); if (entry != null) { // fetch the data
		 * from cache try { String data = new String(entry.data, "UTF-8");
		 * Log.e("CACHE", data); parseJsonFeed(data);
		 * 
		 * } catch (UnsupportedEncodingException e) { e.printStackTrace(); }
		 * 
		 * } else {
		 */
        // making fresh volley request and getting json
        SchoolAppPostRequest jsonReq = new SchoolAppPostRequest(Method.POST,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                // TODO Auto-generated method stub
                Log.e("Volley RESPONSE", response);
                spinner.setVisibility(view.GONE);
                parseJsonFeed(response);

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley RESPONSE ERROR", error.toString());
                spinner.setVisibility(view.GONE);
            }
        }, params);
        // Adding request to volley request queue
        if (!params.get(RequestKeyHelper.PAGE_NUMBER).equalsIgnoreCase("1")) {
            jsonReq.setShouldCache(false);
        } else {
            jsonReq.setShouldCache(true);
        }
        SchoolApp.getInstance().addToRequestQueue(jsonReq);

    }

    private void parseJsonFeed(String responseString) {
        spinner.setVisibility(view.GONE);
        if (mContext == null)
            return;
        if (uiHelper.isDialogActive()) {
            uiHelper.dismissLoadingDialog();
        }
		/*
		 * if (fitnessAdapter.getPageNumber() == 1) {
		 * fitnessAdapter.getList().clear(); // setupPoppyView(); }
		 */
        Log.e("Response CATEGORY", responseString.toString());
        // app.showLog("Response", responseString);
        Wrapper modelContainer = GsonParser.getInstance().parseServerResponse(
                responseString);

        if (modelContainer.getStatus().getCode() == 200) {

            hasNext = modelContainer.getData().get("has_next").getAsBoolean();

            if (pageNumber == 1)
                fitnessAdapter.clearList();

            if (!hasNext) {
                // fitnessAdapter.setStopLoadingData(true);
                stopLoadingData = true;
            }

            // fitnessAdapter.getList().addAll();
            ArrayList<FreeVersionPost> allpost = GsonParser.getInstance()
                    .parseFreeVersionPost(
                            modelContainer.getData().getAsJsonArray("post")
                                    .toString());

            if (getArguments().getInt("categoryIndex", 0) == Integer
                    .parseInt(currentCategoryId)) {
                if (modelContainer.getData().has("subcategory")) {
                    allSubcategory = GsonParser.getInstance().parseSubCategory(
                            modelContainer.getData()
                                    .getAsJsonArray("subcategory").toString());
                }
            }

            createCategoryView();

            if (pageNumber == 1)
                allpost.add(0, getBannerViewPost());
            for (int i = 0; i < allpost.size(); i++) {

                if (allpost.get(i).getPostType().equals("2")) {
                    FreeVersionPost p = allpost.get(i);
                    if (!TextUtils.isEmpty(p.getCategory_id_to_use())
                            && p.getImages().size() == 1) {
                        fitnessAdapter.addSingleSeparatorItem(allpost.get(i));
                    } else {
                        fitnessAdapter.addSeparatorItem(allpost.get(i));
                    }

                } else if (allpost.get(i).getPostType().equals("1")) {

                    switch (allpost.get(i).getNormal_post_type()) {
                        case 0:
						/*if(allpost.get(i).getIsSpellingbee().equals("1")){
							fitnessAdapter.addSpellItem(allpost.get(i));
						}else {*/
                            if (allpost.get(i).getImages().size() == 1) {
                                fitnessAdapter.addSampleOriginalImageItem(allpost
                                        .get(i));
                            } else
                                fitnessAdapter.addItem(allpost.get(i));
                            //}


                            break;
                        case 1:
                            fitnessAdapter.addSampleOneItem(allpost.get(i));
                            break;
                        case 2:
                            fitnessAdapter.addSampleTwoItem(allpost.get(i));
                            break;
                        case 3:
                            fitnessAdapter.addSampleThreeItem(allpost.get(i));
                            break;
                        case 4:
                            fitnessAdapter.addSampleFourItem(allpost.get(i));
                            break;
                        case 5:
                            fitnessAdapter.addSampleFiveItem(allpost.get(i));
                            break;
                        case 6:
                            fitnessAdapter.addSampleSixItem(allpost.get(i));
                            break;
                        case 7:
                            fitnessAdapter.addSampleSevenItem(allpost.get(i));
                            break;
                        case 9:
                            fitnessAdapter.addSampleNineItem(allpost.get(i));
                            break;
                        default:
                            break;
                    }
                } else if (allpost.get(i).getPostType().equals("0")) {
                    allpost.get(i).setCategoryId(currentCategoryId);
                    if(currentCategoryId.equals("7")){
                       fitnessAdapter.addSbStaticItem(allpost.get(i));
                    }else fitnessAdapter.addBannerItem(allpost.get(i));
                } else if (allpost.get(i).getPostType().equals("3")) {
                    fitnessAdapter.addSpellItem(allpost.get(i));
                } else if (allpost.get(i).getPostType().equals("4")) {
                    fitnessAdapter.addQuizItem(allpost.get(i));
                }
            }
            fitnessAdapter.notifyDataSetChanged();

            // if (pageNumber != 0 || isRefreshing) {
            listViewFitness.onRefreshComplete();
            loading = false;
            // }
        }
        currentSubcategoryId = "";
    }

    private ArrayList<SubCategory> allSubcategory = new ArrayList<SubCategory>();
    AsyncHttpResponseHandler fitnessHandler = new AsyncHttpResponseHandler() {

        @Override
        public void onFailure(Throwable arg0, String arg1) {
            uiHelper.showMessage(arg1);
            if (uiHelper.isDialogActive()) {
                uiHelper.dismissLoadingDialog();
            }
            currentCategoryId = "";
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
                spinner.setVisibility(view.VISIBLE);
            }
        }

        ;

        @Override
        public void onSuccess(int arg0, String responseString) {
            // parseJsonFeed(new JSONObject(responseString));
        }
    };

    private FreeVersionPost getBannerViewPost() {
        // TODO Auto-generated method stub
        FreeVersionPost post = new FreeVersionPost();
        post.setPostType("0");
        post.setImages(new ArrayList<String>());
        post.setCategoryId(getArguments().getInt("categoryIndex", 0) + "");
        return post;
    }

    private void createCategoryView() {

        if (!allSubcategory.isEmpty()) {
            if (!addedOnce) {

                createTermButtons(allSubcategory);
                if (currentSubcategoryId.equals("")) {
                    CustomTabButton btn = (CustomTabButton) scrollLinearLayout
                            .getChildAt(0);
                    current = btn;

                    setTabSelected(current);

                }
                addedOnce = true;
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void setTabSelected(View v) {
        CustomTabButton btn = (CustomTabButton) v;

        current.setButtonSelected(false,
                mContext.getResources().getColor(R.color.black),
                R.drawable.resource_sub_icon_normal);

        btn.setButtonSelected(true,
                mContext.getResources().getColor(R.color.black),
                R.drawable.resource_sub_icon_tap);
        current = btn;
    }

    private class NoDataTask extends AsyncTask<Void, Void, String[]> {

        @Override
        protected String[] doInBackground(Void... params) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {

            fitnessAdapter.notifyDataSetChanged();

            // Call onRefreshComplete when the list has been refreshed.
            listViewFitness.onRefreshComplete();

            super.onPostExecute(result);
        }
    }

    private class EfficientAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        // ViewHolder holder;

        private ArrayList<FreeVersionPost> list;
        private static final int TYPE_ITEM = 0;
        private static final int TYPE_ADD = 1;
        private static final int TYPE_BANNER = 2;
        private static final int TYPE_SPELLING_GAME = 3;
        private static final int TYPE_QUIZ_GAME = 4;
        private static final int TYPE_ONE = 5;
        private static final int TYPE_TWO = 6;
        private static final int TYPE_THREE = 7;
        private static final int TYPE_FOUR = 8;
        private static final int TYPE_FIVE = 9;
        private static final int TYPE_SIX = 10;
        private static final int TYPE_SEVEN = 11;
        private static final int TYPE_ORIGINAL_IMAGE = 12;
        private static final int TYPE_NINE = 13;
        private static final int TYPE_ADD_SINGLE = 14;
        private static final int TYPE_SB_STATIC = 15;
        private static final int TYPE_MAX_COUNT = 16;
        private TreeSet<Integer> mSeparatorSet = new TreeSet<Integer>();
        private TreeSet<Integer> mSingleSeparatorSet = new TreeSet<Integer>();
        private TreeSet<Integer> mBannerSet = new TreeSet<Integer>();
        private TreeSet<Integer> mSpellSet = new TreeSet<Integer>();
        private TreeSet<Integer> mQuizSet = new TreeSet<Integer>();
        private TreeSet<Integer> mSampleOne = new TreeSet<Integer>();
        private TreeSet<Integer> mSampleTwo = new TreeSet<Integer>();
        private TreeSet<Integer> mSampleThree = new TreeSet<Integer>();
        private TreeSet<Integer> mSampleFour = new TreeSet<Integer>();
        private TreeSet<Integer> mSampleFive = new TreeSet<Integer>();
        private TreeSet<Integer> mSampleSix = new TreeSet<Integer>();
        private TreeSet<Integer> mSampleSeven = new TreeSet<Integer>();
        private TreeSet<Integer> mSampleNine = new TreeSet<Integer>();
        private TreeSet<Integer> mSampleOriginalImage = new TreeSet<Integer>();
        private TreeSet<Integer> mSbStatic = new TreeSet<Integer>();

        public EfficientAdapter(Context context, ArrayList<FreeVersionPost> list) {
            // Cache the LayoutInflate to avoid asking for a new one each time.
            mInflater = LayoutInflater.from(context);
            this.list = list;
        }

        public void clearList() {
            this.list.clear();
            this.mSeparatorSet.clear();
            this.mSingleSeparatorSet.clear();
            this.mBannerSet.clear();
            this.mSpellSet.clear();
            this.mQuizSet.clear();
            this.mSampleOne.clear();
            this.mSampleTwo.clear();
            this.mSampleThree.clear();
            this.mSampleFour.clear();
            this.mSampleFive.clear();
            this.mSampleSix.clear();
            this.mSampleSeven.clear();
            this.mSampleNine.clear();
            this.mSampleOriginalImage.clear();
            this.mSbStatic.clear();

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

        public void addSingleSeparatorItem(final FreeVersionPost item) {
            list.add(item);
            mSingleSeparatorSet.add(list.size() - 1);
        }


        public void addSampleOneItem(final FreeVersionPost item) {
            list.add(item);
            mSampleOne.add(list.size() - 1);
        }

        public void addSampleTwoItem(final FreeVersionPost item) {
            list.add(item);
            mSampleTwo.add(list.size() - 1);
        }

        public void addSampleThreeItem(final FreeVersionPost item) {
            list.add(item);
            mSampleThree.add(list.size() - 1);
        }

        public void addSampleFourItem(final FreeVersionPost item) {
            list.add(item);
            mSampleFour.add(list.size() - 1);
        }

        public void addSampleFiveItem(final FreeVersionPost item) {
            list.add(item);
            mSampleFive.add(list.size() - 1);
        }

        public void addSampleSixItem(final FreeVersionPost item) {
            list.add(item);
            mSampleSix.add(list.size() - 1);
        }

        public void addSampleSevenItem(final FreeVersionPost item) {
            list.add(item);
            mSampleSeven.add(list.size() - 1);
        }

        public void addSampleNineItem(final FreeVersionPost item) {
            list.add(item);
            mSampleNine.add(list.size() - 1);
        }

        public void addSampleOriginalImageItem(final FreeVersionPost item) {
            list.add(item);
            mSampleOriginalImage.add(list.size() - 1);
        }

        public void addSbStaticItem(final FreeVersionPost item) {
            list.add(item);
            mSbStatic.add(list.size() - 1);
        }

        public void addBannerItem(final FreeVersionPost item) {
            list.add(item);
            mBannerSet.add(list.size() - 1);
        }

        public void addSpellItem(final FreeVersionPost item) {
            list.add(item);
            mSpellSet.add(list.size() - 1);
        }

        public void addQuizItem(final FreeVersionPost item) {
            list.add(item);
            mQuizSet.add(list.size() - 1);
        }

        /**
         * The number of items in the list is determined by the number of
         * speeches in our array.
         *
         * @see android.widget.ListAdapter#getCount()
         */
        public int getCount() {
            return list.size();
        }

        /**
         * Since the data comes from an array, just returning the index is
         * sufficent to get at the data. If we were using a more complex data
         * structure, we would return whatever object represents one row in the
         * list.
         *
         * @see android.widget.ListAdapter#getItem(int)
         */
        public FreeVersionPost getItem(int position) {
            return list.get(position);
        }

        /**
         * Use the array index as a unique id.
         *
         * @see android.widget.ListAdapter#getItemId(int)
         */
        public long getItemId(int position) {
            return position;
            // dfsdf
        }

        /**
         * Make a view to hold each row.
         *
         * @see android.widget.ListAdapter#getView(int, android.view.View,
         * android.view.ViewGroup)
         */
        // public View getView(int position, View convertView, ViewGroup parent)
        // {
        //
        //
        // }

        class ViewHolder {
            CustomRhombusIcon imgViewCategoryMenuIcon;
            TextView txtCategoryName;
            TextView txtPublishedDateString;
            PagerContainer container;
            UninterceptableViewPager viewPager;
            TextView txtSummary;
            TextView txtTitle;
            CustomButton btnWow;
            CustomButton btnShare;
            ImagePagerAdapter imgAdapter;
            CustomButton btnReadLater;
            TextView seenTextView;
            ImageView bannerView;
            ImageView gameImage;
            ImageView gameImageIcon;
            LinearLayout pagerBlock;
            LinearLayout textLay;
            LinearLayout wowCommentLayout;
            ResizableImageView singleImage;
            TextView wowCountTextView;
            // TextView commentCountTextView;
            // new variables
            ImageView assesmentIcon;
            TextView tvHomeSummery;
            TextView tvHomeTitle;
            TextView tvHomeAuthorName, tvHomeShortTitle;

            ImageView ivBg;
            ImageView ivCenter;
            ImageView ivHomeImageOne, ivHomeImageTwo, ivHomeImageThree;

            ImageView relHomeAuthorPic;
            ProgressBar pb1, pb2, pb3;
            ProgressBar singlePb;

            ImageButton btnLeader,btnRule, btnPlay;

        }

        @Override
        public int getItemViewType(int position) {
            if (mSeparatorSet.contains(position))
                return TYPE_ADD;
            if (mSingleSeparatorSet.contains(position))
                return TYPE_ADD_SINGLE;
            if (mBannerSet.contains(position))
                return TYPE_BANNER;
            if (mSpellSet.contains(position))
                return TYPE_SPELLING_GAME;
            if (mQuizSet.contains(position))
                return TYPE_QUIZ_GAME;
            if (mSampleOne.contains(position))
                return TYPE_ONE;
            if (mSampleTwo.contains(position))
                return TYPE_TWO;
            if (mSampleThree.contains(position))
                return TYPE_THREE;
            if (mSampleFour.contains(position))
                return TYPE_FOUR;
            if (mSampleFive.contains(position))
                return TYPE_FIVE;
            if (mSampleSix.contains(position))
                return TYPE_SIX;
            if (mSampleSeven.contains(position))
                return TYPE_SEVEN;
            if (mSampleNine.contains(position))
                return TYPE_NINE;
            if (mSampleOriginalImage.contains(position))
                return TYPE_ORIGINAL_IMAGE;
            if (mSbStatic.contains(position))
                return TYPE_SB_STATIC;
            return TYPE_ITEM;

        }

        @Override
        public int getViewTypeCount() {
            // TODO Auto-generated method stub
            return TYPE_MAX_COUNT;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            final int i = position;
            // FreeVersionPost post=list.get(position);
            // A ViewHolder keeps references to children views to avoid
            // unneccessary calls
            // to findViewById() on each row.
            ViewHolder holder = null;
            int type = getItemViewType(position);
            FreeVersionPost mpost = list.get(position);
            // Log.e("TYPETYPE TYPE", "" + type);
            // When convertView is not null, we can reuse it directly, there is
            // no need
            // to reinflate it. We only inflate a new View when the convertView
            // supplied
            // by ListView is null.
            if (convertView == null) {

                // Creates a ViewHolder and store references to the two children
                // views
                // we want to bind data to.
                holder = new ViewHolder();

                switch (type) {
                    case TYPE_ITEM:
                        convertView = mInflater.inflate(
                                R.layout.fragment_freeversion_home_adapter, parent,
                                false);
                        setHolder(holder, convertView, i);

                        holder.container = (PagerContainer) convertView
                                .findViewById(R.id.pager_container_row);
                        holder.viewPager = (UninterceptableViewPager) holder.container
                                .getViewPager();
                        break;
                    case TYPE_ORIGINAL_IMAGE:
                        convertView = mInflater
                                .inflate(
                                        R.layout.fragment_freeversion_sample_original_image,
                                        parent, false);
                        setHolder(holder, convertView, i);
                        holder.singleImage = (ResizableImageView) convertView
                                .findViewById(R.id.iv_single_image);
                        holder.singlePb = (ProgressBar) convertView
                                .findViewById(R.id.mypb);
                        break;
                    case TYPE_SB_STATIC:
                        convertView = mInflater
                                .inflate(
                                        R.layout.spelling_bee_layout_row,
                                        parent, false);
                        holder.btnLeader = (ImageButton) convertView.findViewById(R.id.btnLeaderBoardsb);
                        holder.btnRule = (ImageButton) convertView.findViewById(R.id.btnRules);
                        holder.btnPlay = (ImageButton) convertView.findViewById(R.id.btnSbPlay);
                        break;
                    case TYPE_NINE:
                        convertView = mInflater.inflate(
                                R.layout.fragment_freeversion_sample_education,
                                parent, false);
                        setHolder(holder, convertView, i);
                        holder.relHomeAuthorPic = (ImageView) convertView
                                .findViewById(R.id.imgViewAuth);
                        holder.tvHomeShortTitle = (TextView) convertView
                                .findViewById(R.id.txtAuthName);
                        holder.tvHomeSummery = (TextView) convertView
                                .findViewById(R.id.txtDetails);
                        break;
                    case TYPE_ONE:
                        convertView = mInflater.inflate(
                                R.layout.fragment_freeversion_sample_one, parent,
                                false);
                        setHolder(holder, convertView, i);
                        holder.textLay.setVisibility(View.GONE);
                        holder.tvHomeSummery = (TextView) convertView
                                .findViewById(R.id.tv_home_summery);
                        holder.tvHomeTitle = (TextView) convertView
                                .findViewById(R.id.tv_home_title);
                        holder.tvHomeAuthorName = (TextView) convertView
                                .findViewById(R.id.tv_home_author_name);
                        holder.ivBg = (ImageView) convertView
                                .findViewById(R.id.iv_bg);
                        holder.pb1 = (ProgressBar) convertView
                                .findViewById(R.id.pb1);
                        break;

                    case TYPE_TWO:
                        convertView = mInflater.inflate(
                                R.layout.fragment_freeversion_sample_two, parent,
                                false);
                        setHolder(holder, convertView, i);
                        holder.textLay.setVisibility(View.GONE);
                        holder.ivBg = (ImageView) convertView
                                .findViewById(R.id.iv_bg);
                        holder.ivCenter = (ImageView) convertView
                                .findViewById(R.id.iv_center);
                        holder.tvHomeSummery = (TextView) convertView
                                .findViewById(R.id.tv_home_summery);
                        holder.pb1 = (ProgressBar) convertView
                                .findViewById(R.id.pb1);
                        break;

                    case TYPE_THREE:
                        convertView = mInflater.inflate(
                                R.layout.fragment_freeversion_sample_three, parent,
                                false);
                        setHolder(holder, convertView, i);
                        holder.ivHomeImageOne = (ImageView) convertView
                                .findViewById(R.id.iv_home_img1);
                        holder.ivHomeImageTwo = (ImageView) convertView
                                .findViewById(R.id.iv_home_img2);
                        holder.ivHomeImageThree = (ImageView) convertView
                                .findViewById(R.id.iv_home_img3);
                        holder.pb1 = (ProgressBar) convertView
                                .findViewById(R.id.pb1);
                        holder.pb2 = (ProgressBar) convertView
                                .findViewById(R.id.pb2);
                        holder.pb3 = (ProgressBar) convertView
                                .findViewById(R.id.pb3);
                        break;

                    case TYPE_FOUR:

                        convertView = mInflater.inflate(
                                R.layout.fragment_freeversion_sample_four, parent,
                                false);
                        setHolder(holder, convertView, i);
                        holder.textLay.setVisibility(View.GONE);
                        holder.relHomeAuthorPic = (ImageView) convertView
                                .findViewById(R.id.rel_home_author_pic);
                        holder.tvHomeShortTitle = (TextView) convertView
                                .findViewById(R.id.tv_home_short_title);
                        holder.tvHomeTitle = (TextView) convertView
                                .findViewById(R.id.tv_home_title);
                        holder.tvHomeSummery = (TextView) convertView
                                .findViewById(R.id.tv_home_summery);
                        break;

                    case TYPE_FIVE:

                        convertView = mInflater.inflate(
                                R.layout.fragment_freeversion_sample_five, parent,
                                false);
                        setHolder(holder, convertView, i);
                        holder.textLay.setVisibility(View.GONE);
                        holder.ivBg = (ImageView) convertView
                                .findViewById(R.id.iv_bg);
                        holder.tvHomeShortTitle = (TextView) convertView
                                .findViewById(R.id.tv_home_short_title);
                        holder.pb1 = (ProgressBar) convertView
                                .findViewById(R.id.pb1);

                        break;

                    case TYPE_SIX:
                        convertView = mInflater.inflate(
                                R.layout.fragment_freeversion_sample_six, parent,
                                false);
                        setHolder(holder, convertView, i);
                        holder.textLay.setVisibility(View.GONE);
                        holder.tvHomeTitle = (TextView) convertView
                                .findViewById(R.id.tv_home_title);
                        holder.tvHomeShortTitle = (TextView) convertView
                                .findViewById(R.id.tv_home_short_title);
                        holder.ivHomeImageOne = (ImageView) convertView
                                .findViewById(R.id.iv_home_img1);
                        holder.ivHomeImageTwo = (ImageView) convertView
                                .findViewById(R.id.iv_home_img2);
                        holder.pb1 = (ProgressBar) convertView
                                .findViewById(R.id.pb1);
                        holder.pb2 = (ProgressBar) convertView
                                .findViewById(R.id.pb2);
                        break;

                    case TYPE_SEVEN:
                        convertView = mInflater.inflate(
                                R.layout.fragment_freeversion_sample_seven, parent,
                                false);
                        setHolder(holder, convertView, i);
                        holder.textLay.setVisibility(View.GONE);
                        holder.ivHomeImageOne = (ImageView) convertView
                                .findViewById(R.id.iv_home_img1);
                        holder.tvHomeShortTitle = (TextView) convertView
                                .findViewById(R.id.tv_home_short_title);
                        holder.tvHomeSummery = (TextView) convertView
                                .findViewById(R.id.tv_home_summery);
                        holder.pb1 = (ProgressBar) convertView
                                .findViewById(R.id.pb1);
                        break;
                    case TYPE_ADD_SINGLE:
                        convertView = mInflater.inflate(
                                R.layout.fragment_freeversion_single_image, parent,
                                false);
                        holder.singleImage = (ResizableImageView) convertView
                                .findViewById(R.id.iv_single_image);
                        holder.singlePb = (ProgressBar) convertView
                                .findViewById(R.id.mypb);
                        holder.pagerBlock = (LinearLayout) convertView
                                .findViewById(R.id.middle);
                        break;
                    case TYPE_ADD:

                        convertView = mInflater.inflate(R.layout.pager_add_view,
                                parent, false);
                        holder.container = (PagerContainer) convertView
                                .findViewById(R.id.pager_container_row);
                        holder.viewPager = (UninterceptableViewPager) holder.container
                                .getViewPager();

                        break;
                    case TYPE_BANNER:

                        convertView = mInflater.inflate(R.layout.banner_view,
                                parent, false);
                        holder.bannerView = (ImageView) convertView
                                .findViewById(R.id.iv_banner);

                        break;
                    case TYPE_SPELLING_GAME:

                        convertView = mInflater.inflate(R.layout.spelling_view,
                                parent, false);
                        holder.gameImage = (ImageView) convertView
                                .findViewById(R.id.post_iv_game_banner);
                        holder.gameImageIcon = (ImageView) convertView
                                .findViewById(R.id.post_iv_game_icon);
                        holder.gameImage
                                .setImageResource(R.drawable.words_of_the_day);
                        holder.gameImageIcon
                                .setImageResource(R.drawable.words_of_the_day_icon);
                        break;

                    case TYPE_QUIZ_GAME:
                        convertView = mInflater.inflate(R.layout.spelling_view,
                                parent, false);
                        holder.gameImage = (ImageView) convertView
                                .findViewById(R.id.post_iv_game_banner);
                        holder.gameImageIcon = (ImageView) convertView
                                .findViewById(R.id.post_iv_game_icon);
                        holder.gameImage
                                .setImageResource(R.drawable.dailyquiz_banner);
                        holder.gameImageIcon.setImageResource(R.drawable.quiz_icon);

                        break;
                    default:
                        break;
                }

				/*
				 * holder.container = (PagerContainer) convertView
				 * .findViewById(R.id.pager_container_row); holder.viewPager =
				 * (UninterceptableViewPager) holder.container .getViewPager();
				 */
                // holder.viewPager.setPageMargin(-10);

                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            int resId = Integer.parseInt(list.get(position).getCategoryId());
            if (list.size() > 0) {
                FreeVersionPost itm = list.get(position);
                switch (type) {
                    case TYPE_ITEM:
                        // if (list.get(position).getCategoryMenuIconUrl() != null
                        // || list.get(position).getCategoryMenuIconUrl()
                        // .length() > 0)
                        // SchoolApp.getInstance().displayUniversalImage(
                        // list.get(position).getCategoryMenuIconUrl(),
                        // holder.imgViewCategoryMenuIcon);

                        setDataToView(holder, resId, position);

                        if (list.get(position).getImages().size() == 0) {
                            holder.pagerBlock.setVisibility(View.GONE);
                            holder.txtTitle.setPadding(0, 20, 0, 0);
                        } else
                            holder.pagerBlock.setVisibility(View.VISIBLE);
                        if (!list.get(position).getAssessment_id().equals("")) {
                            holder.assesmentIcon.setVisibility(View.VISIBLE);
                            holder.assesmentIcon.setTag("" + position);
                            holder.assesmentIcon
                                    .setOnClickListener(CommonChildFragment.this);
                        } else
                            holder.assesmentIcon.setVisibility(View.GONE);
                        setAddAndPostPager(holder.imgAdapter, holder.viewPager,
                                position);
                        break;
                    case TYPE_ORIGINAL_IMAGE:
                        if (list.get(position).getImages().size() == 0) {
                            holder.pagerBlock.setVisibility(View.GONE);
                            holder.txtTitle.setPadding(0, 20, 0, 0);
                        } else
                            holder.pagerBlock.setVisibility(View.VISIBLE);

                        holder.pagerBlock
                                .setOnClickListener(CommonChildFragment.this);
                        setDataToView(holder, resId, position);
                        SchoolApp.getInstance().displayUniversalImageSingle(
                                itm.getImages().get(0), holder.singleImage,
                                holder.singlePb);
                        if (!list.get(position).getAssessment_id().equals("")) {
                            holder.assesmentIcon.setVisibility(View.VISIBLE);
                            holder.assesmentIcon.setTag("" + position);
                            holder.assesmentIcon
                                    .setOnClickListener(CommonChildFragment.this);
                        } else
                            holder.assesmentIcon.setVisibility(View.GONE);

                        break;
                    case TYPE_SB_STATIC:
                        if(userHelper.isLoggedIn())holder.btnPlay.setBackgroundResource(R.drawable.spellingbee_play_btn);
                        else holder.btnPlay.setBackgroundResource(R.drawable.spellingbee_join);

                        holder.btnLeader.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                startActivity(new Intent(mContext,
                                        LeaderBoardActivity.class));
                            }
                        });
                        holder.btnRule.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                startActivity(new Intent(mContext,
                                        SpellingbeeRulesActivity.class));
                            }
                        });
                        holder.btnPlay.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (UserHelper.isLoggedIn()){
                                    if(userHelper.getUser().getType()==UserTypeEnum.STUDENT){
                                        if(UserHelper.getSpellingStatus()==0){
                                            Intent i = new Intent(mContext,
                                                    CompleteProfileActivityContainer.class);
                                            i.putExtra(SPKeyHelper.USER_TYPE, userHelper.getUser().getType().ordinal());
                                            startActivity(i);
                                        }else {
                                            startActivity(new Intent(mContext,
                                                    SpellingbeeTestActivity.class));
                                        }
                                    }else {
                                        startActivity(new Intent(mContext,
                                                SpellingbeeTestActivity.class));
                                    }

                                }
                                else {
                                    startActivity(new Intent(mContext,
                                            LoginActivity.class));
                                }
                            }
                        });
                        break;
                    case TYPE_ONE:
                        setDataToView(holder, resId, position);
                        holder.pagerBlock
                                .setOnClickListener(CommonChildFragment.this);
                        // summery,title,author,ibg
                        holder.tvHomeSummery.setText(itm.getSummary());
                        holder.tvHomeTitle.setText(itm.getTitle());
                        if (!TextUtils.isEmpty(itm.getAuthor()))
                            holder.tvHomeAuthorName.setText(itm.getAuthor());
                        if (itm.getImages().size() > 0)
                            SchoolApp.getInstance()
                                    .displayUniversalImage(itm.getImages().get(0),
                                            holder.ivBg, holder.pb1);
                        if (!list.get(position).getAssessment_id().equals("")) {
                            holder.assesmentIcon.setVisibility(View.VISIBLE);
                            holder.assesmentIcon.setTag("" + position);
                            holder.assesmentIcon
                                    .setOnClickListener(CommonChildFragment.this);
                        } else
                            holder.assesmentIcon.setVisibility(View.GONE);
                        break;

                    case TYPE_TWO:
                        setDataToView(holder, resId, position);
                        holder.pagerBlock
                                .setOnClickListener(CommonChildFragment.this);
                        if (itm.getImages().size() > 0)
                            SchoolApp.getInstance()
                                    .displayUniversalImage(itm.getImages().get(0),
                                            holder.ivBg, holder.pb1);
                        if (!TextUtils.isEmpty(itm.getInside_image()))
                            SchoolApp.getInstance().displayUniversalImage(
                                    itm.getInside_image(), holder.ivCenter);
                        holder.tvHomeSummery.setText(itm.getSummary());
                        if (!list.get(position).getAssessment_id().equals("")) {
                            holder.assesmentIcon.setVisibility(View.VISIBLE);
                            holder.assesmentIcon.setTag("" + position);
                            holder.assesmentIcon
                                    .setOnClickListener(CommonChildFragment.this);
                        } else
                            holder.assesmentIcon.setVisibility(View.GONE);
                        break;

                    case TYPE_THREE:
                        setDataToView(holder, resId, position);
                        holder.pagerBlock
                                .setOnClickListener(CommonChildFragment.this);

                        if (itm.getImages().size() >= 3) {
                            SchoolApp.getInstance().displayUniversalImage(
                                    itm.getImages().get(0), holder.ivHomeImageOne,
                                    holder.pb1);
                            SchoolApp.getInstance().displayUniversalImage(
                                    itm.getImages().get(1), holder.ivHomeImageTwo,
                                    holder.pb2);
                            SchoolApp.getInstance().displayUniversalImage(
                                    itm.getImages().get(2),
                                    holder.ivHomeImageThree, holder.pb3);
                        }
                        if (!list.get(position).getAssessment_id().equals("")) {
                            holder.assesmentIcon.setVisibility(View.VISIBLE);
                            holder.assesmentIcon.setTag("" + position);
                            holder.assesmentIcon
                                    .setOnClickListener(CommonChildFragment.this);
                        } else
                            holder.assesmentIcon.setVisibility(View.GONE);
                        break;

                    case TYPE_FOUR:
                        setDataToView(holder, resId, position);
                        holder.pagerBlock
                                .setOnClickListener(CommonChildFragment.this);
                        if (!TextUtils.isEmpty(itm.getAuthor_image()))
                            SchoolApp.getInstance().displayUniversalImage(
                                    itm.getAuthor_image(), holder.relHomeAuthorPic);
                        holder.tvHomeShortTitle.setText(itm.getShort_title());
                        holder.tvHomeTitle.setText(itm.getTitle());
                        holder.tvHomeSummery.setText(itm.getSummary());
                        if (!list.get(position).getAssessment_id().equals("")) {
                            holder.assesmentIcon.setVisibility(View.VISIBLE);
                            holder.assesmentIcon.setTag("" + position);
                            holder.assesmentIcon
                                    .setOnClickListener(CommonChildFragment.this);
                        } else
                            holder.assesmentIcon.setVisibility(View.GONE);
                        break;

                    case TYPE_FIVE:
                        setDataToView(holder, resId, position);
                        holder.pagerBlock
                                .setOnClickListener(CommonChildFragment.this);
                        if (itm.getImages().size() > 0)
                            SchoolApp.getInstance()
                                    .displayUniversalImage(itm.getImages().get(0),
                                            holder.ivBg, holder.pb1);
                        holder.tvHomeShortTitle.setText(itm.getShort_title());
                        if (!list.get(position).getAssessment_id().equals("")) {
                            holder.assesmentIcon.setVisibility(View.VISIBLE);
                            holder.assesmentIcon.setTag("" + position);
                            holder.assesmentIcon
                                    .setOnClickListener(CommonChildFragment.this);
                        } else
                            holder.assesmentIcon.setVisibility(View.GONE);
                        break;

                    case TYPE_SIX:
                        setDataToView(holder, resId, position);
                        holder.pagerBlock
                                .setOnClickListener(CommonChildFragment.this);
                        holder.tvHomeShortTitle.setText(itm.getShort_title());
                        holder.tvHomeTitle.setText(itm.getTitle());
                        holder.textLay.setVisibility(View.GONE);
                        if (itm.getImages().size() >= 2) {
                            SchoolApp.getInstance().displayUniversalImage(
                                    itm.getImages().get(0), holder.ivHomeImageOne,
                                    holder.pb1);
                            SchoolApp.getInstance().displayUniversalImage(
                                    itm.getImages().get(1), holder.ivHomeImageTwo,
                                    holder.pb2);
                        }
                        if (!list.get(position).getAssessment_id().equals("")) {
                            holder.assesmentIcon.setVisibility(View.VISIBLE);
                            holder.assesmentIcon.setTag("" + position);
                            holder.assesmentIcon
                                    .setOnClickListener(CommonChildFragment.this);
                        } else
                            holder.assesmentIcon.setVisibility(View.GONE);
                        break;

                    case TYPE_SEVEN:
                        holder.pagerBlock
                                .setOnClickListener(CommonChildFragment.this);
                        setDataToView(holder, resId, position);
                        holder.tvHomeSummery.setText(itm.getSummary());
                        holder.tvHomeShortTitle.setText(itm.getShort_title());
                        if (itm.getImages().size() > 0) {
                            SchoolApp.getInstance().displayUniversalImage(
                                    itm.getImages().get(0), holder.ivHomeImageOne,
                                    holder.pb1);
                        }
                        if (!list.get(position).getAssessment_id().equals("")) {
                            holder.assesmentIcon.setVisibility(View.VISIBLE);
                            holder.assesmentIcon.setTag("" + position);
                            holder.assesmentIcon
                                    .setOnClickListener(CommonChildFragment.this);
                        } else
                            holder.assesmentIcon.setVisibility(View.GONE);
                        break;
                    case TYPE_NINE:
                        holder.pagerBlock
                                .setOnClickListener(CommonChildFragment.this);
                        setDataToView(holder, resId, position);

                        if (!TextUtils.isEmpty(itm.getAuthor_image()))
                            SchoolApp.getInstance().displayUniversalImage(
                                    itm.getAuthor_image(), holder.relHomeAuthorPic);
                        holder.tvHomeShortTitle.setText(itm.getAuthor());
                        holder.tvHomeSummery.setText(itm.getFullContent());
                        break;
                    case TYPE_ADD_SINGLE:
                        SchoolApp.getInstance().displayUniversalImageSingle(
                                itm.getImages().get(0), holder.singleImage,
                                holder.singlePb);
                        holder.pagerBlock.setTag("" + position);
                        holder.pagerBlock
                                .setOnClickListener(CommonChildFragment.this);
                        break;

                    case TYPE_ADD:
                        setAddAndPostPager(holder.imgAdapter, holder.viewPager,
                                position);
                        break;

                    case TYPE_BANNER:
					/*
					 * holder.bannerView.setImageResource(AppUtility
					 * .getResourceImageId(resId, false, false));
					 */
                        // Uri uri = Uri.parse("drawable://" +
                        // AppUtility.getResourceImageId(resId, false, false));
                        holder.bannerView.setTag(""+position);
                        holder.bannerView.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if(list.get(Integer.parseInt(view.getTag().toString())).getCategoryId().equals("74")){
                                        if (UserHelper.isLoggedIn())
                                            startActivity(new Intent(mContext,
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
                                    }
                                }
                        });

                        imageLoader.displayImage(
                                "drawable://"
                                        + AppUtility.getResourceImageId(resId,
                                        false, false), holder.bannerView);
                        break;
                    default:

                        break;
                }
                // Log.e("PUBLISHED_DATE", "is: "
                // + list.get(position).getPublishedDateString());
            } else {
                // Log.e("FREE_HOME_API", "array is empty!");
            }

            return convertView;
        }

        private void setDataToView(ViewHolder holder, int resId, int position) {
            // TODO Auto-generated method stub
            if (resId != -1) {
                Log.e("resource id", " " + resId);
                holder.imgViewCategoryMenuIcon.setIconImage(AppUtility
                        .getResourceImageId(resId, true, false));

            }
            holder.txtCategoryName
                    .setText(list.get(position).getCategoryName());
            holder.txtPublishedDateString.setText(list.get(position)
                    .getPublishedDateString());
            holder.txtSummary.setText(list.get(position).getSummary());
            holder.txtTitle.setText(list.get(position).getTitle());
            if (Integer.parseInt(list.get(position).getSeenCount()) >= 1000)
                holder.seenTextView.setText(AppUtility.coolFormat(
                        Double.parseDouble(list.get(position).getSeenCount()),
                        0));
            else
                holder.seenTextView.setText(list.get(position).getSeenCount());

            if (list.get(position).getWow_count().equals("0")) {
                holder.wowCountTextView.setVisibility(View.GONE);
            } else
                holder.wowCountTextView.setVisibility(View.VISIBLE);

            holder.wowCountTextView.setText(list.get(position).getWow_count()
                    + " WoW");
            // holder.commentCountTextView.setText(list.get(position).)
            holder.txtSummary.setTag("" + position);
            holder.pagerBlock.setTag("" + position);
            holder.btnShare.setTag("" + position);

            holder.wowCommentLayout.setTag("" + position);
            holder.wowCountTextView.setTag("" + position);
            // holder.commentCountTextView.setTag(""+position);

            holder.btnReadLater.setTag("" + position);
            holder.btnWow.setTag("" + position);
			/*
			 * if (list.get(position).getCan_wow() == 0) {
			 * holder.btnWow.setTitleColor(getResources()
			 * .getColor(R.color.red));
			 * holder.btnWow.setImage(R.drawable.wow_icon_tap); } else {
			 * holder.btnWow.setTitleColor(getResources().getColor(
			 * R.color.black));
			 * holder.btnWow.setImage(R.drawable.wow_icon_normal); }
			 */
        }

        private void setHolder(ViewHolder holder, View convertView, final int i) {
            // TODO Auto-generated method stub
            holder.textLay = (LinearLayout) convertView
                    .findViewById(R.id.textlay);
            holder.textLay.setVisibility(View.VISIBLE);
            holder.imgViewCategoryMenuIcon = (CustomRhombusIcon) convertView
                    .findViewById(R.id.imgViewCategoryMenuIcon);
            holder.txtCategoryName = (TextView) convertView
                    .findViewById(R.id.txtCategoryName);
            holder.txtPublishedDateString = (TextView) convertView
                    .findViewById(R.id.txtPublishedDateString);
            holder.assesmentIcon = (ImageView) convertView
                    .findViewById(R.id.iv_assesment_icon);
            holder.txtSummary = (TextView) convertView
                    .findViewById(R.id.txtSummary);
            holder.txtTitle = (TextView) convertView
                    .findViewById(R.id.txtHeading);
            holder.pagerBlock = (LinearLayout) convertView
                    .findViewById(R.id.middle);
            holder.txtSummary.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    int pos = Integer.parseInt(v.getTag().toString());
                    View mother = (View) v.getParent().getParent().getParent();
                    FreeVersionPost item = (FreeVersionPost) fitnessAdapter
                            .getItem(pos);

                    // assesment

                    if (item.getForceAssessment().equalsIgnoreCase("1")) {
                        Intent intent = new Intent(mContext, AssesmentActivity.class);
                        startActivity(intent);
                    } else {
                        if (item.getPostType().equals("1")) {
                            Intent intent = new Intent(mContext, SingleItemShowActivity.class);
                            intent.putExtra(AppConstant.ITEM_ID, item.getId());
                            intent.putExtra(AppConstant.ITEM_CAT_ID,
                                    item.getCategoryId());
                            startActivityForResult(intent, REQUEST_CODE_CHILD_SELECTION);
                            item.setSeenCount((1 + Integer.parseInt(item
                                    .getSeenCount()) + ""));
							/*
							 * if (Integer.parseInt(fitnessAdapter.getItem(pos)
							 * .getSeenCount()) < 1000) { TextView tv =
							 * (TextView) mother
							 * .findViewById(R.id.fv_post_tv_seen); // int kk =
							 * item.getSeenCount();
							 * tv.setText(item.getCoolFormatSeenCount()); }
							 */
                            fitnessAdapter.notifyDataSetChanged();
                        }
                    }
                }
            });
            holder.btnWow = (CustomButton) convertView
                    .findViewById(R.id.btnWow);
            holder.wowCommentLayout = (LinearLayout) convertView
                    .findViewById(R.id.wow_comment_layout);
            holder.wowCountTextView = (TextView) convertView
                    .findViewById(R.id.wow_count);
            // holder.commentCountTextView = (TextView)
            // convertView.findViewById(R.id.comment_count);
			/*
			 * holder.wowCommentLayout.setOnClickListener(new OnClickListener()
			 * {
			 * 
			 * @Override public void onClick(View v) { int pos =
			 * Integer.parseInt(v.getTag().toString()); CommentDialog dialog =
			 * CommentDialog.newInstance(Integer
			 * .parseInt(fitnessAdapter.getItem(pos).getId()), "1"
			 * .equals(fitnessAdapter.getItem(pos) .getCan_comment()));
			 * dialog.show(getChildFragmentManager(), null);
			 * 
			 * } });
			 */
            holder.btnWow.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // if (UserHelper.isLoggedIn()) {
                    doWow(Integer.parseInt(v.getTag().toString()));
					/*
					 * CustomButton w = (CustomButton) v;
					 * w.setTitleColor(getResources().getColor(R.color.red));
					 * w.setImage(R.drawable.wow_icon_tap);
					 */
                    View mother = (View) v.getParent().getParent().getParent()
                            .getParent();
                    TextView count = (TextView) mother
                            .findViewById(R.id.wow_count);
                    int n = Integer.parseInt(count.getText().toString()
                            .split(" ")[0]);
                    if (n == 0)
                        count.setVisibility(View.VISIBLE);
                    count.setText((n + 1) + " WoW");

					/*
					 * } else showCustomDialog( "WOW", R.drawable.wow_red,
					 * getResources().getString(R.string.wow_msg) + "\n" +
					 * getResources().getString( R.string.not_logged_in_msg));
					 */

                }
            });

            holder.btnShare = (CustomButton) convertView
                    .findViewById(R.id.btnShare);
            holder.btnShare.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (AppUtility.isInternetConnected()) {
                        ((HomeContainerActivity) mContext)
                                .sharePostUniversal(list.get(Integer.parseInt(v
                                        .getTag().toString())));
                    }
                }
            });
            holder.seenTextView = (TextView) convertView
                    .findViewById(R.id.fv_post_tv_seen);
            holder.btnReadLater = (CustomButton) convertView
                    .findViewById(R.id.btnReadLater);
            holder.btnReadLater.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (UserHelper.isLoggedIn())
                        doReadLater(Integer.parseInt(v.getTag().toString()));
                    else
                        showCustomDialog(
                                "READ LATER",
                                R.drawable.read_later_red_icon,
                                getResources().getString(
                                        R.string.read_later_msg)
                                        + "\n"
                                        + getResources().getString(
                                        R.string.not_logged_in_msg));
                }
            });
        }

        public void setAddAndPostPager(ImagePagerAdapter imgAdapter,
                                       UninterceptableViewPager viewPager, int position) {
            ArrayList<String> imgPaths;
            ArrayList<AddData> addData;
            switch (Integer.parseInt(list.get(position).getPostType())) {
                case 2:
                    addData = list.get(position).getAdd_images();
                    imgAdapter = new ImagePagerAdapter(addData);
                    break;
                case 1:
                    imgPaths = list.get(position).getImages();
                    imgAdapter = new ImagePagerAdapter(imgPaths, 1);
                    break;
                default:
                    break;
            }

            // Log.e("IMAGE Category ID", list.get(position).getPostType()
            // + "");

            // If hardware acceleration is enabled, you should also remove
            // clipping on the pager for its children.

            viewPager.setAdapter(imgAdapter);
            viewPager.setTag(position + "");
            viewPager.setOffscreenPageLimit(imgAdapter.getCount());
            // A little space between pages
            viewPager.setPageMargin(-10);

            // If hardware acceleration is enabled, you should also remove
            // clipping on the pager for its children.
            viewPager.setClipChildren(false);
            viewPager.setOnTouchListener(new OnTouchListener() {
                private float pointX;
                private float pointY;
                private int tolerance = 20;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_MOVE:
                            return false; // This is important, if you return
                        // TRUE the action of swipe will not
                        // take place.
                        case MotionEvent.ACTION_DOWN:
                            pointX = event.getX();
                            pointY = event.getY();
                            break;
                        case MotionEvent.ACTION_UP:
                            boolean sameX = pointX + tolerance > event.getX()
                                    && pointX - tolerance < event.getX();
                            boolean sameY = pointY + tolerance > event.getY()
                                    && pointY - tolerance < event.getY();
                            if (sameX && sameY) {
                                int pos = Integer.parseInt(v.getTag().toString());
                                View mother = (View) v.getParent().getParent()
                                        .getParent().getParent();
                                FreeVersionPost item = (FreeVersionPost) fitnessAdapter
                                        .getItem(pos);

                                // assesment

                                if (item.getForceAssessment().equalsIgnoreCase("1")) {
                                    Intent intent = new Intent(
                                            mContext,
                                            AssesmentActivity.class);
                                    startActivity(intent);
                                } else {
                                    if (item.getPostType().equals("1")) {

                                        if (!TextUtils.isEmpty(item
                                                .getEmbeddedUrl())) {
                                            String string = item.getEmbeddedUrl()
                                                    .replace("embed/", "watch?v=");

                                            startActivity(new Intent(
                                                    Intent.ACTION_VIEW,
                                                    Uri.parse("http:" + string)));
                                            // startActivity(new
                                            // Intent(Intent.ACTION_VIEW,
                                            // Uri.parse(item.getEmbeddedUrl())));

                                        } else {
                                            Intent intent = new Intent(
                                                    mContext,
                                                    SingleItemShowActivity.class);
                                            intent.putExtra(AppConstant.ITEM_ID,
                                                    item.getId());
                                            intent.putExtra(
                                                    AppConstant.ITEM_CAT_ID,
                                                    item.getCategoryId());
                                            startActivityForResult(intent,
                                                    REQUEST_CODE_CHILD_SELECTION);
                                            item.setSeenCount((1 + Integer
                                                    .parseInt(item.getSeenCount()) + ""));
										/*
										 * if (Integer.parseInt(fitnessAdapter
										 * .getItem(pos).getSeenCount()) < 1000)
										 * { TextView tv = (TextView) mother
										 * .findViewById(R.id.fv_post_tv_seen);
										 * // int kk = item.getSeenCount();
										 * tv.setText(item
										 * .getCoolFormatSeenCount()); }
										 */
                                            fitnessAdapter.notifyDataSetChanged();

                                        }

                                    } else if (item.getPostType().equals("2")) {
                                        if (!TextUtils.isEmpty(item
                                                .getCategory_id_to_use())) {
                                            ((HomePageFreeVersion) mContext).loadCategory(
                                                    Integer.parseInt(item
                                                            .getCategory_id_to_use()),
                                                    item.getSubcategory_id_to_use());
                                        } else {

                                            ViewPager vp = (ViewPager) v;
                                            AddData adata = item.getAdd_images()
                                                    .get(vp.getCurrentItem());

                                            if(!TextUtils.isEmpty(adata.getAd_image_category()) && !TextUtils.isEmpty(adata.getAd_image_subcategory()) && !adata.getAd_image_category().equals("0")) {
                                                ((HomePageFreeVersion) mContext).loadCategory(
                                                        Integer.parseInt(adata.getAd_image_category()),
                                                        adata.getAd_image_subcategory());
                                            } else {
                                                Intent i = new Intent(
                                                        Intent.ACTION_VIEW);
                                                i.setData(Uri.parse(item
                                                        .getAdd_images()
                                                        .get(vp.getCurrentItem())
                                                        .getAd_image_link()));
                                                if (!TextUtils.isEmpty(item
                                                        .getAdd_images()
                                                        .get(vp.getCurrentItem())
                                                        .getAd_image_link())) {
                                                    startActivity(i);
                                            }

                                            }
                                        }

                                        // startActivity(i);
                                    }
                                }

                            }
                    }
                    return false;
                }
            });
        }

        public ArrayList<FreeVersionPost> getList() {
            return list;
        }

    }

    @Override
    public void onAuthenticationStart() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onAuthenticationSuccessful() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onAuthenticationFailed(String msg) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        int state;
        switch (v.getId()) {
            case R.id.btn_home_home:
                state = (Integer) v.getTag();
                if (state == 0) {
                    deactiveAllButton();
                    mainHome.setImageResource(R.drawable.home_tap);
                    mainHome.setTag(1);
                    selectedEnum = UserTypeEnum.OTHER;
                    ((HomePageFreeVersion) mContext).loadHome();

                }
                break;
            case R.id.btn_student_student:
                state = (Integer) v.getTag();
                if (state == 0) {
                    deactiveAllButton();
                    mainStudent.setImageResource(R.drawable.top_bar_student_tap);
                    mainStudent.setTag(1);
                    selectedEnum = UserTypeEnum.STUDENT;
                    refreshListWithSelectedCategory("-1");
                }
                break;
            case R.id.btn_parent_parent:
                state = (Integer) v.getTag();
                if (state == 0) {
                    deactiveAllButton();
                    mainParent.setImageResource(R.drawable.top_bar_parents_tap);
                    mainParent.setTag(1);
                    selectedEnum = UserTypeEnum.PARENTS;
                    refreshListWithSelectedCategory("-1");
                }
                break;
            case R.id.btn_teacher_teacher:
                state = (Integer) v.getTag();
                if (state == 0) {
                    deactiveAllButton();
                    mainTeacher.setImageResource(R.drawable.top_bar_teacher_tap);
                    mainTeacher.setTag(1);
                    selectedEnum = UserTypeEnum.TEACHER;
                    refreshListWithSelectedCategory("-1");
                }
                break;
            case R.id.middle:
            case R.id.iv_assesment_icon:
                View mother = (View) v.getParent().getParent().getParent()
                        .getParent();
                FreeVersionPost item = (FreeVersionPost) fitnessAdapter
                        .getItem(Integer.parseInt(v.getTag().toString()));

                if (item.getPostType().equals("1")) {
                    Intent intent = new Intent(mContext,
                            SingleItemShowActivity.class);
                    intent.putExtra(AppConstant.ITEM_ID, item.getId());
                    intent.putExtra(AppConstant.ITEM_CAT_ID, item.getCategoryId());
                    startActivityForResult(intent, REQUEST_CODE_CHILD_SELECTION);

                    item.setSeenCount((1 + Integer.parseInt(item.getSeenCount()) + ""));
				/*
				 * if (Integer.parseInt(item.getSeenCount()) < 1000) { TextView
				 * tv = (TextView) mother .findViewById(R.id.fv_post_tv_seen);
				 * // int kk = item.getSeenCount();
				 * tv.setText(item.getCoolFormatSeenCount()); }
				 */
                    fitnessAdapter.notifyDataSetChanged();
                } else if (item.getPostType().equals("2")) {
                    if (!TextUtils.isEmpty(item.getCategory_id_to_use()))
                        ((HomePageFreeVersion) mContext).loadCategory(
                                Integer.parseInt(item.getCategory_id_to_use()),
                                item.getSubcategory_id_to_use());
                }
                break;

            default:
                CustomTabButton btn = (CustomTabButton) v;
                fitnessAdapter.clearList();
                fitnessAdapter.notifyDataSetChanged();
                processFetchPost(URLHelper.URL_FREE_VERSION_CATEGORY, btn.getTag()
                        .toString().trim(), "", false);
                initializePageing();

                Log.e("BUtton CLICKED", "id: " + btn.getTag().toString().trim());

			/*
			 * if (btn.getTag().toString().trim().equals("25")) { Intent intent
			 * = new Intent(getActivity(), WordsOfTheDayMainActivity.class);
			 * startActivity(intent); }
			 */

                setTabSelected(btn);
                break;
        }

    }

    private void refreshListWithSelectedCategory(String cat_index) {
        fitnessAdapter.clearList();
        fitnessAdapter.notifyDataSetChanged();
        processFetchPost(URLHelper.URL_FREE_VERSION_CATEGORY, cat_index, "",
                false);
        initializePageing();
    }

    private void doReadLater(int i) {
        // TODO Auto-generated method stub
        RequestParams params = new RequestParams();

        params.put(RequestKeyHelper.USER_ID, UserHelper.getUserFreeId());
        params.put(RequestKeyHelper.POST_ID, fitnessAdapter.getList().get(i)
                .getId());

        AppRestClient.post(URLHelper.URL_FREE_VERSION_READLATER, params,
                readLaterHandler);
    }

    private void doWow(int i) {
        // TODO Auto-generated method stub
        RequestParams params = new RequestParams();
        FreeVersionPost p = fitnessAdapter.getList().get(i);
        // params.put(RequestKeyHelper.USER_ID, UserHelper.getUserFreeId());
        params.put(RequestKeyHelper.POST_ID, fitnessAdapter.getList().get(i)
                .getId());
        fitnessAdapter.getList().get(i).setCan_wow(0);

        fitnessAdapter.getList().get(i)
                .setWow_count((Integer.parseInt(p.getWow_count()) + 1) + "");
        AppRestClient.post(URLHelper.URL_FREE_VERSION_ADDWOW, params,
                wowHandler);
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

    private class ImagePagerAdapter extends PagerAdapter {

        private List<String> images;
        private LayoutInflater inflater;
        private List<AddData> addData;
        private int type = 2;

        ImagePagerAdapter(List<String> images, int type) {
            this.images = images;
            this.type = type;
            inflater = getActivity().getLayoutInflater();
        }

        ImagePagerAdapter(List<AddData> addData) {
            this.addData = addData;
            inflater = getActivity().getLayoutInflater();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            if (type == 2)
                return addData.size();
            else if (type == 1)
                return images.size();
            return images.size();
        }

        @Override
        public Object instantiateItem(ViewGroup view, int position) {
            View imageLayout = null;
            if (this.type == 2) {
                imageLayout = inflater.inflate(R.layout.add_view_list, view,
                        false);

            } else if (this.type == 1) {
                imageLayout = inflater.inflate(R.layout.item_pager_image, view,
                        false);
            }

            assert imageLayout != null;
            ImageView imageView = (ImageView) imageLayout
                    .findViewById(R.id.image);

            switch (type) {
                case 2:
                    displayImage(imageLayout, imageView, addData.get(position)
                            .getAd_image());
                    break;
                case 1:
                    displayImage(imageLayout, imageView, images.get(position));
                    break;

                default:
                    break;
            }

            view.addView(imageLayout, 0);
            return imageLayout;
        }

        public void displayImage(View imageLayout, ImageView imageView,
                                 String url) {
            final ProgressBar spinner = (ProgressBar) imageLayout
                    .findViewById(R.id.loading);
            imageLoader.displayImage(url, imageView, options,
                    new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String imageUri, View view) {
                            spinner.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view,
                                                    FailReason failReason) {
                            String message = null;
                            switch (failReason.getType()) {
                                case IO_ERROR:
                                    message = "Input/Output error";
                                    break;
                                case DECODING_ERROR:
                                    message = "Image can't be decoded";
                                    break;
                                case NETWORK_DENIED:
                                    message = "Downloads are denied";
                                    break;
                                case OUT_OF_MEMORY:
                                    message = "Out Of Memory error";
                                    break;
                                case UNKNOWN:
                                    message = "Unknown error";
                                    break;
                            }
                            Toast.makeText(mContext, message,
                                    Toast.LENGTH_SHORT).show();
                            spinner.setVisibility(View.GONE);
                        }

                        @Override
                        public void onLoadingComplete(String imageUri,
                                                      View view, Bitmap loadedImage) {
                            spinner.setVisibility(View.GONE);
                        }
                    });
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
    public void onPaswordChanged() {
        // TODO Auto-generated method stub

    }
}
