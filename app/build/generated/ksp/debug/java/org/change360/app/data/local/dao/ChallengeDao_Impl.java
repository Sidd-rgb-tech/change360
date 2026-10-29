package org.change360.app.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.EntityUpsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
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
import org.change360.app.data.local.entity.ChallengeEntity;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class ChallengeDao_Impl implements ChallengeDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ChallengeEntity> __insertionAdapterOfChallengeEntity;

  private final EntityDeletionOrUpdateAdapter<ChallengeEntity> __updateAdapterOfChallengeEntity;

  private final SharedSQLiteStatement __preparedStmtOfUpdateProgress;

  private final SharedSQLiteStatement __preparedStmtOfDeleteChallengeById;

  private final SharedSQLiteStatement __preparedStmtOfClearAllChallenges;

  private final EntityUpsertionAdapter<ChallengeEntity> __upsertionAdapterOfChallengeEntity;

  public ChallengeDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfChallengeEntity = new EntityInsertionAdapter<ChallengeEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `challenges` (`id`,`title`,`description`,`category`,`difficulty`,`xpReward`,`co2SavedKg`,`waterSavedLiters`,`wasteSavedKg`,`totalSteps`,`completedSteps`,`isCompleted`,`completedAtEpochMillis`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ChallengeEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getTitle());
        statement.bindString(3, entity.getDescription());
        statement.bindString(4, entity.getCategory());
        statement.bindString(5, entity.getDifficulty());
        statement.bindLong(6, entity.getXpReward());
        statement.bindDouble(7, entity.getCo2SavedKg());
        statement.bindDouble(8, entity.getWaterSavedLiters());
        statement.bindDouble(9, entity.getWasteSavedKg());
        statement.bindLong(10, entity.getTotalSteps());
        statement.bindLong(11, entity.getCompletedSteps());
        final int _tmp = entity.isCompleted() ? 1 : 0;
        statement.bindLong(12, _tmp);
        if (entity.getCompletedAtEpochMillis() == null) {
          statement.bindNull(13);
        } else {
          statement.bindLong(13, entity.getCompletedAtEpochMillis());
        }
      }
    };
    this.__updateAdapterOfChallengeEntity = new EntityDeletionOrUpdateAdapter<ChallengeEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `challenges` SET `id` = ?,`title` = ?,`description` = ?,`category` = ?,`difficulty` = ?,`xpReward` = ?,`co2SavedKg` = ?,`waterSavedLiters` = ?,`wasteSavedKg` = ?,`totalSteps` = ?,`completedSteps` = ?,`isCompleted` = ?,`completedAtEpochMillis` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ChallengeEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getTitle());
        statement.bindString(3, entity.getDescription());
        statement.bindString(4, entity.getCategory());
        statement.bindString(5, entity.getDifficulty());
        statement.bindLong(6, entity.getXpReward());
        statement.bindDouble(7, entity.getCo2SavedKg());
        statement.bindDouble(8, entity.getWaterSavedLiters());
        statement.bindDouble(9, entity.getWasteSavedKg());
        statement.bindLong(10, entity.getTotalSteps());
        statement.bindLong(11, entity.getCompletedSteps());
        final int _tmp = entity.isCompleted() ? 1 : 0;
        statement.bindLong(12, _tmp);
        if (entity.getCompletedAtEpochMillis() == null) {
          statement.bindNull(13);
        } else {
          statement.bindLong(13, entity.getCompletedAtEpochMillis());
        }
        statement.bindString(14, entity.getId());
      }
    };
    this.__preparedStmtOfUpdateProgress = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE challenges SET completedSteps = ?, isCompleted = ?, completedAtEpochMillis = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteChallengeById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM challenges WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfClearAllChallenges = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM challenges";
        return _query;
      }
    };
    this.__upsertionAdapterOfChallengeEntity = new EntityUpsertionAdapter<ChallengeEntity>(new EntityInsertionAdapter<ChallengeEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT INTO `challenges` (`id`,`title`,`description`,`category`,`difficulty`,`xpReward`,`co2SavedKg`,`waterSavedLiters`,`wasteSavedKg`,`totalSteps`,`completedSteps`,`isCompleted`,`completedAtEpochMillis`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ChallengeEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getTitle());
        statement.bindString(3, entity.getDescription());
        statement.bindString(4, entity.getCategory());
        statement.bindString(5, entity.getDifficulty());
        statement.bindLong(6, entity.getXpReward());
        statement.bindDouble(7, entity.getCo2SavedKg());
        statement.bindDouble(8, entity.getWaterSavedLiters());
        statement.bindDouble(9, entity.getWasteSavedKg());
        statement.bindLong(10, entity.getTotalSteps());
        statement.bindLong(11, entity.getCompletedSteps());
        final int _tmp = entity.isCompleted() ? 1 : 0;
        statement.bindLong(12, _tmp);
        if (entity.getCompletedAtEpochMillis() == null) {
          statement.bindNull(13);
        } else {
          statement.bindLong(13, entity.getCompletedAtEpochMillis());
        }
      }
    }, new EntityDeletionOrUpdateAdapter<ChallengeEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE `challenges` SET `id` = ?,`title` = ?,`description` = ?,`category` = ?,`difficulty` = ?,`xpReward` = ?,`co2SavedKg` = ?,`waterSavedLiters` = ?,`wasteSavedKg` = ?,`totalSteps` = ?,`completedSteps` = ?,`isCompleted` = ?,`completedAtEpochMillis` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ChallengeEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getTitle());
        statement.bindString(3, entity.getDescription());
        statement.bindString(4, entity.getCategory());
        statement.bindString(5, entity.getDifficulty());
        statement.bindLong(6, entity.getXpReward());
        statement.bindDouble(7, entity.getCo2SavedKg());
        statement.bindDouble(8, entity.getWaterSavedLiters());
        statement.bindDouble(9, entity.getWasteSavedKg());
        statement.bindLong(10, entity.getTotalSteps());
        statement.bindLong(11, entity.getCompletedSteps());
        final int _tmp = entity.isCompleted() ? 1 : 0;
        statement.bindLong(12, _tmp);
        if (entity.getCompletedAtEpochMillis() == null) {
          statement.bindNull(13);
        } else {
          statement.bindLong(13, entity.getCompletedAtEpochMillis());
        }
        statement.bindString(14, entity.getId());
      }
    });
  }

  @Override
  public Object insertChallenges(final List<ChallengeEntity> challenges,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfChallengeEntity.insert(challenges);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateChallenge(final ChallengeEntity challenge,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfChallengeEntity.handle(challenge);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateProgress(final String id, final int completedSteps, final boolean isCompleted,
      final Long completedAtEpochMillis, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateProgress.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, completedSteps);
        _argIndex = 2;
        final int _tmp = isCompleted ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
        _argIndex = 3;
        if (completedAtEpochMillis == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindLong(_argIndex, completedAtEpochMillis);
        }
        _argIndex = 4;
        _stmt.bindString(_argIndex, id);
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
          __preparedStmtOfUpdateProgress.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteChallengeById(final String id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteChallengeById.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, id);
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
          __preparedStmtOfDeleteChallengeById.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object clearAllChallenges(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearAllChallenges.acquire();
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
          __preparedStmtOfClearAllChallenges.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object upsertChallenge(final ChallengeEntity challenge,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __upsertionAdapterOfChallengeEntity.upsert(challenge);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object upsertChallenges(final List<ChallengeEntity> challenges,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __upsertionAdapterOfChallengeEntity.upsert(challenges);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<ChallengeEntity>> observeAllChallenges() {
    final String _sql = "SELECT * FROM challenges ORDER BY isCompleted ASC, xpReward DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"challenges"}, new Callable<List<ChallengeEntity>>() {
      @Override
      @NonNull
      public List<ChallengeEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfDifficulty = CursorUtil.getColumnIndexOrThrow(_cursor, "difficulty");
          final int _cursorIndexOfXpReward = CursorUtil.getColumnIndexOrThrow(_cursor, "xpReward");
          final int _cursorIndexOfCo2SavedKg = CursorUtil.getColumnIndexOrThrow(_cursor, "co2SavedKg");
          final int _cursorIndexOfWaterSavedLiters = CursorUtil.getColumnIndexOrThrow(_cursor, "waterSavedLiters");
          final int _cursorIndexOfWasteSavedKg = CursorUtil.getColumnIndexOrThrow(_cursor, "wasteSavedKg");
          final int _cursorIndexOfTotalSteps = CursorUtil.getColumnIndexOrThrow(_cursor, "totalSteps");
          final int _cursorIndexOfCompletedSteps = CursorUtil.getColumnIndexOrThrow(_cursor, "completedSteps");
          final int _cursorIndexOfIsCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "isCompleted");
          final int _cursorIndexOfCompletedAtEpochMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAtEpochMillis");
          final List<ChallengeEntity> _result = new ArrayList<ChallengeEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ChallengeEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpDifficulty;
            _tmpDifficulty = _cursor.getString(_cursorIndexOfDifficulty);
            final int _tmpXpReward;
            _tmpXpReward = _cursor.getInt(_cursorIndexOfXpReward);
            final float _tmpCo2SavedKg;
            _tmpCo2SavedKg = _cursor.getFloat(_cursorIndexOfCo2SavedKg);
            final float _tmpWaterSavedLiters;
            _tmpWaterSavedLiters = _cursor.getFloat(_cursorIndexOfWaterSavedLiters);
            final float _tmpWasteSavedKg;
            _tmpWasteSavedKg = _cursor.getFloat(_cursorIndexOfWasteSavedKg);
            final int _tmpTotalSteps;
            _tmpTotalSteps = _cursor.getInt(_cursorIndexOfTotalSteps);
            final int _tmpCompletedSteps;
            _tmpCompletedSteps = _cursor.getInt(_cursorIndexOfCompletedSteps);
            final boolean _tmpIsCompleted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsCompleted);
            _tmpIsCompleted = _tmp != 0;
            final Long _tmpCompletedAtEpochMillis;
            if (_cursor.isNull(_cursorIndexOfCompletedAtEpochMillis)) {
              _tmpCompletedAtEpochMillis = null;
            } else {
              _tmpCompletedAtEpochMillis = _cursor.getLong(_cursorIndexOfCompletedAtEpochMillis);
            }
            _item = new ChallengeEntity(_tmpId,_tmpTitle,_tmpDescription,_tmpCategory,_tmpDifficulty,_tmpXpReward,_tmpCo2SavedKg,_tmpWaterSavedLiters,_tmpWasteSavedKg,_tmpTotalSteps,_tmpCompletedSteps,_tmpIsCompleted,_tmpCompletedAtEpochMillis);
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
  public Flow<List<ChallengeEntity>> observeChallengesByCategory(final String category) {
    final String _sql = "SELECT * FROM challenges WHERE category = ? ORDER BY isCompleted ASC, xpReward DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, category);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"challenges"}, new Callable<List<ChallengeEntity>>() {
      @Override
      @NonNull
      public List<ChallengeEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfDifficulty = CursorUtil.getColumnIndexOrThrow(_cursor, "difficulty");
          final int _cursorIndexOfXpReward = CursorUtil.getColumnIndexOrThrow(_cursor, "xpReward");
          final int _cursorIndexOfCo2SavedKg = CursorUtil.getColumnIndexOrThrow(_cursor, "co2SavedKg");
          final int _cursorIndexOfWaterSavedLiters = CursorUtil.getColumnIndexOrThrow(_cursor, "waterSavedLiters");
          final int _cursorIndexOfWasteSavedKg = CursorUtil.getColumnIndexOrThrow(_cursor, "wasteSavedKg");
          final int _cursorIndexOfTotalSteps = CursorUtil.getColumnIndexOrThrow(_cursor, "totalSteps");
          final int _cursorIndexOfCompletedSteps = CursorUtil.getColumnIndexOrThrow(_cursor, "completedSteps");
          final int _cursorIndexOfIsCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "isCompleted");
          final int _cursorIndexOfCompletedAtEpochMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAtEpochMillis");
          final List<ChallengeEntity> _result = new ArrayList<ChallengeEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ChallengeEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpDifficulty;
            _tmpDifficulty = _cursor.getString(_cursorIndexOfDifficulty);
            final int _tmpXpReward;
            _tmpXpReward = _cursor.getInt(_cursorIndexOfXpReward);
            final float _tmpCo2SavedKg;
            _tmpCo2SavedKg = _cursor.getFloat(_cursorIndexOfCo2SavedKg);
            final float _tmpWaterSavedLiters;
            _tmpWaterSavedLiters = _cursor.getFloat(_cursorIndexOfWaterSavedLiters);
            final float _tmpWasteSavedKg;
            _tmpWasteSavedKg = _cursor.getFloat(_cursorIndexOfWasteSavedKg);
            final int _tmpTotalSteps;
            _tmpTotalSteps = _cursor.getInt(_cursorIndexOfTotalSteps);
            final int _tmpCompletedSteps;
            _tmpCompletedSteps = _cursor.getInt(_cursorIndexOfCompletedSteps);
            final boolean _tmpIsCompleted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsCompleted);
            _tmpIsCompleted = _tmp != 0;
            final Long _tmpCompletedAtEpochMillis;
            if (_cursor.isNull(_cursorIndexOfCompletedAtEpochMillis)) {
              _tmpCompletedAtEpochMillis = null;
            } else {
              _tmpCompletedAtEpochMillis = _cursor.getLong(_cursorIndexOfCompletedAtEpochMillis);
            }
            _item = new ChallengeEntity(_tmpId,_tmpTitle,_tmpDescription,_tmpCategory,_tmpDifficulty,_tmpXpReward,_tmpCo2SavedKg,_tmpWaterSavedLiters,_tmpWasteSavedKg,_tmpTotalSteps,_tmpCompletedSteps,_tmpIsCompleted,_tmpCompletedAtEpochMillis);
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
  public Flow<List<ChallengeEntity>> observeCompletedChallenges() {
    final String _sql = "SELECT * FROM challenges WHERE isCompleted = 1 ORDER BY completedAtEpochMillis DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"challenges"}, new Callable<List<ChallengeEntity>>() {
      @Override
      @NonNull
      public List<ChallengeEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfDifficulty = CursorUtil.getColumnIndexOrThrow(_cursor, "difficulty");
          final int _cursorIndexOfXpReward = CursorUtil.getColumnIndexOrThrow(_cursor, "xpReward");
          final int _cursorIndexOfCo2SavedKg = CursorUtil.getColumnIndexOrThrow(_cursor, "co2SavedKg");
          final int _cursorIndexOfWaterSavedLiters = CursorUtil.getColumnIndexOrThrow(_cursor, "waterSavedLiters");
          final int _cursorIndexOfWasteSavedKg = CursorUtil.getColumnIndexOrThrow(_cursor, "wasteSavedKg");
          final int _cursorIndexOfTotalSteps = CursorUtil.getColumnIndexOrThrow(_cursor, "totalSteps");
          final int _cursorIndexOfCompletedSteps = CursorUtil.getColumnIndexOrThrow(_cursor, "completedSteps");
          final int _cursorIndexOfIsCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "isCompleted");
          final int _cursorIndexOfCompletedAtEpochMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAtEpochMillis");
          final List<ChallengeEntity> _result = new ArrayList<ChallengeEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ChallengeEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpDifficulty;
            _tmpDifficulty = _cursor.getString(_cursorIndexOfDifficulty);
            final int _tmpXpReward;
            _tmpXpReward = _cursor.getInt(_cursorIndexOfXpReward);
            final float _tmpCo2SavedKg;
            _tmpCo2SavedKg = _cursor.getFloat(_cursorIndexOfCo2SavedKg);
            final float _tmpWaterSavedLiters;
            _tmpWaterSavedLiters = _cursor.getFloat(_cursorIndexOfWaterSavedLiters);
            final float _tmpWasteSavedKg;
            _tmpWasteSavedKg = _cursor.getFloat(_cursorIndexOfWasteSavedKg);
            final int _tmpTotalSteps;
            _tmpTotalSteps = _cursor.getInt(_cursorIndexOfTotalSteps);
            final int _tmpCompletedSteps;
            _tmpCompletedSteps = _cursor.getInt(_cursorIndexOfCompletedSteps);
            final boolean _tmpIsCompleted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsCompleted);
            _tmpIsCompleted = _tmp != 0;
            final Long _tmpCompletedAtEpochMillis;
            if (_cursor.isNull(_cursorIndexOfCompletedAtEpochMillis)) {
              _tmpCompletedAtEpochMillis = null;
            } else {
              _tmpCompletedAtEpochMillis = _cursor.getLong(_cursorIndexOfCompletedAtEpochMillis);
            }
            _item = new ChallengeEntity(_tmpId,_tmpTitle,_tmpDescription,_tmpCategory,_tmpDifficulty,_tmpXpReward,_tmpCo2SavedKg,_tmpWaterSavedLiters,_tmpWasteSavedKg,_tmpTotalSteps,_tmpCompletedSteps,_tmpIsCompleted,_tmpCompletedAtEpochMillis);
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
  public Flow<List<ChallengeEntity>> observeActiveChallenges() {
    final String _sql = "SELECT * FROM challenges WHERE isCompleted = 0 ORDER BY completedSteps DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"challenges"}, new Callable<List<ChallengeEntity>>() {
      @Override
      @NonNull
      public List<ChallengeEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfDifficulty = CursorUtil.getColumnIndexOrThrow(_cursor, "difficulty");
          final int _cursorIndexOfXpReward = CursorUtil.getColumnIndexOrThrow(_cursor, "xpReward");
          final int _cursorIndexOfCo2SavedKg = CursorUtil.getColumnIndexOrThrow(_cursor, "co2SavedKg");
          final int _cursorIndexOfWaterSavedLiters = CursorUtil.getColumnIndexOrThrow(_cursor, "waterSavedLiters");
          final int _cursorIndexOfWasteSavedKg = CursorUtil.getColumnIndexOrThrow(_cursor, "wasteSavedKg");
          final int _cursorIndexOfTotalSteps = CursorUtil.getColumnIndexOrThrow(_cursor, "totalSteps");
          final int _cursorIndexOfCompletedSteps = CursorUtil.getColumnIndexOrThrow(_cursor, "completedSteps");
          final int _cursorIndexOfIsCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "isCompleted");
          final int _cursorIndexOfCompletedAtEpochMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAtEpochMillis");
          final List<ChallengeEntity> _result = new ArrayList<ChallengeEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ChallengeEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpDifficulty;
            _tmpDifficulty = _cursor.getString(_cursorIndexOfDifficulty);
            final int _tmpXpReward;
            _tmpXpReward = _cursor.getInt(_cursorIndexOfXpReward);
            final float _tmpCo2SavedKg;
            _tmpCo2SavedKg = _cursor.getFloat(_cursorIndexOfCo2SavedKg);
            final float _tmpWaterSavedLiters;
            _tmpWaterSavedLiters = _cursor.getFloat(_cursorIndexOfWaterSavedLiters);
            final float _tmpWasteSavedKg;
            _tmpWasteSavedKg = _cursor.getFloat(_cursorIndexOfWasteSavedKg);
            final int _tmpTotalSteps;
            _tmpTotalSteps = _cursor.getInt(_cursorIndexOfTotalSteps);
            final int _tmpCompletedSteps;
            _tmpCompletedSteps = _cursor.getInt(_cursorIndexOfCompletedSteps);
            final boolean _tmpIsCompleted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsCompleted);
            _tmpIsCompleted = _tmp != 0;
            final Long _tmpCompletedAtEpochMillis;
            if (_cursor.isNull(_cursorIndexOfCompletedAtEpochMillis)) {
              _tmpCompletedAtEpochMillis = null;
            } else {
              _tmpCompletedAtEpochMillis = _cursor.getLong(_cursorIndexOfCompletedAtEpochMillis);
            }
            _item = new ChallengeEntity(_tmpId,_tmpTitle,_tmpDescription,_tmpCategory,_tmpDifficulty,_tmpXpReward,_tmpCo2SavedKg,_tmpWaterSavedLiters,_tmpWasteSavedKg,_tmpTotalSteps,_tmpCompletedSteps,_tmpIsCompleted,_tmpCompletedAtEpochMillis);
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
  public Flow<ChallengeEntity> observeChallengeById(final String id) {
    final String _sql = "SELECT * FROM challenges WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, id);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"challenges"}, new Callable<ChallengeEntity>() {
      @Override
      @Nullable
      public ChallengeEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfDifficulty = CursorUtil.getColumnIndexOrThrow(_cursor, "difficulty");
          final int _cursorIndexOfXpReward = CursorUtil.getColumnIndexOrThrow(_cursor, "xpReward");
          final int _cursorIndexOfCo2SavedKg = CursorUtil.getColumnIndexOrThrow(_cursor, "co2SavedKg");
          final int _cursorIndexOfWaterSavedLiters = CursorUtil.getColumnIndexOrThrow(_cursor, "waterSavedLiters");
          final int _cursorIndexOfWasteSavedKg = CursorUtil.getColumnIndexOrThrow(_cursor, "wasteSavedKg");
          final int _cursorIndexOfTotalSteps = CursorUtil.getColumnIndexOrThrow(_cursor, "totalSteps");
          final int _cursorIndexOfCompletedSteps = CursorUtil.getColumnIndexOrThrow(_cursor, "completedSteps");
          final int _cursorIndexOfIsCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "isCompleted");
          final int _cursorIndexOfCompletedAtEpochMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAtEpochMillis");
          final ChallengeEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpDifficulty;
            _tmpDifficulty = _cursor.getString(_cursorIndexOfDifficulty);
            final int _tmpXpReward;
            _tmpXpReward = _cursor.getInt(_cursorIndexOfXpReward);
            final float _tmpCo2SavedKg;
            _tmpCo2SavedKg = _cursor.getFloat(_cursorIndexOfCo2SavedKg);
            final float _tmpWaterSavedLiters;
            _tmpWaterSavedLiters = _cursor.getFloat(_cursorIndexOfWaterSavedLiters);
            final float _tmpWasteSavedKg;
            _tmpWasteSavedKg = _cursor.getFloat(_cursorIndexOfWasteSavedKg);
            final int _tmpTotalSteps;
            _tmpTotalSteps = _cursor.getInt(_cursorIndexOfTotalSteps);
            final int _tmpCompletedSteps;
            _tmpCompletedSteps = _cursor.getInt(_cursorIndexOfCompletedSteps);
            final boolean _tmpIsCompleted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsCompleted);
            _tmpIsCompleted = _tmp != 0;
            final Long _tmpCompletedAtEpochMillis;
            if (_cursor.isNull(_cursorIndexOfCompletedAtEpochMillis)) {
              _tmpCompletedAtEpochMillis = null;
            } else {
              _tmpCompletedAtEpochMillis = _cursor.getLong(_cursorIndexOfCompletedAtEpochMillis);
            }
            _result = new ChallengeEntity(_tmpId,_tmpTitle,_tmpDescription,_tmpCategory,_tmpDifficulty,_tmpXpReward,_tmpCo2SavedKg,_tmpWaterSavedLiters,_tmpWasteSavedKg,_tmpTotalSteps,_tmpCompletedSteps,_tmpIsCompleted,_tmpCompletedAtEpochMillis);
          } else {
            _result = null;
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
  public Object getChallengeById(final String id,
      final Continuation<? super ChallengeEntity> $completion) {
    final String _sql = "SELECT * FROM challenges WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<ChallengeEntity>() {
      @Override
      @Nullable
      public ChallengeEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfDifficulty = CursorUtil.getColumnIndexOrThrow(_cursor, "difficulty");
          final int _cursorIndexOfXpReward = CursorUtil.getColumnIndexOrThrow(_cursor, "xpReward");
          final int _cursorIndexOfCo2SavedKg = CursorUtil.getColumnIndexOrThrow(_cursor, "co2SavedKg");
          final int _cursorIndexOfWaterSavedLiters = CursorUtil.getColumnIndexOrThrow(_cursor, "waterSavedLiters");
          final int _cursorIndexOfWasteSavedKg = CursorUtil.getColumnIndexOrThrow(_cursor, "wasteSavedKg");
          final int _cursorIndexOfTotalSteps = CursorUtil.getColumnIndexOrThrow(_cursor, "totalSteps");
          final int _cursorIndexOfCompletedSteps = CursorUtil.getColumnIndexOrThrow(_cursor, "completedSteps");
          final int _cursorIndexOfIsCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "isCompleted");
          final int _cursorIndexOfCompletedAtEpochMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAtEpochMillis");
          final ChallengeEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpDifficulty;
            _tmpDifficulty = _cursor.getString(_cursorIndexOfDifficulty);
            final int _tmpXpReward;
            _tmpXpReward = _cursor.getInt(_cursorIndexOfXpReward);
            final float _tmpCo2SavedKg;
            _tmpCo2SavedKg = _cursor.getFloat(_cursorIndexOfCo2SavedKg);
            final float _tmpWaterSavedLiters;
            _tmpWaterSavedLiters = _cursor.getFloat(_cursorIndexOfWaterSavedLiters);
            final float _tmpWasteSavedKg;
            _tmpWasteSavedKg = _cursor.getFloat(_cursorIndexOfWasteSavedKg);
            final int _tmpTotalSteps;
            _tmpTotalSteps = _cursor.getInt(_cursorIndexOfTotalSteps);
            final int _tmpCompletedSteps;
            _tmpCompletedSteps = _cursor.getInt(_cursorIndexOfCompletedSteps);
            final boolean _tmpIsCompleted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsCompleted);
            _tmpIsCompleted = _tmp != 0;
            final Long _tmpCompletedAtEpochMillis;
            if (_cursor.isNull(_cursorIndexOfCompletedAtEpochMillis)) {
              _tmpCompletedAtEpochMillis = null;
            } else {
              _tmpCompletedAtEpochMillis = _cursor.getLong(_cursorIndexOfCompletedAtEpochMillis);
            }
            _result = new ChallengeEntity(_tmpId,_tmpTitle,_tmpDescription,_tmpCategory,_tmpDifficulty,_tmpXpReward,_tmpCo2SavedKg,_tmpWaterSavedLiters,_tmpWasteSavedKg,_tmpTotalSteps,_tmpCompletedSteps,_tmpIsCompleted,_tmpCompletedAtEpochMillis);
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

  @Override
  public Flow<Integer> observeCompletedCount() {
    final String _sql = "SELECT COUNT(*) FROM challenges WHERE isCompleted = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"challenges"}, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
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
  public Flow<Integer> observeTotalCount() {
    final String _sql = "SELECT COUNT(*) FROM challenges";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"challenges"}, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
