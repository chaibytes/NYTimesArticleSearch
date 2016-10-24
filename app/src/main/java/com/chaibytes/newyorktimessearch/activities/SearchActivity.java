package com.chaibytes.newyorktimessearch.activities;

import android.content.Intent;
import android.os.Bundle;
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

import com.chaibytes.newyorktimessearch.ArticleArrayAdapter;
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

    public SearchActivity() {
        settings = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupViews();
    }

    public void setupViews() {
        etQuery = (EditText) findViewById(R.id.etQuery);
        gvResults = (GridView) findViewById(R.id.gvResults);
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

        // Attach the listener to the AdapterView oncreate
        gvResults.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                loadNextDataFromApi(page);
                return true;
            }
        });

        if (settings == null) {
            settings = new Settings(Calendar.getInstance(), "Oldest", false, false, false);
        }
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
        adapter.clear();
        adapter.notifyDataSetChanged();
        onArticlesFetch(0);
    }
    public void onArticlesFetch(int page) {
        String query = etQuery.getText().toString();
        String url = "http://api.nytimes.com/svc/search/v2/articlesearch.json";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("api-key", "cc3a009560154c1684fe4cfe77344d18");
        params.put("page", page);
        if (!TextUtils.isEmpty(query)) {
            params.put("q", query);
        }
        params.put("begin_date", getDateString(settings.getCalendar()));
        params.put("sort", settings.getOption().toLowerCase());
        String newsQuery = createFlattenedList();
        if (!TextUtils.isEmpty(newsQuery)) {
            params.put("fq", "news_desk:(" + newsQuery +")");
        }
        Log.d("DEBUG", params.toString());
        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                JSONArray articleJSONResults = null;

                try {
                    articleJSONResults = response.getJSONObject("response").getJSONArray("docs");
                    articles.addAll(Article.fromJSONArray(articleJSONResults));
                    adapter.notifyDataSetChanged();
//                    adapter.clear();
//                    adapter.addAll(Article.fromJSONArray(articleJSONResults));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.d("Error", errorResponse.toString());
            }
        });
    }

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
        adapter.clear();
    }

    private String getDateString(Calendar cal) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        return format.format(cal.getTime());
    }

}
