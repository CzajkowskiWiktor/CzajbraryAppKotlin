package com.example.czajbraryapp.Activities.Activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import com.example.czajbraryapp.Firestore.FirestoreClass
import com.example.czajbraryapp.Models.User
import com.example.czajbraryapp.R
import com.example.czajbraryapp.Utils.Constants
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : BaseActivity(), View.OnClickListener {

    //for facebook
//    lateinit var callbackManager: CallbackManager
//
//    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        //click event to Forgot Password
        tv_forgot_password.setOnClickListener(this)
        //click event to Login button
        btn_login.setOnClickListener(this)
        //click event to Register
        tv_register.setOnClickListener(this)

        //TODO: facebook login implementation
//        callbackManager = CallbackManager.Factory.create()

        // Initialize Firebase Auth
//        auth = Firebase.auth
        //facebook login button
//        login_button.setReadPermissions(listOf("public_profile", "email"))
//        // Callback registration
//        login_button.registerCallback(callbackManager, object : FacebookCallback<LoginResult?> {
//            override fun onSuccess(loginResult: LoginResult?) {
//                Log.d("TAG", "Success Login")
//                // Get User's Info
//                handleFacebookAccessToken(loginResult!!.accessToken)
//            }
//
//            override fun onCancel() {
//                Toast.makeText(this@LoginActivity, "Login Cancelled", Toast.LENGTH_LONG).show()
//            }
//
//            override fun onError(exception: FacebookException) {
//                Toast.makeText(this@LoginActivity, exception.message, Toast.LENGTH_LONG).show()
//            }
//        })

//        facebook_login_btn.setOnClickListener {
//            LoginManager.getInstance().logInWithReadPermissions(this, listOf("public_profile", "email"))
//        }
//
//        // Callback registration
//        LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult?> {
//            override fun onSuccess(loginResult: LoginResult?) {
//                Log.d("TAG", "Success Login")
//                // Get User's Info
//                handleFacebookAccessToken(loginResult!!.accessToken)
//            }
//
//            override fun onCancel() {
//                Toast.makeText(this@LoginActivity, "Login Cancelled", Toast.LENGTH_LONG).show()
//            }
//
//            override fun onError(exception: FacebookException) {
//                Toast.makeText(this@LoginActivity, exception.message, Toast.LENGTH_LONG).show()
//            }
//        })

    }

//    private fun handleFacebookAccessToken(token: AccessToken) {
////        Log.d(TAG, "handleFacebookAccessToken:$token")
//        val credential = FacebookAuthProvider.getCredential(token.token)
//        auth.signInWithCredential(credential)
//            .addOnCompleteListener(this) { task ->
//                if (task.isSuccessful) {
//                    // Sign in success, update UI with the signed-in user's information
////                    Log.d(TAG, "signInWithCredential:success")
//                    val user = auth.currentUser
//                    updateUI(user)
//                } else {
//                    // If sign in fails, display a message to the user.
//                    Toast.makeText(this@LoginActivity, "Authentication failed.",
//                        Toast.LENGTH_SHORT).show()
//                    updateUI(null)
//                }
//
//                // ...
//            }
//    }
//
//    private fun updateUI(user: FirebaseUser?) {
//        if(user != null) {
//            val intent = Intent(this@LoginActivity, MainActivity::class.java)
//            startActivity(intent)
//        } else {
//            Toast.makeText(this, "Please sign in to continue",
//                Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        // Pass the activity result back to the Facebook SDK
//        callbackManager.onActivityResult(requestCode, resultCode, data)
//    }


    // In Login screen the clickable components are Login Button, ForgotPassword text and Register Text.
    override fun onClick(view: View?) {
        if (view != null) {
            when (view.id) {
                R.id.tv_forgot_password -> {
                    val intent = Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
                    startActivity(intent)
                }

                R.id.btn_login -> {
                    logInRegisteredUser()
                }

                R.id.tv_register -> {
                    // Launch the register screen when the user clicks on the text.
                    val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    /**
     * A function to validate the login entries of a user.
     */
    private fun validateLoginDetails(): Boolean {
        return when {
            TextUtils.isEmpty(til_email.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }
            TextUtils.isEmpty(til_password.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }
            else -> {
//                showErrorSnackBar(resources.getString(R.string.login_successfull), false)
                true
            }
        }
    }

    //Login Function with Firebase
    private fun logInRegisteredUser() {
        if(validateLoginDetails()) {
            //show Progress dailog
            showProgressDialog(resources.getString(R.string.please_wait))

            //get the text from input and trim spaces
            val email: String = til_email.text.toString().trim { it <= ' ' }
            val password: String = til_password.text.toString().trim { it <= ' ' }

            //Log In using FirebaseAuth
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if(task.isSuccessful) {
                        FirestoreClass().getUserDetails(this@LoginActivity)
                    } else {
                        hideProgressDialog()
                        showErrorSnackBar(task.exception!!.message.toString(), true)
                    }
                }
        }
    }

    //A function to notify user that logged in success and get the user details from the FireStore database after authentication.
    fun userLoggedInSuccess(user: User) {
        // Hide the progress dialog.
        hideProgressDialog()

        // Print the user details in the log as of now.
//        Log.i("First Name: ", user.firstName)
//        Log.i("Last Name: ", user.lastName)
//        Log.i("Email: ", user.email)

        //checking if profile is completed
        if (user.profileCompleted == 0) {
            // if user's profile is not completed, show this screen
            val intent = Intent(this@LoginActivity, UserProfileActivity::class.java)
            intent.putExtra(Constants.EXTRA_USER_DETAILS, user)
            startActivity(intent)
        } else {
            //if user's profile is completed show MainActivity
            val intent = Intent(this@LoginActivity, DashboardActivity::class.java)
            startActivity(intent)
        }
        finish()
    }
}