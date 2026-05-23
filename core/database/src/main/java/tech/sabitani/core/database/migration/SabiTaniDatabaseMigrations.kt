package tech.sabitani.core.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

internal val MIGRATION_1_2 =
    object : Migration(startVersion = 1, endVersion = 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS chat_messages (
                    id TEXT NOT NULL PRIMARY KEY,
                    role TEXT NOT NULL,
                    text TEXT NOT NULL,
                    createdAtEpochMillis INTEGER NOT NULL
                )
                """.trimIndent(),
            )
            db.execSQL(
                "CREATE INDEX IF NOT EXISTS index_chat_messages_createdAtEpochMillis " +
                    "ON chat_messages(createdAtEpochMillis)",
            )
        }
    }

internal val SABITANI_DATABASE_MIGRATIONS = arrayOf(MIGRATION_1_2)
