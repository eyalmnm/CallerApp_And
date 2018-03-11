package com.em_projects.callerapp.storage.room;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.em_projects.callerapp.call_log.CallLogEntry;

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
    void removeAllMessages();

    @Query("delete from callLogTbl where uid in (:ids)")
    void removeSpecificBulkOfMessages(Long[] ids);

    @Query("delete from callLogTbl where uid in (:ids)")
    void removeSpecificBulkOfMessages(String[] ids);

    @Insert
    void insertAll(CallLogEntry... callLogEntries);

    @Delete
    void delete(CallLogEntry callLogEntry);
}
