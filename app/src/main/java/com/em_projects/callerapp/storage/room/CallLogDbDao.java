package com.em_projects.callerapp.storage.room;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.em_projects.callerapp.call_log.CallLogEntry;

import java.util.Collection;
import java.util.List;

/**
 * Created by eyalmuchtar on 3/11/18.
 */

@Dao
public interface CallLogDbDao {

    @Query("SELECT * FROM callLogTbl")
    List<CallLogDbEntety> getAll();

    @Query("SELECT COUNT(*) from callLogTbl")
    int countCallLogsEntries();

    @Query("delete from callLogTbl")
    void removeAllCallLogEntries();

    @Query("delete from callLogTbl where uid in (:ids)")
    void removeSpecificBulkOfCallLogEntries(Long[] ids);

    @Query("delete from callLogTbl where uid in (:ids)")
    void removeSpecificBulkOfCallLogEntries(String[] ids);

    @Insert
    void insertAll(CallLogEntry... callLogEntries);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertCallLogs(Collection<CallLogDbEntety> callLogDbEnteties);

    @Delete
    void delete(CallLogEntry callLogEntry);
}
