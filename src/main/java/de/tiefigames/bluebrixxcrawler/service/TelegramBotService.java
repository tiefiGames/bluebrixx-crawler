package de.tiefigames.bluebrixxcrawler.service;

import de.tiefigames.bluebrixxcrawler.client.TelegramBotClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class TelegramBotService {

    private static Logger logger = LoggerFactory.getLogger(TelegramBotService.class);

    private TelegramBotClient telegramBotClient;

    @Autowired
    public TelegramBotService(@Lazy TelegramBotClient telegramBotClient) {
        this.telegramBotClient = telegramBotClient;
    }

    public void sendMessage(String message) {
        telegramBotClient.sendMessage(message);
    }
}
