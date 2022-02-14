package de.tiefigames.bluebrixxcrawler.service;

import de.tiefigames.bluebrixxcrawler.client.BlueBrixxCrawler;
import de.tiefigames.bluebrixxcrawler.entity.ChangeHistory;
import de.tiefigames.bluebrixxcrawler.entity.Notify;
import de.tiefigames.bluebrixxcrawler.entity.Product;
import de.tiefigames.bluebrixxcrawler.entity.ProductStatus;
import de.tiefigames.bluebrixxcrawler.repository.ChangeHistoryRepository;
import de.tiefigames.bluebrixxcrawler.repository.NotifyRepository;
import org.openqa.selenium.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotifyService {

    private static Logger logger = LoggerFactory.getLogger(NotifyService.class);

    private NotifyRepository notifyRepository;

    private ProductService productService;

    private BlueBrixxCrawler blueBrixxCrawler;

    private TelegramBotService telegramBotService;

    private ChangeHistoryRepository changeHistoryRepository;

    public NotifyService(NotifyRepository notifyRepository, ProductService productService,
                         BlueBrixxCrawler blueBrixxCrawler, @Lazy TelegramBotService telegramBotService,
                         ChangeHistoryRepository changeHistoryRepository) {
        this.notifyRepository = notifyRepository;
        this.productService = productService;
        this.blueBrixxCrawler = blueBrixxCrawler;
        this.telegramBotService = telegramBotService;
        this.changeHistoryRepository = changeHistoryRepository;
    }

    public Notify addNotify(String productUrl) {
        logger.info("Add new notify for product with URL {}", productUrl);

        Product product = productService.addProduct(productUrl);
        Notify notify = notifyRepository.findByProduct(product);

        if (notify == null) {
            notify = new Notify();
            notify.setCreated(LocalDateTime.now());
            notify.setProduct(product);
            notifyRepository.save(notify);
            logger.info("Add new notify with ID {}", notify.getId());
        } else {
            logger.info("Notify already exists with ID {}", notify.getId());
        }

        return notify;
    }

    @Scheduled(cron = "* */4 7-18 * * MON-SAT")
    private void checkNotifies() throws InterruptedException {
        try {
            List<Notify> notifyList = (List<Notify>) notifyRepository.findAll();
            if (!notifyList.isEmpty()) {
                logger.info("Start check notified Product availability");
                for (Notify notify : notifyList) {
                    logger.info("Check notify with id {} and setnumber {}", notify.getId(), notify.getProduct().getSetNumber());
                    ProductStatus productStatus = blueBrixxCrawler.getProductStatus(notify.getProduct().getUrl());
                    if (productStatus == ProductStatus.AVAILABLE) {
                        logger.info("Status changed for notify id {} from {} to {}", notify.getId(), notify.getProduct().getProductStatus(), productStatus);

                        // Save ChangeHistory
                        ChangeHistory changeHistory = new ChangeHistory();
                        changeHistory.setCreated(LocalDateTime.now());
                        changeHistory.setOldProductstatus(notify.getProduct().getProductStatus());
                        changeHistory.setNewProductstatus(productStatus);
                        changeHistory.setProduct(notify.getProduct());
                        changeHistoryRepository.save(changeHistory);

                        // Set new Status for Product
                        notify.getProduct().setProductStatus(productStatus);

                        // send message to Telegram
                        String message = String.format("AVAILABLE: Set %s with number %s\n %s", notify.getProduct().getName(), notify.getProduct().getSetNumber(), notify.getProduct().getUrl());
                        telegramBotService.sendMessage(message);

                        // Remove Notify after inform user
                        notifyRepository.delete(notify);
                    }
                    Thread.sleep((long) (Math.random() * (3000 - 1500) + 1500));
                }
            }
        } catch (TimeoutException e) {
            logger.info("Got Timeout by checking notified Product");
        }
    }

    public List<Notify> getWatchlist() {
        logger.info("Fetch all notfied products");
        return (List<Notify>) notifyRepository.findAll();
    }





}
