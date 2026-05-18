package com.example.nammasantheledger

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.telephony.SmsManager
import androidx.core.app.ActivityCompat
import androidx.work.Worker
import androidx.work.WorkerParameters

class WeeklyReminderWorker(
    context: android.content.Context,
    params: WorkerParameters
) : Worker(context, params) {

    override fun doWork(): Result {

        try {

            val db =
                DatabaseHelper(applicationContext)

            val cursor =
                db.getCustomers()

            while(cursor.moveToNext()) {

                val name =
                    cursor.getString(1)

                val phone =
                    cursor.getString(2)

                val balance =
                    cursor.getInt(3)

                if(balance > 0) {

                    sendReminderSMS(
                        phone,
                        name,
                        balance
                    )
                }
            }

            return Result.success()

        } catch (e: Exception) {

            e.printStackTrace()

            return Result.failure()
        }
    }

    private fun sendReminderSMS(
        phone: String,
        name: String,
        balance: Int
    ) {

        if (
            ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.SEND_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        try {

            val smsManager =

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

                    applicationContext.getSystemService(
                        SmsManager::class.java
                    )

                } else {

                    SmsManager.getDefault()
                }

            val message =

                "📒 Namma Santhe Ledger\n\n" +

                        "Hello $name,\n\n" +

                        "Reminder 🔔\n" +

                        "Pending Amount: ₹$balance\n\n" +

                        "Please clear your dues.\n\n" +

                        "Thank You 🙏"

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