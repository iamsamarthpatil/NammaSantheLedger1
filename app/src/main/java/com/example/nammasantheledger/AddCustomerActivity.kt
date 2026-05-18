package com.example.nammasantheledger

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.SmsManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

class AddCustomerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_add_customer)

        val db = DatabaseHelper(this)

        val nameInput =
            findViewById<EditText>(R.id.nameInput)

        val phoneInput =
            findViewById<EditText>(R.id.phoneInput)

        val saveBtn =
            findViewById<Button>(R.id.saveCustomerBtn)

        saveBtn.setOnClickListener {

            val name =
                nameInput.text.toString().trim()

            val phone =
                phoneInput.text.toString().trim()

            if(name.isEmpty()) {

                Toast.makeText(
                    this,
                    "Enter customer name",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            if(phone.length != 10) {

                Toast.makeText(
                    this,
                    "Enter valid 10 digit number",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            val result =
                db.addCustomer(name, phone)

            if(!result) {

                Toast.makeText(
                    this,
                    "Customer already exists",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            sendWelcomeSMS(
                name,
                phone
            )

            Toast.makeText(
                this,
                "Customer Added Successfully",
                Toast.LENGTH_SHORT
            ).show()

            val intent = Intent(
                this,
                AddTransactionActivity::class.java
            )

            intent.putExtra(
                "customerName",
                name
            )

            intent.putExtra(
                "customerPhone",
                phone
            )

            startActivity(intent)

            finish()
        }
    }

    private fun sendWelcomeSMS(
        name: String,
        phone: String
    ) {

        if(
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

            return
        }

        try {

            val smsManager =
                SmsManager.getDefault()

            val message =

                "📒 Namma Santhe Ledger\n\n" +

                        "Hello $name,\n\n" +

                        "Your customer ledger account " +
                        "has been created successfully.\n\n" +

                        "You can now track:\n" +

                        "✅ Udhaar\n" +

                        "✅ Payments\n" +

                        "✅ Pending Balance\n\n" +

                        "🙏 Thank You"

            smsManager.sendTextMessage(
                phone,
                null,
                message,
                null,
                null
            )

        } catch (e: Exception) {

            e.printStackTrace()
        }
    }
}