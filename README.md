# Photo Sanitizer
![](https://media0.giphy.com/media/v1.Y2lkPTc5MGI3NjExcmNjd2FxNzk4MXJzOTUyMGZpY2EwNWJibjhmYmVjMjIzYm05eTZ1NCZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/BRN2Xi0MqnjjO/giphy.gif)

A Java application for cleaning EXIF metadata from images in a specific folder.

## Usage

0. Install java compiler and git if there hasn't been, for example on debian 
``` bash
# update packages information and install via apt
sudo apt update
sudo apt install openjdk-21-jdk git

# check correct installation 
javac --version
git -v
```
1. Clone the repository
``` bash
git clone https://github.com/YarBurArt/exif-meta-cleaner.git
cd exif-meta-cleaner/src
```
2. Compile the source code using
```
javac Main.java GUIMetaCleaner.java
```
3. Run the application using `java Main` or `java GUIMetaCleaner.java` =>

  You can use MetaCleaner through the console interface:
```sh
# to print help message
java Main --help
# to set path start at working dir
java Main --path ./to/images/  
```
Alternatively, you can use the graphical interface, which provides a similar functionality and is easy to use. But it doesn't work as fast.

Here is not a complicated source code, better read it before running it to be more confident in its behavior, quality and functionality.

## Overview

MetaCleaner is a simple Java application that allows users to remove EXIF metadata from their images. 
The application consists of two main classes: `Main.java` and `GUIMetaCleaner.java`. 
The main difference between MetaCleaner and other similar tools is its ability to process entire folders, making it easy to clean metadata from large collections of images. 
Designed with safety and speed in mind, MetaCleaner provides a fast and secure way to remove sensitive metadata from your images, protecting your privacy and security.
It does not remove objects in the image itself, such as selfies in the background of a certain address, where it is possible to calculate PII. 
But in the future, local ML algorithms will be added for auto-covering this.
