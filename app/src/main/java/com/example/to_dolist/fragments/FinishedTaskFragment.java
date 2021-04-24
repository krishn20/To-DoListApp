package com.example.to_dolist.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.example.to_dolist.adapters.FinishedTasksListAdapter;
import com.example.to_dolist.interfaces.RecyclerViewItemButtonsClickListeners;
import com.example.to_dolist.models.ToDoTaskModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FinishedTaskFragment extends Fragment implements RecyclerViewItemButtonsClickListeners {

    SharedPreferenceClass sharedPreferenceClass;
    private String token;

    RecyclerView finishedRecyclerView;
    ProgressBar finishedProgressBar;
    RelativeLayout relativeLayoutNoFinishedTasks;

    ArrayList<ToDoTaskModel> finishedTasksModelArrayList;
    FinishedTasksListAdapter finishedTasksListAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_finished_task, container, false);

        sharedPreferenceClass = new SharedPreferenceClass(getContext());
        token = sharedPreferenceClass.getValue_String("token");

        finishedRecyclerView = view.findViewById(R.id.recycler_view_finished_task_list);
        finishedProgressBar = view.findViewById(R.id.progress_bar_finished_task_list);
        relativeLayoutNoFinishedTasks = view.findViewById(R.id.relative_layout_no_finished_tasks);

        finishedRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        finishedRecyclerView.setHasFixedSize(true);

        getFinishedTasks();

        return view;
    }

    //........................................................................................................................//
    //........................................................................................................................//
    //........................................................................................................................//

    private void showFinishedDeleteDialog(final String finishedTaskId, final int position)
    {

        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setTitle("Are you sure you want to delete this finished task ?")
                .setPositiveButton("Yes", null)
                .setNegativeButton("No", null)
                .create();

        alertDialog.setOnShowListener(dialog ->
        {
            Button button = ((AlertDialog) alertDialog).getButton(AlertDialog.BUTTON_POSITIVE);

            button.setOnClickListener(v ->
            {
                deleteFinishedTask(finishedTaskId, position);
                alertDialog.dismiss();
            });
        });

        alertDialog.show();

    } //showFinishedDeleteDialog close.

    //........................................................................................................................//


    private void deleteFinishedTask(final String finishedTaskId, final int position)
    {

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        String url = "https://to-do-list-app-krishn.herokuapp.com/api/todo/" + finishedTaskId;

        //Here we have replaced the object response and error listeners with their lambdas.

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, url, null, response -> {

            try {

                boolean success = response.getBoolean("success");

                if (success) {
                    String msg = response.getString("msg");
                    Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();

                    finishedTasksModelArrayList.remove(position);
                    finishedTasksListAdapter.notifyItemRemoved(position);
                    getFinishedTasks();

                    relativeLayoutNoFinishedTasks.setVisibility(View.GONE);
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

    } //deleteFinishedTasks close.


    private void getFinishedTasks()
    {
        finishedTasksModelArrayList = new ArrayList<>();
        finishedProgressBar.setVisibility(View.VISIBLE);

        String url = "https://to-do-list-app-krishn.herokuapp.com/api/todo/finished";
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        //Here we have replaced the object response and error listeners with their lambdas.

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, response -> {

            try {

                boolean success = response.getBoolean("success");

                if (success) {

                    finishedProgressBar.setVisibility(View.GONE);
                    finishedRecyclerView.setVisibility(View.VISIBLE);

                    JSONArray taskJSONArray = response.getJSONArray("allToDos");

                    if (taskJSONArray.length() == 0) {
                        finishedRecyclerView.setVisibility(View.GONE);
                        relativeLayoutNoFinishedTasks.setVisibility(View.VISIBLE);
                    }

                    for (int i = 0; i < taskJSONArray.length(); i++) {
                        JSONObject taskJSONObject = taskJSONArray.getJSONObject(i);

                        String userId = taskJSONObject.getString("user");
                        String taskId = taskJSONObject.getString("_id");
                        String title = taskJSONObject.getString("title");
                        String desc = taskJSONObject.getString("description");

                        ToDoTaskModel task = new ToDoTaskModel(userId, taskId, title, desc);
                        finishedTasksModelArrayList.add(task);
                    }

                    //Because our HomeFragment implements RecyclerViewItemButtonsClickListeners interface, we can just directly send HomeFragment.this
                    //and it will directly send the required interface/interface object in the TaskListAdapter.

                    finishedTasksListAdapter = new FinishedTasksListAdapter(getActivity(), finishedTasksModelArrayList, FinishedTaskFragment.this);
                    finishedRecyclerView.setAdapter(finishedTasksListAdapter);

                    String msg = response.getString("msg");
                    Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();

                } else {
                    finishedProgressBar.setVisibility(View.GONE);
                    relativeLayoutNoFinishedTasks.setVisibility(View.VISIBLE);

                    String errMsg = response.getString("msg");
                    Toast.makeText(getActivity(), errMsg, Toast.LENGTH_LONG).show();
                }

            } catch (JSONException e) {
                finishedProgressBar.setVisibility(View.GONE);
                relativeLayoutNoFinishedTasks.setVisibility(View.VISIBLE);
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

            if (error == null || networkResponse == null) {
                return;
            }

            String body;
//          final String statusCode = String.valueOf(error.networkResponse.statusCode);

            try {

                body = new String(error.networkResponse.data, "UTF-8");
                JSONObject errorObject = new JSONObject(body);

                if (errorObject.getString("msg").equals("Token is invalid!")) {
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

    } //getFinishedTasks close.

    //........................................................................................................................//
    //........................................................................................................................//
    //.......................Defining the RecyclerViewItemButtonsClickListeners interface methods here........................//

    @Override
    public void onItemClick(int position) {
        //TODO: Nothing
    }

    @Override
    public void onLongItemClick(int position) {
        //TODO: Nothing
    }

    @Override
    public void onEditBtnClick(int position) {
        //TODO: Nothing
    }

    @Override
    public void onDeleteBtnClick(int position) {
        Toast.makeText(getActivity(),"Position: " + finishedTasksModelArrayList.get(position), Toast.LENGTH_SHORT).show();
        showFinishedDeleteDialog(finishedTasksModelArrayList.get(position).getTaskId(), position);
    }

    @Override
    public void onFinishBtnClick(int position) {
        //TODO: Nothing
    }
}