package hr.krcelicsamsa.worktracking;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface WorkDao {
    @Insert
    void insert(Work work);

    @Query("SELECT * FROM Work")
    List<Work> getAll();
}
