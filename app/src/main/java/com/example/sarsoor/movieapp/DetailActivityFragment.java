package com.example.sarsoor.movieapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    static MovieData movieData;
    static GridView movie_review_List;
    static GridView trailer_view;
    static String BASE_URL;
    static TextView title;
    static ImageView movie_image;
    static TextView date;
    static TextView average;
    static Context context;
    static String[] keys;

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        title = (TextView) rootView.findViewById(R.id.movie_name_text);
        date = (TextView) rootView.findViewById(R.id.movie_date_text);
        average = (TextView) rootView.findViewById(R.id.movie_averge_text);
        movie_image = (ImageView) rootView.findViewById(R.id.movie_image);
        Button favorite = (Button) rootView.findViewById(R.id.favorite);
        context = getContext();
        movie_review_List = (GridView) rootView.findViewById(R.id.movie_review_List);
        trailer_view = (GridView) rootView.findViewById(R.id.trailer_view);
        trailer_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + keys[position])));
            }
        });
        if (!MainActivity.land) {
            display();
        }
        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favoritAction();
            }
        });
        return rootView;
    }

    public static void favoritAction(){
        DatabaseAdapter databaseAdapter = new DatabaseAdapter(context);
        if (databaseAdapter.getData(movieData.getId()) == null) {
            long insrt = databaseAdapter.insertData(movieData);
            if (insrt != -1) {
                Toast.makeText(context, "Favorite", Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(context, "Error In Insert Data", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Favorite Already", Toast.LENGTH_SHORT).show();
        }
    }

    public static void display() {
        if (title != null && movieData != null) {

            title.setText(movieData.getTitel());
            date.setText(movieData.getDate());
            average.setText(movieData.getVote());

            String baseUrl = "http://image.tmdb.org/t/p/w185";
            String poster_url = baseUrl + movieData.getPoster_url();
            Picasso.with(context).load(poster_url).into(movie_image);
            MovieReview task1 = new MovieReview();
            task1.execute(movieData.getId(), "/videos?");

        }
    }

    //--------------------------------------------------------------------------
    public static class MovieReview extends AsyncTask<String, Void, String[]> {


        @Override
        protected String[] doInBackground(String... params) { // params= Vedios + Review istead of create new ask task for each one

            Log.d("message", "message");
            BASE_URL = "http://api.themoviedb.org/3/movie/" + params[0] + params[1];
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String JsonStr = null;
            try {
                final String APPID_PARAM = "api_key";
                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
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
                Log.e("error", "Error ", e);
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
        protected void onPostExecute(String[] movieData) {
            getData(movieData);
        }

        private String[] getDataFromJson(String jsonStr) throws JSONException {
            final String M_LIST = "results";
            if (BASE_URL.contains("videos")) {
                final String key = "key";

                JSONObject movieJson = new JSONObject(jsonStr);
                JSONArray movieArray = movieJson.getJSONArray(M_LIST);

                String keys[] = new String[movieArray.length()];

                for (int i = 0; i < movieArray.length(); i++) {
                    JSONObject movie = movieArray.getJSONObject(i);
                    keys[i] = movie.getString(key);
                }

                return keys;
            } else {
                final String content = "content";
                JSONObject movieJson = new JSONObject(jsonStr);
                JSONArray movieArray = movieJson.getJSONArray(M_LIST);

                String contents[] = new String[movieArray.length()];

                for (int i = 0; i < movieArray.length(); i++) {
                    JSONObject movie = movieArray.getJSONObject(i);
                    contents[i] = movie.getString(content);
                }

                return contents;
            }
        }

        public void getData(final String[] keyss) {
            if (keyss != null) {
                keys = keyss;
                if (BASE_URL.contains("videos")) {
                    KeyAdapter keyAdapter = new KeyAdapter(context, keys);
                    trailer_view.setAdapter(keyAdapter);
                    MovieReview task2 = new MovieReview();

                    task2.execute(movieData.getId(), "/reviews?");
                } else {
                    ArrayAdapter<String> reviewAdapter = new ArrayAdapter<String>(
                            context,
                            android.R.layout.simple_list_item_1
                            ,keys
                    );
                    movie_review_List.setAdapter(reviewAdapter);
                }
            }
        }
    }
}



