package br.com.rafaelfemina.android.pokedex_kotlin.util

import android.content.Context
import androidx.core.content.ContextCompat
import br.com.rafaelfemina.android.pokedex_kotlin.R

object TypeColorUtil {
    fun getColorResForType(typeName: String?, ctx: Context): Int {
        val t = typeName?.lowercase() ?: "unknown"
        return when (t) {
            "normal" -> ContextCompat.getColor(ctx, R.color.type_normal)
            "fighting" -> ContextCompat.getColor(ctx, R.color.type_fighting)
            "flying" -> ContextCompat.getColor(ctx, R.color.type_flying)
            "poison" -> ContextCompat.getColor(ctx, R.color.type_poison)
            "ground" -> ContextCompat.getColor(ctx, R.color.type_ground)
            "rock" -> ContextCompat.getColor(ctx, R.color.type_rock)
            "bug" -> ContextCompat.getColor(ctx, R.color.type_bug)
            "ghost" -> ContextCompat.getColor(ctx, R.color.type_ghost)
            "steel" -> ContextCompat.getColor(ctx, R.color.type_steel)
            "fire" -> ContextCompat.getColor(ctx, R.color.type_fire)
            "water" -> ContextCompat.getColor(ctx, R.color.type_water)
            "grass" -> ContextCompat.getColor(ctx, R.color.type_grass)
            "electric" -> ContextCompat.getColor(ctx, R.color.type_electric)
            "psychic" -> ContextCompat.getColor(ctx, R.color.type_psychic)
            "ice" -> ContextCompat.getColor(ctx, R.color.type_ice)
            "dragon" -> ContextCompat.getColor(ctx, R.color.type_dragon)
            "dark" -> ContextCompat.getColor(ctx, R.color.type_dark)
            "fairy" -> ContextCompat.getColor(ctx, R.color.type_fairy)
            else -> ContextCompat.getColor(ctx, R.color.type_unknown)
        }
    }
}
