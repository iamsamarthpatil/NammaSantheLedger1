package com.example.nammasantheledger

import android.app.AlertDialog
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.ListView
import android.widget.SimpleAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    lateinit var db: DatabaseHelper

    lateinit var customerList: ListView

    lateinit var totalBalanceText: TextView

    lateinit var customerCountText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {

        val prefs =
            getSharedPreferences(
                "ThemePrefs",
                MODE_PRIVATE
            )

        val savedTheme =
            prefs.getInt(
                "themeMode",
                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            )

        AppCompatDelegate.setDefaultNightMode(
            savedTheme
        )

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val toolbar =
            findViewById<Toolbar>(R.id.toolbar)

        setSupportActionBar(toolbar)

        db = DatabaseHelper(this)

        customerList =
            findViewById(R.id.customerList)

        totalBalanceText =
            findViewById(R.id.totalBalanceText)

        customerCountText =
            findViewById(R.id.customerCountText)

        val addBtn =
            findViewById<ImageButton>(R.id.addCustomerBtn)

        addBtn.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    AddCustomerActivity::class.java
                )
            )
        }

        loadCustomers()

        scheduleReminder()
    }

    override fun onResume() {

        super.onResume()

        loadCustomers()
    }

    private fun loadCustomers() {

        val cursor: Cursor =
            db.getCustomers()

        val customerData =
            ArrayList<HashMap<String, String>>()

        var totalBalance = 0

        while(cursor.moveToNext()) {

            val name =
                cursor.getString(1)

            val phone =
                cursor.getString(2)

            val balance =
                cursor.getInt(3)

            totalBalance += balance

            val map =
                HashMap<String, String>()

            map["name"] =
                name

            map["phone"] =
                phone

            map["balance"] =
                "₹$balance"

            map["status"] =

                if(balance > 0)
                    "🟢 TO COLLECT"
                else
                    "✅ CLEARED"

            customerData.add(map)
        }

        totalBalanceText.text =
            "₹$totalBalance"

        customerCountText.text =
            customerData.size.toString()

        val adapter =
            SimpleAdapter(
                this,
                customerData,
                R.layout.customer_item,
                arrayOf(
                    "name",
                    "phone",
                    "balance",
                    "status"
                ),
                intArrayOf(
                    R.id.customerName,
                    R.id.customerPhone,
                    R.id.customerBalance,
                    R.id.customerStatus
                )
            )

        customerList.adapter =
            adapter

        customerList.setOnItemClickListener { _, _, position, _ ->

            val selectedCustomer =
                customerData[position]

            val intent =
                Intent(
                    this,
                    CustomerDetailsActivity::class.java
                )

            intent.putExtra(
                "customerName",
                selectedCustomer["name"]
            )

            intent.putExtra(
                "customerPhone",
                selectedCustomer["phone"]
            )

            startActivity(intent)
        }
    }

    private fun scheduleReminder() {

        val workRequest =
            PeriodicWorkRequestBuilder<WeeklyReminderWorker>(
                15,
                TimeUnit.MINUTES
            )
                .build()

        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork(
                "ledgerReminder",
                ExistingPeriodicWorkPolicy.UPDATE,
                workRequest
            )
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(
            R.menu.main_menu,
            menu
        )

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId) {

            R.id.menu_profile -> {

                startActivity(
                    Intent(
                        this,
                        ProfileActivity::class.java
                    )
                )
            }

            R.id.menu_about -> {

                startActivity(
                    Intent(
                        this,
                        AboutActivity::class.java
                    )
                )
            }

            R.id.menu_theme -> {

                showThemeDialog()
            }

            R.id.menu_change_pin -> {

                startActivity(
                    Intent(
                        this,
                        SettingsActivity::class.java
                    )
                )
            }

            R.id.menu_logout -> {

                val prefs =
                    getSharedPreferences(
                        "LoginPrefs",
                        MODE_PRIVATE
                    )

                prefs.edit()
                    .putBoolean(
                        "isLoggedIn",
                        false
                    )
                    .apply()

                startActivity(
                    Intent(
                        this,
                        LoginActivity::class.java
                    )
                )

                finish()
            }
        }

        return true
    }

    private fun showThemeDialog() {

        val options = arrayOf(
            "☀️ Light Mode",
            "🌙 Dark Mode",
            "📱 System Default"
        )

        val builder =
            AlertDialog.Builder(this)

        builder.setTitle("Choose Theme")

        builder.setItems(options) { _, which ->

            val prefs =
                getSharedPreferences(
                    "ThemePrefs",
                    MODE_PRIVATE
                )

            when(which) {

                0 -> {

                    AppCompatDelegate.setDefaultNightMode(
                        AppCompatDelegate.MODE_NIGHT_NO
                    )

                    prefs.edit()
                        .putInt(
                            "themeMode",
                            AppCompatDelegate.MODE_NIGHT_NO
                        )
                        .apply()
                }

                1 -> {

                    AppCompatDelegate.setDefaultNightMode(
                        AppCompatDelegate.MODE_NIGHT_YES
                    )

                    prefs.edit()
                        .putInt(
                            "themeMode",
                            AppCompatDelegate.MODE_NIGHT_YES
                        )
                        .apply()
                }

                2 -> {

                    AppCompatDelegate.setDefaultNightMode(
                        AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                    )

                    prefs.edit()
                        .putInt(
                            "themeMode",
                            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                        )
                        .apply()
                }
            }

            recreate()
        }

        builder.show()
    }
}