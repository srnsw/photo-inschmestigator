package photoInschmestigator.domain

public class Photo implements Serializable {
    final String format
    final String date
    final String title
    final URL url
    final URL thumbnail
    final Integer series

    public Photo(URL url, URL thumbnail, String title, String date, String format, Integer series){
        this.url = url
        this.title = title
        this.date = date
        this.format = format
        this.thumbnail = thumbnail
        this.series = series
    }
}
