package com.example.mtondolo.journalapp.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface TaskDao {

    @Query("SELECT * FROM task ORDER BY entered_At")
    LiveData<List<TaskEntity>> loadAllTasks();

    @Insert
    void insertTask(TaskEntity taskEntity);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateTask(TaskEntity taskEntity);

    @Delete
    void deleteTask(TaskEntity taskEntity);

    @Query("SELECT * from task WHERE id = :id")
    LiveData<TaskEntity> loadTaskById(int id);
}
