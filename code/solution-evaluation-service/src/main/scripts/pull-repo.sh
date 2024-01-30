#!/usr/bin/bash
cd || exit 1

rm -rf "$2" # delete any repo with $2 name that was for some reason left behind

if [ "$#" -lt 2 ]; then
    echo "Not enough arguments provided. Usage: $0 <repo_url> <repo_name>"
    exit 1
fi

git clone "$1" "$2" || exit 1

cd "$2" || exit 1

pwd
