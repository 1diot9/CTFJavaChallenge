FROM python:3.11-slim

ENV PYTHONUNBUFFERED=1 \
    PYTHONDONTWRITEBYTECODE=1 \
    PIP_NO_CACHE_DIR=1

RUN apt-get update && apt-get install -y \
    sqlite3 \
    curl \
    && rm -rf /var/lib/apt/lists/*

RUN groupadd -r -g 1000 marketflow && \
    useradd -r -u 1000 -g marketflow -m -d /home/marketflow -s /bin/bash marketflow

WORKDIR /app

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

RUN mkdir -p /var/tmp/sessionmaze/uploads \
    /var/tmp/sessionmaze/reports \
    /var/tmp/sessionmaze/cache \
    /var/tmp/sessionmaze/templates && \
    chown -R marketflow:marketflow /var/tmp/sessionmaze && \
    chmod -R 777 /var/tmp/sessionmaze

RUN mkdir -p /srv && \
    echo "flag{fake_flag}" > /flag.txt && \
    chown root:root /flag.txt && \
    chmod 444 /flag.txt

COPY --chown=root:root . /app

RUN chown -R root:root /app && \
    chmod -R 555 /app && \
    chmod 555 /app/app.py && \
    find /app -type f -name "*.py" -exec chmod 444 {} \;

RUN mkdir -p /app/instance && \
    chown marketflow:marketflow /app/instance && \
    chmod 755 /app/instance

RUN touch /var/log/marketflow.log && \
    chown marketflow:marketflow /var/log/marketflow.log && \
    chmod 644 /var/log/marketflow.log

USER marketflow

EXPOSE 5000

HEALTHCHECK --interval=30s --timeout=10s --start-period=5s --retries=3 \
    CMD curl -f http://localhost:5000/ || exit 1

CMD ["python", "-u", "app.py"]
