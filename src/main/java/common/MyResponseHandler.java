package common;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MyResponseHandler implements ResponseHandler<File> {

    final private File target;
    final private String uri;

    public MyResponseHandler(File target, String uri) {
        this.target = target;
        this.uri = uri;
    }


    @Override
    public File handleResponse(HttpResponse httpResponse) throws ClientProtocolException, IOException {

        HttpEntity entity = httpResponse.getEntity();
        if (entity != null) {
            try (FileOutputStream outstream = new FileOutputStream(target)) {

                entity.writeTo(outstream);
                try {

                    BufferedImage picture = ImageIO.read(target);
                    if(picture != null){

                        System.out.println();
                        System.out.printf("LINK: %s\n", uri);
                        System.out.printf("SIZE: %.2f KB\n", entity.getContentLength() / 1024.0);
                        System.out.printf("HEIGHT: %d PX\nWIDTH: %d PX\n", picture.getHeight(), picture.getWidth());
                    } else {

                        System.out.println();
                        System.out.printf("%s%s\n","Picture by link has unsupported format: ", uri);
                    }
                } catch (Exception e) {

                    System.out.println("here");
                    System.out.printf("%s%s\n", "Error: ", e);
                }
            } catch (IOException e) {

                System.out.printf("%s%s\n", "Error processing: ", uri);
            }
        }

        return this.target;
    }
}
