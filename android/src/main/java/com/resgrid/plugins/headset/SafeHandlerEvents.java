package com.resgrid.plugins.headset;

import android.os.Message;

import androidx.annotation.NonNull;

public interface SafeHandlerEvents {

    void handleMessageFromSafeHandler(@NonNull Message message);

}