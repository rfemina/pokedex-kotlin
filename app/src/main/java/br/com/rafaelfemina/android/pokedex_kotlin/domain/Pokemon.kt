package br.com.rafaelfemina.android.pokedex_kotlin.domain

import java.util.Locale

data class Pokemon(
    val number: Int,
    val name: String,
    val types: List<PokemonType>
) {
    val formattedName = name.capitalize(Locale.ROOT)

    val formattedNumber = number.toString().padStart(3, '0')

    val imageUrl = "https://assets.pokemon.com/assets/cms2/img/pokedex/detail/$formattedNumber.png"
}

