package com.trickandroid.paginationrecyclerview.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.trickandroid.paginationrecyclerview.Adapters.RecyclerViewAdapter;
import com.trickandroid.paginationrecyclerview.Models.RecyclerViewModel;
import com.trickandroid.paginationrecyclerview.R;
import com.trickandroid.paginationrecyclerview.Utils.IsLoading;
import com.trickandroid.paginationrecyclerview.Utils.RequestHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private Context context = MainActivity.this;
    private Activity activity = MainActivity.this;

    //widgets
    private RecyclerView recyclerView;
    private ProgressDialog progressDialog;
    private ProgressBar isLoadingPB;
    final IsLoading isLoading = new IsLoading();

    //List
    private List<RecyclerViewModel> recyclerViewModelList = new ArrayList<>();

    //var
    private int pageNumber = 1;
    private int offset = 10;
    private boolean hasMore = false;
    private boolean firstTime = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCancelable(false);
        progressDialog.show();

        initializeWidgets();
    }

    private void initializeWidgets() {
        recyclerView = findViewById(R.id.recyclerView);
        isLoadingPB = findViewById(R.id.isLoadingPB);

        isLoading.setListener(new IsLoading.OnLoadingListener() {
            @Override
            public void onChange() {
                if (isLoading.isLoading()){
                    Log.d(TAG, "onChange: is loading");
                    ViewGroup.MarginLayoutParams marginLayoutParams =
                            (ViewGroup.MarginLayoutParams) recyclerView.getLayoutParams();
                    marginLayoutParams.setMargins(0, 0, 0, dpToPx(100));
                    recyclerView.setLayoutParams(marginLayoutParams);
                    isLoadingPB.setVisibility(View.VISIBLE);
                } else {
                    Log.d(TAG, "onChange: not loading");
                    ViewGroup.MarginLayoutParams marginLayoutParams =
                            (ViewGroup.MarginLayoutParams) recyclerView.getLayoutParams();
                    marginLayoutParams.setMargins(0, 0, 0, dpToPx(0));
                    isLoadingPB.setVisibility(View.GONE);
                    recyclerView.setLayoutParams(marginLayoutParams);
                }
            }
        });
        getList(pageNumber,offset);
    }

    private void getList(final int pageNumber, final int offset) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "http://192.168.1.107/pagination_recyclerview/app/v1/names-list.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (progressDialog.isShowing()) progressDialog.dismiss();
                        if(isLoading.isLoading()) isLoading.setLoading(false);
                        Log.d(TAG, "onResponse: response = " + response);

                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if (jsonObject.isNull("names")){
                                hasMore = false;
                            } else {
                                hasMore = true;
                                JSONArray jsonArray = jsonObject.getJSONArray("names");
                                for (int i=0;i<jsonArray.length();i++){
                                    JSONObject object = jsonArray.getJSONObject(i);

                                    String id = object.getString("id");
                                    String name = object.getString("name");
                                    String number = object.getString("number");

                                    RecyclerViewModel recyclerViewModel = new RecyclerViewModel(id,name,number);
                                    recyclerViewModelList.add(recyclerViewModel);
                                }
                            }
                            if(firstTime) setUpRecyclerView();
                        } catch (JSONException e) {
                            Log.e(TAG, "onResponse: json error",e );
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (progressDialog.isShowing()) progressDialog.dismiss();
                        if(isLoading.isLoading()) isLoading.setLoading(false);
                        Log.d(TAG, "onErrorResponse: Volley Error = " + error);
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("page_num",String.valueOf(pageNumber));
                params.put("offset",String.valueOf(offset));
                return params;
            }
        };
        RequestHandler.getInstance(context).addToRequestQueue(stringRequest);
    }

    private void setUpRecyclerView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(recyclerViewModelList,context);
        recyclerView.setAdapter(recyclerViewAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(1)){
                    Log.d(TAG, "onScrollStateChanged: last");
                    if (hasMore){
                        isLoading.setLoading(true);
                        pageNumber += 1;
                        getList(pageNumber,offset);
                    } else {
                        if(isLoading.isLoading()) isLoading.setLoading(false);
                    }
                }
            }
        });

        firstTime = false;
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

}
