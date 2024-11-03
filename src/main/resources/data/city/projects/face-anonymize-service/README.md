# Face Anonymize Service

This microservice is responsible for exposing APIs for clients to detect faces in the photo and anonymize (blur) them.<br>
The service doesn't store the photo and its information. Only storing in the memory while processing.

# Model data
The service uses YuNet to detect faces. Please see https://github.com/opencv/opencv_zoo.
If you run the service in the local environment, you need to download the model file into the execution directory.
https://github.com/opencv/opencv_zoo/raw/master/models/face_detection_yunet/face_detection_yunet_2022mar.onnx

## Build and run the project with Bazel
```
cd projects/face-anonymize-service
bazel build face-anonymize-service
bazel run binary -- -c=$(pwd)/local/config/config.toml
```
