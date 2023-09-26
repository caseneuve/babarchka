#!/bin/sh

echo "############ Hello from Docker! ##############"

source ./common.sh

log "Running install-yay.sh"
./install-yay.sh

log "Running install-yay.sh"
./install-bb.sh

echo "############ Goodbye Docker! ##############"
