package br.com.rafaelfemina.android.pokedex_kotlin.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.rafaelfemina.android.pokedex_kotlin.R


// Se preferir não usar synthetic, troque para findViewById / viewBinding.

class ProjectStructureDialogFragment(
    private val items: List<String>
) : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, com.google.android.material.R.style.Theme_Material3_Light_Dialog)
        isCancelable = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.dialog_structure, container, false)

        // Recycler setup
        val recycler = root.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.structure_recycler)
        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = ProjectStructureAdapter(items.toMutableList())

        // close button
        root.findViewById<View>(R.id.structure_close).setOnClickListener {
            dismiss()
        }

        return root
    }

    override fun onStart() {
        super.onStart()
        // full width dialog (centered)
        dialog?.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }
}
