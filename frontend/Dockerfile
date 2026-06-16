# Stage 1: Build mã nguồn Vue 3 bằng NodeJS
FROM node:20-alpine AS build-stage
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .

# Khai báo argument môi trường (mặc định là production)
ARG NODE_ENV=production
ENV NODE_ENV=${NODE_ENV}

# Chạy lệnh build nếu là môi trường production
RUN if [ "$NODE_ENV" = "production" ] ; then npm run build ; fi

# Stage 2: Môi trường chạy thực tế
# Sử dụng Nginx để tối ưu cho Production
FROM nginx:stable-alpine AS production-stage
COPY --from=build-stage /app/dist /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]

# LƯU Ý: Nếu bạn muốn chạy thuần chế độ DEV (Vite dev server ở port 5173):
# Hãy xóa toàn bộ Stage 2 ở trên và thay bằng 3 dòng dưới này:
# EXPOSE 5173
# CMD ["npm", "run", "dev", "--", "--host"]
