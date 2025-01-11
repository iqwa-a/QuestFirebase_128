package com.example.firebase.ui.theme.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.firebase.model.Mahasiswa
import com.example.firebase.repository.RepositoryMhs
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class HomeViewModel (
    private val mhs:RepositoryMhs
):ViewModel(){

    init {
        getMhs()
    }

    var mhsUiState : HomeUiState by mutableStateOf(HomeUiState.Loading)
    fun getMhs(){
        viewModelScope.launch {
            mhs.getAllMhs().onStart {
                mhsUiState = HomeUiState.Loading
            }
                .catch {
                    mhsUiState = HomeUiState.Error(e = it)
                }
                .collect{
                    mhsUiState = if (it.isEmpty()){
                        HomeUiState.Error(Exception("Belum ada data mahasiswa"))
                    }
                    else{
                        HomeUiState.Success(it)
                    }
                }
        }
    }
    fun deleteMhs(mahasiswa: Mahasiswa){
        viewModelScope.launch {
            try {
                mhs.deleteMhs(mahasiswa)
            } catch (e: Exception) {
                mhsUiState = HomeUiState.Error(e)
            }

        }
    }
}


sealed class HomeUiState{
    object Loading: HomeUiState()
    data class Success(val data: List<Mahasiswa>) : HomeUiState()
    data class Error(val e: Throwable): HomeUiState()
}