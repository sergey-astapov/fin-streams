# Apache Zookeeper

FROM openjdk:8-jre-alpine

ARG ZOOKEEPER_VERSION=3.4.10
ARG ZOOKEEPER_MIRROR=https://archive.apache.org
ARG ZOOKEEPER_DISTR_URL=${ZOOKEEPER_MIRROR}/dist/zookeeper/zookeeper-${ZOOKEEPER_VERSION}/zookeeper-${ZOOKEEPER_VERSION}.tar.gz

LABEL name="zookeeper-multi" version=${ZOOKEEPER_VERSION}

ADD ./src /

RUN chmod +x /usr/local/sbin/start.sh
RUN echo ${ZOOKEEPER_DISTR_URL}

RUN apk add --no-cache wget bash

RUN wget -q -O - ${ZOOKEEPER_DISTR_URL} | tar -xzf - -C /opt \
  && mv /opt/zookeeper-* /opt/zookeeper \
  && chown -R root:root /opt/zookeeper

RUN addgroup -S zookeeper \
  && adduser -h /var/lib/zookeeper -G zookeeper -S -H -s /sbin/nologin zookeeper \
  && mkdir /var/lib/zookeeper \
  && chown -R zookeeper:zookeeper /var/lib/zookeeper

EXPOSE 2181 2888 3888

VOLUME ["/opt/zookeeper/conf", "/var/lib/zookeeper"]

ENTRYPOINT ["/usr/local/sbin/start.sh"]
