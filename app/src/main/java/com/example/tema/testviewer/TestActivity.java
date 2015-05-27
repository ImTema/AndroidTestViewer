package com.example.tema.testviewer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
//import ;


public class TestActivity extends ActionBarActivity {
    private static final String GRADES = "com.example.tema.GRADES";
    private static final String GRADESYSTEMNAME = "com.example.tema.GRADESYSTEMNAME";
    private Iterator it;
    private static final String QUESTIONS = "com.example.tema.QUESTIONS";
    private static final String SETTINGS = "com.example.tema.SETTINGS";
    private static final String PATH_TO_FILE = "com.example.tema.PATH_TO_FILE";
    private Map<Integer, Question> questions = new HashMap<>();
    private Settings sets = new Settings();
    private static final String TAG = "MyLog";
    private LinearLayout main_l;
    private int wrapContent = LinearLayout.LayoutParams.WRAP_CONTENT;
    private int fillParent = LinearLayout.LayoutParams.FILL_PARENT;
    private int result;
    private String path;
    private Answer currentAnswer;
    private ArrayList<String> answersToShow;
    private ArrayList<Answer> shuffledAnswers;
    private int currentResult;
    private int currentNo = 0;
    private HashMap<Integer, String> selectedAnswers;
    private int type;
    private ArrayList<Answer> orderedAnswersForOrderedId;
    private Button nextButton;
    private Button tipBtn;
    private String currentExplanation;
    private int max_result;

    TextView timerTextView;
    long startTime = 0;

    Handler timerHandler = new Handler();
    Runnable timerRunnable;
    private long timeLimit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        result = 0;
        Intent intent = getIntent();
        sets = intent.getParcelableExtra(SETTINGS);
        path = intent.getStringExtra(PATH_TO_FILE);
        questions = (Map<Integer, Question>) intent.getSerializableExtra(QUESTIONS);
        setContentView(R.layout.activity_test);
        main_l = (LinearLayout) findViewById(R.id.contentLayout);
        nextButton = (Button) findViewById(R.id.nextBtn);
        final List<Integer> shuffledList;
        tipBtn = (Button) findViewById(R.id.tipBtn);
        if (sets.isRandomizeQuestions()) {
            shuffledList = shuffle().subList(0, sets.getNumberOfQuestions());
        } else {
            shuffledList = (new ArrayList<>(questions.keySet())).subList(0, sets.numberOfQuestions);
        }
        countMaximumPoints(shuffledList);
        it = shuffledList.iterator();
        currentNo = 0;
        int key = (int) it.next();
        Question q = questions.get(key);
        currentNo++;
        TestActivity.this.setTitle(currentNo + "/" + shuffledList.size());
        showQuestion(q);
        if (it.hasNext()) {
            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    calculateResult();
                    int key = (int) it.next();
                    currentNo++;
                    TestActivity.this.setTitle(currentNo + "/" + shuffledList.size());
                    Question q = questions.get(key);
                    showQuestion(q);
                    if (!it.hasNext()) {
                        finishTest();
                    }
                }
            });
        } else {
            finishTest();
        }

        timerTextView = (TextView) findViewById(R.id.timerTextView);
        timeLimit = sets.getTimeLimit().getTime();
        startTime = System.currentTimeMillis();

        timerRunnable = new Runnable() {

            @Override
            public void run() {
                long GMT = java.util.TimeZone.getDefault().getRawOffset();
                long millis = System.currentTimeMillis() - startTime - GMT;
                long newMillis = timeLimit - millis;
                int seconds = (int) (newMillis / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;
                if (newMillis <= 0) {
                    Toast.makeText(getApplicationContext(), "Время истекло\nTime's out", Toast.LENGTH_SHORT).show();
                    goToResultScreen();
                } else {
                    timerTextView.setText(String.format("%02d:%02d", minutes, seconds));
                    timerHandler.postDelayed(this, 500);
                }
            }
        };
        timerHandler.postDelayed(timerRunnable, 0);
    }

    private void goToResultScreen() {
        timerHandler.removeCallbacks(timerRunnable);
        calculateResult();
        Intent in = new Intent(TestActivity.this, ResultActivity.class);
        in.putExtra("result", result);
        in.putExtra("max_result", max_result);
        in.putExtra(GRADESYSTEMNAME, sets.getCurrentGrade());
        Log.d(TAG, sets.getGrades().keySet().toString());
        //(HashMap<Integer, Grade>)
        in.putExtra(GRADES, (Serializable) sets.getGrades());
        startActivity(in);
    }

    private void finishTest() {
        Button finishBtn = (Button) findViewById(R.id.finishBtn);
        nextButton.setVisibility(View.INVISIBLE);
        finishBtn.setVisibility(View.VISIBLE);
        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToResultScreen();
            }
        });
    }

    private void countMaximumPoints(List<Integer> shuffledList) {
        max_result = 0;
        for (Integer key : shuffledList) {
            Question q = questions.get(key);
            int temp_type = q.getQuestionType();
            int temp_result = 0;
            int temp_max_result = 0;
            switch (temp_type) {
                case 0:
                    for (Answer a : q.getAnswers().values()) {
                        if (a.isCorrect()) {
                            if (a.getPrice() > temp_max_result) {
                                temp_max_result = a.getPrice();
                            }
                            break;
                        }
                    }
                    temp_result = temp_max_result;
                    break;
                case 1:
                    for (Answer a : q.getAnswers().values()) {
                        if (a.isCorrect()) {
                            if (a.getPrice() > temp_max_result) {
                                temp_max_result = a.getPrice();
                            }
                            break;
                        }
                    }
                    temp_result = temp_max_result;
                    break;
                case 2:
                    for (Answer a : q.getAnswers().values()) {
                        temp_result += a.getPrice();
                    }
                    break;
                case 3:
                    for (Answer a : q.getAnswers().values()) {
                        temp_result += a.getPrice();
                    }
                    break;
                case 4:
                    temp_result = q.getAnswers().size();
                    break;
                case 5:
                    for (Answer a : q.getAnswers().values()) {
                        if (a.getPrice() > temp_max_result) {
                            temp_max_result = a.getPrice();
                        }
                    }
                    temp_result = temp_max_result;
                    break;
                default:
                    break;
            }
            max_result += temp_result;
        }
        Log.d(TAG, "max_result: " + max_result);
    }

    private void calculateResult() {
        Log.d(TAG, "CALCULATING RESULT");
        try {
            if (selectedAnswers.size() == 0)
                throw new NullPointerException();
            switch (type) {
                case 0:
                    Log.d(TAG, "RADIO POLAR. size: " + selectedAnswers.size());
                    Iterator<Integer> iterator = selectedAnswers.keySet().iterator();
                    currentAnswer = shuffledAnswers.get(iterator.next());
                    currentResult = (currentAnswer.isCorrect() ? 1 : -1) * currentAnswer.getPrice();
                    Log.d(TAG, "radio clicked: text " + currentAnswer.getAnswerText());
                    Log.d(TAG, "radio clicked: price " + currentAnswer.getPrice());
                    Log.d(TAG, "radio clicked: isCorrect " + currentAnswer.isCorrect());
                    Log.d(TAG, "---------------------------");
                    break;
                case 1:
                    Log.d(TAG, "RADIO MULTI. size: " + selectedAnswers.size());
                    iterator = selectedAnswers.keySet().iterator();
                    currentAnswer = shuffledAnswers.get(iterator.next());
                    currentResult = (currentAnswer.isCorrect() ? 1 : -1) * currentAnswer.getPrice();
                    Log.d(TAG, "radio clicked: text " + currentAnswer.getAnswerText());
                    Log.d(TAG, "radio clicked: price " + currentAnswer.getPrice());
                    Log.d(TAG, "radio clicked: isCorrect " + currentAnswer.isCorrect());
                    Log.d(TAG, "---------------------------");
                    break;
                case 2:
                    Log.d(TAG, "CHECKBOX. size: " + selectedAnswers.size());
                    iterator = selectedAnswers.keySet().iterator();
                    /*Перебрать все ответы в shuffledAnswers и найти их в selectedAnswers.
                    если есть, то считать что чекнуты, если их нет то считать,
                    что это не чекнуто*/
                    for (Answer a : shuffledAnswers) {
                        if (selectedAnswers.containsValue(a.getAnswerText())) {
                            currentResult+=(a.isCorrect()?1:-1)*a.getPrice();
                            Log.d(TAG, currentResult+"");
                        } else {
                            currentResult+=(a.isCorrect()?-1:1)*a.getPrice();
                            Log.d(TAG, currentResult+"");
                        }
                        Log.d(TAG, "checkbox clicked: text " + a.getAnswerText());
                        Log.d(TAG, "checkbox clicked: price " + a.getPrice());
                        Log.d(TAG, "checkbox clicked: isCorrect " + a.isCorrect());
                        Log.d(TAG, "---------------------------");
                    }
                    break;


//                    currentAnswer = shuffledAnswers.get(v.getId());
//                    if (((CheckBox) v).isChecked()){
//                        selectedAnswers.put(v.getId(), ((CheckBox) v).getText());
//                        currentResult += (currentAnswer.isCorrect() ? 1 : -1) * currentAnswer.getPrice();
//                    }
//                    else{
//                        currentResult += (currentAnswer.isCorrect() ? -1 : 1) * currentAnswer.getPrice();
//                    }

                case 3:
                    Log.d(TAG, "MATCHING. size: " + selectedAnswers.size());
                    iterator = selectedAnswers.keySet().iterator();
                    while (iterator.hasNext()) {
                        int id = iterator.next();
                        currentAnswer = shuffledAnswers.get(id);
                        String rightItem = (String) selectedAnswers.get(id);
                        String rightAnswer = currentAnswer.getAnswerText();
                        if (rightItem.equals(rightAnswer)) {
                            currentResult += 1;
                            Log.d(TAG, rightItem + " correct: correct");
                        } else {
                            currentResult += -1;
                            Log.d(TAG, rightItem + "correct: incorrect");
                        }
                    }
                    Log.d(TAG, "------------------");
                    break;
                case 4:
                    Log.d(TAG, "SEQUENCE check. size: " + selectedAnswers.size());
                    iterator = selectedAnswers.keySet().iterator();

                    while (iterator.hasNext()) {
                        int id = iterator.next();
                        String selectedItem = (String) selectedAnswers.get(id);
                        currentAnswer = shuffledAnswers.get((answersToShow.indexOf(selectedItem) - 1));
                        if (orderedAnswersForOrderedId.indexOf(currentAnswer) == id) {
                            currentResult += 1;
                            Log.d(TAG, selectedItem + " isCorrect: correct");
                        } else {
                            currentResult += -1;
                            Log.d(TAG, selectedItem + " isCorrect: incorrect");
                        }
                    }
                    Log.d(TAG, "------------------");
                    break;
                case 5:
                    Log.d(TAG, "SHORT count: " + selectedAnswers.size());
                    if(answersToShow.contains(selectedAnswers.get(0))){
                        currentAnswer = shuffledAnswers.get(answersToShow.indexOf(selectedAnswers.get(0))-1);
                        currentResult = currentAnswer.getPrice();
                        Log.d(TAG, "correct SHORT");
                    }
                    else{
                        Log.d(TAG, "incorrect");
                    }
                    Log.d(TAG, "------------------");
                    break;
            }
        } catch (NullPointerException e) {
            Toast.makeText(getApplicationContext(), "Ничего не выбрано\nNothing chosen", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        result += currentResult;
        Log.d(TAG, "CURRENT RESULT: " + result);
    }

    private List<Integer> shuffle() {
        final List<Integer> vs = new ArrayList<>(questions.keySet());
        Collections.shuffle(vs);
        return vs;
    }

    private void showQuestion(Question q) {
        currentExplanation = q.getAnswerExplanation();
        tipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), currentExplanation, Toast.LENGTH_LONG).show();
            }
        });
        main_l.removeAllViews();
        currentResult = 0;
        ImageView imageView = new ImageView(this);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent();
            }
        });
        if (q.getFileName() != null) {
//            String pathh = path + q.getFileName();
            String pathh = "/mnt/shared/sharedVMFolder/" + q.getFileName();
            Log.d(TAG, "got path: " + pathh);
            Bitmap image = BitmapFactory.decodeFile(pathh);
            ThumbnailUtils thumb = new ThumbnailUtils();
            image = thumb.extractThumbnail(image, 400, 200);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.CENTER;
            imageView.setLayoutParams(layoutParams);
//            imageView.setScaleType(ImageView.ScaleType.FIT_START);
            imageView.setImageBitmap(image);

            main_l.addView(imageView);
            Log.d(TAG, "image set");
        }
        TextView questionText = new TextView(this);
        questionText.setGravity(Gravity.CENTER);
        questionText.setText(q.getQuestionText());
        main_l.addView(questionText);
        HashMap<Integer, Answer> answers = (HashMap) q.getAnswers();
        type = q.getQuestionType();
        Log.d(TAG, "TYPE is " + type);
        shuffledAnswers = new ArrayList<>(answers.values());
        if (sets.isRandomizeAnswers()) {
            Collections.shuffle(shuffledAnswers);
        }
        answersToShow = new ArrayList<>();
        answersToShow.add("(не выбрано)");
        for (Answer a : shuffledAnswers) {
            answersToShow.add(a.getAnswerText());
        }
        selectedAnswers = new HashMap<>();
        switch (type) {
            case 0:
                RadioGroup rg = new RadioGroup(this);
                for (Answer a : shuffledAnswers) {
                    RadioButton rb = new RadioButton(this);
//                    currentAnswer = a;
                    rb.setText(a.getAnswerText());
                    rb.setId(shuffledAnswers.indexOf(a));
                    rb.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            currentResult = 0;
                            if (((RadioButton) v).isChecked()) {
                                selectedAnswers.clear();
                                selectedAnswers.put(v.getId(), ((RadioButton) v).getText()+"");
                            }
                        }
                    });
                    rg.addView(rb);
                }
                main_l.addView(rg);
                break;
            case 1:
                rg = new RadioGroup(this);
                for (Answer a : shuffledAnswers) {
//                    Answer a = (Answer) pair.getValue();
                    RadioButton rb = new RadioButton(this);
                    rb.setText(a.getAnswerText());
                    rb.setId(shuffledAnswers.indexOf(a));
                    rb.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            currentResult=0;
                            if (((RadioButton) v).isChecked()) {
                                selectedAnswers.clear();
                                selectedAnswers.put(v.getId(), ((RadioButton) v).getText()+"");
                            }
                        }
                    });
//                    rb.setId(a.getId());
                    rg.addView(rb);
                }
                main_l.addView(rg);
                break;
            case 2:
                for (Answer a : shuffledAnswers) {
//                    Answer a = (Answer) pair.getValue();
//                    currentAnswer = a;
                    CheckBox cb = new CheckBox(this);
                    cb.setText(a.getAnswerText());
                    cb.setId(shuffledAnswers.indexOf(a));
                    cb.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            currentResult = 0;
                            if (((CheckBox) v).isChecked()) {
                                selectedAnswers.put(v.getId(), ((CheckBox) v).getText()+"");
                            } else {
                                selectedAnswers.remove(v.getId());
                            }
                        }
                    });
//                    cb.setId(a.getId());
                    main_l.addView(cb);
                }
                break;
            case 3:
                LinearLayout answer_l;
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                        android.R.layout.simple_list_item_1, answersToShow);

//                answer_l.setColumnCount(3);
//                answer_l.setRowCount(size);

                for (Answer a : shuffledAnswers) {
                    currentAnswer = a;
                    answer_l = new LinearLayout(this);

                    TextView tw = new TextView(this);
                    tw.setGravity(Gravity.LEFT);
                    tw.setText(a.getAnswerText2());

                    TextView defis = new TextView(this);
                    defis.setGravity(Gravity.CENTER);
                    defis.setText(" - ");

                    Spinner matchingSpinner = new Spinner(this);
                    selectedAnswers = new HashMap<>();
                    matchingSpinner.setAdapter(dataAdapter);
                    matchingSpinner.setSelection(0);
                    matchingSpinner.setId(shuffledAnswers.indexOf(a));
                    //matchingSpinner.setId(10 * a.getId() + 2);
                    matchingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                            try {
                                String rightItem = parentView.getSelectedItem().toString();
                                if (parentView.getSelectedItemPosition() != 0) {
                                    selectedAnswers.put(parentView.getId(), rightItem);
                                } else {
                                    selectedAnswers.remove(parentView.getId());
                                }
                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), e + "", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parentView) {

                        }

                    });

                    answer_l.addView(tw);
                    answer_l.addView(defis);
                    answer_l.addView(matchingSpinner);
                    main_l.addView(answer_l);
                }
                break;
            case 4:
                dataAdapter = new ArrayAdapter<>(this,
                        android.R.layout.simple_list_item_1, answersToShow);
                orderedAnswersForOrderedId = new ArrayList<>(q.getAnswers().values());
                for (Answer a : orderedAnswersForOrderedId) {
                    Spinner sequenceSpinner = new Spinner(this);
                    sequenceSpinner.setAdapter(dataAdapter);
                    sequenceSpinner.setId(orderedAnswersForOrderedId.indexOf(a));
                    sequenceSpinner.setSelection(0);
                    selectedAnswers = new HashMap<>();
                    sequenceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                            try {
                                if (parentView.getSelectedItemPosition() != 0) {
                                    selectedAnswers.put(parentView.getId(), parentView.getSelectedItem().toString());
                                } else {
                                    selectedAnswers.remove(parentView.getId());
                                }
                            } catch (Exception e) {
                                Log.d(TAG, "Exception caught: " + e);
                            }
                            // your code here
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                    main_l.addView(sequenceSpinner);
                }
                break;
            case 5:
                EditText et = new EditText(this);
                et.addTextChangedListener(new TextWatcher() {

                    public void afterTextChanged(Editable s) {
                    }

                    public void beforeTextChanged(CharSequence s, int start,
                                                  int count, int after) {
                    }

                    public void onTextChanged(CharSequence s, int start,
                                              int before, int count) {
                        Log.d(TAG, "PUTTING in SHORT - "+s);
                        selectedAnswers.put(0, s + "");
                    }
                });
                main_l.addView(et);
                break;
            default:
                break;

        }

    }


    @Override
    protected void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        Log.d(TAG, "onRestart");
        super.onRestart();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_test, menu);
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