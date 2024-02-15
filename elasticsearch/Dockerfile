ARG ELASTIC_VERSION

FROM docker.elastic.co/elasticsearch/elasticsearch:${ELASTIC_VERSION}

# install nori tokenizer
RUN bin/elasticsearch-plugin install analysis-nori