package photoInschmestigator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Collection;

@RestController
public class PhotoController {
    @Autowired
    PhotoService photoService;

    @RequestMapping(value = "/photos", method = RequestMethod.GET )
    public @ResponseBody
    Collection photos(@RequestParam(value="series", required=true) Integer series) throws IOException {
        return photoService.photosForSeries(series);
    }
}
