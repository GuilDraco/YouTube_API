package com.s.youtubeapi.Activitys;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragmentX;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.s.youtubeapi.Adapter.YoutubeVideoAdapter;
import com.s.youtubeapi.Helper.RetrofitConfig;
import com.s.youtubeapi.Helper.VideosConfig;
import com.s.youtubeapi.Interface.YouTubeService;
import com.s.youtubeapi.Model.ItemVideos;
import com.s.youtubeapi.Model.ResultadoVideos;
import com.s.youtubeapi.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerViewVideos;
    private MaterialSearchView searchView;

    private List<ItemVideos> videos = new ArrayList<>();
    private ResultadoVideos resultadoVideos;

    private Retrofit retrofit;
    private ShimmerFrameLayout mShimmerViewContainer;

    private YouTubePlayerSupportFragmentX youTubePlayerFragmentX;
    private YouTubePlayer youTubePlayer;
    private String idVideo;
    private ImageView capa;
    private TextView tituloVideo;
    private TextView descricaoVideo;

    private String titulo;
    private String descricao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark, getTheme()));
        Toolbar toolbar = findViewById(R.id.toolbarVideos);
        toolbar.setTitle("Seu Canal");

        toolbar.setBackgroundColor(getColor(R.color.colorPrimary));
        toolbar.setTitleTextColor(getColor(R.color.colorBranco));
        setSupportActionBar(toolbar);

        recyclerViewVideos = findViewById(R.id.recycler_view);
        searchView = findViewById(R.id.search_bar_videos);
        mShimmerViewContainer = findViewById(R.id.shimmer_view_container);

        capa = findViewById(R.id.imagemCapa);
        tituloVideo = findViewById(R.id.textoTitulo);
        descricaoVideo = findViewById(R.id.textoDescricao);

        //Configuração Retrofit
        retrofit = RetrofitConfig.getRetrofit();

        metodosSearchView();
        recuperarVideos("");
    }

    private void initializeYoutubePlayer() {
        youTubePlayerFragmentX = (YouTubePlayerSupportFragmentX) getSupportFragmentManager()
                .findFragmentById(R.id.youtube_player_fragment);

        if (youTubePlayerFragmentX == null)
            return;

        youTubePlayerFragmentX.initialize(VideosConfig.KEY_YOUTUBE_API, new YouTubePlayer.OnInitializedListener() {

            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
                if (!wasRestored) {
                    youTubePlayer = player;

                    //Inicializa o Video no Fragment ao selecionar o item do RecycleView
                    capa.setVisibility(View.INVISIBLE);
                    tituloVideo.setVisibility(View.VISIBLE);
                    tituloVideo.setText(titulo);

                    descricaoVideo.setVisibility(View.VISIBLE);
                    descricaoVideo.setText(descricao);

                    youTubePlayer.cueVideo(idVideo);
                }
            }
            @Override
            public void onInitializationFailure(YouTubePlayer.Provider arg0, YouTubeInitializationResult arg1) {
                Log.e("player_erro", arg1.name());
            }
        });
    }

    private void recuperarVideos(String pesquisa){
        String q = pesquisa.replaceAll(" ", "+");
        YouTubeService youTubeService = retrofit.create(YouTubeService.class);

        youTubeService.recuperarVideos(
                "snippet", "date", "10",
                VideosConfig.KEY_YOUTUBE_API, VideosConfig.CANAL_ID, q).enqueue(new Callback<ResultadoVideos>() {
            @Override
            public void onResponse(@NonNull Call<ResultadoVideos> call, @NonNull Response<ResultadoVideos> response) {
                if (response.isSuccessful()) {
                    resultadoVideos = response.body();

                    if (resultadoVideos != null) {
                        videos = resultadoVideos.items;

                        idVideo = videos.get(0).id.videoId;
                        titulo = videos.get(0).snippet.title;
                        descricao = videos.get(0).snippet.description;

                        carregarRecycleView();

                        // Stopping Shimmer Effect's animation after data is loaded to ListView
                        mShimmerViewContainer.stopShimmerAnimation();
                        mShimmerViewContainer.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResultadoVideos> call, @NonNull Throwable t) {
                Log.e(TAG, "Youtube Initialization Failure");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_videos, menu);

        MenuItem item = menu.findItem(R.id.menu_search_videos);
        searchView.setMenuItem(item);

        ActionBar actionBar = getSupportActionBar();

        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home){
            onBackPressed();
        }
        return true;
    }

    //Search view
    public void metodosSearchView() {

        //Configua métodos do Search View
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                recuperarVideos(query);
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {

            }

            @Override
            public void onSearchViewClosed() {
                recuperarVideos("");
            }
        });
    }

    public void carregarRecycleView(){

        YoutubeVideoAdapter adapterVideo = new YoutubeVideoAdapter(videos, new YoutubeVideoAdapter.ListItemClickListener() {
            @Override
            public void imageCapaClick(ItemVideos videos) {

                if (youTubePlayerFragmentX != null && youTubePlayer != null) {
                    youTubePlayer.cueVideo(videos.id.videoId);
                }
                Log.d("CLICOU_CAPA", idVideo);
            }
        });

        initializeYoutubePlayer();
        recyclerViewVideos.setHasFixedSize(true);
        recyclerViewVideos.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewVideos.setAdapter(adapterVideo);

    }

    @Override
    public void onResume() {
        super.onResume();
        mShimmerViewContainer.startShimmerAnimation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mShimmerViewContainer.stopShimmerAnimation();
    }
}
