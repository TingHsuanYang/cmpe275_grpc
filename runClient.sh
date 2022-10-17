#!/bin/bash
#

export SVR_HOME="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
echo "** starting client from ${SVR_HOME} **"


JAVA_MAIN=' gash.grpc.route.client.RouteClient'
JAVA_ARGS="request"
JAVA_TUNE='-client -Xms96m -Xmx512m'



# java ${JAVA_TUNE} -cp .:${SVR_HOME}/lib/'*':${SVR_HOME}/classes ${JAVA_MAIN} ${JAVA_ARGS} 
java ${JAVA_TUNE} -cp .:${SVR_HOME}/lib/'*':${SVR_HOME}/bin ${JAVA_MAIN} ${JAVA_ARGS} &
java ${JAVA_TUNE} -cp .:${SVR_HOME}/lib/'*':${SVR_HOME}/bin ${JAVA_MAIN} ${JAVA_ARGS} &
java ${JAVA_TUNE} -cp .:${SVR_HOME}/lib/'*':${SVR_HOME}/bin ${JAVA_MAIN} ${JAVA_ARGS} &
java ${JAVA_TUNE} -cp .:${SVR_HOME}/lib/'*':${SVR_HOME}/bin ${JAVA_MAIN} ${JAVA_ARGS} &
java ${JAVA_TUNE} -cp .:${SVR_HOME}/lib/'*':${SVR_HOME}/bin ${JAVA_MAIN} ${JAVA_ARGS} &
java ${JAVA_TUNE} -cp .:${SVR_HOME}/lib/'*':${SVR_HOME}/bin ${JAVA_MAIN} ${JAVA_ARGS} &
java ${JAVA_TUNE} -cp .:${SVR_HOME}/lib/'*':${SVR_HOME}/bin ${JAVA_MAIN} ${JAVA_ARGS} &
java ${JAVA_TUNE} -cp .:${SVR_HOME}/lib/'*':${SVR_HOME}/bin ${JAVA_MAIN} ${JAVA_ARGS} &
java ${JAVA_TUNE} -cp .:${SVR_HOME}/lib/'*':${SVR_HOME}/bin ${JAVA_MAIN} ${JAVA_ARGS} &
java ${JAVA_TUNE} -cp .:${SVR_HOME}/lib/'*':${SVR_HOME}/bin ${JAVA_MAIN} ${JAVA_ARGS} &
java ${JAVA_TUNE} -cp .:${SVR_HOME}/lib/'*':${SVR_HOME}/bin ${JAVA_MAIN} ${JAVA_ARGS} &
java ${JAVA_TUNE} -cp .:${SVR_HOME}/lib/'*':${SVR_HOME}/bin ${JAVA_MAIN} ${JAVA_ARGS} &
java ${JAVA_TUNE} -cp .:${SVR_HOME}/lib/'*':${SVR_HOME}/bin ${JAVA_MAIN} ${JAVA_ARGS} &
java ${JAVA_TUNE} -cp .:${SVR_HOME}/lib/'*':${SVR_HOME}/bin ${JAVA_MAIN} ${JAVA_ARGS} &
java ${JAVA_TUNE} -cp .:${SVR_HOME}/lib/'*':${SVR_HOME}/bin ${JAVA_MAIN} ${JAVA_ARGS} &
java ${JAVA_TUNE} -cp .:${SVR_HOME}/lib/'*':${SVR_HOME}/bin ${JAVA_MAIN} ${JAVA_ARGS} 