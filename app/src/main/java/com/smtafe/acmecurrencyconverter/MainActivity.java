package com.smtafe.acmecurrencyconverter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * AcmeCurrencyConverter MainActivity
 *
 * @author Tom Green
 * @version 1.0
 */

public class MainActivity extends AppCompatActivity {

    private Handler handler = new Handler();//Handler object to handle threads
    private JSONObject json;
    private EditText input;
    private EditText exchange;
    private NumberPicker picker1;
    private NumberPicker picker2;
    private EditText comparison1;
    private EditText comparison2;
    private TextView inputLabel;
    private TextView exchangeLabel;
    private TextView compLabel1;
    private TextView compLabel2;
    private TextView updateLabel;

    //background layout to be used to change the background colour
    private ConstraintLayout background_layout;
    //Shared Preferences object to access Preferences
    private SharedPreferences sharedPreferences;
    //Shared Preferences on change listener
    private SharedPreferences.OnSharedPreferenceChangeListener prefListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Get the array of spinner values from arrays.xml
        String[] currencies =
                getResources().getStringArray(R.array.spinner_values);

        //Assign the components of the activity
        input = findViewById(R.id.input);
        picker1 = findViewById(R.id.picker1);
        picker2 = findViewById(R.id.picker2);
        exchange = findViewById(R.id.exchangeRate);
        comparison1 = findViewById(R.id.comparison1);
        comparison2 = findViewById(R.id.comparison2);
        inputLabel = findViewById(R.id.inputLabel);
        exchangeLabel = findViewById(R.id.exchangeLabel);
        compLabel1 = findViewById(R.id.comp_label1);
        compLabel2 = findViewById(R.id.comp_label2);
        updateLabel = findViewById(R.id.updateLabel);
        Button btnSave = findViewById(R.id.btnSave);
        Button btnComparison = findViewById(R.id.btnComparison);
        Button btnRefresh = findViewById(R.id.btnRefresh);

        exchange.setFocusable(false);
        comparison1.setFocusable(false);
        comparison2.setFocusable(false);

        //Setup the pickers
        picker1.setMinValue(0);
        picker2.setMinValue(0);
        picker1.setMaxValue(9);
        picker2.setMaxValue(9);
        picker1.setWrapSelectorWheel(true);
        picker2.setWrapSelectorWheel(true);
        picker1.setDisplayedValues(currencies);
        picker2.setDisplayedValues(currencies);

        background_layout = findViewById(R.id.main_background);
        sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());
        updateSettings(sharedPreferences);

        //Block to restore the values on rotation
        if (savedInstanceState != null) {
            input.setText(savedInstanceState.getString("input"));
            picker1.setValue(savedInstanceState.getInt("picker1"));
            picker2.setValue(savedInstanceState.getInt("picker2"));
            exchange.setText(savedInstanceState.getString("exchange"));
            comparison1.setText(savedInstanceState.getString("comparison1"));
            comparison2.setText(savedInstanceState.getString("comparison2"));
        }

        //Block to get the extras provided by when a save is loaded
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            try {
                json = new JSONObject(extras.getString("json"));
                input.setText(json.optString("input"));
                exchange.setText(json.optString("exchange"));
                comparison1.setText(json.optString("comparison1"));
                comparison2.setText(json.optString("comparison2"));
                picker1.setValue(json.optInt("picker1"));
                picker2.setValue(json.optInt("picker2"));
                createTimeStamp();
            } catch (Exception ex) {
            }
        }

        //if statement to load the json file from the file directory
        if (json == null) {
            try {
                json = JSONFetch.loadJSON(this);
                createTimeStamp();
            } catch (Exception e) {
                Toast.makeText(MainActivity.this,
                        "JSON could not be loaded.",
                        Toast.LENGTH_LONG).show();
            }
        }

        populateFields(input.getText().toString(), picker1.getValue(), picker2.getValue());

        //prefListener to call updateSettings when a preference changes
        prefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                updateSettings(sharedPreferences);
            }
        };

        //Register the prefListener
        sharedPreferences.registerOnSharedPreferenceChangeListener(prefListener);

        //btnSave onClickListener to save the current conversion
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText txtSave = new EditText(MainActivity.this);

                txtSave.setHint("Don't use symbols or spaces");

                //Create an AlertDialog to prompt the user to enter a valid save name
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Information")
                        .setMessage("Please enter a valid save name")
                        .setView(txtSave)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (!txtSave.getText().toString().matches("^[a-zA-Z0-9]+$")) {
                                    Toast.makeText(MainActivity.this,
                                            "Invalid save name, please try again.",
                                            Toast.LENGTH_LONG).show();
                                } else {
                                    try {
                                        JSONFetch.saveConversion(MainActivity.this,
                                                json, txtSave.getText().toString(),
                                                input.getText().toString(),
                                                exchange.getText().toString(),
                                                comparison1.getText().toString(),
                                                comparison2.getText().toString(),
                                                picker1.getValue(),
                                                picker2.getValue());
                                        Toast.makeText(MainActivity.this,
                                                "Conversion Saved",
                                                Toast.LENGTH_LONG).show();
                                    } catch (Exception e) {
                                        Toast.makeText(MainActivity.this,
                                                "Couldn't Save Conversion.",
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .show();
            }
        });

        //btnComparison onClickListener to perform a historical conversion
        btnComparison.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText txtDate = new EditText(MainActivity.this);

                txtDate.setHint("Date Format :'YYYY-MM-DD'");

                //Create an AlertDialog to prompt the user to enter a date
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Information")
                        .setMessage("Please enter a valid date")
                        .setView(txtDate)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Test if the date is valid
                                if (txtDate.getText().toString().matches(
                                        "^\\d{4}\\-(0[1-9]|1[012])\\-(0[1-9]|[12][0-9]|3[01])$")) {
                                    historicalComparison(txtDate.getText().toString());
                                    Toast.makeText(MainActivity.this,
                                            txtDate.getText().toString(),
                                            Toast.LENGTH_LONG).show();
                                } else {
                                    //Notify the user that the date was invalid
                                    Toast.makeText(MainActivity.this,
                                            "Invalid Date, Please Try Again.",
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .show();

                Toast.makeText(MainActivity.this,
                        "Compared",
                        Toast.LENGTH_LONG).show();
            }
        });

        //btnRefresh onClickListener to refresh the json data
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Create a new AlertDialog to warn the user that they are going to
                //use an API call
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Warning")
                        .setMessage("Are you sure you wish to use an API call?")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Call fetchAPIDate method
                                fetchAPIData();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .show();

                Toast.makeText(MainActivity.this,
                        "Refreshed",
                        Toast.LENGTH_LONG).show();
            }
        });

        //input textChangedListener to perform a conversion when a currency is changed
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                    try {
                        //Call populateFields method with the input and picker values
                        populateFields(input.getText().toString(), picker1.getValue(),
                                picker2.getValue());
                    } catch (Exception e) {
                    }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //picker onValueChangeListeners to perform a conversion when a currency is changed
        picker1.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                try {
                    //Call populateFields method with the input and picker values
                    populateFields(input.getText().toString(), picker1.getValue(),
                            picker2.getValue());
                } catch (Exception e) {
                }
            }
        });
        picker2.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                try {
                    //Call populateFields method with the input and picker values
                    populateFields(input.getText().toString(), picker1.getValue(),
                            picker2.getValue());
                } catch (Exception e) {
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i;
        switch (item.getItemId()) {
            case R.id.action_settings:
                //Launch settings activity
                i = new Intent(this, SettingsActivity.class);
                startActivity(i);
                break;
            case R.id.saves_activity:
                //Launch saves activity
                i = new Intent(this, SavesActivity.class);
                startActivity(i);
                break;
            case R.id.user_manual:
                //Put the url of the manual in the extras and Launch WebView activity
                i = new Intent(this, WebViewActivity.class);
                i.putExtra("url", "file:///android_asset/manual.html");
                startActivity(i);
                break;
            case R.id.help:
                //Put the url of the help in the extras and Launch WebView activity
                i = new Intent(this, WebViewActivity.class);
                i.putExtra("url", "file:///android_asset/help.html");
                startActivity(i);
                break;
        }
        return true;
    }

    //Save the activity values to the bundle
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("picker1", MainActivity.this.picker1.getValue());
        outState.putInt("picker2", MainActivity.this.picker2.getValue());
        outState.putString("input", MainActivity.this.input.toString());
        outState.putString("exchange", MainActivity.this.exchange.toString());
        outState.putString("comparison1", MainActivity.this.comparison1.toString());
        outState.putString("comparison2", MainActivity.this.comparison2.toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle inState) {
        super.onRestoreInstanceState(inState);
    }

    //populateFields method that is called to determine the exchange rates and conversions
    private void populateFields(String input, int picker1Val, int picker2Val) {
        JSONObject quotes = json.optJSONObject("quotes");
        double rate;
        String rateString = null;
        double convertedInput = 0.0;

        //Assign the spinners to Strings
        String spinner1Current = assignSpinners(picker1Val);
        String spinner2Current = assignSpinners(picker2Val);
        String currComparison1 = null;
        String currComparison2 = null;

        rate = quotes.optDouble("USD" + spinner1Current);
        rateString = Double.toString(1 / rate);
        //If input is empty use 1 to retrieve an exchange rate
        if (input.isEmpty() || input.contains("android.support"))
            input = "1";
        //Convert the input to USD
        double usdConvert = Double.parseDouble(input) * Double.parseDouble(rateString);
        rate = quotes.optDouble("USD" + spinner2Current);
        //Convert the usdConvert into second currency
        convertedInput = usdConvert * rate;
        //obtain the rate from dividing the convertedInput by the input
        rateString = Double.toString(roundFourDecimal(convertedInput / Double.parseDouble(input)));
        exchange.setText(rateString);
        currComparison1 = roundFourDecimal(input) + " to " + roundFourDecimal(convertedInput);
        currComparison2 = roundFourDecimal(convertedInput) + " to " + roundFourDecimal(input);

        //Set the historicalComparison text
        comparison1.setText(currComparison1);
        comparison2.setText(currComparison2);
    }

    //historicalComparison method to return the historical json data
    private void historicalComparison(final String date) {
        //Create a new thread to fetch the data
        new Thread() {
            @Override
            public void run() {
                final JSONObject json =
                        JSONFetch.historicalComparison(MainActivity.this, date);

                if (json == null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this,
                                    MainActivity.this.getString(R.string.error),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            MainActivity.this.json = json;
                            createTimeStamp();
                        }
                    });
                }
            }
        }.start();
    }

    //fetchAPIData method that is called by the refresh button,
    //it calls to the method getJson in the class JSONFetch
    private void fetchAPIData() {
        //Create a new Thread to fetch the data
        new Thread() {
            @Override
            public void run() {
                final JSONObject json
                        = JSONFetch.getJSON(MainActivity.this);

                if (json == null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this,
                                    MainActivity.this.getString(R.string.error),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            MainActivity.this.json = json;
                            createTimeStamp();
                        }
                    });

                }
            }
        }.start();
    }

    //createTimeStamp method to add the add a timestamp to the activity
    private void createTimeStamp() {
        long unixSeconds = Long.parseLong(json.optString("timestamp"));
        //Convert unixSeconds to milliseconds
        Date date = new Date(unixSeconds * 1000L);
        //Format the date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        //Timezone for AWST time
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        String formattedDate = sdf.format(date);

        updateLabel.setText("Rates from: " + formattedDate);
    }

    //assignSpinners method to return the current value of a spinner
    private String assignSpinners(int value) {
        String current = null;
        switch (value) {
            case 0:
                current = "USD";
                break;
            case 1:
                current = "EUR";
                break;
            case 2:
                current = "JPY";
                break;
            case 3:
                current = "GBP";
                break;
            case 4:
                current = "AUD";
                break;
            case 5:
                current = "CAD";
                break;
            case 6:
                current = "CHF";
                break;
            case 7:
                current = "CNY";
                break;
            case 8:
                current = "SEK";
                break;
            case 9:
                current = "NZD";
                break;
        }
        return current;
    }

    //Rounding Methods to four decimal places
    private double roundFourDecimal(double d) {
        DecimalFormat df = new DecimalFormat("#.####");
        return Double.parseDouble(df.format(d));
    }

    private double roundFourDecimal(String s) {
        double d = Double.parseDouble(s);
        DecimalFormat df = new DecimalFormat("#.####");
        return Double.parseDouble(df.format(d));
    }

    //Clear method to clear the currency comparisons
    private void clearComps() {
        comparison1.setText("");
        comparison2.setText("");
    }

    //updateSettings method to apply settings to the activity
    private void updateSettings(SharedPreferences sharedPrefs) {
        //Get the values from the sharedPreferences and change the settings accordingly
        String textSize = sharedPreferences.getString("size", "1");
        switch (Integer.parseInt(textSize)) {
            case 1:
                setTextSize(8);
                break;
            case 2:
                setTextSize(12);
                break;
            case 3:
                setTextSize(16);
                break;
            case 4:
                setTextSize(20);
                break;
        }

        String font = sharedPreferences.getString("font", "1");
        switch (Integer.parseInt(font)) {
            case 1:
                setTypeFace(Typeface.DEFAULT);
                break;
            case 2:
                setTypeFace(MainActivity.this, R.font.fustschoefferdurandusgoticoantiqua118g);
                break;
            case 3:
                setTypeFace(MainActivity.this, R.font.getlost);
                break;
            case 4:
                setTypeFace(MainActivity.this, R.font.itikaffree);
                break;
            case 5:
                setTypeFace(MainActivity.this, R.font.jessencicero12);
                break;
            case 6:
                setTypeFace(MainActivity.this, R.font.oldexcalibur);
                break;
            case 7:
                setTypeFace(MainActivity.this, R.font.parixhybrid111r);
                break;
            case 8:
                setTypeFace(MainActivity.this, R.font.plompraeng);
                break;
            case 9:
                setTypeFace(MainActivity.this, R.font.publicsansblack);
                break;
            case 10:
                setTypeFace(MainActivity.this, R.font.sannisa);
                break;
            case 11:
                setTypeFace(MainActivity.this, R.font.schoolteacherregular);
                break;
            case 12:
                setTypeFace(MainActivity.this, R.font.stokytdemo);
                break;
            case 13:
                setTypeFace(MainActivity.this, R.font.wavepool);
                break;
            case 14:
                setTypeFace(MainActivity.this, R.font.cartoonfree);
                break;
        }

        String textColour = sharedPreferences.getString("colour", "1");
        switch (Integer.parseInt(textColour)) {
            case 1:
                setTextColor(Color.BLACK);
                break;
            case 2:
                setTextColor(Color.WHITE);
                break;
            case 3:
                setTextColor(Color.RED);
                break;
            case 4:
                setTextColor(Color.GREEN);
                break;
            case 5:
                setTextColor(Color.BLUE);
                break;
            case 6:
                setTextColor(Color.MAGENTA);
                break;
        }

        String backColour = sharedPrefs.getString("background", "2");
        switch (Integer.parseInt(backColour)) {
            case 1:
                background_layout.setBackgroundColor(getResources()
                        .getColor(R.color.background_black));
                break;
            case 2:
                background_layout.setBackgroundColor(getResources()
                        .getColor(R.color.background_white));
                break;
            case 3:
                background_layout.setBackgroundColor(getResources()
                        .getColor(R.color.background_red));
                break;
            case 4:
                background_layout.setBackgroundColor(getResources()
                        .getColor(R.color.background_green));
                break;
            case 5:
                background_layout.setBackgroundColor(getResources()
                        .getColor(R.color.background_blue));
                break;
            case 6:
                background_layout.setBackgroundColor(getResources()
                        .getColor(R.color.background_purple));
                break;
            case 7:
                background_layout.setBackgroundColor(getResources()
                        .getColor(R.color.background_orange));
                break;
            case 8:
                background_layout.setBackgroundColor(getResources()
                        .getColor(R.color.background_yellow));
                break;
            case 9:
                background_layout.setBackgroundColor(getResources()
                        .getColor(R.color.background_gold));
                break;
            case 10:
                background_layout.setBackgroundColor(getResources()
                        .getColor(R.color.background_silver));
                break;
        }

        String picker1Colour = sharedPrefs.getString("spinner_1", "2");
        setPickerColor(picker1, Integer.parseInt(picker1Colour));

        String picker2Colour = sharedPrefs.getString("spinner_2", "2");
        setPickerColor(picker2, Integer.parseInt(picker2Colour));
    }

    //setTextSize method to set the text size in updateSettings
    private void setTextSize(int size) {
        input.setTextSize(size);
        exchange.setTextSize(size);
        comparison1.setTextSize(size);
        comparison2.setTextSize(size);
    }

    //setTypeFace methods to set the font of the text in updateSettings
    private void setTypeFace(Context context, int font) {
        input.setTypeface(ResourcesCompat.getFont(context, font));
        exchange.setTypeface(ResourcesCompat.getFont(context, font));
        comparison1.setTypeface(ResourcesCompat.getFont(context, font));
        comparison2.setTypeface(ResourcesCompat.getFont(context, font));
        inputLabel.setTypeface(ResourcesCompat.getFont(context, font));
        exchangeLabel.setTypeface(ResourcesCompat.getFont(context, font));
        compLabel1.setTypeface(ResourcesCompat.getFont(context, font));
        compLabel2.setTypeface(ResourcesCompat.getFont(context, font));
        updateLabel.setTypeface(ResourcesCompat.getFont(context, font));
    }

    private void setTypeFace(Typeface tf) {
        input.setTypeface(tf);
        exchange.setTypeface(tf);
        comparison1.setTypeface(tf);
        comparison2.setTypeface(tf);
        inputLabel.setTypeface(tf);
        exchangeLabel.setTypeface(tf);
        compLabel1.setTypeface(tf);
        compLabel2.setTypeface(tf);
        updateLabel.setTypeface(tf);
    }

    //setTextColor method to set the text color in updateSettings
    private void setTextColor(int color) {
        input.setTextColor(color);
        exchange.setTextColor(color);
        comparison1.setTextColor(color);
        comparison2.setTextColor(color);
        inputLabel.setTextColor(color);
        exchangeLabel.setTextColor(color);
        compLabel1.setTextColor(color);
        compLabel2.setTextColor(color);
        updateLabel.setTextColor(color);
    }

    //setPickerColor method to set the background color of the pickers
    private void setPickerColor(NumberPicker picker, int pickerColor) {
        switch (pickerColor) {
            case 1:
                picker.setBackgroundColor(getResources()
                        .getColor(R.color.background_black));
                break;
            case 2:
                picker.setBackgroundColor(getResources()
                        .getColor(R.color.background_white));
                break;
            case 3:
                picker.setBackgroundColor(getResources()
                        .getColor(R.color.background_red));
                break;
            case 4:
                picker.setBackgroundColor(getResources()
                        .getColor(R.color.background_green));
                break;
            case 5:
                picker.setBackgroundColor(getResources()
                        .getColor(R.color.background_blue));
                break;
            case 6:
                picker.setBackgroundColor(getResources()
                        .getColor(R.color.background_purple));
                break;
            case 7:
                picker.setBackgroundColor(getResources()
                        .getColor(R.color.background_orange));
                break;
            case 8:
                picker.setBackgroundColor(getResources()
                        .getColor(R.color.background_yellow));
                break;
            case 9:
                picker.setBackgroundColor(getResources()
                        .getColor(R.color.background_gold));
                break;
            case 10:
                picker.setBackgroundColor(getResources()
                        .getColor(R.color.background_silver));
                break;
        }
    }
}
