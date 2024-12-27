package com.mrbprakash;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NewsService {
    private final NewsRepository newsRepository;

    @Autowired
    public NewsService(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    public News saveNews(News news) {
        return newsRepository.save(news);
    }

    public Optional<News> getLastNewsByProvider(String provider) {
        return newsRepository.findOneByProvider(provider);
    }

}
