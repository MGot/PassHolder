package com.codes.pro.passholder;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class PasswordList extends ActionBarActivity {

    private ListView listView;
    private List<String> password = new ArrayList<String>();
    private int actualPosition = -1;
    final Context context = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_list);

        listView = (ListView) findViewById(R.id.listview);

        // Instantiating array adapter to populate the listView
        // The layout android.R.layout.simple_list_item_single_choice creates radio button for each listview item
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice,password);

        listView.setAdapter(adapter);


        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
                actualPosition = position;
                //Toast.makeText(getApplicationContext(), "You selected item number: " + actualPosition, Toast.LENGTH_SHORT).show();
            }
        };

        listView.setOnItemClickListener(itemClickListener);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_password_list, menu);
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


    public void addDialog(View v) {
        final EditText input = new EditText(this);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        alertDialogBuilder.setTitle("New password");

        alertDialogBuilder
                .setMessage("Write new password and click 'Yes' to add it")
                .setView(input)
                .setCancelable(false)
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        newPass(input.getText().toString());
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    public void newPass(String pass) {

            Toast.makeText(getApplicationContext(), "Position", Toast.LENGTH_SHORT).show();
            password.add(pass);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice,password);
        listView.setAdapter(adapter);
        Toast.makeText(getApplicationContext(), "You added new password!", Toast.LENGTH_SHORT).show();
    }

    public void modifyDialog(View v) {
        if(actualPosition != -1) {
            final EditText input = new EditText(this);
            input.setText(password.get(actualPosition));
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

            alertDialogBuilder.setTitle("Modify password");

            alertDialogBuilder
                    .setMessage("Edit Your password and click 'Yes' to confirm")
                    .setView(input)
                    .setCancelable(false)
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    })
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            modifyPass(input.getText().toString());
                            dialog.cancel();
                        }
                    });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        } else {
            Toast.makeText(getApplicationContext(), "Select position to modify!", Toast.LENGTH_SHORT).show();
        }
    }

    public void modifyPass(String pass) {

/*
                try {
   myDB = this.openOrCreateDatabase("DatabaseName", MODE_PRIVATE, null);

   /* Create a Table in the Database.
        myDB.execSQL("CREATE TABLE IF NOT EXISTS "
                + TableName
                + " (Field1 VARCHAR, Field2 INT(3));");
 Insert data to a Table*/

        //MODYFIKACJA HASŁĄ W BAZIE DANYCH



        password.set(actualPosition,pass);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice,password);
        listView.setAdapter(adapter);
        Toast.makeText(getApplicationContext(), "You modified password!", Toast.LENGTH_SHORT).show();
    }


    public void removeDialog(View v) {
        String newPass = "";
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        // set title
        alertDialogBuilder.setTitle("Do You really want to remove this password?");

        // set dialog message
        alertDialogBuilder
                .setMessage("Click yes to remove \"" + password.get(actualPosition) + "\"")
                .setCancelable(false)
                .setNegativeButton("No",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                })
                .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, close
                        // current activity
                        removePass();
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    public void removePass() {
        if(actualPosition != -1) {

            /*
                    USUWANE HASŁA Z BAZY DANYCH

             */

            password.remove(actualPosition);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, password);
            listView.setAdapter(adapter);
            actualPosition = -1;
        } else {
            Toast.makeText(getApplicationContext(), "Select position to remove!", Toast.LENGTH_SHORT).show();
        }
    }
}
