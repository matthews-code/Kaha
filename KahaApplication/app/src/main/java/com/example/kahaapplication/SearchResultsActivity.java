package com.example.kahaapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

public class SearchResultsActivity extends ToolBarActivity {

    private TextView tvSearchKeyWord;
    private TextView tvNoResultsMessage;

    private RecyclerView rvResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

    }
    private void initComponents(){
        this.tvSearchKeyWord = findViewById(R.id.tv_search_keyword);
        this.tvNoResultsMessage = findViewById(R.id.tv_search_noResult);
        this.rvResults = findViewById(R.id.rv_search_results);
    }
}