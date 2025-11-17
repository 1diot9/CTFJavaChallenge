FROM php:7.4-apache

WORKDIR /var/www/html

RUN apt-get update && apt-get install -y \
    curl \
    git \
    unzip \
    libpng-dev \
    libjpeg-dev \
    libfreetype6-dev \
    libonig-dev \
    libxml2-dev \
    zip \
    gnupg \
    && rm -rf /var/lib/apt/lists/*


RUN docker-php-ext-install pdo pdo_mysql mysqli mbstring

RUN a2enmod rewrite

COPY . /var/www/html/

RUN mv 000-default.conf /etc/apache2/sites-available/


RUN chown -R www-data:www-data /var/www/html \
    && chmod -R 755 /var/www/html

EXPOSE 80

CMD ["apache2-foreground"]


