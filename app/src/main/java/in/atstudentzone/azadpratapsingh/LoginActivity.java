package in.atstudentzone.azadpratapsingh;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import in.atstudentzone.azadpratapsingh.utils.Constants;
import in.atstudentzone.azadpratapsingh.utils.Utils;


public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private EditText mobileNumber;
    private EditText enterOtpHere;

    private boolean mVerificationInProgress = false;

    //Adding verification id as a member variable.
    private String mVerificationId;

    //Adding a member variable for PhoneAuthProvider.ForceResendingToken callback.
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    //Adding a member variable for a callback which is our PhoneAuthProvider.OnVerificationStateChangeCallbacks.
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);
        // Restoring the instance state
        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }

        //FirebaseAuth.getInstance().getFirebaseAuthSettings().setAppVerificationDisabledForTesting(true);

        mobileNumber = findViewById(R.id.mobileNumber);
        enterOtpHere = findViewById(R.id.enterOtpHere);

        mAuth = FirebaseAuth.getInstance();

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                mVerificationInProgress = false;

                //Calling signInWithPhoneAuthCredential.
                signInWithPhoneAuthCredential(credential);
            }

            //Creating onVerificationFailed() method.
            @Override
            public void onVerificationFailed(FirebaseException e) {

                mVerificationInProgress = false;

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // Setting error to text field
                    mobileNumber.setError("Invalid phone number.");
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Toast.makeText(getApplicationContext(), "Quota exceeded", Toast.LENGTH_LONG).show();
                }
            }

            // Creating onCodeSent() method called after the verification code has been sent by SMS to the provided phone number.
            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code will be sent to the provided phone number
                // Now need to ask the user for entering the code and then construct a credential
                // through integrating the code with a verification ID.
                Toast.makeText(getApplicationContext(), "SMS sent to entered phone number.", Toast.LENGTH_LONG).show();

                // Save the verification ID and resend token to use them later
                mVerificationId = verificationId;
                mResendToken = token;
                enterOtpHere.requestFocus();
            }
        };
    }

    public void getOtpOnClick(View view) {
        validatePhoneNumber();
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+91" + mobileNumber.getText().toString())       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
        mVerificationInProgress = true;
    }

    public void performLogin(View view) {
        String code = enterOtpHere.getText().toString();
        if (TextUtils.isEmpty(code)) {
            enterOtpHere.setError("Cannot be empty.");
            return;
        }
        //Call the verifyPhoneNumberWithCode () method.
        verifyPhoneNumberWithCode(mVerificationId, code);
    }

    //Creating a helper method for verification of phone number with code.
    // Entering code and manually signing in with that code
    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {

        //Adding onCompleteListener to signInWithCredential.
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //Sign-In is successful, update the UI with the signed-in user's information
                            FirebaseUser user = task.getResult().getUser();
                            Utils.setStringPreference(getApplicationContext(), Constants.USER_ID, user.getUid());
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // If the Sign-In fails, it will display a message and also update the UI
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                //mVerificationField.setError("Invalid code.");
                                Toast.makeText(getApplicationContext(), "Invalid code entered", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }

    // Creating helper method for validating phone number.
    private boolean validatePhoneNumber() {
        String phoneNumber = mobileNumber.getText().toString();
        if (TextUtils.isEmpty(phoneNumber)) {
            mobileNumber.setError("Invalid phone number.");
            return false;
        }
        return true;
    }

    //Creating helper method for resending verification code.
    private void resendVerificationCode(String phoneNumber, PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }
}
