# Build all images
./docker-build.sh "$1"

  docker login -u "$1" -p "$2"

readarray -t directories < <(find . -type f -name "Dockerfile")

for directory in "${directories[@]}"; do
  image_name="${directory%"/Dockerfile"}"
  image_name="${image_name##*/}"
  echo "pushing image for $image_name"
  docker push "$1"/$image_name:latest
done

echo "DONE"