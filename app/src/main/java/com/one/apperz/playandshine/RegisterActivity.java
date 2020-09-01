package com.one.apperz.playandshine;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.one.apperz.playandshine.databinding.ActivityRegisterBinding;
import com.one.apperz.playandshine.helperLord.HelperLordConstant;
import com.one.apperz.playandshine.model.UserProfile;

import io.paperdb.Paper;

public class RegisterActivity extends AppCompatActivity {

    Context context;
    private ActivityRegisterBinding binding;
    String SELECTED_TYPE = "";
    String SELECTED_SPORT = "";
    String EXPERIENCE = "";
    ArrayAdapter adapterListOfExperience;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        mAuth = FirebaseAuth.getInstance();
        context = this;
        Paper.init(context);


        binding.inputSport.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SELECTED_SPORT = HelperLordConstant.LIST_OF_SPORTS.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                SELECTED_SPORT = "Other";
            }
        });

        //Creating the ArrayAdapter instance having the sport list
        ArrayAdapter adapterListOfSports = new ArrayAdapter(this,R.layout.custom_spinner,HelperLordConstant.LIST_OF_SPORTS);
        adapterListOfSports.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        binding.inputSport.setAdapter(adapterListOfSports);

        binding.inputExperience.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i!=0){
                    if (SELECTED_TYPE.equals("athlete")){
                        EXPERIENCE = HelperLordConstant.LEVEL_OF_ATHLETE.get(i);
                    }else if(!SELECTED_TYPE.equals("")){
                        EXPERIENCE = HelperLordConstant.YEARS_OF_EXPERIENCE.get(i);
                    }
                }else{
                    EXPERIENCE = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    public void youAreSelected(View view) {
        SELECTED_TYPE = view.getTag().toString();
        Log.d("TAG", "youAreSelected: clicked "+ SELECTED_TYPE);
        binding.a1.setBackgroundColor(Color.parseColor("#00ABCDEF"));
        binding.a2.setBackgroundColor(Color.parseColor("#00ABCDEF"));
        binding.a3.setBackgroundColor(Color.parseColor("#00ABCDEF"));
        binding.a4.setBackgroundColor(Color.parseColor("#00ABCDEF"));
        binding.a5.setBackgroundColor(Color.parseColor("#00ABCDEF"));
        binding.a6.setBackgroundColor(Color.parseColor("#00ABCDEF"));
        binding.a7.setBackgroundColor(Color.parseColor("#00ABCDEF"));
        binding.a8.setBackgroundColor(Color.parseColor("#00ABCDEF"));
        binding.a9.setBackgroundColor(Color.parseColor("#00ABCDEF"));
        final int sdk = android.os.Build.VERSION.SDK_INT;
        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.background_selected_you_are) );
        } else {
            view.setBackground(ContextCompat.getDrawable(context, R.drawable.background_selected_you_are));
        }

        if(SELECTED_TYPE.equals("athlete")||SELECTED_TYPE.equals("coach"))
            binding.inputSport.setVisibility(View.VISIBLE);
        else
            binding.inputSport.setVisibility(View.GONE);

        if(SELECTED_TYPE.equals("athlete")){
            adapterListOfExperience = new ArrayAdapter(this,R.layout.custom_spinner,HelperLordConstant.LEVEL_OF_ATHLETE);
            adapterListOfExperience.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            //Setting the ArrayAdapter data on the Spinner
            binding.inputExperience.setAdapter(adapterListOfExperience);
            binding.inputExperience.setVisibility(View.VISIBLE);
        }else {

            adapterListOfExperience = new ArrayAdapter(this,R.layout.custom_spinner,HelperLordConstant.YEARS_OF_EXPERIENCE);
            adapterListOfExperience.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            //Setting the ArrayAdapter data on the Spinner
            binding.inputExperience.setAdapter(adapterListOfExperience);
            binding.inputExperience.setVisibility(View.VISIBLE);
        }

    }

    public void signUp(View view) {

        String FNAME = binding.inputFname.getText().toString();
        String LNAME = binding.inputLname.getText().toString();
        String EMAIL = binding.inputEmail.getText().toString();
        String PASSWORD = binding.inputPassword.getText().toString();
        String PASSWORD_CON = binding.inputConfirmPassword.getText().toString();
//        SELECTED_SPORT = binding.profileSport.getText().toString();

        Log.d("TAG", "signUp: "+SELECTED_SPORT);

        boolean valid = true;

        if (SELECTED_TYPE.equals("")){
            Toast.makeText(context,"Select You are",Toast.LENGTH_SHORT).show();
            valid = false;
        }

        if (FNAME.length() < 3) {
            binding.inputFname.setError("at least 3 characters");
            valid = false;
        }else{
            binding.inputFname.setError(null);
        }
        if (LNAME.length() < 3)
            binding.inputLname.setError("at least 3 characters");
        else
            binding.inputLname.setError(null);

        if (EMAIL.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(EMAIL).matches()) {
            binding.inputEmail.setError("enter a valid email address");
            valid = false;
        } else {
            binding.inputEmail.setError(null);
        }

        if (!PASSWORD.equals(PASSWORD_CON)) {
            binding.inputConfirmPassword.setText("");
            binding.inputPassword.setError("password does not match");
            valid = false;
        }else{
            binding.inputPassword.setError(null);
        }

        if ( PASSWORD.length() < 6 ) {
            binding.inputPassword.setError("greater than 5 character");
            valid = false;
        } else {
            binding.inputPassword.setError(null);
        }

        if ((SELECTED_TYPE.equals("athlete")||SELECTED_TYPE.equals("coach")) && SELECTED_SPORT.equals("")) {
            Toast.makeText(context, "Please Select Sport", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        if (EXPERIENCE == ""){
            valid = false;
            Toast.makeText(context, "Please Select Experience", Toast.LENGTH_SHORT).show();
        }

        if (valid){
            binding.pleaseWait.setVisibility(View.VISIBLE);
            mAuth.createUserWithEmailAndPassword(EMAIL, PASSWORD)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("TAG", "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("TAG", "createUserWithEmail:failure", task.getException());
                                Toast.makeText(RegisterActivity.this, "Email Already Exists",
                                        Toast.LENGTH_SHORT).show();
                                binding.pleaseWait.setVisibility(View.GONE);
                            }
                            // ...
                        }
                    });
        }

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
        finish();
    }

    private void updateUI(FirebaseUser user) {
        if (user!=null) {
            String fnn=binding.inputFname.getText().toString();
            String lnn=binding.inputLname.getText().toString();
            String namee = fnn+" "+lnn;
            final UserProfile userProfile = new UserProfile(user.getUid(),namee,binding.inputEmail.getText().toString(),SELECTED_SPORT,SELECTED_TYPE,"","",0,"","",EXPERIENCE);
            FirebaseFirestore.getInstance().collection(context.getResources().getString(R.string.users_collection)).document(user.getUid()).set(userProfile).addOnSuccessListener(
                    new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Paper.book().write(context.getResources().getString(R.string.users_collection),userProfile);
                            binding.pleaseWait.setVisibility(View.INVISIBLE);
                            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                            finish();
                        }
                    }
            );

        }
    }

//    public void buttonBackClicked(View view) {
//        startActivity(new Intent(this,LoginActivity.class));
////        finish();
//    }
}