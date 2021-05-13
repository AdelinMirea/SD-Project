# SD-Project

## Getting up and running

1. Have Docker and Kubernetes running
2. cd config
3. ./buildAndTag.sh
4. ./start.sh

Wait a couple of moment and check with "kubectl get pods". All the pods should be running.

For the app use http://localhost

Enter email and password and press sign up. Then press log in.

You should now be able to upload files. (Errors will appear if uploading files over a couple of mb).


If errors appear when running the shell scripts check for the endings to match your OS.
