package br.com.rafaelfemina.android.pokedex_kotlin.api.model

data class CharacteristicResult(
    val id: Int,
    val gene_modulo: Int,
    val possible_values: List<Int>?,
    val highest_stat: NamedApiResource?,
    val descriptions: List<Description>?
)

data class Description(
    val description: String,
    val language: NamedApiResource?
)
