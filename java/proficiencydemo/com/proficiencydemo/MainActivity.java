package mydemo.com.mydemo;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import mydemo.com.mydemo.adater.SwipeListAdapter;
import mydemo.com.mydemo.app.AppController;
import mydemo.com.mydemo.model.Rows;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private String TAG = MainActivity.class.getSimpleName();

    private String JSON_URL = "https://dl.dropboxusercontent.com/s/2iodh4vg0eortkl/facts.json";

    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listView;
    private SwipeListAdapter adapter;
    private List<Rows> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.listView);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);

        dataList = new ArrayList<>();
        adapter = new SwipeListAdapter(this, dataList);
        listView.setAdapter(adapter);

        dataList.clear();
        swipeRefreshLayout.setOnRefreshListener(this);

        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);
                                        fetchData();
                                    }
                                }
        );

    }

    /**
     * This method is called when swipe refresh is pulled down
     */
    @Override
    public void onRefresh() {
        fetchData();
    }

    /**
     * Fetching movies json by making http call
     */
    private void fetchData() {

        // showing refresh animation before making http call
        swipeRefreshLayout.setRefreshing(true);

        // appending offset to url
        String url = JSON_URL;

        adapter.refreshList(dataList);

        // Volley's json array request object
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());

                        if (response.length() > 0) {

                            // looping through json and adding to movies list
                            //  for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject movieObj = response;
                                String title = movieObj.getString("title");
                                JSONArray jsonArray = movieObj.getJSONArray("rows");
                                // movieList = new ArrayList<>();
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    Rows rows = new Rows();
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    rows.setTitle(jsonObject.getString("title"));
                                    rows.setDescription(jsonObject.getString("description"));
                                    rows.setImageHref(jsonObject.getString("imageHref"));

                                    if (dataList != null) {
                                        dataList.add(rows);
                                    }
                                }
                            } catch (JSONException e) {
                                Log.e(TAG, "JSON Parsing error: " + e.getMessage());
                            }
                            // }
                            Log.d(TAG, "size=" + dataList.size());

                            adapter.notifyDataSetChanged();
                        }
                        // stopping swipe refresh
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e(TAG, "Server Error: " + volleyError.getMessage());

                String message = null;
                if (volleyError instanceof NetworkError) {
                    message = "Cannot connect to Internet...Please check your connection!";
                } else if (volleyError instanceof ServerError) {
                    message = "The server could not be found. Please try again after some time!!";
                } else if (volleyError instanceof AuthFailureError) {
                    message = "Cannot connect to Internet...Please check your connection!";
                } else if (volleyError instanceof ParseError) {
                    message = "Parsing error! Please try again after some time!!";
                } else if (volleyError instanceof NoConnectionError) {
                    message = "Cannot connect to Internet...Please check your connection!";
                } else if (volleyError instanceof TimeoutError) {
                    message = "Connection TimeOut! Please check your internet connection.";
                }

                // stopping swipe refresh
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(req);
    }

}
