package com.example.mtondolo.journalapp.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

/**
 * Immutable model class for a TaskEntity.
 */
@Entity(tableName = "task")
public class TaskEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String description;
    @ColumnInfo(name = "entered_At")
    private Date enteredAt;

    /**
     * Use this constructor to create a new active TaskEntity.
     *
     * @param title       title of the task
     * @param description description of the task
     * @param enteredAt   date of entering or updating the task
     */
    @Ignore
    public TaskEntity(String title, String description, Date enteredAt) {
        this.title = title;
        this.description = description;
        this.enteredAt = enteredAt;
    }

    /**
     * Use this constructor to create an active TaskEntity if the TaskEntity already has an id (copy of another
     * TaskEntity).
     *
     * @param title       title of the task
     * @param description description of the task
     * @param enteredAt   date of entering or updating the task
     * @param id          id of the task
     */
    public TaskEntity(int id, String title, String description, Date enteredAt) {
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
