package miu.miu_04.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface CallRecordDao {
    @Query("SELECT * FROM call_records WHERE phoneNumber = :phoneNumber")
    suspend fun findRecordsByPhoneNumber(phoneNumber: String): List<CallRecord>

    @Query("SELECT * FROM call_records_second WHERE phoneNumber = :phoneNumber")
    suspend fun findRecordsByPhoneNumberSecond(phoneNumber: String): List<CallRecord2>

    @Insert
    suspend fun insertRecord(callRecord: CallRecord)

    @Insert
    suspend fun insertRecordSecond(callRecord: CallRecord2)
}