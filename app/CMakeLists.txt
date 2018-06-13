cmake_minimum_required(VERSION 3.4.1)

set(CMAKE_VERBOSE_MAKEFILE on)
set(OPENCV_LIBS ${CMAKE_CURRENT_SOURCE_DIR}/../opencv/src/main/jniLibs)

include_directories(../opencv/sdk/native/jni/include)

add_library(
        detector
        SHARED
        src/main/cpp/DetectionBasedTracker_jni.cpp )

add_library( opencv SHARED IMPORTED )
set_target_properties( opencv PROPERTIES IMPORTED_LOCATION ${OPENCV_LIBS}/${ANDROID_ABI}/libopencv_java3.so )

find_library(
        log-lib
        log )

target_link_libraries(
        detector
        opencv
        ${log-lib} )