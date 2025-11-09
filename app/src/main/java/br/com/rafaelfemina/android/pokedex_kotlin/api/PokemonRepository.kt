package br.com.rafaelfemina.android.pokedex_kotlin.api

import br.com.rafaelfemina.android.pokedex_kotlin.api.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object PokemonRepository {
    // use the initialized service
    private val service: PokemonService
        get() = RetrofitClient.service

    // Exemplo de função suspend que delega à service
    suspend fun listPokemons(limit: Int = 151): PokemonsApiResult? {
        return try {
            withContext(Dispatchers.IO) {
                service.listPokemons(limit)
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getPokemon(number: Int): PokemonApiResult? {
        return try {
            withContext(Dispatchers.IO) {
                service.getPokemon(number)
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getPokemonByName(name: String): PokemonApiResult? {
        return try {
            withContext(Dispatchers.IO) {
                service.getPokemonByName(name)
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun listTypes(): TypesApiResult? {
        return try {
            withContext(Dispatchers.IO) {
                service.listTypes()
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun listGenerations(): GenerationsListResult? {
        return try {
            withContext(Dispatchers.IO) {
                service.listGenerations()
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getGeneration(idOrName: String): GenerationDetailResult? {
        return try {
            withContext(Dispatchers.IO) {
                service.getGeneration(idOrName)
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getCharacteristic(id: Int): CharacteristicResult? {
        return try {
            withContext(Dispatchers.IO) {
                service.getCharacteristic(id)
            }
        } catch (e: Exception) {
            null
        }
    }
}
