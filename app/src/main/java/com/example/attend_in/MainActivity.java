package com.example.attend_in;

// Risks: 

// 1. Intrusive, user may not want app to get details at all times
// 2. Permissions, the app will need to ask for permissions and may ask for more information than it needs 
// 3. Security, app could be cracked to have location of user without consent
// 4. Integrity, bad actors could use app to perform actions for other identities unlawfully or with bad intent
// 5. Checking, user may not be actively at the correct location with the aid of a location changer or ip scrambler
// 6. Access, a user should not be given too much authority such as being able to check in multiple times in a short amount of time
// 7. Consistency, the user should not be able to check in and then leave the designated location until a certain amount of time has passed
// 8. Simplicity, the app should be simple enough for easy check in but also difficult enough to not be abused easily
// 9. Accuracy, a user should not be given access to check in unless they are confirmed to be within the required parameters
// 10. Accessibility, user should only get option to check in once they are confirmed to be at the correct location
// 11. Multi-use, the user should be able to check in with other devices however not be able to abuse this to exploit the system
// 12. Authentication, user must input required information unique to them for ability to check in using app
// 13. Constraints, each device should be given a cool down time after each use to ensure that the system is safe from exploitation

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

import static android.app.ProgressDialog.show;

public class MainActivity extends AppCompatActivity {
    Button btn_checkIn;
    EditText txt_studentID;
    EditText txt_StudentName;
    String lat = "";
    String longt = "";
    boolean inClass = false;
    //Set classroom location (It's currently set as the user's location for now, but we can change later)
    String classLat = "39.96657";
    String classLongt = "-74.90327";


    Timer timer;

    //Upon opening the app
    //Gets the student's geolocation

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Spinner creation
        Spinner spinner;;
        spinner = (Spinner) findViewById(R.id.classList);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String text = parent.getItemAtPosition(position).toString();
                Toast.makeText(parent.getContext(), text, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        //URL for API
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        final String API = "1f576b29567c4c9493df858dd9c285c5";
        final String URL_PREFIX = "https://api.ipgeolocation.io/ipgeo?apiKey=";
        String url = URL_PREFIX + API;

        //Get JSON Object
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                //Try Catch block for getting JSON object
                try {
                    lat = response.getString("latitude");
                    longt = response.getString("longitude");
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                btn_checkIn = findViewById(R.id.checkInButton);
                txt_studentID = (EditText) findViewById(R.id.studentID);
                txt_StudentName = (EditText) findViewById(R.id.StudentName);

                //Button click
                btn_checkIn.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        String studentID = txt_studentID.getText().toString();
                        String StudentName = txt_StudentName.getText().toString();
                        TextView time = (TextView) findViewById(R.id.time);

                        //set error if the student name is empty
                        if(StudentName == null || StudentName.isEmpty()) {
                            txt_StudentName.setError("Please enter your full name");
                            return;
                        }

                        //set error if the student ID is empty or shorter than 9 digits
                        if(studentID.equals("") || studentID.length()<9) {
                            txt_studentID.setError("Please enter your student ID");
                            return;
                        }

                        //Compare student location and classroom location
                        if((lat.substring(0,6).equals(classLat.substring(0,6))) && (longt.substring(0,6).equals(classLongt.substring(0,6)))) {

                            //Toast message to let the student know they're checked in
                            Toast.makeText(MainActivity.this, studentID + " You're checked In. Thanks!", Toast.LENGTH_SHORT).show();

                            //Disables check in button to prevent consecutive attempts
                            btn_checkIn.setEnabled(false);
                            timer = new Timer();
                            timer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    btn_checkIn.setEnabled(true);
                                    finish();
                                }
                            }, 5400000);


                            //Prints out time of check in the declared pattern
                            long date = System.currentTimeMillis();
                            SimpleDateFormat out = new SimpleDateFormat("hh:mm:ss a\nMMM dd yyyy");
                            String datestr = out.format(date);
                            time.setText(datestr);
                        }
                        //If the student is not within the classroom range
                        else{
                            Toast.makeText(MainActivity.this, "You're not within the classroom range.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        },
                //Error listener for JSON request
                new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "error", Toast.LENGTH_SHORT).show();
            }
        });

        //Add the request for geolocation on to the queue
        queue.add(request);


    }

}

