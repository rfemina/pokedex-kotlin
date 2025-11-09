package br.com.rafaelfemina.android.pokedex_kotlin.api.model


data class GenerationDetailResult(
    val id: Int,
    val name: String,
    val pokemon_species: List<NamedApiResource>
// outros campos opcionais omitidos intencionalmente
)