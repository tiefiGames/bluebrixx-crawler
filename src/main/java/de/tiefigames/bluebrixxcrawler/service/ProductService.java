package de.tiefigames.bluebrixxcrawler.service;

import de.tiefigames.bluebrixxcrawler.client.BlueBrixxCrawler;
import de.tiefigames.bluebrixxcrawler.entity.Product;
import de.tiefigames.bluebrixxcrawler.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    private static Logger logger = LoggerFactory.getLogger(ProductService.class);

    private BlueBrixxCrawler blueBrixxCrawler;

    private ProductRepository productRepository;

    @Autowired
    public ProductService(BlueBrixxCrawler blueBrixxCrawler, ProductRepository productRepository) {
        this.blueBrixxCrawler = blueBrixxCrawler;
        this.productRepository = productRepository;
    }

    public Product addProduct(String productUrl) {
        logger.info("Check new product with URL {} already exists", productUrl);

        Product product = productRepository.findProductByUrl(productUrl);
        if (product == null) {
            product = blueBrixxCrawler.getProduct(productUrl);
            productRepository.save(product);
            logger.info("Add new product with ID {} and Setnumber {}", product.getId(), product.getSetNumber());
        } else {
            logger.info("Product already exists with ID {}", product.getId());
        }

        return product;
    }

}
