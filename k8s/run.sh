#!/usr/bin/env bash

for i in `seq 0 9`; do
	export SEGMENTS_COUNT=10
	export SELECTED_SEGMENT=${i}
	cat batch.yaml | envsubst | kubectl apply -f -
done
