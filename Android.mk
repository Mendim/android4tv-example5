LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)

SHELL_RESULT := $(shell ($(LOCAL_PATH)/embed_version.sh))

LOCAL_PACKAGE_NAME := android4tv-example1

LOCAL_MODULE_TAGS := optional
LOCAL_CERTIFICATE := platform

LOCAL_SRC_FILES := $(call all-java-files-under, src)
LOCAL_SRC_FILES += $(call all-Iaidl-files-under, src)

LOCAL_STATIC_JAVA_LIBRARIES := \
    com.iwedia.dtv.framework.service

include $(BUILD_PACKAGE)
