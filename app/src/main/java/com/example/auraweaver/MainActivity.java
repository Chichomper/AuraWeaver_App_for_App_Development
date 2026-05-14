package com.example.auraweaver;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {
    private PreferenceManager prefManager;
    private static AuraCanvasView auraCanvas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prefManager = new PreferenceManager(this);

        ViewPager2 viewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        FloatingActionButton fabInfo = findViewById(R.id.fabInfo);

        viewPager.setAdapter(new AuraPagerAdapter(this));
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(position == 0 ? "Vibes" : "Preferences");
        }).attach();

        fabInfo.setOnClickListener(v -> showInfoModal());
        if (prefManager.getVibeHistory().isEmpty()) showInfoModal();
    }

    private void showInfoModal() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_info, null);
        AlertDialog dialog = new AlertDialog.Builder(this, R.style.Dialog_Aura)
            .setView(dialogView)
            .setCancelable(true)
            .create();
        dialogView.findViewById(R.id.btnCloseDialog).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    public static void updateCanvas(int vibe) { 
        if (auraCanvas != null) auraCanvas.setVibe(vibe); 
    }

    public static void setCanvasRef(AuraCanvasView canvas) { 
        auraCanvas = canvas; 
    }

    private class AuraPagerAdapter extends FragmentStateAdapter {
        public AuraPagerAdapter(AppCompatActivity activity) { 
            super(activity); 
        }

        @NonNull 
        @Override 
        public Fragment createFragment(int position) {
            return position == 0 ? new VibesFragment() : new PreferencesFragment();
        }

        @Override 
        public int getItemCount() { 
            return 2; 
        }
    }

    public static class VibesFragment extends Fragment {
        private final String[] vibes = {"Midnight Coding","Coffee Shop Jazz","Neon Rain","Forest Morning","Sunset Drive","Ocean Static","Thunder Focus","Stardust Drift","Velvet Void"};
        private final String[] emojis = {"🌙","☕","🌆","🌲","🌅","🌊","⛈","✨","🖤"};

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_vibes, container, false);
            RecyclerView recycler = view.findViewById(R.id.recyclerVibes);
            AuraCanvasView canvas = view.findViewById(R.id.auraCanvas);
            MainActivity.setCanvasRef(canvas);

            recycler.setLayoutManager(new GridLayoutManager(getContext(), 2));
            recycler.setAdapter(new VibeAdapter());
            return view;
        }

        private class VibeAdapter extends RecyclerView.Adapter<VibeAdapter.VH> {
            @NonNull 
            @Override
            public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vibe, parent, false));
            }

            @Override
            public void onBindViewHolder(@NonNull VH holder, int position) {
                holder.emoji.setText(emojis[position]);
                holder.name.setText(vibes[position]);
                holder.itemView.setOnClickListener(v -> {
                    MainActivity.updateCanvas(position + 1);
                    Toast.makeText(getContext(), "Weaving: " + vibes[position], Toast.LENGTH_SHORT).show();
                });
            }

            @Override 
            public int getItemCount() { 
                return vibes.length; 
            }

            class VH extends RecyclerView.ViewHolder {
                android.widget.TextView emoji, name;

                VH(View itemView) {
                    super(itemView);
                    emoji = itemView.findViewById(R.id.vibeEmoji);
                    name = itemView.findViewById(R.id.vibeName);
                }
            }
        }
    }

    public static class PreferencesFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_preferences, container, false);
            PreferenceManager pm = new PreferenceManager(requireContext());

            SeekBar seekSpeed = view.findViewById(R.id.seekSpeed);
            SeekBar seekIntensity = view.findViewById(R.id.seekIntensity);
            SeekBar seekParticles = view.findViewById(R.id.seekParticles);

            seekSpeed.setProgress(pm.getSpeed());
            seekIntensity.setProgress(pm.getIntensity());
            seekParticles.setProgress(pm.getParticles());

            view.findViewById(R.id.btnSavePrefs).setOnClickListener(v -> {
                pm.savePreferences(
                    seekSpeed.getProgress(), 
                    seekIntensity.getProgress(), 
                    seekParticles.getProgress(), 
                    false
                );
                if (auraCanvas != null) {
                    auraCanvas.setPreferences(pm.getIntensity(), pm.getParticles(), pm.getSpeed() / 50f);
                }
                Toast.makeText(getContext(), "Preferences Saved", Toast.LENGTH_SHORT).show();
            });

            view.findViewById(R.id.btnClearHistory).setOnClickListener(v -> {
                pm.clearHistory();
                Toast.makeText(getContext(), "History Cleared", Toast.LENGTH_SHORT).show();
            });

            return view;
        }
    }
}
