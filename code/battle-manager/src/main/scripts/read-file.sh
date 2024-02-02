#!/usr/bin/bash

# $1 -> zip directory name (on home dir)
# $2 -> file path in the zip directory
# $3 -> directory where the zip file was extracted

if [ $# -ne 3 ]; then
    echo "[ERROR] Usage: $0 <zip directory name i.e. random string> <path of the file to reade> <directory where the zip file was extracted>"
    exit 1
fi

cd || exit 1

cd "$3" || exit 1

cat "$2"