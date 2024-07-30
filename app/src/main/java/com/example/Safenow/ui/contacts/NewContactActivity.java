package com.example.Safenow.ui.contacts;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.Safenow.R;

public class NewContactActivity extends AppCompatActivity {

    // creating a variables for our button and edittext. 
    private EditText firstNameEdt, lastNameEdt, phoneNumberEdt;

    // creating a constant string variable for our  
    // Contact name, description and duration. 
    public static final String EXTRA_ID = "com.gtappdevelopers.gfgroomdatabase.EXTRA_ID";
    public static final String EXTRA_FIRST_NAME = "com.gtappdevelopers.gfgroomdatabase.EXTRA_FIRST_NAME";
    public static final String EXTRA_LAST_NAME = "com.gtappdevelopers.gfgroomdatabase.EXTRA_LAST_NAME";
    public static final String EXTRA_PHONE_NUMBER = "com.gtappdevelopers.gfgroomdatabase.EXTRA_PHONE_NUMBER";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_contact);

        // initializing our variables for each view. 
        firstNameEdt = findViewById(R.id.idEdtfirstName);
        lastNameEdt = findViewById(R.id.idEdtlastName);
        phoneNumberEdt = findViewById(R.id.idEdtphoneNumber);
        Button contactBtn = findViewById(R.id.idBtnSaveContact);

        // below line is to get intent as we 
        // are getting data via an intent. 
        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_ID)) {
            // if we get id for our data then we are 
            // setting values to our edit text fields. 
            firstNameEdt.setText(intent.getStringExtra(EXTRA_FIRST_NAME));
            lastNameEdt.setText(intent.getStringExtra(EXTRA_LAST_NAME));
            phoneNumberEdt.setText(intent.getStringExtra(EXTRA_PHONE_NUMBER));
        }
        // adding on click listener for our save button. 
        contactBtn.setOnClickListener(v -> {
            // getting text value from edittext and validating if
            // the text fields are empty or not.
            String firstName = firstNameEdt.getText().toString();
            String lastName = lastNameEdt.getText().toString();
            String phoneNumber = phoneNumberEdt.getText().toString();
            if (!phoneNumber.matches("^\\+[0-9]+$")) {
                Toast.makeText(NewContactActivity.this, "Please enter a valid phone number.", Toast.LENGTH_SHORT).show();
            }
            if (firstName.isEmpty() || lastName.isEmpty() || phoneNumber.isEmpty()) {
                Toast.makeText(NewContactActivity.this, "Please enter the valid contact details.", Toast.LENGTH_SHORT).show();
                return;
            }
            // calling a method to save our Contact.
            saveContact(firstName, lastName, phoneNumber);
        });
    }

    private void saveContact(String firstName, String lastName, String phoneNumber) {
        // inside this method we are passing  
        // all the data via an intent. 
        Intent data = new Intent();

        // in below line we are passing all our Contact detail. 
        data.putExtra(EXTRA_FIRST_NAME, firstName);
        data.putExtra(EXTRA_LAST_NAME, lastName);
        data.putExtra(EXTRA_PHONE_NUMBER, phoneNumber);
        int id = getIntent().getIntExtra(EXTRA_ID, -1);
        if (id != -1) {
            // in below line we are passing our id. 
            data.putExtra(EXTRA_ID, id);
        }

        // at last we are setting result as data. 
        setResult(RESULT_OK, data);

        // displaying a toast message after adding the data 
        Toast.makeText(this, "Contact has been saved to Room Database. ", Toast.LENGTH_SHORT).show();
    }
}