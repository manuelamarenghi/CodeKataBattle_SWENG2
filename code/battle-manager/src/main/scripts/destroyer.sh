#!/usr/bin/bash

# $1 -> directory where the zip

if [ $# -ne 1 ]; then
    echo "[ERROR] Usage: $0 <directory>"
    exit 1
fi

cd || exit 1

rm -rf "$1"

exit 0