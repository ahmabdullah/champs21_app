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
        String html ="<font color='white'>"+
                "<p>\n" +
                "    1. To register, tap Log-in and enter your valid email address and choose a password. \n" +
                "</p>\n" +
                "<p>\n" +
                "    2. You can guess the spelling from five clues - Pronunciation, English Meaning, Bangla Meaning, Parts of Speech, Use in a Sentence. \n" +
                "</p>\n" +
                "<p>\n" +
                "    3. If you spell a word wrong, game will be over. You can play again any time. The highest of your scores will be considered to determine your rank among other players.\n" +
                "</p>\n" +
                "<p>\n" +
                "    4. Play for as many words as you want. The more words you attempt, the more is the chance of scoring high.\n" +
                "</p>\n" +
                "<p>\n" +
                "    5. Check your score in the leaderboard. For each correctly spelt word, you get 1 point. Please follow British Standard Spelling to submit answer. \n" +
                "</p>\n" +
                "<p>\n" +
                "    6. Don't forget to watch Spelling Bee episodes on Channel i on Wednesday and Thursday at 7:50 pm (BST) for higher score.\n" +
                "</p>\n" +
                "<p>\n" +
                "    7. You can win exciting prizes at the end of Spelling Bee Season 4. Top ranked 30 (thirty) players, who would be in the Leader Board at end of the Spelling Bee Grand Finale episode airing, will win a digital camera (point & shoot) each.\n" +
                "</p>\n"+
                "<p>\n" +
                "   8. Anyone, of any age, from any location can play Spell Champs 2015. However, only Bangladeshis are eligible for winning prizes. \n" +
                "</p>\n"+
                "    <br/>\n" +
                "</p>\n"+
                "</font>";

        String mime = "text/html";
        String encoding = "utf-8";

        webViewRules.setBackgroundColor(Color.TRANSPARENT);
        webViewRules.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
        webViewRules.getSettings().setJavaScriptEnabled(true);
        webViewRules.loadDataWithBaseURL(null, html, mime, encoding, null);
    }


}
