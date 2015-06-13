package com.champs21.spellingbee;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import com.champs21.freeversion.ChildContainerActivity;
import com.champs21.schoolapp.R;

/**
 * Created by BLACK HAT on 10-Jun-15.
 */
public class SpellingbeeRulesActivity extends ChildContainerActivity {


    private WebView webViewRules;


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
        setContentView(R.layout.activity_spellingbee_rules);

        initView();
        initAction();


    }

    private void initView()
    {
        webViewRules = (WebView)this.findViewById(R.id.webViewRules);
    }

    private void initAction()
    {
        String html = "<font color='white'>"+
                "<p><b><font color = #fbaf30>Online Round</font></b></p>\n" +
                "\n" +
                "<p>The 1st round consists of an online competition. Here, students will have to register at Champs21.com. And get an ID and a password. With this ID and password to play 'Spelling Bee Online Round' - an online vocabulary game where spellers have to spell words correctly after listening to an audio clip. Spellers can play the game as many times as they want. Only their best scores from the game will be considered. Top spellers from each division will be selected for the 2nd Round. </p>\n" +
                "\n" +
                "<p><b><font color = #fbaf30>How to Participate </font></b></p>\n" +
                "\n" +
                "<p>1. Any student studying in Class VI-X is eligible to participate in Spelling Bee 2015.</p>\n" +
                "\n" +
                "<p>2. Students from both English and Bangla mediums are eligible to participate.</p>\n" +
                "\n" +
                "<p>3. The candidates of SSC and O-Level examinations are NOT eligible to participate in Spelling Bee 2015.</p>\n" +
                "\n" +
                "<p>4. The participant must be Bangladeshi.</p>\n" +
                "\n" +
                "<p>5. Any Non-Resident Bangladeshi student can participate in Spelling Bee provided (s)he is a student of Class VI- X.</p>\n" +
                "\n" +
                "<p>6. The speller must not bypass or circumvent normal school activity to study for Spelling Bee.</p>\n" +
                "\n" +
                "<p>7. Upon successfully being selected for the divisional round, the speller must fill up the divisional round access form (to be obtained from the Champs21.com website) and bring it along with him/her to the divisional round venue.</p>\n" +
                "\n" +
                "<p>8. Champs21.com reserves all rights to alter/change the competition format at any point of time</p>\n" +
                "\n" +
                "<p><b><font color = #fbaf30>Note:</font></b> For Non Resident Bangladeshi (N.R.B) students who are willing to participate in the competition, the following apply: </p>\n" +
                "\n" +
                "<p>* N.R.B students from student from class VI-X can participate.</p>\n" +
                "\n" +
                "<p>* Please note that upon qualifying to the divisional round, you will have to travel to Bangladesh to participate in the divisional round. </p>\n" +
                "\n" +
                "<p>Are you already a registered user of www.champs21.com? If yes, just log in to your account and start playing ‘Spelling Bee Online Round’. If you are not registered yet, just register and start playing. </p>\n" +
                "\n" +
                "<p>As per game rules, if you make one mistake, you're out of the game! The good news is that you can play the game as many times as you want until the 1st Round ends on July 15, 2015. Your best score will be considered and only top spellers of your division will be selected for the 2nd Round. </p>\n"
                +"</font>";

        String mime = "text/html";
        String encoding = "utf-8";

        webViewRules.setBackgroundColor(Color.TRANSPARENT);
        webViewRules.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
        webViewRules.getSettings().setJavaScriptEnabled(true);
        webViewRules.loadDataWithBaseURL(null, html, mime, encoding, null);
    }


}
