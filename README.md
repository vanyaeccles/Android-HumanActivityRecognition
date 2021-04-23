
# Android-HumanActivityRecognition

An Android app that uses a Convolutional Neural Network (CNN) model to classify time series of accelerometer data into human activities (walking, sitting, going upstairs/downstairs) in real time. The app streams the classification data to a UDP socket running on a device in the local network.

## The CNN Model used in the app

Adapted from the paper [Real-time human activity recognition from accelerometer data using Convolutional Neural Networks](https://www.sciencedirect.com/science/article/abs/pii/S1568494617305665) and [associated code repository](https://github.com/aiff22/HAR) by Andrey Ignatov.

- Trained on the [WISDM dataset](http://www.cis.fordham.edu/wisdm/dataset.php) - IMU time series data labelled into human activities.


## WisdmTestApp

The Android Studio app (originally written as part of a project for [Fitreo](https://fitreo.com/)). The app gathers phone IMU data into a limited size queue and pre-processes the data into statistical features (as specified in the paper by Ignatov). The app feeds a time series sample along with the statistical features through the CNN.

The output classifications are then streamed through a UDP stream to a UDP socket running on a computer in the local network.

- uses the TensorFlowInferenceInterface to read the CNN model as a tensorflow protobuf (.pb - a file that contains the graph definition and model weights).

## Networking

Some python scripts to be run on a computer in the local network. They recieve the data being sent over the local network by the Android phone provided port forwarding is set up on the local router. Classification data is saved in csv in the 'Data' directory and printed to the console of the computer.
