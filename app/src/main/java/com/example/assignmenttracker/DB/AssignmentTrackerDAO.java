package com.example.assignmenttracker.DB;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.assignmenttracker.AssignmentTracker;
import java.util.List;

/**
 * @author Carlos Santiago, Fernando A. Pulido
 * @since April 27, 2023
 * Description: Defines a DAO interface for accessing the AppDataBase.
 */

@Dao
public interface AssignmentTrackerDAO {
    // Methods - Take on one or more instances of the AssignmentTracker class as parameters
                // and performs appropriate database operations on them.
    @Insert
    void insert(AssignmentTracker... assignmentTrackers);

    @Update
    void update(AssignmentTracker... assignmentTrackers);

    @Delete
    void delete(AssignmentTracker assignmentTracker);

    // Custom methods using the @Query annotation
    @Query("SELECT * FROM " + AppDataBase.ASSIGNMENTTRACKER_TABLE)
    List<AssignmentTracker> getAssignmentTrackers(); // returns list of AssignmentTracker objects
                                                        // in the database.

    @Query("SELECT * FROM " + AppDataBase.ASSIGNMENTTRACKER_TABLE + " WHERE trackerId = :trackerId")
    List<AssignmentTracker> getTrackerById(int trackerId); // takes integer parameter and returns
                                                            // list of AssignmentTracker objects
                                                            // with matching trackerId field.
}
