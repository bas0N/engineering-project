from flask import  current_app
from kafka import KafkaConsumer
# import threading

# def consume_kafka_messages():
#     consumer = KafkaConsumer(
#         'your-topic-name',  # Replace with your Kafka topic
#         bootstrap_servers=['localhost:9092'],  # Replace with your Kafka server address
#         auto_offset_reset='earliest',
#         enable_auto_commit=True,
#         group_id='your-group-id',
#         value_deserializer=lambda x: x.decode('utf-8')
#     )

#     for message in consumer:
#         print(f"Received: {message.value}")

# # Start the consumer in a separate thread
# thread = threading.Thread(target=consume_kafka_messages)
# thread.daemon = True
# thread.start()