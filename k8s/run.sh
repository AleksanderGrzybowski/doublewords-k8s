#!/usr/bin/env bash

SEGMENTS_COUNT=5000

LOOP_END="$(($SEGMENTS_COUNT-1))"

echo "Scheduling jobs for ${SEGMENTS_COUNT} segments ..."

for i in `seq 0 ${LOOP_END}`; do
    export SEGMENTS_COUNT=${SEGMENTS_COUNT}
    export SELECTED_SEGMENT=${i}
    cat batch.yaml | envsubst | kubectl apply -f -
done
