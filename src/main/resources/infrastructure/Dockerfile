# Extend vert.x image
FROM vertx/vertx3

#
ENV VERTICLE_NAME MainVerticle
ENV VERTICLE_FILE lib/imqcm-backend-vertx.jar

# Set the location of the verticles
ENV VERTICLE_HOME /usr/verticles

EXPOSE 8080

# Copy your verticles to the container
COPY $VERTICLE_FILE $VERTICLE_HOME/

# Launch the verticles
WORKDIR $VERTICLE_HOME
ENTRYPOINT ["sh", "-c"]
CMD ["exec vertx run $VERTICLE_NAME -cp $VERTICLE_HOME/*"]