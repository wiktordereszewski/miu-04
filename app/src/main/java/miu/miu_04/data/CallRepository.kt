package miu.miu_04.data

import android.content.Context
import android.util.Log

class CallRepository(context: Context) {
    private val callRecordDao = DatabaseProvider.getDatabase(context).callRecordDao()

    // In CallRepository.kt
    suspend fun findCallsByPhoneNumber(phoneNumber: String, dataSetId: Int): List<Any> {
        return try {
            if (dataSetId == 0) {
                callRecordDao.findRecordsByPhoneNumber(phoneNumber)
            } else {
                callRecordDao.findRecordsByPhoneNumberSecond(phoneNumber)
            }
        } catch (e: Exception) {
            // Log the error
            Log.e("CallRepository", "Error finding calls: ${e.message}", e)
            emptyList()
        }
    }

    suspend fun addCallRecord(callRecord: CallRecord) {
        try {
            callRecordDao.insertRecord(callRecord)
        } catch (e: Exception) {
            Log.e("CallRepository", "Error adding record: ${e.message}", e)
        }
    }

    suspend fun addCallRecordSecond(callRecord: CallRecord2) {
        try {
            callRecordDao.insertRecordSecond(callRecord)
        } catch (e: Exception) {
            Log.e("CallRepository", "Error adding record: ${e.message}", e)
        }
    }
}