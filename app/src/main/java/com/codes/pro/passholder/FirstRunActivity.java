package com.codes.pro.passholder;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;

import java.nio.charset.Charset;
import java.util.regex.Pattern;


public class FirstRunActivity extends ActionBarActivity {

    private EditText setEmail;
    private EditText setPass;
    private Button submitButt;
    public SQLiteDatabase myDb = null;

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
        final HashCode password = Hashing.sha1().hashString(setPass.getText().toString(), Charset.defaultCharset());

        if (checkPassword(password)) {
            if (checkEmail(email)) {
                sendData(email, password);
                Intent main = new Intent(getApplicationContext(), MainActivity.class);
                main.putExtra("email", email);
                main.putExtra("password", password.toString());
                setResult(Activity.RESULT_OK, main);
                finish();
            } else {
                Toast.makeText(FirstRunActivity.this, "Wrong email address!", Toast.LENGTH_SHORT).show();
            }
        }
    }


    /**
     * method to check if email is correct
     * @param target
     * @return
     */
    private boolean checkEmail(CharSequence target) {
        //return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        return true;
    }

    /**
     * method to validate password, check some parameters
     * @param password
     * @return true if pass is ok, otherwise return false
     */
    private boolean checkPassword(HashCode password) {
        /*String passToCheck = setPass.getText().toString();
        final Pattern hasUppercase = Pattern.compile("[A-Z]");
        final Pattern hasLowercase = Pattern.compile("[a-z]");
        final Pattern hasNumber = Pattern.compile("\\d");
        final Pattern hasSpecialChar = Pattern.compile("[^a-zA-Z0-9 ]");
        if(passToCheck.length() < 4) {   //check length of pass
            Toast.makeText(FirstRunActivity.this, "Password must be longer than 3 characters!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!hasUppercase.matcher(passToCheck).find()) {   //check if in pass is big letter like A J S
            Toast.makeText(FirstRunActivity.this, "Password must contain at least 1 big letter!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!hasLowercase.matcher(passToCheck).find()) {  //check if in pass is small letter like a m s
            Toast.makeText(FirstRunActivity.this, "Password must contain at least 1 small letter!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!hasNumber.matcher(passToCheck).find()) { //check if there is number in password like 2 3 1
            Toast.makeText(FirstRunActivity.this, "Password must contain at least 1 number!", Toast.LENGTH_SHORT).show();

        }
        if(!hasSpecialChar.matcher(passToCheck).find()) {    //check if there is a special character in password like # ! *
            Toast.makeText(FirstRunActivity.this, "Password must contain at least 1 special character!", Toast.LENGTH_SHORT).show();
            return false;
        }
        else*/
            return true;
    }

    /**
     * method to send email and pass to MainActivity and save it in SharedPreferences
     * @param email
     * @param password
     */
    private void sendData(String email, HashCode password) {
        MainActivity.userEmail = email;
        MainActivity.userPass = password;
        //MainActivity.emailFromRegister = email;
        //MainActivity.passFromRegister = password;
        SharedPreferences infos = getSharedPreferences("userInfos", 0);
        infos.edit().putString("userEmail", email).commit();
        infos.edit().putString("userPass", password.toString()).commit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Toast.makeText(FirstRunActivity.this, "Hope you'll back here! Sayonara", Toast.LENGTH_SHORT).show();
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
