package com.example.ross.sqlconnectiontest;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class MainActivity extends AppCompatActivity {

    DBConnection dbConnection;
    EditText username, password;
    Button btnLogin;
    ProgressBar progBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbConnection = new DBConnection();;//the class file
        username = (EditText) findViewById(R.id.userName);
        password = (EditText) findViewById(R.id.passWord);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        progBar = (ProgressBar) findViewById(R.id.progBar);
        progBar.setVisibility(View.GONE);

        btnLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                Login login = new Login();
                login.execute("");
            }
        });
    }

    public class Login extends AsyncTask<String,String,String>
    {
        String z = "";
        Boolean isSuccess = false;

        String user = username.getText().toString();
        String pass = password.getText().toString();

        @Override
        protected void onPreExecute()
        {
            progBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String r)
        {
            progBar.setVisibility(View.GONE);
            Toast.makeText(MainActivity.this, r , Toast.LENGTH_SHORT).show();

            if (isSuccess)
            {
                Intent i = new Intent(MainActivity.this, BeaconScanner.class);
                startActivity(i);
                finish();
            }
        }

        @Override
        protected String doInBackground(String... params)
        {
            if(user.trim().equals("") || pass.trim().equals(""))
            {
                z = "Please enter your username and password";
            }
            else
            {
                try
                {
                    Connection con = dbConnection.CONN();
                    if (con == null)
                    {
                        z = "Error in connection with SQL server";
                    }
                    else
                    {
                        String query = "select * from User where UserID ='" + user + "' and password='" + pass + "'";
                        Statement statement = con.createStatement();
                        ResultSet rs = statement.executeQuery(query);

                        if (rs.next())
                        {
                            z = "Login successful!";
                            isSuccess = true;
                        }
                        else
                        {
                            z = "Invalid credentials";
                            isSuccess = false;
                        }
                    }
                }
                catch (Exception ex)
                {
                    isSuccess = false;
                    z = "Exceptions";
                }
            }
            return z;
        }
    }
}
