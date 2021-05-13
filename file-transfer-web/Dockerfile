#### STAGE 1: Build ###
#FROM node:12.7-alpine AS build
#WORKDIR /usr/src/app
#COPY package.json package-lock.json ./
#RUN npm install
#COPY . .
#RUN npm run build
#### STAGE 2: Run ###
#FROM nginx:1.17.1-alpine
#COPY nginx.conf /etc/nginx/nginx.conf
#COPY --from=build /usr/src/app/dist/file-transfer-web /usr/share/nginx/html

FROM node:latest as builder

RUN mkdir -p /app

WORKDIR /app

COPY . .

RUN npm install
RUN npm run build --prod

CMD ["npm", "start"]
EXPOSE 80
FROM nginx:alpine
COPY nginx.conf /etc/nginx/nginx.conf
COPY --from=builder app/dist/file-transfer-web usr/share/nginx/html
CMD ["/bin/sh",  "-c",  "envsubst < /usr/share/nginx/html/assets/settings.template.json > /usr/share/nginx/html/assets/settings.json && exec nginx -g 'daemon off;'"]
