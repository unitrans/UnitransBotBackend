FROM java:openjdk-8-jre-alpine
RUN apk add --update bash
ADD build/distributions/UnitransBot.zip dist.zip
RUN unzip dist.zip

CMD ["/UnitransBot/bin/UnitransBot"]