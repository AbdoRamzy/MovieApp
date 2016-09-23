package com.example.sarsoor.movieapp;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    GridView gridView;
    MovieData[] movieData;
    MovieAdapter movieAdapter;
    static boolean land = false;
    static String state  ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        gridView = (GridView) findViewById(R.id.grid_view);
//
//        MovieTask task = new MovieTask();
//
//        if (isNetworkAvailable()) {
//
//            if(state.equals("top_rated"))
//            { task.execute("top_rated?"); }
//
//            else if(state.equals("Favorite"))
//            {
//                DatabaseAdapter databaseAdapter = new DatabaseAdapter(getBaseContext());
//                movieData = databaseAdapter.getAllData();
//                movieAdapter = new MovieAdapter(getBaseContext(), movieData);
//                gridView.setAdapter(movieAdapter);
//                state="Favorite";
//            }else
//                task.execute("popular?");
//        } else {
//             Intent intent = new Intent(MainActivity.this,noInternet.class);
//             startActivity(intent);
//             finish();
//            //setContentView(R.layout.activity_no_internet);
//            Toast.makeText(MainActivity.this, "Error No Internet Connected", Toast.LENGTH_SHORT).show();
//        }


        /// land & Tab
        FragmentManager fm = getFragmentManager();
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screenSize = getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;
        if (size.x > size.y || screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE || screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE) {
            View viewGroup = (View) findViewById(R.id.fragment2);
            if (DetailActivityFragment.movieData == null) {
                viewGroup.setVisibility(View.INVISIBLE);
            }
            else {
                DetailActivityFragment.display();
                viewGroup.setVisibility(View.VISIBLE);
            }
            land = true;
        } else land = false;


    }

    @Override
    protected void onStart() {
        MovieTask task = new MovieTask();

        if (isNetworkAvailable()) {

            if(state.equals("top_rated"))
            { task.execute("top_rated?"); }

            else if(state.equals("Favorite"))
            {
                DatabaseAdapter databaseAdapter = new DatabaseAdapter(getBaseContext());
                movieData = databaseAdapter.getAllData();
                movieAdapter = new MovieAdapter(getBaseContext(), movieData);
                gridView.setAdapter(movieAdapter);
                state="Favorite";
            }else
                task.execute("popular?");
        } else {
            Intent intent = new Intent(MainActivity.this,noInternet.class);
            startActivity(intent);
            finish();
            //setContentView(R.layout.activity_no_internet);
            Toast.makeText(MainActivity.this, "Error No Internet Connected", Toast.LENGTH_SHORT).show();
        }
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.popular) {
            MovieTask task = new MovieTask();
            task.execute("popular?");
            state="";

        } else if (id == R.id.top_rated) {
            MovieTask task = new MovieTask();
            task.execute("top_rated?");
            state="top_rated";

        } else if (id == R.id.favorite) {
            Toast.makeText(getBaseContext(), "Favorite", Toast.LENGTH_LONG).show();
            DatabaseAdapter databaseAdapter = new DatabaseAdapter(getBaseContext());
            movieData = databaseAdapter.getAllData();
            movieAdapter = new MovieAdapter(getBaseContext(), movieData);
            gridView.setAdapter(movieAdapter);
            state="Favorite";
        }
        return super.onOptionsItemSelected(item);
    }

    //--------------------------------------------------------------------------------------------------
//------------------------- AsyncTask  -------------------------------------------------------------
    public class MovieTask extends AsyncTask<String, Void, MovieData[]> {

        @Override
        protected MovieData[] doInBackground(String... params) { // params= popular + top_rated istead of create new ask task for each one

            Log.d("message", "message");
            String FORECAST_BASE_URL =
                    "https://api.themoviedb.org/3/movie/" + params[0];
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String JsonStr = null;
            try {
                final String APPID_PARAM = "api_key";
                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(APPID_PARAM, "c676636aa2f165af94c2ee0417b786eb")
                        .build();
                URL url = new URL(builtUri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    Log.d("message", "InputStream");
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                if (buffer.length() == 0) {
                    Log.d("message", "buffer");
                    return null;
                }
                JsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e("error", "Error " + e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("error", "Error closing stream", e);
                    }
                }
            }
            try {
                Log.d("message", "data");


                return getDataFromJson(JsonStr);


            } catch (JSONException e) {
                Log.e("error", e.getMessage(), e);
                e.printStackTrace();
            }
            // This will only happen if there was an error getting or parsing the forecast.
            Log.d("message", "null");
            return null;
        }

        @Override
        protected void onPostExecute(MovieData[] movieData) {
            getData(movieData);
        }

        private MovieData[] getDataFromJson(String jsonStr) throws JSONException {
            final String M_LIST = "results";
            final String poster_path = "poster_path";
            final String release_date = "release_date";
            final String vote_average = "vote_average";
            final String overview = "overview";
            final String original_title = "original_title";
            final String id = "id";
            JSONObject movieJson = new JSONObject(jsonStr);
            JSONArray movieArray = movieJson.getJSONArray(M_LIST);

            movieData = new MovieData[movieArray.length()];
            for (int i = 0; i < movieArray.length(); i++) {
                movieData[i] = new MovieData();
                JSONObject movie = movieArray.getJSONObject(i);
                String poster = movie.getString(poster_path);
                String release = movie.getString(release_date);
                String vote = movie.getString(vote_average);
                String overvie = movie.getString(overview);
                String title = movie.getString(original_title);
                String _id = movie.getString(id);

                movieData[i].setPoster_url(poster);
                movieData[i].setDate(release);
                movieData[i].setVote(vote);
                movieData[i].setTitel(title);
                movieData[i].setOverview(overvie);
                movieData[i].setId(_id);
            }


            return movieData;


        }

        public void getData(final MovieData[] moviesData) {
            if (moviesData != null) {
                movieData = moviesData;
                movieAdapter = new MovieAdapter(getBaseContext(), moviesData);
                gridView.setAdapter(movieAdapter);
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (!land) {
                            Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                            startActivity(intent);
                            DetailActivityFragment.movieData = moviesData[position];
                            DetailActivityFragment.display();
                        } else {
                            View viewGroup = (View) findViewById(R.id.fragment2);
                            DetailActivityFragment.movieData = moviesData[position];
                            DetailActivityFragment.display();
                            viewGroup.setVisibility(View.VISIBLE);

                        }
                    }
                });
            } else
                Log.d("message", "No Data");
        }
    }

    //-------------------------------------------------
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getBaseContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
