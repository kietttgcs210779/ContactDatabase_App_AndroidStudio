package com.example.contactdatabaseappjava;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private RecyclerView recyclerView;
    private ContactAdapter adapter;
    private List<Contact> contactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        contactList = getAllContacts();
        adapter = new ContactAdapter(contactList);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new ContactAdapter.OnItemClickListener() {
            @Override
            public void onEditClick(int position) {
                showEditContactDialog(position);
            }

            @Override
            public void onDeleteClick(int position) {
                deleteContact(position);
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddContactDialog();
            }
        });
    }

    private List<Contact> getAllContacts() {
        List<Contact> contacts = new ArrayList<>();
        Cursor cursor = db.query(DatabaseContract.ContactEntry.TABLE_NAME, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseContract.ContactEntry._ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.ContactEntry.COLUMN_NAME_NAME));
            String phone = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.ContactEntry.COLUMN_NAME_PHONE));
            String email = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.ContactEntry.COLUMN_NAME_EMAIL));
            contacts.add(new Contact(id, name, phone, email));
        }
        cursor.close();
        return contacts;
    }

    private void showAddContactDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_edit_contact, null);
        builder.setView(dialogView);

        final EditText nameEditText = dialogView.findViewById(R.id.edit_text_name);
        final EditText phoneEditText = dialogView.findViewById(R.id.edit_text_phone);
        final EditText emailEditText = dialogView.findViewById(R.id.edit_text_email);

        builder.setTitle("Add Contact");
        builder.setPositiveButton("Add", (dialog, which) -> {
            String name = nameEditText.getText().toString();
            String phone = phoneEditText.getText().toString();
            String email = emailEditText.getText().toString();
            addContact(name, phone, email);
        });
        builder.setNegativeButton("Cancel", null);
        builder.create().show();
    }

    private void addContact(String name, String phone, String email) {
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.ContactEntry.COLUMN_NAME_NAME, name);
        values.put(DatabaseContract.ContactEntry.COLUMN_NAME_PHONE, phone);
        values.put(DatabaseContract.ContactEntry.COLUMN_NAME_EMAIL, email);
        long id = db.insert(DatabaseContract.ContactEntry.TABLE_NAME, null, values);

        contactList.add(new Contact(id, name, phone, email));
        adapter.notifyItemInserted(contactList.size() - 1);
    }

    private void showEditContactDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_edit_contact, null);
        builder.setView(dialogView);

        final Contact contact = contactList.get(position);
        final EditText nameEditText = dialogView.findViewById(R.id.edit_text_name);
        final EditText phoneEditText = dialogView.findViewById(R.id.edit_text_phone);
        final EditText emailEditText = dialogView.findViewById(R.id.edit_text_email);

        nameEditText.setText(contact.getName());
        phoneEditText.setText(contact.getPhone());
        emailEditText.setText(contact.getEmail());

        builder.setTitle("Edit Contact");
        builder.setPositiveButton("Save", (dialog, which) -> {
            String name = nameEditText.getText().toString();
            String phone = phoneEditText.getText().toString();
            String email = emailEditText.getText().toString();
            updateContact(position, name, phone, email);
        });
        builder.setNegativeButton("Cancel", null);
        builder.create().show();
    }

    private void updateContact(int position, String name, String phone, String email) {
        Contact contact = contactList.get(position);
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.ContactEntry.COLUMN_NAME_NAME, name);
        values.put(DatabaseContract.ContactEntry.COLUMN_NAME_PHONE, phone);
        values.put(DatabaseContract.ContactEntry.COLUMN_NAME_EMAIL, email);

        db.update(DatabaseContract.ContactEntry.TABLE_NAME, values, DatabaseContract.ContactEntry._ID + "=?", new String[]{String.valueOf(contact.getId())});

        contactList.set(position, new Contact(contact.getId(), name, phone, email));
        adapter.notifyItemChanged(position);
    }

    private void deleteContact(final int position) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Contact")
                .setMessage("Are you sure you want to delete this contact?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    Contact contact = contactList.get(position);
                    db.delete(DatabaseContract.ContactEntry.TABLE_NAME, DatabaseContract.ContactEntry._ID + "=?", new String[]{String.valueOf(contact.getId())});
                    contactList.remove(position);
                    adapter.notifyItemRemoved(position);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}
