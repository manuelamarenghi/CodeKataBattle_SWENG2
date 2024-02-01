#!/usr/bin/bash

cd

zipFileName="$1"

unzip $zipFileName

unzipDir="${zipFileName%.*}"

echo $unzipDir

cd $unzipDir || exit 1

rm -rf .git/

readarray -t paths < <(find ./ -type f)

echo
echo "Unzip completed"

for path in "${paths[@]}"; do
	fileName=${path##*/}
	echo $path
	
	string=$(printf "%s" "$(cat $path)")
	echo $string
done

cd
rm -rf "$unzipDir"
# rm -rf "$zipFileName" # comment for testing

exit
