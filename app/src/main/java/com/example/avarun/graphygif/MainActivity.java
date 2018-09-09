package com.example.avarun.graphygif;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.avarun.constants.MyConstants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements Pagination {
    RecyclerView recyclerView;
    SearchView searchView;

    Context mContext;


    ArrayList<DTO> dtoArrayList_main = new ArrayList<>();
    private String search_query;
    private int offset = 1;
    private GraphyAdapter graphyAdapter;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;
        getSupportActionBar().hide();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        searchView = (SearchView) findViewById(R.id.searchView);


        getSupportActionBar().setTitle("Graphy");
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                // your text view here
                //textView.setText(newText);
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                //textView.setText(query);

                if (!query.isEmpty()) {
                    search_query = query;
                    offset = 1;
                    getData();
                }

                return true;
            }
        });


        getData();
        MyConstants.MyJsonArray = new JSONArray();


    }

    public void getData() {

        new APICall(null, "http://api.giphy.com/v1/gifs/search?q=" + search_query + "&api_key=dc6zaTOxFJmzC&limit=20&offset=" + offset, mContext, new APICall.APIRespose() {
            @Override
            public void onResponse(String result) {

                Log.e(">>>>>>>", result);

                if (result.isEmpty()) {
                    //Utils.toast(mContext.getResources().getString(R.string.no_response_frm_srvr), mContext);
                } else {
                    try {
                        JSONObject resultObject = new JSONObject(result);
                        JSONArray jsonArray = resultObject.getJSONArray("data");

                        if (jsonArray != null && jsonArray.length() > 0) {

                            ArrayList<DTO> dtoArrayList = new ArrayList<>();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                String url = jsonObject.getJSONObject("images").getJSONObject("preview_gif").getString("url");
                                String username = jsonObject.getString("username");

                                DTO dto = new DTO();
                                dto.setUrl(url);
                                dto.setUsername(username);

                                dtoArrayList.add(dto);

                            }

                            if (dtoArrayList != null && dtoArrayList.size() > 0) {

                                if (offset == 1) {
                                    dtoArrayList_main = dtoArrayList;
                                    setAdapter();
                                } else {
                                    for (int i = 0; i < jsonArray.length(); i++)
                                        dtoArrayList_main.add(dtoArrayList.get(i));
                                    graphyAdapter.dtoArrayList = dtoArrayList_main;
                                    graphyAdapter.notifyDataSetChanged();
                                }
                            }
                        } else {
                            Toast.makeText(mContext, "NO RESULT FOUND", Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        }).execute();

    }

    private void setAdapter() {
        graphyAdapter = new GraphyAdapter(dtoArrayList_main, mContext, MainActivity.this);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager); // set LayoutManager to RecyclerView
        recyclerView.setAdapter(graphyAdapter);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void pagination() {
        offset++;
        getData();

    }
}
