FROM node:20-alpine AS build
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm ci
COPY . .
# These URLs are used by the browser, so they must be reachable from your PC (not Docker names)
ARG VITE_AUTH_BASE_URL=http://localhost:9898
ARG VITE_GATEWAY_BASE_URL=http://localhost:8765
ENV VITE_AUTH_BASE_URL=$VITE_AUTH_BASE_URL
ENV VITE_GATEWAY_BASE_URL=$VITE_GATEWAY_BASE_URL
RUN npm run build

FROM nginx:alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist /usr/share/nginx/html
EXPOSE 80
