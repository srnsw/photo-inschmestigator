package photoInschmestigator;

import org.springframework.cache.annotation.Cacheable;
import photoInschmestigator.domain.Photo;

import java.io.IOException;
import java.util.Collection;

/**
 * Created by archerd on 2/10/2014.
 */
public interface PhotoService {
    @Cacheable(value="photos")
    Collection<Photo> photosForSeries(Integer series) throws IOException;
}
