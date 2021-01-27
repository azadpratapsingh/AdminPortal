package in.atstudentzone.azadpratapsingh;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

import in.atstudentzone.azadpratapsingh.model.BooleanChecker;
import in.atstudentzone.azadpratapsingh.model.HouseProperty;
import in.atstudentzone.azadpratapsingh.utils.Constants;
import in.atstudentzone.azadpratapsingh.utils.Utils;


public class DataFormActivity extends AppCompatActivity {

    private EditText mobileNumber;
    private EditText propertyName;
    private EditText city;
    private EditText area;
    private EditText ownerName;
    private EditText prefLanguage;
    private DatabaseReference mDatabase;
    private ChildEventListener databaseChildEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_form);
        mobileNumber = findViewById(R.id.clientMobileNumber);
        propertyName = findViewById(R.id.propertyName);
        city = findViewById(R.id.city);
        area = findViewById(R.id.localArea);
        ownerName = findViewById(R.id.ownerName);
        prefLanguage = findViewById(R.id.preferredLanguage);
        mDatabase = FirebaseDatabase.getInstance().getReference(Constants.TABLE_NAME);
        databaseChildEventListener = getDatabaseChildListeners();
    }

    public void validateMobileNumber(View view) {
        if(Utils.validateEditTextInput(mobileNumber)) {
            checkIfClientAlreadyExists(mobileNumber.getText().toString());
        } else {
            mobileNumber.setError("Invalid phone number.");
        }
    }

    private boolean validateInputs() {
        boolean validInputs = true;
        if(!Utils.validateEditTextInput(propertyName)) {
            propertyName.setError("Invalid property name");
            propertyName.requestFocus();
            validInputs = false;
        }
        if(!Utils.validateEditTextInput(city)) {
            city.setError("Invalid city name");
            city.requestFocus();
            validInputs = false;
        }
        if(!Utils.validateEditTextInput(area)) {
            area.setError("Invalid local/area name.");
            area.requestFocus();
            validInputs = false;
        }
        return validInputs;
    }

    public void submitData(View view) {
        if(!validateInputs()) {
            return;
        }

        mDatabase.addChildEventListener(databaseChildEventListener);

        final HouseProperty houseProperty = HouseProperty.builder()
                .phoneNumber(mobileNumber.getText().toString())
                .pgName(propertyName.getText().toString())
                .city(city.getText().toString())
                .area(area.getText().toString())
                .ownerName(Utils.validateEditTextInput(ownerName) ? ownerName.getText().toString() : Constants.NA_STRING)
                .preferredLanguage(Utils.validateEditTextInput(prefLanguage) ? prefLanguage.getText().toString() : Constants.NA_STRING)
                .applicationStatus(Constants.APPLICATION_STATUS_SUBMITTED)
                .build();

        mDatabase.child(Utils.getStringPreference(getApplicationContext(), Constants.USER_ID) +
                Constants.UNDERSCORE + mobileNumber.getText().toString()).setValue(houseProperty);
        Toast.makeText(getApplicationContext(), "Data entry successful", Toast.LENGTH_LONG).show();
        findViewById(R.id.dataEntryLayout).setVisibility(View.GONE);
    }

    private ChildEventListener getDatabaseChildListeners() {
        return new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                mDatabase.child(dataSnapshot.getKey()).child(Constants.APPLICATION_STATUS).setValue(Constants.APPLICATION_STATUS_PENDING_APPROVAL);
                mDatabase.removeEventListener(databaseChildEventListener);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {}

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
    }

    private void checkIfClientAlreadyExists(String phoneNumber) {
        final BooleanChecker booleanChecker = BooleanChecker.builder().condition(false).build();


        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Map<String, Map<String, String>> data = (Map<String, Map<String, String>>) snapshot.getValue();

                if(null != data) {
                    for(String adminUser: data.keySet()) {
                        if(!booleanChecker.isCondition() && null != adminUser && adminUser.contains(phoneNumber)) {
                            booleanChecker.setCondition(true);
                            break;
                        }
                    }
                }

                if(!booleanChecker.isCondition()) {
                    findViewById(R.id.dataEntryLayout).setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(getApplicationContext(), "Client already exists", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
