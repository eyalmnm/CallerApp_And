package com.em_projects.callerapp.storage.room;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by eyalmuchtar on 3/11/18.
 */

@Dao
public interface CallLogDbDao {

    @Query("SELECT * FROM call_log")
    List<CallLogDbEntety> getAll();

    @Query("SELECT COUNT(*) from call_log")
    int countCallLogsEntries();

    @Query("delete from call_log")
    void removeAllCallLogEntries();

    @Query("delete from call_log where uid in (:ids)")
    void removeSpecificBulkOfCallLogEntries(Long[] ids);

    @Query("delete from call_log where uid in (:ids)")
    void removeSpecificBulkOfCallLogEntries(String[] ids);

    @Insert
    void insertAll(CallLogDbEntety... callLogDbEntries);

    @Insert
    public void insertAll(List<CallLogDbEntety> callLogDbEnteties);

    @Delete
    void delete(CallLogDbEntety callLogDbEntety);
}
