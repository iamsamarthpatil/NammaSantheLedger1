package com.example.nammasantheledger

import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import android.widget.SimpleAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class CustomerDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_customer_details
        )

        val db = DatabaseHelper(this)

        val customerNameText =
            findViewById<TextView>(R.id.customerName)

        val customerPhoneText =
            findViewById<TextView>(R.id.customerPhone)

        val customerBalanceText =
            findViewById<TextView>(R.id.customerBalance)

        val creditTotalText =
            findViewById<TextView>(R.id.creditTotalText)

        val debitTotalText =
            findViewById<TextView>(R.id.debitTotalText)

        val transactionList =
            findViewById<ListView>(R.id.transactionList)

        val addTransactionBtn =
            findViewById<Button>(R.id.addTransactionBtn)

        val shareBtn =
            findViewById<Button>(R.id.shareBtn)

        val customerName =
            intent.getStringExtra(
                "customerName"
            ) ?: ""

        val customerPhone =
            intent.getStringExtra(
                "customerPhone"
            ) ?: ""

        customerNameText.text =
            customerName

        customerPhoneText.text =
            customerPhone

        val balance =
            db.getCustomerBalance(
                customerName
            )

        customerBalanceText.text =
            "₹$balance"

        val cursor: Cursor =
            db.getTransactions(
                customerPhone
            )

        val transactionData =
            ArrayList<HashMap<String, String>>()

        var totalCredit = 0
        var totalDebit = 0

        var fullSummary =
            "📒 Namma Santhe Ledger\n\n"

        fullSummary +=
            "Customer: $customerName\n"

        fullSummary +=
            "Phone: $customerPhone\n\n"

        while(cursor.moveToNext()) {

            val amount =
                cursor.getInt(3)

            val type =
                cursor.getString(4)

            val note =
                cursor.getString(5)

            val map =
                HashMap<String, String>()

            if(type == "CREDIT") {

                totalCredit += amount

                map["type"] =
                    "UDHAAR"

                map["status"] =
                    "🟢 TO COLLECT"

                map["amount"] =
                    "₹$amount"

            } else {

                totalDebit += amount

                map["type"] =
                    "PAYMENT"

                map["status"] =
                    "🔴 PAID"

                map["amount"] =
                    "₹$amount"
            }

            map["item"] =
                note

            map["note"] =
                "Transaction Added"

            map["date"] =
                "Today"

            transactionData.add(map)

            fullSummary +=
                "${map["type"]} - ₹$amount\n"

            fullSummary +=
                "$note\n\n"
        }

        fullSummary +=
            "-------------------\n"

        fullSummary +=
            "Total Credit: ₹$totalCredit\n"

        fullSummary +=
            "Total Debit: ₹$totalDebit\n"

        fullSummary +=
            "Pending Balance: ₹$balance"

        creditTotalText.text =
            "🟢 Credit: ₹$totalCredit"

        debitTotalText.text =
            "🔴 Debit: ₹$totalDebit"

        val adapter =
            SimpleAdapter(
                this,
                transactionData,
                R.layout.transaction_item,
                arrayOf(
                    "type",
                    "date",
                    "item",
                    "note",
                    "status",
                    "amount"
                ),
                intArrayOf(
                    R.id.typeText,
                    R.id.dateText,
                    R.id.itemText,
                    R.id.noteText,
                    R.id.statusText,
                    R.id.amountText
                )
            )

        transactionList.adapter =
            adapter

        addTransactionBtn.setOnClickListener {

            val intent = Intent(
                this,
                AddTransactionActivity::class.java
            )

            intent.putExtra(
                "customerName",
                customerName
            )

            intent.putExtra(
                "customerPhone",
                customerPhone
            )

            startActivity(intent)
        }

        shareBtn.setOnClickListener {

            val shareIntent =
                Intent(Intent.ACTION_SEND)

            shareIntent.type =
                "text/plain"

            shareIntent.putExtra(
                Intent.EXTRA_TEXT,
                fullSummary
            )

            startActivity(
                Intent.createChooser(
                    shareIntent,
                    "Share Ledger"
                )
            )
        }

        if(transactionData.isEmpty()) {

            Toast.makeText(
                this,
                "No transactions found",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}