package com.example.universities.profession;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.universities.R;
import com.example.universities.uniList.UniversitiesList;
import com.example.universities.base.BaseActivity;

/**
 * This class create interview to know your professional orientation
 */
public class ProfessionalOrientation extends BaseActivity implements CompoundButton.OnCheckedChangeListener {
    private TextView textHeader;
    private Button buttonStart;
    private Button buttonNextQ;
    private Button buttonShowUniversities;
    private CheckBox checkBoxQ1;
    private CheckBox checkBoxQ2;
    private CheckBox checkBoxQ3;
    private TestConstants testConstants;
    private int nowQuestion;
    private int[] answers;
    private int[] indAnswers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();

        checkBoxQ1.setOnCheckedChangeListener(this);
        checkBoxQ2.setOnCheckedChangeListener(this);
        checkBoxQ3.setOnCheckedChangeListener(this);
    }

    private void init() {
        testConstants = new TestConstants();

        textHeader = findViewById(R.id.textHeader);
        buttonStart = findViewById(R.id.buttonStart);
        buttonNextQ = findViewById(R.id.buttonNextQ);
        buttonShowUniversities = findViewById(R.id.buttonShowUniversities);
        checkBoxQ1 = findViewById(R.id.checkBoxQ1);
        checkBoxQ2 = findViewById(R.id.checkBoxQ2);
        checkBoxQ3 = findViewById(R.id.checkBoxQ3);

        buttonStart.setVisibility(View.VISIBLE);
    }

    public void startButton(View view) {
        nowQuestion = 0;
        answers = new int[6];

        buttonStart.setVisibility(View.GONE);
        buttonShowUniversities.setVisibility(View.GONE);

        buttonNextQ.setVisibility(View.VISIBLE);
        checkBoxQ1.setVisibility(View.VISIBLE);
        checkBoxQ2.setVisibility(View.VISIBLE);
        checkBoxQ3.setVisibility(View.VISIBLE);

        textHeader.setText(testConstants.questions[nowQuestion]);
        checkBoxQ1.setText(testConstants.answers[nowQuestion][0]);
        checkBoxQ2.setText(testConstants.answers[nowQuestion][1]);
        checkBoxQ3.setText(testConstants.answers[nowQuestion][2]);
    }

    public void setButtonNextQButtonFunk(View view) {
        if (!checkBoxQ1.isChecked() && !checkBoxQ2.isChecked() && !checkBoxQ3.isChecked()) {
            Toast.makeText(getApplicationContext(), "Выберите один из вариантов ответа", Toast.LENGTH_SHORT).show();
        } else {

            if (checkBoxQ1.isChecked()) {
                setAnswers(1);
                checkBoxQ1.setChecked(false);
            }
            else if (checkBoxQ2.isChecked()) {
                setAnswers(2);
                checkBoxQ2.setChecked(false);
            }
            else if (checkBoxQ3.isChecked()) {
                setAnswers(3);
                checkBoxQ3.setChecked(false);
            }

            nowQuestion++;

            if (nowQuestion == testConstants.questions.length) {
                showResults();
            }
            else {

                textHeader.setText(testConstants.questions[nowQuestion]);
                checkBoxQ1.setText(testConstants.answers[nowQuestion][0]);
                checkBoxQ2.setText(testConstants.answers[nowQuestion][1]);
                checkBoxQ3.setText(testConstants.answers[nowQuestion][2]);

                if (nowQuestion == testConstants.questions.length -1 ) {
                    buttonNextQ.setText("Закончить опрос");
                }
            }
        }
    }

    private void setAnswers(int q) {
        for (int i = 0; i < testConstants.blank[nowQuestion].length; i++) {
            if (testConstants.blank[nowQuestion][i] == q) {
                answers[i]++;
            }
        }
    }

    private void showResults() {
        buttonNextQ.setVisibility(View.GONE);
        checkBoxQ1.setVisibility(View.GONE);
        checkBoxQ2.setVisibility(View.GONE);
        checkBoxQ3.setVisibility(View.GONE);

        indAnswers = indMax(answers);
        StringBuilder sb = new StringBuilder();
        sb.append("У вас ");
        sb.append(testConstants.results[indAnswers[0]]);
        if(indAnswers[1] != 0) {
            sb.append('\n').append('\n').append("А также же у вас есть ").append(testConstants.results[indAnswers[1]]);
        }
        textHeader.setGravity(Gravity.LEFT);
        textHeader.setText(sb.toString());

        buttonStart.setVisibility(View.VISIBLE);
        buttonStart.setText("Пройти тест снова");

        buttonShowUniversities.setVisibility(View.VISIBLE);

    }

    private int[] indMax(int[] arr) {
        int max = -1;
        int[] ind = new int[2];
        for (int i = 0; i < arr.length; i++) {
            if (max < arr[i]) {
                ind[0] = i;
                ind[1] = 0;
                max = arr[i];
            }
            if (max == arr[i]) ind[1] = i;
        }
        return ind;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            if (buttonView.getId() == checkBoxQ1.getId()) {
                checkBoxQ2.setChecked(false);
                checkBoxQ3.setChecked(false);
            }
            if (buttonView.getId() == checkBoxQ2.getId()) {
                checkBoxQ1.setChecked(false);
                checkBoxQ3.setChecked(false);
            }
            if (buttonView.getId() == checkBoxQ3.getId()) {
                checkBoxQ2.setChecked(false);
                checkBoxQ1.setChecked(false);
            }
        }
    }

    public void showUniversitiesButtonFunk(View view) {
        Intent intent = new Intent(getApplicationContext(), UniversitiesList.class);
        if(indAnswers[1] != 0) {
            intent.putExtra("professions1", testConstants.professions[indAnswers[0]]);
            intent.putExtra("professions2", testConstants.professions[indAnswers[1]]);
        }
        else
            intent.putExtra("professions1", testConstants.professions[indAnswers[0]]);
        startActivity(intent);
    }

    @Override
    public  int getContentViewId() {
        return R.layout.activity_professional_orientation;
    }

    @Override
    public int getNavigationMenuItemId() {
        return R.id.home;
    }
}