package photoInschmestigator;

import java.net.URL;

public class Photo {
    private final String format;
    private final String date;
    private final String title;
    private final URL url;
    private final URL thumbnail;
    private final Integer series;

    public Photo(URL url, URL thumbnail, String title, String date, String format, Integer series){
        this.url = url;
        this.title = title;
        this.date = date;
        this.format = format;
        this.thumbnail = thumbnail;
        this.series = series;
    }
    public URL getPhoto() {
        return url;
    }

    public URL getThumbnail() {
        return thumbnail;
    }

    public String getFormat() {
        return format;
    }

    public String getDate() {
        return date;
    }

    public String getTitle() {
        return title;
    }

    public Integer getSeries() {
        return series;
    }
}
