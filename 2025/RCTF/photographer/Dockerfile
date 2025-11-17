FROM php:8.4-apache

RUN apt-get update && apt-get install -y \
    libsqlite3-dev \
    libfreetype6-dev \
    libjpeg62-turbo-dev \
    libpng-dev \
    && docker-php-ext-configure gd --with-freetype --with-jpeg \
    && docker-php-ext-install -j$(nproc) gd exif pdo pdo_sqlite \
    && a2enmod rewrite \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*


WORKDIR /var/www/html

COPY . /var/www/html/

RUN mv 000-default.conf /etc/apache2/sites-available/000-default.conf \
    && chmod -R 755 /var/www/html \
    && chmod -R 777 /var/www/html/public/uploads \
    && chmod 777 /var/www/html/database \
    && chmod 666 /var/www/html/database/photographer.db 2>/dev/null

EXPOSE 80

