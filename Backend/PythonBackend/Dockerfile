# Use the official Python image
FROM python:3.11-slim

# Set the working directory in the container
WORKDIR /app

# Copy requirements.txt into the container
COPY requirements.txt /app/

# Install dependencies
RUN pip install --no-cache-dir -r requirements.txt

# Copy the rest of the application code
COPY . /app

# Expose the port your Flask app runs on
EXPOSE 5000

# Set the environment variable for Flask
ENV FLASK_APP=app.py
ENV FLASK_ENV=production

# Command to run the Flask app
CMD ["flask", "run", "--host=0.0.0.0", "--port=5000"]
