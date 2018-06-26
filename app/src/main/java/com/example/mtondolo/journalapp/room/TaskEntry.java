package com.example.mtondolo.journalapp.room;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

/**
 * Immutable model class for a TaskEntry.
 */
@Entity(tableName = "task")
public class TaskEntry {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String description;
    @ColumnInfo(name = "entered_At")
    private Date enteredAt;

    /**
     * Use this constructor to create a new active TaskEntry.
     * @param title       title of the task
     * @param description description of the task
     * @param enteredAt  date of entering or updating the task
     */
   @Ignore
    public TaskEntry(String title, String description, Date enteredAt) {
        this.title = title;
        this.description = description;
        this.enteredAt = enteredAt;
    }

    /**
     * Use this constructor to create an active TaskEntry if the TaskEntry already has an id (copy of another
     * TaskEntry).
     * @param title       title of the task
     * @param description description of the task
     * @param enteredAt   date of entering or updating the task
     * @param id          id of the task
     */
    public TaskEntry(int id, String title, String description, Date enteredAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.enteredAt = enteredAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getEnteredAt() {
        return enteredAt;
    }

    public void setEnteredAt(Date enteredAt) {
        this.enteredAt = enteredAt;
    }
}
