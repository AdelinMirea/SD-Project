pushd ../file-transfer-web
docker build -t filestorage-web .
docker tag filestorage-web localhost:5000/dev/filestorage-web
docker push localhost:5000/dev/filestorage-web; kubectl delete service filestorage-web
kubectl delete deployment filestorage-web
kubectl apply -f angular-deployment.yaml
popd