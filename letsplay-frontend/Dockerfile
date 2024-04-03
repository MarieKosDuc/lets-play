# Build front-end
FROM node:16-alpine as angular-builder
WORKDIR /app/frontend
COPY . /app/frontend
RUN npm install
RUN npm run build

# nginx serve
FROM nginx:1.21.1-alpine
COPY --from=angular-builder /app/frontend/dist /usr/share/nginx/html
EXPOSE 80