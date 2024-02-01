#!/usr/bin/bash

# $1 -> zip directory name (on home dir)
# $2 -> file path in the zip directory

cd || exit 1

cd "$1" || exit 1

cat "$2"