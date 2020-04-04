package com.s.youtubeapi.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;
import com.s.youtubeapi.Helper.VideosConfig;
import com.s.youtubeapi.Model.ItemVideos;
import com.s.youtubeapi.R;

import java.util.List;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class YoutubeVideoAdapter extends RecyclerView.Adapter<YoutubeVideoAdapter.YoutubeViewHolder> {

    private List<ItemVideos> videos;
    private ListItemClickListener mOnClickListener;

    public interface ListItemClickListener {
        void imageCapaClick(ItemVideos videos);
        //void onPlayClick(ItemVideos videos);
    }

    public YoutubeVideoAdapter(List<ItemVideos> videos, ListItemClickListener listener) {
        this.videos = videos;
        this.mOnClickListener = listener;

    }

    @NonNull
    @Override
    public YoutubeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.youtube_video_custom_layout, parent, false);
        return new YoutubeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final YoutubeViewHolder holder, int position) {
        final int index = position;

//        Caso queira passar o id do video para outra atividade

//        ItemVideos videoId = videos.get(0);
//        String idVideo = videoId.id.videoId;
//        Intent intent = new Intent(context, Sua_Atividade.class);
//        intent.putExtra("idVideo", idVideo);

        ItemVideos video = videos.get(position);

        holder.titulo.setText(video.snippet.title);
        holder.descricao.setText(video.snippet.channelTitle);

        /* initialize the thumbnail image view , we need to pass Developer Key */
        holder.youTubeThumbnailView.initialize(VideosConfig.KEY_YOUTUBE_API, new YouTubeThumbnailView.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubeThumbnailView youTubeThumbnailView, final YouTubeThumbnailLoader youTubeThumbnailLoader) {
                youTubeThumbnailLoader.setVideo(videos.get(index).id.videoId);

                youTubeThumbnailLoader.setOnThumbnailLoadedListener(new YouTubeThumbnailLoader.OnThumbnailLoadedListener() {
                    @Override
                    public void onThumbnailLoaded(YouTubeThumbnailView youTubeThumbnailView, String s) {
                        youTubeThumbnailLoader.release();
                    }

                    @Override
                    public void onThumbnailError(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader.ErrorReason errorReason) {
                        Log.e(TAG, "Youtube Thumbnail Error");
                    }
                });
            }

            @Override
            public void onInitializationFailure(YouTubeThumbnailView youTubeThumbnailView, YouTubeInitializationResult youTubeInitializationResult) {
                Log.e(TAG, "Youtube Initialization Failure");
            }
        });
    }

    @Override
    public int getItemCount() {
        return videos.size()-1;
    }

    class YoutubeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        YouTubeThumbnailView youTubeThumbnailView;
        TextView titulo;
        TextView descricao;

        YoutubeViewHolder(View itemView) {
            super(itemView);

            youTubeThumbnailView = itemView.findViewById(R.id.video_thumbnail_image_view);
            titulo = itemView.findViewById(R.id.textoTitulo);
            descricao = itemView.findViewById(R.id.textoDescricao);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mOnClickListener != null){
                int clickedPosition = getAdapterPosition();
                mOnClickListener.imageCapaClick(videos.get(clickedPosition));
            }
        }
    }
}
