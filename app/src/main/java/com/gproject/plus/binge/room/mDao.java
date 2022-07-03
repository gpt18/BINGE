package com.gproject.plus.binge.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface mDao {

    @Insert
    void insertRecord(mEntity moviesEntity);

    @Query("SELECT EXISTS(SELECT * FROM mEntity WHERE m_key = :key)")
    int isDataExist(String key);

    @Query("SELECT * FROM mEntity")
    List<mEntity> getAllMovies();

    @Query("DELETE FROM mEntity WHERE m_key = :key")
    void deleteByKey(String key);
}
