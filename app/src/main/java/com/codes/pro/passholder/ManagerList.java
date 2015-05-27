package com.codes.pro.passholder;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;



//categoryActivity
public class ManagerList extends ListFragment {

    private boolean mDualPane;
    private int mCurCheckPosition = 0;
    private int actualPosition = -1;
    private String categoryText;
    private ArrayList<String> itemname = new ArrayList<String>();
    private String listName;
    private ListView listView;
    private String[] option = {"Add category", "Modify clicked category", "Remove clicked category"};

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View detailsFrame = getActivity().findViewById(R.id.passwordFrag);
        Toast.makeText(getActivity(), "Long click on category to show options", Toast.LENGTH_SHORT).show();


        File database=getActivity().getDatabasePath(MainActivity.
                DATABASE_NAME);

        if (!database.exists()) {
            Toast.makeText(getActivity(), "database is not created yet", Toast.LENGTH_SHORT).show();
        } else {
            try {
                MainActivity.myDB = getActivity().openOrCreateDatabase(MainActivity.DATABASE_NAME, MODE_PRIVATE, null);
                Cursor c = MainActivity.myDB.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name !='android_metadata' AND name != 'sqlite_sequence'", null);

                if (c.moveToFirst()) {
                    while (!c.isAfterLast()) {
                        //Toast.makeText(ManagerList.this, "Table Name=> " + c.getString(0), Toast.LENGTH_LONG).show();
                        itemname.add(c.getString(0));
                        c.moveToNext();
                    }
                }
            } catch(Exception e) {
                Log.e("Error", "Error with creating database", e);
            } finally {
                if (MainActivity.myDB != null)
                    MainActivity.myDB.close();
            }
            Toast.makeText(getActivity(), "database is already created", Toast.LENGTH_SHORT).show();
        }


        if(itemname.size() == 0) {
            newCategory(0);
        }

        listView = this.getListView();
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        setListAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, itemname));


        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                actualPosition = position;
                optionDialog();
                return true;
            }
        });

        mDualPane = detailsFrame != null
                && detailsFrame.getVisibility() == View.VISIBLE;

        //Toast.makeText(getActivity(), "mDualPane " + mDualPane,Toast.LENGTH_LONG).show();

        if (savedInstanceState != null) {
            mCurCheckPosition = savedInstanceState.getInt("curChoice", 0);
        }

        if (mDualPane) {
            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            //Toast.makeText(getActivity(), String.valueOf(mCurCheckPosition), Toast.LENGTH_SHORT).show();
            showDetails(mCurCheckPosition);
        } else {
            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            getListView().setItemChecked(mCurCheckPosition, true);
        }
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        //Toast.makeText(getActivity(), "Position " + position ,Toast.LENGTH_SHORT).show();
        showDetails(position);
    }


    public void optionDialog() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.select_dialog_item, option);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select action:");
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    newCategory();
                    dialog.cancel();
                }
                if (which == 1) {
                    modifyCategory();
                    dialog.cancel();
                }
                if (which == 2) {
                    removeCategory();
                    dialog.cancel();
                }
            }
        });
        final AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void newCategory() {
        final EditText input = new EditText(getActivity());
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        alertDialogBuilder.setTitle("New category");

        alertDialogBuilder
                .setMessage("Write category name and click 'Yes' to add it")
                .setView(input)
                .setCancelable(false)
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (String.valueOf(input.getText()).equals("")) {
                            Toast.makeText(getActivity(), "Set name of Your category!", Toast.LENGTH_SHORT).show();
                            newCategory();
                        } else {

                            boolean check = false;
                            for (int i = 0; i < itemname.size(); ++i) {
                                if (itemname.get(i).equals(String.valueOf(input.getText()))) {
                                    check = true;
                                    break;
                                }
                            }

                            if (check) {
                                Toast.makeText(getActivity(), "Category named: " + String.valueOf(input.getText()) + " exists!", Toast.LENGTH_SHORT).show();
                            } else {
                                createTable(String.valueOf(input.getText()));
                                itemname.add(String.valueOf(input.getText()));
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, itemname);
                                listView.setAdapter(adapter);
                                Toast.makeText(getActivity(), "You added new category!", Toast.LENGTH_SHORT).show();
                            }
                        }
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    public void newCategory(int numOf) {
        final EditText input = new EditText(getActivity());
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        alertDialogBuilder.setTitle("New category");

        alertDialogBuilder
                .setMessage("Write category name and click 'Yes' to add it")
                .setView(input)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (String.valueOf(input.getText()).equals("")) {
                            Toast.makeText(getActivity(), "Set name of Your category!", Toast.LENGTH_SHORT).show();
                            newCategory(0);
                        } else {

                            boolean check = false;
                            for (int i = 0; i < itemname.size(); ++i) {
                                if (itemname.get(i).equals(String.valueOf(input.getText()))) {
                                    check = true;
                                    break;
                                }
                            }

                            if (check) {
                                Toast.makeText(getActivity(), "Category named: " + String.valueOf(input.getText()) + " exists!", Toast.LENGTH_SHORT).show();
                            } else {
                                createTable(String.valueOf(input.getText()));
                                itemname.add(String.valueOf(input.getText()));
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, itemname);
                                listView.setAdapter(adapter);
                                Toast.makeText(getActivity(), "You added new category!", Toast.LENGTH_SHORT).show();
                            }
                        }
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void modifyCategory() {
        final EditText input = new EditText(getActivity());
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        input.setText(itemname.get(actualPosition));
        alertDialogBuilder.setTitle("Modify category");

        alertDialogBuilder
                .setMessage("Edit category name and click 'Yes' to add it")
                .setView(input)
                .setCancelable(false)
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (String.valueOf(input.getText()).equals("")) {
                            Toast.makeText(getActivity(), "Set name of Your category!", Toast.LENGTH_SHORT).show();
                            modifyCategory();
                        } else {
                            try {
                                MainActivity.myDB = getActivity().openOrCreateDatabase(MainActivity.DATABASE_NAME, MODE_PRIVATE, null);
                                String query = "ALTER TABLE " + itemname.get(actualPosition) + " RENAME TO " + String.valueOf(input.getText());
                                MainActivity.myDB.execSQL(query);

                                Toast.makeText(getActivity(), "TableExists  " + isTableExists(MainActivity.myDB, categoryText), Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Log.e("Error", "Error with creating database", e);
                            } finally {
                                if (MainActivity.myDB != null)
                                    MainActivity.myDB.close();
                            }

                            boolean check = false;
                            for (int i = 0; i < itemname.size(); ++i) {
                                if (itemname.get(i).equals(String.valueOf(input.getText()))) {
                                    check = true;
                                    break;
                                }
                            }

                            if (check) {
                                Toast.makeText(getActivity(), "Category named: " + String.valueOf(input.getText()) + " exists!", Toast.LENGTH_SHORT).show();
                            } else {
                                createTable(String.valueOf(input.getText()));
                                itemname.set(actualPosition, String.valueOf(input.getText()));
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, itemname);
                                listView.setAdapter(adapter);
                                Toast.makeText(getActivity(), "You modified the category!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void removeCategory() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle("Remove category");

        alertDialogBuilder
                .setMessage("Do You relly want to remove whole category?")
                .setCancelable(false)
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            MainActivity.myDB = getActivity().openOrCreateDatabase(MainActivity.DATABASE_NAME, MODE_PRIVATE, null);
                            MainActivity.myDB.execSQL("DROP TABLE IF EXISTS " + itemname.get(actualPosition) + ";");

                            Toast.makeText(getActivity(), "TableExists  "+ isTableExists(MainActivity.myDB,itemname.get(actualPosition) ), Toast.LENGTH_SHORT).show();
                        }catch(Exception e) {
                            Log.e("Error", "Error with creating database", e);
                        } finally {
                            if (MainActivity.myDB != null)
                                MainActivity.myDB.close();
                        }
                        itemname.remove(actualPosition);
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,itemname);
                        listView.setAdapter(adapter);
                        Toast.makeText(getActivity(), "You removed the category!", Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    void showDetails(int index) {
        mCurCheckPosition = index;

        if (mDualPane) {
            getListView().setItemChecked(index, true);

            PasswordFragment details = (PasswordFragment) getFragmentManager().findFragmentById(R.id.passwordFrag);
            if (details == null || details.getShownIndex() != index) {

                details = PasswordFragment.newInstance(index, itemname.get(index));
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.passwordFrag, details);
                ft.commit();
            }

        } else {
            listName = itemname.get(index);
            Toast.makeText(getActivity(), "Name " + listName, Toast.LENGTH_SHORT).show();
            Intent pass = new Intent(getActivity().getApplicationContext(),PasswordActivity.class);
            pass.putExtra("categoryText", listName);
            startActivity(pass);
        }
    }

    private void createTable(String categoryText){
        try {
            MainActivity.myDB = getActivity().openOrCreateDatabase(MainActivity.DATABASE_NAME,MODE_PRIVATE, null);
            MainActivity.myDB.execSQL("CREATE TABLE IF NOT EXISTS "
                    + categoryText
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT, password TEXT);");

            Toast.makeText(getActivity(), "TableExists  " + isTableExists(MainActivity.myDB,categoryText ), Toast.LENGTH_SHORT).show();
        }catch(Exception e) {
            Log.e("Error", "Error with creating database", e);
        } finally {
            if (MainActivity.myDB != null)
                MainActivity.myDB.close();
        }
    }

    boolean isTableExists(SQLiteDatabase db, String tableName)
    {
        if (tableName == null || db == null || !db.isOpen()) {
            return false;
        }
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM sqlite_master WHERE type = ? AND name = ?", new String[] {"table", tableName});
        if (!cursor.moveToFirst()) {
            return false;
        }
        int count = cursor.getInt(0);
        cursor.close();
        return count > 0;
    }
}
