#!/bin/sh

log() {
    printf "[BABARCHKA] $(date -Iseconds):\t%s\n" "$@"
}

check_cmd() {
    if type -p $1 >/dev/null; then
        log "'$1' has been installed! :D"
    elif ! [ -z "$2" ]; then
        log "Something went wrong! :((("
        exit $2
    else
        log "Need to install $1..."
    fi
}

export -f log
export -f check_cmd
