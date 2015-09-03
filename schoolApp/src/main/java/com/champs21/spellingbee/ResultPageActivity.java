package com.champs21.spellingbee;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.champs21.freeversion.ChildContainerActivity;
import com.champs21.schoolapp.R;
import com.champs21.schoolapp.model.Wrapper;
import com.champs21.schoolapp.networking.AppRestClient;
import com.champs21.schoolapp.utils.GsonParser;
import com.champs21.schoolapp.utils.URLHelper;
import com.champs21.schoolapp.utils.UserHelper;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by BLACK HAT on 02-Jun-15.
 */
public class ResultPageActivity extends ChildContainerActivity {


    private String typedWord = "";
    private String actualWord = "";
    private String timeTaken = "";
    private String currentScore = "";



    private TextView txtScore;
    private TextView txtHighScore;
    private TextView txtGivenWord;
    private TextView txtTipedWord;
    private TextView txtTimeTaken;

    private ImageButton btnShare;
    private ImageButton btnPlayAgain;

    private ImageButton btnLeaderBoard;


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
        setContentView(R.layout.activity_spellingbee_result_page);

        PrefSingleton.getInstance().Initialize(this);

        if(getIntent().getExtras() != null)
        {
            typedWord = getIntent().getExtras().getString(SpellingbeeConstants.KEY_RESULT_PAGE_TYPED_WORD);
            actualWord = getIntent().getExtras().getString(SpellingbeeConstants.KEY_RESULT_PAGE_ACTUAL_WORD);
            timeTaken = getIntent().getExtras().getString(SpellingbeeConstants.KEY_RESULT_PAGE_TIME_TAKEN);
            currentScore = getIntent().getExtras().getString(SpellingbeeConstants.KEY_RESULT_PAGE_CURRENT_SCORE);

            if(TextUtils.isEmpty(currentScore))
                PrefSingleton.getInstance().savePreference(UserHelper.getUserFreeId()+SpellingbeeConstants.CURRENT_SCORE_LEADERBOARD, "00");

            else
                PrefSingleton.getInstance().savePreference(UserHelper.getUserFreeId()+SpellingbeeConstants.CURRENT_SCORE_LEADERBOARD, currentScore);
        }

        Log.e("TIME_TAKEN", "is: " + timeTaken);


        initView();
        initAction();

        initApicall();


        /*UserIdGeneration usd = new UserIdGeneration();
        Map<String, String> map = usd.createUserToken(259);

        for(int i=0;i<map.size();i++)
        {
            Log.e("MAPPPP", "right: "+map.get("right"));
            Log.e("MAPPPP", "left: "+map.get("left"));
            Log.e("MAPPPP", "user_id_token: "+map.get("user_id_token"));
            Log.e("MAPPPP", "method: "+map.get("method"));
            Log.e("MAPPPP", "operator: "+map.get("operator"));
        }*/


    }

    private void initView()
    {
        txtScore = (TextView)this.findViewById(R.id.txtScore);
        txtHighScore = (TextView)this.findViewById(R.id.txtHighScore);
        txtGivenWord = (TextView)this.findViewById(R.id.txtGivenWord);
        txtTipedWord = (TextView)this.findViewById(R.id.txtTipedWord);
        txtTimeTaken = (TextView)this.findViewById(R.id.txtTimeTaken);

        btnShare = (ImageButton)this.findViewById(R.id.btnShare);
        btnPlayAgain = (ImageButton)this.findViewById(R.id.btnPlayAgain);

        btnLeaderBoard = (ImageButton)this.findViewById(R.id.btnLeaderBoard);
    }

    private void initAction()
    {
        txtScore.setText(currentScore);
        txtGivenWord.setText(actualWord);

        if(!TextUtils.isEmpty(typedWord))
            txtTipedWord.setText(typedWord);
        else
            txtTipedWord.setText("---");



        String val = String.valueOf(getDurationBreakdown(Long.parseLong(timeTaken)));
        txtTimeTaken.setText(val);

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ResultPageActivity.this, AndroidFacebookConnectActivity.class);
                intent.putExtra(SpellingbeeConstants.KEY_SCORE_FOR_FB_SHARE, currentScore);
                startActivity(intent);

            }
        });

        btnPlayAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ResultPageActivity.this, SpellingbeeTestActivity.class);
                startActivity(intent);
                finish();


            }
        });

        btnLeaderBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResultPageActivity.this, LeaderBoardActivity.class);
                startActivity(intent);
            }
        });
    }


    private String getTotalTimeInFormat(long timeInMilliSeconds)
    {
        String timer = String.valueOf(timeInMilliSeconds);

        String time = "";

        if(!TextUtils.isEmpty(timer))
        {
            long seconds = timeInMilliSeconds / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;
            //long days = hours / 24;
            time =  hours % 24 + ":" + minutes % 60 + ":" + seconds % 60;
        }

        return time;
     }

    public static String getDurationBreakdown(long millis)
    {
        if(millis < 0)
        {
            //throw new IllegalArgumentException("Duration must be greater than zero!");

            return "0:0:0";
        }

        long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

        StringBuilder sb = new StringBuilder(64);

        sb.append(hours);
        sb.append(":");
        sb.append(minutes);
        sb.append(":");
        sb.append(seconds);

        String bb = sb.toString();

        return(sb.toString());

        /*int sec  = (int)(millis/ 1000) % 60 ;
        int min  = (int)((millis/ (1000*60)) % 60);
        int hr   = (int)((millis/ (1000*60*60)) % 24);

        String returnData = String.valueOf(hr)+":"+String.valueOf(min)+":"+String.valueOf(sec);

        return returnData;*/

    }

    private void initApicall()
    {
        UserIdGeneration usd = new UserIdGeneration();
        //Map<String, String> map = usd.createUserToken(259);


        int userId = Integer.parseInt(UserHelper.getUserFreeId().trim());

        Map<String, String> map = usd.createUserToken(userId);
        RequestParams params = new RequestParams();

        params.put("left", map.get("left"));
        params.put("right", map.get("right"));
        params.put("method", map.get("method"));
        params.put("operator", map.get("operator"));
        params.put("send_id", map.get("user_id_token"));



        Log.e("MAPPPP", "right: " + map.get("right"));
        Log.e("MAPPPP", "left: " + map.get("left"));
        Log.e("MAPPPP", "user_id_token: " + map.get("user_id_token"));
        Log.e("MAPPPP", "method: " + map.get("method"));
        Log.e("MAPPPP", "operator: " + map.get("operator"));


        long tLong = Long.parseLong(timeTaken);
        int time = (int)tLong/1000;
        Log.e("TIME_TAKEN_API", "is: " + time);

        params.put("total_time", String.valueOf(time));
        params.put("score", currentScore);


        params.put("free_id", UserHelper.getUserFreeId());

        AppRestClient.post(URLHelper.SPELLINGBEE_SAVESCORE_NEW, params, resultHandler);
    }

    AsyncHttpResponseHandler resultHandler = new AsyncHttpResponseHandler() {

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

            Log.e("SCCCCC", "response: "+responseString);

            uiHelper.dismissLoadingDialog();


            Wrapper modelContainer = GsonParser.getInstance()
                    .parseServerResponse(responseString);

            if (modelContainer.getStatus().getCode() == 200) {

                String highScore = modelContainer.getData().get("highestScore").getAsString();

                Log.e("SCCCCC", "is: "+highScore);

                txtHighScore.setText(highScore);

            }


            else {

                /*String highScore = modelContainer.getData().get("highestScore").getAsString();

                Log.e("SCCCCC", "is: "+highScore);

                txtHighScore.setText(highScore);*/


            }



        };
    };



}
