package com.example.simpletodo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String KEY_ITEM_TEXT = "item_text";
    public static final String KEY_ITEM_POSITION = "item_position";
    public static final int EDIT_TEXT_CODE= 20;

    Button btnAdd;
    EditText edtText;
    RecyclerView rvItems;
    List<String> items;
    ItemAdapter itemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAdd = (Button) findViewById(R.id.btnAdd);
        edtText = (EditText) findViewById(R.id.edtItem);
        rvItems = (RecyclerView) findViewById(R.id.rvItems);


        loadItems();

        ItemAdapter.OnClickListener onClickListener = new ItemAdapter.OnClickListener() {
            @Override
            public void OnItemClicked(int position) {
                Intent intent= new Intent(MainActivity.this,EditActivity.class);
                intent.putExtra(KEY_ITEM_TEXT,items.get(position));
                intent.putExtra(KEY_ITEM_POSITION,position);
                startActivityForResult(intent,EDIT_TEXT_CODE);

            }
        };

        ItemAdapter.OnLongClickListener onLongClickListener = new ItemAdapter.OnLongClickListener() {
            @Override
            public void OnItemLongClicked(int position) {
                items.remove(position);
                itemAdapter.notifyItemRemoved(position);
                Toast.makeText(getApplicationContext(), "Item was removed", Toast.LENGTH_SHORT).show();
                saveItems();
            }
        };

        itemAdapter = new ItemAdapter(items, onLongClickListener,onClickListener);
        rvItems.setAdapter(itemAdapter);
        rvItems.setLayoutManager(new LinearLayoutManager(this));

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String todoItem = edtText.getText().toString();
                items.add(todoItem);
                itemAdapter.notifyItemInserted(items.size() - 1);
                edtText.setText("");
                Toast.makeText(getApplicationContext(), "Item was added", Toast.LENGTH_SHORT).show();
                saveItems();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == EDIT_TEXT_CODE) {
            String itemText = data.getStringExtra(KEY_ITEM_TEXT);
            int position = data.getExtras().getInt(KEY_ITEM_POSITION);
            items.set(position,itemText);
            itemAdapter.notifyItemChanged(position);
            Toast.makeText(getApplicationContext(), "Item updated successfully ", Toast.LENGTH_SHORT).show();
            saveItems();
        }
        else
        {
            Log.v("MainActivity","Unknown call to Activity Result");
        }


    }

    private File getDataFile()
       {
          return new File(getFilesDir(),"data.txt");
        }

        private void loadItems()
        {
            try {
                items = new ArrayList<>(FileUtils.readLines(getDataFile(), Charset.defaultCharset()));
            } catch (IOException e) {
                Log.e("MainActivity","Error while reading items",e);
                items = new ArrayList<>();

            }
        }

        // saves the item in the list
        private void saveItems(){
            try {
                FileUtils.writeLines(getDataFile(),items);
            } catch (IOException e) {
                Log.e("MainActivity","Error while writing items",e);
            }
        }


    }
