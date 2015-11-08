# This is a sample makefile written for the Linux platform
# Equivalent makefiles can be written for AIX and Solaris

TOPDIR=.

OS_NAME=LINUX
OS_TYPE=_UNIX
OS_VENDOR=REDHAT
OS_ARCH=x86
MAKE_ARCH=LINUX
ARCH=linux

LINUX_CPPFLAGS=-DLITTLEENDIAN  -DHANDLE_IS_32BITS

LINUX_RELEASEFLAGS=-g -DNDEBUG          #Release build
LINUX_DEBUGFLAGS=-g -D_DEBUG -DDEBUG    #Debug Build
LINUX_FLAGS=${LINUX_DEBUGFLAGS}

LINUX_CC=cc
LINUX_CDEBUG=
LINUX_CFLAGS=-pthread
LINUX_CFLAGS_SO=-fPIC

LINUX_CPP_INCLUDE=-I/usr/include/nptl -I../../inc/common
LINUX_CCC=g++
LINUX_CCDEBUG=
LINUX_CCFLAGS=-pthread
LINUX_CCFLAGS_SO=-fPIC

LINUX_RESOLVE_SYMBOL=
LINUX_LDFLAGS=
LINUX_LDFLAGS_EXE=
LINUX_LDFLAGS_SO=-shared
LINUX_LIBS=-L../../lib/$(ARCH) -lwinux -lstts
CLIB=-L/lib/tls -lpthread -ldl -lstdc++

.SUFFIXES: .c .cpp .h .a .o
SYS_CPPFLAGS=${${MAKE_ARCH}_CPPFLAGS}
CPPFLAGS=\
        -D${OS_VENDOR} \
        -D${OS_TYPE} \
        -D${OS_ARCH} \
        -D${OS_NAME} \
        ${DEBUG_FLAGS} \
        ${SYS_CPPFLAGS} \
        ${LOCAL_CPPFLAGS}
CCC=${${MAKE_ARCH}_CCC}
CPP=cpp
CCDEBUG= ${SYS_CCDEBUG}
CCFLAGS=\
        ${CCDEBUG} \
        ${LOCAL_CCFLAGS} \
        ${SYS_CCFLAGS} \
        ${CPPINCLUDE}
COMPILE.cc=\
        ${CCC} \
        ${${MAKE_ARCH}_CCOPT} \
        ${CCFLAGS} \
        ${CPPFLAGS} \
        ${${MAKE_ARCH}_CC_COMPILE_FLAGS} \
        -c
LINK.cc=\
        ${CCC} \
        ${LDFLAGS}
.cpp:
	@if [ ! -d ${OUTPUTDIR} ] ; then ${MKDIRP} ${OUTPUTDIR} ; fi
	${LINK.cc} -o $@ $@.cpp ${LDLIBS}
.cpp.o:
	@if [ ! -d ${OUTPUTDIR} ] ; then ${MKDIRP} ${OUTPUTDIR} ; fi
	${COMPILE.cc} $< -o $@


SYS_LDFLAGS_SO=${${MAKE_ARCH}_LDFLAGS_SO}
LD=ld
LDFLAGS=${SYS_LDFLAGS} ${LOCAL_LDFLAGS} ${DEBUG_FLAGS}
LDLIBS=${LOCAL_LDLIBS} ${SYS_LDLIBS}

MAKE=make
RM=rm -rf
AR=ar
ARFLAGS=rv
RANLIB=echo ranlib
CP=cp -f
XCP=cp -r -f
MKDIR=mkdir
MKDIRP=mkdir -p
LN=ln -s
INSTALL=install
STRIP=strip
TAR=tar


CPPINCLUDE=${${MAKE_ARCH}_CPP_INCLUDE}
LIB_DIR=${TOPDIR}

#
SOURCES.c=


SOURCES.cc= \
	./file/src/ChatResource.cpp \
	./file/src/ChatSessionTable.cpp \
	./file/src/Disclaimer.cpp \
	./file/src/Image.cpp \
	./file/src/ParseDataMsg.cpp \
	./file/src/RichTextMgr.cpp \
	./file/src/stDdaClApi.cpp

OBJECTS.o=

MODULE=ChatLogging

#       Derived Parameters
#
LIBRARY=$(LIB_DIR)/lib$(MODULE).so
OBJECTS=$(SOURCES.c:.c=.o) $(SOURCES.cc:.cpp=.o)
DEPENDSOURCES=$(SOURCES.c) $(SOURCES.cc)

LOCAL_CPPFLAGS= \
	-I . \
	-I ../../inc/common \
	-I ../nonwin32\
	-I ../../inc/chatlogging\
	-I ../../inc/common/nonwin32\
	-I ./file/inc\
	$(INCLUDE) \
	-D_HRLUBIQUE_ \
	-DUNIX_TOOLKIT_COMPILE
LOCAL_CFLAGS= $(SYS_CFLAGS_SO)
LOCAL_CCFLAGS= $(SYS_CCFLAGS_SO)
LOCAL_LDLIBS= \
#        $(CLIB)


#LOCAL_EXPORTFILE=$(MODULE).${ARCH}.exp

#       Standard Targets
#
all: $(LIBRARY)

clean:
	$(RM) $(LIBRARY)
	$(RM) $(OBJECTS)
	$(RM) $(TEMPLATE_DIR)

$(LIBRARY): $(OBJECTS)

$(LIBRARY): $(OBJECTS)
	$(LINK.cc)  $(SYS_LDFLAGS_SO)  -o $@ $(OBJECTS) $(OBJECTS.o) $(LOCAL_LDLIBS)


