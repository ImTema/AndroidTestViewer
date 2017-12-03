package com.example.tema.testviewer;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.SortedMap;


public class ResultActivity extends ActionBarActivity {
    private static final String GRADES = "com.example.tema.GRADES";
    private static final String GRADESYSTEMNAME = "com.example.tema.GRADESYSTEMNAME";

    private String descriptionText;
    private HashMap<Integer, Grade> grades;
    private int result;
    private String name;
    private String TAG = "MyLog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle("Результат");
        setContentView(R.layout.activity_result);
        Button againBtn = (Button) findViewById(R.id.againBtn);
        TextView resultView = (TextView) findViewById(R.id.result);
        TextView gradeNameView = (TextView) findViewById(R.id.gradeNameView);
        TextView explanationView = (TextView) findViewById(R.id.explanationView);
        Intent intent = getIntent();
        result = intent.getIntExtra("result", 100500);
        int max_result = intent.getIntExtra("max_result", 100500);
        String gradeName = intent.getStringExtra(GRADESYSTEMNAME);
        double avg = (result * 100) / max_result;
        resultView.setText("Вы набрали " + avg + "% (" + result + "/" + max_result + ")");
        resultView.setGravity(Gravity.CENTER);
        /*What if default*/
        try {
            grades = (HashMap<Integer, Grade>) intent.getSerializableExtra(GRADES);
            if (!gradeName.equals("default")) {
                initializeGrade(avg);
                gradeNameView.setText(name);
                gradeNameView.setGravity(Gravity.CENTER);
                explanationView.setText(descriptionText);
                explanationView.setGravity(Gravity.CENTER);
            } else {
                throw new Exception();
            }
        } catch (Exception e) {
            gradeNameView.setVisibility(View.GONE);
            explanationView.setVisibility(View.GONE);
            e.printStackTrace();
        }
        againBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(ResultActivity.this, MainActivity.class);
                startActivity(in);
            }
        });
    }

    @Override
    public void onBackPressed() {
        //do nothing
    }

    private void initializeGrade(double avg) {
        HashMap<Integer, Grade> newGrades = new HashMap<>();
        for (Grade g : grades.values()) {
            newGrades.put(g.getNumberOfPoints(), g);
        }
        ArrayList<Integer> sortedKey = new ArrayList<>(newGrades.keySet());
        Collections.sort(sortedKey);
        sortedKey.add(101);
        int previous = 0;
        for (Integer i : sortedKey) {
            if (avg >= i) {
                previous = i;
                continue;
            }

            break;
        }

        descriptionText = newGrades.get(previous).getDiscription();
        name = newGrades.get(previous).getName();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_result, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
