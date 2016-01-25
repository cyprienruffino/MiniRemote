package controller.communication.wifi;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

/**
 * Created by Valentin on 24/01/2016.
 */
public class SurService extends Service {
    private final IBinder binder = new SurBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class SurBinder extends Binder {
        public SurService getService() {
            return SurService.this;
        }
    }
}
