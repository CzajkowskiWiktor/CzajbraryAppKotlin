package com.example.czajbraryapp.Activities.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.example.czajbraryapp.Firestore.FirestoreClass
import com.example.czajbraryapp.Models.Address
import com.example.czajbraryapp.R
import com.example.czajbraryapp.Utils.Constants
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_address_list.*
import kotlinx.android.synthetic.main.activity_settings.*

class AddressListActivity : BaseActivity() {

    private var mAddressDetails: Address? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_list)

        setupActionBar()

        if (intent.hasExtra(Constants.EXTRA_ADDRESS_DETAILS)) {
            mAddressDetails = intent.getParcelableExtra(Constants.EXTRA_ADDRESS_DETAILS)
        }

        btn_submit_address.setOnClickListener {
            saveAddressToFirestore()
        }
    }

    //A function for actionBar Setup.
    private fun setupActionBar() {
        setSupportActionBar(toolbar_add_edit_address_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        toolbar_add_edit_address_activity.setNavigationOnClickListener { onBackPressed() }
    }

    /**
     * A function to validate the address input entries.
     */
    private fun validateData(): Boolean {
        return when {
            TextUtils.isEmpty(til_full_name.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_please_enter_full_name),
                    true
                )
                false
            }

            TextUtils.isEmpty(til_phone_number.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_please_enter_phone_number),
                    true
                )
                false
            }

            TextUtils.isEmpty(til_address.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_please_enter_address), true)
                false
            }

            TextUtils.isEmpty(til_zip_code.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_please_enter_zip_code), true)
                false
            }
            else -> {
                true
            }
        }
    }

    /**
     * A function to save the address to the cloud firestore.
     */
    private fun saveAddressToFirestore() {

        // Here we get the text from editText and trim the space
        val fullName: String = til_full_name.text.toString().trim { it <= ' ' }
        val phoneNumber: String = til_phone_number.text.toString().trim { it <= ' ' }
        val address: String = til_address.text.toString().trim { it <= ' ' }
        val zipCode: String = til_zip_code.text.toString().trim { it <= ' ' }
        val additionalNote: String = til_additional_note.text.toString().trim { it <= ' ' }
        val otherDetails: String = til_other_details.text.toString().trim { it <= ' ' }

        if (validateData()) {
            // Show the progress dialog.
            showProgressDialog(resources.getString(R.string.please_wait))

            val addressType: String = when {
                rb_home.isChecked -> {
                    Constants.HOME
                }
                else -> {
                    Constants.OFFICE
                }
            }

            val addressModel = Address(
                FirestoreClass().getCurrentUserID(),
                fullName,
                phoneNumber,
                address,
                zipCode,
                additionalNote,
                addressType
            )
             mAddressDetails = addressModel

            //adding an address to FireStore
            FirestoreClass().addAddress(this@AddressListActivity, addressModel)
        }
    }

    /**
     * A function to notify the success result of address saved or updated.
     */
    fun addUpdateAddressSuccess() {
        // Hide progress dialog
        hideProgressDialog()

        val notifySuccessMessage: String = if (mAddressDetails != null && mAddressDetails!!.id.isNotEmpty()) {
            resources.getString(R.string.msg_your_address_updated_successfully)
        } else {
            resources.getString(R.string.err_your_address_added_successfully)
        }

        Toast.makeText(
            this@AddressListActivity,
            notifySuccessMessage,
            Toast.LENGTH_SHORT
        ).show()

//        setResult(RESULT_OK)

        //switching screen to checkout screen
        val intent = Intent(this, CheckoutActivity::class.java)
        intent.putExtra(Constants.EXTRA_SELECTED_ADDRESS, mAddressDetails)
        //clearing our flags/layers/activities
        //intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        //finish()
    }
}