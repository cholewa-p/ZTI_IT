FROM node:latest as build
RUN apt-get update && apt-get -y upgrade
ARG GH_USER=user
ARG GH_TOKEN=token
RUN git clone https://${GH_USER}:${GH_TOKEN}@github.com/cholewa-p/react-tetris.git
WORKDIR "/react-tetris"
EXPOSE 8080
RUN npm install
CMD ["npm", "start"]
