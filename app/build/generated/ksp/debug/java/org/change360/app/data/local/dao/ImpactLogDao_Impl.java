package org.change360.app.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Float;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;
import org.change360.app.data.local.entity.ImpactLogEntity;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class ImpactLogDao_Impl implements ImpactLogDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ImpactLogEntity> __insertionAdapterOfImpactLogEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteLogById;

  private final SharedSQLiteStatement __preparedStmtOfClearAllLogs;

  public ImpactLogDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfImpactLogEntity = new EntityInsertionAdapter<ImpactLogEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `impact_logs` (`id`,`userId`,`sourceChallengeId`,`actionTitle`,`co2SavedKg`,`waterSavedLiters`,`wasteSavedKg`,`loggedAtEpochMillis`) VALUES (nullif(?, 0),?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ImpactLogEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getUserId());
        if (entity.getSourceChallengeId() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getSourceChallengeId());
        }
        statement.bindString(4, entity.getActionTitle());
        statement.bindDouble(5, entity.getCo2SavedKg());
        statement.bindDouble(6, entity.getWaterSavedLiters());
        statement.bindDouble(7, entity.getWasteSavedKg());
        statement.bindLong(8, entity.getLoggedAtEpochMillis());
      }
    };
    this.__preparedStmtOfDeleteLogById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM impact_logs WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfClearAllLogs = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM impact_logs";
        return _query;
      }
    };
  }

  @Override
  public Object insertImpactLog(final ImpactLogEntity log,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfImpactLogEntity.insertAndReturnId(log);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertImpactLogs(final List<ImpactLogEntity> logs,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfImpactLogEntity.insert(logs);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteLogById(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteLogById.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteLogById.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object clearAllLogs(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearAllLogs.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfClearAllLogs.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<ImpactLogEntity>> observeAllLogs() {
    final String _sql = "SELECT * FROM impact_logs ORDER BY loggedAtEpochMillis DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"impact_logs"}, new Callable<List<ImpactLogEntity>>() {
      @Override
      @NonNull
      public List<ImpactLogEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfSourceChallengeId = CursorUtil.getColumnIndexOrThrow(_cursor, "sourceChallengeId");
          final int _cursorIndexOfActionTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "actionTitle");
          final int _cursorIndexOfCo2SavedKg = CursorUtil.getColumnIndexOrThrow(_cursor, "co2SavedKg");
          final int _cursorIndexOfWaterSavedLiters = CursorUtil.getColumnIndexOrThrow(_cursor, "waterSavedLiters");
          final int _cursorIndexOfWasteSavedKg = CursorUtil.getColumnIndexOrThrow(_cursor, "wasteSavedKg");
          final int _cursorIndexOfLoggedAtEpochMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "loggedAtEpochMillis");
          final List<ImpactLogEntity> _result = new ArrayList<ImpactLogEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ImpactLogEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpUserId;
            _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            final String _tmpSourceChallengeId;
            if (_cursor.isNull(_cursorIndexOfSourceChallengeId)) {
              _tmpSourceChallengeId = null;
            } else {
              _tmpSourceChallengeId = _cursor.getString(_cursorIndexOfSourceChallengeId);
            }
            final String _tmpActionTitle;
            _tmpActionTitle = _cursor.getString(_cursorIndexOfActionTitle);
            final float _tmpCo2SavedKg;
            _tmpCo2SavedKg = _cursor.getFloat(_cursorIndexOfCo2SavedKg);
            final float _tmpWaterSavedLiters;
            _tmpWaterSavedLiters = _cursor.getFloat(_cursorIndexOfWaterSavedLiters);
            final float _tmpWasteSavedKg;
            _tmpWasteSavedKg = _cursor.getFloat(_cursorIndexOfWasteSavedKg);
            final long _tmpLoggedAtEpochMillis;
            _tmpLoggedAtEpochMillis = _cursor.getLong(_cursorIndexOfLoggedAtEpochMillis);
            _item = new ImpactLogEntity(_tmpId,_tmpUserId,_tmpSourceChallengeId,_tmpActionTitle,_tmpCo2SavedKg,_tmpWaterSavedLiters,_tmpWasteSavedKg,_tmpLoggedAtEpochMillis);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<ImpactLogEntity>> observeLogsForUser(final String userId) {
    final String _sql = "SELECT * FROM impact_logs WHERE userId = ? ORDER BY loggedAtEpochMillis DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, userId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"impact_logs"}, new Callable<List<ImpactLogEntity>>() {
      @Override
      @NonNull
      public List<ImpactLogEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfSourceChallengeId = CursorUtil.getColumnIndexOrThrow(_cursor, "sourceChallengeId");
          final int _cursorIndexOfActionTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "actionTitle");
          final int _cursorIndexOfCo2SavedKg = CursorUtil.getColumnIndexOrThrow(_cursor, "co2SavedKg");
          final int _cursorIndexOfWaterSavedLiters = CursorUtil.getColumnIndexOrThrow(_cursor, "waterSavedLiters");
          final int _cursorIndexOfWasteSavedKg = CursorUtil.getColumnIndexOrThrow(_cursor, "wasteSavedKg");
          final int _cursorIndexOfLoggedAtEpochMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "loggedAtEpochMillis");
          final List<ImpactLogEntity> _result = new ArrayList<ImpactLogEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ImpactLogEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpUserId;
            _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            final String _tmpSourceChallengeId;
            if (_cursor.isNull(_cursorIndexOfSourceChallengeId)) {
              _tmpSourceChallengeId = null;
            } else {
              _tmpSourceChallengeId = _cursor.getString(_cursorIndexOfSourceChallengeId);
            }
            final String _tmpActionTitle;
            _tmpActionTitle = _cursor.getString(_cursorIndexOfActionTitle);
            final float _tmpCo2SavedKg;
            _tmpCo2SavedKg = _cursor.getFloat(_cursorIndexOfCo2SavedKg);
            final float _tmpWaterSavedLiters;
            _tmpWaterSavedLiters = _cursor.getFloat(_cursorIndexOfWaterSavedLiters);
            final float _tmpWasteSavedKg;
            _tmpWasteSavedKg = _cursor.getFloat(_cursorIndexOfWasteSavedKg);
            final long _tmpLoggedAtEpochMillis;
            _tmpLoggedAtEpochMillis = _cursor.getLong(_cursorIndexOfLoggedAtEpochMillis);
            _item = new ImpactLogEntity(_tmpId,_tmpUserId,_tmpSourceChallengeId,_tmpActionTitle,_tmpCo2SavedKg,_tmpWaterSavedLiters,_tmpWasteSavedKg,_tmpLoggedAtEpochMillis);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<ImpactLogEntity>> observeLogsSince(final long sinceEpochMillis) {
    final String _sql = "SELECT * FROM impact_logs WHERE loggedAtEpochMillis >= ? ORDER BY loggedAtEpochMillis DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, sinceEpochMillis);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"impact_logs"}, new Callable<List<ImpactLogEntity>>() {
      @Override
      @NonNull
      public List<ImpactLogEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfSourceChallengeId = CursorUtil.getColumnIndexOrThrow(_cursor, "sourceChallengeId");
          final int _cursorIndexOfActionTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "actionTitle");
          final int _cursorIndexOfCo2SavedKg = CursorUtil.getColumnIndexOrThrow(_cursor, "co2SavedKg");
          final int _cursorIndexOfWaterSavedLiters = CursorUtil.getColumnIndexOrThrow(_cursor, "waterSavedLiters");
          final int _cursorIndexOfWasteSavedKg = CursorUtil.getColumnIndexOrThrow(_cursor, "wasteSavedKg");
          final int _cursorIndexOfLoggedAtEpochMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "loggedAtEpochMillis");
          final List<ImpactLogEntity> _result = new ArrayList<ImpactLogEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ImpactLogEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpUserId;
            _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            final String _tmpSourceChallengeId;
            if (_cursor.isNull(_cursorIndexOfSourceChallengeId)) {
              _tmpSourceChallengeId = null;
            } else {
              _tmpSourceChallengeId = _cursor.getString(_cursorIndexOfSourceChallengeId);
            }
            final String _tmpActionTitle;
            _tmpActionTitle = _cursor.getString(_cursorIndexOfActionTitle);
            final float _tmpCo2SavedKg;
            _tmpCo2SavedKg = _cursor.getFloat(_cursorIndexOfCo2SavedKg);
            final float _tmpWaterSavedLiters;
            _tmpWaterSavedLiters = _cursor.getFloat(_cursorIndexOfWaterSavedLiters);
            final float _tmpWasteSavedKg;
            _tmpWasteSavedKg = _cursor.getFloat(_cursorIndexOfWasteSavedKg);
            final long _tmpLoggedAtEpochMillis;
            _tmpLoggedAtEpochMillis = _cursor.getLong(_cursorIndexOfLoggedAtEpochMillis);
            _item = new ImpactLogEntity(_tmpId,_tmpUserId,_tmpSourceChallengeId,_tmpActionTitle,_tmpCo2SavedKg,_tmpWaterSavedLiters,_tmpWasteSavedKg,_tmpLoggedAtEpochMillis);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<ImpactLogEntity>> observeLogsForUserSince(final String userId,
      final long sinceEpochMillis) {
    final String _sql = "SELECT * FROM impact_logs WHERE userId = ? AND loggedAtEpochMillis >= ? ORDER BY loggedAtEpochMillis DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, userId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, sinceEpochMillis);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"impact_logs"}, new Callable<List<ImpactLogEntity>>() {
      @Override
      @NonNull
      public List<ImpactLogEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfSourceChallengeId = CursorUtil.getColumnIndexOrThrow(_cursor, "sourceChallengeId");
          final int _cursorIndexOfActionTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "actionTitle");
          final int _cursorIndexOfCo2SavedKg = CursorUtil.getColumnIndexOrThrow(_cursor, "co2SavedKg");
          final int _cursorIndexOfWaterSavedLiters = CursorUtil.getColumnIndexOrThrow(_cursor, "waterSavedLiters");
          final int _cursorIndexOfWasteSavedKg = CursorUtil.getColumnIndexOrThrow(_cursor, "wasteSavedKg");
          final int _cursorIndexOfLoggedAtEpochMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "loggedAtEpochMillis");
          final List<ImpactLogEntity> _result = new ArrayList<ImpactLogEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ImpactLogEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpUserId;
            _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            final String _tmpSourceChallengeId;
            if (_cursor.isNull(_cursorIndexOfSourceChallengeId)) {
              _tmpSourceChallengeId = null;
            } else {
              _tmpSourceChallengeId = _cursor.getString(_cursorIndexOfSourceChallengeId);
            }
            final String _tmpActionTitle;
            _tmpActionTitle = _cursor.getString(_cursorIndexOfActionTitle);
            final float _tmpCo2SavedKg;
            _tmpCo2SavedKg = _cursor.getFloat(_cursorIndexOfCo2SavedKg);
            final float _tmpWaterSavedLiters;
            _tmpWaterSavedLiters = _cursor.getFloat(_cursorIndexOfWaterSavedLiters);
            final float _tmpWasteSavedKg;
            _tmpWasteSavedKg = _cursor.getFloat(_cursorIndexOfWasteSavedKg);
            final long _tmpLoggedAtEpochMillis;
            _tmpLoggedAtEpochMillis = _cursor.getLong(_cursorIndexOfLoggedAtEpochMillis);
            _item = new ImpactLogEntity(_tmpId,_tmpUserId,_tmpSourceChallengeId,_tmpActionTitle,_tmpCo2SavedKg,_tmpWaterSavedLiters,_tmpWasteSavedKg,_tmpLoggedAtEpochMillis);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<Float> observeTotalCo2Saved() {
    final String _sql = "SELECT COALESCE(SUM(co2SavedKg), 0.0) FROM impact_logs";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"impact_logs"}, new Callable<Float>() {
      @Override
      @NonNull
      public Float call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Float _result;
          if (_cursor.moveToFirst()) {
            final float _tmp;
            _tmp = _cursor.getFloat(0);
            _result = _tmp;
          } else {
            _result = 0f;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<Float> observeTotalWaterSaved() {
    final String _sql = "SELECT COALESCE(SUM(waterSavedLiters), 0.0) FROM impact_logs";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"impact_logs"}, new Callable<Float>() {
      @Override
      @NonNull
      public Float call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Float _result;
          if (_cursor.moveToFirst()) {
            final float _tmp;
            _tmp = _cursor.getFloat(0);
            _result = _tmp;
          } else {
            _result = 0f;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<Float> observeTotalWasteSaved() {
    final String _sql = "SELECT COALESCE(SUM(wasteSavedKg), 0.0) FROM impact_logs";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"impact_logs"}, new Callable<Float>() {
      @Override
      @NonNull
      public Float call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Float _result;
          if (_cursor.moveToFirst()) {
            final float _tmp;
            _tmp = _cursor.getFloat(0);
            _result = _tmp;
          } else {
            _result = 0f;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<Float> observeCo2SavedSince(final long sinceEpochMillis) {
    final String _sql = "SELECT COALESCE(SUM(co2SavedKg), 0.0) FROM impact_logs WHERE loggedAtEpochMillis >= ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, sinceEpochMillis);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"impact_logs"}, new Callable<Float>() {
      @Override
      @NonNull
      public Float call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Float _result;
          if (_cursor.moveToFirst()) {
            final float _tmp;
            _tmp = _cursor.getFloat(0);
            _result = _tmp;
          } else {
            _result = 0f;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<Float> observeWaterSavedSince(final long sinceEpochMillis) {
    final String _sql = "SELECT COALESCE(SUM(waterSavedLiters), 0.0) FROM impact_logs WHERE loggedAtEpochMillis >= ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, sinceEpochMillis);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"impact_logs"}, new Callable<Float>() {
      @Override
      @NonNull
      public Float call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Float _result;
          if (_cursor.moveToFirst()) {
            final float _tmp;
            _tmp = _cursor.getFloat(0);
            _result = _tmp;
          } else {
            _result = 0f;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<Float> observeWasteSavedSince(final long sinceEpochMillis) {
    final String _sql = "SELECT COALESCE(SUM(wasteSavedKg), 0.0) FROM impact_logs WHERE loggedAtEpochMillis >= ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, sinceEpochMillis);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"impact_logs"}, new Callable<Float>() {
      @Override
      @NonNull
      public Float call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Float _result;
          if (_cursor.moveToFirst()) {
            final float _tmp;
            _tmp = _cursor.getFloat(0);
            _result = _tmp;
          } else {
            _result = 0f;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getLogByChallengeId(final String challengeId,
      final Continuation<? super ImpactLogEntity> $completion) {
    final String _sql = "SELECT * FROM impact_logs WHERE sourceChallengeId = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, challengeId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<ImpactLogEntity>() {
      @Override
      @Nullable
      public ImpactLogEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfSourceChallengeId = CursorUtil.getColumnIndexOrThrow(_cursor, "sourceChallengeId");
          final int _cursorIndexOfActionTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "actionTitle");
          final int _cursorIndexOfCo2SavedKg = CursorUtil.getColumnIndexOrThrow(_cursor, "co2SavedKg");
          final int _cursorIndexOfWaterSavedLiters = CursorUtil.getColumnIndexOrThrow(_cursor, "waterSavedLiters");
          final int _cursorIndexOfWasteSavedKg = CursorUtil.getColumnIndexOrThrow(_cursor, "wasteSavedKg");
          final int _cursorIndexOfLoggedAtEpochMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "loggedAtEpochMillis");
          final ImpactLogEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpUserId;
            _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            final String _tmpSourceChallengeId;
            if (_cursor.isNull(_cursorIndexOfSourceChallengeId)) {
              _tmpSourceChallengeId = null;
            } else {
              _tmpSourceChallengeId = _cursor.getString(_cursorIndexOfSourceChallengeId);
            }
            final String _tmpActionTitle;
            _tmpActionTitle = _cursor.getString(_cursorIndexOfActionTitle);
            final float _tmpCo2SavedKg;
            _tmpCo2SavedKg = _cursor.getFloat(_cursorIndexOfCo2SavedKg);
            final float _tmpWaterSavedLiters;
            _tmpWaterSavedLiters = _cursor.getFloat(_cursorIndexOfWaterSavedLiters);
            final float _tmpWasteSavedKg;
            _tmpWasteSavedKg = _cursor.getFloat(_cursorIndexOfWasteSavedKg);
            final long _tmpLoggedAtEpochMillis;
            _tmpLoggedAtEpochMillis = _cursor.getLong(_cursorIndexOfLoggedAtEpochMillis);
            _result = new ImpactLogEntity(_tmpId,_tmpUserId,_tmpSourceChallengeId,_tmpActionTitle,_tmpCo2SavedKg,_tmpWaterSavedLiters,_tmpWasteSavedKg,_tmpLoggedAtEpochMillis);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
