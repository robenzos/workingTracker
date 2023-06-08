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

        EditText editText = dialogView.findViewById(R.id.editTextTime);
        editText.setText(inputtedText);

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

        TextInputEditText textInputEditText = bottomSheetView.findViewById(R.id.newPayPerHour);
        textInputEditText.setText(inputtedText);

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