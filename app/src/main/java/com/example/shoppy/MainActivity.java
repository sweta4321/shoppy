package com.example.shoppy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.shoppy.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    Button btnSignUp,btnSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnSignIn=(Button)findViewById(R.id.btnSignIn);
        btnSignUp=(Button)findViewById(R.id.btnSignUp);


        //Init paper
        Paper.init(this);


        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent signi=new Intent(MainActivity.this,signin.class);
                startActivity(signi);
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent signu=new Intent(MainActivity.this,signup.class);
                startActivity(signu);
            }
        });

        //check remember
        String user=Paper.book().read(common.USER_KEY);
        String pwd=Paper.book().read(common.PWD_KEY);
        if(user!=null && pwd!=null)
        {
            if(!user.isEmpty() && !pwd.isEmpty())
                login(user,pwd);
        }


    }

    private void login(final String phone, final String pwd) {

        //Initialize FIREBASE
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        final DatabaseReference table_user=database.getReference("User");//"User" is table name in database


        if (common.isConnectedToInternet(getBaseContext())) {

            //Initialize FIREBASE
            final FirebaseDatabase database1=FirebaseDatabase.getInstance();
            final DatabaseReference table_user1=database1.getReference("User");//"User" is table name in database


            //waiting dialogbox after signin
            final ProgressDialog mDialog = new ProgressDialog(MainActivity.this);
            mDialog.setMessage("waiting...");
            mDialog.show();

            table_user1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //check if user exists or not
                    if (dataSnapshot.child(phone).exists()) {
                        //Get User Information
                        mDialog.dismiss();
                        User user = dataSnapshot.child(phone).getValue(User.class);
                        user.setPhone(phone); //set Phone.

                        if (user.getPassword().equals(pwd)){
                            {
                                Intent homeIntent = new Intent(MainActivity.this, Home.class);
                                common.currentUser = user;
                                startActivity(homeIntent);
                                finish();
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Sign in failed", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        mDialog.dismiss();
                        Toast.makeText(MainActivity.this, "User not exist", Toast.LENGTH_SHORT).show();
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
        else
        {
            Toast.makeText(MainActivity.this, "Please check your Internet connection!!!", Toast.LENGTH_SHORT).show();
            return;
        }
    }
}
