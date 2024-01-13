# Build all images
./docker-build.sh ${{ secrets.DOCKERHUB_USERNAME }} # username is just codekatabattle

docker login -u ${{ secrets.DOCKERHUB_USERNAME }} -p ${{ secrets.DOCKERHUB_TOKEN }}

readarray -t directories < <(find . -type f -name "Dockerfile")

for directory in "${directories[@]}"; do
  image_name="${directory%"/Dockerfile"}"
  image_name="${image_name##*/}"
  echo "pushing image for $image_name"
  docker push ${{ secrets.DOCKERHUB_USERNAME }}/$image_name:latest
done

echo "DONE"