package com.codes.pro.passholder;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import net.sqlcipher.database.SQLiteDatabase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class PasswordActivity extends ActionBarActivity {

    private String categoryText;
    private ListView listView;
    private List<String> password = new ArrayList<String>();
    private int actualPosition = -1;
    final Context context = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_list);
        Bundle extras = getIntent().getExtras();
        if(extras !=null) {
            categoryText = extras.getString("categoryText");
        }
        Toast.makeText(PasswordActivity.this, "categoryText  " + categoryText, Toast.LENGTH_SHORT).show();


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

        try {
            SQLiteDatabase.loadLibs(this.getApplicationContext());
            File databaseFile = getApplicationContext().getDatabasePath(MainActivity.DATABASE_NAME);
            MainActivity.myDB = SQLiteDatabase.openOrCreateDatabase(databaseFile, MainActivity.userPassString, null);
            ArrayList<String> values = getPasswordsFromDatabase(MainActivity.myDB, categoryText);
            for(int i = 0; i < values.size(); i++)
            {
                password.add(values.get(i));
                //adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice,password);
                //listView.setAdapter(adapter);
            }

        } catch(Exception e) {
            Log.e("Error", "Error with creating database", e);
        } finally {
            if (MainActivity.myDB != null)
                MainActivity.myDB.close();
        }

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


    /**
     * method to add new password to database
     * @param pass
     */
    public void newPass(String pass) {
        boolean check = false;
        for (int i = 0; i < password.size(); ++i) {
            if (password.get(i).equals(pass)) {
                check = true;
                break;
            }
        }

        if(check) {
            Toast.makeText(getApplicationContext(), "Password: " + pass + " exists!", Toast.LENGTH_SHORT).show();
        } else {
            try {
                SQLiteDatabase.loadLibs(this.getApplicationContext());
                File databaseFile = getApplicationContext().getDatabasePath(MainActivity.DATABASE_NAME);
                MainActivity.myDB = SQLiteDatabase.openOrCreateDatabase(databaseFile, MainActivity.userPassString, null);

                ContentValues values = new ContentValues();
                values.put("password", pass);
                MainActivity.myDB.insert(categoryText, null, values);

                //displayDatabase(MainActivity.myDB, categoryText);
            }catch(Exception e) {
                Toast.makeText(getApplicationContext(), "ERROR with adding to databse", Toast.LENGTH_SHORT).show();
                Log.e("Error", "Error with creating database", e);
            } finally {
                if (MainActivity.myDB != null)
                    MainActivity.myDB.close();
            }
            password.add(pass);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice,password);
            listView.setAdapter(adapter);
            Toast.makeText(getApplicationContext(), "You added new password!", Toast.LENGTH_SHORT).show();
        }
    }

    public void modifyDialog(View v) {
        if(actualPosition != -1) {
            final EditText input = new EditText(this);
            final String oldPass = password.get(actualPosition);
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
                            modifyPass(oldPass, input.getText().toString());
                            dialog.cancel();
                        }
                    });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        } else {
            Toast.makeText(getApplicationContext(), "Select position to modify!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * method to modify password in database
     * @param oldPass
     * @param newPass
     */
    public void modifyPass(String oldPass, String newPass) {

        //Toast.makeText(PasswordActivity.this, "oldPass  " + oldPass + " newPass " + newPass, Toast.LENGTH_SHORT).show();

        boolean check = false;
        for (int i = 0; i < password.size(); ++i) {
            if (password.get(i).equals(newPass)) {
                check = true;
                break;
            }
        }

        if(check) {
            Toast.makeText(getApplicationContext(), "Password: " + newPass + " exists!", Toast.LENGTH_SHORT).show();
        } else {
            try {
                SQLiteDatabase.loadLibs(this.getApplicationContext());
                File databaseFile = getApplicationContext().getDatabasePath(MainActivity.DATABASE_NAME);
                MainActivity.myDB = SQLiteDatabase.openOrCreateDatabase(databaseFile, MainActivity.userPassString, null);
                String updateQuery = "UPDATE " + categoryText + " SET password='" + newPass + "' WHERE password='" + oldPass + "'";
                MainActivity.myDB.execSQL(updateQuery);

                //displayDatabase(MainActivity.myDB, categoryText);
                //Toast.makeText(ManagerList.this, "TableExists  "+ isTableExists(MainActivity.myDB,itemname.get(position) ), Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "ERROR with modify to database", Toast.LENGTH_SHORT).show();
                Log.e("Error", "Error with creating database", e);
            } finally {
                if (MainActivity.myDB != null)
                    MainActivity.myDB.close();
            }
            password.set(actualPosition,newPass);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice,password);
            listView.setAdapter(adapter);
            Toast.makeText(getApplicationContext(), "You modified password!", Toast.LENGTH_SHORT).show();
        }
    }


    public void removeDialog(View v) {
        if(actualPosition != -1) {
            String newPass = "";
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

            // set title
            alertDialogBuilder.setTitle("Do You really want to remove this password?");

            // set dialog message
            alertDialogBuilder
                    .setMessage("Click yes to remove \"" + password.get(actualPosition) + "\"")
                    .setCancelable(false)
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    })
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            removePass(password.get(actualPosition));
                            dialog.cancel();
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        } else {
            Toast.makeText(getApplicationContext(), "Select position to remove!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * method to delete password from database
     * @param pass
     */
    public void removePass(String pass) {
        if(actualPosition != -1) {

            Toast.makeText(PasswordActivity.this, "Password to remove  " + pass, Toast.LENGTH_SHORT).show();
            try {
                SQLiteDatabase.loadLibs(this.getApplicationContext());
                File databaseFile = getApplicationContext().getDatabasePath(MainActivity.DATABASE_NAME);
                MainActivity.myDB = SQLiteDatabase.openOrCreateDatabase(databaseFile, MainActivity.userPassString, null);
                String deleteQuery = "DELETE FROM " +  categoryText + " Where password='"+ pass +"'";

                MainActivity.myDB.execSQL(deleteQuery);
                //displayDatabase(MainActivity.myDB, categoryText);

            }catch(Exception e) {
                Toast.makeText(getApplicationContext(), "ERROR with delete from database", Toast.LENGTH_SHORT).show();
                Log.e("Error", "Error with creating database", e);
            } finally {
                if (MainActivity.myDB != null)
                    MainActivity.myDB.close();
            }

            password.remove(actualPosition);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, password);
            listView.setAdapter(adapter);
            actualPosition = -1;
        } else {
            Toast.makeText(getApplicationContext(), "Select position to remove!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * method to display all rows from database
     * @param db
     * @param tableName
     */
    private void displayDatabase(SQLiteDatabase db, String tableName)
    {
        ArrayList<String> values = new ArrayList<String>();
        Cursor  cursor = db.rawQuery("SELECT * FROM " + tableName, null);
        Toast.makeText(getApplicationContext(), "number of Rows " + cursor.getCount(), Toast.LENGTH_SHORT).show();
        if (cursor != null) {
            if (cursor .moveToFirst()) {

                while (cursor.isAfterLast() == false) {
                    String name = cursor.getString(cursor
                            .getColumnIndex("password"));

                    values.add(name);
                    cursor.moveToNext();
                }
            }
            cursor.close();
        }

        for(int i = 0; i < values.size(); i++){
            Toast.makeText(getApplicationContext(), "values " + values.get(i), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * method to get all passwords from database
     * @param db
     * @param tableName
     * @return all passwords in ArrayList<String>
     */
    private ArrayList<String> getPasswordsFromDatabase(SQLiteDatabase db, String tableName){
        ArrayList<String> values = new ArrayList<String>();
        Cursor  cursor = db.rawQuery("SELECT password FROM " + tableName, null);
        //Toast.makeText(getApplicationContext(), "number of Rows " + cursor.getCount(), Toast.LENGTH_SHORT).show();
        if (cursor != null) {
            if (cursor .moveToFirst()) {

                while (cursor.isAfterLast() == false) {
                    String name = cursor.getString(cursor
                            .getColumnIndex("password"));

                    values.add(name);
                    cursor.moveToNext();
                }
            }
            cursor.close();
        }

        return values;
    }
}
