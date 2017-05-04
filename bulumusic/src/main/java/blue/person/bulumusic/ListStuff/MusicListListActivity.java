package blue.person.bulumusic.ListStuff;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import blue.person.bulumusic.MainActivityStuff.itemDecoration;
import blue.person.bulumusic.R;

public class MusicListListActivity extends AppCompatActivity {


    private static final int RESULT_CODE_SELECT = 22;
    private listAdapter mListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_list_list);
        Intent intent = getIntent();
        int mode = intent.getIntExtra("mode", listAdapter.MODE_SELECT);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list_list_recycler);
        mListAdapter = new listAdapter(this, mode, intent);
        mListAdapter.setOnClickListener(new listAdapter.onClickListener() {
            @Override
            public void run_select(String title) {
                Intent intent = new Intent();
                intent.putExtra("title", title);
                setResult(RESULT_CODE_SELECT, intent);
                MusicListListActivity.this.finish();
            }

            @Override
            public void run_add() {
                MusicListListActivity.this.finish();
            }
        });
        recyclerView.setAdapter(mListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new itemDecoration());//TODO:换成自己的，现在用的是MainActivity的。
    }


    private static final int menuItemID = 0;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, menuItemID, 1, "Add new");
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == menuItemID) {
            Toast.makeText(this, "clicked", Toast.LENGTH_SHORT).show();
            View view = View.inflate(this, R.layout.add_new_list_dailog, null);
            final EditText name = (EditText) view.findViewById(R.id.tv_list_name);
            new AlertDialog.Builder(this)
                    .setTitle("添加歌单")
                    .setView(view)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String listName = name.getText().toString();
                            mListAdapter.addNewList(listName);
                        }
                    }).show();
        }
        return super.onOptionsItemSelected(item);
    }
}
