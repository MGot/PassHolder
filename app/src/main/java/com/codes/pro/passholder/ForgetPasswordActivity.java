package com.codes.pro.passholder;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by michu on 31.05.15.
 */
public class ForgetPasswordActivity extends ActionBarActivity {

    private Button reminder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        reminder = (Button) findViewById(R.id.reminder);

    }

    /**
     * method to remind password for user
     * @param v
     */
    public void remind(View v) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"michal.walkowiak93@gmail.com"});
        i.putExtra(Intent.EXTRA_SUBJECT, "subject of email");
        i.putExtra(Intent.EXTRA_TEXT   , "body of email");
        try {
            startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(ForgetPasswordActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }
}
