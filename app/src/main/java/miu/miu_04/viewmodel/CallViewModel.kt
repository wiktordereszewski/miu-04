package miu.miu_04.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import miu.miu_04.data.CallRecord
import miu.miu_04.data.CallRecord2
import miu.miu_04.data.CallRepository
import java.util.Date

class CallViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = CallRepository(application)

    private val _searchResults = MutableStateFlow<List<Any>>(emptyList())
    val searchResults: StateFlow<List<Any>> = _searchResults

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun searchCalls(phoneNumber: String, dataSetId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _searchResults.value = repository.findCallsByPhoneNumber(phoneNumber, dataSetId)
            } catch (e: Exception) {
                _searchResults.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addSampleData() {
        viewModelScope.launch {
            val currentTime = System.currentTimeMillis()

            // Dataset 0 (Baza 1)
            val sampleCalls = listOf(
                CallRecord(phoneNumber = "123456789", startDate = Date(currentTime - 3600000), endDate = Date(currentTime - 3550000)),
                CallRecord(phoneNumber = "123456789", startDate = Date(currentTime - 7200000), endDate = Date(currentTime - 7150000))
            )

            // Dataset 1 (Baza 2)
            val sampleCalls2 = listOf(
                CallRecord2(phoneNumber = "123456789", startDate = Date(currentTime - 4800000), endDate = Date(currentTime - 4750000)),
                CallRecord2(phoneNumber = "987654321", startDate = Date(currentTime - 8600000), endDate = Date(currentTime - 8550000))
            )

            sampleCalls.forEach { repository.addCallRecord(it) }
            sampleCalls2.forEach { repository.addCallRecordSecond(it) }
        }
    }
}