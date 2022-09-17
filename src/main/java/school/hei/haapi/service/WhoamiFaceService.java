package school.hei.haapi.service;

import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.BoundingBox;
import com.amazonaws.services.rekognition.model.CompareFacesMatch;
import com.amazonaws.services.rekognition.model.CompareFacesRequest;
import com.amazonaws.services.rekognition.model.CompareFacesResult;
import com.amazonaws.services.rekognition.model.ComparedFace;
import java.util.List;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import com.amazonaws.util.IOUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.hei.haapi.model.EventParticipant;
import school.hei.haapi.repository.EventParticipantRepository;

@Service
@AllArgsConstructor
public class WhoamiFaceService {

    private EventParticipantRepository eventParticipantRepository;
    public static boolean compareFacesS3(byte[] source, byte[] target){

        Float similarityThreshold = 70F;
        //Replace sourceFile and targetFile with the image files you want to compare.
        String sourceImage = "sourceImage";
        String targetImage = "targetImage";
        ByteBuffer sourceImageBytes=null;
        ByteBuffer targetImageBytes=null;

        AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.defaultClient();

        //Load source and target images and create input parameters
        try {
            sourceImageBytes = ByteBuffer.wrap(source);
        }
        catch(Exception e)
        {
            System.out.println("Failed to load source image " + sourceImage);
            System.exit(1);
        }
        try {
            targetImageBytes = ByteBuffer.wrap(target);
        }
        catch(Exception e)
        {
            System.out.println("Failed to load target images: " + targetImage);
            System.exit(1);
        }

        Image sourceI=new Image()
                .withBytes(sourceImageBytes);
        Image targetI=new Image()
                .withBytes(targetImageBytes);

        CompareFacesRequest request = new CompareFacesRequest()
                .withSourceImage(sourceI)
                .withTargetImage(targetI)
                .withSimilarityThreshold(similarityThreshold);

        // Call operation
        CompareFacesResult compareFacesResult=rekognitionClient.compareFaces(request);


        // Display results
        boolean isThere = false;
        List <CompareFacesMatch> faceDetails = compareFacesResult.getFaceMatches();
        for (CompareFacesMatch match: faceDetails){
            ComparedFace face= match.getFace();
            BoundingBox position = face.getBoundingBox();
            System.out.println("Face at " + position.getLeft().toString()
                    + " " + position.getTop()
                    + " matches with " + face.getConfidence().toString()
                    + "% confidence.");
            if(face.getConfidence() > 90){
                isThere = true;
            }
        }
        List<ComparedFace> uncompared = compareFacesResult.getUnmatchedFaces();

        System.out.println("There was " + uncompared.size()
                + " face(s) that did not match");
        System.out.println("Source image rotation: " + compareFacesResult.getSourceImageOrientationCorrection());
        System.out.println("target image rotation: " + compareFacesResult.getTargetImageOrientationCorrection());
        return isThere;
    }

    public boolean isPresent(String eventParticipantId){
        String stat;
        boolean here;
        here = compareFacesS3(new byte[] {}, new byte[] {});
        if (here == true){
            stat="HERE";
            eventParticipantRepository.setStatus(eventParticipantId, stat);
            return true;
        }
        else  {
            stat="MISSING";
            eventParticipantRepository.setStatus(eventParticipantId, stat);
            return false;
        }
    }
}
