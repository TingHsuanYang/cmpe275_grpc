#!/bin/bash
#

export SVR_HOME="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
echo "** starting client from ${SVR_HOME} **"


JAVA_MAIN=' gash.grpc.route.client.RouteClient'
JAVA_ARGS="request"
JAVA_TUNE='-client -Xms96m -Xmx512m'



# java ${JAVA_TUNE} -cp .:${SVR_HOME}/lib/'*':${SVR_HOME}/classes ${JAVA_MAIN} ${JAVA_ARGS} 
java ${JAVA_TUNE} -cp .:${SVR_HOME}/lib/'*':${SVR_HOME}/bin ${JAVA_MAIN} ${JAVA_ARGS} 100
java ${JAVA_TUNE} -cp .:${SVR_HOME}/lib/'*':${SVR_HOME}/bin ${JAVA_MAIN} ${JAVA_ARGS} 101 &
java ${JAVA_TUNE} -cp .:${SVR_HOME}/lib/'*':${SVR_HOME}/bin ${JAVA_MAIN} ${JAVA_ARGS} 102 &
java ${JAVA_TUNE} -cp .:${SVR_HOME}/lib/'*':${SVR_HOME}/bin ${JAVA_MAIN} ${JAVA_ARGS} 103 &
java ${JAVA_TUNE} -cp .:${SVR_HOME}/lib/'*':${SVR_HOME}/bin ${JAVA_MAIN} ${JAVA_ARGS} 104 &
java ${JAVA_TUNE} -cp .:${SVR_HOME}/lib/'*':${SVR_HOME}/bin ${JAVA_MAIN} ${JAVA_ARGS} 105 &
java ${JAVA_TUNE} -cp .:${SVR_HOME}/lib/'*':${SVR_HOME}/bin ${JAVA_MAIN} ${JAVA_ARGS} 106 &
java ${JAVA_TUNE} -cp .:${SVR_HOME}/lib/'*':${SVR_HOME}/bin ${JAVA_MAIN} ${JAVA_ARGS} 107 &
java ${JAVA_TUNE} -cp .:${SVR_HOME}/lib/'*':${SVR_HOME}/bin ${JAVA_MAIN} ${JAVA_ARGS} 108 &
java ${JAVA_TUNE} -cp .:${SVR_HOME}/lib/'*':${SVR_HOME}/bin ${JAVA_MAIN} ${JAVA_ARGS} 109 &
java ${JAVA_TUNE} -cp .:${SVR_HOME}/lib/'*':${SVR_HOME}/bin ${JAVA_MAIN} ${JAVA_ARGS} 110 &
java ${JAVA_TUNE} -cp .:${SVR_HOME}/lib/'*':${SVR_HOME}/bin ${JAVA_MAIN} ${JAVA_ARGS} 111 &
java ${JAVA_TUNE} -cp .:${SVR_HOME}/lib/'*':${SVR_HOME}/bin ${JAVA_MAIN} ${JAVA_ARGS} 112 &
java ${JAVA_TUNE} -cp .:${SVR_HOME}/lib/'*':${SVR_HOME}/bin ${JAVA_MAIN} ${JAVA_ARGS} 113 &
java ${JAVA_TUNE} -cp .:${SVR_HOME}/lib/'*':${SVR_HOME}/bin ${JAVA_MAIN} ${JAVA_ARGS} 114 &
java ${JAVA_TUNE} -cp .:${SVR_HOME}/lib/'*':${SVR_HOME}/bin ${JAVA_MAIN} ${JAVA_ARGS} 115 &
java ${JAVA_TUNE} -cp .:${SVR_HOME}/lib/'*':${SVR_HOME}/bin ${JAVA_MAIN} ${JAVA_ARGS} 116 &
java ${JAVA_TUNE} -cp .:${SVR_HOME}/lib/'*':${SVR_HOME}/bin ${JAVA_MAIN} ${JAVA_ARGS} 117 &
java ${JAVA_TUNE} -cp .:${SVR_HOME}/lib/'*':${SVR_HOME}/bin ${JAVA_MAIN} ${JAVA_ARGS} 118 &
java ${JAVA_TUNE} -cp .:${SVR_HOME}/lib/'*':${SVR_HOME}/bin ${JAVA_MAIN} ${JAVA_ARGS} 119 &
java ${JAVA_TUNE} -cp .:${SVR_HOME}/lib/'*':${SVR_HOME}/bin ${JAVA_MAIN} ${JAVA_ARGS} 120 &
java ${JAVA_TUNE} -cp .:${SVR_HOME}/lib/'*':${SVR_HOME}/bin ${JAVA_MAIN} ${JAVA_ARGS} 121 &
java ${JAVA_TUNE} -cp .:${SVR_HOME}/lib/'*':${SVR_HOME}/bin ${JAVA_MAIN} ${JAVA_ARGS} 122 &
java ${JAVA_TUNE} -cp .:${SVR_HOME}/lib/'*':${SVR_HOME}/bin ${JAVA_MAIN} ${JAVA_ARGS} 123 &
java ${JAVA_TUNE} -cp .:${SVR_HOME}/lib/'*':${SVR_HOME}/bin ${JAVA_MAIN} ${JAVA_ARGS} 124 &
java ${JAVA_TUNE} -cp .:${SVR_HOME}/lib/'*':${SVR_HOME}/bin ${JAVA_MAIN} ${JAVA_ARGS} 125