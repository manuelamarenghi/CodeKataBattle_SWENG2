#!/usr/bin/bash

# $1 -> zip file name
# $2 -> random string to create a unique folder

cd || exit 1

zipFilePath="$1"

mkdir "$2"

mv "$1" "$2"

cd "$2" || exit 1

zipFileName="${zipFilePath##*/}"

unzip "$zipFileName"

unzipDir=$(pwd)

cd "$unzipDir" || exit 1

rm -rf .git/

readarray -t paths < <(find ./ -type f)

echo
echo "Unzip completed"

for path in "${paths[@]}"; do
	echo "$path"
done

cd || exit 1

exit
