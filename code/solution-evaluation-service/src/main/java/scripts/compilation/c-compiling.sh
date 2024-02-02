#!/usr/bin/bash

if [ "$#" -lt 1 ]; then
    echo "Not enough arguments provided. Usage: $0 <path>"
    exit 1
fi

cd "$1" || exit 1

gcc -o executable main.c -O2

readarray -t array < <(find . -type f -name "executable")

if (( ${#array[@]} )); then
    echo "Compilation successful"
    exit 0
else
    echo "Compilation error"
    exit 1
fi