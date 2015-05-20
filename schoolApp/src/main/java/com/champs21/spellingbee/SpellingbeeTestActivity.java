package com.champs21.spellingbee;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.champs21.schoolapp.R;
import com.champs21.schoolapp.model.SpellingbeeDataModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by BLACK HAT on 18-May-15.
 */
public class SpellingbeeTestActivity extends Activity {


    private List<SpellingbeeDataModel> data = null;


    private List<SpellingbeeDataModel> list2;
    private List<SpellingbeeDataModel> list3;


    private TextView txtView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_spelling_demo);
        txtView = (TextView)this.findViewById(R.id.txtView);

        doTestData();
    }


    private void doTestData()
    {
        BgLoadData bgData = new BgLoadData();
        bgData.execute("demo.xml");

        list2 = new ArrayList<SpellingbeeDataModel>();
        list3 = new ArrayList<SpellingbeeDataModel>();


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

            Collections.shuffle(data);

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
            if(pd.isShowing())
            {
                pd.dismiss();
            }

            enlist(data);

            populate();

        }


    }


    private void enlist(List<SpellingbeeDataModel> data)
    {
        for(SpellingbeeDataModel obj : data)
        {
            if(obj.getLevel().equalsIgnoreCase("2"))
            {
                list2.add(obj);
            }

            if(obj.getLevel().equalsIgnoreCase("3"))
            {
                list3.add(obj);
            }
        }

        Log.e("TOTAL_LIST_SIZE", "size: "+data.size());

        Log.e("LIST_2", "size: "+list2.size());
        Log.e("LIST_3", "size: "+list3.size());


    }

    private void populate()
    {
        for(SpellingbeeDataModel obj : data)
        {
            if(obj.getId() == 104)
            {
                txtView.setText(obj.getBanglaMeaning());
            }
        }


    }
}
