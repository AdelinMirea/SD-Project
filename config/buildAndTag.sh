pushd ../File-Storage
docker build -t filestorage-users .
docker tag filestorage-users localhost:5000/dev/filestorage-users
docker push localhost:5000/dev/filestorage-users
kubectl delete service filestorage-users
kubectl delete deployment filestorage-users
kubectl apply -f users-deployment.yml
popd

pushd ../requests
docker build -t filestorage-requests .
docker tag filestorage-requests localhost:5000/dev/filestorage-requests
docker push localhost:5000/dev/filestorage-requests
kubectl delete service filestorage-requests
kubectl delete deployment filestorage-requests
kubectl apply -f requests-deployment.yml
popd

pushd ../file-transfer-web
docker build -t filestorage-web .
docker tag filestorage-web localhost:5000/dev/filestorage-web
docker push localhost:5000/dev/filestorage-web
kubectl delete service filestorage-web
kubectl delete deployment filestorage-web
kubectl apply -f angular-deployment.yml
popd

