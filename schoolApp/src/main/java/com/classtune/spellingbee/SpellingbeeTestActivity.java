package com.classtune.spellingbee;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.classtune.schoolapp.R;
import com.classtune.schoolapp.model.SpellingbeeDataModel;
import com.classtune.schoolapp.model.Wrapper;
import com.classtune.schoolapp.networking.AppRestClient;
import com.classtune.schoolapp.utils.CountDownTimerPausable;
import com.classtune.schoolapp.utils.GsonParser;
import com.classtune.schoolapp.utils.URLHelper;
import com.classtune.schoolapp.utils.UserHelper;
import com.classtune.schoolapp.viewhelpers.UIHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Created by BLACK HAT on 18-May-15.
 */
public class SpellingbeeTestActivity extends Activity implements TextToSpeech.OnInitListener, IOnSkipButtonClick{



    private List<SpellingbeeDataModel> data = null;


    private List<SpellingbeeDataModel> listEasyData;
    private List<SpellingbeeDataModel> listMediumData;
    private List<SpellingbeeDataModel> listHardData;
    private List<SpellingbeeDataModel> listExtremeHardData;

    private List<SpellingbeeDataModel> listCurrentData;

    private List<SpellingbeeDataModel> listDeleteData;




    private CountDownTimerPausable countDownTimer = null;

    private TextView txtTimer;

    private int currentPosition = -1;


    private ImageButton btnMeaning;
    private ImageButton btnSayAgain;
    private ImageButton btnUseInSentence;

    private TextView txtScore;
    private TextView txtWordType;

    private TextView txtPublish;

    private EditText txtSubmit;

    private ImageButton btnEnter;

    private TextToSpeech myTTS = null;

    private boolean isToggleMeaningButton = false;


    private ScrollView scrollViewParent;
    private ScrollView scrollViewChild;

    private int score = 0;

    long tStart = 0; //= System.currentTimeMillis();
    long tEnd = 0; //= System.currentTimeMillis();
    long tDelta = 0;//tEnd - tStart;

    private int multiplier = 0;

    private Gson gson;

    private CustomDialog customDialog;

    private boolean isDialogShowing = false;

    public UIHelper uiHelper;
    public UserHelper userHelper;

    private int tempScore = 0;


    /*@Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        homeBtn.setVisibility(View.VISIBLE);
        logo.setVisibility(View.GONE);
    }*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userHelper = new UserHelper(this);
        uiHelper = new UIHelper(this);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        PrefSingleton.getInstance().Initialize(this);

        setContentView(R.layout.activity_spelling_demo);

        gson = new Gson();

        listEasyData = new ArrayList<SpellingbeeDataModel>();
        listMediumData = new ArrayList<SpellingbeeDataModel>();
        listHardData = new ArrayList<SpellingbeeDataModel>();
        listExtremeHardData = new ArrayList<SpellingbeeDataModel>();
        listCurrentData = new ArrayList<SpellingbeeDataModel>();

        listDeleteData = new ArrayList<SpellingbeeDataModel>();


        listEasyData.clear();
        listMediumData.clear();
        listHardData.clear();
        listExtremeHardData.clear();
        listCurrentData.clear();
        listDeleteData.clear();


        initApiCallGetInit();

        initView();
        initAction();
        loadData();


    }

    private void initApiCallGetInit()
    {
        RequestParams params = new RequestParams();

        params.put("free_id", UserHelper.getUserFreeId());

        AppRestClient.post(URLHelper.SPELLINGBEE_INIT, params, initHandler);
    }
    AsyncHttpResponseHandler initHandler = new AsyncHttpResponseHandler() {

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

                score = modelContainer.getData().get("user_checkpoint").getAsInt();
                txtScore.setText("Score: "+String.valueOf(score));
            }


            else {

            }



        };
    };


    private void initView()
    {
        txtTimer = (TextView)this.findViewById(R.id.txtTimer);

        btnMeaning  =(ImageButton)this.findViewById(R.id.btnMeaning);
        btnSayAgain = (ImageButton)this.findViewById(R.id.btnSayAgain);
        btnUseInSentence = (ImageButton)this.findViewById(R.id.btnUseInSentence);

        txtScore = (TextView)this.findViewById(R.id.txtScore);
        txtWordType = (TextView)this.findViewById(R.id.txtWordType);

        txtPublish = (TextView)this.findViewById(R.id.txtPublish);

        txtSubmit = (EditText)this.findViewById(R.id.txtSubmit);

        btnEnter = (ImageButton)this.findViewById(R.id.btnEnter);

        scrollViewParent = (ScrollView)this.findViewById(R.id.scrollViewParent);
        scrollViewChild = (ScrollView)this.findViewById(R.id.scrollViewChild);


        scrollViewParent.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                //Log.e("PARENT", "PARENT TOUCH");
                findViewById(R.id.scrollViewChild).getParent()
                        .requestDisallowInterceptTouchEvent(false);
                return false;
            }
        });
        scrollViewChild.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                //Log.e("CHILD", "CHILD TOUCH");
                // Disallow the touch request for parent scroll on touch of
                // child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        txtSubmit.setFilters(new InputFilter[]{new InputFilter.AllCaps()});


        customDialog = new CustomDialog(this, this);


    }

    private void initAction()
    {
        //initTimer();

        btnMeaning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isToggleMeaningButton = !isToggleMeaningButton;

                if (isToggleMeaningButton) {
                    btnMeaning.setBackgroundResource(R.drawable.spellingbee_btn_english_meaning);

                    txtPublish.setText(listCurrentData.get(currentPosition).getBanglaMeaning());
                } else {
                    btnMeaning.setBackgroundResource(R.drawable.spellingbee_btn_bangla_meaning);

                    txtPublish.setText(listCurrentData.get(currentPosition).getDefinition());
                }


            }
        });

        btnSayAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Toast.makeText(SpellingbeeTestActivity.this, listCurrentData.get(currentPosition).getWord(), Toast.LENGTH_SHORT).show();
                speakWords(listCurrentData.get(currentPosition).getWord());
            }
        });


        /*btnUseInSentence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtPublish.setText(data.get(currentPosition).getSentence());
            }
        });*/


        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCurrectWord()) {
                    initTimer();

                    score++;
                    tempScore++;
                    initCurrentData();
                } else {

                    //Toast.makeText(SpellingbeeTestActivity.this, "Game Over: Time Taken = "+ getEllaspsedSeconds(), Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(SpellingbeeTestActivity.this, ResultPageActivity.class);

                    intent.putExtra(SpellingbeeConstants.KEY_RESULT_PAGE_TYPED_WORD, txtSubmit.getText().toString());
                    intent.putExtra(SpellingbeeConstants.KEY_RESULT_PAGE_ACTUAL_WORD, listCurrentData.get(currentPosition).getWord());
                    intent.putExtra(SpellingbeeConstants.KEY_RESULT_PAGE_TIME_TAKEN, String.valueOf(getEllaspsedSeconds()));
                    intent.putExtra(SpellingbeeConstants.KEY_RESULT_PAGE_CURRENT_SCORE, String.valueOf(score));


                    startActivity(intent);

                    finish();

                    //Log.e("TIME_TAKEN", "is: " + getEllaspsedSeconds());
                }


            }
        });


        tStart = System.currentTimeMillis();

        /*if(!TextUtils.isEmpty(PrefSingleton.getInstance().getPreference(SpellingbeeConstants.KEY_SAVE_FULL_SCORE)))
        {
            //score = getFullScore()+1;
            score = getFullScore();
        }*/

        txtSubmit.setInputType(txtSubmit.getInputType()
                | EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS
                | EditorInfo.TYPE_TEXT_VARIATION_FILTER);

    }

    private long getEllaspsedSeconds()
    {
        tEnd = System.currentTimeMillis();
        tDelta = tEnd - tStart;
        long elapsedSeconds = (tDelta);

        return elapsedSeconds;
    }

    private void initTimer()
    {


        if(this.countDownTimer != null)
        {
            this.countDownTimer.cancel();
            this.countDownTimer = null;
        }


        if(this.countDownTimer == null)
        {
            this.countDownTimer = new CountDownTimerPausable(SpellingbeeConstants.SPELLINGBEE_TIMER, 1) {
                public void onTick(long millisUntilFinished) {

                    txtTimer.setText(String.valueOf(millisUntilFinished / 1000));


                }

                public void onFinish() {
                    txtTimer.setText("00");

                    //Toast.makeText(SpellingbeeTestActivity.this, "Game Over: Time Taken = "+ getEllaspsedSeconds(), Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(SpellingbeeTestActivity.this, ResultPageActivity.class);

                    intent.putExtra(SpellingbeeConstants.KEY_RESULT_PAGE_TYPED_WORD, txtSubmit.getText().toString());
                    intent.putExtra(SpellingbeeConstants.KEY_RESULT_PAGE_ACTUAL_WORD, listCurrentData.get(currentPosition).getWord());
                    intent.putExtra(SpellingbeeConstants.KEY_RESULT_PAGE_TIME_TAKEN, String.valueOf(getEllaspsedSeconds()));
                    intent.putExtra(SpellingbeeConstants.KEY_RESULT_PAGE_CURRENT_SCORE, String.valueOf(score));

                    startActivity(intent);

                    finish();

                }
            };

            this.countDownTimer.start();
        }
    }


    private void loadData()
    {
        BgLoadData bgData = new BgLoadData();
        if(PrefSingleton.getInstance().containsKey(SpellingbeeConstants.CURRENT_BANK_NUMBER))
        {
            String str = PrefSingleton.getInstance().getPreference(SpellingbeeConstants.CURRENT_BANK_NUMBER);

            switch (Integer.parseInt(str))
            {
                case 1:
                    //bgData.execute("demo_3_mod.xml");
                    bgData.execute("year_1.xml");
                    break;
                case 2:
                    //bgData.execute("demo_4_mod.xml");
                    bgData.execute("year_2.xml");
                    break;
                case 3:
                    //bgData.execute("demo_5_mod.xml");
                    bgData.execute("year_3.xml");
                    break;

                case 4:
                    //bgData.execute("demo_5_mod.xml");
                    bgData.execute("year_4.xml");
                    break;

                case 5:
                    //bgData.execute("demo_5_mod.xml");
                    bgData.execute("year_5.xml");
                    break;

            }
        }
        else
        {
            PrefSingleton.getInstance().savePreference(SpellingbeeConstants.CURRENT_BANK_NUMBER, "1");
            bgData.execute("year_1.xml");
        }




        /*if(!TextUtils.isEmpty(PrefSingleton.getInstance().getPreference("1876"+SpellingbeeConstants.KEY_SAVE_FULL_DATA)))
        {
            //data = getFullData();


            data.removeAll(getFullData());

            splitDataIntoCategories();

            Log.e("DATA_SIZE_IF", "is: "+data.size());
        }

        else
        {
            if(PrefSingleton.getInstance().containsKey(SpellingbeeConstants.CURRENT_BANK_NUMBER))
            {
                String str = PrefSingleton.getInstance().getPreference(SpellingbeeConstants.CURRENT_BANK_NUMBER);

                switch (Integer.parseInt(str))
                {
                    case 1:
                        bgData.execute("demo_3_mod.xml");
                        break;
                    case 2:
                        bgData.execute("demo_4_mod.xml");
                        break;
                    case 3:
                        bgData.execute("demo_5_mod.xml");
                        break;

                }
            }
            else
            {
                PrefSingleton.getInstance().savePreference(SpellingbeeConstants.CURRENT_BANK_NUMBER, "1");
                bgData.execute("demo_3_mod.xml");
            }



        }*/



    }

    private class BgLoadData extends AsyncTask<String, Void, Boolean>
    {

        private ProgressDialog pd;

        @Override
        protected Boolean doInBackground(String... params) {
            // TODO Auto-generated method stub
            try
            {
                data = SAXXMLParser.parse(getAssets().open(params[0]));


                for(int i=0;i<data.size();i++)
                {
                    Log.e("FROM_XML", "data: " + data.get(i).getId());
                    Log.e("FROM_XML", "data: " + data.get(i).getWord());
                    Log.e("FROM_XML", "data: " + data.get(i).getWordTwo());
                    Log.e("FROM_XML", "data: " + data.get(i).getBanglaMeaning());
                    Log.e("FROM_XML", "data: " + data.get(i).getDefinition());
                    Log.e("FROM_XML", "data: " + data.get(i).getSentence());
                    Log.e("FROM_XML", "data: "+data.get(i).getwType());
                    Log.e("FROM_XML", "data: "+data.get(i).getLevel());

                    Log.e("FROM_XML", "_______________________________________");
                }


            } catch (IOException e) {
                e.printStackTrace();
            }

            //Collections.shuffle(data);

            return true;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

            pd = new ProgressDialog(SpellingbeeTestActivity.this);
            pd.setCancelable(false);
            pd.setMessage("Please wait...");
            pd.show();
        }

        protected void onPostExecute(Boolean result)
        {
            /*if(pd.isShowing())
            {
                pd.dismiss();
            }*/

            //initTimer();



            pd.dismiss();
            if(!pd.isShowing())
            {
                initTimer();
            }
            Log.e("DATA_SIZE_ELSE", "is: " + data.size());

            if(!TextUtils.isEmpty(PrefSingleton.getInstance().getPreference(SpellingbeeConstants.KEY_SAVE_FULL_DATA)))
            {
                //data = getFullData();
                //data.removeAll(getFullData());


                /*for(int j=0; j< data.size();j++)
                {
                    for(int i=0;i<getFullData().size();i++)
                    {
                        if(data.get(j).getId() == (getFullData().get(i).getId()))
                            data.remove(j);
                    }
                }

                Log.e("SAVED DATA", "size is: " + getFullData().size());*/

                data.clear();
                data = null;
                data = new ArrayList<SpellingbeeDataModel>();
                data = getFullData();




                splitDataIntoCategories();

                Log.e("DATA_SIZE_IF", "is: "+data.size());
            }
            else
                splitDataIntoCategories();



        }


    }


    private void splitDataIntoCategories()
    {


        for(SpellingbeeDataModel obj : data)
        {
            if(obj.getLevel().equalsIgnoreCase("0"))
            {
                listEasyData.add(obj);
            }

            if(obj.getLevel().equalsIgnoreCase("1"))
            {
                listMediumData.add(obj);
            }
            if(obj.getLevel().equalsIgnoreCase("2"))
            {
                listHardData.add(obj);
            }

            if(obj.getLevel().equalsIgnoreCase("3")) {
                listExtremeHardData.add(obj);
            }
        }

        Log.e("EASY_SIZE", "is: " + listEasyData.size());
        Log.e("EASY_SIZE", "is: " + listMediumData.size());
        Log.e("EASY_SIZE", "is: " + listHardData.size());
        Log.e("EASY_SIZE", "is: " + listExtremeHardData.size());

        /*Collections.reverse(listEasyData);
        Collections.reverse(listMediumData);
        Collections.reverse(listHardData);
        Collections.reverse(listExtremeHardData);
        */



        buildCurrentList();

        initCurrentData();
    }


    private void buildCurrentList()
    {

        /*for (int i = 0; i < SpellingbeeConstants.PER_ITEM_PICK_COUNT;i++)
        {
            listCurrentData.add(listEasyData.get(i));
            listCurrentData.add(listMediumData.get(i));
            listCurrentData.add(listHardData.get(i));

        }*/


        /*int cal = listEasyData.size()/SpellingbeeConstants.PER_ITEM_PICK_COUNT;


        for(int j = 0; j < cal ;j++)
        {
            for (int i = SpellingbeeConstants.PER_ITEM_PICK_COUNT * j; i < SpellingbeeConstants.PER_ITEM_PICK_COUNT * (j+1);i++)
            {
                listCurrentData.add(listEasyData.get(i));

            }

            for (int i = SpellingbeeConstants.PER_ITEM_PICK_COUNT * j; i < SpellingbeeConstants.PER_ITEM_PICK_COUNT * (j+1);i++)
            {

                listCurrentData.add(listMediumData.get(i));


            }

            for (int i = SpellingbeeConstants.PER_ITEM_PICK_COUNT * j; i < SpellingbeeConstants.PER_ITEM_PICK_COUNT * (j+1);i++)
            {

                listCurrentData.add(listHardData.get(i));

            }

        }



        if(!TextUtils.isEmpty(PrefSingleton.getInstance().getPreference(SpellingbeeConstants.KEY_CHECKPOINT_POSITION)))
        {
            currentPosition = Integer.parseInt(PrefSingleton.getInstance().getPreference(SpellingbeeConstants.KEY_CHECKPOINT_POSITION));
        }



        multiplier++;*/

        multiplier++;

        Collections.shuffle(listEasyData);
        Collections.shuffle(listMediumData);
        Collections.shuffle(listHardData);
        Collections.shuffle(listExtremeHardData);


        /*if(!TextUtils.isEmpty(PrefSingleton.getInstance().getPreference(SpellingbeeConstants.KEY_CHECKPOINT_POSITION)))
        {
            int pos = Integer.parseInt(PrefSingleton.getInstance().getPreference(SpellingbeeConstants.KEY_CHECKPOINT_POSITION));
            Log.e("SAVED_POS", "is: "+pos);

            currentPosition = pos;

            buildList();

        }*/


        for(int i=0;i<SpellingbeeConstants.PER_ITEM_PICK_COUNT_EASY;i++)
        {
            if(i<listEasyData.size())
                listCurrentData.add(listEasyData.get(i));
            //listEasyData.remove(i);

        }

        for(int i=0;i<SpellingbeeConstants.PER_ITEM_PICK_COUNT_MEDIUM;i++)
        {
            if(i<listMediumData.size())
                listCurrentData.add(listMediumData.get(i));
            //listMediumData.remove(i);

        }

        for(int i=0;i<SpellingbeeConstants.PER_ITEM_PICK_COUNT_HARD;i++)
        {
            if(i<listHardData.size())
                listCurrentData.add(listHardData.get(i));
            //listHardData.remove(i);

        }

        for(int i=0;i<SpellingbeeConstants.PER_ITEM_PICK_COUNT_EXTREME_HARD;i++)
        {
            if(i<listExtremeHardData.size())
                listCurrentData.add(listExtremeHardData.get(i));
            //listExtremeHardData.remove(i);

        }










        Log.e("CURRENT_SIZE", "is: " + listCurrentData.size());

        for(int i=0;i<listCurrentData.size();i++)
        {
            Log.e("CURRENT_LIST_DATA_ARE", "is: " + listCurrentData.get(i).getId());
            Log.e("CURRENT_LIST_DATA_ARE", "is: " + listCurrentData.get(i).getWord());
            Log.e("CURRENT_LIST_DATA_ARE", "is: " + listCurrentData.get(i).getLevel());
            Log.e("CURRENT_LIST_DATA_ARE", "___________________________________________");
        }

    }


    /*private void buildList()
    {

        for(int i=currentPosition;i<currentPosition+SpellingbeeConstants.PER_ITEM_PICK_COUNT ;i++)
        {
            listCurrentData.add(listEasyData.get(i));
            listCurrentData.add(listMediumData.get(i));
            listCurrentData.add(listHardData.get(i));

        }



    }*/


    private void buildList()
    {

        /*for(int i=currentPosition;i<currentPosition+SpellingbeeConstants.PER_ITEM_PICK_COUNT_EASY ;i++)
        {
            listCurrentData.add(listEasyData.get(i));
            listEasyData.remove(i);

        }

        for(int i=currentPosition;i<currentPosition+SpellingbeeConstants.PER_ITEM_PICK_COUNT_MEDIUM ;i++)
        {
            listCurrentData.add(listMediumData.get(i));
            listMediumData.remove(i);

        }

        for(int i=currentPosition;i<currentPosition+SpellingbeeConstants.PER_ITEM_PICK_COUNT_HARD ;i++)
        {
            listCurrentData.add(listHardData.get(i));
            listHardData.remove(i);

        }

        for(int i=currentPosition;i<currentPosition+SpellingbeeConstants.PER_ITEM_PICK_COUNT_EXTREME_HARD ;i++)
        {
            listCurrentData.add(listExtremeHardData.get(i));
            listExtremeHardData.remove(i);

        }*/

        Log.e("ZZZZZZZ", "size: "+listEasyData.size());
        Log.e("ZZZZZZZ", "mult: "+SpellingbeeConstants.PER_ITEM_PICK_COUNT_EASY*multiplier);
        Log.e("ZZZZZZZ", "mult+: "+SpellingbeeConstants.PER_ITEM_PICK_COUNT_EASY*(multiplier+1));

        for(int i=SpellingbeeConstants.PER_ITEM_PICK_COUNT_EASY*multiplier ;i<SpellingbeeConstants.PER_ITEM_PICK_COUNT_EASY*(multiplier+1) ;i++)
        {
            Log.e("ZZZZZZZ", "pos: " + i);

            if(i<listEasyData.size())
                listCurrentData.add(listEasyData.get(i));
            //listEasyData.remove(i);

        }

        for(int i=SpellingbeeConstants.PER_ITEM_PICK_COUNT_MEDIUM*multiplier;i<SpellingbeeConstants.PER_ITEM_PICK_COUNT_MEDIUM*(multiplier+1) ;i++)
        {
            if(i<listMediumData.size())
                listCurrentData.add(listMediumData.get(i));
            //listMediumData.remove(i);

        }

        for(int i=SpellingbeeConstants.PER_ITEM_PICK_COUNT_HARD*multiplier;i<SpellingbeeConstants.PER_ITEM_PICK_COUNT_HARD*(multiplier+1) ;i++)
        {
            if(i<listHardData.size())
                listCurrentData.add(listHardData.get(i));
            //listHardData.remove(i);

        }

        for(int i=SpellingbeeConstants.PER_ITEM_PICK_COUNT_EXTREME_HARD*multiplier;i<SpellingbeeConstants.PER_ITEM_PICK_COUNT_EXTREME_HARD*(multiplier+1) ;i++)
        {
            if(i<listExtremeHardData.size())
                listCurrentData.add(listExtremeHardData.get(i));
            //listExtremeHardData.remove(i);

        }



        Log.e("CURRENT_SIZE_buildList", "is: " + listCurrentData.size());

        for(int i=0;i<listCurrentData.size();i++)
        {
            Log.e("CURRENT_LIST_DATA_ARE", "is: " + listCurrentData.get(i).getId());
            Log.e("CURRENT_LIST_DATA_ARE", "is: " + listCurrentData.get(i).getWord());
            Log.e("CURRENT_LIST_DATA_ARE", "is: " + listCurrentData.get(i).getLevel());
            Log.e("CURRENT_LIST_DATA_ARE", "___________________________________________");
        }


    }



    private void initCurrentData()
    {

        Log.e("KKKKKKKK", "data size: "+data.size());
        Log.e("KKKKKKKK", "currentpos: "+currentPosition);


        btnMeaning.setBackgroundResource(R.drawable.spellingbee_btn_bangla_meaning);
        isToggleMeaningButton = false;



        if(currentPosition == data.size()-1)
        {
            loadDataOnDemand();

            return;
        }

        if(currentPosition < listCurrentData.size())
        {
            currentPosition++;
        }

        if(currentPosition < data.size() && currentPosition == listCurrentData.size())
        {
            //currentPosition++;
            //multiplier++;

            buildList();
        }



        /*if(currentPosition >= listCurrentData.size())
        {
            multiplier++;
            currentPosition++;

            buildList();

        }*/


        if(currentPosition<listCurrentData.size())
            listDeleteData.add(listCurrentData.get(currentPosition));



        /*if(currentPosition>= 0 && ((currentPosition+1)  % SpellingbeeConstants.CHECK_POINT  == 0))
        {
            PrefSingleton.getInstance().savePreference(SpellingbeeConstants.KEY_CHECKPOINT_POSITION, String.valueOf(currentPosition));

            //data.removeAll(listDeleteData);

            saveFullData();
            saveFullScore();


        }*/




        if(tempScore != 0 && ((score  % SpellingbeeConstants.CHECK_POINT  == 0)))
        {
            countDownTimer.pause();

            isDialogShowing = true;
            new CustomDialog(this, this).show();


        }



        if(score> 0 && (score  % SpellingbeeConstants.CHECK_POINT  == 0))
        {
            PrefSingleton.getInstance().savePreference(SpellingbeeConstants.KEY_CHECKPOINT_POSITION, String.valueOf(currentPosition));

            //data.removeAll(listDeleteData);

            saveFullData();
            //saveFullScore();


        }





        txtPublish.setText(listCurrentData.get(currentPosition).getDefinition());
        txtSubmit.setText("");


        txtScore.setText("Score: "+String.valueOf(score));

        txtWordType.setText(listCurrentData.get(currentPosition).getwType().toUpperCase());

        if(myTTS == null)
            myTTS = new TextToSpeech(this, this);

        if(isDialogShowing == false)
            speakWords(listCurrentData.get(currentPosition).getWord());


        String star = "*";
        String sentence = listCurrentData.get(currentPosition).getSentence();

        for(int i = 0; i<listCurrentData.get(currentPosition).getWord().length() - 1; i++) {
            star = star+"*";
        }

        final String newString = sentence.replaceAll("(?i)"+listCurrentData.get(currentPosition).getWord(), star);



        btnUseInSentence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtPublish.setText(newString);
            }
        });


        Log.e("CURRENT_MULTIPLIER", "is: " + multiplier);
        Log.e("CURRENT_LIST_SIZE", "is: " + listCurrentData.size());
        Log.e("CURRENT_POSITION", "is: " + currentPosition);
        Log.e("CURRENT_WORD", "is: " + listCurrentData.get(currentPosition).getWord());
        Log.e("CURRENT_LEVEL", "is: "+listCurrentData.get(currentPosition).getLevel());
        Log.e("CURRENT_DATA", "__________________________________________");

    }


    @Override
    public void onSkipButtonClicked() {

        isDialogShowing = false;

        if(isDialogShowing == false)
            speakWords(listCurrentData.get(currentPosition).getWord());

        if(countDownTimer.isPaused())
            countDownTimer.start();

    }

    private void removeUsedData()
    {

    }


    private void loadDataOnDemand()
    {
        data.clear();
        listEasyData.clear();
        listMediumData.clear();
        listHardData.clear();
        listExtremeHardData.clear();
        listCurrentData.clear();
        listDeleteData.clear();

        currentPosition = -1;

        if(PrefSingleton.getInstance().getPreference(SpellingbeeConstants.CURRENT_BANK_NUMBER).equalsIgnoreCase("1"))
        {
            PrefSingleton.getInstance().savePreference(SpellingbeeConstants.CURRENT_BANK_NUMBER, "2");
            BgLoadData bgData = new BgLoadData();
            //bgData.execute("demo_4_mod.xml");
            bgData.execute("year_2.xml");
        }
        else if(PrefSingleton.getInstance().getPreference(SpellingbeeConstants.CURRENT_BANK_NUMBER).equalsIgnoreCase("2"))
        {

            PrefSingleton.getInstance().savePreference(SpellingbeeConstants.CURRENT_BANK_NUMBER, "3");
            BgLoadData bgData = new BgLoadData();
            //bgData.execute("demo_5_mod.xml");
            bgData.execute("year_3.xml");
        }

        else if(PrefSingleton.getInstance().getPreference(SpellingbeeConstants.CURRENT_BANK_NUMBER).equalsIgnoreCase("3"))
        {
            PrefSingleton.getInstance().savePreference(SpellingbeeConstants.CURRENT_BANK_NUMBER, "4");
            BgLoadData bgData = new BgLoadData();
            //bgData.execute("demo_3_mod.xml");
            bgData.execute("year_4.xml");
        }

        else if(PrefSingleton.getInstance().getPreference(SpellingbeeConstants.CURRENT_BANK_NUMBER).equalsIgnoreCase("4"))
        {
            PrefSingleton.getInstance().savePreference(SpellingbeeConstants.CURRENT_BANK_NUMBER, "5");
            BgLoadData bgData = new BgLoadData();
            //bgData.execute("demo_3_mod.xml");
            bgData.execute("year_5.xml");
        }

        else if(PrefSingleton.getInstance().getPreference(SpellingbeeConstants.CURRENT_BANK_NUMBER).equalsIgnoreCase("5"))
        {
            PrefSingleton.getInstance().savePreference(SpellingbeeConstants.CURRENT_BANK_NUMBER, "1");
            BgLoadData bgData = new BgLoadData();
            //bgData.execute("demo_3_mod.xml");
            bgData.execute("year_1.xml");
        }



    }




    private void saveFullData()
    {

        /*String val = gson.toJson(listDeleteData);
        PrefSingleton.getInstance().savePreference(SpellingbeeConstants.KEY_SAVE_FULL_DATA, val);*/

        data.removeAll(listDeleteData);
        String val = gson.toJson(data);
        PrefSingleton.getInstance().savePreference(SpellingbeeConstants.KEY_SAVE_FULL_DATA, val);


    }

    private List<SpellingbeeDataModel> getFullData()
    {
        Type type = new TypeToken<List<SpellingbeeDataModel>>(){}.getType();
        List<SpellingbeeDataModel> list = gson.fromJson(PrefSingleton.getInstance().getPreference(SpellingbeeConstants.KEY_SAVE_FULL_DATA), type);

        return list;
    }

    /*private void saveFullScore()
    {
        PrefSingleton.getInstance().savePreference(SpellingbeeConstants.KEY_SAVE_FULL_SCORE, String.valueOf(score));

    }*/

   /* private int getFullScore()
    {
        int score = 0;

        if(!TextUtils.isEmpty(PrefSingleton.getInstance().getPreference(SpellingbeeConstants.KEY_SAVE_FULL_SCORE)))
            score = Integer.parseInt(PrefSingleton.getInstance().getPreference(SpellingbeeConstants.KEY_SAVE_FULL_SCORE));
        else
            score = 0;

        return score;
    }*/




    private boolean isCurrectWord()
    {
        boolean isCorrect = false;

        if(listCurrentData.get(currentPosition).getWord().equalsIgnoreCase(txtSubmit.getText().toString().trim()) || (!TextUtils.isEmpty(listCurrentData.get(currentPosition).getWordTwo()) &&
                listCurrentData.get(currentPosition).getWordTwo().equalsIgnoreCase(txtSubmit.getText().toString().trim())))
            isCorrect = true;

        return isCorrect;
    }

    private void speakWords(String speech)
    {
        myTTS.speak(speech, TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    public void onInit(int initStatus) {
        // TODO Auto-generated method stub
        if (initStatus == TextToSpeech.SUCCESS) {
            if(myTTS.isLanguageAvailable(Locale.UK)==TextToSpeech.LANG_AVAILABLE)
                myTTS.setLanguage(Locale.UK);

            speakWords(listCurrentData.get(currentPosition).getWord());
        }
        else if (initStatus == TextToSpeech.ERROR) {
            Toast.makeText(this, "Sorry! Text To Speech failed...", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onDestroy() {


        if(this.countDownTimer != null)
        {
            this.countDownTimer.cancel();
            this.countDownTimer = null;
        }

        if(myTTS != null)
            myTTS.shutdown();

        super.onDestroy();
    }

    private int getRandomNumberInRange(int minVal, int maxVal)
    {
        Random r = new Random();
        int num = r.nextInt(maxVal - minVal) + minVal;

        return num;
    }

}
