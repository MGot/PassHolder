package com.codes.pro.passholder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;

import java.nio.charset.Charset;


public class FirstRunActivity extends ActionBarActivity {

    private EditText setEmail;
    private EditText setPass;
    private Button submitButt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_run);

        setEmail = (EditText) findViewById(R.id.setEmail);
        setPass = (EditText) findViewById(R.id.setPass);
        submitButt = (Button) findViewById(R.id.submitButt);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        Toast.makeText(FirstRunActivity.this, "Welcome! Write Your e-mail address and password.", Toast.LENGTH_SHORT).show();

        getMenuInflater().inflate(R.menu.menu_first_run, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * method is called after clicked on Submit button
     * @param v
     */
    public void submit(View v) {
        final String email = setEmail.getText().toString();
        checkEmail(email);

        final HashCode password = Hashing.sha1().hashString(setPass.getText().toString(), Charset.defaultCharset());

        if (checkPassword(password)) {
            if (checkEmail(email)) {
                sendData(email, password);
                Intent main = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(main);
            } else {
                Toast.makeText(FirstRunActivity.this, "Wrong email address!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(FirstRunActivity.this, "Password must be longer or equal than 4 character!", Toast.LENGTH_SHORT).show();
        }
    }


    private boolean checkEmail(CharSequence target) {
        //return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        return true;
    }

    private boolean checkPassword(HashCode password) {
        if(setPass.getText().toString().length() < 4)
            return false;
        else
            return true;
    }

    private void sendData(String email, HashCode password) {
        MainActivity.userEmail = email;
        MainActivity.userPass = password;
        SharedPreferences infos = getSharedPreferences("userInfos", 0);
        infos.edit().putString("userEmail", email).commit();
        infos.edit().putString("userPass", password.toString()).commit();
    }
}
