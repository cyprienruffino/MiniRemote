package view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import controller.Controller;
import orleans.info.fr.remotecontrol.R;

/**
 * Created by Valentin on 28/01/2016.
 */
public class SoftwareActivity extends Activity {
    public static final String SOFT = "soft";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.software);
    }

    public void evince(View view) {
        Controller.setDiapoSoft(Diapo.Evince);
        Intent v = new Intent(SoftwareActivity.this, DiapoActivity.class);
        startActivity(v);
    }

    public void libreoffice(View view) {
        Controller.setDiapoSoft(Diapo.LibreOffice);
        Intent v = new Intent(SoftwareActivity.this, DiapoActivity.class);
        startActivity(v);
    }

    public enum Diapo {
        Evince(0),
        LibreOffice(1);

        private int id;

        Diapo(int i) {
            id = i;
        }

        public static Diapo factory(int i) {
            switch (i) {
                case 0:
                    return Evince;
                case 1:
                    return LibreOffice;
            }
            return null;
        }

        public int getId() {
            return id;
        }
    }
}
