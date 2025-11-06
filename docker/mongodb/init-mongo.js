// MongoDB initialization script
// This script runs when the MongoDB container is first created

db = db.getSiblingDB('cdi_database');

// Create the content collection
db.createCollection('content');

print('MongoDB database "cdi_database" and collection "content" initialized successfully');

