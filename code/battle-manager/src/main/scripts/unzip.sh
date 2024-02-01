#!/usr/bin/bash

cd || exit 1

zipFileName="$1"

unzip "$zipFileName"

unzipDir="${zipFileName%.*}"

echo "$unzipDir"

cd "$unzipDir" || exit 1

rm -rf .git/

readarray -t paths < <(find ./ -type f)

echo
echo "Unzip completed"

for path in "${paths[@]}"; do
	echo "$path"
done

cd || exit 1
# rm -rf "$zipFileName" # comment for testing

exit
