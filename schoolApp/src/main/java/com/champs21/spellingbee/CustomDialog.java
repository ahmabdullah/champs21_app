package com.champs21.spellingbee;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.champs21.schoolapp.R;
import com.champs21.schoolapp.utils.CountDownTimerPausable;

import java.util.Random;

/**
 * Created by BLACK HAT on 17-Jun-15.
 */
public class CustomDialog extends Dialog{

    private TextView txtTime;
    private ImageButton btnSkip;
    private CountDownTimerPausable countDownTimer = null;
    private IOnSkipButtonClick listener;
    private LinearLayout layoutAdHolder;



    public CustomDialog(Context context, IOnSkipButtonClick listener) {
        super(context);

        this.listener = listener;

        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        setContentView(R.layout.spellingbee_custom_dialog);

        setCancelable(false);


        int divierId = this.getContext().getResources()
                .getIdentifier("android:id/titleDivider", null, null);
        View divider = this.findViewById(divierId);
        divider.setBackgroundColor(Color.TRANSPARENT);


        initView();
        initAction();
    }


    private void initView()
    {
        txtTime = (TextView)this.findViewById(R.id.txtTime);
        btnSkip = (ImageButton)this.findViewById(R.id.btnSkip);
        layoutAdHolder = (LinearLayout)this.findViewById(R.id.layoutAdHolder);
    }

    private void initAction()
    {
        initTimer();
        btnSkip.setEnabled(false);

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CustomDialog.this.dismiss();
                listener.onSkipButtonClicked();
            }
        });


        int adNum = getRandomNumberInRange(1, 7);

        switch (adNum)
        {
            case 1:
                layoutAdHolder.setBackgroundResource(R.drawable.spellingbee_dialog_ad_1);
                break;

            case 2:
                layoutAdHolder.setBackgroundResource(R.drawable.spellingbee_dialog_ad_2);
                break;

            case 3:
                layoutAdHolder.setBackgroundResource(R.drawable.spellingbee_dialog_ad_3);
                break;

            case 4:
                layoutAdHolder.setBackgroundResource(R.drawable.spellingbee_dialog_ad_4);
                break;

            case 5:
                layoutAdHolder.setBackgroundResource(R.drawable.spellingbee_dialog_ad_5);
                break;

            case 6:
                layoutAdHolder.setBackgroundResource(R.drawable.spellingbee_dialog_ad_6);
                break;

            case 7:
                layoutAdHolder.setBackgroundResource(R.drawable.spellingbee_dialog_ad_7);
                break;

            default:
                layoutAdHolder.setBackgroundResource(R.drawable.spellingbee_dialog_ad_1);
                break;


        }


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
            this.countDownTimer = new CountDownTimerPausable(10000, 1) {
                public void onTick(long millisUntilFinished) {

                    txtTime.setText(String.valueOf(millisUntilFinished / 1000));


                }

                public void onFinish() {
                    txtTime.setText("0");
                    btnSkip.setEnabled(true);
                    btnSkip.setImageResource(R.drawable.spellingbee_btn_dialog_ad);


                }
            };

            this.countDownTimer.start();
        }
    }


    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(this.countDownTimer != null)
        {
            this.countDownTimer.cancel();
            this.countDownTimer = null;
        }
    }


    private int getRandomNumberInRange(int minVal, int maxVal)
    {
        Random r = new Random();
        int num = r.nextInt(maxVal - minVal) + minVal;

        return num;
    }
}
