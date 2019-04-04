#!/usr/bin/env bash

. ./common.sh

SINK_SERVER_IP="92.222.76.126"

export SINK_URL="http://${SINK_SERVER_IP}:8888/store"
export SEGMENTS_COUNT=40

LOOP_START=0
LOOP_END="$(($SEGMENTS_COUNT-1))"

echo "Scheduling jobs for ${SEGMENTS_COUNT} segments ..."

for i in `seq ${LOOP_START} ${LOOP_END}`; do
    export SELECTED_SEGMENT=${i}
    cat batch.yaml | envsubst | kubectl apply -f -
done
