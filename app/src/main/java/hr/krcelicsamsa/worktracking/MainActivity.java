package hr.krcelicsamsa.worktracking;

import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

import hr.krcelicsamsa.worktracking.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private AppDatabase appDatabase;
    List<String> texts;
    TextAdapter adapter;
    double currentPayPerHr;
    double totalEarned = 0.0;
    int totalSecondsWorked = 0;
    NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.GERMANY);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        hr.krcelicsamsa.worktracking.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        appDatabase = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "work_database")
                //.fallbackToDestructiveMigration()
                .build();

        //BottomNavigationView navView = findViewById(R.id.nav_view);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        currentPayPerHr = 0;
        loadCurrentPayPerHr();

        texts = new ArrayList<>();
        adapter = new TextAdapter(texts);
        recyclerView.setAdapter(adapter);

        Button button = findViewById(R.id.button);
        FloatingActionButton fabSettings = findViewById(R.id.fab);
        FloatingActionButton fabStatistics = findViewById(R.id.fabLeft);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeInputDialog();
            }
        });

        fabSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetDialogSettings();
            }
        });

        fabStatistics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetDialogStatistics();
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

    private void loadCurrentPayPerHr() {
        AsyncTask<Void, Void, Double> task = new AsyncTask<Void, Void, Double>() {
            @Override
            protected Double doInBackground(Void... voids) {
                return getCurrentPayPerHr();
            }
            @Override
            protected void onPostExecute(Double payPerHour) {
                currentPayPerHr = payPerHour;
                loadWorks();
            }
        };
        task.execute();
    }

    private void showBottomSheetDialogSettings() {
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_layout_settings, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MainActivity.this);
        bottomSheetDialog.setContentView(bottomSheetView);

        Button closeButton = bottomSheetView.findViewById(R.id.closeButton);
        Button deleteWorkHistoryButton = bottomSheetView.findViewById(R.id.deleteWorkHistoryButton);
        TextInputEditText textInputEditText = bottomSheetView.findViewById(R.id.newPayPerHour);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = String.valueOf(textInputEditText.getText());
                try {
                    double value = Double.parseDouble(input);
                    saveUserSettings(value);
                    bottomSheetDialog.dismiss();
                    loadCurrentPayPerHr();
                } catch (NumberFormatException e) {
                    Toast.makeText(MainActivity.this, "Invalid input. Pleae enter a valid number.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        deleteWorkHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Confirmation");
                builder.setMessage("Are you sure you want to perform this action?");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Executors.newSingleThreadExecutor().execute(new Runnable() {
                            @Override
                            public void run() {
                                appDatabase.workDao().deleteAll();
                                // Use the Main thread to update the UI
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(MainActivity.this, "Obrisani svi radovi.", Toast.LENGTH_SHORT).show();
                                        loadWorks();
                                    }
                                });
                            }
                        });
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        bottomSheetDialog.show();
    }


    private void showBottomSheetDialogStatistics() {
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_layout_statistics, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MainActivity.this);
        bottomSheetDialog.setContentView(bottomSheetView);

        Button closeButton = bottomSheetView.findViewById(R.id.closeButton);
        TextView textViewTimeWorked = bottomSheetView.findViewById(R.id.totalTimeWorked);
        TextView textViewTotalEarned = bottomSheetView.findViewById(R.id.totalEarned);

        textViewTimeWorked.setText(convertTimeFormatToVisual(convertToTime(totalSecondsWorked)));
        textViewTotalEarned.setText(currencyFormatter.format(totalEarned));

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });
        bottomSheetDialog.show();
    }

    public String convertTimeFormatToVisual(String timeString) {
        String[] parts = timeString.split(":");

        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);

        return hours + "h " + minutes + "m";
    }

    private double getCurrentPayPerHr() {
        List<UserSettings> userSettings = appDatabase.userSettingsDao().getAll();
        if ((long) userSettings.size() == 1) {
            return userSettings.get(0).payPerHour;
        } else {
            saveUserSettings(5.31);
            return getCurrentPayPerHr();
        }
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
        work.payPerHour = currentPayPerHr;
        new InsertWorkTask().execute(work);
    }

    private class InsertWorkTask extends AsyncTask<Work, Void, Void> {
        @Override
        protected Void doInBackground(Work... works) {
            appDatabase.workDao().insert(works[0]);
            return null;
        }
    }

    private void saveUserSettings(double newPayPerHour) {
        UserSettings userSettings = new UserSettings();
        userSettings.payPerHour = newPayPerHour;
        new InsertUserSettingsTask().execute(userSettings);
    }

    private class InsertUserSettingsTask extends AsyncTask<UserSettings, Void, Void> {
        @Override
        protected Void doInBackground(UserSettings... userSettings) {
            appDatabase.userSettingsDao().deleteAll();
            appDatabase.userSettingsDao().insert(userSettings[0]);
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

    public double calculateTotalPay(int secondsWorked, double payPerHour) {
        double hoursWorked = secondsWorked / 3600.0;
        return hoursWorked * payPerHour;
    }

    private class LoadWorksTask extends AsyncTask<Void, Void, List<Work>> {
        @Override
        protected List<Work> doInBackground(Void... voids) {
            return appDatabase.workDao().getAll();
        }

        @Override
        protected void onPostExecute(@NonNull List<Work> works) {
            texts.clear();
            if (works.isEmpty()) {
                // Handle empty database here
                // For example, display a message or perform any necessary actions
                texts.add("Još nema spremljenih radova.");
            } else {
                for (int i = works.size() - 1; i >= 0; i--) {
                    Work work = works.get(i);
                    totalSecondsWorked += work.secondsWorked;
                    totalEarned += calculateTotalPay(work.secondsWorked, work.payPerHour);
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.insert(0, "\n");
                    stringBuilder.insert(0, work.payPerHour + "€/hr" + "   ---   " +  currencyFormatter.format(calculateTotalPay(work.secondsWorked, work.payPerHour)));
                    stringBuilder.insert(0, "\n");
                    stringBuilder.insert(0, work.date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")) + "   ---   " + convertToTime(work.secondsWorked));
                    texts.add(stringBuilder.toString());
                }
            }
            adapter.notifyDataSetChanged();
        }
    }
}