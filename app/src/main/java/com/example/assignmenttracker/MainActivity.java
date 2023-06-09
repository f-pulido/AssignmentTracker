package com.example.assignmenttracker;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;
import com.example.assignmenttracker.DB.AppDataBase;
import com.example.assignmenttracker.DB.AssignmentTrackerDAO;
import com.example.assignmenttracker.databinding.ActivityMainBinding;
import java.util.Calendar;
import java.util.List;
/**
 * @author Carlos Santiago, Fernando A. Pulido
 * @since April 23, 2023
 * Description: Android activity class that implements a user landing page.
 */
public class MainActivity extends AppCompatActivity {
    // Field(s)
    private static final String USER_ID_KEY = "com.example.assignmenttracker.userIdKey";
    private static final String PREFERENCES_KEY = "com.example.assignmenttracker.PREFERENCES_KEY";
    private User user;
    private String firstName;
    private String lastName;
    private DatePickerDialog datePickerDialog;
    private Button mainDateButton;
    private ActivityMainBinding binding;
    private EditText assignment;
    private Button submit;
    private EditText subject;
    private TextView mainWelcomeMessage;
    private AssignmentTrackerDAO assignmentTrackerDAO;
    private int userId = -1;
    private SharedPreferences preferences = null;
//     private Button buttonLogout;
    private Button logAttendanceButton;
    public static Intent intentFactory(Context context, int userId) {
        Log.d("MainActivity", "intentFactory CALLED SUCCESSFULLY");
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(USER_ID_KEY, userId);
        return intent;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("MainActivity", "onCreate CALLED SUCCESSFULLY");
        super.onCreate(savedInstanceState);
        // Initialize database, user, and date picker
        getDatabase();
        checkForUser();
        initDatePicker();
        // Set content view using view binding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Initialize UI elements and display welcome message
        assignment = binding.mainAssignmentEditText;
        subject = binding.mainSubjectEditText;
        submit = binding.mainSubmitButton;
        mainWelcomeMessage = binding.mainWelcomeMessage;
        mainDateButton = binding.mainDateButton;
        mainDateButton.setText(getTodaysDate());
        displayWelcomeMessage();
        // Set listener for submit button
        submit.setOnClickListener(view -> {
            submitAssignmentTracker();
        });
        logAttendanceButton = findViewById(R.id.logAttendanceButton);
        logAttendanceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AttendanceActivity.class);
                intent.putExtra(USER_ID_KEY, userId);
                startActivity(intent);
            }
        });
    }
    private String getTodaysDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month = month + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day, month, year);
    }
    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = (datePicker, year, month, day) -> {
            month = month + 1;
            String date = makeDateString(day, month, year);
            mainDateButton.setText(date);
        };
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        datePickerDialog = new DatePickerDialog(this, dateSetListener, year, month, day);
    }
    private String makeDateString(int day, int month, int year) {
        return getMonthFormat(month) + " " + day + " " + year;
    }
    private String getMonthFormat(int month) {
        if (month == 1) return "JAN";
        if (month == 2) return "FEB";
        if (month == 3) return "MAR";
        if (month == 4) return "APR";
        if (month == 5) return "MAY";
        if (month == 6) return "JUN";
        if (month == 7) return "JUL";
        if (month == 8) return "AUG";
        if (month == 9) return "SEP";
        if (month == 10) return "OCT";
        if (month == 11) return "NOV";
        if (month == 12) return "DEC";
        return "JAN";
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d("MainActivity", "onCreateOptionsMenu CALLED SUCCESSFULLY");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.d("MainActivity", "onOptionsItemSelected CALLED SUCCESSFULLY");
        Intent intent;
        switch (item.getItemId()) {
            case R.id.item0:
                Toast.makeText(this, "Go Back Selected Selected", Toast.LENGTH_SHORT).show();
                logoutUser();
                return true;
            case R.id.item1:
                Toast.makeText(this, "Edit Profile Selected", Toast.LENGTH_SHORT).show();
                intent = EditProfileActivity.intentFactory(getApplicationContext(), userId);
                startActivity(intent);
                return true;
            case R.id.item2:
                Toast.makeText(this, "Item 2 Selected", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.item3:
                Toast.makeText(this, "To Do List Selected", Toast.LENGTH_SHORT).show();
                intent = ToDoActivity.intentFactory(getApplicationContext(), userId);
                startActivity(intent);
                return true;
            case R.id.item4:
                Toast.makeText(this, "Logout Selected", Toast.LENGTH_SHORT).show();
                logoutUser();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void displayWelcomeMessage() {
        Log.d("MainActivity", "displayWelcomeMessage CALLED SUCCESSFULLY");
        user = assignmentTrackerDAO.getUserByUserId(userId);
        if (user != null) {
            firstName = user.getFirstName();
            lastName = user.getLastName();
            mainWelcomeMessage.setText("Hello, " + firstName + "!\nEnter your assignments below");
        }
    }
    private void submitAssignmentTracker() {
        Log.d("MainActivity", "submitAssignmentTracker CALLED SUCCESSFULLY");
        String assignmentText = assignment.getText().toString();
        String subjectText = subject.getText().toString();
        String dateValue = mainDateButton.getText().toString();
        AssignmentTracker tracker = new AssignmentTracker(assignmentText, subjectText, dateValue, userId);
        assignmentTrackerDAO.insert(tracker);
        Toast.makeText(this, "Successfully added " + tracker.getAssignment() + " to your To Do List", Toast.LENGTH_SHORT).show();
    }
    private void checkForUser() {
        Log.d("MainActivity", "checkForUser CALLED SUCCESSFULLY");
        // Do we have a user in the intent?
        userId = getIntent().getIntExtra(USER_ID_KEY, -1);
        // Do we have a user in the preferences?
        if (userId != -1) {
            return;
        }
        SharedPreferences preferences = this.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
        userId = preferences.getInt(USER_ID_KEY, -1);
        if (userId != -1) {
            return;
        }
        // Do we have any users at all?
        List<User> users = assignmentTrackerDAO.getAllUsers();
        if (users.size() <= 0) {
            User defaultUser = new User("Mike", "Wazowski", "testuser1", "testuser1", false);
            User altUser = new User("James", "Sullivan", "admin2", "admin2", true);
            assignmentTrackerDAO.insert(defaultUser, altUser);
            Log.d("MainActivity", "SHOULD HAVE INSERTED DEFAULTS");
        }
        // Store the user ID in the preferences
        // SharedPreferences.Editor editor = this.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE).edit();
        // editor.putInt(USER_ID_KEY, userId);
        // editor.apply();
        Intent intent = LoginActivity.intentFactory(this);
        startActivity(intent);
    }
    private void getDatabase() {
        Log.d("MainActivity", "getDatabase CALLED SUCCESSFULLY");
        assignmentTrackerDAO = Room.databaseBuilder(this, AppDataBase.class, AppDataBase.DATABASE_NAME).allowMainThreadQueries().build().AssignmentTrackerDAO();
    }
    private void logoutUser() {
        Log.d("MainActivity", "logoutUser CALLED SUCCESSFULLY");
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setMessage("Logout");
        alertBuilder.setPositiveButton(getString(R.string.yes), (dialog, which) -> {
            clearUserFromIntent();
            clearUserFromPref();
            userId = -1;
            checkForUser();
        });
        alertBuilder.setNegativeButton(getString(R.string.no), (dialog, which) -> {
            //We don't really need to do anything here.
        });
        alertBuilder.create().show();
    }
    private void clearUserFromPref() {
        Log.d("MainActivity", "clearUserFromPref CALLED SUCCESSFULLY");
        getIntent().putExtra(USER_ID_KEY, -1);
    }
    private void clearUserFromIntent() {
        Log.d("MainActivity", "clearUserFromIntent CALLED SUCCESSFULLY");
        addUserToPreference(-1);
    }
    private void addUserToPreference(int userId) {
        Log.d("MainActivity", "addUserToPreference CALLED SUCCESSFULLY");
        if (preferences == null) {
            getPrefs();
        }
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(USER_ID_KEY, userId);
        editor.apply();
    }
    private void getPrefs() {
        Log.d("MainActivity", "getPrefs CALLED SUCCESSFULLY");
        preferences = this.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
    }
    public void openDatePicker(View view) {
        datePickerDialog.show();
    }
}

