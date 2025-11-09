package br.com.rafaelfemina.android.pokedex_kotlin.view

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import br.com.rafaelfemina.android.pokedex_kotlin.R
import br.com.rafaelfemina.android.pokedex_kotlin.domain.Pokemon
import br.com.rafaelfemina.android.pokedex_kotlin.util.TypeColorUtil
import com.bumptech.glide.Glide

class PokemonAdapter(
    private var items: MutableList<Pokemon> = mutableListOf()
) : RecyclerView.Adapter<PokemonAdapter.ViewHolder>() {

    // Substitui toda a lista e notifica adapter
    fun submitList(newList: List<Pokemon>) {
        items = newList.toMutableList()
        notifyDataSetChanged()
    }

    // Retorna item seguro (ou null)
    fun getItem(position: Int): Pokemon? {
        return items.getOrNull(position)
    }

    // Atualiza um item existente (procura por number) e notifica o position alterado
    fun updateItem(updated: Pokemon) {
        val idx = items.indexOfFirst { it.number == updated.number }
        if (idx >= 0) {
            items[idx] = updated
            notifyItemChanged(idx)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.pokemon_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bindView(item)

        holder.itemView.setOnClickListener { view ->
            val ctx = view.context
            val intent = android.content.Intent(ctx, PokemonDetailActivity::class.java).apply {
                putExtra(PokemonDetailActivity.EXTRA_POKEMON_ID, item.number)
                putExtra(PokemonDetailActivity.EXTRA_POKEMON_NAME, item.name)
                val primaryType = if (item.types.isNotEmpty()) item.types[0].name else ""
                putExtra(PokemonDetailActivity.EXTRA_PRIMARY_TYPE, primaryType)
            }
            ctx.startActivity(intent)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivPokemon = itemView.findViewById<ImageView>(R.id.ivPokemon)
        private val tvNumber = itemView.findViewById<TextView>(R.id.tvNumber)
        private val tvName = itemView.findViewById<TextView>(R.id.tvName)
        private val tvType1 = itemView.findViewById<TextView>(R.id.tvType1)
        private val tvType2 = itemView.findViewById<TextView>(R.id.tvType2)

        fun bindView(item: Pokemon?) = with(itemView) {
            if (item == null) {
                ivPokemon.setImageResource(R.drawable.ic_placeholder)
                tvNumber.text = ""
                tvName.text = ""
                tvType1.visibility = View.GONE
                tvType2.visibility = View.GONE
                return@with
            }

            // imagem (usa formattedNumber para construir url)
            Glide.with(context).load(item.imageUrl).into(ivPokemon)

            // número e nome
            tvNumber.text = "Nº ${item.formattedNumber}"
            tvName.text = item.formattedName

            // Tipo 1
            if (item.types.isNotEmpty()) {
                tvType1.visibility = View.VISIBLE
                tvType1.text = item.types[0].name.capitalize()
                val color1 = TypeColorUtil.getColorResForType(item.types[0].name, context)
                setBadgeBackground(tvType1, color1)
            } else {
                tvType1.visibility = View.GONE
            }

            // Tipo 2
            if (item.types.size > 1) {
                tvType2.visibility = View.VISIBLE
                tvType2.text = item.types[1].name.capitalize()
                val color2 = TypeColorUtil.getColorResForType(item.types[1].name, context)
                setBadgeBackground(tvType2, color2)
            } else {
                tvType2.visibility = View.GONE
            }

            // forçar texto em preto
            tvNumber.setTextColor(ContextCompat.getColor(context, android.R.color.black))
            tvName.setTextColor(ContextCompat.getColor(context, android.R.color.black))
        }

        private fun setBadgeBackground(tv: TextView, colorInt: Int) {
            val bg = GradientDrawable()
            bg.cornerRadius = 12f
            bg.setColor(colorInt)
            bg.setStroke(1, 0x22000000)
            tv.background = bg
            val pad = (10 * tv.resources.displayMetrics.density).toInt()
            tv.setPadding(pad, 6, pad, 6)
        }
    }
}
