package co.jaypandya.myaddressplus;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Spinner provincialSpinner, designationSpinner;
    Button submitButton;
    EditText firstName, lastName, address, country, postalCode;
    String sFirstName, sLastName, sAddress, sCountry, sPostalCode, sProvince, sDesignation;
    String aboutMe;

    private Uri peopleUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //wiring up edittexts
        firstName = (EditText)findViewById(R.id.first_name);
        lastName = (EditText)findViewById(R.id.last_name);
        address = (EditText)findViewById(R.id.address);
        country = (EditText)findViewById(R.id.country);
        postalCode = (EditText)findViewById(R.id.postal_code);

        // setting up the spinner
        designationSpinner = (Spinner) findViewById(R.id.designations);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.designations, R.layout.support_simple_spinner_dropdown_item);
        designationSpinner.setAdapter(adapter2);

        // setting up the spinner
        provincialSpinner = (Spinner) findViewById(R.id.province_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.provinces, R.layout.support_simple_spinner_dropdown_item);
        provincialSpinner.setAdapter(adapter);

        Bundle extras = getIntent().getExtras();

        peopleUri = (savedInstanceState == null) ? null : (Uri) savedInstanceState.getParcelable(MyPeopleContentProvider.CONTENT_ITEM_TYPE);

        if (extras != null) {
            peopleUri = extras.getParcelable(MyPeopleContentProvider.CONTENT_ITEM_TYPE);
            fillData(peopleUri);
        }

        submitButton = (Button)findViewById(R.id.submit_button);
        submitButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if (TextUtils.isEmpty(firstName.getText().toString())) {
                    makeToast();
                } else {
                    setResult(RESULT_OK);
                    finish();
                }

            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void fillData(Uri uri){
        String[] projection = {
                PeopleTableHandler.COLUMN_DESIGNATION, PeopleTableHandler.COLUMN_FIRST_NAME, PeopleTableHandler.COLUMN_LAST_NAME, PeopleTableHandler.COLUMN_ADDRESS, PeopleTableHandler.COLUMN_PROVINCE, PeopleTableHandler.COLUMN_COUNTRY, PeopleTableHandler.COLUMN_POSTAL_CODE
        };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            String designation = cursor.getString(cursor.getColumnIndexOrThrow(PeopleTableHandler.COLUMN_DESIGNATION));
            for (int i = 0; i < designationSpinner.getCount(); i++) {

                String s = (String) designationSpinner.getItemAtPosition(i);
                if (s.equalsIgnoreCase(designation)) {
                    designationSpinner.setSelection(i);
                }
            }
            String province = cursor.getString(cursor.getColumnIndexOrThrow(PeopleTableHandler.COLUMN_PROVINCE));
            for (int i = 0; i < provincialSpinner.getCount(); i++) {

                String s = (String) provincialSpinner.getItemAtPosition(i);
                if (s.equalsIgnoreCase(province)) {
                    provincialSpinner.setSelection(i);
                }
            }

            firstName.setText(cursor.getString(cursor.getColumnIndexOrThrow(PeopleTableHandler.COLUMN_FIRST_NAME)));
            lastName.setText(cursor.getString(cursor.getColumnIndexOrThrow(PeopleTableHandler.COLUMN_LAST_NAME)));
            address.setText(cursor.getString(cursor.getColumnIndexOrThrow(PeopleTableHandler.COLUMN_ADDRESS)));
            country.setText(cursor.getString(cursor.getColumnIndexOrThrow(PeopleTableHandler.COLUMN_COUNTRY)));
            postalCode.setText(cursor.getString(cursor.getColumnIndexOrThrow(PeopleTableHandler.COLUMN_POSTAL_CODE)));

            // Always close the cursor
            cursor.close();
        }
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();
        outState.putParcelable(MyPeopleContentProvider.CONTENT_ITEM_TYPE, peopleUri);
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveState();
    }

    private void saveState() {
        sDesignation = (String) designationSpinner.getSelectedItem();
        sFirstName = firstName.getText().toString();
        sLastName = lastName.getText().toString();
        sAddress = address.getText().toString();
        sCountry = country.getText().toString();
        sPostalCode = postalCode.getText().toString();
        sProvince = provincialSpinner.getSelectedItem().toString();


        if (sFirstName.length() == 0 || sLastName.length() == 0 || sAddress.length() == 0 || sCountry.length() == 0 || postalCode.length() == 0) {
            return;
        }

        ContentValues values = new ContentValues();
        values.put(PeopleTableHandler.COLUMN_DESIGNATION, sDesignation);
        values.put(PeopleTableHandler.COLUMN_FIRST_NAME, sFirstName);
        values.put(PeopleTableHandler.COLUMN_LAST_NAME, sLastName);
        values.put(PeopleTableHandler.COLUMN_ADDRESS, sAddress);
        values.put(PeopleTableHandler.COLUMN_PROVINCE, sProvince);
        values.put(PeopleTableHandler.COLUMN_COUNTRY, sCountry);
        values.put(PeopleTableHandler.COLUMN_POSTAL_CODE, sPostalCode);

        if (peopleUri == null) {
            peopleUri = getContentResolver().insert(MyPeopleContentProvider.CONTENT_URI, values);
        } else {
            getContentResolver().update(peopleUri, values, null, null);
        }
    }

    private void makeToast() {
        Toast.makeText(MainActivity.this, "Please maintain a summary", Toast.LENGTH_LONG).show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            sFirstName = firstName.getText().toString();
            sLastName = lastName.getText().toString();
            sAddress = address.getText().toString();
            sCountry = country.getText().toString();
            sPostalCode = postalCode.getText().toString();
            sProvince = provincialSpinner.getSelectedItem().toString();
            sDesignation = designationSpinner.getSelectedItem().toString();
            aboutMe = "ABOUT ->" + sDesignation + " First Name: " + sFirstName + " Last Name: " + sLastName + " Address: " + sAddress + " Province: " + sProvince + " Country: " + sCountry + " Postal Code: " + sPostalCode;

            Intent intent = new Intent(getApplicationContext(), AboutActivity.class);
            intent.putExtra("co.jaypandya.myaddress.aboutme", aboutMe);
            startActivity(intent);
            return true;
        } else if( id == R.id.clear ){
            firstName.getText().clear();
            lastName.getText().clear();
            address.getText().clear();
            country.getText().clear();
            postalCode.getText().clear();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
