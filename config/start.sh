# start mysql
kubectl apply -f ./mysql-configmap.yml
kubectl apply -f ./mysql-credentials.yml
kubectl apply -f ./mysql-root-credential.yml
kubectl apply -f ./mysql-deployment.yml


kubectl apply -f ./requests-configmap.yml
kubectl apply -f ./users-configmap.yml

# start minio
kubectl apply -f ./minio-configmap.yml
kubectl apply -f ./minio-deployment.yml

# start users service
kubectl apply -f ./users-deployment.yml

# start requests service
kubectl apply -f ./requests-deployment.yml

# start front-end service
kubectl apply -f ./angular-deployment.yml
