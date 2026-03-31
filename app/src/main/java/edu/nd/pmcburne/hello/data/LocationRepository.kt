package edu.nd.pmcburne.hello.data

import edu.nd.pmcburne.hello.network.RetrofitInstance

class LocationRepository(private val dao: LocationDao) {

    suspend fun syncFromNetwork() {
        val placemarks = RetrofitInstance.api.getPlacemarks()
        val entities = placemarks.mapNotNull { p ->
            val center = p.visualCenter ?: return@mapNotNull null
            LocationEntity(
                id          = p.id,
                name        = p.name,
                description = p.description.orEmpty(),
                latitude    = center.latitude,
                longitude   = center.longitude,
                tags        = p.tagList.joinToString(",")
            )
        }
        dao.insertAll(entities)
    }

    suspend fun getAllLocations(): List<LocationEntity> = dao.getAllLocations()
}