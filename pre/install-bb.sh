#!/bin/bash

set -e
source ./pre/common.sh

CMD=bb
PKG=babashka-bin

check_cmd $CMD

log "Installing $PKG"
yay -S --noconfirm $PKG

check_cmd $CMD 1
log "$(bb --version)"
