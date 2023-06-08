package hr.krcelicsamsa.worktracking;
import static hr.krcelicsamsa.worktracking.MainActivity.isValidTimeFormat;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;

public class MainActivityMock extends AppCompatActivity {
    TextAdapter adapter;

    public TextAdapter getAdapter() {
        return adapter;
    }

    public boolean showTimeInputDialogMockEntry(String inputtedText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivityMock.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.time_input_dialog, null);
        builder.setView(dialogView);

        EditText editText = new EditText(MainActivityMock.this);
        editText.setText(inputtedText);

        AlertDialog dialog = builder.create();
        dialog.show();

        if (isValidTimeFormat(editText.getText().toString())) {
            return true;
        } else {
            return false;
        }
    }

    public boolean showTimeInputDialogMockOpening() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivityMock.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.time_input_dialog, null);
        builder.setView(dialogView);

        EditText editText = new EditText(MainActivityMock.this);

        builder.setTitle("Enter time:");

        AlertDialog dialog = builder.create();
        dialog.show();

        return dialog.isShowing();
    }

    public boolean showBottomSheetDialogSettingsInput(String inputtedText) {
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_layout_settings, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MainActivityMock.this);
        bottomSheetDialog.setContentView(bottomSheetView);

        EditText textInputEditText = new EditText(MainActivityMock.this);
        textInputEditText.setText(inputtedText);

        bottomSheetDialog.show();

        try {
            Double.parseDouble(String.valueOf(textInputEditText.getText()));
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean showBottomSheetDialogSettingsOpening() {
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_layout_settings, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MainActivityMock.this);
        bottomSheetDialog.setContentView(bottomSheetView);

        Button closeButton = bottomSheetView.findViewById(R.id.closeButton);
        Button deleteWorkHistoryButton = bottomSheetView.findViewById(R.id.deleteWorkHistoryButton);

        EditText textInputEditText = new EditText(MainActivityMock.this);

        bottomSheetDialog.show();

        return bottomSheetDialog.isShowing();
    }
}

/*
   private void showBottomSheetDialogSettings() {
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_layout_settings, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MainActivity.this);
        bottomSheetDialog.setContentView(bottomSheetView);

        Button closeButton = bottomSheetView.findViewById(R.id.closeButton);
        Button deleteWorkHistoryButton = bottomSheetView.findViewById(R.id.deleteWorkHistoryButton);
        TextInputEditText textInputEditText = bottomSheetView.findViewById(R.id.newPayPerHour);

        closeButton.setOnClickListener(v -> {
            String input = String.valueOf(textInputEditText.getText());
            try {
                double value = Double.parseDouble(input);
                saveUserSettings(value);
                bottomSheetDialog.dismiss();
                loadCurrentPayPerHr();
            } catch (NumberFormatException e) {
                Toast.makeText(MainActivity.this, "Invalid input. Pleae enter a valid number.", Toast.LENGTH_SHORT).show();
            }
        });

        deleteWorkHistoryButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Confirmation");
            builder.setMessage("Are you sure you want to perform this action?");

            builder.setPositiveButton("Yes", (dialog, which) -> Executors.newSingleThreadExecutor().execute(() -> {
                appDatabase.workDao().deleteAll();
                // Use the Main thread to update the UI
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "Obrisani svi radovi.", Toast.LENGTH_SHORT).show();
                    loadWorks();
                    bottomSheetDialog.dismiss();
                });
            }));

            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        });

        bottomSheetDialog.show();
    }
 */