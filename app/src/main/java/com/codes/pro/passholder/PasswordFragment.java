package com.codes.pro.passholder;

import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import net.sqlcipher.database.SQLiteDatabase;

import java.io.File;
import java.util.ArrayList;


public class PasswordFragment extends ListFragment {

    private String categoryText;
    private ListView listView;
    private Button addButt;
    private int actualPosition = -1;
    private static String name;

    private ArrayList<String> password = new ArrayList<String>();
    private String[] option = {"Add password", "Modify clicked password", "Remove clicked password"};

    public static PasswordFragment newInstance(int index, String fieldName) {
        PasswordFragment f = new PasswordFragment();

        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putInt("index", index);
        f.setArguments(args);
        name = fieldName;

        return f;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View detailsFrame = getActivity().findViewById(R.id.passwordFrag);
        //Toast.makeText(getActivity(), name,Toast.LENGTH_LONG).show();

        try {
            SQLiteDatabase.loadLibs(getActivity());
            File databaseFile = getActivity().getDatabasePath(MainActivity.DATABASE_NAME);
            MainActivity.myDB = SQLiteDatabase.openOrCreateDatabase(databaseFile, "test123", null);
            ArrayList<String> values = getPasswordsFromDatabase(MainActivity.myDB, name);
            for(int i = 0; i < values.size(); i++) {
                password.add(values.get(i));
                //Toast.makeText(getActivity(), "CHUJ CHUJ CHUJ", Toast.LENGTH_SHORT).show();
            }

            displayDatabase(MainActivity.myDB, name);
        } catch(Exception e) {
            Log.e("Error", "Error with creating database", e);
        } finally {
            if (MainActivity.myDB != null)
                MainActivity.myDB.close();
        }

        if(password.size() == 0) {
            newPassword();
        }

        listView = this.getListView();

        setListAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_expandable_list_item_1, password));

        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
                actualPosition = position;
                optionDialog(position);
            }
        };
        listView.setOnItemClickListener(itemClickListener);
    }

    public int getShownIndex() {
        return getArguments().getInt("index", 0);
    }

    public void optionDialog(final int position) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.select_dialog_item, option);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select action:");
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                if(which == 0) {
                    newPassword();
                    dialog.cancel();
                }
                if(which == 1) {
                    modifyPassword(position);
                    dialog.cancel();
                }
                if(which == 2) {
                    removePassword(position);
                    dialog.cancel();
                }
            }
        });
        final AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void newPassword() {
        final EditText input = new EditText(getActivity());
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        alertDialogBuilder.setTitle("New password");

        alertDialogBuilder
                .setMessage("Write password and click 'Yes' to add it")
                .setView(input)
                .setCancelable(false)
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        boolean check = false;
                        for (int i = 0; i < password.size(); ++i) {
                            if (password.get(i).equals(String.valueOf(input.getText()))) {
                                check = true;
                                break;
                            }
                        }

                        if (check) {
                            Toast.makeText(getActivity(), "Password: " + String.valueOf(input.getText()) + " exists!", Toast.LENGTH_SHORT).show();
                        } else {
                            try {
                                SQLiteDatabase.loadLibs(getActivity());
                                File databaseFile = getActivity().getDatabasePath(MainActivity.DATABASE_NAME);
                                MainActivity.myDB = SQLiteDatabase.openOrCreateDatabase(databaseFile, "test123", null);
                                //MainActivity.myDB = getActivity().openOrCreateDatabase(MainActivity.DATABASE_NAME, MODE_PRIVATE, null);

                                ContentValues values = new ContentValues();
                                values.put("password", String.valueOf(input.getText()));
                                MainActivity.myDB.insert(name, null, values);

                                //displayDatabase(MainActivity.myDB, name);
                            } catch (Exception e) {
                                Toast.makeText(getActivity(), "ERROR with adding to databse", Toast.LENGTH_SHORT).show();
                                Log.e("Error", "Error with creating database", e);
                            } finally {
                                if (MainActivity.myDB != null)
                                    MainActivity.myDB.close();
                            }
                            password.add(String.valueOf(input.getText()));
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, password);
                            listView.setAdapter(adapter);
                            Toast.makeText(getActivity(), "You added new password!", Toast.LENGTH_SHORT).show();
                        }
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void modifyPassword(final int position) {
        final EditText input = new EditText(getActivity());
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        input.setText(password.get(position));
        alertDialogBuilder.setTitle("Modify password");

        alertDialogBuilder
                .setMessage("Modify password and click 'Yes' to add it")
                .setView(input)
                .setCancelable(false)
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        /*


                            EDYCJA HASŁA W BAZIE


                         */
                        boolean check = false;
                        for (int i = 0; i < password.size(); ++i) {
                            if(password.get(i).equals(String.valueOf(input.getText()))) {
                                check = true;
                                break;
                            }
                        }

                        if(check) {
                            Toast.makeText(getActivity(), "Password: " + String.valueOf(input.getText()) + " exists!", Toast.LENGTH_SHORT).show();
                        } else {
                            password.set(position, String.valueOf(input.getText()));
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, password);
                            listView.setAdapter(adapter);
                            Toast.makeText(getActivity(), "You modified password!", Toast.LENGTH_SHORT).show();
                        }

                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void removePassword(final int position) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle("Remove password");
        final String pass = password.get(position);
        alertDialogBuilder
                .setMessage("Do You really want to remove " + password.get(position) + " ?")
                .setCancelable(false)
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(getActivity(), "Password to remove " + pass, Toast.LENGTH_SHORT).show();
                        try {
                            SQLiteDatabase.loadLibs(getActivity());
                            File databaseFile = getActivity().getDatabasePath(MainActivity.DATABASE_NAME);
                            MainActivity.myDB = SQLiteDatabase.openOrCreateDatabase(databaseFile, "test123", null);
                            //MainActivity.myDB = getActivity().openOrCreateDatabase(MainActivity.DATABASE_NAME, MODE_PRIVATE, null);
                            String deleteQuery = "DELETE FROM " +  name + " Where password='"+ pass +"'";

                            MainActivity.myDB.execSQL(deleteQuery);
                            //displayDatabase(MainActivity.myDB, categoryText);

                        }catch(Exception e) {
                            Toast.makeText(getActivity(), "ERROR with delete from database", Toast.LENGTH_SHORT).show();
                            Log.e("Error", "Error with creating database", e);
                        } finally {
                            if (MainActivity.myDB != null)
                                MainActivity.myDB.close();
                        }
                        password.remove(position);
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, password);
                        listView.setAdapter(adapter);

                        Toast.makeText(getActivity(), "You removed password!", Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private ArrayList<String> getPasswordsFromDatabase(SQLiteDatabase db, String tableName){
        ArrayList<String> values = new ArrayList<String>();
        Cursor cursor = db.rawQuery("SELECT password FROM " + tableName, null);
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

    private void displayDatabase(SQLiteDatabase db, String tableName)
    {
        ArrayList<String> values = new ArrayList<String>();
        Cursor  cursor = db.rawQuery("SELECT * FROM " + tableName, null);
        Toast.makeText(getActivity().getApplicationContext(), "number of Rows " + cursor.getCount(), Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getActivity().getApplicationContext(), "values " + values.get(i), Toast.LENGTH_SHORT).show();
        }
    }
}
