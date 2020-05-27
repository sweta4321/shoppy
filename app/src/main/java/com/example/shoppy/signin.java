package com.example.shoppy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shoppy.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.widget.CheckBox;

import io.paperdb.Paper;

public class signin extends AppCompatActivity {


    EditText edtPhone,edtPassword;
    Button btnSignIn;
    CheckBox ckbRemember;
    TextView txtForgotPwd;

    FirebaseDatabase database;
   DatabaseReference table_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        edtPassword=(MaterialEditText)findViewById(R.id.edtPassword);
        edtPhone=(MaterialEditText)findViewById(R.id.edtPhone);
        btnSignIn=(Button)findViewById(R.id.btnSignIn);
        ckbRemember=(CheckBox)findViewById(R.id.ckbRemember);
        txtForgotPwd=(TextView)findViewById(R.id.txtForgotPwd);


        //init Paper
        Paper.init(this);

        //Initialize FIREBASE
        database=FirebaseDatabase.getInstance();
       table_user=database.getReference("User");//"User" is table name in database


        txtForgotPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showForgotPwdDialog();
            }
        });


        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (common.isConnectedToInternet(getBaseContext())) {

                    //Save username & password for onetime signin
                    if(ckbRemember.isChecked())
                    {
                        Paper.book().write(common.USER_KEY,edtPhone.getText().toString());
                        Paper.book().write(common.PWD_KEY,edtPassword.getText().toString());

                    }

                    //waiting dialogbox after signin
                    final ProgressDialog mDialog = new ProgressDialog(signin.this);
                    mDialog.setMessage("waiting...");
                    mDialog.show();

                    table_user.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            //check if user exists or not
                            if (dataSnapshot.child(edtPhone.getText().toString()).exists()) {
                                //Get User Information
                                mDialog.dismiss();
                                User user = dataSnapshot.child(edtPhone.getText().toString()).getValue(User.class);
                                user.setPhone(edtPhone.getText().toString()); //set Phone.

                                if (user.getPassword().equals(edtPassword.getText().toString())) {
                                    {
                                        Intent homeIntent = new Intent(signin.this, Home.class);
                                        common.currentUser = user;
                                        startActivity(homeIntent);
                                        finish();
                                    }
                                } else {
                                    Toast.makeText(signin.this, "Sign in failed", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                mDialog.dismiss();
                                Toast.makeText(signin.this, "User not exist", Toast.LENGTH_SHORT).show();
                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
                else
                {
                    Toast.makeText(signin.this, "Please check your Internet connection!!!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

        });


    }

    private void showForgotPwdDialog() {

        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Forgot Password");
        builder.setMessage("Enter your secure code");

        LayoutInflater inflater=this.getLayoutInflater();
        View forgot_view=inflater.inflate(R.layout.forgot_passsword_layout,null);

        builder.setView(forgot_view);
        builder.setIcon(R.drawable.ic_security_black_24dp);

        final MaterialEditText edtPhone=(MaterialEditText)forgot_view.findViewById(R.id.edtPhone);
        final MaterialEditText edtSecureCode=(MaterialEditText)forgot_view.findViewById(R.id.edtSecureCode);

        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //check if user available
                table_user.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user=dataSnapshot.child(edtPhone.getText().toString()).getValue(User.class);
                        if(user.getSecureCode().equals(edtSecureCode.getText().toString()))
                            Toast.makeText(signin.this, "Your password : "+user.getPassword(), Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(signin.this, "Wrong secure code!!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();



    }
}
