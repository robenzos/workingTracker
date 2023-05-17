package hr.krcelicsamsa.worktracking;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UserSettingsDao {
    @Insert
    void insert(UserSettings userSettings);

    @Query("SELECT * FROM UserSettings")
    List<UserSettings> getAll();
}
