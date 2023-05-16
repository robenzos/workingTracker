package hr.krcelicsamsa.worktracking;

import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import hr.krcelicsamsa.worktracking.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private AppDatabase appDatabase;
    List<String> texts;
    TextAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        hr.krcelicsamsa.worktracking.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        appDatabase = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "note-db").build();

        //BottomNavigationView navView = findViewById(R.id.nav_view);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        texts = new ArrayList<>();
        adapter = new TextAdapter(texts);
        recyclerView.setAdapter(adapter);

        Button button = findViewById(R.id.button);
        FloatingActionButton fab = findViewById(R.id.fab);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeInputDialog();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetDialog();
            }
        });

        //AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
        //        R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
        //        .build();
        //NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        //NavigationUI.setupWithNavController(binding.navView, navController);

        loadWorks();
    }

    private void showBottomSheetDialog() {
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_layout, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MainActivity.this);
        bottomSheetDialog.setContentView(bottomSheetView);

        Button closeButton = bottomSheetView.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.show();
    }

    private void showTimeInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.time_input_dialog, null);
        builder.setView(dialogView);

        final EditText editTextTime = dialogView.findViewById(R.id.editTextTime);

        builder.setTitle("Enter time:");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String timeInput = editTextTime.getText().toString();

                if (isValidTimeFormat(timeInput)) {
                    Toast toast = Toast.makeText(MainActivity.this, "Selected Time: " + timeInput, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 16); // Adjust the vertical offset as needed
                    toast.show();
                    saveWork(timeInput);
                    loadWorks();
                } else {
                    Toast toast = Toast.makeText(MainActivity.this, "Invalid time format", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 16); // Adjust the vertical offset as needed
                    toast.show();
                    dialog.cancel();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void saveWork(String inputtedTime) {
        Work work = new Work();
        work.date = LocalDateTime.now();
        work.secondsWorked = calculateSeconds(inputtedTime);
        work.payPerHour = 5.31;
        new InsertWorkTask().execute(work);
    }

    private class InsertWorkTask extends AsyncTask<Work, Void, Void> {
        @Override
        protected Void doInBackground(Work... works) {
            appDatabase.workDao().insert(works[0]);
            return null;
        }
    }

    public static int calculateSeconds(String time) {
        String[] parts = time.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        int seconds = Integer.parseInt(parts[2]);

        return hours * 3600 + minutes * 60 + seconds;
    }

    public static String convertToTime(int seconds) {
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int remainingSeconds = seconds % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds);
    }

    private boolean isValidTimeFormat(String time) {
        String regex = "^([0-1]\\d|2[0-3]):([0-5]\\d):([0-5]\\d)$";
        return time.matches(regex);
    }

    private void loadWorks() {
        new LoadWorksTask().execute();
    }

    private class LoadWorksTask extends AsyncTask<Void, Void, List<Work>> {
        @Override
        protected List<Work> doInBackground(Void... voids) {
            return appDatabase.workDao().getAll();
        }

        @Override
        protected void onPostExecute(@NonNull List<Work> works) {
            texts.clear();
            for (int i = works.size() - 1; i >= 0; i--) {
                Work work = works.get(i);
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.insert(0, "\n\n");
                stringBuilder.insert(0, work.date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")) + "   ---   " + convertToTime(work.secondsWorked));
                texts.add(stringBuilder.toString());
            }
            adapter.notifyDataSetChanged();
        }
    }

}