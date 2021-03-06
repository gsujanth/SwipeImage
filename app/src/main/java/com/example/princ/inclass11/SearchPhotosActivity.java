package com.example.princ.inclass11;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/*
  Author : Sujanth Babu Guntupalli
*/

public class SearchPhotosActivity extends AppCompatActivity {

    OkHttpClient client = new OkHttpClient();
    private final String TAG = "demoSP";
    ArrayList<SearchImagesResponse.Hit> images = new ArrayList<>();
    ImagesPagerAdapter imagesPagerAdapter;

    EditText searchPhotosET;
    Button searchButton;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_photos);

        searchPhotosET = findViewById(R.id.searchPhotosET);
        searchButton = findViewById(R.id.searchButton);
        viewPager = findViewById(R.id.viewPager);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyword = searchPhotosET.getText().toString();
                if (!keyword.isEmpty()) {
                    searchImages(keyword);
                } else {
                    Toast.makeText(SearchPhotosActivity.this, "No keyword entered", Toast.LENGTH_SHORT).show();
                }

            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == images.size() - 1) {
                    Toast.makeText(SearchPhotosActivity.this, "No more Images", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onPageSelected(int position) {
                Toast.makeText(SearchPhotosActivity.this, "Image " + (position + 1), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.clearmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_clear:
                searchPhotosET.setText("");
                if (images != null && !images.isEmpty()) {
                    images.clear();
                    imagesPagerAdapter.notifyDataSetChanged();
                    viewPager.setAdapter(imagesPagerAdapter);
                } else {
                    Toast.makeText(this, "No Images to clear", Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void searchImages(String keyword) {
        Request request = new Request.Builder()
                .url("https://pixabay.com/api/?key=8642355-be17b0bc866caca641ac1cd44&q=" + keyword + "&image_type=photo").build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "searchImagesOnFailure: ");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String str = response.body().string();
                Log.d(TAG, "searchImagesOnResponse: ");
                if (response.isSuccessful()) {
                    Gson gson = new Gson();
                    final SearchImagesResponse searchImagesResponse = gson.fromJson(str, SearchImagesResponse.class);
                    images = searchImagesResponse.hits;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(SearchPhotosActivity.this, images.size() + " images loaded", Toast.LENGTH_SHORT).show();
                            imagesPagerAdapter = new ImagesPagerAdapter(getSupportFragmentManager(), images, SearchPhotosActivity.this);
                            viewPager.setAdapter(imagesPagerAdapter);
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(SearchPhotosActivity.this, "Bad Request", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });
    }

}