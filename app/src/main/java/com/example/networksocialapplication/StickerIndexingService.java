package com.example.networksocialapplication;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import com.google.android.gms.tasks.Task;
import com.google.firebase.appindexing.FirebaseAppIndex;
import com.google.firebase.appindexing.builders.Indexables;
import com.google.firebase.appindexing.builders.StickerBuilder;
import com.google.firebase.appindexing.builders.StickerPackBuilder;

public class StickerIndexingService extends JobIntentService {

    private static final int UNIQUE_JOB_ID = 42;

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        String smileyImageUri = Uri.parse("android.resource://com.vicros0723.melissastickers/drawable/smiley").toString();

        StickerBuilder smileyStickerBuilder = Indexables.stickerBuilder()
                .setName("smiley")
                .setUrl("mystickers://sticker/smiley")
                .setImage(smileyImageUri)
                .setDescription("Melissa smiling!")
                .setKeywords("Melissa", "smiley")
                .setIsPartOf(Indexables.stickerPackBuilder().setName("Melissa"));

        StickerPackBuilder stickerPackBuilder = Indexables.stickerPackBuilder()
                .setName("Melissa")
                .setUrl("mystickers://sticker/pack/melissa")
                .setHasSticker(smileyStickerBuilder);
        Task<Void> update = FirebaseAppIndex.getInstance().update(
                stickerPackBuilder.build(),
                smileyStickerBuilder.build());
    }
    public static void enqueueWork(Context context) {
        enqueueWork(context, StickerIndexingService.class, UNIQUE_JOB_ID, new Intent());
    }
}
