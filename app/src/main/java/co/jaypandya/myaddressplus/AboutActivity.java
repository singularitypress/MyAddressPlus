package co.jaypandya.myaddressplus;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    TextView aboutMe;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        Bundle extras = getIntent().getExtras();
        String about = extras.getString("co.jaypandya.myaddress.aboutme");
        aboutMe = (TextView)findViewById(R.id.about_me);
        aboutMe.setText(about);
    }
}
