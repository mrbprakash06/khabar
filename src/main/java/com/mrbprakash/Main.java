package com.mrbprakash;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan("com.mrbprakash")
@PropertySource("classpath:application.properties")
public class Main {

    public static void main(String[] args) {
        try (var context = new AnnotationConfigApplicationContext(Main.class);
                var executorService = Executors
                        .newScheduledThreadPool(Runtime.getRuntime().availableProcessors());) {

            int downloadInterval = Integer.parseInt(context.getEnvironment().getProperty("server.download-interval"));

            for (var name : context.getBeanNamesForType(NewsDownloader.class)) {
                var downloader = ((NewsDownloader) context.getBean(name));
                executorService.scheduleWithFixedDelay(new NewsDownloadTask(downloader), 0, downloadInterval,
                        TimeUnit.MINUTES);
            }

            Thread.currentThread().join();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}