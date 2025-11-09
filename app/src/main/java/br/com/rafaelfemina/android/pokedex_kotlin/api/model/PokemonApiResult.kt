package br.com.rafaelfemina.android.pokedex_kotlin.api.model

import br.com.rafaelfemina.android.pokedex_kotlin.domain.PokemonType

// Reaproveita PokemonTypeSlot definido em outro arquivo (se já existir),
// caso contrário mantenha este aqui.
// Se já tiver uma definição igual, apenas garanta que o nome e pacote batam.

data class PokemonsApiResult(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<PokemonResult>
)

data class PokemonResult(
    val name: String,
    val url: String
)

// Modelo principal retornado por GET /pokemon/{id} ou /pokemon/{name}
data class PokemonApiResult(
    val id: Int,
    val name: String,
    val types: List<PokemonTypeSlot>,
    val stats: List<StatSlot>? = null,
    val sprites: SpriteSet? = null // opcional, caso queira imagens alternativas
)

data class PokemonTypeSlot(
    val slot: Int,
    val type: PokemonType
)

// modelo para os stats que você exibiu no detalhe
data class StatSlot(
    val base_stat: Int,
    val effort: Int,
    val stat: NamedApiResource
)

// reuso de NamedApiResource definido em GenerationsListResult.kt
// se por acaso não existir, crie em outro arquivo api.model.NamedApiResource

// sprites simplificado (opcional)
data class SpriteSet(
    val front_default: String?
)
