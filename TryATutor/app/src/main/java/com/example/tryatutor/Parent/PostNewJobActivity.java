package com.example.tryatutor.Parent;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import com.example.tryatutor.Database.AddressData;
import com.example.tryatutor.Database.JobBoardDataHandler;
import com.example.tryatutor.Database.ParentDataHandler;
import com.example.tryatutor.Database.SharedPreferencesManager;
import com.example.tryatutor.LoginAndRegistration.LoginActivity;
import com.example.tryatutor.R;
import com.example.tryatutor.Shared.GoogleMapActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PostNewJobActivity extends AppCompatActivity {

    private TextInputEditText subject,info,address,expireDate;
    private TextInputLayout subjectLayout,infoLayout,addressLayout,expireDateLayout;
    private Button postButton;
    private String wrong_input = "Wrong input";
    private ParentDataHandler parentData;
    private AddressData addressData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_new_job);

        // bind all elements
        subject = findViewById(R.id.postAJobSubject_id);
        info = findViewById(R.id.postAJobInformation_id);
        address = findViewById(R.id.postAJobAddress_id);
        expireDate = findViewById(R.id.postAJobExpireDate_id);

        subjectLayout = findViewById(R.id.postAJobSubjectLayout_id);
        infoLayout = findViewById(R.id.postAJobInformationLayout_id);
        addressLayout = findViewById(R.id.postAJobAddressLayout_id);
        expireDateLayout = findViewById(R.id.postAJobExpireDateLayout_id);

        postButton = findViewById(R.id.postAJobButton_id);

        SharedPreferencesManager sharedPreferencesManager =new SharedPreferencesManager(PostNewJobActivity.this);
        parentData = sharedPreferencesManager.getParentDataSharedPreferences();

        address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(PostNewJobActivity.this, GoogleMapActivity.class);
                intent.putExtra("senderType", "postJob");
                startActivityForResult(intent, 1);
            }
        });

        expireDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatePicker datepicker = new DatePicker(PostNewJobActivity.this);
                int curDay = datepicker.getDayOfMonth();
                int curMonth = (datepicker.getMonth());
                int curYear = datepicker.getYear();

                DatePickerDialog datePickerDialog = new DatePickerDialog(PostNewJobActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                                // day / month / year
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                Date date = null;
                                try {
                                    date = simpleDateFormat.parse(i2+"/"+(i1+1)+"/"+i);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                if(new Date().before(date))
                                {
                                    expireDateLayout.setError(null);
                                    expireDateLayout.setErrorEnabled(false);
                                    expireDate.setText(i2+"/"+(i1+1)+"/"+i);
                                }
                                else
                                {
                                    expireDate.setText("");
                                    expireDateLayout.setError(null);
                                    expireDateLayout.setErrorEnabled(false);
                                    expireDateLayout.setError(wrong_input);
                                }
                            }
                        },curYear,curMonth,curDay);
                datePickerDialog.show();
            }
        });

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!validateData())
                {
                    return;
                }

                DatePicker datepicker = new DatePicker(PostNewJobActivity.this);
                int curDay = datepicker.getDayOfMonth();
                int curMonth = (datepicker.getMonth());
                int curYear = datepicker.getYear();
                String cur_date = curDay+"/"+curMonth+"/"+curYear;

                String job_id = parentData.getuId() +"-"+ new Date().getTime();

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference(getResources().getString(R.string.JOB_BOARD_TABLE));
                JobBoardDataHandler jobBoardData = new JobBoardDataHandler(job_id,
                        parentData.getuId(),
                        info.getText().toString().trim(),
                        addressData.getAddress(),
                        cur_date,
                        expireDate.getText().toString(),
                        parentData.getName(),
                        subject.getText().toString().trim(),
                        addressData.getLatitude(),
                        addressData.getLongitude()
                        );
                reference.child(job_id).setValue(jobBoardData);

                AlertDialog.Builder builder = new AlertDialog.Builder(PostNewJobActivity.this);
                builder.setTitle("Job Posted");
                builder.setMessage("Your job has been posted.")
                        .setCancelable(false)
                        .setNeutralButton("To Dashboard", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(PostNewJobActivity.this, ParentHomeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                        });

                builder.show();
            }
        });
    }

    private boolean validateData() {
        boolean flag = true;
        if(subject.getText().toString().trim().isEmpty())
        {
            flag=false;
            subjectLayout.setError(null);
            subjectLayout.setErrorEnabled(false);
            subjectLayout.setError("Field cannot be empty");
        }
        else
        {
            subjectLayout.setError(null);
            subjectLayout.setErrorEnabled(false);
        }

        if(address.getText().toString().trim().isEmpty())
        {
            flag=false;
            addressLayout.setError(null);
            addressLayout.setErrorEnabled(false);
            addressLayout.setError("Field cannot be empty");
        }
        else
        {
            addressLayout.setError(null);
            addressLayout.setErrorEnabled(false);
        }
        if(expireDate.getText().toString().trim().isEmpty())
        {
            flag=false;
            expireDateLayout.setError(null);
            expireDateLayout.setErrorEnabled(false);
            expireDate.setError("Field cannot be empty");
        }
        else
        {
            expireDateLayout.setError(null);
            expireDateLayout.setErrorEnabled(false);
        }
        return flag;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == 1) {
            addressData = (AddressData) data.getSerializableExtra("addressData");
            address.setText(addressData.getAddress());
        }
    }
}