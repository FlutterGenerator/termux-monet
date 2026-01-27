LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE:= libnative-entrypoint
LOCAL_SRC_FILES:= main.cpp
LOCAL_LDLIBS:= -llog -landroid -ldl

LOCAL_LDFLAGS += "-Wl,-z,max-page-size=16384"

include $(BUILD_SHARED_LIBRARY)