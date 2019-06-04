package com.smtafe.acmecurrencyconverter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

/**
 * AcmeCurrencyConverter SavesActivity
 *
 * @author Tom Green
 * @version 1.0
 */

public class SavesActivity extends AppCompatActivity {

    private String[] values;

    private JSONObject json;
    private Button btnLoad;
    private Button btnDelete;
    private NumberPicker savePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saves);

        //Assign the components of the activity
        btnLoad = findViewById(R.id.btnLoad);
        btnDelete = findViewById(R.id.btnDelete);
        savePicker = findViewById(R.id.savePicker);

        //Setup the savePicker
        savePicker.setMinValue(0);
        savePicker.setWrapSelectorWheel(true);

        try {
            populatePicker(SavesActivity.this);
        } catch (NullPointerException ex) {
            Toast.makeText(SavesActivity.this,
                    "Could not load.",
                    Toast.LENGTH_LONG).show();
        }

        //btnLoad onClickListener to load the selected conversion
        btnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //Load the save and then put it in the Intents extra under the key "json"
                    json = JSONFetch.loadConversion(
                            SavesActivity.this, values[savePicker.getValue()]);
                    Intent i = new Intent(SavesActivity.this, MainActivity.class);
                    i.putExtra("json", json.toString());
                    startActivity(i);
                } catch (JSONException | IOException ex) {
                    Toast.makeText(SavesActivity.this,
                            "Could not load Conversion",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        //btnDelete onClickListener to delete the selected conversion
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Create a new AlertDialog to confirm that the user wishes to delete a save
                new AlertDialog.Builder(SavesActivity.this)
                        .setTitle("Warning")
                        .setMessage("Are you sure you want to delete this Save?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteFile(SavesActivity.this, savePicker.getValue());
                                Toast.makeText(SavesActivity.this,
                                        "Save Deleted!",
                                        Toast.LENGTH_SHORT).show();
                                populatePicker(SavesActivity.this);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .show();
            }
        });
    }

    //populatePicker method to populate the saves picker
    private void populatePicker(Context context) throws NullPointerException {
        File savePath = new File(context.getFilesDir(), "saves");
        File[] saves = savePath.listFiles();

        //If there are no saves, notify the user
        if (saves == null || saves.length == 0) {
            values = new String[]{"No Saves Found", "Add Saves on the conversion activity"};
            savePicker.setMaxValue(1);
        } else {
            values = new String[saves.length];

            //Store a cleanFileName in values
            for (int i = 0; i < saves.length; i++) {
                values[i] = cleanFileName(context, saves[i].toString());
            }

            if (values.length != 0)
                savePicker.setMaxValue(values.length - 1);
            else
                savePicker.setMaxValue(0);
        }
        //Set the picker's displayed values to values
        savePicker.setDisplayedValues(values);
    }

    //cleanFileName method to remove the file path and extension from the file so
    //it can be displayed in the picker
    private String cleanFileName(Context context, String fileName) {
        fileName = fileName.replace(context.getFilesDir().toString() + "/saves/", "");
        fileName = fileName.replace(".json", "");
        return fileName;
    }

    //deleteFile method to delete the file selected with the picker
    private void deleteFile(Context context, int i) {
        File savePath = new File(context.getFilesDir(), "saves/" + values[i] + ".json");
        //If the file was either deleted or not, notify the user
        if (savePath.delete()) {
            Toast.makeText(SavesActivity.this,
                    values[i] + " deleted",
                    Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(SavesActivity.this,
                    values[i] + " could not be deleted",
                    Toast.LENGTH_LONG).show();
        }
    }
}
