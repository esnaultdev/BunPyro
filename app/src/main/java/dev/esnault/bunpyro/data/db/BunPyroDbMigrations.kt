package dev.esnault.bunpyro.data.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase


/**
 * Migration from 1 to 2.
 * The review table was using the id as a primary key and storing normal
 * and ghost reviews in the same table. It was assumed that review ids are
 * unique across all review types, which isn't the case.
 *
 * Two solutions were available:
 * - Split review into normal_review and ghost_review. This also means that review_history
 *   needs to either point to one of the two, or also be split into normal_review_history
 *   and ghost_review_history
 * - Make the (id, review_type) a composite primary key for the review table
 *
 * It's the second solution that was chosen as having to maintain duplicated tables, entities
 * and DAOs didn't sound right.
 * This might not be the best solution if the normal and ghost reviews change very differently
 * in the future, but YAGNI.
 */
val migration_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Using SQLite, we need to drop a table and recreate it to update its primary key

        // Create `review_tmp` with the right primary key
        database.execSQL("CREATE TABLE `review_tmp` (`id` INTEGER NOT NULL, `type` INTEGER NOT NULL, `grammar_id` INTEGER NOT NULL, `created_at` INTEGER NOT NULL, `updated_at` INTEGER NOT NULL, `next_review` INTEGER NOT NULL, `last_studied_at` INTEGER, PRIMARY KEY(`id`, `type`))")
        // Copy `review` to `review_tmp`
        database.execSQL("INSERT INTO `review_tmp` SELECT * FROM `review`")

        // Create `review_history_tmp` with the right primary key and foreign key
        database.execSQL("CREATE TABLE `review_history_tmp` (`question_id` INTEGER NOT NULL, `time` INTEGER NOT NULL, `status` INTEGER NOT NULL, `attempts` INTEGER NOT NULL, `streak` INTEGER NOT NULL, `history_index` INTEGER NOT NULL, `review_id` INTEGER NOT NULL, `review_type` INTEGER NOT NULL, PRIMARY KEY(`history_index`, `review_id`, `review_type`), FOREIGN KEY(`review_id`, `review_type`) REFERENCES `review_tmp`(`id`, `type`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        // Copy `review_history to `review_history_tmp`
        // Since `review_history` did not contain the review type, we need to fetch it from `review`
        database.execSQL("INSERT INTO `review_history_tmp` SELECT `question_id`, `time`, `status`, `attempts`, `streak`, `history_index`, `review_id`, `review.type` FROM `review_history` INNER JOIN `review` ON `review_history.review_id` = `review.id`")

        // Drop `review_history`
        // This also deletes the `index_review_history_review_id` index
        database.execSQL("DROP TABLE IF EXISTS `review_history`")
        // Drop `review`
        database.execSQL("DROP TABLE IF EXISTS `review`")

        // Rename `review_history_tmp` to `review_history`
        database.execSQL("ALTER TABLE `review_history_tmp` RENAME TO `review_history`")
        // Rename `review_tmp` to `review`
        database.execSQL("ALTER TABLE `review_tmp` RENAME TO `review`")

        // Create the `review_history` index for the foreign key
        database.execSQL("CREATE INDEX IF NOT EXISTS `index_review_history_review_id_review_type` ON `review_history` (`review_id`, `review_type`)")
    }
}

val bunPyroDbMigrations = arrayOf(migration_1_2)
