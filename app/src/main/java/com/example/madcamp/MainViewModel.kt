package com.example.madcamp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    init {
        // ViewModel이 생성될 때 가상 데이터 로딩 시작
        viewModelScope.launch {
            delay(1000) // 2초간 딜레이 (데이터 로딩 시간이라고 가정)
            _isLoading.value = false
        }
    }
}