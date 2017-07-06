package com.example.android.booklistingapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String BOOK_API_URL = "https://www.googleapis.com/books/v1/volumes?q=";
    private ArrayList<Book> bookArrayList = new ArrayList<>();
    private ProgressBar loadingProgressBar;
    private ListView listView;
    private EditText searchWordEditText;
    private Button searchBooksButton;
    private BookAdapter bookAdapter;
    private TextView noBooksTextView;
    private TextView noNetworkTextView;
    private TextView emptyTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchBooksButton = (Button) findViewById(R.id.search_books_button);

        searchWordEditText = (EditText) findViewById(R.id.search_word_editText);

        listView = (ListView) findViewById(R.id.list_view);

        loadingProgressBar = (ProgressBar) findViewById(R.id.loading);

        noBooksTextView = (TextView) findViewById(R.id.no_books_textView);

        noNetworkTextView = (TextView) findViewById(R.id.no_network_textView);

        emptyTextView = (TextView) findViewById(R.id.empty_textView);

        searchBooksButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                listView.setAdapter(null);
                loadingProgressBar.setVisibility(View.VISIBLE);
                emptyTextView.setVisibility(View.INVISIBLE);
                noBooksTextView.setVisibility(View.INVISIBLE);
                noNetworkTextView.setVisibility(View.INVISIBLE);

                String searchQuery = searchWordEditText.getText().toString().trim();

                if (checkConnection() == true) {

                    if (searchQuery == null || searchQuery.trim().length() == 0) {
                        loadingProgressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(MainActivity.this, "You Did not Type anything !", Toast.LENGTH_LONG).show();
                        emptyTextView.setVisibility(View.VISIBLE);
                        return;
                    } else {
                        BooksAsyncTask task = new BooksAsyncTask();
                        task.execute(searchQuery);
                    }

                } else {
                    loadingProgressBar.setVisibility(View.INVISIBLE);
                    noNetworkTextView.setVisibility(View.VISIBLE);
                    Toast.makeText(MainActivity.this, "No Connection Found !", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void updateUi() {

        bookAdapter = new BookAdapter(this, bookArrayList);
        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(bookAdapter);
        Toast.makeText(MainActivity.this, "Books Downloaded Successfully !", Toast.LENGTH_LONG).show();
    }

    private boolean checkConnection() {

        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        return isConnected;
    }


    public class BooksAsyncTask extends AsyncTask<String, Void, ArrayList<Book>> {

        @Override
        protected ArrayList<Book> doInBackground(String... params) {

            String searchQuery = params[0];
            String maxResult = "&maxResults=10";
            URL url = createUrl(BOOK_API_URL + searchQuery + maxResult);

            // Perform HTTP request to the URL and receive a JSON response back
            String jsonResponse = "";
            try {
                jsonResponse = makeHttpRequest(url);
            } catch (IOException e) {
                Log.e(LOG_TAG, "HTTP Request Error", e);
            }

            try {
                bookArrayList.clear();

                JSONObject baseJsonResponse = new JSONObject(jsonResponse);
                JSONArray itemsArray = baseJsonResponse.getJSONArray("items");

                for (int i = 0; i < itemsArray.length(); i++) {

                    JSONObject bookJsonObject = itemsArray.getJSONObject(i);

                    JSONObject volumeInfo = bookJsonObject.getJSONObject("volumeInfo");

                    JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");

                    String title;
                    if (volumeInfo.has("title")) {
                        title = volumeInfo.getString("title");
                    } else {
                        title = "Title N/A";
                    }

                    String author;
                    if (volumeInfo.has("authors")) {
                        author = volumeInfo.getString("authors");
                    } else {
                        author = "Author N/A";
                    }

                    String publisher;
                    if (volumeInfo.has("publisher")) {
                        publisher = volumeInfo.getString("publisher");
                    } else {
                        publisher = "Publisher N/A";
                    }

                    String image;
                    if (imageLinks.has("thumbnail")) {
                        image = imageLinks.getString("thumbnail");
                    } else {
                        image = "https://upload.wikimedia.org/wikipedia/en/6/62/Google_Play_Books_icon.png";
                    }

                    Book book = new Book(title, author, publisher, image);

                    bookArrayList.add(book);
                }
                return bookArrayList;

            } catch (JSONException e) {
                Log.e(LOG_TAG, "Error with parsing Books JSON results: ", e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Book> book) {

            if (book == null || bookArrayList.size() == 0) {
                Toast.makeText(MainActivity.this, "No Book Available !", Toast.LENGTH_LONG).show();
                noBooksTextView.setVisibility(View.VISIBLE);
                loadingProgressBar.setVisibility(View.INVISIBLE);
                return;
            } else {
                loadingProgressBar.setVisibility(View.INVISIBLE);
                bookArrayList = book;
                updateUi();
            }
        }

        private String makeHttpRequest(URL url) throws IOException {

            String jsonResponse = "";
            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;

            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setReadTimeout(10000 /* milliseconds */);
                urlConnection.setConnectTimeout(15000 /* milliseconds */);
                urlConnection.connect();

                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);

            } catch (IOException e) {
                Log.e(LOG_TAG, "HTTP URL Problem", e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            }
            return jsonResponse;
        }

        private String readFromStream(InputStream inputStream) throws IOException {

            StringBuilder output = new StringBuilder();

            if (inputStream != null) {

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);

                String line = reader.readLine();
                while (line != null) {
                    output.append(line);
                    line = reader.readLine();
                }
            }
            return output.toString();
        }

        private URL createUrl(String stringUrl) {

            URL url = null;

            try {
                url = new URL(stringUrl);
            } catch (MalformedURLException exception) {
                Log.e(LOG_TAG, "Error with creating URL", exception);
                return null;
            }
            return url;
        }
    }
}