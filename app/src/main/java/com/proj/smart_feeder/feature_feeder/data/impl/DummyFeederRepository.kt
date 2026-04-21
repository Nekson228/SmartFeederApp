<<<<<<<< Updated upstream:app/src/main/java/com/proj/smart_feeder/feature_feeder/data/impl/DummyFeederRepository.kt
package com.proj.smart_feeder.feature_feeder.data.impl

import android.os.Build
import androidx.annotation.RequiresApi
import com.proj.smart_feeder.feature_feeder.data.repository.FeederRepository
import com.proj.smart_feeder.feature_feeder.ui.FeederState
========
package com.proj.smart_feeder.feature_feeder.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.proj.smart_feeder.feature_feeder.data.FeederState
>>>>>>>> Stashed changes:app/src/main/java/com/proj/smart_feeder/feature_feeder/data/repository/DummyFeederRepository.kt
import java.sql.Timestamp
import kotlin.time.Instant

class DummyFeederRepository: FeederRepository {
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getFeederState(): FeederState {
        return FeederState(
            isLoading = false,
            currentFoodGrams = 800,
            maxFoodCapacityGrams = 1000,
            lastSeenTimestamp = Timestamp(System.currentTimeMillis()).toInstant() as Instant?,
        )
    }

    override suspend fun getRecentFeedings(): List<String> {
        return listOf(
            "Fed 100g at 2026-06-01 08:00",
            "Fed 150g at 2026-06-01 12:00",
            "Fed 200g at 2026-06-01 18:00"
        )
    }
}