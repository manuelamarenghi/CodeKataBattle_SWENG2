#!/usr/bin/bash

# <(command) is provides a pipe (file-like object)
# -t trims the strings
readarray -t locations < <(find . -type f -name "Dockerfile")

# delete everything after the last '/', i.e., delete "/Dockerfile"
for ((i=0; i<${#locations[@]}; i++)); do
    locations[$i]=${locations[$i]%/*}
done

# exit if no Dockerfiles are found
if [ ${#locations[@]} -eq 0 ]; then
	echo "No dockerfiles found in the children of $(pwd)"
	exit 1
fi

# exit if user did not provide an ID ~ should not be codekatabattle if the image is for testing
if [ "$#" -eq 0 ]; then
    echo "No arguments provided. Usage: $0 <image_id>"
    exit 1
fi

# finally build the images
for location in "${locations[@]}"; do
	image_name=${location##*/}
	echo "Building image for $image_name"
	echo "at location $location"
	docker build "${location}" -t "$1"/"$image_name"
	echo
done
