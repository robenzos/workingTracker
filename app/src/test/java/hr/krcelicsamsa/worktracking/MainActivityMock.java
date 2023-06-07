package hr.krcelicsamsa.worktracking;
import static hr.krcelicsamsa.worktracking.MainActivity.isValidTimeFormat;
import android.content.Context;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivityMock extends AppCompatActivity {
    TextAdapter adapter;

    public TextAdapter getAdapter() {
        return adapter;
    }

    public boolean showTimeInputDialogMock(String inputtedText) {
        EditText editText = new EditText(MainActivityMock.this);
        editText.setText(inputtedText);

        if (isValidTimeFormat(editText.getText().toString())) {
            return true;
        } else {
            return false;
        }
    }

}

/*
    private void showTimeInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.time_input_dialog, null);
        builder.setView(dialogView);

        final EditText editTextTime = dialogView.findViewById(R.id.editTextTime);

        builder.setTitle("Enter time:");
        builder.setPositiveButton("OK", (dialog, which) -> {
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
 */