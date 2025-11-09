package br.com.rafaelfemina.android.pokedex_kotlin.api.model

data class TypesApiResult(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<TypeResult>
)

data class TypeResult(
    val name: String,
    val url: String
)
