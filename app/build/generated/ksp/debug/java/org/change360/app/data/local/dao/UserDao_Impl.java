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
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;
import org.change360.app.data.local.entity.UserEntity;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class UserDao_Impl implements UserDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<UserEntity> __insertionAdapterOfUserEntity;

  private final EntityDeletionOrUpdateAdapter<UserEntity> __updateAdapterOfUserEntity;

  private final SharedSQLiteStatement __preparedStmtOfIncrementXpAndStreak;

  private final SharedSQLiteStatement __preparedStmtOfUpdateStreak;

  private final SharedSQLiteStatement __preparedStmtOfUpdateAuraLevel;

  private final SharedSQLiteStatement __preparedStmtOfDeleteUserById;

  private final SharedSQLiteStatement __preparedStmtOfClearUsers;

  private final EntityUpsertionAdapter<UserEntity> __upsertionAdapterOfUserEntity;

  public UserDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfUserEntity = new EntityInsertionAdapter<UserEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `users` (`id`,`displayName`,`email`,`ecoPledge`,`auraLevel`,`xpPoints`,`currentStreakDays`,`createdAtEpochMillis`) VALUES (?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final UserEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getDisplayName());
        statement.bindString(3, entity.getEmail());
        statement.bindString(4, entity.getEcoPledge());
        statement.bindLong(5, entity.getAuraLevel());
        statement.bindLong(6, entity.getXpPoints());
        statement.bindLong(7, entity.getCurrentStreakDays());
        statement.bindLong(8, entity.getCreatedAtEpochMillis());
      }
    };
    this.__updateAdapterOfUserEntity = new EntityDeletionOrUpdateAdapter<UserEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `users` SET `id` = ?,`displayName` = ?,`email` = ?,`ecoPledge` = ?,`auraLevel` = ?,`xpPoints` = ?,`currentStreakDays` = ?,`createdAtEpochMillis` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final UserEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getDisplayName());
        statement.bindString(3, entity.getEmail());
        statement.bindString(4, entity.getEcoPledge());
        statement.bindLong(5, entity.getAuraLevel());
        statement.bindLong(6, entity.getXpPoints());
        statement.bindLong(7, entity.getCurrentStreakDays());
        statement.bindLong(8, entity.getCreatedAtEpochMillis());
        statement.bindString(9, entity.getId());
      }
    };
    this.__preparedStmtOfIncrementXpAndStreak = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE users SET xpPoints = xpPoints + ?, auraLevel = ((xpPoints + ?) / 500) + 1, currentStreakDays = currentStreakDays + ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateStreak = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE users SET currentStreakDays = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateAuraLevel = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE users SET auraLevel = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteUserById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM users WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfClearUsers = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM users";
        return _query;
      }
    };
    this.__upsertionAdapterOfUserEntity = new EntityUpsertionAdapter<UserEntity>(new EntityInsertionAdapter<UserEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT INTO `users` (`id`,`displayName`,`email`,`ecoPledge`,`auraLevel`,`xpPoints`,`currentStreakDays`,`createdAtEpochMillis`) VALUES (?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final UserEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getDisplayName());
        statement.bindString(3, entity.getEmail());
        statement.bindString(4, entity.getEcoPledge());
        statement.bindLong(5, entity.getAuraLevel());
        statement.bindLong(6, entity.getXpPoints());
        statement.bindLong(7, entity.getCurrentStreakDays());
        statement.bindLong(8, entity.getCreatedAtEpochMillis());
      }
    }, new EntityDeletionOrUpdateAdapter<UserEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE `users` SET `id` = ?,`displayName` = ?,`email` = ?,`ecoPledge` = ?,`auraLevel` = ?,`xpPoints` = ?,`currentStreakDays` = ?,`createdAtEpochMillis` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final UserEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getDisplayName());
        statement.bindString(3, entity.getEmail());
        statement.bindString(4, entity.getEcoPledge());
        statement.bindLong(5, entity.getAuraLevel());
        statement.bindLong(6, entity.getXpPoints());
        statement.bindLong(7, entity.getCurrentStreakDays());
        statement.bindLong(8, entity.getCreatedAtEpochMillis());
        statement.bindString(9, entity.getId());
      }
    });
  }

  @Override
  public Object insertUser(final UserEntity user, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfUserEntity.insert(user);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateUser(final UserEntity user, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfUserEntity.handle(user);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object incrementXpAndStreak(final String userId, final int xpEarned,
      final int streakIncrement, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfIncrementXpAndStreak.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, xpEarned);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, xpEarned);
        _argIndex = 3;
        _stmt.bindLong(_argIndex, streakIncrement);
        _argIndex = 4;
        _stmt.bindString(_argIndex, userId);
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
          __preparedStmtOfIncrementXpAndStreak.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateStreak(final String userId, final int streakDays,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateStreak.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, streakDays);
        _argIndex = 2;
        _stmt.bindString(_argIndex, userId);
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
          __preparedStmtOfUpdateStreak.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateAuraLevel(final String userId, final int level,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateAuraLevel.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, level);
        _argIndex = 2;
        _stmt.bindString(_argIndex, userId);
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
          __preparedStmtOfUpdateAuraLevel.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteUserById(final String userId, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteUserById.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, userId);
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
          __preparedStmtOfDeleteUserById.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object clearUsers(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearUsers.acquire();
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
          __preparedStmtOfClearUsers.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object upsertUser(final UserEntity user, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __upsertionAdapterOfUserEntity.upsert(user);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<UserEntity> observeActiveUser() {
    final String _sql = "SELECT * FROM users LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"users"}, new Callable<UserEntity>() {
      @Override
      @Nullable
      public UserEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDisplayName = CursorUtil.getColumnIndexOrThrow(_cursor, "displayName");
          final int _cursorIndexOfEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "email");
          final int _cursorIndexOfEcoPledge = CursorUtil.getColumnIndexOrThrow(_cursor, "ecoPledge");
          final int _cursorIndexOfAuraLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "auraLevel");
          final int _cursorIndexOfXpPoints = CursorUtil.getColumnIndexOrThrow(_cursor, "xpPoints");
          final int _cursorIndexOfCurrentStreakDays = CursorUtil.getColumnIndexOrThrow(_cursor, "currentStreakDays");
          final int _cursorIndexOfCreatedAtEpochMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAtEpochMillis");
          final UserEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpDisplayName;
            _tmpDisplayName = _cursor.getString(_cursorIndexOfDisplayName);
            final String _tmpEmail;
            _tmpEmail = _cursor.getString(_cursorIndexOfEmail);
            final String _tmpEcoPledge;
            _tmpEcoPledge = _cursor.getString(_cursorIndexOfEcoPledge);
            final int _tmpAuraLevel;
            _tmpAuraLevel = _cursor.getInt(_cursorIndexOfAuraLevel);
            final int _tmpXpPoints;
            _tmpXpPoints = _cursor.getInt(_cursorIndexOfXpPoints);
            final int _tmpCurrentStreakDays;
            _tmpCurrentStreakDays = _cursor.getInt(_cursorIndexOfCurrentStreakDays);
            final long _tmpCreatedAtEpochMillis;
            _tmpCreatedAtEpochMillis = _cursor.getLong(_cursorIndexOfCreatedAtEpochMillis);
            _result = new UserEntity(_tmpId,_tmpDisplayName,_tmpEmail,_tmpEcoPledge,_tmpAuraLevel,_tmpXpPoints,_tmpCurrentStreakDays,_tmpCreatedAtEpochMillis);
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
  public Flow<UserEntity> observeUserById(final String userId) {
    final String _sql = "SELECT * FROM users WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, userId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"users"}, new Callable<UserEntity>() {
      @Override
      @Nullable
      public UserEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDisplayName = CursorUtil.getColumnIndexOrThrow(_cursor, "displayName");
          final int _cursorIndexOfEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "email");
          final int _cursorIndexOfEcoPledge = CursorUtil.getColumnIndexOrThrow(_cursor, "ecoPledge");
          final int _cursorIndexOfAuraLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "auraLevel");
          final int _cursorIndexOfXpPoints = CursorUtil.getColumnIndexOrThrow(_cursor, "xpPoints");
          final int _cursorIndexOfCurrentStreakDays = CursorUtil.getColumnIndexOrThrow(_cursor, "currentStreakDays");
          final int _cursorIndexOfCreatedAtEpochMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAtEpochMillis");
          final UserEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpDisplayName;
            _tmpDisplayName = _cursor.getString(_cursorIndexOfDisplayName);
            final String _tmpEmail;
            _tmpEmail = _cursor.getString(_cursorIndexOfEmail);
            final String _tmpEcoPledge;
            _tmpEcoPledge = _cursor.getString(_cursorIndexOfEcoPledge);
            final int _tmpAuraLevel;
            _tmpAuraLevel = _cursor.getInt(_cursorIndexOfAuraLevel);
            final int _tmpXpPoints;
            _tmpXpPoints = _cursor.getInt(_cursorIndexOfXpPoints);
            final int _tmpCurrentStreakDays;
            _tmpCurrentStreakDays = _cursor.getInt(_cursorIndexOfCurrentStreakDays);
            final long _tmpCreatedAtEpochMillis;
            _tmpCreatedAtEpochMillis = _cursor.getLong(_cursorIndexOfCreatedAtEpochMillis);
            _result = new UserEntity(_tmpId,_tmpDisplayName,_tmpEmail,_tmpEcoPledge,_tmpAuraLevel,_tmpXpPoints,_tmpCurrentStreakDays,_tmpCreatedAtEpochMillis);
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
  public Object getUserById(final String userId,
      final Continuation<? super UserEntity> $completion) {
    final String _sql = "SELECT * FROM users WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, userId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<UserEntity>() {
      @Override
      @Nullable
      public UserEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDisplayName = CursorUtil.getColumnIndexOrThrow(_cursor, "displayName");
          final int _cursorIndexOfEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "email");
          final int _cursorIndexOfEcoPledge = CursorUtil.getColumnIndexOrThrow(_cursor, "ecoPledge");
          final int _cursorIndexOfAuraLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "auraLevel");
          final int _cursorIndexOfXpPoints = CursorUtil.getColumnIndexOrThrow(_cursor, "xpPoints");
          final int _cursorIndexOfCurrentStreakDays = CursorUtil.getColumnIndexOrThrow(_cursor, "currentStreakDays");
          final int _cursorIndexOfCreatedAtEpochMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAtEpochMillis");
          final UserEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpDisplayName;
            _tmpDisplayName = _cursor.getString(_cursorIndexOfDisplayName);
            final String _tmpEmail;
            _tmpEmail = _cursor.getString(_cursorIndexOfEmail);
            final String _tmpEcoPledge;
            _tmpEcoPledge = _cursor.getString(_cursorIndexOfEcoPledge);
            final int _tmpAuraLevel;
            _tmpAuraLevel = _cursor.getInt(_cursorIndexOfAuraLevel);
            final int _tmpXpPoints;
            _tmpXpPoints = _cursor.getInt(_cursorIndexOfXpPoints);
            final int _tmpCurrentStreakDays;
            _tmpCurrentStreakDays = _cursor.getInt(_cursorIndexOfCurrentStreakDays);
            final long _tmpCreatedAtEpochMillis;
            _tmpCreatedAtEpochMillis = _cursor.getLong(_cursorIndexOfCreatedAtEpochMillis);
            _result = new UserEntity(_tmpId,_tmpDisplayName,_tmpEmail,_tmpEcoPledge,_tmpAuraLevel,_tmpXpPoints,_tmpCurrentStreakDays,_tmpCreatedAtEpochMillis);
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
  public Object getActiveUser(final Continuation<? super UserEntity> $completion) {
    final String _sql = "SELECT * FROM users LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<UserEntity>() {
      @Override
      @Nullable
      public UserEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDisplayName = CursorUtil.getColumnIndexOrThrow(_cursor, "displayName");
          final int _cursorIndexOfEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "email");
          final int _cursorIndexOfEcoPledge = CursorUtil.getColumnIndexOrThrow(_cursor, "ecoPledge");
          final int _cursorIndexOfAuraLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "auraLevel");
          final int _cursorIndexOfXpPoints = CursorUtil.getColumnIndexOrThrow(_cursor, "xpPoints");
          final int _cursorIndexOfCurrentStreakDays = CursorUtil.getColumnIndexOrThrow(_cursor, "currentStreakDays");
          final int _cursorIndexOfCreatedAtEpochMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAtEpochMillis");
          final UserEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpDisplayName;
            _tmpDisplayName = _cursor.getString(_cursorIndexOfDisplayName);
            final String _tmpEmail;
            _tmpEmail = _cursor.getString(_cursorIndexOfEmail);
            final String _tmpEcoPledge;
            _tmpEcoPledge = _cursor.getString(_cursorIndexOfEcoPledge);
            final int _tmpAuraLevel;
            _tmpAuraLevel = _cursor.getInt(_cursorIndexOfAuraLevel);
            final int _tmpXpPoints;
            _tmpXpPoints = _cursor.getInt(_cursorIndexOfXpPoints);
            final int _tmpCurrentStreakDays;
            _tmpCurrentStreakDays = _cursor.getInt(_cursorIndexOfCurrentStreakDays);
            final long _tmpCreatedAtEpochMillis;
            _tmpCreatedAtEpochMillis = _cursor.getLong(_cursorIndexOfCreatedAtEpochMillis);
            _result = new UserEntity(_tmpId,_tmpDisplayName,_tmpEmail,_tmpEcoPledge,_tmpAuraLevel,_tmpXpPoints,_tmpCurrentStreakDays,_tmpCreatedAtEpochMillis);
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
