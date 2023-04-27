package com.example.assignmenttracker;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.assignmenttracker.DB.AppDataBase;
import com.example.assignmenttracker.DB.AssignmentTrackerDAO;
import com.example.assignmenttracker.databinding.LandingPageBinding;

import java.util.List;

public class LandingPage extends AppCompatActivity {
//    Comment here
    LandingPageBinding binding;

    TextView mainDisplay;
    EditText assignment;
    EditText score;
    Button submit;
    AssignmentTrackerDAO assignmentTrackerDAO;

    List<AssignmentTracker> assignmentTrackerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landing_page);
        binding = LandingPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // binding
        mainDisplay = binding.mainAssignmentTrackerDisplay;
        assignment = binding.mainAssignmentEditText;
        score = binding.mainScoreEditText;
        submit = binding.mainSubmitButton;

        assignmentTrackerDAO = Room.databaseBuilder(this, AppDataBase.class, AppDataBase.DATABASE_NAME)
                .allowMainThreadQueries()
                .build()
                .AssignmentTrackerDAO();

        refreshDisplay();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitAssignmentTracker();
                refreshDisplay();
            }
        });
    } // end of onCreate

    private void submitAssignmentTracker() {
        String assignment1 = assignment.getText().toString();
        double score2 = Double.parseDouble(score.getText().toString());
        AssignmentTracker tracker = new AssignmentTracker(assignment1, score2);

        assignmentTrackerDAO.insert(tracker);
    }

    private void refreshDisplay() {
        assignmentTrackerList = assignmentTrackerDAO.getAssignmentTrackers();
        if(!assignmentTrackerList.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for(AssignmentTracker tracker : assignmentTrackerList) {
                sb.append(tracker.toString());
            }
        } else {
            mainDisplay.setText(R.string.nothing_in_tracker_message);
        }
    }
}