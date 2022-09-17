package school.hei.haapi.endpoint.rest.controller;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import school.hei.haapi.endpoint.rest.model.Whoami;
import school.hei.haapi.endpoint.rest.security.model.Principal;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@RestController
public class WhoamiFaceController {


  @PostMapping(value="/whoamiface", consumes = {MediaType.IMAGE_JPEG_VALUE})
  public Whoami uploadImage(@RequestBody byte[] imgByte, @AuthenticationPrincipal Principal principal) throws IOException {
    ByteArrayInputStream inpByte = new ByteArrayInputStream(imgByte);
    BufferedImage bufferImg = ImageIO.read(inpByte);
    ByteArrayOutputStream temp = new ByteArrayOutputStream();
    //ImageIO.write(bufferImg, "jpg", temp);
    byte[] result = temp.toByteArray();

    Whoami whoami = new Whoami();
    whoami.setId(principal.getUserId());
    whoami.setBearer(principal.getBearer());
    whoami.setRole(Whoami.RoleEnum.valueOf(principal.getRole()));
    return whoami;
  }
}
