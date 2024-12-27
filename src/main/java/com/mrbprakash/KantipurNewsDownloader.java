package com.mrbprakash;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class KantipurNewsDownloader implements NewsDownloader {

    @Value("${mail.from}")
    private String from;

    private final String baseUrl = "https://ekantipur.com/news";
    private final NewsService newsService;
    private final JavaMailSender mailSender;
    private final TemplateEngine emailTemplateEngine;

    @Autowired
    public KantipurNewsDownloader(NewsService newsService, JavaMailSender mailSender,
            TemplateEngine emailTemplateEngine) {
        this.newsService = newsService;
        this.mailSender = mailSender;
        this.emailTemplateEngine = emailTemplateEngine;
    }

    @Override
    public void download() {
        var last = getLastNews();

        try {
            var newsList = fetchNews();
            if (last.isPresent()) {
                var lastNews = last.get();
                newsList = newsList.stream().filter(n -> n.getSequence() > lastNews.getSequence()).toList();

            }
            for (var latestNews : newsList) {
                newsService.saveNews(latestNews);
            }

            log.info(newsList.size() + " latest news found " + baseUrl);

            if (newsList.size() > 0) {
                try {
                    String emailText = prepareEmailText(newsList);
                    sendEmail(emailText);
                    log.info("Email sent " + baseUrl);
                } catch (Exception e) {
                    log.error("Error sending email " + baseUrl, e);
                }
            }
        } catch (IOException e) {
            log.error("Error downloading " + baseUrl, e);
        }
    }

    private String prepareEmailText(List<News> newsList) {
        Context context = new Context();
        context.setVariable("newsList", newsList);

        return emailTemplateEngine.process("news", context);
    }

    private void sendEmail(String text) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        helper.setText(text, true);
        helper.setSubject("Your News");
        helper.setTo("mrbprakash06@gmail.com");
        helper.setFrom(from);

        mailSender.send(message);
    }

    private List<News> fetchNews() throws IOException {
        log.info("Download started for " + baseUrl);

        Document document = Jsoup.connect(baseUrl)
                .header("Host", "ekantipur.com")
                .header("User-Agent", "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:133.0) Gecko/20100101 Firefox/133.0")
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .header("Accept-Language", "en-US,en;q=0.5")
                .header("Connection", "keep-alive")
                .header("Upgrade-Insecure-Requests", "1")
                .header("Sec-Fetch-Dest", "document")
                .header("Sec-Fetch-Mode", "navigate")
                .header("Sec-Fetch-Site", "none")
                .header("Sec-Fetch-User", "?1")
                .header("Priority", "u=0, i")
                .get();

        Elements articles = document.select("article.normal");
        List<News> newsList = new ArrayList<>();

        for (Element article : articles) {
            Long sequence = Long.parseLong(article.select("a.saveNewsLink").attr("attr-newsid"));

            Element titleElement = article.selectFirst("h2 a");
            String title = titleElement != null ? titleElement.text() : "";

            Element summaryElement = article.selectFirst("p");
            String summary = summaryElement != null ? summaryElement.text() : "";

            String url = titleElement != null ? titleElement.absUrl("href") : "";

            var news = News.builder()
                    .sequence(sequence)
                    .title(title)
                    .summary(summary)
                    .url(url)
                    .provider(baseUrl)
                    .build();

            newsList.add(news);
        }

        log.info("Download completed for " + baseUrl);

        return newsList;
    }

    private Optional<News> getLastNews() {
        return newsService.getLastNewsByProvider(this.baseUrl);
    }

}