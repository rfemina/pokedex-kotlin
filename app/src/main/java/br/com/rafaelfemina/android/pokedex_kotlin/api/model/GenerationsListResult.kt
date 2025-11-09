package br.com.rafaelfemina.android.pokedex_kotlin.api.model


data class GenerationsListResult(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<NamedApiResource>
)


data class NamedApiResource(
    val name: String,
    val url: String
)