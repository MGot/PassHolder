package com.codes.pro.passholder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
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
        Toast.makeText(FirstRunActivity.this, "First time so register", Toast.LENGTH_SHORT).show();

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
        checkPass(password);

        Intent main = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(main);
    }

    /**
     * method to check if login is correct, cannot get null or asdjhda
     * @param email
     */
    private void checkEmail(String email)
    {
        MainActivity.userEmail = email;
        Toast.makeText(FirstRunActivity.this, "Login " + email, Toast.LENGTH_SHORT).show();
        SharedPreferences infos = getSharedPreferences("userInfos", 0);
        infos.edit().putString("userEmail", email).commit();
    }

    /**
     * method to check if pass is correct - has at least 8 characters, big letters and special characters
     * @param password
     */
    private void checkPass(HashCode password)
    {
        MainActivity.userPass = password;
        Toast.makeText(FirstRunActivity.this, "Pass " + password, Toast.LENGTH_SHORT).show();
        SharedPreferences infos = getSharedPreferences("userInfos", 0);
        infos.edit().putString("userPass", password.toString()).commit();
    }
}
