

# Funktionen vom BlueBrixx Crawler 

## 1. Produkt URL über Telegram an Crawler senden

An den Telegram Bot kann eine Produkt URL gesendet werden mit `/notify {URL}` die dann vom Crawler eingelesen wird 
und in der Datenbank persistiert wird. Der Befehl gibt nach erfolreichem hinzufügen eine ID zurück über die 
dann für weitere Befehle verwendet werden soll.


## 2. Crawler Scheduler

Der Crawler hat einen konfigurierten Scheduler der in bestimmten Abständen die Prudkte aus der Datenbank 
einliest und die Webseiten aufruft. Sollten die Produkte verfügbar sein, wird sofort eine Telegram Nachricht 
an den Bot versendet. Der Status Wechsel wird in der Datenbank vermerkt und es werden keine weiteren Nachrichten an den 
Bot zu dem Produkt versendet.


## 3. Abrufen der Watchlist über den Bot

Mit dem Befehl `/watchlist` kann die aktuelle Produkt Watchliste abgerufen werden 


## 4. Automatischer Einkauf

Über den Telegram Bot kann ein automatischer Einkauf getätigt werden. Hierfür muss der Befehl `/buy {URL}` ausgeführt
werden.

## 5. Löschen von Produkten 

Der Befehl `/remove {id}` nimmt eine ID entgegen und löscht die Einträge in der Datenbank.


