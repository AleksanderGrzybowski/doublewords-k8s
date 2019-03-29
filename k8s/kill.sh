#! /bin/bash

kubectl get jobs | awk '{print $1}' | grep doublewords | xargs kubectl delete job
