# Copyright (C) 2009 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := zzztimer



LOCAL_C_INCLUDES := $(LOCAL_PATH)/libogg/include \
$(LOCAL_PATH)/vorbis-android/include \
$(LOCAL_PATH)/vorbis-android/lib

#LOCAL_CFLAGS := -D__ANDROID__ 
#-I$(LOCAL_PATH)

$(warning Value of LOCAL_C_INCLUDES is '$(LOCAL_C_INCLUDES)') 
$(warning Value of LOCAL_CFLAGS is '$(LOCAL_CFLAGS)') 

LOCAL_LDLIBS := -L$(SYSROOT)/usr/lib -llog

LOCAL_SRC_FILES := zzztimer.cpp dtfm.cpp inflate_buff.cpp ogg_writer.cpp \
vorbis-android/lib/wav_ogg_file_codec_jni.c \
vorbis-android/lib/encode_file.c \
vorbis-android/lib/analysis.c \
vorbis-android/lib/registry.c \
vorbis-android/lib/vorbisenc.c \
vorbis-android/lib/bitwise.c \
vorbis-android/lib/framing.c \
vorbis-android/lib/bitrate.c  \
vorbis-android/lib/block.c  \
vorbis-android/lib/codebook.c  \
vorbis-android/lib/envelope.c  \
vorbis-android/lib/floor0.c \
vorbis-android/lib/floor1.c  \
vorbis-android/lib/info.c  \
vorbis-android/lib/lookup.c \
vorbis-android/lib/lpc.c \
vorbis-android/lib/lsp.c \
vorbis-android/lib/mapping0.c \
vorbis-android/lib/mdct.c \
vorbis-android/lib/psy.c \
vorbis-android/lib/res0.c \
vorbis-android/lib/sharedbook.c \
vorbis-android/lib/smallft.c \
vorbis-android/lib/synthesis.c \
vorbis-android/lib/vorbisfile.c \
vorbis-android/lib/window.c 


include $(BUILD_SHARED_LIBRARY)
