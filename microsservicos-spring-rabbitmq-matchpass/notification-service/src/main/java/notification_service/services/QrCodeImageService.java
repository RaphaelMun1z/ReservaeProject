package notification_service.services;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

@Service
public class QrCodeImageService {

    public String generateQrCodeBase64(String content) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();

            BitMatrix bitMatrix = qrCodeWriter.encode(
                content,
                BarcodeFormat.QR_CODE,
                320,
                320
            );

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            MatrixToImageWriter.writeToStream(
                bitMatrix,
                "PNG",
                outputStream
            );

            return Base64.getEncoder()
                .encodeToString(outputStream.toByteArray());
        } catch (Exception exception) {
            throw new IllegalStateException(
                "Não foi possível gerar a imagem do QR Code.",
                exception
            );
        }
    }
}