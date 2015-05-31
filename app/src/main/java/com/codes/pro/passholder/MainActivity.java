package com.codes.pro.passholder;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;

import net.sqlcipher.database.SQLiteDatabase;

import java.io.File;
import java.nio.charset.Charset;


public class MainActivity extends ActionBarActivity {

    public static final String DATABASE_NAME = "PASSWORDS_DB.db";
    public static SQLiteDatabase myDB;

    private EditText pass;
    private Button login;

    public static String userEmail = "";
    public static HashCode userPass = null;     //pass from FirstRunActivity, generally just use in first open application
    public static String userPassString = "";   //pass from SharedPreferenced

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pass = (EditText) findViewById(R.id.editText);
        login = (Button) findViewById(R.id.loginButt);


    }

    /**
     * method to initialize encrypted database
     */
    private void InitializeSQLCipher() {
        SQLiteDatabase.loadLibs(this);
        File databaseFile = getDatabasePath(DATABASE_NAME);

        databaseFile.mkdirs();
        databaseFile.delete();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.app

        SharedPreferences settings = getSharedPreferences("MyPrefsFile", 0);
        SharedPreferences infos = getSharedPreferences("userInfos", 0);
        userEmail = infos.getString("userEmail", "");
        userPassString = infos.getString("userPass", "");

        if (settings.getBoolean("firstRun", true) || userEmail.equals("") || userPassString.equals("")) {               //the app is being launched for first time- register time
            openFirstRun(); //go to register menu
            if(userEmail.equals(""))
                Toast.makeText(MainActivity.this, "Email empty  ", Toast.LENGTH_SHORT).show();

            InitializeSQLCipher();

            settings.edit().putBoolean("firstRun", false).commit();
        }

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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
        Toast.makeText(MainActivity.this, "Exit", Toast.LENGTH_SHORT).show();

        if(getIntent().getBooleanExtra("closeApp", false))
        {
            Toast.makeText(MainActivity.this, "Encrypting", Toast.LENGTH_SHORT).show();
            android.os.Process.killProcess(android.os.Process.myPid());

            finish();
        }
    }

    /**
     * method called after clicked on log in button, generally it checks if pass match
     * @param v
     */
    public void logIn(View v) {

        final HashCode password = Hashing.sha1().hashString(pass.getText().toString(), Charset.defaultCharset());
        //Toast.makeText(MainActivity.this, "Pass1 " + password.toString() + " Pass2u " + userPassString, Toast.LENGTH_SHORT).show();
        if(userPassString.equals(password.toString()) || userPass.toString().equals(password.toString())) {
            Intent manList = new Intent(getApplicationContext(), ManagerActivity.class);
            startActivity(manList);
        }
        else{
            Toast.makeText(getApplicationContext(), "Wrong password! Try again", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * method opening ForgetPasswordActivity
     * @param view
     */
    public void forgetPassword(View view)
    {
        Intent forgetPassword = new Intent(getApplicationContext(), ForgetPasswordActivity.class);
        startActivity(forgetPassword);
    }

    /**
     * method to inform user how to use PassHolder
     * @param v
     */
    public void howTo(View v) {
        Intent howTo = new Intent(getApplicationContext(), HowToActivity.class);
        startActivity(howTo);
    }

    public static Context getContext() {
        try {
            return (Context) Class.forName("android.app.ActivityThread")
                    .getMethod("currentApplication").invoke(null, (Object[]) null);
        } catch (final Exception e1) {
            try {
                return (Context) Class.forName("android.app.AppGlobals")
                        .getMethod("getInitialApplication").invoke(null, (Object[]) null);
            } catch (final Exception e2) {
                throw new RuntimeException("Failed to get application instance");
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Toast.makeText(MainActivity.this, "Hope you'll back here! Sayonara", Toast.LENGTH_SHORT).show();
            Toast.makeText(MainActivity.this, "Encrypting", Toast.LENGTH_SHORT).show();
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * method to get result from FirstRunActivity
     * @param requestCode
     * @param resultCode
     * @param data
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == RESULT_OK || resultCode == RESULT_CANCELED){
                if(getIntent().getBooleanExtra("closeApp", false))
                {
                    finish();
                }
            }
        }
    }

}
