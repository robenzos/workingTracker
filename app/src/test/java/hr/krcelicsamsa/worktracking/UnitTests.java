package hr.krcelicsamsa.worktracking;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;
import static hr.krcelicsamsa.worktracking.MainActivity.calculateTotalPay;
import static hr.krcelicsamsa.worktracking.MainActivity.convertToTime;
import static hr.krcelicsamsa.worktracking.MainActivity.isValidTimeFormat;
import static hr.krcelicsamsa.worktracking.MainActivity.convertTimeFormatToVisual;
//import static hr.krcelicsamsa.worktracking.MainActivityMock.showTimeInputDialogMock;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class UnitTests {
    private MainActivityMock activity;
    private TextAdapter adapter;

    @Before
    public void setup() {
        activity = Robolectric.buildActivity(MainActivityMock.class).create().resume().get();
        adapter = activity.getAdapter();
    }

    @After
    public void tearDown() {
        activity.finish();
    }

    //unit tests (only one module)
    @Test
    public void secondsToTimeIsCorrect() {
        assertEquals("01:34:30", convertToTime(5670));
    }

    @Test
    public void isValidTimeFormatCorrect() {
        assertTrue(isValidTimeFormat("01:34:30"));
    }

    @Test
    public void isNotValidTimeFormatCorrect() {
        assertFalse(isValidTimeFormat("01:342:30"));
    }

    @Test
    public void isConversionToVisualCorrect() {
        assertEquals("1h 34m", convertTimeFormatToVisual("01:34:30"));
    }

    //medium tests (retrieving and testing data from the database)
    @Test
    public void isTimeInputPopupEntryCorrect() {
        assertTrue(activity.showTimeInputDialogMock("01:34:30"));
    }

    @Test
    public void isTimeInputPopupEntryFalse() {
        assertFalse(activity.showTimeInputDialogMock("01:134:30"));
    }

    //end-to-end

}