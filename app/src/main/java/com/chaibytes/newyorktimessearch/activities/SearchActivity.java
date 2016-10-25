package com.chaibytes.newyorktimessearch.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

import com.chaibytes.newyorktimessearch.adapter.ArticleArrayAdapter;
import com.chaibytes.newyorktimessearch.R;
import com.chaibytes.newyorktimessearch.model.Article;
import com.chaibytes.newyorktimessearch.model.Settings;
import com.chaibytes.newyorktimessearch.utils.EndlessScrollListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity {

    private final int REQUEST_CODE = 20;

    EditText etQuery;
    GridView gvResults;
    Button btnSearch;

    Settings settings;
    ArrayList<Article> articles;
    ArticleArrayAdapter adapter;

    Handler handler = new Handler();
    EndlessScrollListener scrollListener;

    public static final int CAPACITY = 30;

    public SearchActivity() {
        settings = new Settings();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        gvResults = (GridView) findViewById(R.id.gvResults);
        // Attach the listener to the AdapterView oncreate
        scrollListener = new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                Log.d("DEBUG", String.valueOf(page));
                loadNextDataFromApi(page);
                return true;
            }
        };
        gvResults.setOnScrollListener(scrollListener);
        setupViews();
        // Fire the basic search query initially.
        onArticlesFetch(0);
    }

    public void setupViews() {
        etQuery = (EditText) findViewById(R.id.etQuery);
        btnSearch = (Button) findViewById(R.id.btnSearch);
        articles = new ArrayList<>();
        adapter = new ArticleArrayAdapter(this, articles);
        gvResults.setAdapter(adapter);

        // hook up listener for grid click
        gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // create an intent to display the article
                Intent i = new Intent(getApplicationContext(), ArticleActivity.class);
                // get the article to display
                Article article = articles.get(position);
                // pass in that article into the intent
                i.putExtra("article", article);
                // launch the activity
                startActivity(i);

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_filter, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.miFilter) {
            // Handle presses on the action bar items
            applyFilter();
        }
        return true;
    }


    private void loadNextDataFromApi(int page) {
        onArticlesFetch(page);
    }

    public void onArticleSearch(View view) {
        articles.clear();
        adapter.notifyDataSetChanged();
        onArticlesFetch(0);
    }

    public void onArticlesFetch(final int page) {
        String query = etQuery.getText().toString();
        String url = "http://api.nytimes.com/svc/search/v2/articlesearch.json";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("api-key", "cc3a009560154c1684fe4cfe77344d18");
        params.put("page", page);
        if (!TextUtils.isEmpty(query)) {
            params.put("q", query);
        }
        if (settings != null && settings.getCalendar() != null) {
            params.put("begin_date", getDateString(settings.getCalendar()));
        }
        if (settings != null && settings.getOption() != null) {
            params.put("sort", settings.getOption().toLowerCase());
        }

        String newsQuery = createFlattenedList();
        if (!TextUtils.isEmpty(newsQuery)) {
            params.put("fq", "news_desk:(" + newsQuery +")");
        }
        Log.d("DEBUG", params.toString());
        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray articleJSONResults = null;

                try {
                    articleJSONResults = response.getJSONObject("response").getJSONArray("docs");
                    if (articleJSONResults.length() == 0) {
                        scrollListener.limitReached();
                        Log.d("DEBUG", "Limit Reached");
                    } else {
                        articles.addAll(Article.fromJSONArray(articleJSONResults));
                        adapter.notifyDataSetChanged();
                        scrollListener.onLoadFinish(page, adapter.getCount());

                        if (adapter.getCount() <= CAPACITY) {
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    onArticlesFetch(page + 1);
                                }
                            }, 2500L);

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                // This gives a failure on loading
                scrollListener.onFailure();
                Log.d("Error", errorResponse.toString());
            }
        });
    }

    @NonNull
    private String createFlattenedList() {
        String list = "";
        if (settings.isArtChecked()) {
            list += "\"Arts\"" + " ";
        }
        if (settings.isFashionChecked()) {
            list += "\"Fashion & Style\"" + " ";
        }
        if (settings.isSportsChecked()) {
            list += "\"Sports\"";
        }
        return list.trim();
    }

    public void applyFilter() {
        // Display a settings screen
        Intent i = new Intent(getApplicationContext(), SettingsActivity.class);
        i.putExtra("settings", settings);
        startActivityForResult(i, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            settings = data.getParcelableExtra("settings");
        }
    }

    private String getDateString(Calendar cal) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        return format.format(cal.getTime());
    }

}
