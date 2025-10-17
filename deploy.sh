#!/usr/bin/env bash

set -e  # exit if any command fails

EC2_USER="ec2-user"
EC2_HOST="16.171.135.215"
EC2_KEY="$HOME/.ssh/needapp-key.pem"
EC2_PATH="/home/ec2-user/needapp.jar"
SERVICE_NAME="needapp"

echo "🚀 Building JAR..."
./mvnw clean package -DskipTests

echo "📦 Copying JAR to EC2..."
scp -i "$EC2_KEY" target/needapp-0.0.1-SNAPSHOT.jar $EC2_USER@$EC2_HOST:$EC2_PATH

echo "🔄 Restarting service on EC2..."
ssh -i "$EC2_KEY" $EC2_USER@$EC2_HOST "sudo systemctl daemon-reload && sudo systemctl restart $SERVICE_NAME && sudo systemctl status $SERVICE_NAME --no-pager"

echo "✅ Deployment complete!"
