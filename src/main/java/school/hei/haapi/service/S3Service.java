package school.hei.haapi.service;

import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClient;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.AmazonRekognitionException;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.S3Object;

public class S3Service {

    public void setRekognitionClient(){
        String photo = "input.jpg";
        String bucket = "bucket";

        AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.defaultClient();
    }
    public static void getS3Image(AmazonRekognitionClient rekClient, String bucket, String image) {


        try {
            S3Object s3Object = new S3Object();
            s3Object.withBucket(bucket)
                    .withName(image);

            Image myImage = new Image();
            myImage.withS3Object(s3Object);

        } catch (AmazonRekognitionException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

}
