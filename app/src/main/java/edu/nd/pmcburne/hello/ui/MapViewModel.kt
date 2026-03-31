package edu.nd.pmcburne.hello.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import edu.nd.pmcburne.hello.data.AppDatabase
import edu.nd.pmcburne.hello.data.LocationEntity
import edu.nd.pmcburne.hello.data.LocationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MapViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = LocationRepository(
        AppDatabase.getDatabase(application).locationDao()
    )

    private val _allLocations = MutableStateFlow<List<LocationEntity>>(emptyList())

    private val _tags = MutableStateFlow<List<String>>(emptyList())
    val tags: StateFlow<List<String>> = _tags

    private val _selectedTag = MutableStateFlow("core")
    val selectedTag: StateFlow<String> = _selectedTag

    private val _filteredLocations = MutableStateFlow<List<LocationEntity>>(emptyList())
    val filteredLocations: StateFlow<List<LocationEntity>> = _filteredLocations

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        viewModelScope.launch {
            try {
                repository.syncFromNetwork()
            } catch (_: Exception) { }
            val locations = repository.getAllLocations()
            _allLocations.value = locations
            _tags.value = locations
                .flatMap { it.tags.split(",") }
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .distinct()
                .sorted()
            applyFilter()
            _isLoading.value = false
        }
    }

    fun selectTag(tag: String) {
        _selectedTag.value = tag
        applyFilter()
    }

    private fun applyFilter() {
        val tag = _selectedTag.value
        _filteredLocations.value = _allLocations.value.filter { loc ->
            loc.tags.split(",").map { it.trim() }.contains(tag)
        }
    }
}