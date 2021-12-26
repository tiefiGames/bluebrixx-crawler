#!/bin/sh

# mvn clean


docker network create bbrixx_network

docker build . -t bluebrixx_crawler

docker-compose up -f infrastructure/chrome_driver/docker-compose.yml -d

