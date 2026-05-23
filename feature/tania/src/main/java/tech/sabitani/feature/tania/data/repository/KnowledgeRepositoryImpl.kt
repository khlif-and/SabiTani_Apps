package tech.sabitani.feature.tania.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import tech.sabitani.feature.tania.data.local.KnowledgeAssetLoader
import tech.sabitani.feature.tania.domain.model.KnowledgeEntry
import tech.sabitani.feature.tania.domain.repository.KnowledgeRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class KnowledgeRepositoryImpl
    @Inject
    constructor(
        private val loader: KnowledgeAssetLoader,
    ) : KnowledgeRepository {
        private val cacheMutex = Mutex()
        private var cached: List<KnowledgeEntry>? = null

        override suspend fun loadAll(): List<KnowledgeEntry> =
            cached ?: cacheMutex.withLock {
                cached ?: withContext(Dispatchers.IO) { loader.loadAll() }.also { cached = it }
            }
    }
