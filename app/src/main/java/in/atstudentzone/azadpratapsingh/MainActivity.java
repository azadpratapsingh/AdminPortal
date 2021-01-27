package in.atstudentzone.azadpratapsingh;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import in.atstudentzone.azadpratapsingh.model.HouseProperty;
import in.atstudentzone.azadpratapsingh.utils.Constants;
import in.atstudentzone.azadpratapsingh.utils.Utils;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void performDataEntry(View view) {
        Utils.setStringPreference(getApplicationContext(), Constants.USER_ID, "randomId");
        Intent intent = new Intent(getApplicationContext(), DataSubmissionActivity.class);
        startActivity(intent);
        /*final HouseProperty houseProperty = HouseProperty.builder()
                .phoneNumber("something1")
                .city("city1")
                .area("something")
                .ownerName("something")
                .preferredLanguage("something")
                .pgName("something")
                .build();

        Toast.makeText(getApplicationContext(), "Performing data entry", Toast.LENGTH_LONG).show();

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("houseProperties");
        mDatabase.child("something1").setValue(houseProperty);*/

    }

    public void performDataValidation(View view) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(Constants.TABLE_NAME);
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                final HouseProperty houseProperty = snapshot.getValue(HouseProperty.class);
                Toast.makeText(getApplicationContext(), "Getting houseProperty" + houseProperty.getCity(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //Toast.makeText(getApplicationContext(), "Performing data validation", Toast.LENGTH_LONG).show();
    }
}
