package photoInschmestigator;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;

@Service
public class PhotoService {
    private static ConcurrentTaskScheduler scheduler = new ConcurrentTaskScheduler();

    @Value(value = "${photo-investigator.url}")
    private URL url;

    @Value(value = "${photo-investigator.userAgent}")
    private String userAgent;

    private boolean hasPhotos(final Integer series) throws IOException {
       return Jsoup.connect(format("%s?series=%d&B1=Search", url, series))
               .method(Connection.Method.GET)
               .userAgent(userAgent)
               .execute()
               .parse()
               .select("table.imagetable tbody tr ").size() > 0;
    }

    private Collection<Photo> collectPhotos(final Integer series) throws IOException {
        Collection<Photo> photos = Lists.newLinkedList();
        final Connection.Response response = Jsoup.connect(format("%s?series=%d&B1=Search", url, series))
                .method(Connection.Method.GET)
                .userAgent(userAgent)
                .execute();

        Document doc = response.parse();


        for (Element image : doc.select("table.imagetable tbody tr ")) {
            Elements details = image.select(".imagedetail");

            photos.add(
                    new Photo(
                            new URL(new URL(doc.baseUri()), image.select(".image").select("img").attr("src").replace("_T.", ".")),
                            new URL(new URL(doc.baseUri()), image.select(".image").select("img").attr("src")),
                            details.select("p:nth-child(2)").first().ownText(),
                            details.select("p:nth-child(4)").first().ownText(),
                            details.select("p:nth-child(5)").first().ownText(),
                            Integer.valueOf(details.select("p:nth-child(8) a").text())
                    )
            );
        }
        Elements links = doc.select("#content > form:nth-child(9) > p > a");

        for (final Element link : links) {
            if (!link.hasAttr("title")) {
                photos.addAll(new LinkedList<Photo>() {
                    {
                        LinkedList<Integer> numbers = new LinkedList<Integer>();
                        assert link.hasAttr("href");

                        Pattern p = Pattern.compile("\\d+");
                        Matcher m = p.matcher(link.attr("href"));
                        while (m.find()) {

                            numbers.add(Integer.valueOf(m.group()));
                        }
                        if (numbers.size() == 2) {

                            Document page = Jsoup.connect(format("%s?%d+%d-", url, numbers.get(1), numbers.get(0)))
                                    .cookies(response.cookies())
                                    .userAgent(userAgent)
                                    .get();

                            for (Element image : page.select("table.imagetable tbody tr ")) {
                                Elements details = image.select(".imagedetail");

                                this.add(
                                        new Photo(
                                                new URL(new URL(page.baseUri()), image.select(".image").select("img").attr("src").replace("_T.", ".")),
                                                new URL(new URL(page.baseUri()), image.select(".image").select("img").attr("src")),
                                                details.select("p:nth-child(2)").first().ownText(),
                                                details.select("p:nth-child(4)").first().ownText(),
                                                details.select("p:nth-child(5)").first().ownText(),
                                                Integer.valueOf(details.select("p:nth-child(8) a").text())
                                        )
                                );

                            }
                        }
                    }
                });
            }
        }
        return photos;
    }

    Collection<Photo> photosForSeries(Integer series) throws IOException {
        return collectPhotos(series);
    }
}
