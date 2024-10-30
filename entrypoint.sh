#!/bin/bash

# Install curl if it's not already installed
if ! command -v curl &>/dev/null; then
  echo "Installing curl..."
  apt-get update && apt-get install -y curl
fi

# Start ollama in the background
ollama serve &

# Loop until ollama is ready to accept commands
until curl -s http://localhost:11434 > /dev/null; do
  echo "Waiting for ollama to start..."
  sleep 5
done

# Once ready, pull the required models
ollama pull all-minilm:latest
ollama pull mistral-small:latest

# Keep the server running in the foreground
wait
