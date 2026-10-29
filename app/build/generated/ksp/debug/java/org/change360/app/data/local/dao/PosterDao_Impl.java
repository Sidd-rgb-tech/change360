package org.change360.app.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
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
import org.change360.app.data.local.entity.PosterEntity;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class PosterDao_Impl implements PosterDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<PosterEntity> __insertionAdapterOfPosterEntity;

  private final EntityDeletionOrUpdateAdapter<PosterEntity> __updateAdapterOfPosterEntity;

  private final SharedSQLiteStatement __preparedStmtOfUpdateExportedUri;

  private final SharedSQLiteStatement __preparedStmtOfDeletePosterById;

  private final SharedSQLiteStatement __preparedStmtOfClearAllPosters;

  public PosterDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfPosterEntity = new EntityInsertionAdapter<PosterEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `posters` (`id`,`title`,`pledgeQuote`,`templateStyle`,`accentColorHex`,`exportedUriString`,`createdAtEpochMillis`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final PosterEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getTitle());
        statement.bindString(3, entity.getPledgeQuote());
        statement.bindString(4, entity.getTemplateStyle());
        statement.bindString(5, entity.getAccentColorHex());
        if (entity.getExportedUriString() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getExportedUriString());
        }
        statement.bindLong(7, entity.getCreatedAtEpochMillis());
      }
    };
    this.__updateAdapterOfPosterEntity = new EntityDeletionOrUpdateAdapter<PosterEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `posters` SET `id` = ?,`title` = ?,`pledgeQuote` = ?,`templateStyle` = ?,`accentColorHex` = ?,`exportedUriString` = ?,`createdAtEpochMillis` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final PosterEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getTitle());
        statement.bindString(3, entity.getPledgeQuote());
        statement.bindString(4, entity.getTemplateStyle());
        statement.bindString(5, entity.getAccentColorHex());
        if (entity.getExportedUriString() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getExportedUriString());
        }
        statement.bindLong(7, entity.getCreatedAtEpochMillis());
        statement.bindLong(8, entity.getId());
      }
    };
    this.__preparedStmtOfUpdateExportedUri = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE posters SET exportedUriString = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeletePosterById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM posters WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfClearAllPosters = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM posters";
        return _query;
      }
    };
  }

  @Override
  public Object insertPoster(final PosterEntity poster,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfPosterEntity.insertAndReturnId(poster);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertPosters(final List<PosterEntity> posters,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfPosterEntity.insert(posters);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updatePoster(final PosterEntity poster,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfPosterEntity.handle(poster);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateExportedUri(final long id, final String exportedUriString,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateExportedUri.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, exportedUriString);
        _argIndex = 2;
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
          __preparedStmtOfUpdateExportedUri.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deletePosterById(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeletePosterById.acquire();
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
          __preparedStmtOfDeletePosterById.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object clearAllPosters(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearAllPosters.acquire();
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
          __preparedStmtOfClearAllPosters.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<PosterEntity>> observeSavedPosters() {
    final String _sql = "SELECT * FROM posters ORDER BY createdAtEpochMillis DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"posters"}, new Callable<List<PosterEntity>>() {
      @Override
      @NonNull
      public List<PosterEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfPledgeQuote = CursorUtil.getColumnIndexOrThrow(_cursor, "pledgeQuote");
          final int _cursorIndexOfTemplateStyle = CursorUtil.getColumnIndexOrThrow(_cursor, "templateStyle");
          final int _cursorIndexOfAccentColorHex = CursorUtil.getColumnIndexOrThrow(_cursor, "accentColorHex");
          final int _cursorIndexOfExportedUriString = CursorUtil.getColumnIndexOrThrow(_cursor, "exportedUriString");
          final int _cursorIndexOfCreatedAtEpochMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAtEpochMillis");
          final List<PosterEntity> _result = new ArrayList<PosterEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PosterEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpPledgeQuote;
            _tmpPledgeQuote = _cursor.getString(_cursorIndexOfPledgeQuote);
            final String _tmpTemplateStyle;
            _tmpTemplateStyle = _cursor.getString(_cursorIndexOfTemplateStyle);
            final String _tmpAccentColorHex;
            _tmpAccentColorHex = _cursor.getString(_cursorIndexOfAccentColorHex);
            final String _tmpExportedUriString;
            if (_cursor.isNull(_cursorIndexOfExportedUriString)) {
              _tmpExportedUriString = null;
            } else {
              _tmpExportedUriString = _cursor.getString(_cursorIndexOfExportedUriString);
            }
            final long _tmpCreatedAtEpochMillis;
            _tmpCreatedAtEpochMillis = _cursor.getLong(_cursorIndexOfCreatedAtEpochMillis);
            _item = new PosterEntity(_tmpId,_tmpTitle,_tmpPledgeQuote,_tmpTemplateStyle,_tmpAccentColorHex,_tmpExportedUriString,_tmpCreatedAtEpochMillis);
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
  public Flow<PosterEntity> observePosterById(final long id) {
    final String _sql = "SELECT * FROM posters WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"posters"}, new Callable<PosterEntity>() {
      @Override
      @Nullable
      public PosterEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfPledgeQuote = CursorUtil.getColumnIndexOrThrow(_cursor, "pledgeQuote");
          final int _cursorIndexOfTemplateStyle = CursorUtil.getColumnIndexOrThrow(_cursor, "templateStyle");
          final int _cursorIndexOfAccentColorHex = CursorUtil.getColumnIndexOrThrow(_cursor, "accentColorHex");
          final int _cursorIndexOfExportedUriString = CursorUtil.getColumnIndexOrThrow(_cursor, "exportedUriString");
          final int _cursorIndexOfCreatedAtEpochMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAtEpochMillis");
          final PosterEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpPledgeQuote;
            _tmpPledgeQuote = _cursor.getString(_cursorIndexOfPledgeQuote);
            final String _tmpTemplateStyle;
            _tmpTemplateStyle = _cursor.getString(_cursorIndexOfTemplateStyle);
            final String _tmpAccentColorHex;
            _tmpAccentColorHex = _cursor.getString(_cursorIndexOfAccentColorHex);
            final String _tmpExportedUriString;
            if (_cursor.isNull(_cursorIndexOfExportedUriString)) {
              _tmpExportedUriString = null;
            } else {
              _tmpExportedUriString = _cursor.getString(_cursorIndexOfExportedUriString);
            }
            final long _tmpCreatedAtEpochMillis;
            _tmpCreatedAtEpochMillis = _cursor.getLong(_cursorIndexOfCreatedAtEpochMillis);
            _result = new PosterEntity(_tmpId,_tmpTitle,_tmpPledgeQuote,_tmpTemplateStyle,_tmpAccentColorHex,_tmpExportedUriString,_tmpCreatedAtEpochMillis);
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
  public Object getPosterById(final long id, final Continuation<? super PosterEntity> $completion) {
    final String _sql = "SELECT * FROM posters WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<PosterEntity>() {
      @Override
      @Nullable
      public PosterEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfPledgeQuote = CursorUtil.getColumnIndexOrThrow(_cursor, "pledgeQuote");
          final int _cursorIndexOfTemplateStyle = CursorUtil.getColumnIndexOrThrow(_cursor, "templateStyle");
          final int _cursorIndexOfAccentColorHex = CursorUtil.getColumnIndexOrThrow(_cursor, "accentColorHex");
          final int _cursorIndexOfExportedUriString = CursorUtil.getColumnIndexOrThrow(_cursor, "exportedUriString");
          final int _cursorIndexOfCreatedAtEpochMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAtEpochMillis");
          final PosterEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpPledgeQuote;
            _tmpPledgeQuote = _cursor.getString(_cursorIndexOfPledgeQuote);
            final String _tmpTemplateStyle;
            _tmpTemplateStyle = _cursor.getString(_cursorIndexOfTemplateStyle);
            final String _tmpAccentColorHex;
            _tmpAccentColorHex = _cursor.getString(_cursorIndexOfAccentColorHex);
            final String _tmpExportedUriString;
            if (_cursor.isNull(_cursorIndexOfExportedUriString)) {
              _tmpExportedUriString = null;
            } else {
              _tmpExportedUriString = _cursor.getString(_cursorIndexOfExportedUriString);
            }
            final long _tmpCreatedAtEpochMillis;
            _tmpCreatedAtEpochMillis = _cursor.getLong(_cursorIndexOfCreatedAtEpochMillis);
            _result = new PosterEntity(_tmpId,_tmpTitle,_tmpPledgeQuote,_tmpTemplateStyle,_tmpAccentColorHex,_tmpExportedUriString,_tmpCreatedAtEpochMillis);
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
