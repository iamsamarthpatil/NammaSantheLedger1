package com.example.nammasantheledger

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :

    SQLiteOpenHelper(
        context,
        "LedgerDB",
        null,
        2
    ) {

    override fun onCreate(db: SQLiteDatabase) {

        db.execSQL(
            "CREATE TABLE customers(" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT," +
                    "phone TEXT," +
                    "balance INTEGER)"
        )

        db.execSQL(
            "CREATE TABLE transactions(" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "customerName TEXT," +
                    "phone TEXT," +
                    "amount INTEGER," +
                    "type TEXT," +
                    "note TEXT," +
                    "date TEXT)"
        )
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {

        db.execSQL(
            "DROP TABLE IF EXISTS customers"
        )

        db.execSQL(
            "DROP TABLE IF EXISTS transactions"
        )

        onCreate(db)
    }

    fun addCustomer(
        name: String,
        phone: String
    ): Boolean {

        val db = writableDatabase

        val values = ContentValues()

        values.put("name", name)

        values.put("phone", phone)

        values.put("balance", 0)

        val result =
            db.insert(
                "customers",
                null,
                values
            )

        return result != -1L
    }

    fun addTransaction(
        customerName: String,
        phone: String,
        amount: Int,
        type: String,
        note: String
    ) {

        val db = writableDatabase

        val values = ContentValues()

        values.put(
            "customerName",
            customerName
        )

        values.put(
            "phone",
            phone
        )

        values.put(
            "amount",
            amount
        )

        values.put(
            "type",
            type
        )

        values.put(
            "note",
            note
        )

        values.put(
            "date",
            System.currentTimeMillis().toString()
        )

        db.insert(
            "transactions",
            null,
            values
        )

        if(type == "CREDIT") {

            db.execSQL(
                "UPDATE customers " +
                        "SET balance = balance + $amount " +
                        "WHERE phone='$phone'"
            )

        } else {

            db.execSQL(
                "UPDATE customers " +
                        "SET balance = balance - $amount " +
                        "WHERE phone='$phone'"
            )
        }
    }

    fun getCustomers(): Cursor {

        return readableDatabase.rawQuery(
            "SELECT * FROM customers ORDER BY balance DESC",
            null
        )
    }

    fun getCustomerBalance(
        customerName: String
    ): Int {

        val cursor =
            readableDatabase.rawQuery(
                "SELECT balance FROM customers WHERE name=?",
                arrayOf(customerName)
            )

        var balance = 0

        if(cursor.moveToFirst()) {

            balance =
                cursor.getInt(0)
        }

        cursor.close()

        return balance
    }

    fun getTransactions(
        phone: String
    ): Cursor {

        return readableDatabase.rawQuery(
            "SELECT * FROM transactions " +
                    "WHERE phone=? " +
                    "ORDER BY id DESC",
            arrayOf(phone)
        )
    }
}