package com.example.tema.testviewer;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class MainActivity extends ActionBarActivity {
    private static final String QUESTIONS = "com.example.tema.QUESTIONS";
    final static String myLog = "MyLog";
    private static final String SETTINGS = "com.example.tema.SETTINGS";
    private static final String PATH_TO_FILE = "com.example.tema.PATH_TO_FILE";
    Settings sets = new Settings();
    Map<Integer, Question> questions = new HashMap<>();
    String path = null;
    HashMap<String, String> hasmp = new HashMap<>();
    private String image_path;
    Button beginBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(myLog, "create on " + (new Date(System.currentTimeMillis())).toString());
        super.onCreate(savedInstanceState);
        this.setTitle("Главное меню");
        setContentView(R.layout.main);
        try {
            beginBtn = (Button) findViewById(R.id.beginTestBtn);
            beginBtn.setVisibility(View.INVISIBLE);
            final TextView view = (TextView) findViewById(R.id.currentTest);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    parseFileEtc();
                    beginBtn.setVisibility(View.VISIBLE);
                }
            });
            beginBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent in = new Intent(MainActivity.this, TestActivity.class);
                    in.putExtra(SETTINGS, sets);
                    in.putExtra(QUESTIONS, (HashMap) questions);
                    in.putExtra(PATH_TO_FILE, image_path);
                    Toast.makeText(getApplicationContext(), "Время пошло\nYou have\n" + sets.getTimeLimit().toString(), Toast.LENGTH_SHORT).show();
                    startActivity(in);
                }
            });
            final Button chooseTestButton = (Button) findViewById(R.id.chooseTestButton);
            chooseTestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showFileChooser();
                }
            });
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Exception caught: " + e, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public void parseFileEtc() {
        try {
            HashMap grades = new HashMap<>();
            Question q = new Question();
            Answer a = new Answer();
            HashMap answers = new HashMap<>();
            Grade grade = new Grade();
            XmlPullParser xpp = prepareXpp();
            Log.d(myLog, "started parsing: ");
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                switch (xpp.getEventType()) {
                    case XmlPullParser.START_TAG:
                        String tag_name = xpp.getName();
                        switch (tag_name) {
                            case "questionsToAsk":
                                xpp.next();
                                sets.setNumberOfQuestions(Integer.parseInt(xpp.getText()));
                                Log.d(myLog, "2ask: " + sets.getNumberOfQuestions());
                                break;
                            case "randomizeQuestions":
                                xpp.next();
                                sets.setRandomizeQuestions(xpp.getText().equals("1"));
                                Log.d(myLog, "randomQ: " + sets.isRandomizeQuestions());
                                break;
                            case "randomizeAnswers":
                                xpp.next();
                                sets.setRandomizeAnswers(xpp.getText().equals("1"));
                                Log.d(myLog, "randomA: " + sets.isRandomizeQuestions());
                                break;
                            case "timeLimit":
                                DateFormat formatter = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
                                xpp.next();
                                try {//
                                    sets.setTimeLimit(new Time(formatter.parse(xpp.getText()).getTime()));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                    Log.d(myLog, e.toString());
                                }
                                Log.d(myLog, "timeLimit: " + sets.getTimeLimit());
                                break;
                            case "gradeSystem":
                                sets.setCurrentGrade(xpp.getAttributeValue(0));
                                Log.d(myLog, "currentGrade: " + sets.getCurrentGrade());
                                break;
                            case "Grade":
                                grade = new Grade();
                                if (sets.getCurrentGrade().equals("custom")) {
                                    int id = Integer.parseInt(xpp.getAttributeValue(0));
                                    Log.d(myLog, "grade id: " + id);
                                    grade.setId(id);
                                    xpp.next();
                                    xpp.next();
                                    xpp.next();
                                    grade.setName(xpp.getText());
                                    Log.d(myLog, "name: " + grade.getName());
                                    xpp.next();
                                    xpp.next();
                                    xpp.next();
                                    xpp.next();
                                    grade.setNumberOfPoints(Integer.parseInt(xpp.getText()));
                                    Log.d(myLog, "NoP: " + grade.getNumberOfPoints());
                                    xpp.next();
                                    xpp.next();
                                    xpp.next();
                                    xpp.next();
                                    grade.setDiscription(xpp.getText());
                                    Log.d(myLog, "desc: " + grade.getDiscription());
                                }
                                break;
                            case "questions":
                                break;
                            case "Question":
                                q = new Question();
                                int id = Integer.parseInt(xpp.getAttributeValue(0));
                                Log.d(myLog, "ques id: " + id);
                                q.setId(id);
                                Log.d(myLog, "new Question");
                                break;
                            case "questionType":
                                xpp.next();
                                q.setQuestionType(Integer.parseInt(xpp.getText()));
                                break;
                            case "questionText":
                                xpp.next();
                                q.setQuestionText(xpp.getText());
                                break;
                            case "explanation":
                                xpp.next();
                                q.setAnswerExplanation(xpp.getText());
                                break;
                            case "image":
                                Log.d(myLog, xpp.getAttributeCount() + " count");
                                if (xpp.getAttributeValue(0).equals("no")) {
                                    xpp.next();
                                    q.setFileName(xpp.getText());
                                }
                                break;
                            case "answers":
                                answers = new HashMap<>();
                                q.setCount(Integer.parseInt(xpp.getAttributeValue(0)));
                                Log.d(myLog, "new array fo answers");
                                break;
                            case "Answer":
                                a = new Answer();
                                id = Integer.parseInt(xpp.getAttributeValue(0));
                                Log.d(myLog, "answer id: " + id);
                                a.setId(id);
                                Log.d(myLog, "new answer");
                                break;
                            case "plainText":
                                xpp.next();
                                a.setAnswerText(xpp.getText());
                                break;
                            case "plainText2":
                                xpp.next();
                                a.setAnswerText2(xpp.getText());
                                break;
                            case "isCorrect":
                                xpp.next();
                                a.setCorrect(xpp.getText().equals("1"));
                                break;
                            case "price":
                                xpp.next();
                                a.setPrice(Integer.parseInt(xpp.getText()));
                                break;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        tag_name = xpp.getName();
                        switch (tag_name) {
                            case "gradeSystem":
                                sets.setGrades(grades);
                                Log.d(myLog, "grades set");
                                break;
                            case "Grade":
                                grades.put(grade.getId(), grade);
                                Log.d(myLog, "adding grade #" + grade.getId());
                                break;
                            case "Question":
                                questions.put(q.getId(), q);
                                Log.d(myLog, "adding question #" + q.getId());
                                break;
                            case "Answer":
                                answers.put(a.getId(), a);
                                Log.d(myLog, "adding answer #" + a.getId());
                                break;
                            case "answers":
                                q.setAnswers(answers);
                                Log.d(myLog, "answers set");
                                break;
                        }
                        break;
                    default:
                        break;
                }
                xpp.next();
            }
            beginBtn.setVisibility(View.VISIBLE);
            return;
        } catch (XmlPullParserException e) {
            Log.d(myLog, "XML parse error: " + e);
            Toast.makeText(getApplicationContext(), "Ошибка чтения теста: проверьте расширение файла(xml).\n\n" + e, Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (IOException e) {
            Log.d(myLog, "OtherException: " + e);
            Toast.makeText(getApplicationContext(), "Вылетел IOException. Решение: все закройте и запустите по-новой.\n\n" + e, Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (Exception e){
            Toast.makeText(getApplicationContext(), "Вылетел какой-то Exception. Решение: все закройте и запустите по-новой.\n\n " + e, Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        beginBtn.setVisibility(View.INVISIBLE);
    }

    XmlPullParser prepareXpp() throws XmlPullParserException, FileNotFoundException {
        Log.d(myLog, "preparingXpp");
//        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
//        factory.setNamespaceAware(true);
//        XmlPullParser xpp = factory.newPullParser();
        //xpp.setInput(new StringReader(read(Environment.getExternalStorageDirectory().getPath() + "/Download/test.txt")));
        //return xpp;
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
//factory.setNamespaceAware(true); // если используется пространство имён
        XmlPullParser parser = factory.newPullParser();
            //File file = new File(Environment.getExternalStorageDirectory()+ "/Download/exampleTest.xml");

//        File file = new File("/mnt/shared/sharedVMFolder/exampleTest.xml");
            File file = new File(path);
            FileInputStream fis = new FileInputStream(file);
            parser.setInput(new InputStreamReader(fis));
        return parser;
        //return getResources().getXml(R.xml.temp);
    }

    public void OnOpenFileClick(View view) {
        OpenFileDialog fileDialog = new OpenFileDialog(this);
        fileDialog.show();
    }

    private static final int FILE_SELECT_CODE = 0;

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    Log.d(myLog, "File Uri: " + uri.toString());
                    // Get the path
                    try {
                        this.path = getPath(this, uri);
                        CharSequence fileName = path.subSequence(path.lastIndexOf("@") + 1, path.length()-4);
                        ((TextView) findViewById(R.id.currentTest)).setText(fileName);
                        parseFileEtc();

                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Exception: "+e, Toast.LENGTH_SHORT).show();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                        Log.d(myLog, e + "");
                        Toast.makeText(getApplicationContext(), "Exception: "+e, Toast.LENGTH_SHORT).show();
                    }
                    image_path = path.substring(0, path.lastIndexOf("/") + 1);
                    Log.d(myLog, "File Path + File: " + path);
                    Log.d(myLog, "File Path: " + image_path);

                    // Get the file instance
                    // File file = new File(path);
                    // Initiate the upload
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public static String getPath(Context context, Uri uri) throws URISyntaxException {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {"_data"};
            Cursor cursor = null;

            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                // Eat it
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }
}
