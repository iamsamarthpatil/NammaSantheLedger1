package com.example.nammasantheledger

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences(
            "LoginPrefs",
            Context.MODE_PRIVATE
        )

        if(
            prefs.getBoolean(
                "isLoggedIn",
                false
            )
        ) {

            startActivity(
                Intent(
                    this,
                    MainActivity::class.java
                )
            )

            finish()
        }

        setContentView(R.layout.activity_login)

        val phoneInput =
            findViewById<EditText>(R.id.phoneInput)

        val pinInput =
            findViewById<EditText>(R.id.pinInput)

        val loginButton =
            findViewById<Button>(R.id.loginButton)

        loginButton.setOnClickListener {

            val phone =
                phoneInput.text.toString().trim()

            val pin =
                pinInput.text.toString().trim()

            if(phone.length != 10) {

                Toast.makeText(
                    this,
                    "Enter valid 10 digit number",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            if(pin.length != 4) {

                Toast.makeText(
                    this,
                    "MPIN must contain 4 digits",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            val savedPhone =
                prefs.getString(
                    "phone",
                    null
                )

            if(savedPhone == null) {

                prefs.edit()
                    .putString(
                        "phone",
                        phone
                    )
                    .putString(
                        "pin",
                        pin
                    )
                    .putBoolean(
                        "isLoggedIn",
                        true
                    )
                    .apply()

                Toast.makeText(
                    this,
                    "Account Created Successfully",
                    Toast.LENGTH_SHORT
                ).show()

            } else {

                val savedPin =
                    prefs.getString(
                        "pin",
                        ""
                    )

                if(
                    phone == savedPhone &&
                    pin == savedPin
                ) {

                    prefs.edit()
                        .putBoolean(
                            "isLoggedIn",
                            true
                        )
                        .apply()

                    Toast.makeText(
                        this,
                        "Login Successful",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {

                    Toast.makeText(
                        this,
                        "Wrong Phone or MPIN",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@setOnClickListener
                }
            }

            startActivity(
                Intent(
                    this,
                    MainActivity::class.java
                )
            )

            finish()
        }
    }
}