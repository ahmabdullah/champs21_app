package com.champs21.schoolapp.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.champs21.schoolapp.R;
import com.champs21.schoolapp.model.BaseType;
import com.champs21.schoolapp.model.GraphSubjectType;
import com.champs21.schoolapp.model.Picker;
import com.champs21.schoolapp.model.PickerType;
import com.champs21.schoolapp.model.ProgressExam;
import com.champs21.schoolapp.model.Wrapper;
import com.champs21.schoolapp.networking.AppRestClient;
import com.champs21.schoolapp.utils.GsonParser;
import com.champs21.schoolapp.utils.RequestKeyHelper;
import com.champs21.schoolapp.utils.URLHelper;
import com.champs21.schoolapp.utils.UserHelper;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.List;

/**
 * Created by Tasvir on 4/8/2015.
 */
public class ProgressGraphFragment extends Fragment implements View.OnClickListener {

    private TextView subjectTextView;
    private ImageButton btnSubjectSelect;
    private ProgressBar pbGraph;
    private LinearLayout graphView;
    private UserHelper userHelper;
    private GraphicalView mChartView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userHelper = new UserHelper(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_progress_graph, container, false);
        subjectTextView = (TextView) view.findViewById(R.id.tv_prog_selected_subject);
        btnSubjectSelect = (ImageButton) view.findViewById(R.id.btn_prog_select_subject);
        btnSubjectSelect.setOnClickListener(this);
        pbGraph = (ProgressBar) view.findViewById(R.id.pb_graph);
        graphView = (LinearLayout) view.findViewById(R.id.graph_view);
        return view;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btn_prog_select_subject:
                showSubjectPicker();
                break;
            default:
                break;
        }
    }

    private void setGraphVisibility(boolean status) {
        graphView.setVisibility(status ? View.VISIBLE : View.INVISIBLE);
        pbGraph.setVisibility(!status ? View.VISIBLE : View.INVISIBLE);
    }
    public void showSubjectPicker() {

        CustomPickerWithLoadData picker = CustomPickerWithLoadData.newInstance(0);
        RequestParams params = new RequestParams();
        params.put(RequestKeyHelper.USER_SECRET, UserHelper.getUserSecret());
        if(userHelper.getUser().getType()== UserHelper.UserTypeEnum.PARENTS){
            params.put(RequestKeyHelper.BATCH_ID,userHelper.getUser().getSelectedChild().getBatchId());
            params.put(RequestKeyHelper.STUDENT_ID,userHelper.getUser().getSelectedChild().getProfileId());
        }


        picker.setData(PickerType.GRAPH,params, URLHelper.URL_GET_GRAPH_SUBJECTS, PickerCallback , "Select Subject");
        picker.show(getChildFragmentManager(), null);
    }
    Picker.PickerItemSelectedListener PickerCallback = new Picker.PickerItemSelectedListener() {

        @Override
        public void onPickerItemSelected(BaseType item) {

            switch (item.getType()) {
                case GRAPH:
                    subjectTextView.setText(item.getText());
                    fetchGraphData(((GraphSubjectType)item).getId());
                    break;
                default:
                    break;
            }
        }
    };
    private void fetchGraphData(String subjectId){
        RequestParams params = new RequestParams();
        params.put(RequestKeyHelper.USER_SECRET, UserHelper.getUserSecret());
        params.put(RequestKeyHelper.SUBJECT_ID, subjectId);
        params.put(RequestKeyHelper.EXAM_CATEGORY,"0");
        if(userHelper.getUser().getType()== UserHelper.UserTypeEnum.PARENTS){
            params.put(RequestKeyHelper.BATCH_ID,userHelper.getUser().getSelectedChild().getBatchId());
            params.put(RequestKeyHelper.STUDENT_ID,userHelper.getUser().getSelectedChild().getProfileId());
        }
        AppRestClient.post(URLHelper.URL_GET_REPORT_PROGRESS, params,  new AsyncHttpResponseHandler(){
            public void onFailure(Throwable arg0, String arg1) {
                setGraphVisibility(true);
//                Log.e("error", arg1);
            };

            public void onStart() {
                setGraphVisibility(false);

            };

            public void onSuccess(int arg0, String responseString) {

                setGraphVisibility(true);
                Wrapper wrapper = GsonParser.getInstance().parseServerResponse(
                        responseString);
                JsonObject progress = wrapper.getData().getAsJsonObject("progress");
                Log.e("response graph",(progress.get("exam")).toString());
                openChart(GsonParser.getInstance().parseGraphDataList(
                        (progress.get("exam")).toString()));
                //adapter.notifyDataSetChanged();
            };
        });

    }
    private void openChart(List<ProgressExam> exam){
        int[] x = { 0,1,2,3,4,5,6,7 };
        int[] income = { 2000,2500,2700,3000,2800,3500,3700,3800};
        int[] expense = {2200, 2700, 2900, 2800, 2600, 3000, 3300, 3400 };




        // Creating an  XYSeries for Income
        //CategorySeries incomeSeries = new CategorySeries("Income");
        XYSeries incomeSeries = new XYSeries("Your Percentage");
        // Creating an  XYSeries for Income
        XYSeries expenseSeries = new XYSeries("Highest Percentage");
        // Adding data to Income and Expense Series
        for(int i=0;i<exam.size();i++){
            incomeSeries.add(i,exam.get(i).getYour_percent());//income[i]
            expenseSeries.add(i,exam.get(i).getMax_mark_percent());//expense[i]
        }


        // Creating a dataset to hold each series
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        // Adding Income Series to the dataset
        dataset.addSeries(incomeSeries);
        // Adding Expense Series to dataset
        dataset.addSeries(expenseSeries);


        DisplayMetrics metrics = getActivity().getResources().getDisplayMetrics();
        float val = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, metrics);

        // Creating XYSeriesRenderer to customize incomeSeries
        XYSeriesRenderer incomeRenderer = new XYSeriesRenderer();
        incomeRenderer.setColor(Color.rgb(130, 130, 230));
        incomeRenderer.setFillPoints(false);
        incomeRenderer.setLineWidth(2);
        incomeRenderer.setDisplayChartValues(true);
        incomeRenderer.setChartValuesTextSize(val);

        // Creating XYSeriesRenderer to customize expenseSeries
        XYSeriesRenderer expenseRenderer = new XYSeriesRenderer();
        expenseRenderer.setColor(Color.rgb(220, 80, 80));
        expenseRenderer.setFillPoints(false);
        expenseRenderer.setLineWidth(2);
        expenseRenderer.setDisplayChartValues(true);
        expenseRenderer.setChartValuesTextSize(val);

        // Creating a XYMultipleSeriesRenderer to customize the whole chart
        XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();
        multiRenderer.setXLabels(0);
        //multiRenderer.setChartTitle("Income vs Expense Chart");
        //multiRenderer.setXTitle("Year 2012");
        //multiRenderer.setYTitle("Amount in Dollars");
        for(int i=0; i< exam.size();i++){
            multiRenderer.addXTextLabel(i, exam.get(i).getExam_name());//mMonth[i]
        }


        // Adding incomeRenderer and expenseRenderer to multipleRenderer
        // Note: The order of adding dataseries to dataset and renderers to multipleRenderer
        // should be same
        multiRenderer.setXLabelsColor(Color.BLACK);
        multiRenderer.removeAllRenderers();
        multiRenderer.addSeriesRenderer(incomeRenderer);
        multiRenderer.addSeriesRenderer(expenseRenderer);

        multiRenderer.setZoomButtonsVisible(false);
        multiRenderer.setDisplayValues(true);
        multiRenderer.setShowLegend(true);
        //multiRenderer.setLegendHeight(150);
        multiRenderer.setFitLegend(true);
        multiRenderer.setMargins(new int[] { 50, 50, 50, 22 });
        //multiRenderer.setPanEnabled(false);

        multiRenderer.setLabelsTextSize(val);
        multiRenderer.setLegendTextSize(val);
        multiRenderer.setLabelsColor(Color.WHITE);
        multiRenderer.setApplyBackgroundColor(true);
        multiRenderer.setMarginsColor(Color.WHITE);
        multiRenderer.setBackgroundColor(Color.WHITE);
        multiRenderer.setXAxisMax(exam.size());
        multiRenderer.setXAxisMin(-1);
        multiRenderer.setYAxisMax(120);
        multiRenderer.setYAxisMin(0);
        float barwidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 30, metrics);
        multiRenderer.setBarWidth(barwidth);



        // Creating an intent to plot bar chart using dataset and multipleRenderer
        //Intent intent = ChartFactory.getBarChartIntent(getBaseContext(), dataset, multiRenderer, BarChart.Type.DEFAULT);

        // Start Activity
        //startActivity(new CombinedTemperatureChart().execute(this));
        if(mChartView!=null)graphView.removeView(mChartView);
        mChartView = ChartFactory.getBarChartView(getActivity(),dataset,multiRenderer,BarChart.Type.DEFAULT);

        graphView.addView(mChartView  , new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.FILL_PARENT));

    }
}
