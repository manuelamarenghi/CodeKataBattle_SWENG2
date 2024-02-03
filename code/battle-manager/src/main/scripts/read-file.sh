#!/usr/bin/bash

# $1 -> file path in the zip directory
# $2 -> directory where the zip file was extracted

if [ $# -ne 2 ]; then
    echo "[ERROR] Usage: $0 <path of the file to read> <directory where the zip file was extracted>"
    exit 1
fi

cd || exit 1

cd "$2" || exit 1

cat "$1"