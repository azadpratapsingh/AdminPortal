package in.atstudentzone.azadpratapsingh;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import in.atstudentzone.azadpratapsingh.model.HouseProperty;
import in.atstudentzone.azadpratapsingh.utils.Constants;
import in.atstudentzone.azadpratapsingh.utils.Utils;


public class DataSubmissionActivity extends AppCompatActivity {

    private TextView submitted;
    private TextView pendingApproval;
    private TextView approved;
    private TextView rejected;
    private TextView leads;
    private ProgressBar progressBar;
    private LinearLayout dataContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_submission);

        submitted = (TextView) findViewById(R.id.submitted);
        pendingApproval = (TextView) findViewById(R.id.pendingApproval);
        approved = (TextView) findViewById(R.id.approved);
        rejected = (TextView) findViewById(R.id.rejected);
        leads = (TextView) findViewById(R.id.leads);
        progressBar = (ProgressBar) findViewById(R.id.pbLoading);
        dataContainer = (LinearLayout) findViewById(R.id.dataContainer);
    }

    public void submitData(View view) {
        Intent intent = new Intent(getApplicationContext(), DataFormActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.VISIBLE);
        dataContainer.setVisibility(View.GONE);
        populateData();
    }

    private void populateData() {
        final String adminUser = Utils.getStringPreference(getApplicationContext(), Constants.USER_ID);

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(Constants.TABLE_NAME);
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                final Map<String, Map<String, String>> data = (Map<String, Map<String, String>>) snapshot.getValue();
                List<HouseProperty> housePropertyList = new ArrayList<>();
                ObjectMapper mapper = new ObjectMapper();

                if(null != data) {
                    for(String dataSetKey: data.keySet()) {
                        if(null != dataSetKey && dataSetKey.contains(adminUser)) {
                            final HouseProperty houseProperty = mapper.convertValue(data.get(dataSetKey), HouseProperty.class);
                            housePropertyList.add(houseProperty);
                        }
                    }
                }
                updateTextViews(housePropertyList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

            private void updateTextViews(List<HouseProperty> housePropertyList) {
                int submittedCount = 0, pendingApprovalCount = 0, approvedCount = 0, rejectedCount = 0, leadsCount = 0;
                for(HouseProperty houseProperty: housePropertyList) {
                    if(null == houseProperty.getApplicationStatus()) {
                        continue;
                    }
                    switch (houseProperty.getApplicationStatus()) {
                        case Constants.APPLICATION_STATUS_SUBMITTED:
                            submittedCount++;
                            break;
                        case Constants.APPLICATION_STATUS_PENDING_APPROVAL:
                            pendingApprovalCount++;
                            break;
                        case Constants.APPLICATION_STATUS_APPROVED:
                            approvedCount++;
                            break;
                        case Constants.APPLICATION_STATUS_REJECTED:
                            rejectedCount++;
                            break;
                        case Constants.APPLICATION_STATUS_LEADS:
                            leadsCount++;
                            break;
                        default:
                            break;
                    }
                }

                submitted.setText(submittedCount == 0 ? Constants.APPLICATION_SUBMITTED_COUNT_ZERO_TEXT : Constants.APPLICATION_SUBMITTED_COUNT_TEXT + getFormattedCount(submittedCount));
                pendingApproval.setText(pendingApprovalCount == 0 ? Constants.PENDING_APPROVAL_COUNT_ZERO_TEXT : Constants.PENDING_APPROVAL_COUNT_TEXT + getFormattedCount(pendingApprovalCount));
                approved.setText(approvedCount == 0 ? Constants.APPROVED_COUNT_ZERO_TEXT : Constants.APPROVED_COUNT_TEXT + getFormattedCount(approvedCount));
                rejected.setText(rejectedCount == 0 ? Constants.REJECTED_COUNT_ZERO_TEXT : Constants.REJECTED_COUNT_TEXT + getFormattedCount(rejectedCount));
                leads.setText(leadsCount == 0 ? Constants.LEADS_COUNT_ZERO_TEXT : Constants.LEADS_COUNT_TEXT + getFormattedCount(leadsCount));

                progressBar.setVisibility(View.GONE);
                dataContainer.setVisibility(View.VISIBLE);
            }

            private String getFormattedCount(int count) {
                if(count < 10) {
                    return "00" + count;
                } else if(count < 100) {
                    return "0" + count;
                } else {
                    return "" + count;
                }
            }
        });
    }
}
