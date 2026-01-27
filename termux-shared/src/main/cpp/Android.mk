LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := local-socket
LOCAL_SRC_FILES := local-socket.cpp
LOCAL_LDLIBS := -llog

LOCAL_LDFLAGS += "-Wl,-z,max-page-size=16384"

include $(BUILD_SHARED_LIBRARY)