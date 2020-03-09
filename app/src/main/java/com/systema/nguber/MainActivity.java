package com.systema.nguber;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.systema.nguber.Model.User;

import dmax.dialog.SpotsDialog;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {

    Button btnSignIn, btnRegister;
    RelativeLayout rootLayout;

    FirebaseAuth auth;
    FirebaseDatabase db;
    DatabaseReference users;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Before setContentView
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                                            .setDefaultFontPath("fonts/neosansstd.otf")
                                            .setFontAttrId(R.attr.fontPath)
                                            .build());
        setContentView(R.layout.activity_main);

        //Init Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        users = db.getReference("Users");

        //Init View
        btnRegister = findViewById(R.id.btnRegister);
        btnSignIn = findViewById(R.id.btnSignIn);
        rootLayout = findViewById(R.id.rootLayout);

        //Event
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRegisterDialog();
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSignInDialog();
            }
        });
    }

    private void showSignInDialog() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("SIGN IN");
        dialog.setMessage("Please use email to sign in");

        LayoutInflater inflater = LayoutInflater.from(this);
        View login_layout = inflater.inflate(R.layout.layout_login, null);

        final MaterialEditText edtEmail = login_layout.findViewById(R.id.edtEmail);
        final MaterialEditText edtPassword = login_layout.findViewById(R.id.edtPassword);

        dialog.setView(login_layout);

        //Set Button
        dialog.setPositiveButton("SIGN IN", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();

                        //CHANGE LOG 4
                        //Set disable button Sign In if is processing
                        btnSignIn.setEnabled(false);
                        //END CHANGE LOG 4

                        //Check Validation
                        if (TextUtils.isEmpty(edtEmail.getText().toString())) {
                            Snackbar.make(rootLayout, "Please enter email address", Snackbar.LENGTH_SHORT)
                                    .show();
                        }

                        if (TextUtils.isEmpty(edtPassword.getText().toString())) {
                            Snackbar.make(rootLayout, "Please enter password", Snackbar.LENGTH_SHORT)
                                    .show();
                        }

                        if (edtPassword.getText().toString().length() < 6) {
                            Snackbar.make(rootLayout, "Password too short!", Snackbar.LENGTH_SHORT)
                                    .show();
                        }

                        //CHANGE LOG 1
                        final android.app.AlertDialog waitingDialog = new SpotsDialog.Builder().setContext(MainActivity.this).build();
                        waitingDialog.show();
                        //END CHANGE LOG 1

                        //Login
                        auth.signInWithEmailAndPassword(edtEmail.getText().toString(),edtPassword.getText().toString())
                                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {
                                        //CHANGE LOG 2
                                        waitingDialog.dismiss();
                                        //END CHANGE LOG 2

                                        startActivity(new Intent(MainActivity.this, MapsActivity.class));
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //CHANGE LOG 3
                                waitingDialog.dismiss();
                                //END CHANGE LOG 3

                                Snackbar.make(rootLayout, "Failed"+e.getMessage(), Snackbar.LENGTH_SHORT)
                                        .show();

                                //CHANGE LOG 4
                                //Active Button
                                btnSignIn.setEnabled(true);
                                //END CHANGE LOG 4
                            }
                        });
                    }
                });

        dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        dialog.show();
    }

    private void showRegisterDialog() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("REGISTER");
        dialog.setMessage("Please use email to register");

        LayoutInflater inflater = LayoutInflater.from(this);
        View register_layout = inflater.inflate(R.layout.layout_register, null);

        final MaterialEditText edtEmail = register_layout.findViewById(R.id.edtEmail);
        final MaterialEditText edtPassword = register_layout.findViewById(R.id.edtPassword);
        final MaterialEditText edtName = register_layout.findViewById(R.id.edtName);
        final MaterialEditText edtPhone = register_layout.findViewById(R.id.edtPhone);

        dialog.setView(register_layout);

        //Set Button
        dialog.setPositiveButton("REGISTER", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

                //Check Validation
                if (TextUtils.isEmpty(edtEmail.getText().toString()))
                {
                    Snackbar.make(rootLayout, "Please enter email address", Snackbar.LENGTH_SHORT)
                            .show();
                }

                if (TextUtils.isEmpty(edtPhone.getText().toString()))
                {
                    Snackbar.make(rootLayout, "Please enter phone number", Snackbar.LENGTH_SHORT)
                            .show();
                }

                if (TextUtils.isEmpty(edtPassword.getText().toString()))
                {
                    Snackbar.make(rootLayout, "Please enter password", Snackbar.LENGTH_SHORT)
                            .show();
                }

                if (edtPassword.getText().toString().length() < 6)
                {
                    Snackbar.make(rootLayout, "Password too short!", Snackbar.LENGTH_SHORT)
                            .show();
                }

                //Register New User
                auth.createUserWithEmailAndPassword(edtEmail.getText().toString(), edtPassword.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                //Save user to database
                                User user = new User();
                                user.setEmail(edtEmail.getText().toString());
                                user.setName(edtName.getText().toString());
                                user.setPhone(edtPhone.getText().toString());
                                user.setPassword(edtPassword.getText().toString());

                                //Use email to key
                                users.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(user)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Snackbar.make(rootLayout, "Register succesfully", Snackbar.LENGTH_SHORT)
                                                        .show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Snackbar.make(rootLayout, "Failed"+e.getMessage(), Snackbar.LENGTH_SHORT)
                                                        .show();
                                            }
                                        });
                            }
                        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(rootLayout, "Failed"+e.getMessage(), Snackbar.LENGTH_SHORT)
                                .show();
                    }
                });
            }
        });

        dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        dialog.show();
    }
}
