package com.codes.pro.passholder;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;


//categoryActivity
public class ManagerList extends ActionBarActivity {

    private ListView list;
    private ManagerListAdapter adapter;
    private ArrayList<String> itemname = new ArrayList<String>();
    private TextView category;
    final Context context = this;
    private String listName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_manager_list);
        Toast.makeText(getApplicationContext(), "Long click on category to remove it", Toast.LENGTH_SHORT).show();
        adapter = new ManagerListAdapter(this,itemname);

        list=(ListView)findViewById(R.id.managerList);
        list.setAdapter(adapter);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,itemname);
        arrayAdapter.clear();



        list.setSelection(1);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listName = itemname.get(position);
                Toast.makeText(getApplicationContext(), "Name " + listName, Toast.LENGTH_SHORT).show();
                Intent pass = new Intent(getApplicationContext(),PasswordList.class);
                pass.putExtra("categoryText", listName);
                startActivity(pass);
            }
        });

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                removeCatDialog(position);
                return true;
            }
        });

        File database=getApplicationContext().getDatabasePath(MainActivity.
                DATABASE_NAME);

        if (!database.exists()) {
            Toast.makeText(getApplicationContext(), "database is not created yet", Toast.LENGTH_SHORT).show();
        } else {
            try {
                MainActivity.myDB = this.openOrCreateDatabase(MainActivity.DATABASE_NAME, MODE_PRIVATE, null);
                Cursor c = MainActivity.myDB.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name !='android_metadata' AND name != 'sqlite_sequence'", null);

                if (c.moveToFirst()) {
                    while (!c.isAfterLast()) {
                        //Toast.makeText(ManagerList.this, "Table Name=> " + c.getString(0), Toast.LENGTH_LONG).show();
                        itemname.add(c.getString(0));
                        list.setAdapter(arrayAdapter);
                        c.moveToNext();
                    }
                }
            } catch(Exception e) {
                Log.e("Error", "Error with creating database", e);
            } finally {
                if (MainActivity.myDB != null)
                    MainActivity.myDB.close();
            }
            Toast.makeText(getApplicationContext(), "database is already created", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_manager_list, menu);
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

    public String getListName(){
        return listName;
    }

    public void newCategory(View v) {
        category = (EditText) findViewById(R.id.newSourceField);
        String text = category.getText().toString();
        itemname.add(text);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,itemname);
        list.setAdapter(arrayAdapter);
        createTable(text);
    }

    /**
     * method to create table in database when users added category
     * @param categoryText - category name
     */
    private void createTable(String categoryText){

            try {
                MainActivity.myDB = this.openOrCreateDatabase(MainActivity.DATABASE_NAME, MODE_PRIVATE, null);
                  /* Create a Table in the Database. */
                MainActivity.myDB.execSQL("CREATE TABLE IF NOT EXISTS "
                        + categoryText
                        + " (id INTEGER PRIMARY KEY AUTOINCREMENT, password TEXT);");

                Toast.makeText(ManagerList.this, "TableExists  " + isTableExists(MainActivity.myDB,categoryText ), Toast.LENGTH_SHORT).show();
            }catch(Exception e) {
                Log.e("Error", "Error with creating database", e);
            } finally {
                if (MainActivity.myDB != null)
                    MainActivity.myDB.close();
            }



    }

    public void removeCatDialog(final int position) {
        String newPass = "";
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        alertDialogBuilder.setTitle("Do You really want to remove this category?");

        alertDialogBuilder
                .setMessage("Click yes to remove \"" + itemname.get(position) + "\"")
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
                        removeCat(position);
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }



    public void removeCat(int position) {

        Toast.makeText(ManagerList.this, "CategoryName  " + itemname.get(position), Toast.LENGTH_SHORT).show();
        try {
            MainActivity.myDB = this.openOrCreateDatabase(MainActivity.DATABASE_NAME, MODE_PRIVATE, null);
                  /* Create a Table in the Database. */
            MainActivity.myDB.execSQL("DROP TABLE IF EXISTS " + itemname.get(position) + ";");

            Toast.makeText(ManagerList.this, "TableExists  "+ isTableExists(MainActivity.myDB,itemname.get(position) ), Toast.LENGTH_SHORT).show();
        }catch(Exception e) {
            Log.e("Error", "Error with creating database", e);
        } finally {
            if (MainActivity.myDB != null)
                MainActivity.myDB.close();
        }



        itemname.remove(position);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, itemname);
        list.setAdapter(adapter);
    }

    /**
     * method to check if table in database exists
     * @param db
     * @param tableName
     * @return true if exists, false otherwise
     */
    boolean isTableExists(SQLiteDatabase db, String tableName)
    {
        if (tableName == null || db == null || !db.isOpen())
        {
            return false;
        }
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM sqlite_master WHERE type = ? AND name = ?", new String[] {"table", tableName});
        if (!cursor.moveToFirst())
        {
            return false;
        }
        int count = cursor.getInt(0);
        cursor.close();
        return count > 0;
    }
}
