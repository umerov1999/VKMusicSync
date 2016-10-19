package com.krava.vkmedia.presentation.presenter;


import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.krava.vkmedia.domain.DataManager;
import com.krava.vkmedia.presentation.view.SongListView;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiAudio;
import com.vk.sdk.api.model.VKList;

/**
 * Created by krava2008 on 19.10.16.
 */

@InjectViewState
public class SongListPresenter extends MvpPresenter<SongListView> {
    public static final int TYPE_MY_LIST = 0;
    public static final int TYPE_CACHED = 1;
    public static final int TYPE_RECOMMENDATION = 2;
    public static final int TYPE_POPULAR = 3;

    private VKRequest request;
    private int type = 0;
    private int ownerId = -1;
    private boolean isHistoryLoading = false;

    public SongListPresenter() {

    }

    public void setType(int type) {
        this.type = type;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public boolean isTypeMyList() { return type == TYPE_MY_LIST; }

    public boolean isTypeCached() { return type == TYPE_CACHED; }

    public void getSongList(int offset) {
        switch (type){
            case TYPE_MY_LIST:
                loadUserSongs(offset, ownerId);
                break;
            case TYPE_CACHED:
                loadCachedSongs();
                break;
            case TYPE_POPULAR:
                loadPopularSongs(offset);
                break;
            case TYPE_RECOMMENDATION:
                loadRecommendationSongs(offset);
                break;
            default:
                break;
        }
    }

    private void loadUserSongs(int offset, int ownerId) {
        if(isHistoryLoading) return;

        if(request != null){
            request.cancel();
        }
        if(offset != 0) {
            isHistoryLoading = true;
        }
        this.request = VKApi.audio().get(VKParameters.from("owner_id", ownerId, "count", 30, "offset", offset));
        this.request.attempts = 3;
        this.request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(final VKResponse response) {
                super.onComplete(response);
                isHistoryLoading = false;

                if(offset == 0) {
                    getViewState().initSongList((VKList<VKApiAudio>)response.parsedModel);
                }else{
                    getViewState().addSongs((VKList<VKApiAudio>)response.parsedModel);
                }
            }

            @Override
            public void onError(VKError error) {
                super.onError(error);
                isHistoryLoading = false;

                getViewState().onError();
            }
        });
    }

    private void loadCachedSongs() {
        getViewState().initSongList(DataManager.getInstance().getCachedSongs());
    }

    private void loadPopularSongs(int offset) {
        if(isHistoryLoading) return;

        if(request != null) {
            request.cancel();
        }
        request = VKApi.audio().getPopular(VKParameters.from("offset", offset));
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                isHistoryLoading = false;

                if(offset == 0) {
                    getViewState().initSongList((VKList<VKApiAudio>)response.parsedModel);
                }else{
                    getViewState().addSongs((VKList<VKApiAudio>)response.parsedModel);
                }
            }

            @Override
            public void onError(VKError error) {
                super.onError(error);
                isHistoryLoading = false;

                getViewState().onError();
            }
        });
    }

    private void loadRecommendationSongs(int offset) {
        if(isHistoryLoading) return;

        if(request != null) {
            request.cancel();
        }
        request = VKApi.audio().getRecommendations(VKParameters.from("user_id", ownerId, "count", 30, "offset", offset));
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                isHistoryLoading = false;

                if(offset == 0) {
                    getViewState().initSongList((VKList<VKApiAudio>)response.parsedModel);
                }else{
                    getViewState().addSongs((VKList<VKApiAudio>)response.parsedModel);
                }
            }

            @Override
            public void onError(VKError error) {
                super.onError(error);
                isHistoryLoading = false;

                getViewState().onError();
            }
        });
    }
}