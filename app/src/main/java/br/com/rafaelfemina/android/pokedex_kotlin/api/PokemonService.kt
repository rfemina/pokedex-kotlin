package br.com.rafaelfemina.android.pokedex_kotlin.api

import br.com.rafaelfemina.android.pokedex_kotlin.api.model.*
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit service com funções suspend para coroutines.
 * Retrofit 2.6+ suporta suspend functions nativamente.
 */
interface PokemonService {

    @GET("pokemon")
    suspend fun listPokemons(@Query("limit") limit: Int): PokemonsApiResult

    @GET("pokemon/{number}")
    suspend fun getPokemon(@Path("number") number: Int): PokemonApiResult

    // Também suportamos buscar por nome
    @GET("pokemon/{name}")
    suspend fun getPokemonByName(@Path("name") name: String): PokemonApiResult

    @GET("type")
    suspend fun listTypes(): TypesApiResult

    @GET("generation")
    suspend fun listGenerations(): GenerationsListResult

    @GET("characteristic/{id}")
    suspend fun getCharacteristic(@Path("id") id: Int): CharacteristicResult


    @GET("generation/{idOrName}")
    suspend fun getGeneration(@Path("idOrName") idOrName: String): GenerationDetailResult
}
