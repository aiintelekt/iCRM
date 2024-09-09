# Use a base image
FROM ubuntu:18.04

LABEL maintainer="support@aiintelekt.com"

# Set environment variables
ENV JAVA_HOME /usr/local/java
ENV PATH $JAVA_HOME/bin:$PATH

# Install necessary packages
RUN apt-get update && \
    apt-get install -y wget tar ant && \
    apt-get clean

# Copy the Oracle JDK 8 tarball to the container
COPY icrm_base/jdk-8u391-linux-x64.tar.gz /tmp

# Install Oracle JDK 8
RUN mkdir -p /usr/local/java && \
    tar -xvzf /tmp/jdk-8u391-linux-x64.tar.gz -C /usr/local/java --strip-components=1 && \
    rm /tmp/jdk-8u391-linux-x64.tar.gz

# Copy the runtime files to the container
COPY . /workspace

# Set the working directory
WORKDIR /workspace/icrm_base

# Build OFBiz
# RUN ant build

# Expose the necessary ports
EXPOSE 8080 8443

# Define the command to run OFBiz
# CMD ["ant", "start"]
