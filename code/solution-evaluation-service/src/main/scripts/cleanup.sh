#!/usr/bin/env bash

if [ $# -lt 1 ]; then
    echo "Not enough arguments provided. Usage: $0 <path>"
    exit 1
fi

rm -rf "$1"

echo "clean up successful"