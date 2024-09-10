# Use a base image
FROM ubuntu:18.04

LABEL maintainer="support@aiintelekt.com"

# Set environment variables with the correct format
ENV JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
ENV PATH=$JAVA_HOME/bin:$PATH

# Install necessary packages, including OpenJDK 8
RUN apt-get update && \
    apt-get install -y wget tar ant openjdk-8-jdk && \
    apt-get clean

# Copy the runtime files to the container
COPY . /workspace

# Set the working directory
WORKDIR /workspace

# Change file permissions
RUN chmod -R 777 /workspace

# Expose the necessary ports
EXPOSE 8080 8443

# Run the OFBiz startup script
CMD ["sh", "-c", "/workspace/startofbiz.sh && tail -f /dev/null"]

