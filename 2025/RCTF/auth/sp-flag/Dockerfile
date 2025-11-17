FROM python:3.12-slim

WORKDIR /app

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY saml2 /app/saml2
COPY templates /app/templates
COPY config.py fetch_metadata.py app.py ./

EXPOSE 5000

CMD ["python", "app.py"]
