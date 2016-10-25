package com.chaibytes.newyorktimessearch.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.chaibytes.newyorktimessearch.R;
import com.chaibytes.newyorktimessearch.fragment.DatePickerFragment;
import com.chaibytes.newyorktimessearch.model.Settings;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SettingsActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    EditText etBeginDate;
    Button btnSave;
    Settings settings;
    Calendar c;
    DatePickerFragment newFragment;
    Spinner spinner;
    CheckBox arts, fashion, sports;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        newFragment = new DatePickerFragment();
        etBeginDate = (EditText) findViewById(R.id.etBegindate);
        btnSave = (Button) findViewById(R.id.btnSave);
        spinner = (Spinner) findViewById(R.id.spSortOrder);
        arts = (CheckBox) findViewById(R.id.cbArts);
        fashion = (CheckBox) findViewById(R.id.cbFashion);
        sports = (CheckBox) findViewById(R.id.cbSports);

        getViewItems();

        settings = getIntent().getParcelableExtra("settings");
        setViewItems();

    }

    private void setViewItems() {
        String date = "";
        if (settings != null && settings.getCalendar() != null) {
            date = getDateString(settings.getCalendar());
        } else {
            date = getDateString(Calendar.getInstance());
        }
        etBeginDate.setText(date);

        int index = 0;
        SpinnerAdapter adapter = spinner.getAdapter();
        String option = settings.getOption();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).equals(option)) {
                index = i;
                break;
            }
        }
        // Set spinner
        spinner.setSelection(index);

        // Set checkboxes
        arts.setChecked(settings.isArtChecked());
        fashion.setChecked(settings.isFashionChecked());
        sports.setChecked(settings.isSportsChecked());

        arts.setOnCheckedChangeListener(checkListener);
        fashion.setOnCheckedChangeListener(checkListener);
        sports.setOnCheckedChangeListener(checkListener);


    }

    public void onSubmit(View v) {
        this.finish();
    }

    CompoundButton.OnCheckedChangeListener checkListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
            switch (compoundButton.getId()) {
                case R.id.cbArts:
                    settings.setArtChecked(checked);
                    break;
                case R.id.cbFashion:
                    settings.setFashionChecked(checked);
                    break;
                case R.id.cbSports:
                    settings.setSportsChecked(checked);
                    break;
            }
        }

    };
    private void getViewItems() {
        etBeginDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle data = new Bundle();
                data.putParcelable("settings", settings);
                // attach to an onclick handler to show the date picker
                newFragment.setArguments(data);
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

        // SelectOption
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                settings.setOption(adapterView.getItemAtPosition(pos).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // Save button
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.putExtra("settings", settings);
                setResult(RESULT_OK, i);
                finish();
            }
        });
    }

    // handle the date selected
    @Override
    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
        // store the values selected into a Calendar instance
        c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, monthOfYear);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        etBeginDate.setText(getDateString(c));
        settings.setCalendar(c);
    }

    private String getDateString(Calendar cal) {
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yy");
        return format.format(cal.getTime());
    }

}
