#! /bin/bash

. ./common.sh
kubectl get jobs | awk '{ print $1 }' | grep ${JOB_PREFIX} | xargs kubectl delete job
