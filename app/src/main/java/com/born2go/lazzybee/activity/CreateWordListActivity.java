package com.born2go.lazzybee.activity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.born2go.lazzybee.R;
import com.born2go.lazzybee.gdatabase.server.dataServiceApi.model.GroupVoca;
import com.born2go.lazzybee.gtools.LazzyBeeSingleton;

public class CreateWordListActivity extends AppCompatActivity {

    public static final String WORD_LIST = "word_list";
    public static final int REG_INPUT_WORD_LIST = 12345;
    public static final int UPDATE_1 = 1;
    private String wordList;
    private EditText txtwordList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_word_list);
        //
        if (getIntent().getExtras() != null) {
            wordList = getIntent().getExtras().getString(WORD_LIST);
        }

        txtwordList = (EditText) findViewById(R.id.txtWordList);
        if (wordList != null) {
            txtwordList.setText(wordList);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        // Inflate menu to add items to action bar if it is present.
        inflater.inflate(R.menu.menu_input_word_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_input) {

            inputNewWordList();


            return true;
        } else return super.onOptionsItemSelected(item);

    }

    private void inputNewWordList() {
        String wordList = txtwordList.getText().toString();
        if (wordList.trim().isEmpty()) {
            Toast.makeText(this, "Input new word list", Toast.LENGTH_SHORT).show();
            return;
        }

        //new GroupSSVoca
        GroupVoca groupVoca = new GroupVoca();
        groupVoca.setListVoca(wordList);

        //save word List
        saveWordList(groupVoca);


        //Save groupVoca to server
    }

    private void saveWordList(final GroupVoca groupVoca) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setTitle("Please waiting ...");
        dialog.setMessage("Loading...");
        dialog.show();

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                LazzyBeeSingleton.learnApiImplements.addToIncomingList(groupVoca);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                //Dismis dialog
                if ((dialog != null) && dialog.isShowing()) {
                    dialog.dismiss();
                }
                setResult(UPDATE_1);
                finish();
            }
        };
        task.execute();
    }
}
