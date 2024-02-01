#!/usr/bin/bash

# $1 -> zip directory name (on home dir)
# $2 -> file path in the zip directory
# $3 -> directory where the zip file was extracted

cd || exit 1

cd "$3" || exit 1

cat "$2"