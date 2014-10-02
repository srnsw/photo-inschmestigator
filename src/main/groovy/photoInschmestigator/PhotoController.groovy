package photoInschmestigator

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

import static org.springframework.web.bind.annotation.RequestMethod.*

@RestController
public class PhotoController {
    @Autowired
    PhotoService photoService

    @RequestMapping(value = "/photos", method = GET )
    public @ResponseBody
    Collection photos(@RequestParam(value="series", required=true) Integer series) throws IOException {
        photoService.photosForSeries(series)
    }
}
