package com.example.networksocialapplication.time_current;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.EditText;
import android.widget.TimePicker;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;

public class DatePicker {
    private Context mContext;

    public DatePicker(Context context) {
        mContext = context;
    }

    public DatePicker() {
    }

    public void showDatePicker(final EditText editText) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day_of_month = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, android.R.style.Theme_Holo_Light_Dialog_MinWidth, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                String dateOfBirth = dayOfMonth + "/" + month + "/" + year;
                editText.setText(dateOfBirth);
            }
        }, year, month, day_of_month);
        datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.rgb(52, 73, 85)));
        datePickerDialog.show();

    }

    public void showTimePicker(final EditText editText){
        Calendar calendar = Calendar.getInstance();
        final int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                editText.setText(hour +" : "+ minute);
            }
        }, hour,minute,true);
        timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.rgb(52, 73, 85)));
        timePickerDialog.show();
    }
}
