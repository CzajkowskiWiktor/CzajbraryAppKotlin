package com.example.czajbraryapp.Activities.Activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.czajbraryapp.Firestore.FirestoreClass
import com.example.czajbraryapp.Models.User
import com.example.czajbraryapp.R
import com.example.czajbraryapp.Utils.Constants
import com.example.czajbraryapp.Utils.GlideLoader
import kotlinx.android.synthetic.main.activity_user_profile.*
import java.io.IOException

class UserProfileActivity : BaseActivity(), View.OnClickListener {

    private lateinit var mUserDetails: User
    private var mSelectedImageFileUri: Uri? = null
    private var mUserProfileImageURL: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        if(intent.hasExtra(Constants.EXTRA_USER_DETAILS)) {
            //get user details from intent as a parcelableExtra
            mUserDetails = intent.getParcelableExtra(Constants.EXTRA_USER_DETAILS)!!
        }

        //setting first, last name and also email to UserProfile screen
        til_first_name.setText(mUserDetails.firstName)
        til_last_name.setText(mUserDetails.lastName)
        til_email.isEnabled = false
        til_email.setText(mUserDetails.email)

        if(mUserDetails.profileCompleted == 0) {
            //changing title text
            tv_title.text = resources.getString(R.string.title_complete_profile)
            //blocking to change values: first,last Name and email
            til_first_name.isEnabled = false
            til_last_name.isEnabled = false
        } else {
            setupActionBar()

            til_first_name.isEnabled = true
            til_last_name.isEnabled = true
            tv_title.text = resources.getString(R.string.title_edit_profile)
            GlideLoader(this@UserProfileActivity).loadUserPicture(mUserDetails.image, iv_user_photo)

            if (mUserDetails.mobile != 0L) {
                til_mobile_number.setText(mUserDetails.mobile.toString())
            }
            if (mUserDetails.gender == Constants.MALE) {
                rb_male.isChecked = true
            } else {
                rb_female.isChecked = true
            }
        }

        iv_user_photo.setOnClickListener(this@UserProfileActivity)
        btn_submit.setOnClickListener(this@UserProfileActivity)
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.iv_user_photo -> {
                    // we check if the permission is already allowed or we need to request for it.
                    // First of all we check the READ_EXTERNAL_STORAGE permission and if it is not allowed we will request for the same.
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                        == PackageManager.PERMISSION_GRANTED
                    ) {
                        Constants.showImageChooser(this)
                        //showErrorSnackBar("You already have the storage permission.", false)
                    } else {
                        /*Requests permissions to be granted to this application. These permissions
                         must be requested in your manifest, they should not be granted to your app,
                         and they should have protection level*/

                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            Constants.READ_STORAGE_PERMISSION_CODE
                        )
                    }
                }

                R.id.btn_submit -> {
                    if (validateUserProfileDetails()) {
                        showProgressDialog(resources.getString(R.string.please_wait))

                        if (mSelectedImageFileUri != null){
                            FirestoreClass().uploadImageToCloudStorage(this, mSelectedImageFileUri, Constants.USER_PROFILE_IMAGE)
                        } else {
                            updateUserProfileDetails()
                        }
                    }
                }
            }
        }
    }

    /**
     * A function to update user profile details to the firestore.
     */
    private fun updateUserProfileDetails() {
        val userHashMap = HashMap<String, Any>()

        // Get the FirstName from editText and trim the space
        val firstName = til_first_name.text.toString().trim { it <= ' ' }
        if (firstName != mUserDetails.firstName) {
            userHashMap[Constants.FIRST_NAME] = firstName
        }

        // Get the LastName from editText and trim the space
        val lastName = til_last_name.text.toString().trim { it <= ' ' }
        if (lastName != mUserDetails.lastName) {
            userHashMap[Constants.LAST_NAME] = lastName
        }

        val moblieNumber = til_mobile_number.text.toString().trim{it <= ' '}

        val gender = if(rb_male.isChecked) {
            Constants.MALE
        } else {
            Constants.FEMALE
        }

        if(mUserProfileImageURL.isNotEmpty()) {
            userHashMap[Constants.IMAGE] = mUserProfileImageURL
        }

        if (moblieNumber.isNotEmpty() && moblieNumber != mUserDetails.mobile.toString()) {
            userHashMap[Constants.MOBILE] = moblieNumber.toLong()
        }

        if (gender.isNotEmpty() && gender != mUserDetails.gender) {
            userHashMap[Constants.GENDER] = gender
        }

        //key: gender value: male
        userHashMap[Constants.GENDER] = gender

        //make user profile completed
        userHashMap[Constants.COMPLETE_PROFILE] = 1

        //update user details to firestore
        FirestoreClass().updateUserProfileData(this, userHashMap)
    }

    fun userProfileUpdateSuccess() {
        hideProgressDialog()

        Toast.makeText(
            this@UserProfileActivity,
            resources.getString(R.string.msg_profile_update_success),
            Toast.LENGTH_SHORT
        ).show()

        //go to main page
        startActivity(Intent(this@UserProfileActivity, DashboardActivity::class.java))
        finish()
    }

    /**
     * This function will identify the result of runtime permission after the user allows or deny permission based on the unique code.
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            //If permission is granted
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Constants.showImageChooser(this)
                //showErrorSnackBar("The storage permission is granted.", false)
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(
                    this,
                    resources.getString(R.string.read_storage_permission_denied),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE) {
                if (data != null) {
                    try {
                        //the uri of selected img from phone storage
                        mSelectedImageFileUri = data.data!!

                        GlideLoader(this).loadUserPicture(mSelectedImageFileUri!!, iv_user_photo)
                        //iv_user_photo.setImageURI(selectedImageFileUri)
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(
                            this@UserProfileActivity,
                            resources.getString(R.string.image_selection_failed),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            // A log is printed when user close or cancel the image selection.
            Log.e("Request Cancelled", "Image selection cancelled")
        }
    }

    /**
     * A function for actionBar Setup.
     */
    private fun setupActionBar() {

        setSupportActionBar(toolbar_user_profile_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        toolbar_user_profile_activity.setNavigationOnClickListener { onBackPressed() }
    }

    //checking if user provide phone number
    private fun validateUserProfileDetails(): Boolean {
        return when {
            TextUtils.isEmpty(til_mobile_number.text.toString().trim{it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_mobile_number), true)
                false
            }
            else -> {
                true
            }
        }
    }

    /**
     * A function to notify the success result of image upload to the Cloud Storage.
     * @param imageURL After successful upload the Firebase Cloud returns the URL.
     */
    fun imageUploadSuccess(imageURL: String) {
        //hideProgressDialog()
        mUserProfileImageURL = imageURL
        updateUserProfileDetails()
    }
}