package com.taxi.nanny.views.driver.schedule;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.applandeo.materialcalendarview.EventDay;
import com.taxi.nanny.MainActivity;
import com.taxi.nanny.R;
import com.taxi.nanny.views.BaseActivity;
import com.taxi.nanny.views.FullImage;
import com.taxi.nanny.views.driver.vehicles.VehicleDetails;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.OnClick;

public class NotePreviewActivity extends BaseActivity {
    @Override
    protected int getContentId() {
        return R.layout.note_preview_activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHeaderText("Schedule Detail");
        Intent intent = getIntent();

        TextView note = (TextView) findViewById(R.id.note);

        if (intent != null) {
            Object event = intent.getParcelableExtra(DriverSchudule.EVENT);

            if(event instanceof MyEventDay)
            {
                MyEventDay myEventDay = (MyEventDay)event;
              //  getSupportActionBar().setTitle(getFormattedDate(myEventDay.getCalendar().getTime()));
                note.setText(myEventDay.getNote());

                return;
            }

            if(event instanceof EventDay){
                EventDay eventDay = (EventDay)event;
                getSupportActionBar().setTitle(getFormattedDate(eventDay.getCalendar().getTime()));
            }
        }
    }

    public static String getFormattedDate(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
        return simpleDateFormat.format(date);
    }


    @OnClick({R.id.img_back_btn})
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.img_back_btn:
                finish();
                break;


        }
    }



}

