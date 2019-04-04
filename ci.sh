#! /bin/bash
IMAGE="kelog/doublewords:latest"

docker build -t ${IMAGE} .
docker push ${IMAGE} 
