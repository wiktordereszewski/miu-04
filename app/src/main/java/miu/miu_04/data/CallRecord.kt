package miu.miu_04.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity (tableName = "call_records")
data class CallRecord (
    @PrimaryKey (autoGenerate = true)
    val id: Int = 0,
    val phoneNumber: String,
    val startDate: Date,
    val endDate: Date
)