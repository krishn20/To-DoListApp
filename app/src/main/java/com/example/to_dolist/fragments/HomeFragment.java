package com.example.to_dolist.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import com.example.to_dolist.LoginActivity;
import com.example.to_dolist.R;
import com.example.to_dolist.UtilsService.SharedPreferenceClass;
import com.example.to_dolist.adapters.TasksListAdapter;
import com.example.to_dolist.interfaces.RecyclerViewItemButtonsClickListeners;
import com.example.to_dolist.models.ToDoTaskModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment implements RecyclerViewItemButtonsClickListeners {

    //We are implementing an interface having methods for all the button clicks present in the Lower part of the List CardView.
    //We made the interface, implemented it here, thus we defined it's functions here as well as to what they will do, and then finally passed it as an object to our
    //TaskListAdapter. This is because this Adapter is actually going to use these interface methods there.

    FloatingActionButton floatingActionButton;
    SharedPreferenceClass sharedPreferenceClass;
    private String token;

    RecyclerView recyclerView;
    ProgressBar progressBar;
    RelativeLayout relativeLayoutNoTasks;

    ArrayList<ToDoTaskModel> taskModelsArrayList;
    TasksListAdapter tasksListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        floatingActionButton = view.findViewById(R.id.fab_add_tasks);
        sharedPreferenceClass = new SharedPreferenceClass(getContext());
        token = sharedPreferenceClass.getValue_String("token");

        floatingActionButton.setOnClickListener(view1 -> {
            showAlertDialog();
        });

        recyclerView = view.findViewById(R.id.recycler_view_task_list);
        progressBar = view.findViewById(R.id.progress_bar_task_list);
        relativeLayoutNoTasks = view.findViewById(R.id.relative_layout_no_tasks);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        getTasks();

        return view;

    } //onCreateView close.


    //----------------------------------------------------------------------------------------------------------------------//
    //----------------------------------------------------------------------------------------------------------------------//
    //------------------------------------------------Dialog Methods--------------------------------------------------------//

    public void showAlertDialog() {

        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.custom_dialog_layout, null, false);

        final EditText et_title = alertLayout.findViewById(R.id.et_task_title);
        final EditText et_description = alertLayout.findViewById(R.id.et_task_description);

        AlertDialog dialog = new MaterialAlertDialogBuilder(getActivity(), R.style.ThemeOverlay_MaterialComponents_Dark)
                .setView(alertLayout)
                .setTitle("Add Task")
                .setPositiveButton("Add", null)
                .setNegativeButton("Cancel", null)
                .create();


        //This is just an interface to get the dialog properties and buttons separately. We took the Positive Button separately so that we can add it's
        //functionality here.

        dialog.setOnShowListener(dialogInterface -> {

            Button positiveButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
            Button negativeButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);

            positiveButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_add_task, 0, 0, 0);
            negativeButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_cancel, 0, 0, 0);

            positiveButton.setOnClickListener(view -> {

                String title = et_title.getText().toString();
                String description = et_description.getText().toString();

                if(!TextUtils.isEmpty(title))
                {
                    addTask(title, description);
                    dialog.dismiss();
                }
                else
                {
                    Toast.makeText(getActivity(), "Please enter Title of your Task!", Toast.LENGTH_SHORT).show();
                }
            });

        });

        dialog.show();
        dialog.setCanceledOnTouchOutside(false);

    } //showAlertDialog close.

    //........................................................................................................................//

    private void showUpdateDialog(final String taskId, final String title, final String description)
    {

        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.custom_dialog_layout, null, false);

        final EditText et_title = alertLayout.findViewById(R.id.et_task_title);
        final EditText et_description = alertLayout.findViewById(R.id.et_task_description);

        et_title.setText(title);
        et_description.setText(description);

        AlertDialog dialog = new MaterialAlertDialogBuilder(getActivity(), R.style.ThemeOverlay_MaterialComponents_Dark)
                .setView(alertLayout)
                .setTitle("Update Task")
                .setPositiveButton("Update", null)
                .setNegativeButton("Cancel", null)
                .create();


        //This is just an interface to get the dialog properties and buttons separately. We took the Positive Button separately so that we can add it's
        //functionality here.

        dialog.setOnShowListener(dialogInterface -> {

            Button positiveButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
            Button negativeButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);

            positiveButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_add_task, 0, 0, 0);
            negativeButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_cancel, 0, 0, 0);

            positiveButton.setOnClickListener(view -> {

                String updatedTitle = et_title.getText().toString();
                String updatedDescription = et_description.getText().toString();

                if(!TextUtils.isEmpty(title))
                {
                    updateTask(taskId, updatedTitle, updatedDescription);
                    dialog.dismiss();
                }
                else
                {
                    Toast.makeText(getActivity(), "Please enter Title of your Task!", Toast.LENGTH_SHORT).show();
                }
            });

        });

        dialog.show();
        dialog.setCanceledOnTouchOutside(false);


    } //showUpdateDialog close.

    //........................................................................................................................//

    private void showDeleteDialog(final String taskId, final  int position)
    {
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setTitle("Are you sure you want to delete the task ?")
                .setPositiveButton("Yes", null)
                .setNegativeButton("No", null)
                .create();

        alertDialog.setOnShowListener((DialogInterface.OnShowListener) dialog ->
        {
            Button button = ((AlertDialog) alertDialog).getButton(AlertDialog.BUTTON_POSITIVE);

            button.setOnClickListener(v ->
            {
                deleteTask(taskId, position);
                alertDialog.dismiss();
            });
        });

        alertDialog.show();

    } //showDeleteDialog close.

    //........................................................................................................................//

    private void showFinishDialog(final String taskId, final  int position)
    {
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setTitle("Want to move the task to \"Finished Tasks\"?")
                .setPositiveButton("Yes", null)
                .setNegativeButton("No", null)
                .create();

        alertDialog.setOnShowListener((DialogInterface.OnShowListener) dialog ->
        {
            Button button = ((AlertDialog) alertDialog).getButton(AlertDialog.BUTTON_POSITIVE);

            button.setOnClickListener(v ->
            {
                finishTask(taskId, position);
                alertDialog.dismiss();
            });
        });

        alertDialog.show();
    } //showFinishDialog close.


    //........................................................................................................................//
    //........................................................................................................................//
    //....................................API Call Methods - GET, POST, PUT, DELETE...........................................//

    private void getTasks() {

        taskModelsArrayList = new ArrayList<>();
        progressBar.setVisibility(View.VISIBLE);

        String url = "https://to-do-list-app-krishn.herokuapp.com/api/todo";
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        //Here we have replaced the object response and error listeners with their lambdas.

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, response -> {

            try {

                boolean success = response.getBoolean("success");

                if (success) {

                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);

                    JSONArray taskJSONArray = response.getJSONArray("allToDos");

                    if(taskJSONArray.length() == 0)
                    {
                        recyclerView.setVisibility(View.GONE);
                        relativeLayoutNoTasks.setVisibility(View.VISIBLE);
                    }

                    for(int i=0; i<taskJSONArray.length(); i++)
                    {
                        JSONObject taskJSONObject = taskJSONArray.getJSONObject(i);

                        String userId = taskJSONObject.getString("user");
                        String taskId = taskJSONObject.getString("_id");
                        String title = taskJSONObject.getString("title");
                        String desc = taskJSONObject.getString("description");

                        ToDoTaskModel task = new ToDoTaskModel(userId, taskId, title, desc);
                        taskModelsArrayList.add(task);
                    }

                    //Because our HomeFragment implements RecyclerViewItemButtonsClickListeners interface, we can just directly send HomeFragment.this
                    //and it will directly send the required interface/interface object in the TaskListAdapter.

                    tasksListAdapter = new TasksListAdapter(getActivity(), taskModelsArrayList, HomeFragment.this);
                    recyclerView.setAdapter(tasksListAdapter);

                    String msg = response.getString("msg");
                    Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();

                }


                else {
                    progressBar.setVisibility(View.GONE);
                    relativeLayoutNoTasks.setVisibility(View.VISIBLE);

                    String errMsg = response.getString("msg");
                    Toast.makeText(getActivity(), errMsg, Toast.LENGTH_LONG).show();
                }

            }

            catch (JSONException e) {
                progressBar.setVisibility(View.GONE);
                relativeLayoutNoTasks.setVisibility(View.VISIBLE);
                e.printStackTrace();
            }

        }, error -> {

            Toast.makeText(getActivity(), "Volley Error Occurred", Toast.LENGTH_SHORT).show();

            //Here we write the logic of getting the error msg.(Code for double ensuring that error is reported).
            //We take out the response we get from the network, convert it into a String, and then finally into a JSON Object.
            //Then we pass the msg to a Toast.
            //Although it's a VolleyError, but we can show responses of failed registration etc. here as well.

            //---------------------------------------------------------------------------------------------------------------------------------------//

            NetworkResponse networkResponse = error.networkResponse;

            if(error == null || networkResponse == null)
            {
                return;
            }

            String body;
//          final String statusCode = String.valueOf(error.networkResponse.statusCode);

            try
            {

                body = new String(error.networkResponse.data, "UTF-8");
                JSONObject errorObject = new JSONObject(body);

                if(errorObject.getString("msg").equals("Token is invalid!"))
                {
                    sharedPreferenceClass.clearData();
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    Toast.makeText(getActivity(), "Your session has expired! Please login again to continue.", Toast.LENGTH_LONG).show();
                }

                Toast.makeText(getActivity(), errorObject.getString("msg"), Toast.LENGTH_LONG).show();

            } catch (UnsupportedEncodingException | JSONException e) {
                e.printStackTrace();
            }



//            if(error instanceof ServerError && networkResponse != null){
//
//                try {
//
//                    String res = new String(networkResponse.data, HttpHeaderParser.parseCharset(networkResponse.headers, "utf-8"));
//
//                    JSONObject object = new JSONObject(res);
//
//                    progressBar.setVisibility(View.GONE);
//                    relativeLayoutNoTasks.setVisibility(View.VISIBLE);
//                    Toast.makeText(getActivity(), object.getString("msg"), Toast.LENGTH_LONG).show();
//
//                } catch (JSONException | UnsupportedEncodingException je){
//                    progressBar.setVisibility(View.GONE);
//                    relativeLayoutNoTasks.setVisibility(View.VISIBLE);
//                    je.printStackTrace();
//                }
//
//            }

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

    } //getTasks close.

    //........................................................................................................................//

    private void addTask(String title, String description)
    {

        HashMap<String, String> body = new HashMap<>();
        body.put("title", title);
        body.put("description", description);

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        String url = "https://to-do-list-app-krishn.herokuapp.com/api/todo";


        //Here we have replaced the object response and error listeners with their lambdas.

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(body), response -> {

            try {

                boolean success = response.getBoolean("success");

                if (success) {
                    String msg = response.getString("msg");
                    Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
                    relativeLayoutNoTasks.setVisibility(View.GONE);
                    getTasks();
                }

                else {
                    String errMsg = response.getString("msg");
                    Toast.makeText(getActivity(), errMsg, Toast.LENGTH_LONG).show();
                }

            }

            catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> {

            Toast.makeText(getActivity(), "Volley Error Occurred", Toast.LENGTH_SHORT).show();

            //Here we write the logic of getting the error msg.(Code for double ensuring that error is reported).
            //We take out the response we get from the network, convert it into a String, and then finally into a JSON Object.
            //Then we pass the msg to a Toast.
            //Although it's a VolleyError, but we can show responses of failed registration etc. here as well.

            //---------------------------------------------------------------------------------------------------------------------------------------//

            NetworkResponse networkResponse = error.networkResponse;

            if(error instanceof ServerError && networkResponse != null){

                try {

                    String res = new String(networkResponse.data, HttpHeaderParser.parseCharset(networkResponse.headers, "utf-8"));

                    JSONObject object = new JSONObject(res);
                    Toast.makeText(getActivity(), object.getString("msg"), Toast.LENGTH_LONG).show();

                } catch (JSONException | UnsupportedEncodingException je){
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

    } //addTask close.

    //........................................................................................................................//

    private void updateTask(String taskId, String updatedTitle, String updatedDescription)
    {

        HashMap<String, String> body = new HashMap<>();
        body.put("title", updatedTitle);
        body.put("description", updatedDescription);

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        String url = "https://to-do-list-app-krishn.herokuapp.com/api/todo/"+taskId;

        //Here we have replaced the object response and error listeners with their lambdas.

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, new JSONObject(body), response -> {

            try {

                boolean success = response.getBoolean("success");

                if (success) {
                    String msg = response.getString("msg");
                    Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
                    relativeLayoutNoTasks.setVisibility(View.GONE);
                    getTasks();
                }

                else {
                    String errMsg = response.getString("msg");
                    Toast.makeText(getActivity(), errMsg, Toast.LENGTH_LONG).show();
                }

            }

            catch (JSONException e) {
                e.printStackTrace();
            }

            }, error -> {


            Toast.makeText(getActivity(), "Volley Error Occurred", Toast.LENGTH_SHORT).show();

            //Here we write the logic of getting the error msg.(Code for double ensuring that error is reported).
            //We take out the response we get from the network, convert it into a String, and then finally into a JSON Object.
            //Then we pass the msg to a Toast.
            //Although it's a VolleyError, but we can show responses of failed registration etc. here as well.

            //---------------------------------------------------------------------------------------------------------------------------------------//

            NetworkResponse networkResponse = error.networkResponse;

            if(error instanceof ServerError && networkResponse != null){

                try {

                    String res = new String(networkResponse.data, HttpHeaderParser.parseCharset(networkResponse.headers, "utf-8"));

                    JSONObject object = new JSONObject(res);
                    Toast.makeText(getActivity(), object.getString("msg"), Toast.LENGTH_LONG).show();

                } catch (JSONException | UnsupportedEncodingException je){
                    je.printStackTrace();
                }

            }

            //---------------------------------------------------------------------------------------------------------------------------------------//

        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                HashMap<String, String> headers = new HashMap<>();

                headers.put("Content-Type", "application/json");

                return headers;
            }

        };

        //Volley also provides a RetryPolicy, helping in retrying for connecting to the server OR retrying in processing the request/response.

        int socketTime = 3000;
        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTime, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        jsonObjectRequest.setRetryPolicy(retryPolicy);


        //Adding the request to Volley, which then sends it to the server.

        requestQueue.add(jsonObjectRequest);

    } //updateTask close.

    //........................................................................................................................//

    private void deleteTask(final String taskId, final int position) {

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        String url = "https://to-do-list-app-krishn.herokuapp.com/api/todo/" + taskId;

        //Here we have replaced the object response and error listeners with their lambdas.

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, url, null, response -> {

            try {

                boolean success = response.getBoolean("success");

                if (success) {
                    String msg = response.getString("msg");
                    Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();

                    taskModelsArrayList.remove(position);
                    tasksListAdapter.notifyItemRemoved(position);
                    getTasks();

                    relativeLayoutNoTasks.setVisibility(View.GONE);
                }

                else {
                    String errMsg = response.getString("msg");
                    Toast.makeText(getActivity(), errMsg, Toast.LENGTH_LONG).show();
                }

            }

            catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> {


            Toast.makeText(getActivity(), "Volley Error Occurred", Toast.LENGTH_SHORT).show();

            //Here we write the logic of getting the error msg.(Code for double ensuring that error is reported).
            //We take out the response we get from the network, convert it into a String, and then finally into a JSON Object.
            //Then we pass the msg to a Toast.
            //Although it's a VolleyError, but we can show responses of failed registration etc. here as well.

            //---------------------------------------------------------------------------------------------------------------------------------------//

            NetworkResponse networkResponse = error.networkResponse;

            if(error instanceof ServerError && networkResponse != null){

                try {

                    String res = new String(networkResponse.data, HttpHeaderParser.parseCharset(networkResponse.headers, "utf-8"));

                    JSONObject object = new JSONObject(res);
                    Toast.makeText(getActivity(), object.getString("msg"), Toast.LENGTH_LONG).show();

                } catch (JSONException | UnsupportedEncodingException je){
                    je.printStackTrace();
                }

            }

            //---------------------------------------------------------------------------------------------------------------------------------------//

        });

        //Volley also provides a RetryPolicy, helping in retrying for connecting to the server OR retrying in processing the request/response.

        int socketTime = 3000;
        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTime, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        jsonObjectRequest.setRetryPolicy(retryPolicy);


        //Adding the request to Volley, which then sends it to the server.

        requestQueue.add(jsonObjectRequest);

    } //deleteTask close.

    //........................................................................................................................//

    private void finishTask(String taskId, int position)
    {

        HashMap<String, Boolean> body = new HashMap<>();
        body.put("finished", true);

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        String url = "https://to-do-list-app-krishn.herokuapp.com/api/todo/"+taskId;

        //Here we have replaced the object response and error listeners with their lambdas.

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, new JSONObject(body), response -> {

            try {

                boolean success = response.getBoolean("success");

                if (success) {
                    String msg = "Task finished successfully!";
                    Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
                    relativeLayoutNoTasks.setVisibility(View.GONE);

                    taskModelsArrayList.remove(position);
                    tasksListAdapter.notifyItemRemoved(position);
                    getTasks();
                }

                else {
                    String errMsg = response.getString("msg");
                    Toast.makeText(getActivity(), errMsg, Toast.LENGTH_LONG).show();
                }

            }

            catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> {


            Toast.makeText(getActivity(), "Volley Error Occurred", Toast.LENGTH_SHORT).show();

            //Here we write the logic of getting the error msg.(Code for double ensuring that error is reported).
            //We take out the response we get from the network, convert it into a String, and then finally into a JSON Object.
            //Then we pass the msg to a Toast.
            //Although it's a VolleyError, but we can show responses of failed registration etc. here as well.

            //---------------------------------------------------------------------------------------------------------------------------------------//

            NetworkResponse networkResponse = error.networkResponse;

            if(error instanceof ServerError && networkResponse != null){

                try {

                    String res = new String(networkResponse.data, HttpHeaderParser.parseCharset(networkResponse.headers, "utf-8"));

                    JSONObject object = new JSONObject(res);
                    Toast.makeText(getActivity(), object.getString("msg"), Toast.LENGTH_LONG).show();

                } catch (JSONException | UnsupportedEncodingException je){
                    je.printStackTrace();
                }

            }

            //---------------------------------------------------------------------------------------------------------------------------------------//

        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                HashMap<String, String> headers = new HashMap<>();

                headers.put("Content-Type", "application/json");

                return headers;
            }

        };

        //Volley also provides a RetryPolicy, helping in retrying for connecting to the server OR retrying in processing the request/response.

        int socketTime = 3000;
        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTime, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        jsonObjectRequest.setRetryPolicy(retryPolicy);


        //Adding the request to Volley, which then sends it to the server.

        requestQueue.add(jsonObjectRequest);

    } //finishTask close.


    //........................................................................................................................//
    //........................................................................................................................//
    //.......................Defining the RecyclerViewItemButtonsClickListeners interface methods here........................//

    @Override
    public void onItemClick(int position) {
        Toast.makeText(getActivity(), "Item number:  "+ position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLongItemClick(int position) {
        showUpdateDialog(taskModelsArrayList.get(position).getTaskId(), taskModelsArrayList.get(position).getTitle(), taskModelsArrayList.get(position).getDescription());
    }

    @Override
    public void onEditBtnClick(int position) {
        showUpdateDialog(taskModelsArrayList.get(position).getTaskId(), taskModelsArrayList.get(position).getTitle(), taskModelsArrayList.get(position).getDescription());
    }

    @Override
    public void onDeleteBtnClick(int position) {
        showDeleteDialog(taskModelsArrayList.get(position).getTaskId(), position);
    }

    @Override
    public void onFinishBtnClick(int position) {
        showFinishDialog(taskModelsArrayList.get(position).getTaskId(), position);
    }
}