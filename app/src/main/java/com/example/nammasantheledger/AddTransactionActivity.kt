package com.example.nammasantheledger

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.telephony.SmsManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

class AddTransactionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_add_transaction
        )

        val db =
            DatabaseHelper(this)

        val amountInput =
            findViewById<EditText>(R.id.amountInput)

        val noteInput =
            findViewById<EditText>(R.id.noteInput)

        val creditBtn =
            findViewById<Button>(R.id.creditBtn)

        val debitBtn =
            findViewById<Button>(R.id.debitBtn)

        val customerName =
            intent.getStringExtra(
                "customerName"
            ) ?: ""

        val customerPhone =
            intent.getStringExtra(
                "customerPhone"
            ) ?: ""

        creditBtn.setOnClickListener {

            val amountText =
                amountInput.text.toString()

            val note =
                noteInput.text.toString()

            if(amountText.isEmpty()) {

                Toast.makeText(
                    this,
                    "Enter amount",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            val amount =
                amountText.toInt()

            db.addTransaction(
                customerName,
                customerPhone,
                amount,
                "CREDIT",
                note
            )

            sendSMS(
                customerPhone,
                customerName,
                amount,
                note,
                "UDHAAR"
            )

            Toast.makeText(
                this,
                "Credit Added",
                Toast.LENGTH_SHORT
            ).show()

            finish()
        }

        debitBtn.setOnClickListener {

            val amountText =
                amountInput.text.toString()

            val note =
                noteInput.text.toString()

            if(amountText.isEmpty()) {

                Toast.makeText(
                    this,
                    "Enter amount",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            val amount =
                amountText.toInt()

            db.addTransaction(
                customerName,
                customerPhone,
                amount,
                "DEBIT",
                note
            )

            sendSMS(
                customerPhone,
                customerName,
                amount,
                note,
                "PAYMENT"
            )

            Toast.makeText(
                this,
                "Debit Added",
                Toast.LENGTH_SHORT
            ).show()

            finish()
        }
    }

    private fun sendSMS(
        phone: String,
        name: String,
        amount: Int,
        note: String,
        type: String
    ) {

        if (
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.SEND_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.SEND_SMS
                ),
                101
            )

            Toast.makeText(
                this,
                "Allow SMS Permission",
                Toast.LENGTH_LONG
            ).show()

            return
        }

        try {

            val smsManager =

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

                    getSystemService(
                        SmsManager::class.java
                    )

                } else {

                    SmsManager.getDefault()
                }

            val message =

                "📒 Namma Santhe Ledger\n\n" +

                        "Hello $name,\n\n" +

                        "$type Added\n" +

                        "Amount: ₹$amount\n" +

                        "Item: $note\n\n" +

                        "Thank You 🙏"

            smsManager.sendTextMessage(
                phone,
                null,
                message,
                null,
                null
            )

            Toast.makeText(
                this,
                "SMS Sent Successfully",
                Toast.LENGTH_LONG
            ).show()

        } catch (e: Exception) {

            Toast.makeText(
                this,
                "SMS Failed: ${e.message}",
                Toast.LENGTH_LONG
            ).show()

            e.printStackTrace()
        }
    }
}