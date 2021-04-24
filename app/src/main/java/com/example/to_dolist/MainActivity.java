package com.example.to_dolist;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.to_dolist.UtilsService.SharedPreferenceClass;
import com.example.to_dolist.fragments.FinishedTaskFragment;
import com.example.to_dolist.fragments.HomeFragment;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    SharedPreferenceClass sharedPreferenceClass;
    private Toolbar toolbar;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private TextView userName, userEmail;
    private CircleImageView userImage;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        toolbar = findViewById(R.id.toolbar);

        sharedPreferenceClass = new SharedPreferenceClass(this);
        token = sharedPreferenceClass.getValue_String("token");

        View headerView = navigationView.getHeaderView(0);
        userName = headerView.findViewById(R.id.header_user_name);
        userEmail = headerView.findViewById(R.id.header_user_email);
        userImage = headerView.findViewById(R.id.header_user_avatar);


        setUpToolbar();
        initDrawerAndFragment();
        getUserProfile();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                setDrawerClick(item.getItemId());
                item.setCheckable(true);
                item.setChecked(true);
                drawerLayout.closeDrawers();
                return true;
            }
        });

    } //onCreate close.


    //----------------------------------------------------------------------------------------------------------------------//
    //----------------------------------------------------------------------------------------------------------------------//
    //----------------------------------------------------------------------------------------------------------------------//

    private void getUserProfile() {

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = "https://to-do-list-app-krishn.herokuapp.com/api/todo/auth";

        //Here we have replaced the object response and error listeners with their lambdas.

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, response -> {

            try {

                boolean success = response.getBoolean("success");

                if (success) {

                    JSONObject user = response.getJSONObject("user");

                    userName.setText(user.getString("username"));
                    userEmail.setText(user.getString("email"));
                    Picasso.get().load(user.getString("avatar")).placeholder(R.drawable.to_do_list_2).error(R.drawable.to_do_list_2).into(userImage);
                } else {
                    String errMsg = response.getString("msg");
                    Toast.makeText(this, errMsg, Toast.LENGTH_LONG).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> {


            Toast.makeText(this, "Volley Error Occurred", Toast.LENGTH_SHORT).show();

            //Here we write the logic of getting the error msg.(Code for double ensuring that error is reported).
            //We take out the response we get from the network, convert it into a String, and then finally into a JSON Object.
            //Then we pass the msg to a Toast.
            //Although it's a VolleyError, but we can show responses of failed registration etc. here as well.

            //---------------------------------------------------------------------------------------------------------------------------------------//

            NetworkResponse networkResponse = error.networkResponse;

            if (error instanceof ServerError && networkResponse != null) {

                try {

                    String res = new String(networkResponse.data, HttpHeaderParser.parseCharset(networkResponse.headers, "utf-8"));

                    JSONObject object = new JSONObject(res);
                    Toast.makeText(this, object.getString("msg"), Toast.LENGTH_LONG).show();

                } catch (JSONException | UnsupportedEncodingException je) {
                    je.printStackTrace();
                }

            }

            //---------------------------------------------------------------------------------------------------------------------------------------//

        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                HashMap<String, String> headers = new HashMap<>();

                headers.put("Content-Type", "application/json");
                headers.put("Authorization", token);
                return headers;
            }

        };

        //Volley also provides a RetryPolicy, helping in retrying for connecting to the server OR retrying in processing the request/response.

        int socketTime = 3000;
        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTime, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        jsonObjectRequest.setRetryPolicy(retryPolicy);


        //Adding the request to Volley, which then sends it to the server.

        requestQueue.add(jsonObjectRequest);
    }

    //---------------------------------------------------------------------------------------------------------------------------------------//
    //---------------------------------------------------------------------------------------------------------------------------------------//
    //---------------------------------------------------------------------------------------------------------------------------------------//


    //Setting up the toolbar properties.
    private void setUpToolbar() {
        setSupportActionBar(toolbar);
//        getSupportActionBar().setTitle("To Do List App");
//        getSupportActionBar().setHomeButtonEnabled(true);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initDrawerAndFragment() {

        //Setting up the initial fragment upon opening of the app.
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new HomeFragment()).commit();
        navigationView.setCheckedItem(R.id.menu_home);

        //Drawer and Toggle settings are also set.
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        actionBarDrawerToggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        actionBarDrawerToggle.syncState();
        //sync state syncs the state of the Drawer Toggle button even if we swipe access the drawer instead of clicking on the button.

        drawerLayout.addDrawerListener(actionBarDrawerToggle);

    }

    //Function made to manage menu item clicks on the side menu/nav menu items.
    private void setDrawerClick(int itemId) {
        switch (itemId) {
            case R.id.menu_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new HomeFragment()).commit();
                break;

            case R.id.menu_finished_task:
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new FinishedTaskFragment()).commit();
                break;

            case R.id.menu_logout:
                sharedPreferenceClass.clearData();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
        }
    }

    //----------------------------------------------------------------------------------------------------------------------//
    //----------------------------------------------Override Methods--------------------------------------------------------//

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_share:
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBodyMessage = "Hey! Try this To-Do List App that uses a real-time server and database to process and store your requests!!";

                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBodyMessage);
                startActivity(Intent.createChooser(sharingIntent, "Share via..."));
                return true;

            case R.id.menu_refresh:
                Fragment frag = getSupportFragmentManager().findFragmentById(R.id.content_frame);
//                Fragment home = new HomeFragment();
//                assert frag != null;

                if (frag instanceof HomeFragment) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new HomeFragment()).commit();
                } else {
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new FinishedTaskFragment()).commit();
                }

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }
}