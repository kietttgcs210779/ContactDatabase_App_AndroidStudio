package com.example.contactdatabaseappjava;

import android.provider.BaseColumns;

public final class DatabaseContract {
    // Để ngăn ai đó vô tình khởi tạo lớp contract,
    // hãy tạo một constructor private.
    private DatabaseContract() {}

    public static class ContactEntry implements BaseColumns {
        public static final String TABLE_NAME = "contacts";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_PHONE = "phone";
        public static final String COLUMN_NAME_EMAIL = "email";
    }
}
