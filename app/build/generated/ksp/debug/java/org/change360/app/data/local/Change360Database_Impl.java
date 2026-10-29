package org.change360.app.data.local;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;
import org.change360.app.data.local.dao.ChallengeDao;
import org.change360.app.data.local.dao.ChallengeDao_Impl;
import org.change360.app.data.local.dao.ChatMessageDao;
import org.change360.app.data.local.dao.ChatMessageDao_Impl;
import org.change360.app.data.local.dao.ImpactLogDao;
import org.change360.app.data.local.dao.ImpactLogDao_Impl;
import org.change360.app.data.local.dao.PosterDao;
import org.change360.app.data.local.dao.PosterDao_Impl;
import org.change360.app.data.local.dao.UserDao;
import org.change360.app.data.local.dao.UserDao_Impl;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class Change360Database_Impl extends Change360Database {
  private volatile UserDao _userDao;

  private volatile ChallengeDao _challengeDao;

  private volatile ImpactLogDao _impactLogDao;

  private volatile ChatMessageDao _chatMessageDao;

  private volatile PosterDao _posterDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `users` (`id` TEXT NOT NULL, `displayName` TEXT NOT NULL, `email` TEXT NOT NULL, `ecoPledge` TEXT NOT NULL, `auraLevel` INTEGER NOT NULL, `xpPoints` INTEGER NOT NULL, `currentStreakDays` INTEGER NOT NULL, `createdAtEpochMillis` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `challenges` (`id` TEXT NOT NULL, `title` TEXT NOT NULL, `description` TEXT NOT NULL, `category` TEXT NOT NULL, `difficulty` TEXT NOT NULL, `xpReward` INTEGER NOT NULL, `co2SavedKg` REAL NOT NULL, `waterSavedLiters` REAL NOT NULL, `wasteSavedKg` REAL NOT NULL, `totalSteps` INTEGER NOT NULL, `completedSteps` INTEGER NOT NULL, `isCompleted` INTEGER NOT NULL, `completedAtEpochMillis` INTEGER, PRIMARY KEY(`id`))");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_challenges_category` ON `challenges` (`category`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_challenges_isCompleted` ON `challenges` (`isCompleted`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `impact_logs` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `userId` TEXT NOT NULL, `sourceChallengeId` TEXT, `actionTitle` TEXT NOT NULL, `co2SavedKg` REAL NOT NULL, `waterSavedLiters` REAL NOT NULL, `wasteSavedKg` REAL NOT NULL, `loggedAtEpochMillis` INTEGER NOT NULL)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_impact_logs_userId` ON `impact_logs` (`userId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_impact_logs_loggedAtEpochMillis` ON `impact_logs` (`loggedAtEpochMillis`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `chat_messages` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `sender` TEXT NOT NULL, `content` TEXT NOT NULL, `suggestedActionCategory` TEXT, `timestampEpochMillis` INTEGER NOT NULL)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_chat_messages_timestampEpochMillis` ON `chat_messages` (`timestampEpochMillis`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `posters` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `title` TEXT NOT NULL, `pledgeQuote` TEXT NOT NULL, `templateStyle` TEXT NOT NULL, `accentColorHex` TEXT NOT NULL, `exportedUriString` TEXT, `createdAtEpochMillis` INTEGER NOT NULL)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_posters_createdAtEpochMillis` ON `posters` (`createdAtEpochMillis`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '611146e48de9f9b9955c6d6afd40fd6d')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `users`");
        db.execSQL("DROP TABLE IF EXISTS `challenges`");
        db.execSQL("DROP TABLE IF EXISTS `impact_logs`");
        db.execSQL("DROP TABLE IF EXISTS `chat_messages`");
        db.execSQL("DROP TABLE IF EXISTS `posters`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsUsers = new HashMap<String, TableInfo.Column>(8);
        _columnsUsers.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("displayName", new TableInfo.Column("displayName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("email", new TableInfo.Column("email", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("ecoPledge", new TableInfo.Column("ecoPledge", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("auraLevel", new TableInfo.Column("auraLevel", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("xpPoints", new TableInfo.Column("xpPoints", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("currentStreakDays", new TableInfo.Column("currentStreakDays", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("createdAtEpochMillis", new TableInfo.Column("createdAtEpochMillis", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysUsers = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesUsers = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoUsers = new TableInfo("users", _columnsUsers, _foreignKeysUsers, _indicesUsers);
        final TableInfo _existingUsers = TableInfo.read(db, "users");
        if (!_infoUsers.equals(_existingUsers)) {
          return new RoomOpenHelper.ValidationResult(false, "users(org.change360.app.data.local.entity.UserEntity).\n"
                  + " Expected:\n" + _infoUsers + "\n"
                  + " Found:\n" + _existingUsers);
        }
        final HashMap<String, TableInfo.Column> _columnsChallenges = new HashMap<String, TableInfo.Column>(13);
        _columnsChallenges.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChallenges.put("title", new TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChallenges.put("description", new TableInfo.Column("description", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChallenges.put("category", new TableInfo.Column("category", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChallenges.put("difficulty", new TableInfo.Column("difficulty", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChallenges.put("xpReward", new TableInfo.Column("xpReward", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChallenges.put("co2SavedKg", new TableInfo.Column("co2SavedKg", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChallenges.put("waterSavedLiters", new TableInfo.Column("waterSavedLiters", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChallenges.put("wasteSavedKg", new TableInfo.Column("wasteSavedKg", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChallenges.put("totalSteps", new TableInfo.Column("totalSteps", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChallenges.put("completedSteps", new TableInfo.Column("completedSteps", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChallenges.put("isCompleted", new TableInfo.Column("isCompleted", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChallenges.put("completedAtEpochMillis", new TableInfo.Column("completedAtEpochMillis", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysChallenges = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesChallenges = new HashSet<TableInfo.Index>(2);
        _indicesChallenges.add(new TableInfo.Index("index_challenges_category", false, Arrays.asList("category"), Arrays.asList("ASC")));
        _indicesChallenges.add(new TableInfo.Index("index_challenges_isCompleted", false, Arrays.asList("isCompleted"), Arrays.asList("ASC")));
        final TableInfo _infoChallenges = new TableInfo("challenges", _columnsChallenges, _foreignKeysChallenges, _indicesChallenges);
        final TableInfo _existingChallenges = TableInfo.read(db, "challenges");
        if (!_infoChallenges.equals(_existingChallenges)) {
          return new RoomOpenHelper.ValidationResult(false, "challenges(org.change360.app.data.local.entity.ChallengeEntity).\n"
                  + " Expected:\n" + _infoChallenges + "\n"
                  + " Found:\n" + _existingChallenges);
        }
        final HashMap<String, TableInfo.Column> _columnsImpactLogs = new HashMap<String, TableInfo.Column>(8);
        _columnsImpactLogs.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsImpactLogs.put("userId", new TableInfo.Column("userId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsImpactLogs.put("sourceChallengeId", new TableInfo.Column("sourceChallengeId", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsImpactLogs.put("actionTitle", new TableInfo.Column("actionTitle", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsImpactLogs.put("co2SavedKg", new TableInfo.Column("co2SavedKg", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsImpactLogs.put("waterSavedLiters", new TableInfo.Column("waterSavedLiters", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsImpactLogs.put("wasteSavedKg", new TableInfo.Column("wasteSavedKg", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsImpactLogs.put("loggedAtEpochMillis", new TableInfo.Column("loggedAtEpochMillis", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysImpactLogs = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesImpactLogs = new HashSet<TableInfo.Index>(2);
        _indicesImpactLogs.add(new TableInfo.Index("index_impact_logs_userId", false, Arrays.asList("userId"), Arrays.asList("ASC")));
        _indicesImpactLogs.add(new TableInfo.Index("index_impact_logs_loggedAtEpochMillis", false, Arrays.asList("loggedAtEpochMillis"), Arrays.asList("ASC")));
        final TableInfo _infoImpactLogs = new TableInfo("impact_logs", _columnsImpactLogs, _foreignKeysImpactLogs, _indicesImpactLogs);
        final TableInfo _existingImpactLogs = TableInfo.read(db, "impact_logs");
        if (!_infoImpactLogs.equals(_existingImpactLogs)) {
          return new RoomOpenHelper.ValidationResult(false, "impact_logs(org.change360.app.data.local.entity.ImpactLogEntity).\n"
                  + " Expected:\n" + _infoImpactLogs + "\n"
                  + " Found:\n" + _existingImpactLogs);
        }
        final HashMap<String, TableInfo.Column> _columnsChatMessages = new HashMap<String, TableInfo.Column>(5);
        _columnsChatMessages.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChatMessages.put("sender", new TableInfo.Column("sender", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChatMessages.put("content", new TableInfo.Column("content", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChatMessages.put("suggestedActionCategory", new TableInfo.Column("suggestedActionCategory", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChatMessages.put("timestampEpochMillis", new TableInfo.Column("timestampEpochMillis", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysChatMessages = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesChatMessages = new HashSet<TableInfo.Index>(1);
        _indicesChatMessages.add(new TableInfo.Index("index_chat_messages_timestampEpochMillis", false, Arrays.asList("timestampEpochMillis"), Arrays.asList("ASC")));
        final TableInfo _infoChatMessages = new TableInfo("chat_messages", _columnsChatMessages, _foreignKeysChatMessages, _indicesChatMessages);
        final TableInfo _existingChatMessages = TableInfo.read(db, "chat_messages");
        if (!_infoChatMessages.equals(_existingChatMessages)) {
          return new RoomOpenHelper.ValidationResult(false, "chat_messages(org.change360.app.data.local.entity.ChatMessageEntity).\n"
                  + " Expected:\n" + _infoChatMessages + "\n"
                  + " Found:\n" + _existingChatMessages);
        }
        final HashMap<String, TableInfo.Column> _columnsPosters = new HashMap<String, TableInfo.Column>(7);
        _columnsPosters.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPosters.put("title", new TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPosters.put("pledgeQuote", new TableInfo.Column("pledgeQuote", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPosters.put("templateStyle", new TableInfo.Column("templateStyle", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPosters.put("accentColorHex", new TableInfo.Column("accentColorHex", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPosters.put("exportedUriString", new TableInfo.Column("exportedUriString", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPosters.put("createdAtEpochMillis", new TableInfo.Column("createdAtEpochMillis", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysPosters = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesPosters = new HashSet<TableInfo.Index>(1);
        _indicesPosters.add(new TableInfo.Index("index_posters_createdAtEpochMillis", false, Arrays.asList("createdAtEpochMillis"), Arrays.asList("ASC")));
        final TableInfo _infoPosters = new TableInfo("posters", _columnsPosters, _foreignKeysPosters, _indicesPosters);
        final TableInfo _existingPosters = TableInfo.read(db, "posters");
        if (!_infoPosters.equals(_existingPosters)) {
          return new RoomOpenHelper.ValidationResult(false, "posters(org.change360.app.data.local.entity.PosterEntity).\n"
                  + " Expected:\n" + _infoPosters + "\n"
                  + " Found:\n" + _existingPosters);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "611146e48de9f9b9955c6d6afd40fd6d", "031df1bde87638d19348f594bbfbe498");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "users","challenges","impact_logs","chat_messages","posters");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `users`");
      _db.execSQL("DELETE FROM `challenges`");
      _db.execSQL("DELETE FROM `impact_logs`");
      _db.execSQL("DELETE FROM `chat_messages`");
      _db.execSQL("DELETE FROM `posters`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(UserDao.class, UserDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ChallengeDao.class, ChallengeDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ImpactLogDao.class, ImpactLogDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ChatMessageDao.class, ChatMessageDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(PosterDao.class, PosterDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public UserDao userDao() {
    if (_userDao != null) {
      return _userDao;
    } else {
      synchronized(this) {
        if(_userDao == null) {
          _userDao = new UserDao_Impl(this);
        }
        return _userDao;
      }
    }
  }

  @Override
  public ChallengeDao challengeDao() {
    if (_challengeDao != null) {
      return _challengeDao;
    } else {
      synchronized(this) {
        if(_challengeDao == null) {
          _challengeDao = new ChallengeDao_Impl(this);
        }
        return _challengeDao;
      }
    }
  }

  @Override
  public ImpactLogDao impactLogDao() {
    if (_impactLogDao != null) {
      return _impactLogDao;
    } else {
      synchronized(this) {
        if(_impactLogDao == null) {
          _impactLogDao = new ImpactLogDao_Impl(this);
        }
        return _impactLogDao;
      }
    }
  }

  @Override
  public ChatMessageDao chatMessageDao() {
    if (_chatMessageDao != null) {
      return _chatMessageDao;
    } else {
      synchronized(this) {
        if(_chatMessageDao == null) {
          _chatMessageDao = new ChatMessageDao_Impl(this);
        }
        return _chatMessageDao;
      }
    }
  }

  @Override
  public PosterDao posterDao() {
    if (_posterDao != null) {
      return _posterDao;
    } else {
      synchronized(this) {
        if(_posterDao == null) {
          _posterDao = new PosterDao_Impl(this);
        }
        return _posterDao;
      }
    }
  }
}
