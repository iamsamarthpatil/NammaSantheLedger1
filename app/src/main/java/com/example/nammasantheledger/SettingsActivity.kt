package com.example.nammasantheledger

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    lateinit var oldPin: EditText
    lateinit var newPin: EditText
    lateinit var otpInput: EditText
    lateinit var verifyBtn: Button

    private val fakeOTP = "1234"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_settings)

        oldPin = findViewById(R.id.oldPin)
        newPin = findViewById(R.id.newPin)
        otpInput = findViewById(R.id.otpInput)
        verifyBtn = findViewById(R.id.verifyBtn)

        Toast.makeText(
            this,
            "OTP Sent: 1234",
            Toast.LENGTH_LONG
        ).show()

        verifyBtn.setOnClickListener {

            val prefs = getSharedPreferences(
                "LoginPrefs",
                MODE_PRIVATE
            )

            val savedPin = prefs.getString("pin", "")

            if(oldPin.text.toString() != savedPin) {

                Toast.makeText(
                    this,
                    "Old MPIN Wrong",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            if(otpInput.text.toString() != fakeOTP) {

                Toast.makeText(
                    this,
                    "Invalid OTP",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            prefs.edit()
                .putString(
                    "pin",
                    newPin.text.toString()
                )
                .apply()

            Toast.makeText(
                this,
                "MPIN Changed Successfully",
                Toast.LENGTH_SHORT
            ).show()

            finish()
        }
    }
}