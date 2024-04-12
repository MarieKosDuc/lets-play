# Build front-end
FROM node:16-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY ./src ./src
COPY angular.json .
COPY tsconfig.json .
COPY tsconfig.app.json .
COPY tsconfig.spec.json .
COPY tailwind.config.js .
COPY postcss.config.js .
RUN npm run build

# Serve app with Nginx
FROM nginx:latest
COPY nginx.conf /etc/nginx/nginx.conf
COPY --from=builder /app/dist/lets-play/ /usr/share/nginx/html
EXPOSE 80