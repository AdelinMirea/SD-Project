kubectl delete service filestorage-web
kubectl delete service filestorage-users
kubectl delete service filestorage-requests
kubectl delete service mysql
kubectl delete service minio

kubectl delete deploy filestorage-web
kubectl delete deploy filestorage-users
kubectl delete deploy filestorage-requests
kubectl delete deploy mysql
kubectl delete deploy minio

kubectl delete job minio-create-bucket