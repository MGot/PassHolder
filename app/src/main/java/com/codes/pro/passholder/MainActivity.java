package com.codes.pro.passholder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.codes.pro.passholder.R;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;

import java.nio.charset.Charset;
import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    private EditText pass;
    private Button login;

    private String emailFromRegister;
    private String passFromRegister;
    public static String userEmail = "";
    public static HashCode userPass = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        pass = (EditText) findViewById(R.id.editText);
        login = (Button) findViewById(R.id.loginButt);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        SharedPreferences settings = getSharedPreferences("MyPrefsFile", 0);

        if (settings.getBoolean("firstRun", true)) {               //the app is being launched for first time- register time
            openFirstRun(); //go to register menu

            // record the fact that the app has been started at least once
            settings.edit().putBoolean("firstRun", false).commit();
        }

        getUserInfos();

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * method to get email and pass from registered user
     */
    private void getUserInfos(){
        SharedPreferences infos = getSharedPreferences("userInfos", 0);
        emailFromRegister = infos.getString("userEmail", "empty");
        passFromRegister = infos.getString("userPass", "empty");
        Toast.makeText(MainActivity.this, "Email given " + emailFromRegister, Toast.LENGTH_SHORT).show();
        Toast.makeText(MainActivity.this, "Pass given " + passFromRegister, Toast.LENGTH_SHORT).show();
    }

    /**
     * method to open registation frame -> FirstRunActivity
     */
    private void openFirstRun(){
        Intent intent = new Intent(this, FirstRunActivity.class);
        startActivity(intent);
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

    @Override
    public void onResume() {
        super.onResume();
    }

    public void logIn(View v) {

        final HashCode password = Hashing.sha1().hashString(pass.getText().toString(), Charset.defaultCharset());
        Toast.makeText(MainActivity.this, "Pass1 " + password.toString() + " Pass2 " + passFromRegister, Toast.LENGTH_SHORT).show();
        if(passFromRegister.equals(password.toString())) {
            Intent manList = new Intent(getApplicationContext(), ManagerList.class);
            startActivity(manList);
        }
        else{
            Toast.makeText(MainActivity.this, "Wrong password! Try again", Toast.LENGTH_SHORT).show();
        }
    }

    public void howTo(View v) {
        Intent howTo = new Intent(getApplicationContext(), HowToActivity.class);
        startActivity(howTo);
    }
}
