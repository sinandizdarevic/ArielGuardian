package com.ariel.guardian.command;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.ariel.guardian.library.utils.ArielUtilities;
import com.google.zxing.WriterException;

/**
 * Created by mikalackis on 16.11.16..
 */

public class QRCodeCommand extends Command {

    protected QRCodeCommand(AbstractBuilder builder) {
        super(builder);
    }

    @Override
    public void execute() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Bitmap qr_code = null;
                try {
                    qr_code = ArielUtilities.generateDeviceQRCode(ArielUtilities.getUniquePseudoID());
                    String base64bitmap = ArielUtilities.base64Encode2String(qr_code);
                    message.setDataObject(base64bitmap);
                    reportToMaster();
                } catch (WriterException e) {
                    e.printStackTrace();
                    reportToMaster();
                }
            }
        });
    }

    public static class QRCodeBuilder extends AbstractBuilder<QRCodeBuilder> {

        @Override
        protected QRCodeBuilder me() {
            return this;
        }

        @Override
        public QRCodeCommand build() {
            return new QRCodeCommand(me());
        }

    }
}
