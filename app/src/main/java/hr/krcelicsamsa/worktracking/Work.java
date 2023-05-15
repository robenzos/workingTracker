package hr.krcelicsamsa.worktracking;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.LocalDateTime;

@Entity
public class Work {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "seconds worked")
    public int secondsWorked;

    @ColumnInfo(name = "pay per hour")
    public double payPerHour;

    @ColumnInfo(name = "date")
    public LocalDateTime date;
}
