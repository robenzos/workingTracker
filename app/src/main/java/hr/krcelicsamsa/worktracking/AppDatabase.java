package hr.krcelicsamsa.worktracking;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {Work.class, UserSettings.class}, version = 2)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract WorkDao workDao();
    public abstract UserSettingsDao userSettingsDao();
}