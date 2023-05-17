package hr.krcelicsamsa.worktracking;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class UserSettings {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "pay per hour")
    public double payPerHour;
}
