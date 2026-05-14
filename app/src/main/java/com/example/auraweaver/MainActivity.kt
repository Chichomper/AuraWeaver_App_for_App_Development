package com.example.auraweaver

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.auraweaver.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var prefManager: PreferenceManager

    companion object {
        var auraCanvas: AuraCanvasView? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefManager = PreferenceManager(this)

        binding.viewPager.adapter = AuraPagerAdapter(this)

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = if (position == 0) "Vibes" else "Preferences"
        }.attach()

        binding.fabInfo.setOnClickListener { showInfoModal() }

        if (prefManager.getVibeHistory().isEmpty()) {
            showInfoModal()
        }
    }

    private fun showInfoModal() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_info, null)
        val dialog = AlertDialog.Builder(this, R.style.Dialog_Aura)
            .setView(dialogView)
            .setCancelable(true)
            .create()
        dialogView.findViewById<View>(R.id.btnCloseDialog).setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    class AuraPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
        override fun getItemCount(): Int = 2
        override fun createFragment(position: Int): Fragment =
            if (position == 0) VibesFragment() else PreferencesFragment()
    }

    class VibesFragment : Fragment() {

        private val vibes = listOf(
            "Midnight Coding", "Coffee Shop Jazz", "Neon Rain",
            "Forest Morning", "Sunset Drive", "Ocean Static",
            "Thunder Focus", "Stardust Drift", "Velvet Void"
        )

        private val emojis = listOf(
            "🌙", "☕", "🌆",
            "🌲", "🌅", "🌊",
            "⛈", "✨", "🖤"
        )

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            val view = inflater.inflate(R.layout.fragment_vibes, container, false)
            val recycler = view.findViewById<RecyclerView>(R.id.recyclerVibes)
            val canvas = view.findViewById<AuraCanvasView>(R.id.auraCanvas)
            auraCanvas = canvas

            recycler.layoutManager = GridLayoutManager(context, 2)
            recycler.adapter = VibeAdapter()
            return view
        }

        inner class VibeAdapter : RecyclerView.Adapter<VibeAdapter.VH>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
                val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_vibe, parent, false)
                return VH(itemView)
            }

            override fun onBindViewHolder(holder: VH, position: Int) {
                holder.emoji.text = emojis[position]
                holder.name.text = vibes[position]
                holder.itemView.setOnClickListener {
                    auraCanvas?.setVibe(position + 1)
                    Toast.makeText(context, "Weaving: ${vibes[position]}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun getItemCount(): Int = vibes.size

            inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
                val emoji: android.widget.TextView = itemView.findViewById(R.id.vibeEmoji)
                val name: android.widget.TextView = itemView.findViewById(R.id.vibeName)
            }
        }
    }

    class PreferencesFragment : Fragment() {

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            val view = inflater.inflate(R.layout.fragment_preferences, container, false)
            val pm = PreferenceManager(requireContext())

            val seekSpeed = view.findViewById<SeekBar>(R.id.seekSpeed)
            val seekIntensity = view.findViewById<SeekBar>(R.id.seekIntensity)
            val seekParticles = view.findViewById<SeekBar>(R.id.seekParticles)

            seekSpeed.progress = pm.getSpeed()
            seekIntensity.progress = pm.getIntensity()
            seekParticles.progress = pm.getParticles()

            view.findViewById<View>(R.id.btnSavePrefs).setOnClickListener {
                pm.savePreferences(
                    seekSpeed.progress,
                    seekIntensity.progress,
                    seekParticles.progress,
                    false
                )
                auraCanvas?.setPreferences(pm.getIntensity(), pm.getParticles(), pm.getSpeed() / 50f)
                Toast.makeText(context, "Preferences Saved", Toast.LENGTH_SHORT).show()
            }

            view.findViewById<View>(R.id.btnClearHistory).setOnClickListener {
                pm.clearHistory()
                Toast.makeText(context, "History Cleared", Toast.LENGTH_SHORT).show()
            }

            return view
        }
    }
}
