#!/bin/bash

set -e

source ./pre/common.sh

# using binary to avoid installing go
PKG=yay-bin
CMD=yay

check_cmd $CMD

CWD=$(pwd)
log "CWD was $CWD"
cd

log "Installing git"
sudo pacman -S --needed --noconfirm git

log "Installing $PKG"
git clone https://aur.archlinux.org/$PKG.git \
    && cd yay-bin \
    && makepkg -sri --noconfirm \
    && cd \
    && rm -rf .cache yay-bin

check_cmd $CMD 1
log "$(yay --version)"

log "Going back to $CWD"
cd "$CWD"

log "Generating a development package database for *-git packages that were installed without yay. This command should only be run once"
yay -Y --gendb

log "Checking for development package updates"
yay -Syu --devel --noconfirm

log "Making development package updates permanently enabled"
yay -Y --devel --save
