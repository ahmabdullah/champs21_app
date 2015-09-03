package com.champs21.spellingbee;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.champs21.freeversion.ChildContainerActivity;
import com.champs21.freeversion.CompleteProfileActivityContainer;
import com.champs21.schoolapp.LoginActivity;
import com.champs21.schoolapp.R;
import com.champs21.schoolapp.model.Wrapper;
import com.champs21.schoolapp.networking.AppRestClient;
import com.champs21.schoolapp.utils.GsonParser;
import com.champs21.schoolapp.utils.SPKeyHelper;
import com.champs21.schoolapp.utils.SchoolApp;
import com.champs21.schoolapp.utils.URLHelper;
import com.champs21.schoolapp.utils.UserHelper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by BLACK HAT on 08-Jun-15.
 */
public class LeaderBoardActivity extends ChildContainerActivity {


    private ImageView imgProfileImage;
    private TextView txtName;

    private TextView txtScoreDetails;
    private TextView txtRank;

    private ListView listViewLeaderBoard;
    private  UserHelper userHelper;

    private List<LeaderBoardModel> listLeaderBoard;
    private AdapterLeaderBoard adapter;

    private TextView txtMessage;


    private List<String> listDivision;

    //private String selectedDivision = "Dhaka";

    private String userFullName = "";





    private RelativeLayout layoutTop;

    private ImageButton btnPlayNow;


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
        setContentView(R.layout.spellingbee_leaderboard_activity);

        PrefSingleton.getInstance().Initialize(this);

        listLeaderBoard = new ArrayList<LeaderBoardModel>();

        listDivision = new ArrayList<String>();
        listDivision.add("Dhaka");
        listDivision.add("Chittagong");
        listDivision.add("Sylhet");
        listDivision.add("Rajshashi");
        listDivision.add("Rangpur");
        listDivision.add("Khulna");
        listDivision.add("Barisal");


        userHelper = new UserHelper(this);

        initView();
        initAction();

        initApicall();
    }


    private void initView()
    {
        imgProfileImage = (ImageView)this.findViewById(R.id.imgProfileImage);
        txtName = (TextView)this.findViewById(R.id.txtName);

        txtScoreDetails  =(TextView)this.findViewById(R.id.txtScoreDetails);
        txtRank = (TextView)this.findViewById(R.id.txtRank);

        listViewLeaderBoard  = (ListView)this.findViewById(R.id.listViewLeaderBoard);

        adapter = new AdapterLeaderBoard();
        listViewLeaderBoard.setAdapter(adapter);

        txtMessage = (TextView)this.findViewById(R.id.txtMessage);


        layoutTop = (RelativeLayout)this.findViewById(R.id.layoutTop);


        if(UserHelper.isLoggedIn())
        {
            layoutTop.setVisibility(View.VISIBLE);

            /*if (userHelper.getUser().getType() == UserHelper.UserTypeEnum.STUDENT)
            {
                layoutTop.setVisibility(View.VISIBLE);
            }
            else
                layoutTop.setVisibility(View.GONE);*/
        }
        else
            layoutTop.setVisibility(View.GONE);


        btnPlayNow = (ImageButton)this.findViewById(R.id.btnPlayNow);

    }

    private void initAction()
    {
        SchoolApp.getInstance().displayUniversalImage(userHelper.getUser().getProfilePicsUrl(), imgProfileImage);
        if(TextUtils.isEmpty(userHelper.getUser().getProfilePicsUrl()))
            imgProfileImage.setImageResource(R.drawable.user_avatar);


        //txtName.setText(userHelper.getUser().getFullName());
        //txtSchoolName.setText(userHelper.getUser().getSchoolName());


        /*if(!TextUtils.isEmpty(userHelper.getUser().getMedium()))
        {
            userFullName = userHelper.getUser().getFirstName()+" "+userHelper.getUser().getMiddleName()+" "+userHelper.getUser().getLastName();
        }
        else
        {
            userFullName = userHelper.getUser().getFirstName()+" "+userHelper.getUser().getLastName();
        }*/

        userFullName = userHelper.getUser().getFirstName()+" "+userHelper.getUser().getLastName();

        txtName.setText(userFullName);

        Log.e("FULL NAME", "is: " + UserHelper.getSchoolName());
        Log.e("FULL NAME", "is: " + userHelper.getUser().getFirstName());

        btnPlayNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (UserHelper.isLoggedIn()){
                    if(userHelper.getUser().getType()== UserHelper.UserTypeEnum.STUDENT){
                        if(UserHelper.getSpellingStatus()==0){
                            Intent i = new Intent(LeaderBoardActivity.this,
                                    CompleteProfileActivityContainer.class);
                            i.putExtra(SPKeyHelper.USER_TYPE, userHelper.getUser().getType().ordinal());
                            startActivity(i);
                        }else {
                            startActivity(new Intent(LeaderBoardActivity.this,
                                    SpellingbeeTestActivity.class));
                        }
                    }else {
                        startActivity(new Intent(LeaderBoardActivity.this,
                                SpellingbeeTestActivity.class));
                    }
                }
                else {
                    startActivity(new Intent(LeaderBoardActivity.this,
                            LoginActivity.class));
                }

                finish();

            }
        });

    }


    private void initApicall()
    {


        RequestParams params = new RequestParams();

        //params.put("division", divisionName);


        params.put("free_id", UserHelper.getUserFreeId());

        AppRestClient.post(URLHelper.SPELLINGBEE_LEADERBOARD_NEW, params, leaderBoardHandler);
    }

    AsyncHttpResponseHandler leaderBoardHandler = new AsyncHttpResponseHandler() {

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

            listLeaderBoard.clear();


            Wrapper modelContainer = GsonParser.getInstance()
                    .parseServerResponse(responseString);

            if (modelContainer.getStatus().getCode() == 200) {

                JsonArray arrayLeader = modelContainer.getData().get("leaderboard").getAsJsonArray();

                if(arrayLeader.size() <= 0)
                {
                    txtMessage.setVisibility(View.VISIBLE);
                    txtMessage.setText("No Leaderboard found.");
                }
                else
                {
                    txtMessage.setVisibility(View.GONE);
                    List<LeaderBoardModel> listTemp = parseLeaderBoardList(arrayLeader.toString());

                    for(int i=0;i<listTemp.size();i++)
                    {
                        listLeaderBoard.add(listTemp.get(i));
                    }




                    adapter.notifyDataSetChanged();
                }


                String rank = modelContainer.getData().get("rank").getAsString();
                String bestScore = modelContainer.getData().get("best_score").getAsString();
                String division = modelContainer.getData().get("division").getAsString();

                String str = division.toLowerCase();

                String upperString = "";

                if(!TextUtils.isEmpty(str))
                    upperString = str.substring(0,1).toUpperCase() + str.substring(1);

                populateData(rank, bestScore, upperString);


            }


            else {

            }



        };
    };


    private List<LeaderBoardModel> parseLeaderBoardList(String object) {

        List<LeaderBoardModel> tags = new ArrayList<LeaderBoardModel>();
        Type listType = new TypeToken<List<LeaderBoardModel>>() {}.getType();
        tags = (List<LeaderBoardModel>) new Gson().fromJson(object, listType);
        return tags;
    }



    private class AdapterLeaderBoard extends BaseAdapter{

        @Override
        public int getCount() {
            return listLeaderBoard.size();
        }

        @Override
        public Object getItem(int position) {
            return listLeaderBoard.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder;
            if(convertView == null)
            {
                holder = new ViewHolder();

                convertView = LayoutInflater.from(LeaderBoardActivity.this).inflate(R.layout.row_spellingbee_leaderboard, parent, false);

                holder.txtPosition = (TextView)convertView.findViewById(R.id.txtPosition);
                holder.txtName = (TextView)convertView.findViewById(R.id.txtName);
                holder.txtSchoolName = (TextView)convertView.findViewById(R.id.txtSchoolName);
                holder.txtHighScore = (TextView)convertView.findViewById(R.id.txtHighScore);
                holder.txtTimeTaken = (TextView)convertView.findViewById(R.id.txtTimeTaken);

                convertView.setTag(holder);
            }
            else {
                holder = (ViewHolder)convertView.getTag();
            }

            holder.txtPosition.setText(String.valueOf(position+1)+".");
            holder.txtName.setText(listLeaderBoard.get(position).getName());
            holder.txtSchoolName.setText(listLeaderBoard.get(position).getSchoolName());
            holder.txtHighScore.setText("Best Score: "+listLeaderBoard.get(position).getHighScore());
            holder.txtTimeTaken.setText("Time: "+listLeaderBoard.get(position).getTime());

            return convertView;
        }


        class ViewHolder
        {
            TextView txtPosition;
            TextView txtName;
            TextView txtSchoolName;
            TextView txtHighScore;
            TextView txtTimeTaken;

        }


    }


    private void populateData(String rank, String bestScore, String division)
    {
        String score;
        if(TextUtils.isEmpty(PrefSingleton.getInstance().getPreference(UserHelper.getUserFreeId()+SpellingbeeConstants.CURRENT_SCORE_LEADERBOARD)))
            score = "00";
        else
            score = PrefSingleton.getInstance().getPreference(UserHelper.getUserFreeId()+SpellingbeeConstants.CURRENT_SCORE_LEADERBOARD);

        //txtRank.setText("Rank: "+rank);
        txtScoreDetails.setText("Best Score: "+bestScore);

        if(rank.equalsIgnoreCase("0"))
            txtRank.setText("Rank: N/A");
        else
            txtRank.setText("Rank: "+rank);





    }




}
