package de.tiefigames.bluebrixxcrawler.client;

import de.tiefigames.bluebrixxcrawler.entity.Notify;
import de.tiefigames.bluebrixxcrawler.entity.TelegramInfo;
import de.tiefigames.bluebrixxcrawler.repository.TelegramInfoRepository;
import de.tiefigames.bluebrixxcrawler.service.NotifyService;
import org.openqa.selenium.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
public class TelegramBotClient extends TelegramLongPollingBot {

    private static Logger logger = LoggerFactory.getLogger(TelegramBotClient.class);

    @Value("${telegram.token}")
    private String token;

    @Value("${telegram.username}")
    private String username;

    private NotifyService notifyService;

    private TelegramInfoRepository telegramInfoRepository;

    private Long chatId;

    @Autowired
    public TelegramBotClient(@Lazy NotifyService notifyService, TelegramInfoRepository telegramInfoRepository) {
        this.notifyService = notifyService;
        this.telegramInfoRepository = telegramInfoRepository;
    }

    @PostConstruct
    public void start() throws Exception{
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(this);
    }

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public void onUpdateReceived(Update update) {
        String message = update.getMessage().getText();
        logger.info("Update {}", message);

        setChatId(update);

        if (message.startsWith("/notify")) {
            try {
                Notify notify = notifyService.addNotify(message.replace("/notify", "").trim());
                String responseMessage = String.format("Notify with ID: %s and Setnumber %s created/updated", notify.getId(), notify.getProduct().getSetNumber());
                sendMessage(responseMessage);
            } catch (TimeoutException e) {
                sendMessage("Got timeout failure to add new notify");
            }
        } else if (message.equals("/watchlist")) {
            List<Notify> notifyList = notifyService.getWatchlist();
            StringBuilder builder = new StringBuilder("Watchlist:\n\n");
            for (Notify notify : notifyList) {
                builder.append(String.format("Set: %s\nSetnumber: %s\nStatus: %s\nNotify_ID: %s\n%s\n\n", notify.getProduct().getName(),
                        notify.getProduct().getSetNumber(), notify.getProduct().getProductStatus(), notify.getId(),
                        notify.getProduct().getUrl()));
            }
            sendMessage(builder.toString());
        }
    }

    public void sendMessage(String message) {
        if (chatId == null) {
            chatId = telegramInfoRepository.findAll().iterator().next().getChatId();
        }
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(message);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            logger.error("Failed to send message \"{}\" to {} due to error: {}", message, chatId, e.getMessage());
        }
    }

    private void setChatId(Update update) {
        chatId = update.getMessage().getChatId();
        TelegramInfo telegramInfo = new TelegramInfo();
        telegramInfo.setChatId(chatId);

        // TODO besseren Mechanismus dafür finden, sodass wirklich immer nur ein Eintrag existiert
        telegramInfoRepository.deleteAll();
        telegramInfoRepository.save(telegramInfo);
    }




}
