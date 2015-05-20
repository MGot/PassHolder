package com.codes.pro.passholder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
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

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    public static final String DATABASE_NAME = "PASSWORDS_DB";
    public static SQLiteDatabase myDB;

    private EditText pass;
    private Button login;

    public static String emailFromRegister = "non";
    public static String passFromRegister = "non";
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

        if (settings.getBoolean("firstRun", true) || userEmail.equals("") || userPass.equals(null)) {               //the app is being launched for first time- register time
            openFirstRun(); //go to register menu

            // record the fact that the app has been started at least once
            settings.edit().putBoolean("firstRun", false).commit();
        }

        //Toast.makeText(MainActivity.this, "Asynchr?", Toast.LENGTH_SHORT).show();


        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * method to get email and pass from registered user
     */
    private void getUserInfos(){
        SharedPreferences infos = getSharedPreferences("userInfos", 0);
        emailFromRegister = infos.getString("userEmail", "non");
        passFromRegister = infos.getString("userPass", "non");
        Toast.makeText(MainActivity.this, "Email given  " + emailFromRegister, Toast.LENGTH_SHORT).show();
        Toast.makeText(MainActivity.this, "Pass given " + passFromRegister, Toast.LENGTH_SHORT).show();
    }

    /**
     * method to open registation frame -> FirstRunActivity
     */
    private void openFirstRun(){
        Intent intent = new Intent(getApplicationContext(), FirstRunActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivityForResult(intent, 1);
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


    /**
     * method called after clicked on log in button, generally it checks if pass match
     * @param v
     */
    public void logIn(View v) {

        final HashCode password = Hashing.sha1().hashString(pass.getText().toString(), Charset.defaultCharset());
        //Toast.makeText(MainActivity.this, "Pass1 " + password.toString() + " Pass2u " + userPass, Toast.LENGTH_SHORT).show();
        if(userPass.toString().equals(password.toString())) {
            Intent manList = new Intent(getApplicationContext(), ManagerList.class);
            startActivity(manList);
        }
        else{
            Toast.makeText(MainActivity.this, "Wrong password! Try again", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * method to inform user how to use PassHolder
     * @param v
     */
    public void howTo(View v) {
        Intent howTo = new Intent(getApplicationContext(), HowToActivity.class);
        startActivity(howTo);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Toast.makeText(MainActivity.this, "Hope you'll back here! Sayonara", Toast.LENGTH_SHORT).show();
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == RESULT_OK){
                emailFromRegister = data.getStringExtra("email");
                passFromRegister = data.getStringExtra("password");

                Toast.makeText(MainActivity.this, "Email from register " + emailFromRegister, Toast.LENGTH_SHORT).show();
                Toast.makeText(MainActivity.this, "Pass from register " + passFromRegister, Toast.LENGTH_SHORT).show();

            }
            if (resultCode == RESULT_CANCELED) {
                //finish();
            }
        }
    }//onActivityResult

}
