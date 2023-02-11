package com.voitov.contactsreader

import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val permissionType =
            ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS)
        if (permissionType == PackageManager.PERMISSION_GRANTED) {
            requestContacts()
        } else {
            requestPermission()
        }
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.READ_CONTACTS),
            READ_CONTACTS_REQUEST_CODE,
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d(TAG, requestCode.toString())
        if (requestCode == READ_CONTACTS_REQUEST_CODE && grantResults.isNotEmpty()) {
            val permissionGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED
            if (permissionGranted) {
                requestContacts()
            } else {
                Log.d(TAG, "Access denied")
            }
        }
    }

    private fun requestContacts() {
        val projection = arrayOf(
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.Contacts.HAS_PHONE_NUMBER
        )

        val cursor = contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            projection,
            null,
            null
        )

        while (cursor?.moveToNext() == true) {
            val id = cursor.getInt(
                cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID)
            )

            val name =
                cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME))

            val contact = Contact(id, name)
            Log.d(TAG, contact.toString())
        }
        cursor?.close()
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val READ_CONTACTS_REQUEST_CODE = 100
    }
}